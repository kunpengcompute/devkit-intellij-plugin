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

package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings;

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningWeakPwdConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.setting.sys.SysSettingConfigurable;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import lombok.Getter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The class SettingsActionGroup
 *
 * @since v2.2.T4
 */
public enum SettingActionEnum {
    /**
     * 用户管理
     */
    MANAGE_USER(TuningUserManageConstant.MANAGE_USER,
            "com.huawei.kunpeng.tuning.UseSettings", 0x001,
            new AnAction(TuningUserManageConstant.MANAGE_USER) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), TuningUserManageConstant.MANAGE_USER);
                }
            }),

    /**
     * 弱口令字典
     */
    WEAK_PASSWORD(TuningWeakPwdConstant.WEAK_PASSWORD_DIC,
            "com.huawei.kunpeng.tuning.settings.weakPwdPanel", 0x011,
            new AnAction(TuningWeakPwdConstant.WEAK_PASSWORD_DIC) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(
                                    CommonUtil.getDefaultProject(), TuningWeakPwdConstant.WEAK_PASSWORD_DIC);
                }
            }),

    /**
     * 系统设置
     */
    SYSTEM_SETTINGS(TuningUserManageConstant.SYSTEM_SETTING_TITLE,
            "com.huawei.kunpeng.tuning.settings.SystemSettingPanel", 0x001,
            new AnAction(TuningUserManageConstant.SYSTEM_SETTING_TITLE) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), SysSettingConfigurable.class);
                }
            }),

    /**
     * 登录设置
     */
    LOGIN_SETTINGS(TuningI18NServer.toLocale("plugins_hyper_tuner_loginsettings_title"),
            "com.huawei.kunpeng.kunpeng.tuning.LoginSettings", 0x010,
            new AnAction(TuningI18NServer.toLocale("plugins_hyper_tuner_loginsettings_title")) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(
                                    CommonUtil.getDefaultProject(),
                                    TuningI18NServer.toLocale("plugins_hyper_tuner_loginsettings_title"));
                }
            }),
    /**
     * 日志管理
     */
    LOG_MANAGE(TuningUserManageConstant.getLogTitle(),
            "com.huawei.kunpeng.tuning.LogSettings", 0x0111,
            new AnAction(TuningUserManageConstant.getLogTitle()) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), TuningUserManageConstant.getLogTitle());
                }

                @Override
                public void update(@NotNull AnActionEvent event) {
                    getTemplatePresentation().setText(TuningUserManageConstant.getLogTitle());
                }
            },
            TuningUserManageConstant::getLogTitle),
    /**
     * web证书
     */
    WEB_SERVER_CERTIFICATE(
            TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_title"),
            "com.huawei.kunpeng.tuning.webservercert",
            0x111,
            new AnAction(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_title")) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(
                                    CommonUtil.getDefaultProject(),
                                    TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_title"));
                }
            });

    /**
     * SearchableConfigurable 唯一ID名称
     */
    @Getter
    private String id;

    /**
     * 显示名称
     */
    @Getter
    private String name;

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
     * 对actions进行分组
     *
     * @return action分组信息
     */
    public static List<List<SettingActionEnum>> groups() {
        return Arrays.asList(Arrays.asList(MANAGE_USER, WEAK_PASSWORD, SYSTEM_SETTINGS, LOGIN_SETTINGS, LOG_MANAGE),
                Collections.singletonList(WEB_SERVER_CERTIFICATE));
    }

    /**
     * isAdminUserNeed
     *
     * @return isAdminUserNeed
     */
    public boolean isAdminUserNeed() {
        return ((flag & 0x001) == 0x001);
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
     * 判断该action是否加入group
     *
     * @param role 用户角色
     * @return true:加入 false:不加入
     */
    public boolean shouldAddBy(String role) {
        boolean isAdminUser = Objects.equals(USER_ROLE_ADMIN, role);
        return (isAdminUserNeed() && isAdminUser) || (!isAdminUser && isComUserNeed());
    }
}
