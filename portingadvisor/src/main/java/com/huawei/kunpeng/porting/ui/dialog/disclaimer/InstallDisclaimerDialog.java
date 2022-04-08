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

package com.huawei.kunpeng.porting.ui.dialog.disclaimer;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.IDEComponentManager;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.InstallUpgradePanel;
import com.huawei.kunpeng.porting.action.install.InstallAction;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.common.constant.PortingWeakPwdConstant;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingInstallUpgradeWrapDialog;

import com.intellij.openapi.util.IconLoader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * 安装部署前的免责声明
 *
 * @since 2012-10-12
 */
public class InstallDisclaimerDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public InstallDisclaimerDialog(String title, String dialogName, IDEBasePanel panel, Rectangle rectangle,
        Dimension dimension, boolean resizable) {
        this.title = ValidateUtils.isEmptyString(title) ? PortingWeakPwdConstant.BEFORE_INSTALL : title;
        this.dialogName = ValidateUtils.isEmptyString(dialogName)
            ? Dialogs.INSTALL_DISCLAIMER.dialogName()
            : dialogName;
        this.mainPanel = panel;

        // 无位置信息时居中显示
        this.rectangle = rectangle;
        setDoNotAskOption(new DoNotAskOption.Adapter() {
            @Override
            public void rememberChoice(boolean b, int i) {
            }

            @Override
            public boolean shouldSaveOptionsOnCancel() {
                return true;
            }

            @Override
            public @NotNull String getDoNotShowMessage() {
                return PortingWeakPwdConstant.READ_DEPLOY;
            }
        });
        // 初始化弹框内容
        initDialog();
        okAction.setEnabled(false);
        addChangeListener();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public InstallDisclaimerDialog(String title, IDEBasePanel panel, Rectangle rectangle) {
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
    public InstallDisclaimerDialog(String title, String dialogName, IDEBasePanel panel, Dimension dimension,
        boolean resizable) {
        this(title, dialogName, panel, null, dimension, resizable);
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public InstallDisclaimerDialog(String title, IDEBasePanel panel, Dimension dimension) {
        this(title, null, panel, null, dimension, false);
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public InstallDisclaimerDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, null, false);
    }

    /**
     * 安装部署外部超链接
     *
     * @param msg          链接内容
     * @param installTitle installTitle
     * @return jPanel 主面板
     */
    private static JPanel panelWithHtmlListener(String msg, JLabel installTitle) {
        Color color = installTitle.getForeground();
        Font font = installTitle.getFont();
        String messages = MessageFormat.format(msg,
            color.getRed(), color.getGreen(), color.getBlue(), font.getFontName(), font.getSize());
        JEditorPane jEditorPane = new JEditorPane("text/html", messages);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);

        HyperlinkListener listener = new HyperlinkListener() {
            /**
             * 超链接实现
             *
             * @param hyperLink 超链接
             */
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperLink) {
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(hyperLink.getEventType())) {
                    return;
                }

                try {
                    URI uri = hyperLink.getURL().toURI();
                    Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException | IOException e) {
                    Logger.error("Link Error or The Internet is break.");
                }
            }
        };
        jEditorPane.addHyperlinkListener(listener);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout(5, 5));
        jPanel.add(jEditorPane, BorderLayout.CENTER);

        return jPanel;
    }

    /**
     * 安装部署内容声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JLabel installTitle = new JLabel(PortingWeakPwdConstant.FIRST_CONFIG_TITLE);
        installTitle.setIcon(IconLoader.findIcon(IDEConstant.ICON_INFO));

        // 主面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(600, 278));
        centerPanel.add(installTitle, BorderLayout.NORTH);
        centerPanel.add(panelWithHtmlListener(PortingWeakPwdConstant.DEPLOY_CONTENT, installTitle),
            BorderLayout.SOUTH);
        return centerPanel;
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start Deploy.");
        // 关闭配置远程服务器窗口
        IDEBaseDialog configDialog = IDEComponentManager.getViableDialog(Dialogs.SERVER_CONFIG.dialogName());
        if (configDialog != null) {
            configDialog.dispose();
        }
        // 打开安装服务器窗口
        InstallAction installAction = new InstallAction();
        InstallUpgradePanel panel = new InstallUpgradePanel(null, PortingUserManageConstant.INSTALL_TITLE, false,
            installAction);
        IDEBaseDialog dialog = new PortingInstallUpgradeWrapDialog(PortingUserManageConstant.INSTALL_TITLE, panel);
        dialog.displayPanel();
    }

    /**
     * onCancelAction 取消安装
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel Deploy.");
    }

    /**
     * 勾选监听
     */
    public void addChangeListener() {
        JComponent jCheckBox = createDoNotAskCheckbox();
        if (!(jCheckBox instanceof JCheckBox)) {
            return;
        }
        ((JCheckBox) jCheckBox).addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Object checkBoxObj = changeEvent.getSource();
                if (checkBoxObj instanceof JCheckBox) {
                    doEvent((JCheckBox) checkBoxObj);
                }
            }

            private void doEvent(JCheckBox checkBoxObj) {
                JCheckBox checkBox = checkBoxObj;
                if (checkBox.isSelected()) {
                    getOKAction().setEnabled(true);
                } else {
                    getOKAction().setEnabled(false);
                }
            }
        });
    }
}
