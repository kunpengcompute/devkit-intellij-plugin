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

import com.alibaba.fastjson.JSONArray;

import org.apache.commons.lang.StringUtils;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 * 可编辑面板的种类 枚举
 *
 * @since 2021-9-9
 */
public enum EditPanelTypeEnum {
    NULL("null") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> list1, List<String> list2) {
            return Optional.empty();
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            return Optional.empty();
        }
    },
    /**
     * Label展示框
     */
    J_LABEL("jLabel") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            String label2Str = "";
            if (value != null) {
                if (StringUtils.isEmpty(value)) {
                    label2Str = "--";
                } else if ("null".equals(value)) {
                    return Optional.empty();
                } else {
                    label2Str = value;
                }
            } else if (selected != null && selected.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < selected.size(); i++) {
                    builder.append(selected.get(i));
                    if (i != selected.size() - 1) {
                        builder.append(",");
                    }
                }
                label2Str = builder.toString();
            } else {
                return Optional.empty();
            }
            return Optional.of(OtherInfoPanelUtil.getJLabelPanel(label, label2Str));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            if (component instanceof JLabel) {
                JLabel jLabel = (JLabel) component;
                return Optional.of(jLabel.getText());
            }
            return Optional.empty();
        }
    },
    /**
     * 文本编辑框
     */
    TEXT_FIELD("textField") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            return Optional.of(OtherInfoPanelUtil.getTextFieldPanel(label, value == null ? "" : value));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            if (component instanceof JTextField) {
                JTextField jTextField = (JTextField) component;
                return Optional.of(jTextField.getText());
            }
            return Optional.empty();
        }
    },
    /**
     * 自定义数字编辑面板，可以点击切换自定义和高精度
     */
    J_SPINNER_CUSTOM("jSpinnerCustom") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            int itnValue;
            boolean isCustom = true;
            try {
                // 自定义为低精度，为int类型
                itnValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                isCustom = false;
                itnValue = 710;
            }
            return Optional.of(OtherInfoPanelUtil.getJSpinnerCustomPanel(label, isCustom, itnValue));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            if (component instanceof JComboBox) {
                JComboBox jComboBox = (JComboBox) component;
                Object selectedItemObj = jComboBox.getSelectedItem();
                if (selectedItemObj instanceof String) {
                    // 是否为自定义
                    if (TaskManageContent.PARAM_INTERVAL_CUSTOM.equals(selectedItemObj)) {
                        Component jSpinnerComp = jComboBox.getParent().getComponent(2);
                        if (!(jSpinnerComp instanceof JSpinner)) {
                            return Optional.empty();
                        }
                        JSpinner jSpinner = (JSpinner) jSpinnerComp;
                        Object jSpinnerValObj = jSpinner.getValue();
                        if (jSpinnerValObj instanceof Integer) {
                            return Optional.of(jSpinnerValObj.toString());
                        }
                    } else {
                        return Optional.of("0.71");
                    }
                }
            }
            return Optional.empty();
        }
    },
    /**
     * 数字编辑面板
     */
    J_SPINNER("jSpinner") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            int itnValue;
            try {
                itnValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                itnValue = 0;
            }
            return Optional.of(OtherInfoPanelUtil.getJSpinnerPanel(label, itnValue));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            if (component instanceof JSpinner) {
                JSpinner jSpinner = (JSpinner) component;
                Object newValueObj = jSpinner.getValue();
                if (newValueObj instanceof Integer) {
                    return Optional.of(newValueObj.toString());
                }
            }
            return Optional.empty();
        }
    },
    /**
     * 开/关 单选框
     */
    RADIO_BTN("radioBtn") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            Boolean booleValue;
            if (value.equals(TaskManageContent.PARAM_TRUE)) {
                booleValue = Boolean.TRUE;
            } else {
                booleValue = Boolean.FALSE;
            }
            return Optional.of(OtherInfoPanelUtil.getRadioBtnPanel(label, booleValue));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            if (component instanceof JRadioButton) {
                JRadioButton jRadioButton = (JRadioButton) component;
                return Optional.of(jRadioButton.isSelected() + "");
            }
            return Optional.empty();
        }
    },
    /**
     * 多选框
     */
    MULTI_CHECK_BOX("multiCheckBox") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            if (selected == null || selected.size() == 0) {
                return Optional.empty();
            }
            return Optional.of(OtherInfoPanelUtil.getMultiCheckBoxPanel(label, showList, selected));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            List<String> selectedList = new ArrayList<>();
            Component parentComp = component.getParent();
            if (parentComp instanceof JPanel) {
                JPanel jPanel = (JPanel) parentComp;
                for (int k = 1; k < jPanel.getComponentCount(); k++) {
                    Component componentK = jPanel.getComponent(k);
                    getList(selectedList, componentK);
                }
            }
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(selectedList);
            return Optional.of(jsonArray.toJSONString());
        }

        private void getList(List<String> selectedList, Component componentK) {
            if (componentK instanceof JCheckBox) {
                JCheckBox jCheckBoxK = (JCheckBox) componentK;
                boolean isSelect = jCheckBoxK.isSelected();
                if (isSelect) {
                    selectedList.add(jCheckBoxK.getText());
                }
            }
        }
    },
    /**
     * 下拉单选框
     */
    J_COMBO_BOX("jComboBox") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            if (selected == null || selected.size() == 0) {
                return Optional.empty();
            }
            return Optional.of(OtherInfoPanelUtil.getJComboBoxPanel(label, showList, selected));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            if (component instanceof JComboBox) {
                JComboBox jComboBox = (JComboBox) component;
                Object newValueObj = jComboBox.getSelectedItem();
                if (newValueObj instanceof String) {
                    return Optional.of((String) newValueObj);
                }
            }
            return Optional.empty();
        }
    },
    /**
     * 锁与等待面板
     */
    LOCK_FUNCTION("lockFunction") {
        @Override
        public Optional<JPanel> getItemPanel(String label, String value, List<String> showList, List<String> selected) {
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(OtherInfoPanelUtil.addFunctionNamePanel(label, value));
        }

        @Override
        public Optional<String> getNewValFormComp(Component component) {
            Component parentComp = component.getParent();
            if (parentComp instanceof JPanel) {
                JPanel panel = (JPanel) parentComp;
                int labelIdx = 2; // 新的函数字符串存储位置
                Component indexComp = panel.getComponent(labelIdx);
                if (indexComp instanceof JLabel) {
                    return Optional.of(((JLabel) indexComp).getText());
                }
            }
            return Optional.empty();
        }
    };

    private final String panelType;

    EditPanelTypeEnum(String panelType) {
        this.panelType = panelType;
    }

    /**
     * 通过延伸信息value获取PageType类的一个枚举实例
     *
     * @param value 值
     * @return String
     */
    public static EditPanelTypeEnum getType(String value) {
        for (EditPanelTypeEnum panelTypeEnum : EditPanelTypeEnum.values()) {
            if (panelTypeEnum.value().equals(value)) {
                return panelTypeEnum;
            }
        }
        return EditPanelTypeEnum.NULL;
    }

    /**
     * 获取函数名
     *
     * @return String
     */
    public String value() {
        return panelType;
    }

    /**
     * 获取单个信息Panel，
     * （已经国际化）仅负责展示
     *
     * @param label  label
     * @param value  value
     * @param show   show
     * @param select select
     * @return 单个信息Panel
     */
    public abstract Optional<JPanel> getItemPanel(String label, String value, List<String> show, List<String> select);

    /**
     * 从不同的输入组件中获取用户输入的新的值
     * (国际化的值)，仅负责获取值，不负责逆国际化
     *
     * @param component 输入组件
     * @return 用户输入/选择的新的值，选择的值不包含国际化(为true/false字符串)
     */
    public abstract Optional<String> getNewValFormComp(Component component);
}
