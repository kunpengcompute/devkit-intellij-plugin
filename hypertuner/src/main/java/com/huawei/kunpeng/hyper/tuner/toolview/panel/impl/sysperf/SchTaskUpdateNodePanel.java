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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.EditPanelTypeEnum;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskOtherInfo;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;

import org.apache.commons.lang.StringUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 预约任务 更新
 * 设置多节点参数 面板
 *
 * @since 2021-07-14
 */
public class SchTaskUpdateNodePanel extends IDEBasePanel {
    private final Integer panelWidth = 500;
    private final Integer panelHeight = 25;
    private final Integer label1Height = 25;
    private final Integer label1Width = 100;
    private JPanel mainPanel;
    /**
     * 除应用参数（pid/进程名/应用路径/应用参数外）的参数开始的组件索引
     */
    private int nodeParamIndex = 2;
    private Project project;

    private HashMap<String, Integer> compIndexMap = new HashMap<>();
    private Integer compIndex = 0;
    private List<SchTaskOtherInfo> nodeParamList;
    private boolean isSaveNewVal; // 是否保存新的值

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param panelName   面板名称
     * @param displayName 面板显示title
     */
    public SchTaskUpdateNodePanel(String panelName, String displayName, List<SchTaskOtherInfo> nodeParamList) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "SchTaskUpdateNodePanel" : panelName;
        this.nodeParamList = nodeParamList;
        initPanel(mainPanel); // 初始化面板
    }

    @Override
    protected void initPanel(JPanel panel) {
        project = CommonUtil.getDefaultProject();
        super.initPanel(mainPanel);

        GridLayout gridLayout = new GridLayout();
        mainPanel.setLayout(gridLayout);
        mainPanel.setPreferredSize(new Dimension(500, -1));
        mainPanel.setBorder(null);
        for (SchTaskOtherInfo item : nodeParamList) {
            String fieldNameI18N = item.getFieldNameI18N(); // 国际化的值
            String fieldValue = item.getFieldValue(); // 字段值
            String fieldName = item.getFieldName(); // 字段名称
            JPanel addPanel;
            if (item.getFieldType().equals(EditPanelTypeEnum.J_LABEL.value())) {
                addPanel = getLabelPanel(fieldName, fieldNameI18N, fieldValue);
            } else {
                addPanel = getTextFieldPanel(fieldName, fieldNameI18N, fieldValue);
            }
            mainPanel.add(addPanel);
            compIndexMap.put(fieldName, compIndex++);
        }
        int count = mainPanel.getComponentCount();
        gridLayout.setRows(count); // 设置行数
        gridLayout.setColumns(1); // 设置列数
        gridLayout.setVgap(8); // 设置竖直间隙
    }

    private void setAppLaunchInfoShow(String appDir, String appParam) {
        // 应用路径
        JPanel appDirPanel = getTextFieldPanel("dir", TaskManageContent.PARAM_APPLICATION_PATH, appDir);
        mainPanel.add(appDirPanel);

        // 应用参数
        JPanel appParamPanel =
                getTextFieldPanel("param", TaskManageContent.PARAM_APPLICATION_PARAM, appParam);
        mainPanel.add(appParamPanel);
    }

    // 设置 App Attach 相关的信息
    private void setAppAttachInfoShow(String processName, String pid) {
        // 进程名
        JPanel processNamePanel = getTextFieldPanel("processName", TaskManageContent.PARAM_PROCESS_NAME, processName);
        mainPanel.add(processNamePanel);
        // PID
        JPanel pidPanel = getTextFieldPanel("pid", TaskManageContent.PARAM_PID, pid);
        mainPanel.add(pidPanel);
    }

    /**
     * 根据传来的参数生成Panel
     *
     * @param label0Text    字段名称
     * @param label1Text    展示的国际化字段名称
     * @param textFieldText 字段值对象
     * @return JPanel JPanel
     */
    private JPanel getTextFieldPanel(String label0Text, String label1Text, String textFieldText) {
        String feldText = textFieldText == null ? "" : textFieldText;
        JPanel panel = getBasicPanel(label0Text, label1Text, 5);
        JTextField jTextField = new JTextField();
        jTextField.setText(feldText);
        jTextField.setPreferredSize(new Dimension(200, label1Height));
        panel.add(jTextField);
        return panel;
    }

    private JPanel getLabelPanel(String label0Text, String label1Text, String labelText) {
        String labelShowText = labelText == null ? "" : labelText;
        JPanel panel = getBasicPanel(label0Text, label1Text, 5);
        JLabel jLabel = new JLabel();
        jLabel.setText(labelShowText);
        jLabel.setPreferredSize(new Dimension(200, label1Height));
        panel.add(jLabel);
        return panel;
    }

    private JPanel getBasicPanel(String label0Text, String label1Text, int extrasHeight) {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        JPanel basicPanel = new JPanel(flowLayout);
        JLabel label0 = new JLabel(label0Text);
        JLabel label1 = new JLabel(" " + label1Text);
        basicPanel.add(label0);
        basicPanel.add(label1);
        Dimension panelDim = new Dimension(panelWidth, panelHeight + extrasHeight);
        basicPanel.setPreferredSize(panelDim);
        basicPanel.setMinimumSize(panelDim);
        basicPanel.setMaximumSize(panelDim);
        Dimension labelDim = new Dimension(label1Width + 26, label1Height + extrasHeight);
        label1.setPreferredSize(labelDim);
        label1.setMinimumSize(labelDim);
        label1.setMaximumSize(labelDim);
        Dimension label0Dim = new Dimension(0, label1Height + extrasHeight);
        label0.setPreferredSize(label0Dim);
        label0.setMinimumSize(label0Dim);
        label0.setMaximumSize(label0Dim);
        return basicPanel;
    }

    /**
     * 将用户输入的参数保存到Map中
     */
    public void getNewNodeParamMap() {
        getNewValueHandle();
        isSaveNewVal = true;
    }

    public boolean isSaveNewVal() {
        return isSaveNewVal;
    }

    /**
     * 从 otherInfoPanel 中编辑框中获取 用户输入/选择的新数据，存入 paramList 中
     */
    protected void getNewValueHandle() {
        for (SchTaskOtherInfo infoItem : nodeParamList) {
            String fieldName = infoItem.getFieldName();
            int cmpIdx = compIndexMap.get(fieldName);
            if (infoItem.getFieldType().equals(EditPanelTypeEnum.TEXT_FIELD.value())) {
                Component component = mainPanel.getComponent(cmpIdx);
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    Component component2 = panel.getComponent(2);
                    if (component2 instanceof JTextField) {
                        JTextField textField = (JTextField) component2;
                        infoItem.setFieldValue(textField.getText());
                    }
                }
            }
        }
    }

    /**
     * 确认操作
     *
     * @return 是否选择下载文件
     */
    public Boolean onOK() {
        return false;
    }

    /**
     * 全量校验
     *
     * @return 所有校验异常
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();
        for (SchTaskOtherInfo infoItem : nodeParamList) {
            String fieldName = infoItem.getFieldName();
            int cmpIdx = compIndexMap.get(fieldName);
            if (infoItem.getFieldType().equals(EditPanelTypeEnum.TEXT_FIELD.value())) {
                Component component = mainPanel.getComponent(cmpIdx);
                compInputValVerify(result, infoItem, component);
            }
        }
        return result;
    }

    private void compInputValVerify(List<ValidationInfo> result, SchTaskOtherInfo infoItem, Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Component component2 = panel.getComponent(2);
            if (component2 instanceof JTextField) {
                JTextField textField = (JTextField) component2;
                String inputVal = textField.getText();
                String itemVerifyRegex = infoItem.getVerifyRegex();
                if (!StringUtils.isEmpty(itemVerifyRegex) && !StringUtils.isEmpty(inputVal)) {
                    if (!inputVal.matches(itemVerifyRegex)) {
                        result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_INCORRECT_FORMAT, textField));
                    }
                }
            }
        }
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}
