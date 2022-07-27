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

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_ICON;
import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.VOC_QR_ICON;
import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_OPERATE_CLOSE;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.MessageFormat;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;

/**
 * 意见反馈无网络弹框。
 *
 * @since 2.3.T20
 */
public class NoNetworkForFeedbackDialog extends IdeaDialog {
    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public NoNetworkForFeedbackDialog(String title, IDEBasePanel panel) {
        this.title = title;
        this.dialogName = "NoNetworkForFeedbackDialog";
        this.mainPanel = panel;

        // 初始化弹框内容
        initDialog();
        setCancelButtonText(TERM_OPERATE_CLOSE);
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
        jEditorPane.addHyperlinkListener(hyperlinkEvent -> {
            if (!HyperlinkEvent.EventType.ACTIVATED.equals(hyperlinkEvent.getEventType())) {
                return;
            }
            CommonUtil.openURI(hyperlinkEvent.getDescription());
        });
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
        JLabel installTitle = new JLabel(CommonI18NServer.toLocale("plugins_common_message_voc_tips"));
        installTitle.setIcon(ICON_INFO_ICON);
        // 主面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(600, 278));
        centerPanel.add(installTitle, BorderLayout.NORTH);
        centerPanel.add(panelWithHtmlListener(CommonI18NServer.toLocale("plugins_common_message_voc"),
                installTitle), BorderLayout.CENTER);
        JLabel vocIoc = new JLabel();
        vocIoc.setIcon(VOC_QR_ICON);
        centerPanel.add(vocIoc, BorderLayout.SOUTH);
        return centerPanel;
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onOKAction() {
    }

    /**
     * onCancelAction 取消安装
     */
    @Override
    protected void onCancelAction() {
        Logger.info("close Deploy.");
    }

    /**
     * 重写按钮显示
     *
     * @return 返回。
     */
    @Override
    protected Action[] createActions() {
        return new Action[] {getCancelAction()};
    }
}
