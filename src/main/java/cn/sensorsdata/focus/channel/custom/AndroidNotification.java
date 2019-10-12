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

package cn.sensorsdata.focus.channel.custom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)

import cn.jpush.api.push.model.notification.PlatformNotification;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;

/**
 * 在极光官方的代码基础上增加了 uri_action、uri_activity 厂商字段
 */
@javax.annotation.Generated("Source code recreated from a .class file by IntelliJ IDEA")
public class AndroidNotification extends PlatformNotification {
  public static final String NOTIFICATION_ANDROID = "android";
  private static final String TITLE = "title";
  private static final String BUILDER_ID = "builder_id";
  private static final String INBOX = "inbox";
  private static final String STYLE = "style";
  private static final String ALERT_TYPE = "alert_type";
  private static final String BIG_TEXT = "big_text";
  private static final String BIG_PIC_PATH = "big_pic_path";
  private static final String PRIORITY = "priority";
  private static final String CATEGORY = "category";
  private static final String LARGE_ICON = "large_icon";
  private static final String INTENT = "intent";
  private static final String URI_ACTION = "uri_action";
  private static final String URI_ACTIVITY = "uri_activity";
  private final String title;
  private final int builderId;
  private int style;
  private int alert_type;
  private String big_text;
  private Object inbox;
  private String big_pic_path;
  private int priority;
  private String category;
  private String large_icon;
  private JsonObject intent;
  private String uriAction;
  private String uriActivity;

  private AndroidNotification(Object alert, String title, int builderId, int style, int alertType,
      String bigText, Object inbox, String bigPicPath, int priority, String category, String large_icon,
      JsonObject intent, Map<String, String> extras, Map<String, Number> numberExtras,
      Map<String, Boolean> booleanExtras, Map<String, JsonObject> jsonExtras, String uriAction, String uriActivity) {
    super(alert, extras, numberExtras, booleanExtras, jsonExtras);
    this.style = 0;
    this.title = title;
    this.builderId = builderId;
    this.style = style;
    this.alert_type = alertType;
    this.big_text = bigText;
    this.inbox = inbox;
    this.big_pic_path = bigPicPath;
    this.priority = priority;
    this.category = category;
    this.large_icon = large_icon;
    this.intent = intent;
    this.uriAction = uriAction;
    this.uriActivity = uriActivity;
  }

  public static AndroidNotification.Builder newBuilder() {
    return new AndroidNotification.Builder();
  }

  public static AndroidNotification alert(String alert) {
    return newBuilder().setAlert(alert).build();
  }

  public String getPlatform() {
    return "android";
  }

  protected Object getInbox() {
    return this.inbox;
  }

  protected void setInbox(Object inbox) {
    this.inbox = inbox;
  }

  public JsonElement toJSON() {
    JsonObject json = super.toJSON().getAsJsonObject();
    if (this.builderId > 0) {
      json.add("builder_id", new JsonPrimitive(this.builderId));
    }

    if (null != this.title) {
      json.add("title", new JsonPrimitive(this.title));
    }

    if (0 != this.style) {
      json.add("style", new JsonPrimitive(this.style));
    }

    if (-1 != this.alert_type) {
      json.add("alert_type", new JsonPrimitive(this.alert_type));
    }

    if (null != this.big_text) {
      json.add("big_text", new JsonPrimitive(this.big_text));
    }

    if (null != this.inbox && this.inbox instanceof JsonObject) {
      json.add("inbox", (JsonObject) this.inbox);
    }

    if (null != this.big_pic_path) {
      json.add("big_pic_path", new JsonPrimitive(this.big_pic_path));
    }

    if (0 != this.priority) {
      json.add("priority", new JsonPrimitive(this.priority));
    }

    if (null != this.category) {
      json.add("category", new JsonPrimitive(this.category));
    }

    if (null != this.large_icon) {
      json.add("large_icon", new JsonPrimitive(this.large_icon));
    }

    if (null != this.intent) {
      json.add("intent", this.intent);
    }

    if (null != this.uriAction) {
      json.add(URI_ACTION, new JsonPrimitive(this.uriAction));
    }

    if (null != this.uriActivity) {
      json.add(URI_ACTIVITY, new JsonPrimitive(this.uriActivity));
    }
    return json;
  }

  public static class Builder extends
      cn.jpush.api.push.model.notification.PlatformNotification.Builder<AndroidNotification, AndroidNotification.Builder> {
    private String title;
    private int builderId;
    private int style = 0;
    private int alert_type = -1;
    private String big_text;
    private Object inbox;
    private String big_pic_path;
    private int priority;
    private String category;
    private String large_icon;
    private JsonObject intent;
    private String uriAction;
    private String uriActivity;

    public Builder() {
    }

    protected AndroidNotification.Builder getThis() {
      return this;
    }

    public AndroidNotification.Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public AndroidNotification.Builder setBuilderId(int builderId) {
      this.builderId = builderId;
      return this;
    }

    public AndroidNotification.Builder setAlert(Object alert) {
      this.alert = alert;
      return this;
    }

    public AndroidNotification.Builder setStyle(int style) {
      this.style = style;
      return this;
    }

    public AndroidNotification.Builder setAlertType(int alertType) {
      this.alert_type = alertType;
      return this;
    }

    public AndroidNotification.Builder setBigText(String bigText) {
      this.big_text = bigText;
      return this;
    }

    public AndroidNotification.Builder setBigPicPath(String bigPicPath) {
      this.big_pic_path = bigPicPath;
      return this;
    }

    public AndroidNotification.Builder setPriority(int priority) {
      this.priority = priority;
      return this;
    }

    public AndroidNotification.Builder setCategory(String category) {
      this.category = category;
      return this;
    }

    public AndroidNotification.Builder setInbox(Object inbox) {
      if (null == inbox) {
        PlatformNotification.LOG.warn("Null inbox. Throw away it.");
        return this;
      } else {
        this.inbox = inbox;
        return this;
      }
    }

    public AndroidNotification.Builder setLargeIcon(String largeIcon) {
      this.large_icon = largeIcon;
      return this;
    }

    public AndroidNotification.Builder setIntent(JsonObject intent) {
      if (null == intent) {
        PlatformNotification.LOG.warn("Null intent. Throw away it.");
        return this;
      } else {
        this.intent = intent;
        return this;
      }
    }

    public AndroidNotification.Builder setUriAction(String uriAction) {
      this.uriAction = uriAction;
      return this;
    }

    public AndroidNotification.Builder setUriActivity(String uriActivity) {
      this.uriActivity = uriActivity;
      return this;
    }

    public AndroidNotification build() {
      return new AndroidNotification(this.alert, this.title, this.builderId, this.style, this.alert_type,
          this.big_text, this.inbox, this.big_pic_path, this.priority, this.category, this.large_icon, this.intent,
          this.extrasBuilder, this.numberExtrasBuilder, this.booleanExtrasBuilder, this.jsonExtrasBuilder, uriAction,
          uriActivity);
    }
  }
}
