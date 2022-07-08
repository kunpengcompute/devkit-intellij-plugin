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
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.DeployUtil;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_ICON;

/**
 * 安装账户判断提示的弹窗
 *
 * @since 2020-12-21
 */
public class AccountTipsDialog extends IdeaDialog {
    /**
     * root用户链接
     */
    private static final String ROOT_FAQ = CommonI18NServer.toLocale("plugins_ui_common_root_faq_url");

    /**
     * 普通用户链接
     */
    private static final String OTHER_FAQ = CommonI18NServer.toLocale("plugins_ui_common_other_faq_url");

    private Map<String, String> params;

    private ActionOperate actionOperate;

    private String userRole;

    private boolean isRoot;


    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public AccountTipsDialog(String title, String dialogName, IDEBasePanel panel,
        Map<String, String> params, ActionOperate actionOperate) {
        this.title = title;
        this.dialogName = dialogName;
        this.mainPanel = panel;
        this.params = params;
        this.actionOperate = actionOperate;
        this.userRole = params.get("user");
        if (Objects.equals(userRole, "root")) {
            isRoot = true;
        }
        // 初始化弹框内容
        initDialog();
    }


    /**
     * 安装部署内容声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setMaximumSize(new Dimension(570, 71));
        JLabel test = new JLabel("For Tips");
        test.setEnabled(false);
        String messages = isRoot ? CommonI18NServer.toLocale("plugins_ui_common_using_account_tips")
                : CommonI18NServer.toLocale("plugins_ui_common_using_common_tips");
        mainPanel.add(panelWithHtmlListener(messages, test),
                BorderLayout.CENTER);
        return mainPanel;
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start AccountTipsDialog.");
        SshConfig config = DeployUtil.getConfig(params);
        DeployUtil.gotoTestConn(actionOperate, config, this::displayServerAbnormalPanel);
    }

    /**
     * 显示网络不通异常引导面板
     *
     * @param ip ip
     */
    public void displayServerAbnormalPanel(String ip) {
        StringBuffer detail = new StringBuffer();
        detail.append(CommonI18NServer.toLocale("plugins_common_message_responseError_messagePrefix_deployScenario"))
                .append(CommonI18NServer.toLocale("plugins_common_message_responseError_viewDetail"))
                .append(CommonI18NServer.toLocale("plugins_common_message_responseError_messageSuffix"));
        IDENotificationUtil.notificationCommon(
                new NotificationBean("", detail.toString(), NotificationType.ERROR));
    }

    /**
     * onCancelAction 取消安装
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel AccountTipsDialog.");
        // 取消时恢复检测按钮功能
        DeployUtil.recoveryCheckAndLoad();
    }

    @Override
    protected boolean isNeedInvokeLaterPanel() {
        return false;
    }

    /**
     * 安装部署外部超链接
     *
     * @param msg          链接内容
     * @param installTitle installTitle
     * @return jPanel 主面板
     */
    private JPanel panelWithHtmlListener(String msg, JLabel installTitle) {
        Color color = installTitle.getForeground();
        Font font = installTitle.getFont();
        String messages = MessageFormat.format(msg, color.getRed(), color.getGreen(), color.getBlue(),
                font.getFontName(), font.getSize());
        if (!isRoot) {
            messages = MessageFormat.format(msg, color.getRed(), color.getGreen(), color.getBlue(),
                    font.getFontName(), font.getSize(), this.userRole);
        }
        JEditorPane jEditorPane = new JEditorPane("text/html", messages);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);
        HyperlinkListener listener = new HyperlinkListener() {
            /**
             * 超链接实现
             *
             * @param hyperLink 超链接
             */
            public void hyperlinkUpdate(HyperlinkEvent hyperLink) {
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(hyperLink.getEventType())) {
                    return;
                }

                try {
                    String url = isRoot ? ROOT_FAQ : OTHER_FAQ;
                    URI uri = new URI(url);
                    Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException | IOException e) {
                    Logger.error("Link Error or The Internet is break.");
                }
            }
        };
        jEditorPane.addHyperlinkListener(listener);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JLabel icon = new JLabel();
        if (!isRoot) {
            String titleTips = MessageFormat.format(CommonI18NServer.toLocale(
                    "plugins_ui_common_using_common_title_tips"), this.userRole);
            icon.setText(titleTips);
        } else {
            icon.setText(CommonI18NServer.toLocale(
                    "plugins_ui_common_using_account_title_tips"));
        }
        jPanel.add(icon, BorderLayout.WEST);
        jPanel.add(jEditorPane, BorderLayout.SOUTH);
        icon.setIcon(ICON_INFO_ICON);
        icon.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        icon.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        return jPanel;
    }
}
