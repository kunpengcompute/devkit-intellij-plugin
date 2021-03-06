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
 * ??????????????????????????????
 *
 * @since 2012-10-12
 */
public class InstallDisclaimerDialog extends IdeaDialog {
    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param title      ????????????
     * @param dialogName ????????????
     * @param panel      ???????????????????????????
     * @param resizable  ??????????????????
     */
    public InstallDisclaimerDialog(String title, String dialogName, IDEBasePanel panel, Rectangle rectangle,
        Dimension dimension, boolean resizable) {
        this.title = ValidateUtils.isEmptyString(title) ? PortingWeakPwdConstant.BEFORE_INSTALL : title;
        this.dialogName = ValidateUtils.isEmptyString(dialogName)
            ? Dialogs.INSTALL_DISCLAIMER.dialogName()
            : dialogName;
        this.mainPanel = panel;

        // ??????????????????????????????
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
        // ?????????????????????
        initDialog();
        okAction.setEnabled(false);
        addChangeListener();
    }

    /**
     * ??????????????????????????????
     *
     * @param title ????????????
     * @param panel ???????????????????????????
     */
    public InstallDisclaimerDialog(String title, IDEBasePanel panel, Rectangle rectangle) {
        this(title, null, panel, rectangle, null, false);
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param title      ????????????
     * @param dialogName ????????????
     * @param panel      ???????????????????????????
     * @param resizable  ??????????????????
     */
    public InstallDisclaimerDialog(String title, String dialogName, IDEBasePanel panel, Dimension dimension,
        boolean resizable) {
        this(title, dialogName, panel, null, dimension, resizable);
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param title ????????????
     * @param panel ???????????????????????????
     */
    public InstallDisclaimerDialog(String title, IDEBasePanel panel, Dimension dimension) {
        this(title, null, panel, null, dimension, false);
    }

    /**
     * ??????????????????????????????
     *
     * @param title ????????????
     * @param panel ???????????????????????????
     */
    public InstallDisclaimerDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, null, false);
    }

    /**
     * ???????????????????????????
     *
     * @param msg          ????????????
     * @param installTitle installTitle
     * @return jPanel ?????????
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
             * ???????????????
             *
             * @param hyperLink ?????????
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
     * ????????????????????????
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JLabel installTitle = new JLabel(PortingWeakPwdConstant.FIRST_CONFIG_TITLE);
        installTitle.setIcon(IconLoader.findIcon(IDEConstant.ICON_INFO));

        // ?????????
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(600, 278));
        centerPanel.add(installTitle, BorderLayout.NORTH);
        centerPanel.add(panelWithHtmlListener(PortingWeakPwdConstant.DEPLOY_CONTENT, installTitle),
            BorderLayout.SOUTH);
        return centerPanel;
    }

    /**
     * ?????????Install??????
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start Deploy.");
        // ?????????????????????????????????
        IDEBaseDialog configDialog = IDEComponentManager.getViableDialog(Dialogs.SERVER_CONFIG.dialogName());
        if (configDialog != null) {
            configDialog.dispose();
        }
        // ???????????????????????????
        InstallAction installAction = new InstallAction();
        InstallUpgradePanel panel = new InstallUpgradePanel(null, PortingUserManageConstant.INSTALL_TITLE, false,
            installAction);
        IDEBaseDialog dialog = new PortingInstallUpgradeWrapDialog(PortingUserManageConstant.INSTALL_TITLE, panel);
        dialog.displayPanel();
    }

    /**
     * onCancelAction ????????????
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel Deploy.");
    }

    /**
     * ????????????
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
