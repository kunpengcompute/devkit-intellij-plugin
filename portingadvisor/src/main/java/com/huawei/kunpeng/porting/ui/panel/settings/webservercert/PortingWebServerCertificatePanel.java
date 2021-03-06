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

package com.huawei.kunpeng.porting.ui.panel.settings.webservercert;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.webservercert.WebServerCertificatePanel;
import com.huawei.kunpeng.intellij.ui.render.LogTableRenderer;
import com.huawei.kunpeng.porting.action.setting.webcert.PortingWebServerCertificateAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.bean.PortingWebServerCertificateBean;

import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

/**
 * WebServerCertificatePanel
 *
 * @since 2020-10-07
 */
public class PortingWebServerCertificatePanel extends WebServerCertificatePanel {
    private boolean isAdmin = PortingUserManageConstant.USER_ROLE_ADMIN
        .equals(PortingUserInfoContext.getInstance().getRole());
    private PortingWebServerCertificateAction portingWebServerCertificateAction;

    /**
     * ??????????????????????????????????????????
     *
     * @param toolWindow  ?????????toolWindow??????????????????
     * @param panelName   ????????????
     * @param displayName ????????????title
     * @param isLockable  isLockable
     */
    public PortingWebServerCertificatePanel(
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
    public PortingWebServerCertificatePanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * ???toolWindow???????????????,????????????????????????
     *
     * @param toolWindow toolWindow
     */
    public PortingWebServerCertificatePanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(this.mainPanel);
        addEditorPanel(I18NServer.toLocale("plugins_porting_certificate_webNotice"),
            mainPanel, webNoticePanel, webNotice);
        mainPanel.updateUI();

        this.webServerCertTable = new JBTable(); // ???????????????
        toolbarForRunTable = ToolbarDecorator.createDecorator(webServerCertTable);
        tableJPane.removeAll();
        tableJPane.add(toolbarForRunTable.createPanel());
        updateTable();
    }

    /**
     * ????????????
     *
     * @return boolean ???????????????????????????
     */
    public boolean updateTable() {
        // ??????????????????????????????
        PortingWebServerCertificateBean webServerCert = getAction().getCertStatus(this,
            null);
        if (webServerCert == null) {
            return false;
        }
        String serverCertExpireTime = webServerCert.getWebServerCertExpireTime();
        if (serverCertExpireTime == null || serverCertExpireTime.equals(this.certExpireTime)) {
            return false;
        } else {
            doUpdate(webServerCert);
            return true;
        }
    }

    private void doUpdate(PortingWebServerCertificateBean webServerCert) {
        this.certExpireTime = webServerCert.getWebServerCertExpireTime();
        this.tableDate[0][0] = webServerCert.getWebServerCertName();
        this.tableDate[0][1] = webServerCert.getWebServerCertExpireTime();
        this.tableDate[0][2] = webServerCert.getStatus();

        List<String> columnNameList = new ArrayList<>();
        columnNameList.add(I18NServer.toLocale("plugins_porting_certificate_name"));
        columnNameList.add(I18NServer.toLocale("plugins_porting_certificate_validTime"));
        columnNameList.add(I18NServer.toLocale("plugins_porting_certificate_status"));
        if (isAdmin) {
            // ?????????????????????????????????web????????????
            columnNameList.add(I18NServer.toLocale("plugins_porting_certificate_options"));
            this.tableDate[0][3] = "...";
        }

        this.webServerCertTable.setModel(new DefaultTableModel(this.tableDate, columnNameList.toArray()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // ????????????????????????
                if (column == OPERATION_INDEX) {
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
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() ->
            IdeFocusManager.getGlobalInstance().requestFocus(this.webServerCertTable, true));
    }

    private void addListener() {
        this.webServerCertTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * ??????????????????
                 *
                 *  @param event ??????
                 */
                @Override
                public void mouseClicked(MouseEvent event) {
                    int row = webServerCertTable.rowAtPoint(event.getPoint());
                    int col = webServerCertTable.columnAtPoint(event.getPoint());
                    if (row == 0 && col == 3) {
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
            action = new PortingWebServerCertificateAction();
        }
        portingWebServerCertificateAction = new PortingWebServerCertificateAction();
        portingWebServerCertificateAction.setWebServerCertificatePanel(this);
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof PortingWebServerCertificateAction) {
            this.action = action;
        }
        registerComponentAction();
    }

    /**
     * ??????Action
     *
     * @return webServerCertificateAction
     */
    @Override
    public PortingWebServerCertificateAction getAction() {
        if (portingWebServerCertificateAction == null) {
            portingWebServerCertificateAction = new PortingWebServerCertificateAction();
            portingWebServerCertificateAction.setWebServerCertificatePanel(this);
        }
        return portingWebServerCertificateAction;
    }

    private void showPopupMenu(Component invoker, int xPostion, int yPostion) {
        // ?????? ???????????? ??????
        JPopupMenu popupMenu = new JPopupMenu();

        // ?????? ????????????
        JMenuItem createCsrFile = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_generation_file"));
        JMenuItem importCert = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_import_file"));
        JMenuItem restart = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_restart"));
        JMenuItem updateSecret = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_update"));

        // ?????? ???????????? ??? ????????????
        popupMenu.add(createCsrFile);
        popupMenu.add(importCert);
        popupMenu.add(restart);
        popupMenu.add(updateSecret);

        // ?????????????????????????????????
        createCsrFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAction().createCsrFile();
            }
        });
        importCert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAction().importCertFile();
            }
        });
        // ?????????????????????????????????
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAction().restartService();
            }
        });
        updateSecret.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAction().requestUpdateKey();
            }
        });

        // ?????????????????????????????????
        popupMenu.show(invoker, xPostion, yPostion);
    }
}
