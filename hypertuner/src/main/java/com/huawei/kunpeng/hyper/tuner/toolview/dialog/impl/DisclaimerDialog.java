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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl;

import com.alibaba.fastjson.JSONObject;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.constant.InstallConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.dialog.DisclaimerCommonDialog;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.IDEComponentManager;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_OPERATE_CLOSE;

/**
 * 登录后的免责声明
 *
 * @since 2021-5-31
 */
public class DisclaimerDialog extends DisclaimerCommonDialog {
    /**
     * 带位置信息的构造函数
     *
     * @param title      弹窗标题
     * @param dialogName 弹窗名称
     * @param panel      需要展示的面板之一
     * @param isDoEvent  是否进行确认（是否为第一次登录，需要签署免责声明）
     */
    public DisclaimerDialog(String title, String dialogName, IDEBasePanel panel, boolean isDoEvent) {
        this.isDoEvent = isDoEvent;
        this.title = ValidateUtils.isEmptyString(title) ? InstallConstant.BEFORE_INSTALL : title;
        this.dialogName =
                ValidateUtils.isEmptyString(dialogName) ? Dialogs.INSTALL_DISCLAIMER.dialogName() : dialogName;
        this.mainPanel = panel;

        // 无位置信息时居中显示
        if (this.isDoEvent) {
            Logger.info("Default Processing");
        } else {
            setCancelButtonText(TERM_OPERATE_CLOSE);
        }

        // 初始化弹框内容
        initDialog();
        okAction.setEnabled(false);
        addChangeListener();
        setResizable(false);
    }

    /**
     * 免责声明构造内容面板
     *
     * @param msg          链接内容
     * @param installTitle installTitle
     * @return jPanel 主面板
     */
    private static JPanel panelWithHtmlListener(String msg, JLabel installTitle) {
        Color color = installTitle.getForeground();
        Font font = installTitle.getFont();
        String messages =
                MessageFormat.format(
                        msg, color.getRed(), color.getGreen(), color.getBlue(), font.getFontName(), font.getSize());
        JEditorPane jEditorPane = new JEditorPane("text/html", messages);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);

        HyperlinkListener listener =
                new HyperlinkListener() {
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
     * 免责声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JLabel headerLabel = new JLabel();
        if (this.isDoEvent) {
            headerLabel.setText(TuningUserManageConstant.USER_DISCLAIMER_HEAD);
            headerLabel.setIcon(BaseIntellijIcons.load(TuningIDEConstant.ICON_INFO));
        } else {
            headerLabel.setVisible(false);
        }

        // 主面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(600, 230));
        centerPanel.add(headerLabel, BorderLayout.NORTH);
        centerPanel.add(
                panelWithHtmlListener(TuningUserManageConstant.USER_DISCLAIMER_CONTENT, headerLabel),
                BorderLayout.CENTER);
        return centerPanel;
    }

    /**
     * 返回免责证明结果
     *
     * @return 返回签署结果
     */
    protected boolean okVerify() {
        Logger.info("Cancel User Disclaimer.");
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>(BUTTON_LENGTH);
        button.add(IDEMessageDialogUtil.ButtonName.THINK_AGAIN);
        button.add(IDEMessageDialogUtil.ButtonName.LOG_OUT);
        String select =
                IDEMessageDialogUtil.showDialog(
                        new MessageDialogBean(
                                TuningUserManageConstant.USER_DISCLAIMER_REFUSE_WARNING,
                                TuningUserManageConstant.USER_DISCLAIMER_TIPS_TITLE,
                                button,
                                0,
                                IDEMessageDialogUtil.getWarn()));
        return !select.equals(IDEMessageDialogUtil.ButtonName.THINK_AGAIN.getKey())
                && !select.equals(IDEMessageDialogUtil.ButtonName.CANCEL.getKey());
    }

    /**
     * 同意免责声明。关闭弹窗
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start Deploy.");
        // 关闭配置远程服务器窗口
        IDEBaseDialog configDialog = IDEComponentManager.getViableDialog(Dialogs.SERVER_CONFIG.dialogName());
        if (configDialog != null) {
            configDialog.dispose();
        }
        JSONObject paramJsonObj = new JSONObject();
        paramJsonObj.put("SYS_DISCLAIMER", "1");
        JSONObject javaParamJsonObj = new JSONObject();
        javaParamJsonObj.put("signed", true);
        setDisclaimer("user-management/api/v2.2/users/user-extend/", paramJsonObj);
        setDisclaimer("java-perf/api/disclaimer/createGuardian/", javaParamJsonObj);
    }

    /**
     * 设置免责声明是否显示状态
     *
     * @param url          接口地址
     * @param paramJsonObj paramJsonObj
     */
    public void setDisclaimer(String url, JSONObject paramJsonObj) {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        url,
                        HttpMethod.POST.vaLue(),
                        "");
        String objStr = JsonUtil.getJsonStrFromJsonObj(paramJsonObj);
        message.setBodyData(objStr);
        TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 取消按钮事件
     */
    @Override
    protected void onCancelAction() {
    }
}
