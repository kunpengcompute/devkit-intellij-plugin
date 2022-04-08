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

package com.huawei.kunpeng.porting.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.PortingAction;
import com.huawei.kunpeng.porting.common.utils.DiskUtil;
import com.huawei.kunpeng.porting.ui.panel.PortingPanel;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 右键源码分析弹框
 *
 * @since 2020-10-09
 */
public class PortingWrapDialog extends IdeaDialog {
    private static final String HELP_URL = I18NServer.toLocale("plugins_porting_porting_help_url");

    private PortingPanel portingPanel;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public PortingWrapDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? I18NServer.toLocale("plugins_porting_params_config") : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.PORTING.dialogName() : dialogName;
        this.mainPanel = panel;
        if (mainPanel instanceof PortingPanel) {
            portingPanel = (PortingPanel) mainPanel;
        }

        // 设置弹框大小是否可变
        this.resizable = resizable;

        // 设置弹框中确认取消按钮的名称
        setOKAndCancelName(I18NServer.toLocale("plugins_common_button_analysis"),
            I18NServer.toLocale("plugins_common_button_cancel"));

        // 设置帮助
        setHelp(I18NServer.toLocale("plugins_porting_src_migration_tip"), HELP_URL);

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public PortingWrapDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
        updateDialog();
    }

    /**
     * 刷新弹框
     */
    @Override
    public void updateDialog() {
        // 检查磁盘空间
        int ans = DiskUtil.queryDiskState();
        // 检查是否已成功上传文件且磁盘空间充足
        if (portingPanel.checkRequired() && ans != DiskUtil.DISK_STATUS_RED_ALARM) {
            okAction.setEnabled(true);
        } else {
            okAction.setEnabled(false);
        }
    }

    /**
     * 分析按钮状态更改
     *
     * @param flag false置灰，反之则然
     */
    public void setAnalyzeEnable(boolean flag) {
        okAction.setEnabled(flag);
    }

    @Override
    protected boolean okVerify() {
        return portingPanel.doValidate() == null;
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        if (mainPanel.getAction() instanceof PortingAction) {
            PortingAction action = (PortingAction) mainPanel.getAction();
            action.onOKAction(portingPanel.getAnalyzeParams());
        }
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
        if (mainPanel.getAction() instanceof PortingAction) {
            PortingAction action = (PortingAction) mainPanel.getAction();
            action.onCancelAction(null, null);
        }
    }

    /**
     * 创建面板内容
     *
     * @return JComponent
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}
