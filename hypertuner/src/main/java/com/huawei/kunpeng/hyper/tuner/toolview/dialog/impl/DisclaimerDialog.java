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

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_OPERATE_CLOSE;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
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

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.application.ApplicationManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * ????????????????????????
 *
 * @since 2021-5-31
 */
public class DisclaimerDialog extends DisclaimerCommonDialog {
    /**
     * ??????????????????????????????
     *
     * @param title      ????????????
     * @param dialogName ????????????
     * @param panel      ???????????????????????????
     * @param panel      ??????
     * @param isDoEvent  ???????????????????????????????????????????????????????????????????????????
     */
    public DisclaimerDialog(String title, String dialogName, IDEBasePanel panel, boolean isDoEvent) {
        this.isDoEvent = isDoEvent;
        this.title = ValidateUtils.isEmptyString(title) ? WeakPwdConstant.BEFORE_INSTALL : title;
        this.dialogName =
                ValidateUtils.isEmptyString(dialogName) ? Dialogs.INSTALL_DISCLAIMER.dialogName() : dialogName;
        this.mainPanel = panel;

        // ??????????????????????????????
        if (this.isDoEvent) {
            setDoNotAskOption(
                    new DoNotAskOption.Adapter() {
                        @Override
                        public void rememberChoice(boolean boo, int i) {
                        }

                        @Override
                        public boolean shouldSaveOptionsOnCancel() {
                            return true;
                        }

                        @Override
                        public @NotNull String getDoNotShowMessage() {
                            return WeakPwdConstant.READ_DEPLOY;
                        }
                    });
        } else {
            setCancelButtonText(TERM_OPERATE_CLOSE);
        }

        // ?????????????????????
        initDialog();
        okAction.setEnabled(false);
        addChangeListener();
        setResizable(false);
    }

    /**
     * ??????????????????????????????
     *
     * @param msg          ????????????
     * @param installTitle installTitle
     * @return jPanel ?????????
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
                     * ???????????????
                     *
                     * @param hyperLink ?????????
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
     * ????????????
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

        // ?????????
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(600, 230));
        centerPanel.add(headerLabel, BorderLayout.NORTH);
        centerPanel.add(
                panelWithHtmlListener(TuningUserManageConstant.USER_DISCLAIMER_CONTENT, headerLabel),
                BorderLayout.CENTER);
        return centerPanel;
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
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
        if (select.equals(IDEMessageDialogUtil.ButtonName.THINK_AGAIN.getKey())
                || select.equals(IDEMessageDialogUtil.ButtonName.CANCEL.getKey())) {
            return false;
        } else {
            ApplicationManager.getApplication()
                    .invokeLater(
                            () -> {
                                // ??????
                                TuningLoginUtils.logout();
                            });
            return true;
        }
    }

    /**
     * ?????????????????????????????????
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start Deploy.");
        // ?????????????????????????????????
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
     * ????????????????????????????????????
     *
     * @param url          ????????????
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
     * ??????????????????
     */
    @Override
    protected void onCancelAction() {
    }
}
