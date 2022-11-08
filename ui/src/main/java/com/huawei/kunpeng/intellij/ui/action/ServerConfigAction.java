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

package com.huawei.kunpeng.intellij.ui.action;

import com.huawei.kunpeng.intellij.common.ConfigInfo;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.InstallConstant;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.http.HttpsServer;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 服务器配置事件处理器
 *
 * @since 2021-04-13
 */
public abstract class ServerConfigAction extends IDEPanelBaseAction {
    /**
     * 工具名
     */
    protected String toolName;

    public ServerConfigAction(String toolName) {
        this.toolName = toolName;
    }

    /**
     * 保存服务器配置
     *
     * @param params ip port certFile证书文件 useCertFlag是否使用证书
     * @return true:保存服务器配置成功
     */
    public boolean save(Map<String, String> params) {
        String host = params.get("ip");
        String port = params.get("port");
        String localPort = params.get("localPort");
        String certFile = params.get("certFile");
//        ConfigUtils.fillIp2JsonFile(toolName, host, port, localPort, certFile);
        // handle cert
        verifyOrTrustCert(params);
        // 将plugin设置为配置服务器状态
        IDEContext.setIDEPluginStatus(toolName, IDEPluginStatus.IDE_STATUS_SERVER_DEPLOY);
        // check connection is ok
        ResponseBean response = getServiceConfigResponse();
        if (response != null &&
                (successCode().equals(response.getCode()) || successCode().equals(response.getStatus()))) {
            if (!HttpsServer.isCertConfirm && Boolean.parseBoolean(params.get("useCertFlag"))) {
                serverCertConfirmFailed(toolName, host);
                return false;
            }
            showNotification();
            // update global Context
            updateIDEContext(host);
            // clear userConfig when config server again
            ConfigUtils.updateUserConfig(ConfigProperty.AUTO_LOGIN_CONFIG.vaLue(), " ", false, false);
            synchronizedLeftTree();
            return true;
        }
        // 将plugin设置为初始状态
        IDEContext.setIDEPluginStatus(toolName, IDEPluginStatus.IDE_STATUS_INIT);
        // 清空本地 ip 缓存
//        ConfigUtils.fillIp2JsonFile(toolName, "", "", "", "");
        return false;
    }

    /**
     * 配置服务器成功后提示携带登录跳转
     */
    public void showNotification() {
        Project project = CommonUtil.getDefaultProject();
        String content = CommonI18NServer.toLocale("common_config_success");
        NotificationBean notificationBean = new NotificationBean(CommonI18NServer.toLocale(
                "common_config_title"), content, NotificationType.INFORMATION);
        notificationBean.setProject(project);
        IDENotificationUtil.notificationForHyperlink(notificationBean, data -> notificationForHyperlinkAction());
    }

    private void synchronizedLeftTree() {
        // 如果打开多个project， 同步每一个project左侧树状态
        Project[] openProjects = ProjectUtil.getOpenProjects();
        ApplicationManager.getApplication().invokeLater(() -> {
            for (Project proj : openProjects) {
                // 配置服务器完成后刷新左侧树面板为已配置面板
                customizeRefreshPanel(proj);
            }
        });
    }

    private void verifyOrTrustCert(Map<String, String> params) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        String certPath = "";
        if (config.get(ConfigProperty.CERT_PATH.vaLue()) instanceof String) {
            certPath = (String) config.get(ConfigProperty.CERT_PATH.vaLue());
        }
        boolean useCertFlag = Boolean.parseBoolean(params.get("useCertFlag"));
        if (useCertFlag && StringUtil.stringIsEmpty(certPath)) {
            Logger.error("certificate verify failed");
            FileUtil.removeCertConfig();
            NotificationBean notification = new NotificationBean(
                    UserManageConstant.CERT_ERROR_TITLE, UserManageConstant.CERT_ERROR_CONTENT, NotificationType.ERROR);
            IDENotificationUtil.notificationCommon(notification);
            return;
        }
        if (!useCertFlag && Objects.isNull(certPath)) {
            FileUtil.removeCertConfig();
        }
    }

    /**
     * 更新缓存：当前插件状态以及
     *
     * @param ip 配置服务器地址
     */
    private void updateIDEContext(String ip) {
        // update globe IDEPluginStatus
        IDEContext.setIDEPluginStatus(toolName, IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        ConfigInfo curInfo = new ConfigInfo(ip, "");
        IDEContext.getProjectConfig().put(CommonUtil.getDefaultProject().getName() + "#" + toolName, curInfo);
    }

    /**
     * 自定义 配置服务器完成后刷新左侧树面板为已配置面板
     *
     * @param proj openProjects
     */
    protected abstract void customizeRefreshPanel(Project proj);

    /**
     * 点击登录超链接执行的动作
     */
    protected void notificationForHyperlinkAction() {
    }

    /**
     * 配置成功 code
     *
     * @return string code
     */
    protected abstract String successCode();

    /**
     * 保存服务器配置信息
     *
     * @param params        配置服务器信息参数
     * @param actionOperate 自定义操作
     */
    protected void onOKAction(Map params, ActionOperate actionOperate) {
        Map<String, String> param = JsonUtil.getValueIgnoreCaseFromMap(params, "param", Map.class);
        if (IDEContext.checkLogin(toolName)) {
            Map<String, Object> mapInfo = new HashMap<String, Object>();
            mapInfo.put("name", InstallConstant.CONFIG_SAVE_CONFIRM_TITLE);
            showConfigSaveConfirmDialog(mapInfo, param);
        } else {
            // ServerConfigWrapDialog 保存服务器配置之前操作
            preSaveConfig();
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                if (!save(param)) {
                    // 配置服务器失败处理
                    saveConfigFailedOperate();
                }
            });
        }
    }

    /**
     * 展示配置dialog
     *
     * @param mapInfo SaveConfirmPanel初始化参数
     * @param param   ConfigSaveConfirmDialog初始化参数
     */
    protected abstract void showConfigSaveConfirmDialog(Map<String, Object> mapInfo, Map<String, String> param);

    /**
     * 取消服务器配置信息
     *
     * @param params        服务器配置信息参数
     * @param actionOperate 自定义操作
     */
    public void onCancelAction(Map params, ActionOperate actionOperate) {
    }

    /**
     * 自定义 通过调用首次登陆接口判断修改的IP和端口是否可正常访问
     *
     * @return ResponseBean 响应实体
     */
    protected abstract ResponseBean getServiceConfigResponse();

    /**
     * 通过调用接口获取服务端版本，判断是否当前版本插件是否支持
     *
     * @return boolean true：兼容当前服务端版本，false：不兼容当前服务端版本，并在右下角提示弹窗
     */
    protected abstract boolean checkServiceVersionCompatible();

    /**
     * 保存服务器配置信息前
     */
    protected void preSaveConfig() {
    }

    /**
     * 保存服务器配置信息失败后
     */
    protected void saveConfigFailedOperate() {
    }

    /**
     * 配置服务器证书失败处理
     *
     * @param toolName 工具名称
     * @param ip       配置IP
     */
    public abstract void serverCertConfirmFailed(String toolName, String ip);
}
