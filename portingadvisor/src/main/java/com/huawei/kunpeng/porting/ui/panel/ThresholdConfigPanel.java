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

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.threshold.ThresholdConfigAction;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedFunctionPageEditor;

import com.intellij.notification.NotificationType;
import com.intellij.ui.components.fields.IntegerField;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 阈值设置配置面板
 *
 * @since v1.0
 */
public class ThresholdConfigPanel extends IDEBasePanel {
    /**
     * 历史最小值属性
     */
    private static final String NUM_SAFE = "safenums";

    /**
     * 历史最大值属性
     */
    private static final String NUM_DANGEROUS = "dangerousnums";

    /**
     * 报告最小值
     */
    private static final Integer MIN_REPORT_VALUE = 1;

    /**
     * 报告最大值
     */
    private static final Integer MAX_REPORT_VALUE = 50;

    /**
     * 主面板
     */
    private JPanel mainPanel;

    /**
     * icon图标
     */
    private JLabel iconLabel;

    /**
     * 阈值设置描述面板
     */
    private JPanel descriptionPanel;

    /**
     * 最小值Label
     */
    private JLabel minNumLabel;

    /**
     * 最小值提示面板
     */
    private JPanel minNumPanel;

    /**
     * 最大值面板
     */
    private JPanel maxNumPanel;

    /**
     * 最大值Label
     */
    private JLabel maxNumLabel;

    /**
     * 最小值文本框
     */
    private IntegerField minNumField;

    /**
     * 最大值文本框
     */
    private IntegerField maxNumField;

    /**
     * 阈值描述
     */
    private JPanel desPanel;
    private JEditorPane desEditorPanel;

    /**
     * 最小值提示描述
     */
    private JPanel minDesPanel;
    private JEditorPane minDesEditorPanel;

    /**
     * 最大值提示描述
     */
    private JPanel maxDesPanel;
    private JEditorPane maxDesEditorPanel;


    /**
     * 阈值设置动作类
     */
    private ThresholdConfigAction thresholdConfigAction;

    /**
     * 历史最小阈值
     */
    private int safeNum;

    /**
     * 历史最大阈值
     */
    private int dangerousNum;

    /**
     * 构造方法
     */
    public ThresholdConfigPanel() {
        // 注册监听事件
        registerComponentAction();

        // 初始化面板
        initPanel();
    }

    private void initPanel() {
        int width = 100;
        if (I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            width = 180;
        }
        addEditorPanel(I18NServer.toLocale("plugins_common_porting_settings_threshold_reportWarn"), minNumLabel,
                desPanel, desEditorPanel);
        addEditorPanel(I18NServer.toLocale("plugins_common_porting_settings_threshold_minDes"), minNumLabel,
                minDesPanel, minDesEditorPanel);
        addEditorPanel(I18NServer.toLocale("plugins_common_porting_settings_threshold_maxDes"), minNumLabel,
                maxDesPanel, maxDesEditorPanel);
        mainPanel.updateUI();

        minNumLabel.setText(I18NServer.toLocale("plugins_common_porting_settings_threshold_minNum"));
        minNumLabel.setPreferredSize(new Dimension(width, 30));
        minNumField.setValue(safeNum);
        maxNumLabel.setText(I18NServer.toLocale("plugins_common_porting_settings_threshold_maxNum"));
        maxNumLabel.setPreferredSize(new Dimension(width, 30));
        maxNumField.setValue(dangerousNum);
    }

    /**
     * 获取主面板
     *
     * @return 主面板
     */
    public JPanel getConfPanel() {
        return mainPanel;
    }

    /**
     * 重置阈值设置
     */
    public void resetConf() {
        if (safeNum != minNumField.getValue() || dangerousNum != maxNumField.getValue()) {
            minNumField.setValue(safeNum);
            maxNumField.setValue(dangerousNum);
        }
    }

    /**
     * apply按钮添加事件，保存阈值设置
     */
    public void applyAndSaveConf() {
        boolean nonCompliance = checkFieldValue(minNumField.getValue(), maxNumField.getValue());
        if (nonCompliance) {
            return;
        }
        ResponseBean responseBean = thresholdConfigAction.saveConfig(minNumField.getValue(), maxNumField.getValue());
        String message = null;
        NotificationType notificationType = NotificationType.INFORMATION;
        if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            message = I18NServer.toLocale("plugins_common_porting_settings_threshold_modify_success");
            safeNum = minNumField.getValue();
            dangerousNum = maxNumField.getValue();
        } else {
            notificationType = NotificationType.ERROR;
            message = CommonUtil.getRspTipInfo(responseBean);
        }

        IDENotificationUtil.notificationCommon(new NotificationBean("", message, notificationType));
        // reload page for update report history lists
        if (EnhancedFunctionPageEditor.isPageOpen()) {
            EnhancedFunctionPageEditor.closePage();
            EnhancedFunctionPageEditor.openPage();
        }
    }

    /**
     * 阈值校验
     *
     * @param minNum 最小值
     * @param maxNum 最大值
     * @return 是否非法阈值区间
     */
    private boolean checkFieldValue(int minNum, int maxNum) {
        if (safeNum == minNum && dangerousNum == maxNum) {
            return true;
        }
        return checkModifiedValue(minNum, maxNum);
    }

    /**
     * 校验输入的阈值是否合法
     *
     * @param minNum 最小值
     * @param maxNum 最大值
     * @return 输入的阈值是否合规
     */
    private boolean checkModifiedValue(int minNum, int maxNum) {
        // 最小值警告
        if (minNum < MIN_REPORT_VALUE || minNum >= MAX_REPORT_VALUE) {
            maxNumField.setToolTipText(I18NServer.toLocale("plugins_common_porting_settings_threshold_max_error"));
            minNumField.setToolTipText(I18NServer.toLocale("plugins_common_porting_settings_threshold_min_error"));
            return true;
        }
        // 最大值警告
        if (maxNum > MAX_REPORT_VALUE || maxNum <= MIN_REPORT_VALUE) {
            minNumField.setToolTipText(I18NServer.toLocale("plugins_common_porting_settings_threshold_min_error"));
            maxNumField.setToolTipText(I18NServer.toLocale("plugins_common_porting_settings_threshold_max_error"));
            return true;
        }
        return false;
    }

    /**
     * 简单校验阈值，如果两个阈值同时等于数据库值则按钮禁用
     *
     * @return 阈值是否合规
     */
    public boolean checkFieldValue() {
        return minNumField.getValue() == safeNum && maxNumField.getValue() == dangerousNum;
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            thresholdConfigAction = new ThresholdConfigAction();
        }
        ResponseBean responseBean = thresholdConfigAction.queryReportMask();
        if (responseBean != null && RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            Map<String, Object> map = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            if (map.get(NUM_SAFE) instanceof Integer) {
                safeNum = (Integer) map.get(NUM_SAFE);
            }
            if (map.get(NUM_DANGEROUS) instanceof Integer) {
                dangerousNum = (Integer) map.get(NUM_DANGEROUS);
            }
        }
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}
