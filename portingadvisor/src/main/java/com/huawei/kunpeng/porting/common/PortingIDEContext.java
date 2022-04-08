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

package com.huawei.kunpeng.porting.common;

import static com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus.IDE_STATUS_SERVER_CONFIG;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.ChangePasswordDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.LoginWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.ChangePasswordPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingChangePasswordDialog;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingLoginWrapDialog;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingServerConfigWrapDialog;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingWrapDialog;
import com.huawei.kunpeng.porting.ui.panel.PortingLoginPanel;
import com.huawei.kunpeng.porting.ui.panel.PortingPanel;
import com.huawei.kunpeng.porting.ui.panel.PortingServerConfigPanel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * porting context info
 *
 * @since 2.3.T10
 */
public class PortingIDEContext extends IDEContext {
    /**
     * 常量实例
     */
    public static IDEContext instance = new IDEContext();

    /**
     * 当前用户的历史报告数量
     */
    private static AtomicInteger reportsNum = new AtomicInteger(0);

    /**
     * get global PortingIDEPluginStatus
     *
     * @return IDEPluginStatus 获取PortingIDE状态值
     */
    public static IDEPluginStatus getPortingIDEPluginStatus() {
        return Optional.ofNullable(getIDEPluginStatus(PortingIDEConstant.TOOL_NAME_PORTING))
            .orElse(IDEPluginStatus.IDE_STATUS_INIT);
    }

    /**
     * set global PortingIDEPluginStatus
     *
     * @param value 设置PortingIDE的状态值
     */
    public static void setPortingIDEPluginStatus(IDEPluginStatus value) {
        setIDEPluginStatus(PortingIDEConstant.TOOL_NAME_PORTING, value);
    }

    /**
     * check porting 是否登陆
     *
     * @return boolean check是否OK
     */
    public static boolean checkPortingLogin() {
        return checkLogin(PortingIDEConstant.TOOL_NAME_PORTING);
    }

    /**
     * 右键操作前check是否配置服务器，否则弹出相关窗口
     *
     * @return boolean check是否OK
     */
    public static boolean checkServerConfig() {
        int value = 0;
        IDEPluginStatus valueFromGlobalContext = getIDEPluginStatus(PortingIDEConstant.TOOL_NAME_PORTING);
        value = (valueFromGlobalContext).value();

        if (value < IDE_STATUS_SERVER_CONFIG.value()
            && StringUtil.stringIsEmpty(PortingCommonUtil.readCurIpFromConfig())) {
            IDEBasePanel panel = new PortingServerConfigPanel(null);

            PortingServerConfigWrapDialog serverConfigDialog = new PortingServerConfigWrapDialog(
                PortingUserManageConstant.CONFIG_TITLE, panel);
            serverConfigDialog.displayPanel();
            final int exitCode = serverConfigDialog.getExitCode();
            return exitCode != DialogWrapper.CANCEL_EXIT_CODE;
        }
        return true;
    }


    /**
     * 右键操作前check是否登陆，否则弹出相关窗口
     *
     * @return boolean check是否OK
     */
    public static boolean checkLoginForDialog() {
        if (checkLogin(PortingIDEConstant.TOOL_NAME_PORTING)) {
            return true;
        }

        // 未登陆则弹框
        IDEBasePanel panel = new PortingLoginPanel(null);
        LoginWrapDialog loginDialog = new PortingLoginWrapDialog(panel);
        loginDialog.displayPanel();
        int exitCode = loginDialog.getExitCode();
        if (exitCode == LoginWrapDialog.CANCEL_EXIT_CODE) {
            return false;
        }
        IDEBasePanel panel1 = new ChangePasswordPanel(null);
        ChangePasswordDialog changePwdDialog = new PortingChangePasswordDialog(null, panel1);
        exitCode = changePwdDialog.getExitCode();
        if ((exitCode == ChangePasswordDialog.CANCEL_EXIT_CODE) && (
            LoginUtils.USER_FIRST_LOGIN.equals(LoginWrapDialog.loginChangeCodeStatus)
                || LoginUtils.LOGIN_EXPIRED.equals(LoginWrapDialog.loginChangeCodeStatus))) {
            return false;
        }
        return true;
    }

    /**
     * 获取porting插件webview页面index入口
     *
     * @return string value值
     */
    public static String getPortingWebViewIndex() {
        if (getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, BaseCacheVal.PORTING_WEB_VIEW_INDEX.vaLue())
            == null) {
            IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
                BaseCacheVal.PORTING_WEB_VIEW_INDEX.vaLue(),
                CommonUtil.getPluginInstalledPathFile(IDEConstant.PORTING_WEB_VIEW_PATH)
                    + IDEConstant.PATH_SEPARATOR
                    + PortingIDEConstant.TOOL_NAME_PORTING + IDEConstant.PATH_SEPARATOR
                    + IDEConstant.PORTING_WEB_VIEW_INDEX_HTML);
        }
        return getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
            BaseCacheVal.PORTING_WEB_VIEW_INDEX.vaLue());
    }

    /**
     * 获取porting源码扫描中文反斜杠唯一替换字符串
     *
     * @return string value值
     */
    public static String getPortingZHPathUnicodeStr() {
        return getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
            BaseCacheVal.PORTING_ZH_PATH_UNICODE_STR.vaLue());
    }

    /**
     * 获取当前系统编码
     *
     * @return string value值
     */
    public static String getCurrentCharset() {
        return getValueFromGlobalContext(null,
            BaseCacheVal.CURRENT_CHARSET.vaLue());
    }

    /**
     * 获取当前用户报告数量
     *
     * @return int
     */
    public static int getReportsNum() {
        return reportsNum.get();
    }

    /**
     * 设置当前报告数量
     *
     * @param num num
     */
    public static void setReportsNum(int num) {
        reportsNum = new AtomicInteger(num);
    }

    /**
     * 增加报告数量
     */
    public static void increaseReportsNum() {
        reportsNum.incrementAndGet();
    }

    /**
     * 减少报告数量
     */
    public static void decreaseReportsNum() {
        reportsNum.decrementAndGet();
    }

    /**
     * 报告数量清零
     */
    public static void clearReportsNum() {
        reportsNum = new AtomicInteger(0);
    }


    // 初始化常用的基础变量缓存
    static {
        IDEContext.setValueForGlobalContext(null, PortingIDEConstant.TOOL_NAME_DEP, new HashMap<>());
        IDEContext.setValueForGlobalContext(null, PortingIDEConstant.TOOL_NAME_PORTING, new HashMap<>());
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_DEP,
            BaseCacheVal.BASE_URL.vaLue(), "/dependency/api");
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_DEP, BaseCacheVal.IP.vaLue(), null);
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_DEP, BaseCacheVal.PORT.vaLue(), null);
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_DEP, BaseCacheVal.TOKEN.vaLue(), null);
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
            BaseCacheVal.BASE_URL.vaLue(), "/porting/api");
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, BaseCacheVal.IP.vaLue(), null);
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, BaseCacheVal.PORT.vaLue(), null);
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, BaseCacheVal.TOKEN.vaLue(), null);
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
            BaseCacheVal.PORTING_ZH_PATH_UNICODE_STR.vaLue(), CommonUtil.generateRandomStr());
        setIDEPluginStatus(PortingIDEConstant.TOOL_NAME_PORTING, IDEPluginStatus.IDE_STATUS_INIT);
    }
}
