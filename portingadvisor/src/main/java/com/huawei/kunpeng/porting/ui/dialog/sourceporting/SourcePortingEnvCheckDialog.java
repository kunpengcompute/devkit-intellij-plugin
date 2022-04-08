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

package com.huawei.kunpeng.porting.ui.dialog.sourceporting;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;

/**
 * The class SourcePortingEnvCheckDialog: 源码迁移时，查询运行环境
 *
 * @since v1.0
 */
public class SourcePortingEnvCheckDialog extends IdeaDialog {
    private JCheckBox isDoNotPromptCheckBox;
    private String messageTitle;
    private String messageBody;
    private boolean needBody;
    private Color color;
    private Font font;
    private boolean isSigned;

    /**
     * 初始化源码迁移环境检查Dialog
     *
     * @param title        title
     * @param dialogName   dialogName
     * @param messageTitle 主面板
     * @param needBody     panel主要信息
     */
    public SourcePortingEnvCheckDialog(String title, String dialogName, String messageTitle, boolean needBody) {
        this.title = title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ?
            Dialogs.SOURCE_PORTING_ENV_CHECK.dialogName() : dialogName;
        ;
        this.needBody = needBody;
        this.messageTitle = messageTitle;
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 创建环境检查Dialog主Panel
     *
     * @return JComponent
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setMaximumSize(new Dimension(600, 200));
        centerPanel.add(this.createCheckBox(), BorderLayout.SOUTH);

        JLabel titleLabel = new JLabel();
        if (messageTitle == null) {
            messageTitle = I18NServer.toLocale("plugins_porting_env_check_title");
            messageTitle = MessageFormat.format(messageTitle, color.getRed(), color.getGreen(), color.getBlue());
        }
        titleLabel.setText(messageTitle);
        titleLabel.setMinimumSize(new Dimension(600, 50));
        titleLabel.setIcon(new ImageIcon(SourcePortingEnvCheckDialog.class.getResource("/assets/img/settings" +
            "/disclaimer_dialog.png")));
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        titleLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        centerPanel.add(titleLabel, BorderLayout.NORTH);
        font = titleLabel.getFont();
        if (needBody) {
            centerPanel.add(obtainMessageBodyPanel(color), BorderLayout.CENTER);
        }

        return centerPanel;
    }

    /**
     * 获取对应的提示消息体
     *
     * @param color ide整体配色
     * @return JPanel
     */
    private JPanel obtainMessageBodyPanel(Color color) {
        messageBody = I18NServer.toLocale("plugins_porting_env_check_body");
        boolean isX86Platform = SourcePortingHandler.confirmIsX86PlatForm();
        String libstdcLink = isX86Platform
            ? I18NServer.toLocale("plugins_porting_x86_libstdc")
            : I18NServer.toLocale("plugins_porting_arm_libstdc");
        String glibcLink = isX86Platform
            ? I18NServer.toLocale("plugins_porting_x86_glibc")
            : I18NServer.toLocale("plugins_porting_arm_glibc");
        SourcePortingHandler.getCustomInstallPath();
        messageBody = MessageFormat.format(
            messageBody, PortingUserInfoContext.getInstance().getCustomPath(), libstdcLink,
            glibcLink, color.getRed(), color.getGreen(), color.getBlue(), font.getFontName(), font.getSize());
        JEditorPane jEditorPane = new JEditorPane("text/html", messageBody);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);
        jEditorPane.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                URL linkUrl = event.getURL();
                try {
                    Desktop.getDesktop().browse(linkUrl.toURI());
                } catch (IOException | URISyntaxException ex) {
                    Logger.error("Download rmp package failed, {}", ex);
                }
            }
        });
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout(5, 5));
        jPanel.add(jEditorPane, BorderLayout.CENTER);
        jPanel.setMaximumSize(new Dimension(579, 150));
        return jPanel;
    }

    @Override
    protected void onOKAction() {
        if (isSigned) {
            String ip = IDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
                BaseCacheVal.IP.vaLue());
            PortingCommonUtil.write2ConfigEnvPrompt(ip + "#" + PortingUserInfoContext.getInstance().getUserName());
        }
    }

    @Override
    protected void onCancelAction() {
    }

    /**
     * 覆盖默认的ok/cancel按钮
     *
     * @return Action[]
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[] {getOKAction()};
    }

    /**
     * 环境检查是否不再提醒checkbox
     *
     * @return checkBoxPanel
     */
    public JPanel createCheckBox() {
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BorderLayout());
        isDoNotPromptCheckBox = new JCheckBox();
        isDoNotPromptCheckBox.setText(I18NServer.toLocale("plugins_porting_env_check_do_not_again"));
        color = isDoNotPromptCheckBox.getForeground();
        checkBoxPanel.add(isDoNotPromptCheckBox, BorderLayout.WEST);
        addChangeListener();
        return checkBoxPanel;
    }

    /**
     * 添加监听器。
     */
    private void addChangeListener() {
        isDoNotPromptCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Object checkBoxObj = changeEvent.getSource();
                // 获取事件源（即复选框本身）
                if (checkBoxObj instanceof JCheckBox) {
                    doEvent((JCheckBox) checkBoxObj);
                }
            }

            private void doEvent(JCheckBox checkBoxObj) {
                JCheckBox checkBox = checkBoxObj;
                if (checkBox.isSelected()) {
                    isSigned = true;
                } else {
                    isSigned = false;
                }
            }
        });
    }
}
