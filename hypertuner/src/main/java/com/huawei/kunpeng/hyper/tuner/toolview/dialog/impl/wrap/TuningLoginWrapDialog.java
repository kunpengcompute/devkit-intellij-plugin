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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap;

import com.huawei.kunpeng.hyper.tuner.action.panel.webservercert.TuningWebServerCertificateAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.hyper.tuner.http.manager.user.TuningUserManagerHandler;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.DisclaimerDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.LeftTreeUtil;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.LoginWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.ChangePasswordPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LoginPanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Map;

/**
 * intellij 类型弹框
 *
 * @since 2020-09-25
 */
public class TuningLoginWrapDialog extends LoginWrapDialog {
    /**
     * 记录阈值  CANCLE = 0.75;
     */
    public static double INFO_CANCLE = 0.75;
    /**
     * 记录阈值  INFO_WARNING = 0.90
     */
    public static double INFO_WARNING = 0.90;
    private static final String LOGIN_TITLE = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_login");

    public TuningLoginWrapDialog(IDEBasePanel panel) {
        super.createLoginWrapDialog(panel, false);
    }

    /**
     * 磁盘监控
     *
     * @param showTip showTip
     * @return boolean
     */
    public static boolean getDiscAlarm(boolean showTip) {
        ResponseBean resp = TuningUserManagerHandler.getDiscAlarm();
        if (resp == null) {
            return false;
        }
        Map<String, JSONObject> jsonMessage = JsonUtil.getJsonObjFromJsonStr(resp.getData());
        // 工作空间
        JSONObject data = jsonMessage.get("data");
        if (data == null) {
            Logger.info("data is null");
            return false;
        }
        if (data.get("total") == null || data.get("free") == null || data.get("suggest_space") == null) {
            Logger.info("data.data is null.");
            return false;
        }
        if (!isNumeric(data.get("total").toString()) || !isNumeric(data.get("free").toString())
                || !isNumeric(data.get("suggest_space").toString())) {
            Logger.info("data is not numertic.");
            return false;
        }
        String alarm = "";
        if (data.get("alarm") instanceof String) {
            alarm = (String) data.get("alarm");
        }
        Double total = Double.valueOf(data.get("total").toString());
        Double free = Double.valueOf(data.get("free").toString());
        Double suggestSpace = Double.valueOf(data.get("suggest_space").toString());
        getSpaceAlarm(total, free, suggestSpace, alarm);
        // 磁盘空间
        JSONObject dataAll = jsonMessage.get("data_all");
        if (dataAll == null) {
            Logger.info("dataAll is null");
            return false;
        }
        boolean flag = getSpaceDiskAlarm(dataAll, showTip);
        return flag;
    }

    private static boolean isNumeric(String str) {
        try {
            new BigDecimal(str).toString();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 显示自盘高进信息
     *
     * @param total total
     * @param free free
     * @param suggestSpace suggestSpace
     * @param alarm alarm
     */
    public static void getSpaceAlarm(Double total, Double free, Double suggestSpace, String alarm) {
        // 计算使用率（被除数判断非0）
        BigDecimal workSpaceUsage = BigDecimal.valueOf(0.00);
        if (!("Normal".equals(alarm))) {
            String tipContent = I18NServer.toLocale("plugins_hyper_tuner_work_space_usage_diskTip");
            if (free > suggestSpace) {
                tipContent = I18NServer.toLocale("plugins_hyper_tuner_work_space_usage_disk");
            }
            tipContent = MessageFormat.format(tipContent, total, free, suggestSpace);
            BigDecimal totala = BigDecimal.valueOf(total);
            BigDecimal freeb = BigDecimal.valueOf(free);
            workSpaceUsage = totala.subtract(freeb).divide(totala, 2, RoundingMode.HALF_UP);
            ;
            if ((workSpaceUsage.compareTo(BigDecimal.valueOf(INFO_WARNING))) > 0) {
                notifyInfo("", tipContent, NotificationType.WARNING);
            } else if ((workSpaceUsage.compareTo(BigDecimal.valueOf(INFO_CANCLE))) > 0) {
                notifyInfo("", tipContent, NotificationType.INFORMATION);
            } else {
                Logger.info("everything is Normal");
            }
        }
    }

    /**
     * 检测磁盘空间
     *
     * @param dataAll dataAll
     * @param showTip showTip
     * @return boolean
     */
    public static boolean getSpaceDiskAlarm(JSONObject dataAll, boolean showTip) {
        if (dataAll.get("total") == null || dataAll.get("free") == null || dataAll.get("suggest_space") == null) {
            Logger.info("data.dataAll is null.");
            return false;
        }
        if (!isNumeric(dataAll.get("total").toString()) || !isNumeric(dataAll.get("free").toString())
                || !isNumeric(dataAll.get("suggest_space").toString())) {
            Logger.info("data is not numertic.");
            return false;
        }
        String alarm = "";
        if (dataAll.get("alarm") instanceof String) {
            alarm = (String) dataAll.get("alarm");
        }
        Double totalAll = Double.valueOf(dataAll.get("total").toString());
        Double freeAll = Double.valueOf(dataAll.get("free").toString());
        Double suggestSpaceAll = Double.valueOf(dataAll.get("suggest_space").toString());
        BigDecimal diskSpaceUsage = BigDecimal.valueOf(0.00);
        if (!("Normal".equals(alarm))) {
            BigDecimal totalAlla = BigDecimal.valueOf(totalAll);
            BigDecimal freeAllb = BigDecimal.valueOf(freeAll);
            diskSpaceUsage = totalAlla.subtract(freeAllb).divide(totalAlla, 2, RoundingMode.HALF_UP);
            String tipContent = I18NServer.toLocale("plugins_hyper_tuner_work_space_diskTip");
            if (freeAll > suggestSpaceAll) {
                tipContent = I18NServer.toLocale("plugins_hyper_tuner_work_space_disk");
            }
            tipContent = MessageFormat.format(tipContent, totalAll, freeAll, suggestSpaceAll);
            if ((diskSpaceUsage.compareTo(BigDecimal.valueOf(INFO_WARNING))) > 0) {
                if (showTip) {
                    notifyInfo("", tipContent, NotificationType.WARNING);
                }
                return true;
            } else if ((diskSpaceUsage.compareTo(BigDecimal.valueOf(INFO_CANCLE))) > 0) {
                // 磁盘空间使用率提示弹窗:增加到大于80%小于等于90%出现||从大于90%减少到大于75%时出现 && 工作空间使用率小于等于90%
                if (showTip) {
                    notifyInfo("", tipContent, NotificationType.INFORMATION);
                }
                return false;
            } else {
                Logger.info("everything is Normal");
            }
        }
        return false;
    }

    /**
     * notifyInfo
     *
     * @param name name
     * @param value value
     * @param type type
     */
    public static void notifyInfo(String name, String value, NotificationType type) {
        NotificationBean notificationBean = new NotificationBean(name, value, type);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    @Override
    protected String title() {
        return TuningUserManageConstant.LEFTTREE_LOGIN_TITLE;
    }

    @Override
    protected String dialogName() {
        return Dialogs.LOGIN.dialogName();
    }

    @Override
    protected String helpUrl() {
        return TuningI18NServer.toLocale("plugins_hyper_tuner_login_help_url");
    }

    @Override
    protected void refreshLogin() {
        TuningLoginUtils.refreshLogin();
    }

    @Override
    protected String getPluginToolWindowID() {
        return TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID;
    }

    @Override
    protected boolean isLoginSucceed(ResponseBean rsp) {
        return TuningLoginUtils.LOGIN_SUCESS.equals(rsp.getCode());
    }

    @Override
    protected boolean isPwdWeakType(ResponseBean rsp) {
        return TuningLoginUtils.LOGIN_PWD_WEAKTYPE.equals(rsp.getCode()) || "0".equals(
                CommonUtil.getRepDataInfo(rsp, WEAK_PWD, WEAK_PWD));
    }

    @Override
    protected boolean isFirstLogin(ResponseBean rsp) {
        return TuningLoginUtils.LOGIN_FIRST_SUCCESS.equals(rsp.getCode());
    }

    @Override
    protected boolean isPwdExpired(ResponseBean rsp) {
        return TuningLoginUtils.LOGIN_SUCCESS_PWD_EXPIRED.equals(rsp.getCode());
    }

    @Override
    protected boolean isPwdWillExpired(ResponseBean rsp) {
        return TuningLoginUtils.LOGIN_SUCCESS_PWD_WILLEXIPIRED.equals(rsp.getCode());
    }

    @Override
    protected boolean isOnlineUserMax(ResponseBean rsp) {
        return TuningLoginUtils.LOGIN_ONLINE_USER_MAX.equals(rsp.getCode());
    }

    @Override
    protected boolean loginSucceedLaterAction(LoginPanel panel, ResponseBean rsp) {
        UserInfoContext.getInstance().setUserInfo(rsp);
        String tips = TuningI18NServer.toLocale("plugins_hyper_tuner_login_sucess");
        // 更新config 配置
        processContextUserInfo(panel.getUserNameField().getText());
        processSavePassword(panel, ConfigProperty.AUTO_LOGIN_CONFIG.vaLue());
        if (panel.getSavePwdCheckBox().isSelected()
                && ValidateUtils.equals(UserInfoContext.getInstance().getRole(), "Admin")) {
            tips += TuningI18NServer.toLocale("plugins_hyper_tuner_label_adminAutoLoginTip");
        }
        checkToolVersion();
        TuningWebServerCertificateAction wbServerCertificateAction = new TuningWebServerCertificateAction();
        wbServerCertificateAction.getCertInfo();
        message(LOGIN_TITLE, tips, NotificationType.INFORMATION);
        return true;
    }

    @Override
    protected boolean loginPwdWeakTypeAction(LoginPanel panel, ResponseBean rsp) {
        message(LOGIN_TITLE, CommonI18NServer.toLocale("common_tips_pwdWeak_warn"),
                NotificationType.WARNING);
        return false;
    }

    @Override
    protected boolean firstLoginLaterAction(LoginPanel panel, ResponseBean rsp) {
        loginChangeCodeStatus = TuningLoginUtils.LOGIN_FIRST_SUCCESS;
        UserInfoContext.getInstance().setUserInfo(rsp);
        return true;
    }

    @Override
    protected boolean pwdExpiredLaterAction(LoginPanel panel, ResponseBean rsp) {
        message(LOGIN_TITLE, rsp.getMessage(), NotificationType.INFORMATION);
        loginChangeCodeStatus = TuningLoginUtils.LOGIN_SUCCESS_PWD_EXPIRED;
        UserInfoContext.getInstance().setUserInfo(rsp);
        return true;
    }

    @Override
    protected boolean pwdWillExpiredLaterAction(LoginPanel panel, ResponseBean rsp) {
        return pwdExpiredLaterAction(panel, rsp);
    }

    @Override
    protected boolean onlineUserMaxLaterAction(LoginPanel panel, ResponseBean rsp) {
        message(LOGIN_TITLE, rsp.getMessage(), NotificationType.ERROR);
        UserInfoContext.getInstance().setUserInfo(rsp);
        ApplicationManager.getApplication().invokeLater(() -> {
            refreshLogin();
        });
        return true;
    }

    @Override
    protected void showLoginErrorInfo(ResponseBean rsp) {
        IDENotificationUtil.notificationCommon(
                new NotificationBean(LOGIN_TITLE, rsp.getMessage(), NotificationType.ERROR));
        ApplicationManager.getApplication().invokeLater(() -> refreshLogin());
    }

    @Override
    protected ResponseBean doLoginRequest(LoginPanel panel) {
        return TuningUserManagerHandler.doLoginRequest(panel.getUserNameField().getPassword(),
                panel.getPwdField().getPassword());
    }

    @Override
    protected String getToolName() {
        return TuningIDEConstant.TOOL_NAME_TUNING;
    }

    @Override
    protected boolean isFirstLoginAccordingStatus() {
        return TuningLoginUtils.LOGIN_FIRST_SUCCESS.equals(loginChangeCodeStatus);
    }

    @Override
    protected boolean isFirstLoginOrPwdExpired() {
        return TuningLoginUtils.LOGIN_FIRST_SUCCESS.equals(loginChangeCodeStatus)
                || TuningLoginUtils.LOGIN_SUCCESS_PWD_EXPIRED.equals(loginChangeCodeStatus);
    }

    @Override
    protected void popChangePwdDialog(ChangePasswordPanel changePwdPanel) {
        // the status should set before call changePwdDialog.displayPanel()
        loginChangeCodeStatus = TuningLoginUtils.LOGIN_OK;
        IDEBaseDialog changePwdDialog = new TuningChangePasswordDialog(null, changePwdPanel);
        refreshLogin();
        changePwdDialog.displayPanel();
    }

    @Override
    protected void doLoginLaterAction() {
        // update global IDEPluginStatus
        TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_LOGIN);
        mainPanel.clearPwd();

        checkDisclaimer();

        // 登录成功后，对每个打开的project刷新左侧树面板
        LeftTreeUtil.refreshLeftTreePanel();

        // 登录成功之后如果打开的是settings面板，则将settings面板关闭
        closeIntellijSettingsDialog();

        getDiscAlarm(true);
    }

    @Override
    protected ResponseBean doGetToolVersionRequest() {
        return TuningUserManagerHandler.doGetToolVersionRequest();
    }

    @Override
    protected String getServerVersion(ResponseBean rsp) {
        Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(rsp.getData());
        return jsonMessage.get("version");
    }

    @Override
    protected String getToolVersionInfo() {
        return TuningI18NServer.toLocale("plugins_hyper_tuner_version_not_the");
    }

    @Override
    protected void setGlobalContext(String version) {
        TuningIDEContext.setValueForGlobalContext(null, BaseCacheVal.SERVER_VERSION.vaLue(), version);
    }

    @Override
    protected String showServerOldTips() {
        return I18NServer.toLocale("plugins_hyper_tuner_version_server_old");
    }

    @Override
    protected String showPluginOldTips() {
        return I18NServer.toLocale("plugins_hyper_tuner_version_plugin_old");
    }

    @Override
    protected ResponseBean doSetAdminPwdRequest(char[] pwd, char[] confirmPwd) {
        return TuningUserManagerHandler.doSetAdminPwdRequest(pwd, confirmPwd);
    }

    @Override
    protected String setPwdErrorInfo(ResponseBean rsp) {
        return rsp.getMessage();
    }

    private void message(String title, String tips, NotificationType type) {
        IDENotificationUtil.notificationCommon(new NotificationBean(title, tips, type));
    }


    /**
     * 通过接口检查是否 展示过免责声明
     */
    private void checkDisclaimer() {
        ResponseBean rsp = TuningUserManagerHandler.checkDisclaimer();
        if (rsp == null) {
            return;
        }
        String code = rsp.getCode();
        if (!"UserManage.Success".equals(code)) {
            return;
        }
        Object dataObj = JSONObject.parse(rsp.getData());
        if (dataObj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) dataObj;
            String sysDisclaimer = jsonObject.getString("SYS_DISCLAIMER");
            if ("0".equals(sysDisclaimer)) {
                IDEBaseDialog dialog =
                        new DisclaimerDialog(TuningUserManageConstant.USER_DISCLAIMER_TITLE, null, null, true);
                dialog.displayPanel();
            }
        }
    }
}