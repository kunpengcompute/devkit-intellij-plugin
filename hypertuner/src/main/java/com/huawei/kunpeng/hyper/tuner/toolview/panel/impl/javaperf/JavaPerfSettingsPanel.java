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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf;

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.hyper.tuner.action.panel.javaperf.JavaPerfSettingAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.CommonSettingsPanel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * javaPerf设置顶层节点面板
 *
 * @since 2021-07-07
 */
public class JavaPerfSettingsPanel extends CommonSettingsPanel {
    private String stack; // 栈深度配置

    private String logLevel; // 运行日志级别

    private String levelDay; // 内部通信证书自动告警时间（天）

    /**
     * 配置类
     */
    private JavaPerfSettingAction javaPerfSettingAction = new JavaPerfSettingAction();

    /**
     * 构造函数
     */
    public JavaPerfSettingsPanel() {
        // 初始化数据
        mainPanel = new JPanel();
        initPanel(mainPanel);
        isEdit();
        registerComponentAction();
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 用户状态是否改变。
     *
     * @return 用户状态是否改变。
     */
    private boolean isUserChange() {
        String guiUserLimit = userLimitField.getText();
        String guiUserTime = userTimeField.getText();
        boolean isUserChange =
                !Objects.equals(userLimit.toString(), guiUserLimit)
                        || !Objects.equals(userTime.toString(), guiUserTime);
        return isUserChange;
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JComponent getPreferredFocusedComponent() {
        return mainPanel;
    }

    /**
     * 界面是否修改
     *
     * @return 结果
     */
    public boolean isModified() {
        return isUserChange() || isLogLevelChange();
    }


    /**
     * 日志级别是否改变。
     *
     * @return 日志级别是否改变。
     */
    private boolean isLogLevelChange() {
        return !Objects.equals(currentLevel, level);
    }

    /**
     * 事件处理。
     *
     * @throws ConfigurationException 配置异常。
     */
    public void apply() throws ConfigurationException {
        userLimitField.validateContent();
        userTimeField.validateContent();

        String limit = userLimitField.getText();
        // 修改内部通信证书自动告警时间（天）
        if (!limit.equals(userLimit.toString())) {
            if (changeLimit(limit)) {
                userLimit = Integer.valueOf(limit);
            }
        }
        String time = userTimeField.getText();
        // 修改栈深度配置
        if (!time.equals(userTime.toString())) {
            if (changeTime(time)) {
                userTime = Integer.valueOf(time);
            }
        }
        currentLevel = javaPerfSettingAction.getLogLevel();
        if (!level.equals(currentLevel)) {
            javaPerfSettingAction.changeLogLevel(level);
            currentLevel = level;
        }
        // 修改栈深度配置
        if (!time.equals(userTime.toString())) {
            if (changeTime(time)) {
                userTime = Integer.valueOf(time);
            }
        }
    }

    /**
     * 重置界面方法
     */
    public void reset() {
        // 获取内部通信证书自动告警时间（天）
        levelDay = javaPerfSettingAction.getEarlyWarningDays();
        // 获取栈深度
        stack = javaPerfSettingAction.getstackDepth();
        // 获取运行日志级别
        logLevel = javaPerfSettingAction.getLogLevel();

        // 设置初始值
        // 内部通信证书自动告警时间（天）
        userLimit = Integer.valueOf(levelDay);
        userLimitField.setText(userLimit.toString());
        // 设置栈深度
        userTime = Integer.valueOf(stack);
        userTimeField.setText(userTime.toString());
        // 设置运行日志
        comboBox.setSelectedItem(logLevel);
    }

    /**
     * 初始化面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        if (action == null) {
            action = new JavaPerfSettingAction();
        }
        // 获取内部通信证书自动告警时间（天）
        levelDay = javaPerfSettingAction.getEarlyWarningDays();
        // 获取栈深度
        stack = javaPerfSettingAction.getstackDepth();

        // 内部通信证书自动告警时间（天）(7~180)
        userLimitField =
                new IntegerField(JavaProviderSettingConstant.MAX_ALARM_DAYS, MIN, MAX);
        userLimitField.setCanBeEmpty(false); // 是否可为空
        userLimitField.setText(levelDay);
        userLimitField.setToolTipText(JavaProviderSettingConstant.TIP_NUM_MODIFY_ALARM);
        JPanel userLimitPanel = new JPanel(new BorderLayout());
        userLimitPanel.add(userLimitField);
        JLabel label = new JLabel("(7-180)");
        userLimitPanel.add(label, BorderLayout.EAST);
        // 栈深度配置 (16~64)
        userTimeField =
                new IntegerField(JavaProviderSettingConstant.STACK_DEPTH, STACK_DEPTH_MIN_NUM, STACK_DEPTH_MAX_NUM);
        userTimeField.setCanBeEmpty(false);
        userTimeField.setText(stack);
        userTimeField.setToolTipText(JavaProviderSettingConstant.TIP_STACK_DEPTH);
        JPanel userTimePanel = new JPanel(new BorderLayout());
        userTimePanel.add(userTimeField);
        JLabel jlabel = new JLabel("(16-64)");
        userTimePanel.add(jlabel, BorderLayout.EAST);

        // 往表单添加元素
        FormBuilder mainFormBuilder =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(new JLabel(JavaProviderSettingConstant.MAX_ALARM_DAYS), userLimitPanel);
        // 获取日志级别
        if (Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole())) {
            Map<String, Object> obj = javaPerfSettingAction.getAllLogLevel();
            JSONArray arrayList = new JSONArray();
            if (obj.get("members") instanceof JSONArray) {
                arrayList = (JSONArray) obj.get("members");
            }
            // 用户管理运行日志级别
            getLogLevel(mainFormBuilder, arrayList, userTimePanel);
        } else {
            getLogLevel(mainFormBuilder, null, userTimePanel);
        }
        mainPanel = mainFormBuilder.addComponentFillVertically(panel, 0).getPanel();
    }

    /**
     * 是否可以编辑
     */
    private void isEdit() {
        if (Objects.equals(TuningUserManageConstant.USER_ROLE_USER, UserInfoContext.getInstance().getRole())) {
            userLimitField.setEditable(false);
            userTimeField.setEditable(false);
            comboBox.setEnabled(false);
        }
    }

    /**
     * 获取运行日志级别
     *
     * @param mainFormBuilder mainFormBuilder
     * @param arrayList       arraylist
     * @param jPanel          JPanel
     */
    private void getLogLevel(FormBuilder mainFormBuilder, JSONArray arrayList, JPanel jPanel) {
        currentLevel = javaPerfSettingAction.getLogLevel();
        level = currentLevel;
        levelTip = new JLabel(JavaProviderSettingConstant.TERM_RUNLOGLEVEL);
        List<String> testList = new ArrayList<>();
        String[] listData;
        if (arrayList == null) {
            listData = new String[]{"ERROR", "WARNING", "INFO", "DEBUG"};
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                testList.add(arrayList.get(i).toString());
            }
            listData = testList.toArray(new String[testList.size()]);
        }
        ComboBoxModel<String> model = new DefaultComboBoxModel(listData);
        comboBox = new ComboBox();
        comboBox.setModel(model);
        JPanel comboBoxPanel = new JPanel(new BorderLayout());
        comboBoxPanel.add(comboBox);
        for (int i = 0; i < listData.length; i++) {
            if (listData[i].equals(level)) {
                // 设置默认选中的条目
                comboBox.setSelectedIndex(i);
                break;
            }
        }
        JLabel label2 = new JLabel();
        label2.setIcon(BaseIntellijIcons.Settings.CERT_TIPS);
        label2.setPreferredSize(new Dimension(42, 32));
        comboBoxPanel.add(label2, BorderLayout.EAST);
        label2.setToolTipText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaPerf_log_tips"));
        mainFormBuilder
                .addLabeledComponent(levelTip, comboBoxPanel)
                .addLabeledComponent(new JLabel(JavaProviderSettingConstant.STACK_DEPTH), jPanel);
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
        registerComponentAction();
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new JavaPerfSettingAction();
        }
        comboBox.addItemListener(
                event -> {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        if (comboBox.getSelectedItem() instanceof String) {
                            level = (String) comboBox.getSelectedItem();
                        }
                    }
                });
    }

    /**
     * 修改内部通信证书自动告警时间（天）
     *
     * @param limit 最大数
     * @return 结果
     */
    private boolean changeLimit(String limit) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject object = new JSONObject();
        object.put("earlyWarningDays", limit);
        javaPerfSettingAction.changEarlyWarningDays(
                object,
                (data) -> {
                    ResponseBean res = (ResponseBean) data;
                    Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(res.getData());
                    String result = "";
                    if (jsonMessage.get("result") instanceof String) {
                        result = (String) jsonMessage.get("result");
                    }
                    switch (result) {
                        case "success":
                            notifyInfo(
                                    JavaProviderSettingConstant.MAX_ALARM_DAYS,
                                    MESSAGE_SUCESS,
                                    NotificationType.INFORMATION);
                            succeeded.set(true);
                            break;
                        default:
                            notifyInfo(
                                    JavaProviderSettingConstant.MAX_ALARM_DAYS, MESSAGE_FAILD, NotificationType.ERROR);
                            userLimitField.setText(userLimit.toString());
                            break;
                    }
                });
        return succeeded.get();
    }

    private void notifyInfo(String name, String value, NotificationType type) {
        NotificationBean notificationBean = new NotificationBean(name, value, type);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    /**
     * 修改栈深度配置
     *
     * @param stack 栈深度配置
     * @return 结果
     */
    private boolean changeTime(String stack) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        javaPerfSettingAction.changeStack(
                stack,
                (data) -> {
                    if (data instanceof ResponseBean) {
                        ResponseBean res = (ResponseBean) data;
                        switch (res.getCode()) {
                            case "0":
                                notifyInfo(
                                        JavaProviderSettingConstant.STACK_DEPTH,
                                        MESSAGE_SUCESS,
                                        NotificationType.INFORMATION);
                                succeeded.set(true);
                                break;
                            default:
                                notifyInfo(
                                        JavaProviderSettingConstant.STACK_DEPTH, MESSAGE_FAILD, NotificationType.ERROR);
                                userTimeField.setText(userTime.toString());
                                break;
                        }
                    }
                });
        return succeeded.get();
    }
}
