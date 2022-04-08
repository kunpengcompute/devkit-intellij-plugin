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

package com.huawei.kunpeng.porting.action.serverconfig;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.action.ServerConfigAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.CretConfirmPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.SaveConfirmPanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.toolwindow.DeleteAllReportsAction;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.PortingWeakPwdConstant;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.dialog.PortingConfigSaveConfirmDialog;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingCertConfirmWrapDialog;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeLoginPanel;

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
public class PortingServerConfigAction extends ServerConfigAction {
    /**
     * 查询服务器是否可用url
     */
    public static final String SERVER_STATUS_URL = "/users/admin/status/";
    /**
     * 实例化
     */
    public static PortingServerConfigAction instance = new PortingServerConfigAction();
    /**
     * 响应成功状态
     */
    private static final String SUCCESS = "0";

    private PortingServerConfigAction() {
        super(PortingIDEConstant.TOOL_NAME_PORTING);
    }

    /**
     * 配置服务器完成后刷新左侧树面板为已配置面板
     *
     * @param proj openProjects
     */
    @Override
    protected void customizeRefreshPanel(Project proj) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(proj).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
        LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, proj);
        UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        // 重新配置后关闭打开的历史报告页面
        DeleteAllReportsAction.closeAllOpenedReports(proj);
    }

    /**
     * 展示登录页面时再次判断服务器状态，防止这期间服务器的工具被卸载
     */
    @Override
    public void notificationForHyperlinkAction() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            ResponseBean response = getServiceConfigResponse();
            if (response != null && SUCCESS.equals(response.getStatus())) {
                ApplicationManager.getApplication().invokeLater(LoginUtils::gotoLogin);
            } else {
                Logger.error("Can not get the Login dialog.");
            }
        });
    }

    @Override
    protected String successCode() {
        return SUCCESS;
    }

    @Override
    protected void showConfigSaveConfirmDialog(Map<String, Object> mapInfo, Map<String, String> param) {
        IDEBasePanel createConfirmPanel = new SaveConfirmPanel(null, mapInfo);
        PortingConfigSaveConfirmDialog dialog = new PortingConfigSaveConfirmDialog(
                PortingWeakPwdConstant.CONFIG_SAVE_CONFIRM_TITLE, createConfirmPanel);
        dialog.setIp(param.get("ip"));
        dialog.setPort(param.get("port"));
        dialog.setCertFile(param.get("certFile"));
        dialog.setUseCertFlag(Boolean.parseBoolean(param.get("useCertFlag")));
        dialog.displayPanel();
    }

    /**
     * 自定义 通过调用首次登陆接口判断修改的IP和端口是否可正常访问
     *
     * @return ResponseBean 响应实体
     */
    @Override
    protected ResponseBean getServiceConfigResponse() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, SERVER_STATUS_URL,
                HttpMethod.GET.vaLue(), false);
        return PortingHttpsServer.INSTANCE.requestData(message);
    }

    @Override
    protected void preSaveConfig() {
        // 左侧树面板加载loading，loadingText为系统默认
        UIUtils.changeToolWindowToLoadingPanel(CommonUtil.getDefaultProject(), null,
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
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
            IDEBaseDialog dialog = new PortingCertConfirmWrapDialog(CommonI18NServer.toLocale(
                    "common_setting_cert_error_title"), panel, toolName);
            dialog.displayPanel();
        });
    }
}
