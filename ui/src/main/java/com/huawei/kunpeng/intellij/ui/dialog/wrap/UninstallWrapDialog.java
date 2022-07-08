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

package com.huawei.kunpeng.intellij.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.SshAction;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LeftTreeLoadingPanel;
import com.huawei.kunpeng.intellij.ui.panel.UninstallPanel;
import com.intellij.openapi.ui.DialogEarthquakeShaker;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 安装弹框
 *
 * @since 2020-09-25
 */
public abstract class UninstallWrapDialog extends IdeaDialog implements ActionOperate {
    /**
     * checkButton。
     */
    public static JButton checkButton;

    /**
     * gifLabel。
     */
    public static JLabel gifLabel;

    private static final String HELP_URL = CommonI18NServer.toLocale("plugins_ui_common_uninstall_help_url");

    /**
     * 将要卸载的服务器ip
     */
    public String needUninstallIp;

    private JButton check;

    private JLabel loadGif;

    private ActionOperate actionOperate = this;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param rectangle  位置大小信息
     * @param resizable  大小是否可变
     */
    public UninstallWrapDialog(String title, String dialogName, IDEBasePanel panel,
        Rectangle rectangle, boolean resizable) {
        this.title = title;
        this.dialogName = ValidateUtils.isEmptyString(dialogName) ? Dialogs.UNINSTALL.dialogName() : dialogName;
        this.title = title;
        this.mainPanel = panel;

        // 无位置信息时居中显示
        this.rectangle = rectangle;
        // 设置弹框大小是否可变
        this.resizable = resizable;
        // 设置弹框中确认取消按钮的名称
        setOKAndCancelAndNextName(CommonI18NServer.toLocale("plugins_common_button_uninstall"),
                CommonI18NServer.toLocale("plugins_common_button_cancel"),
                CommonI18NServer.toLocale("plugins_common_button_connect"));
        // 设置帮助
        setHelp(CommonI18NServer.toLocale("plugins_ui_common_uninstall_help"), HELP_URL);
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title     弹窗标题
     * @param panel     需要展示的面板之一
     * @param rectangle 位置大小信息
     */
    public UninstallWrapDialog(String title, IDEBasePanel panel, Rectangle rectangle) {
        this(title, null, panel, rectangle, false);
    }

    /**
     * 不带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public UninstallWrapDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this(title, dialogName, panel, null, resizable);
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public UninstallWrapDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 设置连接检查按钮的有效性
     *
     * @param flag true有效，反之则然
     */
    public static void setCheckEnable(boolean flag) {
        if (checkButton == null) {
            return;
        }
        checkButton.setEnabled(flag);
    }

    /**
     * 设置动态加载图标的可见性
     *
     * @param flag true为可见，反之则然
     */
    public static void setGifVisible(boolean flag) {
        if (gifLabel == null) {
            return;
        }
        gifLabel.setVisible(flag);
    }

    /**
     * 初始化弹框
     */
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
        if (mainPanel instanceof UninstallPanel) {
            UninstallPanel uninstallPanel = (UninstallPanel) mainPanel;
            this.check = uninstallPanel.getCheckConnectionButton();
            this.loadGif = uninstallPanel.getLoadGif();
            this.getButton(nextAction).setVisible(false);
            this.check.requestFocus(true);
            this.check.setText(CommonI18NServer.toLocale("plugins_common_button_connect"));
            this.check.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    doCheckClickAction(uninstallPanel);
                }
            });
        }
        okAction.setEnabled(false);
    }

    private void doCheckClickAction(UninstallPanel uninstallPanel) {
        // 对所有输入进行校验
        boolean validate = validateAllParam(uninstallPanel);
        if (validate) {
            // 点击检测后将组件同步给缓存
            checkButton = this.check;
            gifLabel = this.loadGif;
            // 开始时设置loading动态图标
            if (UIUtil.isUnderDarcula()) {
                loadGif.setIcon(new ImageIcon(LeftTreeLoadingPanel.class.getResource(
                        IDEConstant.LOADING_DARCULA_GIF)));
            } else {
                loadGif.setIcon(new ImageIcon(LeftTreeLoadingPanel.class.getResource(
                        IDEConstant.LOADING_OTHER_GIF)));
            }
            loadGif.setVisible(true);
            check.setEnabled(false);
            IDEPanelBaseAction idePanelBaseAction = mainPanel.getAction();
            if (idePanelBaseAction instanceof SshAction) {
                SshAction action = (SshAction) idePanelBaseAction;
                action.onNextAction(uninstallPanel.getParams(), actionOperate);
            }
        }
    }

    /**
     * 参数校验
     *
     * @param uninstallPanel 安装面板
     * @return 校验结果
     */
    private boolean validateAllParam(UninstallPanel uninstallPanel) {
        List<ValidationInfo> infoList = uninstallPanel.doValidateAll();
        if (ValidateUtils.isNotEmptyCollection(infoList)) {
            ValidationInfo info = infoList.get(0);
            if (info.component != null && info.component.isVisible()) {
                IdeFocusManager.getInstance(null).requestFocus(info.component, true);
            }
            if (!isInplaceValidationToolTipEnabled()) {
                DialogEarthquakeShaker.shake(getPeer().getWindow());
            }
            startTrackingValidation();
            if (infoList.stream().anyMatch(info1 -> !info1.okEnabled)) {
                return false;
            }
            if (nextVerify()) {
                close(NEXT_USER_EXIT_CODE);
                return true;
            }
        }
        after();
        return true;
    }

    /**
     * 点击next前的校验事件
     *
     * @return boolean
     */
    protected abstract boolean nextVerify();

    /**
     * 点击next事件
     */
    @Override
    protected void onNextAction() {
    }


    @Override
    protected void onCancelAction() {
        mainPanel.clearPwd();
    }

    /**
     * 创建主内容面板
     *
     * @return IDEBasePanel
     */
    @Nullable
    @Override
    protected IDEBasePanel createCenterPanel() {
        return mainPanel;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    @Override
    @NotNull
    protected List<ValidationInfo> doValidateAll() {
        return this.mainPanel.doValidateAll();
    }

    @Override
    public void actionOperate(Object data) {
        if (data instanceof Boolean) {
            okAction.setEnabled((Boolean) data);
        }
    }
}
