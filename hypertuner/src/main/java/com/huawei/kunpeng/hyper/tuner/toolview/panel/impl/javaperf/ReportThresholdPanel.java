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

import com.huawei.kunpeng.hyper.tuner.action.javaperf.ReportThresholdAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.SysSettingManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.ReportThresholdConstant;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * The class ReportThresholdPanel
 *
 * @since v2.2.T4
 */
public class ReportThresholdPanel extends IDEBasePanel {
    /**
     * 采样报告提示阈值最小数限制。
     */
    private static final int SAMPLING_LIMIT_MIN_NUM = 1;

    /**
     * 采样报告提示阈值最大数限制。
     */
    private static final int SAMPLING_LIMIT_MAX_NUM = 20;

    /**
     * 采样报告最大阈值最小数限制。
     */
    private static final int REPORT_MAXIMUM_MIN_NUM = 1;

    /**
     * 采样报告最大阈值最大数限制。
     */
    private static final int REPORT_MAXIMUM_MAX_NUM = 20;

    /**
     * 历史报告閾值最小值
     */
    private static final int MIN = 1;

    /**
     * 历史报告閾值最大值
     */
    private static final int MAX = 10;

    /**
     * 导入报告最小阈值
     */
    private static final int IMPORT_SIZE_MIN = 1;

    /**
     * 导入报告最大阈值
     */
    private static final int IMPORT_SIZE_MAX = 2048;

    private static final String MESSAGE_SUCESS = SysSettingManageConstant.UPDATE_SUCESS;

    private static final String MESSAGE_FAILD = SysSettingManageConstant.UPDATE_FAILD;

    private static final long serialVersionUID = -7492589440302113818L;

    /**
     * 主面板
     */
    private JPanel mainPanel;

    /**
     * 配置类
     */
    private JSONObject samplingLimitConfig;

    private JSONObject threadHistoryConfig;

    private JSONObject memHistoryConfig;

    private JSONObject gclogConfig;

    private Integer samplingLimit;

    private Integer reportMaximum;

    private Integer threadHistoryReports;

    private Integer threadHistoryMax;

    private Integer memHistoryReports;

    private Integer memHistoryMax;

    private Integer importSize;

    private Integer GCHistoryReports;

    private Integer GCHistoryMax;

    private IntegerField samplingLimitField;

    private IntegerField reportMaximumField;

    private IntegerField threadHistoryReportsField;

    private IntegerField threadHistoryMaxField;

    private IntegerField memHistoryReportsField;

    private IntegerField memHistoryMaxField;

    private IntegerField importSizeField;

    private IntegerField gcHistoryReportsField;

    private IntegerField gcHistoryMaxField;

    private ReportThresholdAction reportThresholdAction;

    /**
     * 构造函数
     */
    public ReportThresholdPanel() {
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
        return isThresholdChange();
    }

    /**
     * 报告阈值状态是否改变。
     *
     * @return 报告阈值状态是否改变。
     */
    private boolean isThresholdChange() {
        String guiSamplingLimit = samplingLimitField.getText();
        String guiReportMaximum = reportMaximumField.getText();
        String guiThreadHistoryReports = threadHistoryReportsField.getText();
        String guiThreadHistoryMax = threadHistoryMaxField.getText();
        String guiMemHistoryReports = memHistoryReportsField.getText();
        String guiMemHistoryMax = memHistoryMaxField.getText();
        String guiImportSize = importSizeField.getText();
        String guiGCHistoryReports = gcHistoryReportsField.getText();
        String guiGCHistoryMax = gcHistoryMaxField.getText();

        boolean isThresholdChange =
                !Objects.equals(samplingLimit.toString(), guiSamplingLimit)
                        || !Objects.equals(reportMaximum.toString(), guiReportMaximum)
                        || !Objects.equals(threadHistoryReports.toString(), guiThreadHistoryReports)
                        || !Objects.equals(threadHistoryMax.toString(), guiThreadHistoryMax)
                        || !Objects.equals(memHistoryReports.toString(), guiMemHistoryReports)
                        || !Objects.equals(memHistoryMax.toString(), guiMemHistoryMax)
                        || !Objects.equals(importSize.toString(), guiImportSize)
                        || !Objects.equals(GCHistoryReports.toString(), guiGCHistoryReports)
                        || !Objects.equals(GCHistoryMax.toString(), guiGCHistoryMax);
        return isThresholdChange;
    }

    /**
     * 事件处理。
     *
     * @throws ConfigurationException 配置异常。
     */
    public void apply() throws ConfigurationException {
        samplingLimitField.validateContent();
        reportMaximumField.validateContent();
        threadHistoryReportsField.validateContent();
        threadHistoryMaxField.validateContent();
        memHistoryReportsField.validateContent();
        memHistoryMaxField.validateContent();
        importSizeField.validateContent();
        gcHistoryReportsField.validateContent();
        gcHistoryMaxField.validateContent();
        String samplingLimitStr = samplingLimitField.getText();
        String reportMaxStr = reportMaximumField.getText();
        String threadHistoryReportsStr = threadHistoryReportsField.getText();
        String threadHistoryMaxStr = threadHistoryMaxField.getText();
        String memHistoryReportsStr = memHistoryReportsField.getText();
        String memHistoryMaxStr = memHistoryMaxField.getText();
        String importSizeStr = importSizeField.getText();
        String gcHistoryReportsStr = gcHistoryReportsField.getText();
        String gcHistoryMaxStr = gcHistoryMaxField.getText();
        // 修改采样报告提示阈值
        if (!samplingLimitStr.equals(samplingLimit.toString()) || !reportMaxStr.equals(reportMaximum.toString())) {
            if (changeSamplingLimit(samplingLimitStr, reportMaxStr)) {
                samplingLimit = Integer.valueOf(samplingLimitStr);
                reportMaximum = Integer.valueOf(reportMaxStr);
            }
        }
        // 修改线程转储历史报告提示阈值
        if (!threadHistoryReportsStr.equals(threadHistoryReports.toString())
                || !threadHistoryMaxStr.equals(threadHistoryMax.toString())) {
            if (changeThreadHistory(threadHistoryReportsStr, threadHistoryMaxStr)) {
                threadHistoryReports = Integer.valueOf(threadHistoryReportsStr);
                threadHistoryMax = Integer.valueOf(threadHistoryMaxStr);
            }
        }
        // 修改内存转储历史报告提示阈值
        if (!memHistoryReportsStr.equals(memHistoryReports.toString())
                || !memHistoryMaxStr.equals(memHistoryMax.toString())
                || !importSizeStr.equals(importSize.toString())) {
            if (changeMemHistory(memHistoryReportsStr, memHistoryMaxStr, importSizeStr)) {
                threadHistoryReports = Integer.valueOf(memHistoryReportsStr);
                memHistoryMax = Integer.valueOf(memHistoryMaxStr);
                importSize = Integer.valueOf(importSizeStr);
            }
        }
        // 修改GC日志历史报告提示阈值
        if (!gcHistoryReportsStr.equals(GCHistoryReports.toString())
                || !gcHistoryMaxStr.equals(GCHistoryMax.toString())) {
            if (changeGClog(gcHistoryReportsStr, gcHistoryMaxStr)) {
                GCHistoryReports = Integer.valueOf(gcHistoryReportsStr);
                GCHistoryMax = Integer.valueOf(gcHistoryMaxStr);
            }
        }
    }

    /**
     * 重置界面方法
     */
    public void reset() {
        samplingLimitConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/report/");
        threadHistoryConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/threadDump/");
        memHistoryConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/heap/");
        gclogConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/gcLog/");

        // 设置初始值
        samplingLimit = Integer.valueOf(samplingLimitConfig.getString("alarmJFRCount"));
        samplingLimitField.setText(samplingLimit.toString());
        reportMaximum = Integer.valueOf(samplingLimitConfig.getString("maxJFRCount"));
        reportMaximumField.setText(reportMaximum.toString());
        threadHistoryReports = Integer.valueOf(threadHistoryConfig.getString("alarmThreadDumpCount"));
        threadHistoryReportsField.setText(threadHistoryReports.toString());
        threadHistoryMax = Integer.valueOf(threadHistoryConfig.getString("maxThreadDumpCount"));
        threadHistoryMaxField.setText(threadHistoryMax.toString());
        memHistoryReports = Integer.valueOf(memHistoryConfig.getString("alarmHeapCount"));
        memHistoryReportsField.setText(memHistoryReports.toString());
        memHistoryMax = Integer.valueOf(memHistoryConfig.getString("maxHeapCount"));
        memHistoryMaxField.setText(memHistoryMax.toString());
        importSize = Integer.valueOf(memHistoryConfig.getString("maxHeapSize"));
        importSizeField.setText(importSize.toString());
        GCHistoryReports = Integer.valueOf(gclogConfig.getString("alarmGcLogCount"));
        gcHistoryReportsField.setText(GCHistoryReports.toString());
        GCHistoryMax = Integer.valueOf(gclogConfig.getString("maxGcLogCount"));
        gcHistoryMaxField.setText(GCHistoryMax.toString());
    }

    /**
     * 初始化面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        if (reportThresholdAction == null) {
            reportThresholdAction = new ReportThresholdAction();
        }
        samplingLimitConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/report/");
        threadHistoryConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/threadDump/");
        memHistoryConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/heap/");
        gclogConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/gcLog/");

        JPanel samplingLimitJPanel = setSamplingLimit();
        JPanel reportMaximumJPanel = setReportMaximum();
        JPanel historicalReportsJPanel = setThreadHistoryReports();
        JPanel historyMaxJPanel = setThreadHistoryMax();
        JPanel memHistoryReportsJPanel = setMemHistoryReports();
        JPanel memHistoryMaxJPanel = setMemHistoryMax();
        JPanel importSizeJPanel = setImportSize();
        JPanel gcHistoryReportsJPanel = setGCHistoryReports();
        JPanel gcHistoryMaxJPanel = setGCHistoryMax();

        // 往表单添加元素
        JLabel label = new JLabel("               ");
        FormBuilder reportFormBuilder =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.SAMPLING_ANALYSIS), new JSeparator())
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.REPORT_WARNING), samplingLimitJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.REPORT_WARNING_TIP), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.REPORT_MAXIMUM), reportMaximumJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.REPORT_MAXIMUM_TIP), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.THREAD_DUMP), new JSeparator(), 20)
                        .addLabeledComponent(
                                new JLabel(ReportThresholdConstant.WARNING_THRESHOLD), historicalReportsJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.WARNING_THRESHOLD_THREAD), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.HISTORY_MAX), historyMaxJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.HISTORY_MAX_THREAD), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.MEMORY_DUMP), new JSeparator(), 20)
                        .addLabeledComponent(
                                new JLabel(ReportThresholdConstant.WARNING_THRESHOLD), memHistoryReportsJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.WARNING_THRESHOLD_MEM), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.HISTORY_MAX), memHistoryMaxJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.HISTORY_MAX_MEM), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.IMPORT_SIZE), importSizeJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.IMPORT_SIZE_TIPS), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.GC_LOGS), new JSeparator(), 20)
                        .addLabeledComponent(
                                new JLabel(ReportThresholdConstant.WARNING_THRESHOLD), gcHistoryReportsJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.WARNING_THRESHOLD_GC), -2)
                        .addLabeledComponent(new JLabel(ReportThresholdConstant.HISTORY_MAX), gcHistoryMaxJPanel)
                        .addLabeledComponent(label, new JLabel(ReportThresholdConstant.HISTORY_MAX_GC), -2);
        mainPanel = reportFormBuilder.addComponentFillVertically(panel, 0).getPanel();
    }

    private IntegerField getIntegerField(HashMap<String, String> paramMap, int min, int max, JSONObject JsonObj) {
        // 采样报告提示阈值
        IntegerField integerField = new IntegerField(paramMap.get("title"), min, max);
        integerField.setCanBeEmpty(false);
        integerField.setToolTipText(paramMap.get("toolTipText"));
        integerField.setText(JsonObj.getString(paramMap.get("key")));
        return integerField;
    }

    private JPanel getReportPanel(IntegerField reportField, String tips) {
        FlowLayout layout = new FlowLayout(FlowLayout.LEADING);
        JPanel samplingLimitPanel = new JPanel(layout);
        samplingLimitPanel.add(reportField);
        JLabel tipLabel = new JLabel(tips);
        reportField.setPreferredSize(new Dimension(600, 32));
        samplingLimitPanel.add(tipLabel);
        return samplingLimitPanel;
    }

    private JPanel setSamplingLimit() {
        // 采样报告提示阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.REPORT_WARNING);
        paramMap.put("key", "alarmJFRCount");
        paramMap.put("toolTipText", SysSettingManageConstant.TIP_NUM_MODIFY);
        samplingLimitField =
                getIntegerField(paramMap, SAMPLING_LIMIT_MIN_NUM, SAMPLING_LIMIT_MAX_NUM, samplingLimitConfig);
        return getReportPanel(samplingLimitField, "(1-20)");
    }

    private JPanel setReportMaximum() {
        // 采样报告最大阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.REPORT_MAXIMUM);
        paramMap.put("key", "maxJFRCount");
        paramMap.put("toolTipText", SysSettingManageConstant.TIP_NUM_MODIFY);
        reportMaximumField =
                getIntegerField(paramMap, SAMPLING_LIMIT_MIN_NUM, SAMPLING_LIMIT_MAX_NUM, samplingLimitConfig);
        return getReportPanel(reportMaximumField, "(1-20)");
    }

    private JPanel setThreadHistoryReports() {
        // 线程转储历史报告提示阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.WARNING_THRESHOLD);
        paramMap.put("key", "alarmThreadDumpCount");
        paramMap.put("toolTipText", ReportThresholdConstant.WARNING_THRESHOLD_TIP);
        threadHistoryReportsField = getIntegerField(paramMap, MIN, MAX, threadHistoryConfig);
        return getReportPanel(threadHistoryReportsField, "(1-10)");
    }

    private JPanel setThreadHistoryMax() {
        // 线程转储历史报告最大阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.HISTORY_MAX);
        paramMap.put("key", "maxThreadDumpCount");
        paramMap.put("toolTipText", ReportThresholdConstant.WARNING_THRESHOLD_TIP);
        threadHistoryMaxField = getIntegerField(paramMap, MIN, MAX, threadHistoryConfig);
        return getReportPanel(threadHistoryMaxField, "(1-10)");
    }

    private JPanel setMemHistoryReports() {
        // 内存转储历史报告提示阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.WARNING_THRESHOLD);
        paramMap.put("key", "alarmHeapCount");
        paramMap.put("toolTipText", ReportThresholdConstant.WARNING_THRESHOLD_TIP);
        memHistoryReportsField = getIntegerField(paramMap, MIN, MAX, memHistoryConfig);
        return getReportPanel(memHistoryReportsField, "(1-10)");
    }

    private JPanel setMemHistoryMax() {
        // 内存转储历史报告最大阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.HISTORY_MAX);
        paramMap.put("key", "maxHeapCount");
        paramMap.put("toolTipText", ReportThresholdConstant.WARNING_THRESHOLD_TIP);
        memHistoryMaxField = getIntegerField(paramMap, MIN, MAX, memHistoryConfig);
        return getReportPanel(memHistoryMaxField, "(1-10)");
    }

    private JPanel setImportSize() {
        // 导入报告大小阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.IMPORT_SIZE);
        paramMap.put("key", "maxHeapSize");
        paramMap.put("toolTipText", ReportThresholdConstant.IMPORT_SIZE_TIP);
        importSizeField = getIntegerField(paramMap, IMPORT_SIZE_MIN, IMPORT_SIZE_MAX, memHistoryConfig);
        return getReportPanel(importSizeField, "(1-2048)");
    }

    private JPanel setGCHistoryReports() {
        // GC日志历史报告提示阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.WARNING_THRESHOLD);
        paramMap.put("key", "alarmGcLogCount");
        paramMap.put("toolTipText", ReportThresholdConstant.WARNING_THRESHOLD_TIP);
        gcHistoryReportsField = getIntegerField(paramMap, MIN, MAX, gclogConfig);
        return getReportPanel(gcHistoryReportsField, "(1-10)");
    }

    private JPanel setGCHistoryMax() {
        // GC日志历史报告最大阈值
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("title", ReportThresholdConstant.HISTORY_MAX);
        paramMap.put("key", "alarmGcLogCount");
        paramMap.put("toolTipText", ReportThresholdConstant.WARNING_THRESHOLD_TIP);
        gcHistoryMaxField = getIntegerField(paramMap, MIN, MAX, gclogConfig);
        return getReportPanel(gcHistoryMaxField, "(1-10)");
    }

    private void isEdit() {
        if (Objects.equals(TuningUserManageConstant.USER_ROLE_USER, UserInfoContext.getInstance().getRole())) {
            samplingLimitField.setEditable(false);
            reportMaximumField.setEditable(false);
            threadHistoryReportsField.setEditable(false);
            threadHistoryMaxField.setEditable(false);
            memHistoryReportsField.setEditable(false);
            memHistoryMaxField.setEditable(false);
            importSizeField.setEditable(false);
            gcHistoryReportsField.setEditable(false);
            gcHistoryMaxField.setEditable(false);
        }
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
    }

    private void notifyInfo(String name, String value, NotificationType type) {
        NotificationBean notificationBean = new NotificationBean(name, value, type);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    private void message(Object data, String title) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        if (data instanceof ResponseBean) {
            ResponseBean res = (ResponseBean) data;
            switch (res.getCode()) {
                case "0":
                    notifyInfo(title, MESSAGE_SUCESS, NotificationType.INFORMATION);
                    succeeded.set(true);
                    break;
                default:
                    String msg = res.getMessage();
                    if (msg.contains("maxJFRCount")) {
                        msg =
                                msg.replaceFirst("maxJFRCount", ReportThresholdConstant.MAX_JFR_COUNT)
                                        .replaceFirst("alarmJFRCount", ReportThresholdConstant.ALARM_JFR_COUNT);
                    }
                    notifyInfo(title, msg, NotificationType.ERROR);
                    break;
            }
        }
    }

    /**
     * 修改采样分析报告阈值
     *
     * @param samplingData    传入提示阈值数据
     * @param samplingDataMax 传入最大阈值数据
     * @return 结果
     */
    private boolean changeSamplingLimit(String samplingData, String samplingDataMax) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject object = new JSONObject();
        object.put("alarmJFRCount", samplingData);
        object.put("maxJFRCount", samplingDataMax);
        reportThresholdAction.changeReportConfig(
                "java-perf/api/tools/settings/report/",
                object,
                (data) -> {
                    message(data, ReportThresholdConstant.SAMPLING_ANALYSIS);
                });
        return succeeded.get();
    }

    /**
     * 修改线程转储阈值数据
     *
     * @param threadData    传入线程转储提示阈值数据
     * @param threadDataMax 传入线程转储最大阈值数据
     * @return 结果
     */
    private boolean changeThreadHistory(String threadData, String threadDataMax) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject object = new JSONObject();
        object.put("maxThreadDumpCount", threadDataMax);
        object.put("alarmThreadDumpCount", threadData);
        reportThresholdAction.changeReportConfig(
                "java-perf/api/tools/settings/threadDump/",
                object,
                (data) -> {
                    message(data, ReportThresholdConstant.THREAD_DUMP);
                });
        return succeeded.get();
    }

    /**
     * 修改内存转储阈值
     *
     * @param memData     传入内存转储提示阈值数据
     * @param memDataMax  传入内存转储最大阈值数据
     * @param memSizeData 传入导入文件大小阈值数据
     * @return 结果
     */
    private boolean changeMemHistory(String memData, String memDataMax, String memSizeData) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject object = new JSONObject();
        object.put("maxHeapCount", memDataMax);
        object.put("alarmHeapCount", memData);
        object.put("maxHeapSize", memSizeData);
        reportThresholdAction.changeReportConfig(
                "java-perf/api/tools/settings/heap/",
                object,
                (data) -> {
                    message(data, ReportThresholdConstant.MEMORY_DUMP);
                });
        return succeeded.get();
    }

    /**
     * 修改GC日志阈值
     *
     * @param gcData    GC日志报告提示阈值
     * @param gcDataMax GC日志报告最大阈值
     * @return 结果
     */
    private boolean changeGClog(String gcData, String gcDataMax) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject object = new JSONObject();
        object.put("maxGcLogCount", gcDataMax);
        object.put("alarmGcLogCount", gcData);
        reportThresholdAction.changeReportConfig(
                "java-perf/api/tools/settings/gcLog/",
                object,
                (data) -> {
                    message(data, ReportThresholdConstant.GC_LOGS);
                });
        return succeeded.get();
    }
}
