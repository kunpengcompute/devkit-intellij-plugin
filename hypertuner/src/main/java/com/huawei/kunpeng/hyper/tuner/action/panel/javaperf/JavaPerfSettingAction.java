/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
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

package com.huawei.kunpeng.hyper.tuner.action.panel.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.exception.IDEException;
import com.huawei.kunpeng.intellij.common.http.HttpAPIServiceTrust;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Java性能分析系统设置
 *
 * @since 2021-07-08
 */
public class JavaPerfSettingAction extends IDEPanelBaseAction {
    /**
     * 获取栈深度配置
     *
     * @return 栈深度
     */
    public String getstackDepth() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/tools/settings/stackDepth", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        return responseBean == null ? "" : responseBean.getData();
    }

    /**
     * 获取内部通信证书自动告警时间（天）
     *
     * @return 告警天数
     */
    public String getEarlyWarningDays() {
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "java-perf/api/tools/certificates", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return "";
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object members = jsonMessage.get("members");
        if (!(members instanceof JSONArray)) {
            return "";
        }
        JSONArray memberJSONArr = (JSONArray) members;
        JSONObject jsonObject = memberJSONArr.getJSONObject(0);
        String warningDaysInt = String.valueOf(jsonObject.getInteger("earlyWarningDays"));
        return warningDaysInt;
    }

    /**
     * 获取运行日志级别
     *
     * @return 日志级别
     */
    public String getLogLevel() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/logging/levels/root", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        String logLevel = null;
        if (responseBean == null) {
            return logLevel;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (jsonMessage.get("serverLogLevel") instanceof String) {
            logLevel = (String) jsonMessage.get("serverLogLevel");
        }
        return logLevel;
    }

    /**
     * 修改内部通信证书自动告警时间（天）
     *
     * @param obj           入参
     * @param actionOperate 回调
     */
    public void changEarlyWarningDays(JSONObject obj, ActionOperate actionOperate) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/tools/certificates/warningDays/", HttpMethod.POST.vaLue(), "");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            Logger.error("responseBean is null");
            return;
        }
        actionOperate.actionOperate(responseBean);
    }

    /**
     * 修改栈深度配置
     *
     * @param stack         栈深度
     * @param actionOperate 回调
     */
    public void changeStack(String stack, ActionOperate actionOperate) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/tools/settings/stackDepth/" + stack, HttpMethod.POST.vaLue(), "");
        message.setBodyData(null);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            Logger.error("responseBean is null");
            return;
        }
        actionOperate.actionOperate(responseBean);
    }

    /**
     * 修改运行日志级别
     *
     * @param level 修改运行日志级别
     */
    public void changeLogLevel(String level) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/logging/levels/root", "PATCH", "");
        Object ctxObj = IDEContext.getValueFromGlobalContext(null, message.getModule());
        String result = "";
        if (ctxObj instanceof Map) {
            Map<String, Object> context = (Map<String, Object>) ctxObj;
            String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue()))
                    .map(Object::toString).orElse(null);
            String port = Optional.ofNullable(context.get(BaseCacheVal.PORT.vaLue()))
                    .map(Object::toString).orElse(null);
            if (ValidateUtils.isEmptyString(ip) || ValidateUtils.isEmptyString(port)) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean("", I18NServer.toLocale("plugins_common_message_configServer"),
                                NotificationType.WARNING));
                throw new IDEException();
            }
            // 组装完整的url
            String url = IDEConstant.URL_PREFIX +
                    ip +
                    ":" +
                    port +
                    context.get(BaseCacheVal.BASE_URL.vaLue()) +
                    message.getUrl();
            JSONObject jsonParam = new JSONObject();
            String token = Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue()))
                    .map(Object::toString).orElse(null);
            jsonParam.put("validLogLevel", level);
            try {
                result = HttpAPIServiceTrust.getResponseString(url, jsonParam, "PATCH", token, "TUNING");
            } catch (IOException e) {
                Logger.error("Patch request exception：message is {}", e.getMessage());
            }
            if ("".equals(result)) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(JavaProviderSettingConstant.RUN_LOG_LEVEL,
                                TuningI18NServer.toLocale("plugins_hyper_tuner_common_update_sucess"),
                                NotificationType.INFORMATION));
                return;
            } else {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(JavaProviderSettingConstant.RUN_LOG_LEVEL,
                                TuningI18NServer.toLocale("plugins_hyper_tuner_common_update_faild"),
                                NotificationType.ERROR));
            }
        }
    }

    /**
     * 获取所有日志级别
     *
     * @return map
     */
    public Map<String, Object> getAllLogLevel() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/logging/levels", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        return jsonMessage;
    }
}
