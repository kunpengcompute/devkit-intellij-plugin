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

package com.huawei.kunpeng.porting.common.utils;

import static com.huawei.kunpeng.intellij.ui.utils.UILoginUtils.decrypt;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LoginPanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.toolwindow.DeleteAllReportsAction;
import com.huawei.kunpeng.porting.common.CacheDataOpt;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingLoginWrapDialog;
import com.huawei.kunpeng.porting.ui.panel.PortingLoginPanel;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeLoginPanel;

import com.intellij.ide.impl.ProjectUtil;
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
public final class LoginUtils {
    /**
     * ????????????
     */
    public static final String LOGIN_OK = "0";

    /**
     * ?????????????????????
     */
    public static final String USER_FIRST_LOGIN = RespondStatus.LOGIN_FIRST_SUCCESS.value();

    /**
     * ???????????????
     */
    public static final String LOGIN_EXPIRED = RespondStatus.LOGIN_SUCCESS_PWD_EXPIRED.value();

    /**
     * ??????????????????
     */
    public static final String LOGIN_WILL_EXPIRED = RespondStatus.LOGIN_PWD_EXPIRED.value();

    /**
     * ?????????
     */
    public static final String LOGIN_WEAK_PWD_WARN = RespondStatus.LOGIN_PWD_WEAK.value();

    private LoginUtils() {
    }

    /**
     * ??????DPAPI??????????????????
     *
     * @param panel login??????
     */
    public static void encryptDPAPI(LoginPanel panel) {
    }

    private static String encodeForOS(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            sb.append("^").append(ch);
        }
        return sb.toString();
    }

    private static String decodeForOS(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < input.length(); ) {
            char ch = input.charAt(i);
            sb.append(ch);
            i += 2;
        }
        return sb.toString();
    }

    /**
     * ????????????
     */
    public static void autoLogin() {
        // ????????????
        String userName = PortingUserInfoContext.getInstance().getUserName();
        String osName = System.getProperty("os.name");
        if (ValidateUtils.isEmptyString(osName)) {
            Logger.error("Get os error.");
            return;
        }
        if (ValidateUtils.isEmptyString(
            userName) && JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "autoLogin", boolean.class)) {
            RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/login/",
                HttpMethod.POST.vaLue(), "");
            Map<String, String> obj = new HashMap<>();
            obj.put("username",
                JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "userName", String.class));
            // ??????IDE????????????????????????????????????????????????
            ConfigUtils.updateIDEContextConfigInfoByConfigFile(null);
            obj.put("password", decrypt());
            message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
            ResponseBean rsp = PortingHttpsServer.INSTANCE.requestData(message);
            if (rsp == null) {
                return;
            }
            String loginStatus = rsp.getStatus();
            if (!USER_FIRST_LOGIN.equals(loginStatus) && !LOGIN_EXPIRED.equals(loginStatus)) {
                PortingUserInfoContext.getInstance().setUserInfo(rsp);
                PortingIDEContext.setPortingIDEPluginStatus(IDEPluginStatus.IDE_STATUS_LOGIN);
                Logger.info("auto login successfully!");
            }
        }
    }

    /**
     * ????????????
     */
    public static void gotoLogin() {
        IDEBasePanel panel = new PortingLoginPanel(null);
        IDEBaseDialog dialog = new PortingLoginWrapDialog(panel);
        dialog.displayPanel();
    }

    /**
     * ????????????
     */
    public static void logout() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/logout/",
            HttpMethod.POST.vaLue(), "");

        // ??????????????????
        ResponseBean rsp = PortingHttpsServer.INSTANCE.requestData(message);
        if (rsp == null) {
            return;
        }
        clearStatus();
    }

    /**
     * ??????????????????????????????
     */
    public static void refreshLogin() {
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            DeleteAllReportsAction.closeAllOpenedReports(project);
            // ??????????????????????????????????????????
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }

    /**
     * ??????????????????
     */
    public static void clearStatus() {
        // ??????????????????
        PortingUserInfoContext.clearStatus();

        // update global IDEPluginStatus
        PortingIDEContext.setPortingIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        CacheDataOpt.clearUserFiles();
        // ??????token
        CacheDataOpt.updateGlobalToken(PortingIDEConstant.TOOL_NAME_PORTING, null);
        // ?????????project???????????????
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // ????????????????????????????????????????????????
            DeleteAllReportsAction.closeAllOpenedReports(PortingCommonUtil.getDefaultProject());
            // ??????????????????????????????
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }
}
