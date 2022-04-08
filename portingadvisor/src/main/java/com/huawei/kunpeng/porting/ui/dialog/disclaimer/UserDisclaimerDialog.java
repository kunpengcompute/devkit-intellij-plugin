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

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.PORTING_USER_DISCLAIMER;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_OPERATE_CLOSE;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_CONFIRMED;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST1;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_TITLE;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.dialog.DisclaimerCommonDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;
import com.huawei.kunpeng.porting.common.utils.IntellijAllIcons;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * 免责声明面板（弹框）
 *
 * @since 2020-10-19
 */
public class UserDisclaimerDialog extends DisclaimerCommonDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param isDoEvent  isDoEvent
     */
    public UserDisclaimerDialog(String title, String dialogName, IDEBasePanel panel, boolean isDoEvent) {
        this.title = ValidateUtils.isEmptyString(title) ? USER_DISCLAIMER_TITLE : title;
        this.dialogName = ValidateUtils.isEmptyString(dialogName) ? USER_DISCLAIMER_TITLE : dialogName;
        this.mainPanel = panel;
        // 无位置信息时居中显示
        this.rectangle = new Rectangle(600, 360);
        this.isDoEvent = isDoEvent;
        if (this.isDoEvent) {
            setDoNotAskOption(new DoNotAskOption.Adapter() {
                @Override
                public void rememberChoice(boolean b, int i) {
                }

                @Override
                public boolean shouldSaveOptionsOnCancel() {
                    return true;
                }

                @Override
                public @NotNull @NlsContexts.Checkbox String getDoNotShowMessage() {
                    return CommonI18NServer.toLocale("common_message_beforeInstallOption");
                }
            });
            setCancelButtonText(I18NServer.toLocale("plugins_common_term_operate_cancel"));
            setOKButtonText(I18NServer.toLocale("plugins_common_term_operate_ok"));
        } else {
            setCancelButtonText(TERM_OPERATE_CLOSE);
        }

        // 初始化弹框内容
        initDialog();
        getOKAction().setEnabled(false);
        addChangeListener();
    }

    /**
     * 安装部署内容声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // 内容声明
        JLabel titleLabel = new JLabel();
        if (!isDoEvent) {
            titleLabel.setText(USER_DISCLAIMER_CONFIRMED);
        } else {
            titleLabel.setText(USER_DISCLAIMER_LIST1);
            titleLabel.setIcon(IntellijAllIcons.ReportOperation.ICON_INFO);
            titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
            titleLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        }

        titleLabel.setMinimumSize(new Dimension(580, 60));
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(titleLabel);
        northPanel.setMinimumSize(new Dimension(600, 60));
        northPanel.add(new JPanel(), BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(northPanel, BorderLayout.NORTH); // 添加北边页面。
        centerPanel.setPreferredSize(new Dimension(600, 360));
        Color color = titleLabel.getForeground();
        Font font = titleLabel.getFont();
        String messages = MessageFormat.format(PORTING_USER_DISCLAIMER,
            color.getRed(), color.getGreen(), color.getBlue(), font.getFontName(), font.getSize());

        JBScrollPane jbScrollPane = new JBScrollPane(panelWithHtmlListener(messages));
        jbScrollPane.setMaximumSize(new Dimension(579, 236));

        jbScrollPane.setBorder(null); // 设置无边框。
        // 滚动条总是垂直的。
        jbScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jbScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(jbScrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    /**
     * 安装部署外部超链接
     *
     * @param msg 链接内容
     * @return jPanel 主面板
     */
    private static JPanel panelWithHtmlListener(String msg) {
        JEditorPane jEditorPane = new JEditorPane("text/html", msg);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);
        jEditorPane.setCaretPosition(0);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout(5, 5));
        jPanel.add(jEditorPane, BorderLayout.CENTER);
        return jPanel;
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start User Disclaimer.");
        if (isDoEvent) {
            // 修改免责声明全局状态，
            UserManagerAction.setSignDisclaimer();
        }
    }

    /**
     * 返回免责证明结果
     *
     * @return 返回签署结果
     */
    @Override
    protected boolean okVerify() {
        Logger.info("Cancel User Disclaimer.");
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>(BUTTON_LENGTH);
        button.add(IDEMessageDialogUtil.ButtonName.THINK_AGAIN);
        button.add(IDEMessageDialogUtil.ButtonName.LOG_OUT);
        String select = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(I18NServer.toLocale("plugins_common_porting_user_disclaimer_tips"),
                I18NServer.toLocale("plugins_common_porting_user_disclaimer_tips_title"),
                button,
                0,
                IDEMessageDialogUtil.getWarn()
            ));
        if (select.equals(IDEMessageDialogUtil.ButtonName.THINK_AGAIN.getKey())
            || select.equals(IDEMessageDialogUtil.ButtonName.CANCEL.getKey())) {
            return false;
        } else {
            ApplicationManager.getApplication().invokeLater(() -> {
                // 登出
                LoginUtils.logout();
            });
            return true;
        }
    }
}
