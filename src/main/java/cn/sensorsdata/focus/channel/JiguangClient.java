/*
 * Copyright 2019 Sensors Data Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sensorsdata.focus.channel;

import com.sensorsdata.focus.channel.ChannelClient;
import com.sensorsdata.focus.channel.ChannelConfig;
import com.sensorsdata.focus.channel.annotation.SfChannelClient;
import com.sensorsdata.focus.channel.entry.MessagingTask;
import com.sensorsdata.focus.channel.entry.PushTask;
import com.sensorsdata.focus.channel.push.PushTaskUtils;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.sensorsdata.focus.channel.custom.AndroidNotification;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SfChannelClient(version = "v0.1.7", desc = "SF 极光推送客户端")
@Slf4j
public class JiguangClient extends ChannelClient {

  // 每次批量发送请求最多包含多少推送 ID
  private static final int BATCH_SIZE = 600;

  // 如果当前分钟 API 剩余调用次数小于该值，那么 sleep 到下一分钟
  private static final int RATE_LIMIT_REMAINING_SLEEP_TRIGGER = 150;

  private JPushClient jpushClient;

  // 需要开启极光 vip 才需要设置，华为通道
  private String uriActivity;

  // 需要开启极光 vip 才需要设置，OPPO 通道
  private String uriAction;

  private boolean mutableContent;

  // 第三方 intent 字段模板
  private String intentTemplate;

  @Override
  public void initChannelClient(ChannelConfig channelConfig) {
    JiguangChannelConfig jiguangChannelConfig = (JiguangChannelConfig) channelConfig;

    String appKey = jiguangChannelConfig.getAppKey();
    String masterSecret = jiguangChannelConfig.getMasterSecret();
    ClientConfig config = ClientConfig.getInstance();
    jpushClient = new JPushClient(masterSecret, appKey, null, config);

    if (StringUtils.isNotBlank(jiguangChannelConfig.getUriActivity())) {
      this.uriActivity = jiguangChannelConfig.getUriActivity();
    }

    if (StringUtils.isNotBlank(jiguangChannelConfig.getUriAction())) {
      this.uriAction = jiguangChannelConfig.getUriAction();
    }

    this.mutableContent = "true".equalsIgnoreCase(jiguangChannelConfig.getMutableContent());

    this.intentTemplate = jiguangChannelConfig.getIntentTemplate();
  }

  @Override
  public void send(List<MessagingTask> messagingTasks) throws Exception {
    // 将推送内容相同的任务分到一组，后面按组批量推送
    Collection<List<MessagingTask>> taskGroups = PushTaskUtils.groupByTaskContent(messagingTasks, BATCH_SIZE);

    for (List<MessagingTask> taskList : taskGroups) {
      // 经过上面分组，一批里每个 PushTask 除了 ID，其他是一样的
      PushTask pushTask = taskList.get(0).getPushTask();

      // 将本批次所有推送 ID 提取出来
      List<String> cidList = new ArrayList<>();
      for (MessagingTask messagingTask : taskList) {
        cidList.add(messagingTask.getPushTask().getClientId());
      }

      // 构造 extraMap。作为 notification 参数
      // 可修改 createExtraMap 逻辑以实现自定义的格式和内容
      Map<String, String> extraMap = createExtraMap(pushTask);

      Notification notification = Notification.newBuilder()
          .addPlatformNotification(createAndroidNotification(pushTask, extraMap))
          .addPlatformNotification(createIosNotification(pushTask, extraMap))
          .build();

      PushPayload pushPayload = PushPayload.newBuilder()
          .setPlatform(Platform.all())
          .setAudience(Audience.registrationId(cidList))
          .setNotification(notification)
          .setOptions(Options.newBuilder().setApnsProduction(true).build()).build();

      String failReason = null;
      try {
        PushResult result = jpushClient.sendPush(pushPayload);
        // 速率限制强制 sleep
        rateLimitSleepIfNecessary(result);
        log.info("finish push process. [task='{}', sendContent={}, response='{}']", pushTask, pushPayload, result);
      } catch (APIRequestException e) {
        log.warn("jiguang send push with exception. [tasks='{}']", taskList);
        log.warn("exception", e);
        failReason = e.getErrorMessage();
      } catch (APIConnectionException e) {
        log.warn("jiguang send push with exception. [tasks='{}']", taskList);
        log.warn("exception", e);
        failReason = "APIConnectionException";
      } catch (Exception e) {
        log.error("jiguang send push with exception. [tasks='{}']", taskList);
        log.error("exception", e);
        failReason = e.getMessage();
      }

      for (MessagingTask messagingTask : taskList) {
        messagingTask.setSuccess(failReason == null);
        messagingTask.setFailReason(failReason);
      }
    }
  }

  /**
   * 该函数用于生成 extraMap，可以更改下面逻辑以实现自定义的数据格式和内容
   */
  private Map<String, String> createExtraMap(PushTask pushTask) {
    Map<String, String> extraMap = new LinkedHashMap<>();

    // sf_data 是按照 SF 预置的格式对数据进行的格式化封装
    // 如果客户端不需要 sf_data 字段可以不设值
    if (StringUtils.isNotBlank(pushTask.getSfData())) {
      extraMap.put("sf_data", pushTask.getSfData());
    }

    if (MapUtils.isNotEmpty(pushTask.getCustomized())) {
      for (Map.Entry<String, String> entry : pushTask.getCustomized().entrySet()) {
        if (StringUtils.isNotBlank(entry.getKey()) && entry.getValue() != null) {
          extraMap.put(entry.getKey(), entry.getValue());
        }
      }
    }

    // 兼容通过自定义参数传厂商字段添加了 $uri_activity，但不需要放到 extra map 传到客户端
    extraMap.remove("$uri_activity");

    return extraMap;
  }

  /**
   * 生成 iOS 推送的通知内容
   */
  private IosNotification createIosNotification(PushTask pushTask, Map<String, String> extraMap) {
    IosAlert iosAlert = IosAlert.newBuilder()
        .setTitleAndBody(pushTask.getMsgTitle(), null, pushTask.getMsgContent()).build();
    IosNotification.Builder iosNotificationBuilder = IosNotification.newBuilder()
        .setAlert(iosAlert)
        .setMutableContent(mutableContent)
        .addExtras(extraMap);

    return iosNotificationBuilder.build();
  }

  /**
   * 生成 Android 推送的通知内容
   */
  private AndroidNotification createAndroidNotification(PushTask pushTask, Map<String, String> extraMap) {
    AndroidNotification.Builder androidNotificationBuilder =
        AndroidNotification.newBuilder()
            .setTitle(pushTask.getMsgTitle())
            .setAlert(pushTask.getMsgContent())
            .addExtras(extraMap);

    // 设置厂商字段
    androidNotificationBuilder.setUriAction(uriAction).setUriActivity(uriActivity);

    // SF 默认逻辑，可修改：如果 customized 中有 $uri_activity 字段，那么使用该字段作为厂商字段
    Map<String, String> customized = pushTask.getCustomized();
    if (MapUtils.isNotEmpty(customized)) {
      String activityForNativeChannel = customized.get("$uri_activity");
      if (StringUtils.isNotBlank(activityForNativeChannel)) {
        androidNotificationBuilder.setUriAction(activityForNativeChannel)
            .setUriActivity(activityForNativeChannel);
      }
    }

    // SF 默认逻辑，可修改：设置 intent，这里使用 SF 预置的模板规则拼接，可以改成自定义的规则
    if (StringUtils.isNotBlank(intentTemplate)) {
      String intent = PushTaskUtils.generateIntentFromTemplate(pushTask, intentTemplate);

      JsonObject intentObject = new JsonObject();
      intentObject.add("url", new JsonPrimitive(intent));
      androidNotificationBuilder.setIntent(intentObject);
    }

    return androidNotificationBuilder.build();
  }

  /**
   * 发送过于频繁则 sleep 再发送
   */
  private void rateLimitSleepIfNecessary(PushResult result) {
    if (result.getRateLimitRemaining() < RATE_LIMIT_REMAINING_SLEEP_TRIGGER) {
      int rateLimitReset = result.getRateLimitReset() + 1;
      log.info("rate limit triggered, will sleep. [remainingTrigger={}, realRemaining={}, sleep={}]",
          RATE_LIMIT_REMAINING_SLEEP_TRIGGER, result.getRateLimitRemaining(), rateLimitReset);
      try {
        Thread.sleep(rateLimitReset * 1000L);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
      log.info("sleep done.");
    }
  }

  @Override
  public void close() {
    jpushClient.close();
    log.debug("close jiguang client");
  }
}
