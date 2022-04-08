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
 * 通用校验工具类
 *
 * @since 2020/10/13
 */
public final class TuningLoginUtils {
    /**
     * 登录成功
     */
    public static final String LOGIN_OK = "0";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCESS = "UserManage.Success";

    /**
     * 登录成功并是首次登录
     */
    public static final String LOGIN_FIRST_SUCCESS = "UserManage.session.Post.FirstLogin";

    /**
     * 登录成功密码超过三个月没修改
     */
    public static final String LOGIN_SUCCESS_PWD_EXPIRED = "UserManage.session.Post.PwdExpired";

    /**
     * 登录成功密码即将过期
     */
    public static final String LOGIN_SUCCESS_PWD_WILLEXIPIRED = "UserManage.session.Post.PwdWillExpired";

    /**
     * 密码为弱口令，请重新设置
     */
    public static final String LOGIN_PWD_WEAKTYPE = "UserManage.WeakPassword.Post.WeakTypePwd";

    /**
     * 在线用户达到最大值
     */
    public static final String LOGIN_ONLINE_USER_MAX = "UserManage.session.Post.OnlineUserMax";

    // 登出成功
    private static final String LOGOUT_SUCESS = TuningI18NServer.toLocale("plugins_hyper_tuner_logout_sucess");

    // 登出失败
    private static final String LOGOUT_FAILD = TuningI18NServer.toLocale("plugins_hyper_tuner_logout_faild");

    private TuningLoginUtils() {
    }

    /**
     * 自动登录
     */
    public static void autoLogin() {
        // 自动登录
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
            // 设置IDE缓存，实现对上一个用户的自动解密
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
     * 自动登录
     */
    public static void gotoLogin() {
        IDEBasePanel panel = new TuningLoginPanel(null);
        IDEBaseDialog dialog = new TuningLoginWrapDialog(panel);
        dialog.displayPanel();
    }

    /**
     * 退出登录
     */
    public static void logout() {
        String tips = "";
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "user-management/api/v2.2/users/session/" + UserInfoContext.getInstance().getLoginId() + "/",
                HttpMethod.DELETE.vaLue(), "");
        // 调用登出接口
        ResponseBean rsp = TuningHttpsServer.INSTANCE.requestData(message);
        if (rsp != null && rsp.getCode().equals(LOGIN_SUCESS)) {
            tips = LOGOUT_SUCESS;
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.INFORMATION));
        } else {
            tips = LOGOUT_FAILD;
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.ERROR));
            return;
        }

        // 清除所有状态
        UserInfoContext.getInstance().clearUserInfo();

        // update global IDEPlutus
        TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        CacheDataOpt.clearUserFiles();
        // 对每个project更新左侧树
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // 重新配置后关闭打开的历史报告页面
            LeftTreeAction.instance().closeAllOpenedWebViewPage(project);
            // 用户退出后更新左侧树
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(project).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }

    /**
     * 左侧树刷新为登录面板
     */
    public static void refreshLogin() {
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // 重新配置后关闭打开的历史报告页面
            LeftTreeAction.instance().closeAllOpenedWebViewPage(project);
            // 点击后将左侧树刷新为加载面板
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(project).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }

    /**
     * 清除所有状态
     */
    public static void clearStatus() {
        // 清除所有状态
        UserInfoContext.getInstance().clearUserInfo();

        // update global IDEPluginStatus
        TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        CacheDataOpt.clearUserFiles();
        // 清除token
        CacheDataOpt.updateGlobalToken(TuningIDEConstant.TOOL_NAME_TUNING, null);

        // 对每个project更新左侧树
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // 重新配置后关闭打开的历史报告页面
            LeftTreeAction.instance().closeAllOpenedWebViewPage(TuningCommonUtil.getDefaultProject());
            // 用户退出后更新左侧树
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                    TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }
}
