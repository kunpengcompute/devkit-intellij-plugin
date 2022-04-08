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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.wm.ToolWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Porting 异常场景显示面板
 *
 * @since 2020-10-09
 */
public class ErrorPanel extends IDEBasePanel {
    private JPanel mainPanel;

    private JLabel errorLabel;

    private JLabel unAccessibleLabel;

    private JPanel unAccessiblePanel;

    private JLabel stepOneDesc;

    private JLabel stepTwoDesc;

    private JLabel stepThrDesc;

    private JLabel stepYunDesc;

    private JLabel stepFouDesc;

    private JLabel stepFivDesc;

    private JPanel stepTwoPanel;

    private JPanel stepOtherPanel;

    private JPanel topPanel;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public ErrorPanel(
            ToolWindow toolWindow,
            String panelName,
            String displayName,
            String errorMsg,
            boolean isAccessible,
            boolean isLockable,
            String hostOfUninstall) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.ERROR_GUIDE.panelName() : panelName;

        // 初始化面板
        initPanel(isAccessible, errorMsg, hostOfUninstall);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(
                mainPanel,
                StringUtil.stringIsEmpty(displayName) ? Panels.ERROR_GUIDE.panelName() : displayName,
                isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示名称
     * @param isLock  isLock
     */
    public ErrorPanel(ToolWindow toolWindow, String displayName, String errorMsg, boolean isLock) {
        this(toolWindow, null, displayName, errorMsg, true, isLock, null);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示名称
     * @param isLockable  isLockable
     */
    public ErrorPanel(ToolWindow toolWindow, String displayName, boolean isAccessible, boolean isLockable) {
        this(toolWindow, null, displayName, null, isAccessible, isLockable, null);
    }

    /**
     * 带toolWin和displayName的构造参数
     *
     * @param toolWin      toolWin
     * @param displayName     面板显示名称
     * @param hostOfUninstall 卸载的主机IP
     */
    public ErrorPanel(ToolWindow toolWin, String displayName, String hostOfUninstall) {
        this(toolWin, null, displayName, null, false, true, hostOfUninstall);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow toolWindow
     * @param toolWindow errorMsg
     * @param isLock isLock
     */
    public ErrorPanel(ToolWindow toolWindow, String errorMsg, boolean isLock) {
        this(toolWindow, null, null, errorMsg, true, isLock, null);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public ErrorPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, null, true, false, null);
    }

    /**
     * 初始化主面板
     *
     * @param isAccessible    是否IP端口配置正常
     * @param errorMsg        提示信息
     * @param hostOfUninstall 卸载的主机IP
     */
    protected void initPanel(boolean isAccessible, String errorMsg, String hostOfUninstall) {
        int width = 880;
        if (TuningI18NServer.getCurrentLanguage().equals(Language.EN)) {
            width = 960;
        }
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setMaximumSize(new Dimension(width, 500));
        if (isAccessible) {
            Logger.error("An exception occurs in the application and the interface fails to be invoked.");
            errorLabel = new JLabel();
            errorLabel.setText(errorMsg);
            mainPanel.add(errorLabel);
        } else {
            Logger.error("No response from the server is received. Check for exceptions.");
            setUnAccessiblePanel(width, hostOfUninstall);
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(unAccessiblePanel);
        }

        super.initPanel(mainPanel);
    }

    /**
     * 设置未响应面板
     *
     * @param width           面板宽度
     * @param hostOfUninstall 标识是否来源卸载
     */
    private void setUnAccessiblePanel(int width, String hostOfUninstall) {
        Map<String, Object> context = null;
        Object contextObj = IDEContext.getValueFromGlobalContext(null, TuningIDEConstant.TOOL_NAME_TUNING);
        if (contextObj instanceof Map) {
            context = (Map<String, Object>) contextObj;
        }
        List<Object> params = new ArrayList<Object>();
        params.add(context == null ? null : context.get(BaseCacheVal.IP.vaLue()));
        params.add(context == null ? null : context.get(BaseCacheVal.PORT.vaLue()));
        unAccessiblePanel.setPreferredSize(
                new Dimension(width, ValidateUtils.isEmptyString(hostOfUninstall) ? 380 : 290));
        unAccessiblePanel.setLayout(null);
        unAccessibleLabel.setText(TuningI18NServer.toLocale("plugins_porting_title_serverException"));
        unAccessibleLabel.setBounds(50, 20, width, 26);
        if (ValidateUtils.isNotEmptyString(hostOfUninstall)) {
            stepOneDesc.setText(
                    MessageFormat.format(
                            TuningI18NServer.toLocale("plugins_porting_message_networkErrorTip_deployScenario"),
                            hostOfUninstall));
        } else {
            String netErrorTip =
                    String.format(
                            Locale.ROOT,
                            TuningI18NServer.toLocale("plugins_porting_message_networkErrorTip"),
                            params.toArray());
            stepOneDesc.setText(
                    TuningI18NServer.getCurrentLanguage().equals(Language.EN)
                            ? netErrorTip
                            : netErrorTip.replaceAll(" ", ""));
        }
        stepOneDesc.setBounds(50, 10, width, 26);
        stepTwoDesc.setText(TuningI18NServer.toLocale("plugins_porting_message_networkErrorResult1"));
        stepTwoDesc.setBounds(50, 30, width, 26);
        stepThrDesc.setText(TuningI18NServer.toLocale("plugins_porting_message_networkErrorResult2"));
        stepThrDesc.setBounds(50, 50, width, 26);
        setTopPanel(width);
        if (ValidateUtils.isEmptyString(hostOfUninstall) && context != null) {
            setUnAccessiblePanel(width, context);
        } else {
            setUnAccessiblePanelOfUninstall(width);
        }
    }

    /**
     * 设置卸载未响应面板内容
     *
     * @param width 面板宽度
     */
    private void setUnAccessiblePanelOfUninstall(int width) {
        stepYunDesc.setText(
                MessageFormat.format(
                        TuningI18NServer.toLocale("plugins_porting_message_networkErrorYunTip"),
                        TuningI18NServer.toLocale("plugins_porting_message_networkErrorYunTip_deployScenario"),
                        TuningI18NServer.toLocale(
                                "plugins_porting_message_networkErrorYunTip_deployScenario_connIssue")));
        stepYunDesc.setBounds(50, 76, width, 180);
        stepOtherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepOtherPanel.add(
                new JLabel(TuningI18NServer.toLocale("plugins_porting_message_CommunityTip_deployScenario")));
        JLabel communityTipLink = new JLabel();
        setStyleAndMouseListener(
                communityTipLink, TuningI18NServer.toLocale("plugins_porting_message_CommunityTipLink"));
        stepOtherPanel.add(communityTipLink);
        stepOtherPanel.add(new JLabel(TuningI18NServer.toLocale("plugins_porting_message_CommunityTipEnd")));
        stepOtherPanel.setBounds(45, 256, width, 26);
        fillUnAccessiblePanel(Boolean.FALSE);
    }

    /**
     * 设置未响应面板内容
     *
     * @param width   面板宽度
     * @param context 全局上下文
     */
    private void setUnAccessiblePanel(int width, Map<String, Object> context) {
        String normalScenario =
                MessageFormat.format(
                        TuningI18NServer.toLocale(
                                "plugins_porting_message_networkErrorYunTip_normalScenario_connIssue"),
                        context.get(BaseCacheVal.PORT.vaLue()));
        stepYunDesc.setText(
                MessageFormat.format(
                        TuningI18NServer.toLocale("plugins_porting_message_networkErrorYunTip"),
                        TuningI18NServer.toLocale("plugins_porting_message_networkErrorYunTip_normalScenario"),
                        normalScenario));
        stepYunDesc.setBounds(50, 76, width, 180);
        stepFouDesc.setText(TuningI18NServer.toLocale("plugins_porting_message_serverErrorTip"));
        stepFouDesc.setBounds(50, 255, width, 26);
        stepFivDesc.setText(TuningI18NServer.toLocale("plugins_porting_message_serverErrorResult1"));
        stepFivDesc.setBounds(50, 275, width, 26);
        stepTwoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepTwoPanel.add(new JLabel(TuningI18NServer.toLocale("plugins_porting_message_serverErrorResult2Reference")));
        JLabel serverErrorResultLink = new JLabel();
        setStyleAndMouseListener(
                serverErrorResultLink, TuningI18NServer.toLocale("plugins_porting_message_serverErrorResult2Link"));
        stepTwoPanel.add(serverErrorResultLink);
        stepTwoPanel.add(new JLabel(TuningI18NServer.toLocale("plugins_porting_message_serverErrorResult2Deal")));
        stepTwoPanel.setBounds(45, 295, width, 26);
        stepOtherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepOtherPanel.add(new JLabel(TuningI18NServer.toLocale("plugins_porting_message_CommunityTipStart")));
        JLabel communityTipLink = new JLabel();
        setStyleAndMouseListener(
                communityTipLink, TuningI18NServer.toLocale("plugins_porting_message_CommunityTipLink"));
        stepOtherPanel.add(communityTipLink);
        stepOtherPanel.add(new JLabel(TuningI18NServer.toLocale("plugins_porting_message_CommunityTipEnd")));
        stepOtherPanel.setBounds(45, 330, width, 26);
        fillUnAccessiblePanel(Boolean.TRUE);
    }

    /**
     * 填充未响应面板
     *
     * @param ifCommonScenario 是否普通场景
     */
    private void fillUnAccessiblePanel(boolean ifCommonScenario) {
        if (ifCommonScenario) {
            unAccessiblePanel.add(stepFouDesc);
            unAccessiblePanel.add(stepFivDesc);
            unAccessiblePanel.add(stepTwoPanel);
        }
        unAccessiblePanel.add(stepOneDesc);
        unAccessiblePanel.add(stepTwoDesc);
        unAccessiblePanel.add(stepThrDesc);
        unAccessiblePanel.add(stepYunDesc);
        unAccessiblePanel.add(stepOtherPanel);
    }

    /**
     * 目标label链接添加监听事件
     *
     * @param tipLinkLabel 目标链接Label
     * @param origin       原始i18n字符串
     */
    private void setStyleAndMouseListener(JLabel tipLinkLabel, String origin) {
        if (tipLinkLabel != null && ValidateUtils.isNotEmptyString(origin)) {
            tipLinkLabel.setText(origin);
            tipLinkLabel.setForeground(new Color(47, 101, 202, 1));
            tipLinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tipLinkLabel.addMouseListener(new LinkMouseAdapter(origin));
        }
    }

    /**
     * 顶部面板添加图标
     *
     * @param width 面板宽度
     */
    private void setTopPanel(int width) {
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBounds(0, 20, width, 20);
        topPanel.setMaximumSize(new Dimension(width, 30));
        JLabel iconLabel =
                new JLabel(new ImageIcon(ErrorPanel.class.getResource("/assets/img/error-instruction/error_icon.png")));
        iconLabel.setBounds(0, 20, 20, 20);
        topPanel.add(iconLabel);
        topPanel.add(unAccessibleLabel);
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    @Override
    protected void registerComponentAction() {
    }

    /**
     * Label鼠标事件内部类
     */
    private class LinkMouseAdapter extends MouseAdapter {
        private String origin;

        LinkMouseAdapter(String origin) {
            this.origin = origin;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                if (ValidateUtils.isNotEmptyString(origin)) {
                    URI uri =
                            new URI(
                                    origin.substring(
                                            origin.indexOf(IDEConstant.URL_PREFIX), origin.lastIndexOf("\">")));
                    Desktop.getDesktop().browse(uri);
                }
            } catch (IOException ioexp) {
                Logger.error("An IOException occurs in the program. message is IOException");
            } catch (URISyntaxException uriexp) {
                Logger.error("An URISyntaxException occurs in the program. message is URISyntaxException");
            }

            Logger.info("Listening events are added to the panel. element is origin");
        }
    }
}
