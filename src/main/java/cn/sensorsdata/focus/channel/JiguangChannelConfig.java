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

import com.sensorsdata.focus.channel.ChannelConfig;
import com.sensorsdata.focus.channel.annotation.ConfigField;
import com.sensorsdata.focus.channel.annotation.SfChannelConfig;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@SfChannelConfig
@Data
public class JiguangChannelConfig extends ChannelConfig {

  @ConfigField(cname = "AppKey", desc = "平台 AppKey，可在平台配置页面获取")
  @NotBlank
  @Size(min = 24, max = 24)
  private String appKey;

  @ConfigField(cname = "MasterSecret", desc = "平台 MasterSecret，可在平台配置页面获取")
  @NotBlank
  @Size(min = 24, max = 24)
  private String masterSecret;

  @ConfigField(cname = "厂商通道 uri_activity", desc = "如果对接了极光华为等厂商通道，此处填写 uri_activity 值，详情可询问极光推送")
  private String uriActivity;

  @ConfigField(cname = "厂商通道 uri_action", desc = "如果对接了极光 OPPO 等厂商通道，此处填写 uri_action 值，详情可询问极光推送")
  private String uriAction;

  @ConfigField(cname = "mutable_content", desc = "可选配置。iOS 配置项，详情请查看极光推送官方文档", defaultValue = "false")
  private String mutableContent;

  @ConfigField(cname = "安卓推送 Intent 模板", desc = "可选配置。设置第三方厂商通道使用的 Intent 模板。")
  private String intentTemplate;
}
