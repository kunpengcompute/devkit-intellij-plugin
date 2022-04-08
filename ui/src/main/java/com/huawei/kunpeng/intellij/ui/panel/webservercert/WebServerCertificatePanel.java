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

package com.huawei.kunpeng.intellij.ui.panel.webservercert;

import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.ui.ToolbarDecorator;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * web服务证书Panel
 *
 * @since 2021-09-08
 */
public class WebServerCertificatePanel extends IDEBasePanel {
    /**
     * 操作列下标
     */
    protected static final int OPERATION_INDEX = 4;

    /**
     * 閾值最小值
     */
    protected static final int MIN = 7;

    /**
     * 閾值最大值
     */
    protected static final int MAX = 180;

    /**
     * 操作列下标
     */
    protected static final int STATUS_INDEX = 2;

    /**
     * 表格数据
     */
    protected Object[][] tableDate = new Object[1][4];

    /**
     * 是否管理员
     */
    protected boolean isAdmin;

    /**
     * 主面板
     */
    protected JPanel mainPanel;

    /**
     * 中间面板
     */
    protected JPanel centerPanel;

    /**
     * web服务证书表格
     */
    protected JTable webServerCertTable;

    /**
     * 帮助
     */
    protected JLabel helpLabel;

    /**
     * 证书面板
     */
    protected JPanel certPanel;

    /**
     * 提示阈panel
     */
    protected JPanel webNoticePanel;

    /**
     * 表格panel
     */
    protected JPanel tableJPane;

    /**
     * 提示语
     */
    protected JEditorPane webNotice;

    /**
     * web服务证书有效时间
     */
    protected String certExpireTime;

    /**
     * 表格工具栏
     */
    protected ToolbarDecorator toolbarForRunTable;


    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 获取mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 点击apply按钮提交修改请求
     */
    public void apply() {
    }

    /**
     * 重置面板内容
     */
    public void reset() {
    }

    /**
     * 判断是否修改参数值且输入了用户密码
     * 若是，设置界面enable Apply按钮
     * 否则，Apply按钮不可点击
     *
     * @return boolean
     */
    public boolean isModified() {
        return false;
    }
}
