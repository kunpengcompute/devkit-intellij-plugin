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

package com.huawei.kunpeng.porting.ui.panel;

import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.PortingAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

/**
 * 右键源码分析面板
 *
 * @since 2020-10-09
 */
public class PortingPanel extends IDEBasePanel {
    /**
     * 上传的文件常量名
     */
    public static final String UPLOAD_FILE = "uploadFile";

    /**
     * 主面板
     */
    private JPanel mainPanel;

    private Map<String, Map<String, String>> osSystemMap = new HashMap<>();

    private JCheckBox cCheckBox;

    private JCheckBox fortranCheckBox;

    private JLabel typeLabel;

    private JLabel builderVersionLabel;

    private JComboBox cComboBox;

    private JComboBox fortranComboBox;

    private JLabel buildToolLabel;

    private JComboBox buildToolComboBox;

    private JLabel buildComLabel;

    private JLabel systemTypeLabel;

    private JComboBox systemTypeComboBox;

    private JLabel systemKernelVLable;

    private JComboBox systemKernelVComboBox;

    private JLabel codeText;

    private JLabel codeDir;

    private JTextField buildTextField;

    private JCheckBox interpretedCheckBox;

    private JCheckBox goCheckBox;

    private JEditorPane portingTipPane;

    private JLabel firstBlankLabel;

    private JLabel secondBlankLabel;

    private JLabel thirdBlankLabel;

    private JEditorPane firstBlankPane;

    private JEditorPane secondBlankPane;

    private File uploadFile;

    /**
     * 构造函数
     *
     * @param toolWindow  toolWindow
     * @param panelName   面板名称
     * @param displayName 面板展示名
     * @param params      面板携带参数
     */
    public PortingPanel(ToolWindow toolWindow, String panelName, String displayName, Map params) {
        setToolWindow(toolWindow);
        this.params = params;
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.PORTING.panelName() : panelName;
        this.uploadFile = JsonUtil.getValueIgnoreCaseFromMap(params, UPLOAD_FILE, File.class);

        // 初始化面板
        initPanel(mainPanel);
        // 初始化面板内组件事件
        registerComponentAction();
        // 初始化content实例
        createContent(mainPanel, displayName, false);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow toolWindow
     * @param params     面板携带参数
     */
    public PortingPanel(ToolWindow toolWindow, Map params) {
        this(toolWindow, null, null, params);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public PortingPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, null);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        // 默认选中C/C++
        initCheckBoxes();
        // 设置页面国际化信息
        initLabels();
        setPortingTipProperties();
        setBlankProperties();
        // 加载扫描配置参数系统信息
        loadSystemInfo();
    }

    private void setPortingTipProperties() {
        portingTipPane.setContentType("text/html");
        portingTipPane.setText(I18NServer.toLocale("source_porting_tip"));
        portingTipPane.setAlignmentY(0.5f);
        portingTipPane.setAlignmentX(0.5f);
        portingTipPane.setBackground(new JBColor(0xf2f2f2, 0x3c3f41));
    }

    private void setBlankProperties() {
        setBlankLabelProperties(firstBlankLabel);
        setBlankLabelProperties(secondBlankLabel);
        setBlankLabelProperties(thirdBlankLabel);
        setBlankPaneProperties(firstBlankPane);
        setBlankPaneProperties(secondBlankPane);
    }

    private void initCheckBoxes() {
        cCheckBox.setSelected(true);
        cCheckBox.setToolTipText(I18NServer.toLocale("source_code_type_tip"));
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.setDismissDelay(10000);
        fortranCheckBox.setToolTipText(I18NServer.toLocale("source_code_type_tip"));
        goCheckBox.setToolTipText(I18NServer.toLocale("source_code_type_tip"));
        interpretedCheckBox.setToolTipText(I18NServer.toLocale("interpreted_code_type_tip"));
        interpretedCheckBox.setText(I18NServer.toLocale("interpreted_label"));
        cComboBox.setEnabled(true);
        fortranComboBox.setEnabled(false);
        systemKernelVComboBox.setEnabled(false);
    }

    private void initLabels() {
        codeText.setText(I18NServer.toLocale("plugins_porting_code_dir"));
        try {
            String filePath = uploadFile == null ? "" : uploadFile.getCanonicalPath();
            codeDir.setText(getDirPath(filePath));
            codeDir.setToolTipText(filePath);
        } catch (IOException e) {
            Logger.error("Get file path error.");
        }
        typeLabel.setText(I18NServer.toLocale("plugins_porting_fortran"));
        builderVersionLabel.setIcon(BaseIntellijIcons.Settings.CERT_TIPS);
        builderVersionLabel.setText(I18NServer.toLocale("plugins_porting_compiler_version"));
        builderVersionLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        builderVersionLabel.setIconTextGap(5);
        builderVersionLabel.setToolTipText(I18NServer.toLocale("compiler_version_tip"));
        buildToolLabel.setText(I18NServer.toLocale("plugins_porting_construct_tool"));
        buildComLabel.setText(I18NServer.toLocale("plugins_porting_compile_command"));
        systemTypeLabel.setText(I18NServer.toLocale("plugins_porting_target_os"));
        systemKernelVLable.setText(I18NServer.toLocale("plugins_porting_target_system_kernel_version"));
    }


    private String getDirPath(String filePath) {
        if (filePath.length() > 55) {
            return filePath.substring(0, 55) + "...";
        }
        return filePath;
    }

    private void setBlankLabelProperties(JLabel blankLabel) {
        blankLabel.setVisible(false);
        blankLabel.setText("");
    }

    private void setBlankPaneProperties(JEditorPane blankPane) {
        blankPane.setVisible(false);
        blankPane.setBackground(new JBColor(0xf2f2f2, 0x3c3f41));
        blankPane.setText("");
    }


    private void refreshView() {
        isVisibleComponent(cCheckBox.isSelected() || fortranCheckBox.isSelected()
            || goCheckBox.isSelected() || !interpretedCheckBox.isSelected());
        if (parentComponent instanceof IdeaDialog) {
            ((IdeaDialog) parentComponent).updateDialog();
        }
    }

    /**
     * 检查必填项是否OK
     *
     * @return true:OK,false:不OK
     */
    public boolean checkRequired() {
        if (!isUploadingFileOK()) {
            return false;
        }

        if (!isSelectedSrcType()) {
            return false;
        }

        return isSelectedCompileCmd();
    }

    private boolean isUploadingFileOK() {
        return uploadFile != null;
    }

    private boolean isSelectedSrcType() {
        return fortranCheckBox.isSelected() || cCheckBox.isSelected()
            || goCheckBox.isSelected() || interpretedCheckBox.isSelected();
    }

    private boolean isSelectedCompileCmd() {
        if (!cCheckBox.isSelected() && !fortranCheckBox.isSelected()
            && !goCheckBox.isSelected() && interpretedCheckBox.isSelected()) {
            return true;
        }
        String compileCmd = buildTextField.getText().trim();
        return ValidateUtils.isNotEmptyString(compileCmd) && (compileCmd.startsWith("make") || compileCmd.startsWith(
            "cmake") || compileCmd.startsWith("go"));
    }


    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new PortingAction();
        }
        unionCheckBox();
    }

    private void unionCheckBox() {
        languageCheckBoxAddItemListener();
        unionParamCheckBoxAddItemListener();
        regCompileCmdListener();
    }

    private void languageCheckBoxAddItemListener() {
        // C/C++选中下,C/C++编译器版本可编辑
        cCheckBoxAddItemListener();
        // fortran选中下,fortran编译器版本可编辑
        fortranceCheckBoxAddItemListener();
        // go选中下编译器版本可编辑
        goCheckBoxAddItemListener();
        // 解释型语言
        interpretedCheckBoxAddItemListener();
    }

    private void interpretedCheckBoxAddItemListener() {
        interpretedCheckBox.addItemListener((event) -> {
            if (event.getSource() instanceof JCheckBox) {
                JCheckBox jCheckBox = (JCheckBox) event.getSource();
                buildToolComboBox.setEnabled(!jCheckBox.isSelected());
                if (fortranCheckBox.isSelected() || cCheckBox.isSelected()) {
                    buildToolComboBox.setEnabled(true);
                }
                refreshView();
            }
        });
    }

    private void goCheckBoxAddItemListener() {
        goCheckBox.addItemListener((event) -> {
            if (event.getSource() instanceof JCheckBox) {
                JCheckBox jCheckBox = (JCheckBox) event.getSource();
                cComboBox.setEnabled(jCheckBox.isSelected() || cCheckBox.isSelected() || fortranCheckBox.isSelected());
                buildToolComboBox.setEnabled(cCheckBox.isSelected() || jCheckBox.isSelected());
                refreshView();
            }
        });
    }

    private void fortranceCheckBoxAddItemListener() {
        fortranCheckBox.addItemListener((event) -> {
            if (event.getSource() instanceof JCheckBox) {
                JCheckBox jCheckBox = (JCheckBox) event.getSource();
                fortranComboBox.setEnabled(jCheckBox.isSelected());
                buildToolComboBox.setEnabled(cCheckBox.isSelected() || jCheckBox.isSelected()
                    || goCheckBox.isSelected());
                goBuildToolHide();
                refreshView();
            }
        });
    }

    private void cCheckBoxAddItemListener() {
        cCheckBox.addItemListener((event) -> {
            if (event.getSource() instanceof JCheckBox) {
                JCheckBox jCheckBox = (JCheckBox) event.getSource();
                cComboBox.setEnabled(jCheckBox.isSelected() || goCheckBox.isSelected());
                buildToolComboBox.setEnabled(fortranCheckBox.isSelected() || jCheckBox.isSelected()
                    || goCheckBox.isSelected());
                portingTipPane.setVisible(jCheckBox.isSelected());
                firstBlankPane.setVisible(!portingTipPane.isVisible());
                if ("en-us".equals(I18NServer.getCurrentLanguage())) {
                    secondBlankPane.setVisible(!portingTipPane.isVisible());
                } else {
                    secondBlankPane.setVisible(false);
                }
                goBuildToolHide();
                refreshView();
            }
        });
    }

    private void isVisibleComponent(boolean isVisible) {
        builderVersionLabel.setVisible(isVisible);
        cComboBox.setVisible(isVisible);
        fortranComboBox.setVisible(isVisible);
        buildToolComboBox.setVisible(isVisible);
        buildTextField.setVisible(isVisible);
        buildToolLabel.setVisible(isVisible);
        buildComLabel.setVisible(isVisible);
        firstBlankLabel.setVisible(!isVisible);
        secondBlankLabel.setVisible(!isVisible);
        thirdBlankLabel.setVisible(!isVisible);
    }

    private void goBuildToolHide() {
        if (cCheckBox.isSelected() || fortranCheckBox.isSelected()) {
            // 移除go构建工具
            buildToolComboBox.removeItem("go");
            buildToolComboBox.setSelectedIndex(0);
        } else {
            if (!"go".equals(buildToolComboBox.getItemAt(buildToolComboBox.getItemCount()))) {
                buildToolComboBox.addItem("go");
            }
        }
    }

    private void unionParamCheckBoxAddItemListener() {
        // 操作系统与内核版本联动
        systemTypeComBoxAddItemListener();

        // 构建工具与编译命令的联动
        buildToolComBoxAddItemListener();
    }

    private void buildToolComBoxAddItemListener() {
        buildToolComboBox.addItemListener(event -> {
            Object object = buildToolComboBox.getSelectedItem();
            if (event.getSource() instanceof JComboBox && object != null) {
                if ("automake".equals(object.toString())) {
                    buildTextField.setText("make");
                    buildTextField.setEditable(false);
                } else if ("go".equals(object.toString())) {
                    buildTextField.setText("go build");
                    buildTextField.setEditable(true);
                } else {
                    buildTextField.setEditable(true);
                    buildTextField.setText(object.toString());
                }
            }
        });
    }

    private void systemTypeComBoxAddItemListener() {
        systemTypeComboBox.addItemListener(event -> {
            Object object = systemTypeComboBox.getSelectedItem();
            if (event.getSource() instanceof JComboBox) {
                systemKernelVComboBox.removeAllItems();
                // 实现目标操作系统与内核联动
                systemKernelVComboBox.addItem(osSystemMap.get(object.toString()).get("kernel_default"));
                if (cComboBox.isEnabled()) {
                    cComboBox.setSelectedItem(osSystemMap.get(object.toString()).get("gcc_default"));
                }
            }
        });
    }

    private void regCompileCmdListener() {
        buildTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refreshView();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refreshView();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                refreshView();
            }
        });

        buildTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                parentValidate();
            }
        });
    }

    private void parentValidate() {
        IdeaDialog parentDialog = null;
        if (parentComponent instanceof IdeaDialog) {
            parentDialog = (IdeaDialog) parentComponent;
        } else {
            return;
        }

        parentDialog.doOkValidate();
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof PortingAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * 加载扫描配置参数系统信息
     */
    private void loadSystemInfo() {
        String data = SourcePortingHandler.getInstance().getSystemInfo().orElse(new ResponseBean()).getData();
        Map mapData = JsonUtil.getJsonObjFromJsonStr(data);
        showOSAndKernelView(mapData);
        showLanguageAndBuildToolView(mapData);
    }

    private void showOSAndKernelView(Map mapData) {
        List<Map<String, String>> osSystemList = JsonUtil.getValueIgnoreCaseFromMap(mapData, "os_system_list",
            List.class);
        osSystemList.sort((firstOs, secondOs) -> {
            String firstOsSystem = firstOs.get("os_system").toLowerCase(Locale.ROOT);
            String secondOsSystem = secondOs.get("os_system").toLowerCase(Locale.ROOT);
            return firstOsSystem.compareTo(secondOsSystem);
        });
        for (Map<String, String> osMap : osSystemList) {
            this.osSystemMap.put(osMap.get("os_system"), osMap);
        }

        // 目标操作系统、目标操作系统内核, 默认显示第一个
        for (Map<String, String> osSystem : osSystemList) {
            systemTypeComboBox.addItem(osSystem.get("os_system"));
        }
        // 默认选择第五个 CentOs 7.6
        systemTypeComboBox.setSelectedIndex(4);
        Map<String, String> selectOsSystem = osSystemList.get(4);
        systemKernelVComboBox.addItem(selectOsSystem.get("kernel_default"));
        cComboBox.setSelectedItem(selectOsSystem.get("gcc_default"));
    }

    private void showLanguageAndBuildToolView(Map mapData) {
        // gcc版本
        List<String> gccList = JsonUtil.getValueIgnoreCaseFromMap(mapData, "gcc_list", List.class);
        for (String fortran : gccList) {
            cComboBox.addItem(fortran);
        }

        // fortran版本
        List<String> fortranList = JsonUtil.getValueIgnoreCaseFromMap(mapData, "fortran_list", List.class);
        for (String fortran : fortranList) {
            fortranComboBox.addItem(fortran);
        }
        List<String> constructTools = JsonUtil.getValueIgnoreCaseFromMap(mapData, "construct_tools", List.class);
        // 编译工具、编译命令
        for (String constructTool : constructTools) {
            if (!"go".equals(constructTool)) {
                buildToolComboBox.addItem(constructTool);
            }
        }
    }

    /**
     * 获取porting参数
     *
     * @return 参数Map
     */
    public Map<String, Object> getAnalyzeParams() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> compiler = new HashMap<String, Object>();
        Map<String, Object> goCompiler = new HashMap<String, Object>();
        result.put("info", params);
        params.put("compiler", compiler);
        params.put("cgocompiler", goCompiler);
        params.put("compilecommand", buildTextField.getText().trim());
        params.put("constructtool", buildToolComboBox.getSelectedItem());
        params.put("sourcedir", getSrcDir());
        params.put("targetkernel", systemKernelVComboBox.getSelectedItem());
        params.put("targetos", systemTypeComboBox.getSelectedItem().toString().toLowerCase(Locale.ROOT));
        params.put("interpreted", false);

        // 设置用户选择的参数
        if (!fortranCheckBox.isSelected()) {
            params.put("gfortran", "");
        } else {
            params.put("gfortran", fortranComboBox.getSelectedItem().toString().split(" ")[0].toLowerCase(Locale.ROOT)
                + fortranComboBox.getSelectedItem().toString().split(" ")[1]);
        }
        getCAndGoCompiler(compiler, cCheckBox);

        getCAndGoCompiler(goCompiler, goCheckBox);

        if (interpretedCheckBox.isSelected()) {
            params.put("interpreted", true);
        }

        if (!fortranCheckBox.isSelected() && !cCheckBox.isSelected() && !goCheckBox.isSelected()) {
            params.put("compilecommand", "");
            params.put("constructtool", "");
        }

        // 右键需要后台上传文件
        params.put("os_mapping_dir", codeDir.getText());
        result.put(UPLOAD_FILE, uploadFile);

        return result;
    }

    private void getCAndGoCompiler(Map<String, Object> compiler, JCheckBox cCheckBox) {
        Object selectedItem = cComboBox.getSelectedItem();
        if (!cCheckBox.isSelected() || selectedItem == null) {
            compiler.put("type", "");
            compiler.put("version", "");
        } else {
            String[] split = selectedItem.toString().split(" ");
            if (split.length > 1) {
                compiler.put("type", split[0].toLowerCase(Locale.ROOT));
                compiler.put("version", split[1]);
            } else {
                compiler.put("type", "");
                compiler.put("version", "");
            }
        }
    }

    @NotNull
    private String getSrcDir() {
        String portAdv = PortingIDEConstant.PATH_SEPARATOR + "portadv" + PortingIDEConstant.PATH_SEPARATOR;
        String sourceCode = PortingIDEConstant.PATH_SEPARATOR + "sourcecode" + PortingIDEConstant.PATH_SEPARATOR;
        if (this.uploadFile != null) {
            /**
             * 上传文件成功后后端返回的文件名
             */
            String unzipDataName = StringUtil.getPortingPrefix(this.uploadFile.getName());
            return SourcePortingHandler.getCustomInstallPath() + portAdv + PortingUserInfoContext.getInstance()
                .getUserName() + sourceCode + unzipDataName;
        }
        return "";
    }

    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        if (!isSelectedCompileCmd()) {
            vi = new ValidationInfo(I18NServer.toLocale("plugins_porting_tip_compile_cmd"), buildTextField);
        }

        return vi;
    }
}
