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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert;

import com.huawei.kunpeng.hyper.tuner.action.panel.webservercert.TuningWebServerCertificateAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.model.TuningWebServerCertificateBean;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.webservercert.WebServerCertificatePanel;
import com.huawei.kunpeng.intellij.ui.render.LogTableRenderer;

import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

/**
 * WebServerCertificatePanel
 *
 * @since 2020-10-07
 */
public class TuningWebServerCertificatePanel extends WebServerCertificatePanel {
    private boolean isAdmin = TuningUserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole());
    private TuningWebServerCertificateAction webServerCertificateAction;

    /**
     * ??????????????????????????????????????????
     *
     * @param toolWindow  ?????????toolWindow??????????????????
     * @param panelName   ????????????
     * @param displayName ????????????title
     * @param isLockable  isLockable
     */
    public TuningWebServerCertificatePanel(
            ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = ValidateUtils.isEmptyString(panelName) ? Panels.WEB_SERVER_CERTIFICATE.panelName() : panelName;

        // ??????????????????????????????
        registerComponentAction();

        // ???????????????
        initPanel(mainPanel);

        // ?????????content??????
        createContent(mainPanel, displayName, isLockable);
    }

    /**
     * ???toolWindow???displayName???????????????
     *
     * @param toolWindow  toolWindow
     * @param displayName ??????????????????
     * @param isLockable  isLockable
     */
    public TuningWebServerCertificatePanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }


    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(this.mainPanel);
        addHtmlEditorPanel();
        this.webServerCertTable = new JBTable(); // ???????????????
        toolbarForRunTable = ToolbarDecorator.createDecorator(webServerCertTable);
        tableJPane.removeAll();
        tableJPane.add(toolbarForRunTable.createPanel());
        updateTable();
    }

    /**
     * ??????webNotice??????
     */
    private void addHtmlEditorPanel() {
        StringBuilder tempSB = new StringBuilder();
        tempSB.append("<html> <body><div style=\"color:rgb({0},{1},{2}); font-family:{3};font-size:{4}\">");
        tempSB.append(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_webNotice"));
        tempSB.append("</div> </body></html>");
        Color color = mainPanel.getForeground();
        Font font = mainPanel.getFont();
        String messages =
                MessageFormat.format(
                        tempSB.toString(),
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        font.getFontName(),
                        font.getSize());
        webNotice = new JEditorPane("text/html", messages);
        webNotice.setEditable(false);
        webNotice.setOpaque(false);

        webNoticePanel.removeAll(); // ?????????????????????
        webNoticePanel.add(webNotice);
        webNotice.updateUI();
        IdeFocusManager.getGlobalInstance()
                .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(webNoticePanel, true));
        webNoticePanel.updateUI();
        mainPanel.updateUI();
    }

    /**
     * ????????????
     *
     * @return boolean ???????????????????????????
     */
    public boolean updateTable() {
        // ??????????????????????????????
        TuningWebServerCertificateBean webServerCert = getAction().getCertStatus(this, null);
        this.tableDate[0][0] = webServerCert.getWebServerCertName();
        this.tableDate[0][1] = webServerCert.getWebServerCertExpireTime();
        this.tableDate[0][2] = webServerCert.getStatus();

        List<String> columnNameList = new ArrayList<String>();
        columnNameList.add(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_name"));
        columnNameList.add(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_validTime"));
        columnNameList.add(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_status"));
        if (isAdmin) {
            // ?????????????????????????????????web????????????
            columnNameList.add(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_options"));
            this.tableDate[0][3] = "...";
        }

        this.webServerCertTable.setModel(
                new DefaultTableModel(this.tableDate, columnNameList.toArray()) {
                    @Override
                    public boolean isCellEditable(int row, int columns) {
                        // ????????????????????????
                        if (columns == OPERATION_INDEX) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

        // ?????????????????????
        this.webServerCertTable.getTableHeader().setReorderingAllowed(false);
        this.webServerCertTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new LogTableRenderer());
        addListener();
        if (webServerCert.getWebServerCertExpireTime().equals(this.certExpireTime)) {
            return false;
        } else {
            this.certExpireTime = webServerCert.getWebServerCertExpireTime();
            return true;
        }
    }

    private void addListener() {
        this.webServerCertTable.addMouseListener(
                new MouseAdapter() {
                    /**
                     * ??????????????????
                     *
                     *  @param event ??????
                     */
                    public void mouseClicked(MouseEvent event) {
                        int row = webServerCertTable.rowAtPoint(event.getPoint());
                        int column = webServerCertTable.columnAtPoint(event.getPoint());
                        if (row == 0 && column == 3) {
                            showPopupMenu(event.getComponent(), event.getX(), event.getY());
                        }
                    }
                });
    }

    /**
     * ??????????????????
     */
    @Override
    protected void registerComponentAction() {
        if (this.action == null) {
            action = new TuningWebServerCertificateAction();
        }
        webServerCertificateAction = new TuningWebServerCertificateAction();
        webServerCertificateAction.setWebServerCertificatePanel(this);
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof TuningWebServerCertificateAction) {
            this.action = action;
        }
        registerComponentAction();
    }

    /**
     * ??????Action
     *
     * @return webServerCertificateAction
     */
    public TuningWebServerCertificateAction getAction() {
        if (webServerCertificateAction == null) {
            webServerCertificateAction = new TuningWebServerCertificateAction();
            webServerCertificateAction.setWebServerCertificatePanel(this);
        }
        return webServerCertificateAction;
    }


    private void showPopupMenu(Component invoker, int xPostion, int yPostion) {
        // ?????? ???????????? ??????
        JPopupMenu popupMenu = new JPopupMenu();

        // ?????? ????????????
        JMenuItem updateSecret = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_update"));
        JMenuItem restart = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_restart"));
        JMenuItem importCert = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file"));
        JMenuItem createCsrFile =
                new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_generation_file"));
        // ?????? ???????????? ??? ????????????
        popupMenu.add(createCsrFile);
        popupMenu.add(importCert);
        popupMenu.add(restart);
        popupMenu.add(updateSecret);

        // ?????????????????????????????????
        createCsrFile.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getAction().createCsrFile();
                    }
                });
        importCert.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getAction().importCertFile();
                    }
                });
        // ?????????????????????????????????
        restart.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getAction().restartService();
                    }
                });
        updateSecret.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getAction().requestUpdateKey();
                    }
                });

        // ?????????????????????????????????
        popupMenu.show(invoker, xPostion, yPostion);
    }
}
