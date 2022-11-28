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

package com.huawei.kunpeng.intellij.ui.dialog;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.constant.InstallConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.enums.CheckConnResponse;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.DeployUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 指纹确认弹窗
 *
 * @since 2021-04-10
 */
public class NewFingerTipDialog extends IdeaDialog {
    private SshConfig config;

    public SshConfig getConfig() {
        return config;
    }

    public void setConfig(SshConfig config) {
        this.config = config;
    }

    private ActionOperate actionOperate;

    public ActionOperate getActionOperate() {
        return actionOperate;
    }

    public void setActionOperate(ActionOperate actionOperate) {
        this.actionOperate = actionOperate;
    }

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param dimension  维度
     * @param resizable  大小是否可变
     */
    public NewFingerTipDialog(String title, String dialogName, IDEBasePanel panel, Rectangle rectangle,
                              Dimension dimension, boolean resizable) {
        this.title = ValidateUtils.isEmptyString(title) ? InstallConstant.FINGER_CONFIRM_TITLE : title;
        this.dialogName = ValidateUtils.isEmptyString(dialogName) ? Dialogs.FINGER_TIP.dialogName() : dialogName;
        this.mainPanel = panel;
        // 设置弹框中按钮的名称
        setOKAndCancelName(CommonI18NServer.toLocale("common_yes"),
                CommonI18NServer.toLocale("common_no"));
        // 无位置信息时居中显示
        this.rectangle = rectangle;
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public NewFingerTipDialog(String title, IDEBasePanel panel, Rectangle rectangle) {
        this(title, null, panel, rectangle, null, false);
    }

    /**
     * 不带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public NewFingerTipDialog(String title, String dialogName, IDEBasePanel panel, Dimension dimension,
                              boolean resizable) {
        this(title, dialogName, panel, null, dimension, resizable);
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public NewFingerTipDialog(String title, IDEBasePanel panel, Dimension dimension) {
        this(title, null, panel, null, dimension, false);
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public NewFingerTipDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, null, false);
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void onOKAction() {
        Logger.info("ConfirmFinger to connect ");
        // 执行连接测试
        DeployUtil.newExecuteOnPoolTestConn(config, actionOperate);
    }

    @Override
    protected void onCancelAction() {
        actionOperate.actionOperate(CheckConnResponse.CANCEL);
    }
}
