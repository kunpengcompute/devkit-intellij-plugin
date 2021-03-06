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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import static com.huawei.kunpeng.intellij.ui.utils.UILoginUtils.decrypt;

import com.huawei.kunpeng.hyper.tuner.action.LeftTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.CacheDataOpt;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningLoginWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningLoginPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeLoginPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.HashMap;
import java.util.Map;

/**
 * ?????????????????????
 *
 * @since 2020/10/13
 */
public final class TuningLoginUtils {
    /**
     * ????????????
     */
    public static final String LOGIN_OK = "0";

    /**
     * ????????????
     */
    public static final String LOGIN_SUCESS = "UserManage.Success";

    /**
     * ??????????????????????????????
     */
    public static final String LOGIN_FIRST_SUCCESS = "UserManage.session.Post.FirstLogin";

    /**
     * ??????????????????????????????????????????
     */
    public static final String LOGIN_SUCCESS_PWD_EXPIRED = "UserManage.session.Post.PwdExpired";

    /**
     * ??????????????????????????????
     */
    public static final String LOGIN_SUCCESS_PWD_WILLEXIPIRED = "UserManage.session.Post.PwdWillExpired";

    /**
     * ????????????????????????????????????
     */
    public static final String LOGIN_PWD_WEAKTYPE = "UserManage.WeakPassword.Post.WeakTypePwd";

    /**
     * ???????????????????????????
     */
    public static final String LOGIN_ONLINE_USER_MAX = "UserManage.session.Post.OnlineUserMax";

    // ????????????
    private static final String LOGOUT_SUCESS = TuningI18NServer.toLocale("plugins_hyper_tuner_logout_sucess");

    // ????????????
    private static final String LOGOUT_FAILD = TuningI18NServer.toLocale("plugins_hyper_tuner_logout_faild");

    private TuningLoginUtils() {
    }

    /**
     * ????????????
     */
    public static void autoLogin() {
        // ????????????
        String userName = UserInfoContext.getInstance().getUserName();
        String osName = System.getProperty("os.name");
        if (ValidateUtils.isEmptyString(osName)) {
            Logger.error("Get os error.");
            return;
        }
        Boolean autoLogin = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "autoLogin", boolean.class);
        if (ValidateUtils.isEmptyString(userName) && autoLogin != null && autoLogin) {
            RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                    "user-management/api/v2.2/users/session/", HttpMethod.POST.vaLue(), "");
            Map<String, String> obj = new HashMap();
            obj.put("username", JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(),
                    "userName", String.class));
            // ??????IDE????????????????????????????????????????????????
            ConfigUtils.updateIDEContextConfigInfoByConfigFile(null);
            obj.put("password", decrypt());
            message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
            ResponseBean rsp = TuningHttpsServer.INSTANCE.requestData(message);
            if (rsp == null) {
                return;
            }
            String loginStatus = rsp.getCode();
            if (!LOGIN_FIRST_SUCCESS.equals(loginStatus) && !LOGIN_SUCCESS_PWD_EXPIRED.equals(loginStatus)) {
                UserInfoContext.getInstance().setUserInfo(rsp);
                TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_LOGIN);
                Logger.info("auto login successfully!");
            }
        }
    }

    /**
     * ????????????
     */
    public static void gotoLogin() {
        IDEBasePanel panel = new TuningLoginPanel(null);
        IDEBaseDialog dialog = new TuningLoginWrapDialog(panel);
        dialog.displayPanel();
    }

    /**
     * ????????????
     */
    public static void logout() {
        String tips = "";
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "user-management/api/v2.2/users/session/" + UserInfoContext.getInstance().getLoginId() + "/",
                HttpMethod.DELETE.vaLue(), "");
        // ??????????????????
        ResponseBean rsp = TuningHttpsServer.INSTANCE.requestData(message);
        if (rsp != null && rsp.getCode().equals(LOGIN_SUCESS)) {
            tips = LOGOUT_SUCESS;
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.INFORMATION));
        } else {
            tips = LOGOUT_FAILD;
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.ERROR));
            return;
        }

        // ??????????????????
        UserInfoContext.getInstance().clearUserInfo();

        // update global IDEPlutus
        TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        CacheDataOpt.clearUserFiles();
        // ?????????project???????????????
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // ????????????????????????????????????????????????
            LeftTreeAction.instance().closeAllOpenedWebViewPage(project);
            // ??????????????????????????????
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(project).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }

    /**
     * ??????????????????????????????
     */
    public static void refreshLogin() {
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // ????????????????????????????????????????????????
            LeftTreeAction.instance().closeAllOpenedWebViewPage(project);
            // ??????????????????????????????????????????
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(project).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }

    /**
     * ??????????????????
     */
    public static void clearStatus() {
        // ??????????????????
        UserInfoContext.getInstance().clearUserInfo();

        // update global IDEPluginStatus
        TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        CacheDataOpt.clearUserFiles();
        // ??????token
        CacheDataOpt.updateGlobalToken(TuningIDEConstant.TOOL_NAME_TUNING, null);

        // ?????????project???????????????
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // ????????????????????????????????????????????????
            LeftTreeAction.instance().closeAllOpenedWebViewPage(TuningCommonUtil.getDefaultProject());
            // ??????????????????????????????
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                    TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }
}
