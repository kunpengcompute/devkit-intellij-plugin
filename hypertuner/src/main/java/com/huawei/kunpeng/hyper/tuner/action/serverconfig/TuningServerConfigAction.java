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

package com.huawei.kunpeng.hyper.tuner.action.serverconfig;

import com.huawei.kunpeng.hyper.tuner.common.constant.InstallManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.NginxUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.CompatibilityDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.TuningCertConfirmWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.TuningConfigSaveConfirmDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningConfigSuccessPanel;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.IDELoginEditor;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.ServerConfigAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.CretConfirmPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.SaveConfirmPanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 服务器配置事件处理器
 *
 * @since 2020-10-08
 */
public class TuningServerConfigAction extends ServerConfigAction {
    /**
     * 查询服务器是否可用url
     */
    public static final String SERVER_STATUS_URL = "user-management/api/v2.2/users/install-info/";
    public static final String SERVER_VERSION_URL = "/user-management/api/v2.2/users/version/";

    /**
     * 常量实例
     */
    public static TuningServerConfigAction instance = new TuningServerConfigAction();

    /**
     * 响应成功状态
     */
    private static final String SUCCESS = TuningIDEConstant.SUCCESS_CODE;

    public TuningServerConfigAction() {
        super(TuningIDEConstant.TOOL_NAME_TUNING);
    }

    /**
     * 配置服务器完成后刷新左侧树面板为已配置面板
     *
     * @param proj openProjects
     */
    @Override
    protected void customizeRefreshPanel(Project proj) {
        ToolWindow toolWindow =
                ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
        TuningConfigSuccessPanel tuningConfigSuccessPanel = new TuningConfigSuccessPanel(toolWindow, proj);
        if (toolWindow != null) {
            toolWindow.getContentManager().addContent(tuningConfigSuccessPanel.getContent());
            toolWindow.getContentManager().setSelectedContent(tuningConfigSuccessPanel.getContent());
        }
    }

    /**
     * 配置服务器确定事件
     *
     * @param params        配置服务器信息参数
     * @param actionOperate 自定义操作
     */
    @Override
    public void onOKAction(Map params, ActionOperate actionOperate) {
        Map<String, String> param = JsonUtil.getValueIgnoreCaseFromMap(params, "param", Map.class);
        // 保存配置服务器，加载loading...
        preSaveConfig();
        if (!save(param)) {
            // 配置服务器失败处理
            saveConfigFailedOperate();
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    UserManageConstant.CONFIG_TITLE,
                    TuningI18NServer.toLocale("plugins_common_message_responseError_messagePrefix"),
                    NotificationType.ERROR));
        } else {
            // 配置服务器成功
            // 判断服务器兼容性
            boolean isCompatible = checkServiceVersionCompatible();
            if (isCompatible) {
                // 仅在版本适配的情况下打开 web view 页面，允许用户使用
                System.out.println("is compatible!!");
                ApplicationManager.getApplication().invokeLater(() -> {
                    NginxUtil.updateNginxConfig(param.get("ip"), param.get("port"), param.get("localPort"));
                    // 打开web首页
                    IDELoginEditor.openPage(param.get("localPort"));
                });
            }
        }
    }

    /**
     * 展示登录页面时再次判断服务器状态，防止这期间服务器的工具被卸载
     */
    @Override
    public void notificationForHyperlinkAction() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            ResponseBean responseBean = getServiceConfigResponse();
            if (responseBean != null && !responseBean.getCode().equals(TuningIDEConstant.SUCCESS_CODE)) {
                IDENotificationUtil.notificationCommon(new NotificationBean("",
                        responseBean.getMessage(), NotificationType.WARNING));
            }
        });
    }

    @Override
    protected void showConfigSaveConfirmDialog(Map<String, Object> mapInfo, Map<String, String> param) {
        IDEBasePanel createConfirmPanel = new SaveConfirmPanel(null, mapInfo);
        TuningConfigSaveConfirmDialog dialog = new TuningConfigSaveConfirmDialog(
                InstallManageConstant.CONFIG_SAVE_CONFIRM_TITLE, createConfirmPanel);
        if (ValidateUtils.isEmptyMap(param)) {
            return;
        }
        dialog.setIp(param.get("ip"));
        dialog.setPort(param.get("port"));
        dialog.setCertFile(param.get("certFile"));
        dialog.setUseCertFlag(Boolean.parseBoolean(param.get("useCertFlag")));
        dialog.displayPanel();
    }

    @Override
    protected String successCode() {
        return SUCCESS;
    }

    /**
     * 自定义 通过调用首次登陆接口判断修改的IP和端口是否可正常访问
     *
     * @return ResponseBean 响应实体
     */
    @Override
    protected ResponseBean getServiceConfigResponse() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, SERVER_STATUS_URL,
                HttpMethod.GET.vaLue(), false);
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 通过调用接口获取服务端版本，读取本地配置文件中的兼容性配置字段，判断是否当前版本插件是否支持
     *
     * @return boolean true：兼容当前服务端版本，false：不兼容当前服务端版本，并在右下角提示弹窗
     */
    @Override
    protected boolean checkServiceVersionCompatible() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, SERVER_VERSION_URL,
                HttpMethod.GET.vaLue(), false);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            Logger.warn("An error occurred while getting the server version, the response is null");
            return false;
        }
        String responseBeanDataJsStr = responseBean.getData();
        JSONObject jsonObject = JSON.parseObject(responseBeanDataJsStr);
        String serverVersionStr = jsonObject.getString("version");

        boolean isContains = true; // 默认插件兼容所有版本插件
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        Object configVersionObj = config.get(ConfigProperty.CONFIG_VERSION.vaLue());
        String minimumVersion = "";
        if (configVersionObj instanceof List) {
            List configList = (List) configVersionObj;
            if (!configList.isEmpty()) {
                // 配置文件中兼容性版本不为空，则说明对兼容性有要求
                isContains = configList.contains(serverVersionStr);
                minimumVersion = configList.get(0) + "";
            } else {
                Logger.warn("Plugin compatibility is not configured, all background version are compatible by default");
            }
        }
        Logger.info("The current plugin version compatibility is " + isContains);
        if (!isContains) {
            String serverOldTip = MessageFormat.format(
                    TuningI18NServer.toLocale("plugins_hyper_tuner_version_server_old"),
                    minimumVersion, serverVersionStr);
            String title = TuningI18NServer.toLocale("plugins_hyper_tuner_version_tip");
            CompatibilityDialog dialog = new CompatibilityDialog(title, serverOldTip);
            dialog.displayPanel();
        }
        return isContains;
    }

    @Override
    protected void preSaveConfig() {
        // 左侧树面板加载loading，loadingText为系统默认
        UIUtils.changeToolWindowToLoadingPanel(CommonUtil.getDefaultProject(), null,
                TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
    }

    @Override
    protected void saveConfigFailedOperate() {
        // 左侧树面板刷新到配置服务器面板
        ApplicationManager.getApplication().invokeLater(TuningCommonUtil::refreshServerConfigPanel);
    }

    @Override
    public void serverCertConfirmFailed(String toolName, String ip) {
        ApplicationManager.getApplication().invokeLater(() -> {
            IDEBasePanel panel = new CretConfirmPanel(null,
                    CommonI18NServer.toLocale("common_setting_cert_error_content_tip"), ip);
            IDEBaseDialog dialog = new TuningCertConfirmWrapDialog(
                    UserManageConstant.CERT_ERROR_TITLE, panel, toolName);
            dialog.displayPanel();
        });
    }
}
