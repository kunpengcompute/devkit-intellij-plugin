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

package com.huawei.kunpeng.porting.action.setting.whitelist;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.ui.panel.WhitelistManagePanel;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

/**
 * 白名单管理
 *
 * @since 2020-10-12
 */
public class WhitelistManageConfigurable implements SearchableConfigurable {
    private static final String ID = "Whitelist Management";
    private static final String DISPLAY_NAME =
            I18NServer.toLocale("plugins_porting_title_whitelistManage");
    private WhitelistManagePanel whitelistManagePanel;

    /**
     * 获取ID
     *
     * @return ID
     */
    @Override
    @NotNull
    public String getId() {
        return ID;
    }

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getHelpTopic() {
        return "";
    }

    /**
     * 创建面板
     *
     * @return whitelistManagePanel
     */
    @Override
    public JComponent createComponent() {
        if (whitelistManagePanel == null) {
            whitelistManagePanel = new WhitelistManagePanel();
        }
        return whitelistManagePanel.getPanel();
    }

    /**
     * 是否修改判断
     *
     * @return boolean
     */
    @Override
    public boolean isModified() {
        return whitelistManagePanel != null && whitelistManagePanel.isModified();
    }

    /**
     * 点击Apply事件
     *
     * @throws ConfigurationException 异常
     */
    @Override
    public void apply() throws ConfigurationException {
        if (whitelistManagePanel != null) {
            whitelistManagePanel.apply();
            whitelistManagePanel.clearPwd(); // 安全整改清除密码。
        }
    }

    /**
     * 点击reset按钮事件
     */
    @Override
    public void reset() {
        if (whitelistManagePanel != null) {
            whitelistManagePanel.reset();
        }
    }

    /**
     * disposeUIResources方法
     */
    @Override
    public void disposeUIResources() {
        if (whitelistManagePanel != null) {
            whitelistManagePanel.clearPwd(); // 安全整改清除密码。
        }
        whitelistManagePanel = null;
    }
}
