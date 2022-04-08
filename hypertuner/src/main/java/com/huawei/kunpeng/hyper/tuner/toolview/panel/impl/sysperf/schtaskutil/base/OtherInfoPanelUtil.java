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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskCommonFormatUtil;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.SchTaskUpdateFunctionDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskUpdateFunctionPanel;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.openapi.ui.ComboBox;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 * 添加面板的工具类
 *
 * @since 2021-9-9
 */
public class OtherInfoPanelUtil {
    private static final Integer PANEL_WIDTH = 500;
    private static final Integer PANEL_HEIGHT = 25;
    private static final Integer LABEL1_HEIGHT = 25;
    private static final Integer LABEL1_WIDTH = 100;
    /**
     * 字段国际化Map
     */
    private static HashMap<String, String> fileValueI18NMap = TaskCommonFormatUtil.getTaskInfoParamValueI18NMap();
    /**
     * 多选/单选 国际化Map
     */
    private static HashMap<String, String> selectN81IMap = TaskCommonFormatUtil.getSelectMap();

    /**
     * 根据传来的参数生成 展示 Panel
     *
     * @param label1Text 展示的国际化字段名称
     * @param label2Text 字段值对象
     * @return JPanel JPanel
     */
    public static JPanel getJLabelPanel(String label1Text, String label2Text) {
        String textFieldText2 = label2Text == null ? "" : label2Text;
        JPanel jLabelPanel = getBasicPanel(label1Text, 5);
        JLabel label = new JLabel();
        if (textFieldText2.length() > 40) {
            label = tooLongLabelShow(textFieldText2);
        } else if (textFieldText2.length() > 28) {
            label.setText(textFieldText2);
            label.setToolTipText(textFieldText2);
        } else {
            label.setText(textFieldText2);
        }
        label.setPreferredSize(new Dimension(200, LABEL1_HEIGHT));
        jLabelPanel.add(label);
        return jLabelPanel;
    }

    /**
     * 较长字段展示时，分割添加换行
     *
     * @param resultStr 待展示的字段
     * @return 换行之后，添加悬浮框提示的Label
     */
    public static JLabel tooLongLabelShow(String resultStr) {
        JLabel label2 = new JLabel();
        String showStr = resultStr.substring(0, 50).replaceAll("\\^_\\{(,)2}", "") + "...";
        label2.setText(showStr);
        String[] funNameStrArr = resultStr.replaceFirst("\\^_\\{(,)2}", "").split("\\^_\\{(,)2}");
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        for (int i = 0; i < funNameStrArr.length; i++) {
            builder.append(funNameStrArr[i]);
            if (i % 4 == 0 && builder.length() > 55) {
                builder.append("<br>");
            }
        }
        builder.append("</html>");
        label2.setToolTipText(builder.toString());
        return label2;
    }

    /**
     * 根据传来的参数生成Panel
     *
     * @param label1Text    展示的国际化字段名称
     * @param textFieldText 字段值对象
     * @return JPanel JPanel
     */
    public static JPanel getTextFieldPanel(String label1Text, String textFieldText) {
        String textFieldText2 = textFieldText == null ? "" : textFieldText;
        JPanel textFieldPanel = getBasicPanel(label1Text, 5);
        JTextField jTextField = new JTextField();
        jTextField.setText(textFieldText2);
        jTextField.setPreferredSize(new Dimension(390, LABEL1_HEIGHT));
        textFieldPanel.add(jTextField);
        return textFieldPanel;
    }

    /**
     * 获取数字编辑Panel
     *
     * @param label1Text 展示的国际化字段名称
     * @param number     展示的值
     * @return 数字编辑Panel
     */
    public static JPanel getJSpinnerPanel(String label1Text, Integer number) {
        int numberInt = number == null ? 0 : number;
        JPanel jSpinnerPanel = getBasicPanel(label1Text, 8);
        JSpinner spinner1 = new JSpinner();
        spinner1.setPreferredSize(new Dimension(390, LABEL1_HEIGHT + 3));
        spinner1.setValue(numberInt);
        jSpinnerPanel.add(spinner1);
        return jSpinnerPanel;
    }

    /**
     * 获取自定义 数字编辑Panel
     * 可切换高精度/自定义
     * 高精度不可编辑/自定义可编辑，有区间限制
     *
     * @param label1Text 展示的国际化字段名称
     * @param isCustom   是否为自定义
     * @param number     展示的值
     * @return 数字编辑Panel
     */
    public static JPanel getJSpinnerCustomPanel(String label1Text, boolean isCustom, Integer number) {
        // 切换组件
        JComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(new Dimension(130, LABEL1_HEIGHT));
        comboBox.addItem(TaskManageContent.PARAM_INTERVAL_HIGH);
        comboBox.addItem(TaskManageContent.PARAM_INTERVAL_CUSTOM);
        // 数值输入组件
        JSpinner spinner1 = new JSpinner();
        spinner1.setPreferredSize(new Dimension(255, LABEL1_HEIGHT + 3));
        spinner1.setValue(number == null ? 0 : number);
        JPanel jSpinnerCustomPanel = getBasicPanel(label1Text, 8);
        Component label1Comp = jSpinnerCustomPanel.getComponent(0);
        if (!(label1Comp instanceof JLabel)) {
            return jSpinnerCustomPanel;
        }
        // 国际化字段名称 label
        JLabel label1 = (JLabel) label1Comp;
        if (isCustom) {
            label1.setText(TaskManageContent.PARAM_INTERVAL_MS);
            comboBox.setSelectedItem(TaskManageContent.PARAM_INTERVAL_CUSTOM);
            spinner1.setEnabled(true);
        } else {
            label1.setText(TaskManageContent.PARAM_INTERVAL_US);
            comboBox.setSelectedItem(TaskManageContent.PARAM_INTERVAL_HIGH);
            spinner1.setEnabled(false);
        }
        jSpinnerCustomPanel.add(comboBox);
        jSpinnerCustomPanel.add(spinner1);
        comboBox.addActionListener(
                new ActionListener() {
                    /**
                     * 监听【任务状态】筛选框变化
                     *
                     * @param event 事件
                     */
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        Object selectItem = comboBox.getSelectedItem();
                        if (!(selectItem instanceof String)) {
                            return;
                        }
                        if ("comboBoxChanged".equals(event.getActionCommand())) {
                            if (TaskManageContent.PARAM_INTERVAL_HIGH.equals(selectItem)) {
                                spinner1.setEnabled(false);
                                spinner1.setValue(710);
                                label1.setText(TaskManageContent.PARAM_INTERVAL_US);
                            } else {
                                spinner1.setEnabled(true);
                                spinner1.setValue(1);
                                label1.setText(TaskManageContent.PARAM_INTERVAL_MS);
                            }
                        }
                    }
                });
        return jSpinnerCustomPanel;
    }

    /**
     * 获取 true/false 类型值的编辑Panel
     *
     * @param label1Text 展示的国际化字段名称
     * @param select     选中
     * @return RadioBtnPanel
     */
    public static JPanel getRadioBtnPanel(String label1Text, Boolean select) {
        JPanel radioBtnPanel = getBasicPanel(label1Text, 0);
        JRadioButton radioButton1 = new JRadioButton();
        radioButton1.setText(TaskManageContent.PARAM_TRUE);
        JRadioButton radioButton2 = new JRadioButton();
        radioButton2.setText(TaskManageContent.PARAM_FALSE);
        radioBtnPanel.add(radioButton1);
        radioBtnPanel.add(radioButton2);
        ButtonGroup radioBtnGroup = new ButtonGroup();
        radioBtnGroup.add(radioButton1);
        radioBtnGroup.add(radioButton2);
        if (select != null && select) {
            radioButton1.setSelected(true);
        } else {
            radioButton2.setSelected(true);
        }
        return radioBtnPanel;
    }

    /**
     * 获取下拉单选框菜单 Panel
     *
     * @param label1Text 展示的国际化字段名称
     * @param showList   带展示的值
     * @param selectList 选中的项目
     * @return 数字编辑Panel
     */
    public static JPanel getJComboBoxPanel(String label1Text, List<String> showList, List<String> selectList) {
        String select = selectList.get(0);
        JPanel comboBoxPanel = getBasicPanel(label1Text, 8);
        Dimension panelDim = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
        comboBoxPanel.setPreferredSize(panelDim);
        comboBoxPanel.setMinimumSize(panelDim);
        comboBoxPanel.setMaximumSize(panelDim);
        ComboBoxModel<String> model = new DefaultComboBoxModel(showList.toArray());
        JComboBox comboBox = new ComboBox();
        comboBox.setModel(model);
        for (int i = 0; i < showList.size(); i++) {
            if (showList.get(i).equals(select)) {
                comboBox.setSelectedIndex(i);
            }
        }
        comboBox.setPreferredSize(new Dimension(390, LABEL1_HEIGHT));
        comboBoxPanel.add(comboBox);
        return comboBoxPanel;
    }

    /**
     * 获取复选框按钮编辑面板
     *
     * @param label1Text 展示的国际化字段名称
     * @param showList   展示的字段值（带选择项）
     * @param selected   选中的
     * @return MultiCheckBoxPanel
     */
    public static JPanel getMultiCheckBoxPanel(String label1Text, List<String> showList, List<String> selected) {
        JPanel multiCheckBoxPanel = getBasicPanel(label1Text, 2);
        for (String s : showList) {
            JCheckBox checkBoxItem = new JCheckBox();
            checkBoxItem.setText(s);
            for (String select : selected) {
                if (select != null && select.equals(s)) {
                    checkBoxItem.setSelected(true);
                    break;
                }
            }
            multiCheckBoxPanel.add(checkBoxItem);
        }
        return multiCheckBoxPanel;
    }

    /**
     * 锁与等待展示panel：标准函数和自定义锁与等待函数 面板 展示
     *
     * @param label1Text   label1Text
     * @param functionName functionName
     * @return 锁与等待展示 函数 panel
     */
    public static JPanel addFunctionNamePanel(String label1Text, String functionName) {
        // 标准函数
        JPanel functionNamePanel = OtherInfoPanelUtil.getBasicPanel(label1Text, 5);
        JButton selectFunBtn = new JButton(TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION_SELECT);
        functionNamePanel.add(selectFunBtn);
        JLabel newSelectFunLabel = new JLabel();
        newSelectFunLabel.setPreferredSize(new Dimension(0, 0));
        newSelectFunLabel.setText(functionName);
        functionNamePanel.add(newSelectFunLabel);
        // 生成参数Set
        HashSet<String> selectFunctionSet = new HashSet<>();
        String[] functionArr = functionName.split("\\^_\\{(,)2}");
        Collections.addAll(selectFunctionSet, functionArr);
        selectFunctionSet.remove("");
        // 处理自定义函数
        List<String> customerFunNameList = new ArrayList<>();
        String lastFunName = functionArr[functionArr.length - 1];
        // 最后一个元素包含 ; 则 ; 后面的为自定义函数
        if (lastFunName.contains(";")) {
            selectFunctionSet.remove(functionArr[functionArr.length - 1]);
            String[] customerFunArr = lastFunName.split(";");
            customerFunNameList.add(customerFunArr[customerFunArr.length - 1]);
            selectFunctionSet.add(customerFunArr[0] + ";");
        } else {
            selectFunctionSet.remove(lastFunName);
            selectFunctionSet.add(lastFunName + ";");
            customerFunNameList.add("");
        }
        selectFunBtn.addMouseListener(
                new MouseAdapter() {
                    /**
                     * 鼠标点击事件 #447ff5
                     *
                     * @param event 事件
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        SchTaskUpdateFunctionDialog dialog =
                                new SchTaskUpdateFunctionDialog(
                                        TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION_SELECT,
                                        new SchTaskUpdateFunctionPanel(
                                                "SchTaskUpdateNodePanel",
                                                "displayName",
                                                selectFunctionSet,
                                                customerFunNameList));
                        dialog.displayPanel();
                        newSelectFunLabel.setText(lockFunctionNameHandel(selectFunctionSet, customerFunNameList));
                    }
                });
        return functionNamePanel;
    }

    /**
     * 锁与等待分析： 新的选中函数处理函数
     *
     * @param selectFunctionSet   选中的函数
     * @param customerFunNameList 自定义函数
     * @return 处理后所有的函数字符串
     */
    private static String lockFunctionNameHandel(HashSet<String> selectFunctionSet, List<String> customerFunNameList) {
        StringBuilder newFunctionBuild = new StringBuilder();
        for (String selectItem : selectFunctionSet) {
            newFunctionBuild.append("^_{,2}").append(selectItem);
        }
        newFunctionBuild.append(customerFunNameList.get(0));
        return newFunctionBuild.toString();
    }

    /**
     * String 转int
     *
     * @param numberStr String
     * @return int
     */
    public static int parseString2Int(String numberStr) {
        int numberInt = 0;
        try {
            numberInt = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            Logger.info("number string  format exception：");
        }
        return numberInt;
    }

    /**
     * 获取基础的Panel
     *
     * @param label1Text   展示的国际化字段名称
     * @param extrasHeight 额外的适应高度
     * @return BasicPanel
     */
    public static JPanel getBasicPanel(String label1Text, int extrasHeight) {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        JPanel basicPanel = new JPanel(flowLayout);
        String showLabel1Srt = " " + label1Text;
        JLabel label1 = new JLabel(showLabel1Srt);
        if (showLabel1Srt.length() > 16) {
            label1.setToolTipText(showLabel1Srt);
        }
        Dimension panelDim = new Dimension(PANEL_WIDTH, PANEL_HEIGHT + extrasHeight);
        basicPanel.setMinimumSize(panelDim);
        basicPanel.setPreferredSize(panelDim);
        Dimension labelDim = new Dimension(LABEL1_WIDTH + 26, LABEL1_HEIGHT + extrasHeight);
        label1.setMinimumSize(labelDim);
        basicPanel.setMaximumSize(panelDim);
        label1.setPreferredSize(labelDim);
        label1.setMaximumSize(labelDim);
        basicPanel.add(label1);
        return basicPanel;
    }


    /**
     * 获取新的值 bool 类型转 enable/disable
     *
     * @param bool bool类型
     * @return enable/disable
     */
    protected String bool2AbleString(boolean bool) {
        if (bool) {
            return "enable";
        } else {
            return "disable";
        }
    }

    /**
     * 获取新的值 bool 类型转 enable/disable
     *
     * @param ableStr String 类型
     * @return enable/disable
     */
    protected Boolean ableStr2Bool(String ableStr) {
        if ("enable".equals(ableStr)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

}
