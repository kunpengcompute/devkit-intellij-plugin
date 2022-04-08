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

import com.huawei.kunpeng.hyper.tuner.action.LeftTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningWeakPwdConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.TuningCertConfirmWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.TuningConfigSaveConfirmDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeLoginPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.LeftTreeUtil;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.ServerConfigAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.CretConfirmPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.SaveConfirmPanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

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
    /**
     * 常量实例
     */
    public static TuningServerConfigAction instance = new TuningServerConfigAction();
    /**
     * 响应成功状态
     */
    private static final String SUCCESS = TuningIDEConstant.SUCCESS_CODE;

    private TuningServerConfigAction() {
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
        LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, proj);
        UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        // 重新配置后关闭打开的webview页面
        LeftTreeAction.instance().closeAllOpenedWebViewPage(proj);
    }

    /**
     * 展示登录页面时再次判断服务器状态，防止这期间服务器的工具被卸载
     */
    @Override
    public void notificationForHyperlinkAction() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            ResponseBean response = getServiceConfigResponse();
            if (response != null && SUCCESS.equals(response.getCode())) {
                ApplicationManager.getApplication().invokeLater(TuningLoginUtils::gotoLogin);
            } else {
                Logger.error("Can not get the Login dialog.");
            }
        });
    }

    @Override
    protected void showConfigSaveConfirmDialog(Map<String, Object> mapInfo, Map<String, String> param) {
        IDEBasePanel createConfirmPanel = new SaveConfirmPanel(null, mapInfo);
        TuningConfigSaveConfirmDialog dialog = new TuningConfigSaveConfirmDialog(
                TuningWeakPwdConstant.CONFIG_SAVE_CONFIRM_TITLE, createConfirmPanel);
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
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return responseBean;
        }
        if (!responseBean.getCode().equals(TuningIDEConstant.SUCCESS_CODE)) {
            IDENotificationUtil.notificationCommon(new NotificationBean("",
                    responseBean.getMessage(), NotificationType.WARNING));
        }
        return responseBean;
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
        ApplicationManager.getApplication().invokeLater(LeftTreeUtil::refresh2ConfigPanel);
    }

    @Override
    public void serverCertConfirmFailed(String toolName, String ip) {
        ApplicationManager.getApplication().invokeLater(() -> {
            IDEBasePanel panel = new CretConfirmPanel(null,
                    CommonI18NServer.toLocale("common_setting_cert_error_content_tip"), ip);
            IDEBaseDialog dialog = new TuningCertConfirmWrapDialog(CommonI18NServer.toLocale(
                    "common_setting_cert_error_title"), panel, toolName);
            dialog.displayPanel();
        });
    }
}
