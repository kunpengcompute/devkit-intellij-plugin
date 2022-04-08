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

package com.huawei.kunpeng.intellij.ui.panel;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;
import com.twelvemonkeys.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The class FileSaveAsPanel: 文件重复另存为Panel
 *
 * @since v1.0
 */
public class FileSaveAsPanel extends IDEBasePanel {
    private static String fileSaveAsPanelName = "FILE_SAVE_AS";

    private JPanel mainPanel;

    private JLabel fileStar;

    private JLabel newFileName;

    private JTextField fileName;

    private String defaultNewName;

    /**
     * 完整的构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName 面板名称
     * @param displayName 显示名称
     * @param defaultNewName 默认新名称
     * @param isLockable 是否锁定
     */
    public FileSaveAsPanel(ToolWindow toolWindow, String panelName, String displayName, String defaultNewName,
        boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = ValidateUtils.isEmptyString(panelName) ? fileSaveAsPanelName : panelName;
        this.defaultNewName = defaultNewName;
        // 初始化面板
        initPanel(mainPanel);
        // 初始化content实例
        createContent(mainPanel, displayName, isLockable);
    }

    /**
     * 反射调用的构造函数
     *
     * @param toolWindow 工具窗口
     * @param defaultNewName 默认新名称
     */
    public FileSaveAsPanel(ToolWindow toolWindow, String defaultNewName) {
        this(toolWindow, null, null, defaultNewName, false);
    }

    /**
     * 获取文件名
     *
     * @return 文件名
     */
    public JTextField getFileName() {
        return fileName;
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        newFileName.setText(CommonI18NServer.toLocale("common_new_file_name"));
        fileName.setText(defaultNewName);
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();
        if (StringUtil.isEmpty(fileName.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_required_tip"), fileName));
        }
        if (FileUtil.isContainChinese(fileName.getText()) || !FileUtil.validateFileName(fileName.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_file_name_illegal_tip"), fileName));
        }
        return result;
    }
}
