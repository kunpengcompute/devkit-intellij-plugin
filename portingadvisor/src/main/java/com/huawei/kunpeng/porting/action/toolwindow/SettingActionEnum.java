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

package com.huawei.kunpeng.porting.action.toolwindow;

import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.options.ex.ConfigurableExtensionPointUtil;
import com.intellij.openapi.options.ex.ConfigurableVisitor;

import lombok.Getter;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * The class SettingsActionGroup
 *
 * @since v2.2.T4
 */
public enum SettingActionEnum {
    /**
     * 用户管理
     */
    MANAGE_USER(UserManageConstant.MANAGE_USER,
        "com.huawei.kunpeng.porting.UseSettings", 0x001,
        new AnAction(UserManageConstant.MANAGE_USER) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), UserManageConstant.MANAGE_USER);
            }
        }),

    /**
     * 弱口令字典
     */
    WEAK_PASSWORD(WeakPwdConstant.WEAK_PASSWORD_DIC,
        "com.huawei.kunpeng.porting.settings.weakPwdPanel", 0x011,
        new AnAction(WeakPwdConstant.WEAK_PASSWORD_DIC) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), WeakPwdConstant.WEAK_PASSWORD_DIC);
            }
        }),

    /**
     * 系统设置
     */
    SYSTEM_SETTINGS(UserManageConstant.SYSTEM_SETTING_TITLE,
        "com.huawei.kunpeng.porting.settings.SystemSettingPanel", 0x001,
        new AnAction(UserManageConstant.SYSTEM_SETTING_TITLE) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ConfigurableGroup group = ConfigurableExtensionPointUtil.getConfigurableGroup(
                    CommonUtil.getDefaultProject(), true);
                List<ConfigurableGroup> groups = group.getConfigurables().length == 0 ?
                    Collections.emptyList() : Collections.singletonList(group);
                Configurable systemSetting = ConfigurableVisitor.findById(
                    "com.huawei.kunpeng.porting.settings.SystemSettingPanel",
                    Collections.singletonList(group));
                ShowSettingsUtilImpl.getDialog(CommonUtil.getDefaultProject(), groups, systemSetting).show();
            }
        }),

    /**
     * 登录设置
     */
    LOGIN_SETTINGS(I18NServer.toLocale(Constants.PLUGINS_PORTING_LOGINSETTINGS_TITLE),
        "com.huawei.kunpeng.porting.LoginSettings", 0x010,
        new AnAction(I18NServer.toLocale(Constants.PLUGINS_PORTING_LOGINSETTINGS_TITLE)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(),
                        I18NServer.toLocale(Constants.PLUGINS_PORTING_LOGINSETTINGS_TITLE));
            }
        }),

    /**
     * 日志管理
     */
    LOG_MANAGE(PortingUserManageConstant.getLogTitle(),
        "com.huawei.kunpeng.porting.LogSettings", 0x0111,
        new AnAction(PortingUserManageConstant.getLogTitle()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(),
                            PortingUserManageConstant.getLogTitle());
            }

            @Override
            public void update(@NotNull AnActionEvent event) {
                getTemplatePresentation().setText(PortingUserManageConstant.getLogTitle());
            }
        }, PortingUserManageConstant::getLogTitle),

    /**
     * 白名单管理
     */
    WHITELIST_MANAGE(I18NServer.toLocale(Constants.PLUGINS_PORTING_TITLE_WHITELIST_MANAGE),
        "Whitelist Management", 0x001,
        new AnAction(I18NServer.toLocale(Constants.PLUGINS_PORTING_TITLE_WHITELIST_MANAGE)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(),
                        I18NServer.toLocale(Constants.PLUGINS_PORTING_TITLE_WHITELIST_MANAGE));
            }
        }),

    /**
     * 迁移模板
     */
    PORTING_TEMPLATE(I18NServer.toLocale(Constants.PLUGINS_PORTING_TITLE_SOFTWARE_PORTING_TEMPLATE),
        "Porting Template", 0x001,
        new AnAction(I18NServer.toLocale(Constants.PLUGINS_PORTING_TITLE_SOFTWARE_PORTING_TEMPLATE)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(),
                        I18NServer.toLocale(Constants.PLUGINS_PORTING_TITLE_SOFTWARE_PORTING_TEMPLATE));
            }
        }),

    /**
     * 扫描参数
     */
    SCAN_PARAM(I18NServer.toLocale(Constants.PLUGINS_COMMON_PORTING_SETTINGS_CONFIGURE_SCAN_PARAMS),
        "Configure Scan Parameter", 0x011,
        new AnAction(I18NServer.toLocale(Constants.PLUGINS_COMMON_PORTING_SETTINGS_CONFIGURE_SCAN_PARAMS)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(),
                        I18NServer.toLocale(Constants.PLUGINS_COMMON_PORTING_SETTINGS_CONFIGURE_SCAN_PARAMS));
            }
        }),

    /**
     * 阈值设置
     */
    THRESHOLD_SETTING(I18NServer.toLocale(Constants.PLUGINS_COMMON_PORTING_SETTINGS_THRESHOLD_CONFIG),
        "com.huawei.kunpeng.porting.ThresholdSettings", 0x101,
        new AnAction(I18NServer.toLocale(Constants.PLUGINS_COMMON_PORTING_SETTINGS_THRESHOLD_CONFIG)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(),
                        I18NServer.toLocale(Constants.PLUGINS_COMMON_PORTING_SETTINGS_THRESHOLD_CONFIG));
            }
        }),

    /**
     * web证书
     */
    WEB_SERVER_CERTIFICATE(CommonI18NServer.toLocale(Constants.PLUGINS_COMMON_CERTIFICATE_TITLE),
        "com.huawei.kunpeng.porting.webservercert", 0x011,
        new AnAction(CommonI18NServer.toLocale(Constants.PLUGINS_COMMON_CERTIFICATE_TITLE)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(),
                        CommonI18NServer.toLocale(Constants.PLUGINS_COMMON_CERTIFICATE_TITLE));
            }
        }),

    /**
     * 证书吊销列表
     */
    CERTIFICATE_REVOCATION_LIST(I18NServer.toLocale(Constants.PLUGINS_PORTING_CERTIFICATE_REVOCATION_LIST),
        "com.huawei.kunpeng.porting.webservercert.revocation.list", 0x011,
        new AnAction(I18NServer.toLocale(Constants.PLUGINS_PORTING_CERTIFICATE_REVOCATION_LIST)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ShowSettingsUtil.getInstance().showSettingsDialog(CommonUtil.getDefaultProject(),
                    I18NServer.toLocale(Constants.PLUGINS_PORTING_CERTIFICATE_REVOCATION_LIST));
            }
        });


    /**
     * 显示名称
     */
    @Getter
    private String name;

    /**
     * SearchableConfigurable 唯一ID名称
     */
    @Getter
    private String id;

    /**
     * 标记位：目前一共使用了3位，从低位为到高位含义如下：
     * 0x001:是否管理员用户需要，默认为1，代表需要。
     * 0x010:是否普通用户需要，默认为0，代表不需要。
     * 0x100:是否需要划分分割线需要，默认为0，代表不需要。
     */
    private int flag = 0x001;

    /**
     * 显示动作
     */
    @Getter
    private AnAction anAction;

    /**
     * 更新动作
     */
    @Getter
    private UpdateInterface updateInterface;

    /**
     * 是否管理员用户需要
     */
    private boolean isComUserNeed;

    /**
     * 是否普通用户需要
     */
    private boolean isAddSeparator = false;

    /**
     * 是否需要划分分割线需要
     */
    private boolean isAdminUserNeed = true;

    /**
     * 构造函数
     *
     * @param name            name
     * @param id              id
     * @param flag            预留|isAddSeparator|isAdminUserNeed|isComUserNeed
     * @param anAction        anAction
     * @param updateInterface updateInterface
     */
    SettingActionEnum(String name, String id, int flag, AnAction anAction, UpdateInterface updateInterface) {
        this.name = name;
        this.id = id;
        this.flag = flag;
        this.anAction = anAction;
        this.updateInterface = updateInterface;
    }

    /**
     * 构造函数
     *
     * @param name     name
     * @param id       id
     * @param flag     预留|isAddSeparator|isAdminUserNeed|isComUserNeed
     * @param anAction anAction
     */
    SettingActionEnum(String name, String id, int flag, AnAction anAction) {
        this(name, id, flag, anAction, null);
    }

    /**
     * isComUserNeed
     *
     * @return isComUserNeed
     */
    public boolean isComUserNeed() {
        return ((flag & 0x010) == 0x010);
    }

    /**
     * isAddSeparator
     *
     * @return isAddSeparator
     */
    public boolean isAddSeparator() {
        return ((flag & 0x100) == 0x100);
    }

    /**
     * isAdminUserNeed
     *
     * @return isAdminUserNeed
     */
    public boolean isAdminUserNeed() {
        return ((flag & 0x001) == 0x001);
    }

    private static class Constants {
        private static final String PLUGINS_PORTING_LOGINSETTINGS_TITLE = "plugins_porting_loginsettings_title";
        private static final String PLUGINS_PORTING_TITLE_WHITELIST_MANAGE = "plugins_porting_title_whitelistManage";
        private static final String PLUGINS_PORTING_TITLE_SOFTWARE_PORTING_TEMPLATE =
            "plugins_porting_title_software_porting_template";
        private static final String PLUGINS_COMMON_PORTING_SETTINGS_CONFIGURE_SCAN_PARAMS =
            "plugins_common_porting_settings_configureScanParams";
        private static final String PLUGINS_COMMON_PORTING_SETTINGS_THRESHOLD_CONFIG =
            "plugins_common_porting_settings_threshold_config";
        private static final String PLUGINS_COMMON_CERTIFICATE_TITLE = "plugins_common_certificate_title";
        private static final String PLUGINS_PORTING_CERTIFICATE_REVOCATION_LIST =
            "plugins_porting_certificate_revocation_list";
    }
}
