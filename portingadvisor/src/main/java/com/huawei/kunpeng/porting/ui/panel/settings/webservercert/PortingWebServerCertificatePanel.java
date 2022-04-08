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
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public PortingWebServerCertificatePanel(
        ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = ValidateUtils.isEmptyString(panelName) ? Panels.WEB_SERVER_CERTIFICATE.panelName() : panelName;

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化面板
        initPanel(mainPanel);

        // 初始化content实例
        createContent(mainPanel, displayName, isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示名称
     * @param isLockable  isLockable
     */
    public PortingWebServerCertificatePanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public PortingWebServerCertificatePanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(this.mainPanel);
        addEditorPanel(I18NServer.toLocale("plugins_porting_certificate_webNotice"),
            mainPanel, webNoticePanel, webNotice);
        mainPanel.updateUI();

        this.webServerCertTable = new JBTable(); // 优化界面。
        toolbarForRunTable = ToolbarDecorator.createDecorator(webServerCertTable);
        tableJPane.removeAll();
        tableJPane.add(toolbarForRunTable.createPanel());
        updateTable();
    }

    /**
     * 更新表格
     *
     * @return boolean 证书信息是否有更新
     */
    public boolean updateTable() {
        // 获取和设置表格行数据
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
            // 只有管理员登录才能管理web服务证书
            columnNameList.add(I18NServer.toLocale("plugins_porting_certificate_options"));
            this.tableDate[0][3] = "...";
        }

        this.webServerCertTable.setModel(new DefaultTableModel(this.tableDate, columnNameList.toArray()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 操作行才能被编辑
                if (column == OPERATION_INDEX) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        // 不可以重排列。
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
                 * 鼠标点击事件
                 *
                 *  @param event 事件
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
     * 注册组件事件
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
     * 获取Action
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
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();

        // 创建 一级菜单
        JMenuItem createCsrFile = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_generation_file"));
        JMenuItem importCert = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_import_file"));
        JMenuItem restart = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_restart"));
        JMenuItem updateSecret = new JMenuItem(I18NServer.toLocale("plugins_porting_certificate_update"));

        // 添加 一级菜单 到 弹出菜单
        popupMenu.add(createCsrFile);
        popupMenu.add(importCert);
        popupMenu.add(restart);
        popupMenu.add(updateSecret);

        // 添加菜单项的点击监听器
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
        // 添加菜单项的点击监听器
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

        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xPostion, yPostion);
    }
}
