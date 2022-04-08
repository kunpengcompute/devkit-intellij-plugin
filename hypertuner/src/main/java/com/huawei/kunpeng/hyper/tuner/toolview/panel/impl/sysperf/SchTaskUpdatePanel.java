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

import com.huawei.kunpeng.hyper.tuner.action.sysperf.SchTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.SchTaskFormatUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskCommonFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.CppHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.FalseHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.HpcHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.IoHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.LockHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.MemAccessHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.MicroHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.MissEventHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.OverAllHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.ProcessHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.ResHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskPanelHandle;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import org.apache.commons.lang.StringUtils;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * 修改 预约任务 panel
 *
 * @since 2021-4-25
 */
public class SchTaskUpdatePanel extends IDEBasePanel {
    private final Integer panelWidth = 500;
    private final Integer panelHeight = 25;
    private final Integer label1Height = 25;
    private final Integer label1Width = 100;
    private final int taskId;
    private final SchTaskBean schTaskItem;
    private final JSONObject taskInfoJsonObj;
    private final HashMap<String, Integer> compIndexMap;
    private JPanel mainPanel;
    private JPanel taskNamePanel;
    private JPanel basicInfoPanel;
    private JPanel analysisTargetPanel;
    private JPanel analysisTypePanel;
    private JPanel processNamePanel;
    private JLabel taskNameLabel1;
    private JLabel taskNameLabel2;
    private JLabel analysisTarget1;
    private JLabel analysisTarget2;
    /**
     * 分析模式 （仅分析对象为应用时展示）
     */
    private JPanel analysisModePanel;
    private JLabel analysisModeLabel1;
    private JLabel analysisModeLabel2;
    private JLabel analysisType1;
    private JLabel analysisType2;
    private JLabel cycle; // 采集方式
    private JCheckBox onceCheckBox;
    private JCheckBox cycleCheckBox;
    private ButtonGroup cycleBtnGroup;
    private ButtonGroup targetParamBtnGroup;
    private JLabel targetTimeLabel;
    private JTextField targetTimeText;
    private JLabel cycleDateLabel;
    private JTextField appointmentText; // 单次采集预约时间
    private JTextField cycleStartText; // 周期采集开始时时间
    private JTextField cycleStopText; // 周期采集结束时间
    private JPanel datePanel;
    private JPanel cyclePanel;
    private JPanel appointPanel;
    private JPanel targetTimePanel;
    private JPanel appparamPanel;
    private JLabel appparamLabel;
    private JTextField appparamField;
    private JPanel appDirPanel;
    private JLabel appDirLabel;
    private JTextField appDirField;
    private JTextField processNameField;
    private boolean PIDSelected;
    private boolean processNameSelected;
    private JPanel PIDPanel;
    private JTextField PIDField;
    private JCheckBox processNameCheckBox;
    private JCheckBox PIDCheckBox;
    private JPanel appLaunchPanel;
    private JPanel appAttachPanel;
    private JPanel otherInfoPanel;
    private JPanel cyclePanel2;
    private JPanel scheduleInfoPanel;
    private JPanel memAnalysisPanel;
    private JLabel memAnalysisLabel1;
    private JLabel memAnalysisLabel2;

    private JScrollPane mainScrollPane;
    private JPanel allInfoPanel;
    /**
     * 获取多选框 选项国际化 和接口数据的映射，用于将用户选择的值转化为接口传递的值
     * 注意：当变量名重复时，不能在此处添加，应单独处理
     */
    private HashMap<String, String> selectN81IMap;
    /**
     * 任务信息 字段值 国际化Map
     */
    private HashMap<String, String> fileValueI18NMap;
    private String newProcessName;
    private String newPid;
    private String newAppDir;
    private String newAppParam;
    private String newTargetTime;
    private Boolean newCycleSampling;
    private String newCycleStart;
    private String newCycleStop;
    private String newAppointment;
    private boolean switchBtnValid = false;
    private int compIndex = 0;
    private SchTaskPanelHandle otherInfoHandle;

    public SchTaskUpdatePanel(Integer taskId) {
        this.taskId = taskId;
        SchTaskAction schTaskManageAction = new SchTaskAction();
        schTaskItem = schTaskManageAction.getSchTaskItem(taskId);
        taskInfoJsonObj = schTaskManageAction.getSchTaskInfoJSONObj(taskId);
        compIndexMap = new HashMap<>();
        initPanel(mainPanel); // 初始化面板
        registerComponentAction(); // 初始化面板内组件事件
        createContent(mainPanel, SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE, false); // 初始化content实例
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        return vi;
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        cycleBtnGroup = new ButtonGroup();
        cycleBtnGroup.add(onceCheckBox);
        cycleBtnGroup.add(cycleCheckBox);

        // 基础信息
        taskNameLabel1.setText(TaskManageContent.TASK_NAME);
        taskNameLabel2.setText(schTaskItem.getTaskName());
        analysisTarget1.setText(TaskManageContent.ANALYSIS_TARGET); // 分析对象
        String analysisTarget = SchTaskFormatUtil.analysisTargetFormat(schTaskItem.getTaskInfo().getAnalysisTarget());
        // 分析模式
        if (TaskManageContent.ANALYSIS_TARGET_APP.equals(analysisTarget)) {
            analysisModePanel.setVisible(true);
            analysisModeLabel1.setText(TaskManageContent.ANALYSIS_MODE);
            analysisModeLabel2.setText(schTaskItem.getTaskInfo().getAnalysisTarget());
        } else {
            analysisModePanel.setVisible(false);
        }
        analysisTarget2.setText(analysisTarget);
        analysisType1.setText(TaskManageContent.ANALYSIS_TYPE); // 分析类型
        String analysisType = SchTaskFormatUtil.analysisTypeFormat(schTaskItem.getAnalysisType());
        analysisType2.setText(analysisType);

        // 访存分析类型字段（仅访存分析类型存在），在setAnalysisTypeInfoShow（） 中设置
        memAnalysisPanel.setVisible(false);
        // 设置其他信息
        setAnalysisTypeInfoShow();

        // 预约信息
        cycle.setText(TaskManageContent.PARAM_OPERATE_TYPE); // 采集方式
        cycleDateLabel.setText(TaskManageContent.PARAM_SAMPLE_DATE); // 采集日期
        onceCheckBox.setText(TaskManageContent.PARAM_OPERATE_TYPE_ONCE);
        cycleCheckBox.setText(TaskManageContent.PARAM_OPERATE_TYPE_CYCLE);
        setDateShow();
        targetTimeLabel.setText(TaskManageContent.PARAM_TARGET_TIME); // 采集时间
        targetTimeText.setText(schTaskItem.getTargetTime());
        addListener();
    }

    /**
     * 设置分析对象相关的信息
     *
     * @param target      分析对象
     * @param dir         应用目录
     * @param param       应用参数
     * @param processName 进程名称
     * @param pid         pid
     */
    private void setAnalysisTargetInfoShow(String target, String dir, String param, String processName, String pid) {
        appAttachPanel.setVisible(false);
        appLaunchPanel.setVisible(false);
        switch (target) {
            case TaskManageContent.TARGET_APP_LAUNCH_APPLICATION:
                setAppLaunchInfoShow(dir, param);
                appLaunchPanel.setVisible(true);
                break;
            case TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS:
                setAppAttachInfoShow(processName, pid);
                appAttachPanel.setVisible(true);
                break;
            case TaskManageContent.TARGET_APP_PROFILE_SYSTEM:
                // $FALL-THROUGH$
            case TaskManageContent.TARGET_APP_SYSTEM:
                break;
            default:
                break;
        }
    }

    /**
     * 设置 App Launch 相关的信息
     *
     * @param appDir   应用目录
     * @param appParam 应用参数
     */
    private void setAppLaunchInfoShow(String appDir, String appParam) {
        // 应用路径
        appDirLabel.setText(TaskManageContent.PARAM_APPLICATION_PATH);
        appDirField.setText(appDir);

        // 应用参数
        appparamLabel.setText(TaskManageContent.PARAM_APPLICATION_PARAM);
        appparamField.setText(appParam);
    }

    /**
     * 设置 App Attach 相关的信息
     *
     * @param processName 进程名称
     * @param pid         pid
     */
    private void setAppAttachInfoShow(String processName, String pid) {
        // 进程名
        processNameCheckBox.setText(TaskManageContent.PARAM_PROCESS_NAME);
        if (StringUtil.stringIsEmpty(processName)) {
            processNameField.setEditable(false);
            processNameCheckBox.setSelected(false);
            processNameSelected = false;
        } else {
            processNameCheckBox.setSelected(true);
            processNameSelected = true;
            processNameField.setText(processName);
        }
        // PID
        PIDCheckBox.setText(TaskManageContent.PARAM_PID);
        if (StringUtil.stringIsEmpty(pid)) {
            PIDField.setEditable(false);
            PIDCheckBox.setSelected(false);
            PIDSelected = false;
        } else {
            PIDCheckBox.setSelected(true);
            PIDSelected = true;
            PIDField.setText(pid);
        }
    }

    private void setAnalysisTypeInfoShow() {
        GridLayout gridLayout = new GridLayout();
        otherInfoPanel.setLayout(gridLayout);
        otherInfoPanel.setPreferredSize(new Dimension(500, -1));
        otherInfoPanel.setBorder(null);
        fileValueI18NMap = TaskCommonFormatUtil.getTaskInfoParamValueI18NMap();
        selectN81IMap = TaskCommonFormatUtil.getSelectMap();
        String analysisType = schTaskItem.getAnalysisType();
        switch (analysisType) {
            // 全景分析
            case "system":
                overAllShow();
                break;
            case "microarchitecture":
                microShow();
                break;
            case "mem_access":
                memAccessShow();
                break;
            case "miss_event":
                missEventShow();
                break;
            case "falsesharing":
                falseSharingShow();
                break;
            case "ioperformance":
                ioperformanceShow();
                break;
            case "process-thread-analysis":
                processShow();
                break;
            case "C/C++ Program":
                cppShow();
                break;
            case "resource_schedule":
                resSchShow();
                break;
            case "system_lock":
                lockShow();
                break;
            case "hpc_analysis":
                hpcShow();
                break;
            default:
        }
        otherInfoHandle.addPanel();
        int count = otherInfoPanel.getComponentCount();
        gridLayout.setRows(count); // 设置行数
        gridLayout.setColumns(1); // 设置列数
        gridLayout.setVgap(8); // 设置竖直间隙
    }

    private void overAllShow() {
        appAttachPanel.setVisible(false);
        appLaunchPanel.setVisible(false);
        otherInfoHandle = new OverAllHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void microShow() {
        setAppInfo2();
        otherInfoHandle = new MicroHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void memAccessShow() {
        setAppInfo();
        memAnalysisPanel.setVisible(true);
        this.memAnalysisLabel1.setText(TaskManageContent.ACCESS_ANALYSIS_TYPE);
        this.memAnalysisLabel2.setText(TaskManageContent.ANALYSIS_TYPE_MEM_ACCESS_STATISTICS);
        otherInfoHandle = new MemAccessHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void missEventShow() {
        String analysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        String app = taskInfoJsonObj.getJSONObject("task_param").getString("app"); // 应用路径
        String appArgs = taskInfoJsonObj.getJSONObject("task_param").getString("appArgs"); // 应用参数
        String processName = taskInfoJsonObj.getJSONObject("task_param").getString("process_name"); // 进程名
        String pid = taskInfoJsonObj.getJSONObject("task_param").getString("pid"); // PID
        setAnalysisTargetInfoShow(analysisTarget, app, appArgs, processName, pid); // miss Event
        memAnalysisPanel.setVisible(true);
        this.memAnalysisLabel1.setText(TaskManageContent.ACCESS_ANALYSIS_TYPE);
        this.memAnalysisLabel2.setText(TaskManageContent.ANALYSIS_TYPE_MISS_EVENT);
        otherInfoHandle = new MissEventHandle(otherInfoPanel, taskInfoJsonObj, compIndexMap);
        otherInfoHandle.setSchTaskItem(schTaskItem);
        otherInfoHandle.generateSchItemShowList();
    }

    private void falseSharingShow() {
        setAppInfo();
        memAnalysisPanel.setVisible(true);
        this.memAnalysisLabel1.setText(TaskManageContent.ACCESS_ANALYSIS_TYPE);
        this.memAnalysisLabel2.setText(TaskManageContent.ANALYSIS_TYPE_FALSE_SHARE);
        otherInfoHandle = new FalseHandle(otherInfoPanel, schTaskItem, taskInfoJsonObj, compIndexMap);
    }

    private void ioperformanceShow() {
        setAppInfo2();
        otherInfoHandle = new IoHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void processShow() {
        setAppInfo();
        otherInfoHandle = new ProcessHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void resSchShow() {
        setAppInfo2();
        otherInfoHandle = new ResHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void cppShow() {
        setAppInfo2();
        String targetPid = schTaskItem.getTaskInfo().getTargetPid();
        if (StringUtil.stringIsEmpty(targetPid)) {
            PIDField.setEditable(false);
            PIDCheckBox.setSelected(false);
            PIDSelected = false;
        } else {
            PIDCheckBox.setSelected(true);
            PIDSelected = true;
            PIDField.setText(targetPid + "");
        }
        otherInfoHandle = new CppHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    /**
     * 锁与等待展示panel 函数
     */
    private void lockShow() {
        setAppInfo2();
        otherInfoHandle = new LockHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void hpcShow() {
        setAppInfo();
        otherInfoHandle = new HpcHandle(otherInfoPanel, schTaskItem, compIndexMap);
    }

    private void setAppInfo() {
        String analysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        String appDir = schTaskItem.getTaskInfo().getAppDir(); // 应用路径
        String appParam = schTaskItem.getTaskInfo().getAppParameters(); // 应用参数
        String processName = schTaskItem.getTaskInfo().getProcessName(); // 进程名
        String pid = schTaskItem.getTaskInfo().getPid(); // PID
        setAnalysisTargetInfoShow(analysisTarget, appDir, appParam, processName, pid);
    }

    private void setAppInfo2() {
        String analysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        String appDir = schTaskItem.getTaskInfo().getAppDir(); // 应用路径
        String appParam = schTaskItem.getTaskInfo().getAppParameters(); // 应用参数
        String processName = schTaskItem.getTaskInfo().getProcessName(); // 进程名
        String pid = schTaskItem.getTaskInfo().getTargetPid(); // PID
        setAnalysisTargetInfoShow(analysisTarget, appDir, appParam, processName, pid);
    }

    /**
     * 根据是否 周期采集，展示相应的组件
     */
    private void setDateShow() {
        if (schTaskItem.isCycle()) { // 采集方式为周期采集时。显示 开始结束日期  cycleStart -> 2021-4-9
            cycleCheckBox.setSelected(true);
            cycleStopText.setText(schTaskItem.getTaskInfo().getCycleStop());
            cycleStartText.setText(schTaskItem.getTaskInfo().getCycleStart());
            cyclePanel.setVisible(true);
            appointPanel.setVisible(false);
        } else { // 非周期采集时。显示预约时间
            onceCheckBox.setSelected(true);
            appointmentText.setText(schTaskItem.getTaskInfo().getAppointment());
            appointPanel.setVisible(true);
            cyclePanel.setVisible(false);
        }
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new SchTaskAction();
        }
    }

    /**
     * setAction
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof SchTaskAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * 当前修改的预约任务的ID
     *
     * @return ID
     */
    public int getCurrentSchTaskId() {
        return schTaskItem.getTaskId();
    }

    /**
     * 返回根据输入值更新后的任务 Json对象
     *
     * @return 任务对象
     */
    public JSONObject getNewSchTask() {
        newProcessName = processNameField.getText();
        newPid = PIDField.getText();
        newAppDir = appDirField.getText();
        newAppParam = appparamField.getText();
        newTargetTime = targetTimeText.getText();
        newCycleSampling = getTaskOperateType();
        newCycleStart = cycleStartText.getText();
        newCycleStop = cycleStopText.getText();
        newAppointment = appointmentText.getText();
        JSONObject jsonObject = this.taskInfoJsonObj;
        jsonObject.put("appointment", newAppointment);
        jsonObject.put("cycle", newCycleSampling);
        jsonObject.put("cycleStart", newCycleStart);
        jsonObject.put("cycleStop", newCycleStop);
        jsonObject.put("targetTime", newTargetTime);
        JSONArray nodeConfigJsonArr = taskInfoJsonObj.getJSONArray("nodeConfig");
        for (int i = 0; i < nodeConfigJsonArr.size(); i++) {
            JSONObject nodeConfigItemJsonObj = nodeConfigJsonArr.getJSONObject(i);
            // 微架构 等类型
            JSONObject taskParamObj = nodeConfigItemJsonObj.getJSONObject("taskParam");
            if (taskParamObj == null) {
                // 热点函数 等类型
                taskParamObj = nodeConfigItemJsonObj.getJSONObject("task_param");
            }
            taskParamObj.put("appointment", newAppointment);
            taskParamObj.put("cycle", newCycleSampling);
            taskParamObj.put("cycleStart", newCycleStart);
            taskParamObj.put("cycleStop", newCycleStop);
            taskParamObj.put("targetTime", newTargetTime);
        }
        otherInfoHandle.setNewAppDir(newAppDir);
        otherInfoHandle.setNewAppParam(newAppParam);
        otherInfoHandle.setNewProcessName(newProcessName);
        otherInfoHandle.setNewPid(newPid);
        otherInfoHandle.getNewVal(jsonObject);
        return jsonObject;
    }

    /**
     * 获取采集方式
     *
     * @return 是否为周期采集
     */
    private boolean getTaskOperateType() {
        Enumeration<AbstractButton> jRadioBtnEnumeration = cycleBtnGroup.getElements();
        AbstractButton abstractBtn;
        while (jRadioBtnEnumeration.hasMoreElements()) {
            abstractBtn = jRadioBtnEnumeration.nextElement();
            if (abstractBtn instanceof JCheckBox) {
                JCheckBox jCheckBox = (JCheckBox) abstractBtn;
                if (jCheckBox.isSelected()) {
                    return TaskManageContent.PARAM_OPERATE_TYPE_CYCLE.equals(jCheckBox.getText());
                }
            }
        }
        return false;
    }

    /**
     * 监听 单选框改变
     */
    private void addListener() {
        // 单次采集
        this.onceCheckBox.addMouseListener(
                new MouseAdapter() {
                    /**
                     * 鼠标点击事件 #447ff5
                     *
                     *  @param event 事件
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        appointPanel.setVisible(true);
                        cyclePanel.setVisible(false);
                    }
                });
        // 周期采集
        this.cycleCheckBox.addMouseListener(
                new MouseAdapter() {
                    /**
                     * 鼠标点击事件 #447ff5
                     *
                     *  @param event 事件
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        appointPanel.setVisible(false);
                        cyclePanel.setVisible(true);
                    }
                });

        // 进程名称
        this.processNameCheckBox.addMouseListener(
                new MouseAdapter() {
                    /**
                     * 鼠标点击事件 #447ff5
                     *
                     *  @param event 事件
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        // 如果pid未选中，进程名不能 取消选中
                        if (!PIDSelected) {
                            processNameCheckBox.setSelected(true);
                            processNameCheckBox.setEnabled(true);
                            return;
                        }
                        processNameSelected = processNameCheckBox.isSelected();
                        processNameField.setEditable(processNameSelected);
                        if (!processNameSelected) {
                            processNameField.setText("");
                        }
                    }
                });
        // pid
        this.PIDCheckBox.addMouseListener(
                new MouseAdapter() {
                    /**
                     * 鼠标点击事件 #447ff5
                     *
                     *  @param event 事件
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        // 如果进程名未选中，pid不能 取消选中
                        if (!processNameSelected) {
                            PIDCheckBox.setSelected(true);
                            PIDField.setEditable(true);
                            return;
                        }
                        PIDSelected = PIDCheckBox.isSelected();
                        PIDField.setEditable(PIDSelected);
                        if (!PIDSelected) {
                            PIDField.setText("");
                        }
                    }
                });
    }

    /**
     * 全量校验
     *
     * @return 所有校验异常
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();
        analysisTargetInfoValid(result); // 分析对象信息校验
        timeValid(result); // 时间校验
        dataValid(result); // 日期校验
        otherInfoHandle.inputValid(result);
        return result;
    }

    /**
     * 分析对象信息校验
     *
     * @param result 错误提示数组
     */
    private void analysisTargetInfoValid(List<ValidationInfo> result) {
        if (appLaunchPanel.isVisible()) {
            String dirText = appDirField.getText();
            // 应用路径为空或不满足正则表达式则提示错误
            if (StringUtils.isEmpty(dirText) || !dirText.matches(TaskManageContent.PARAM_APP_DIR_REGEX)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_INCORRECT_FORMAT, appDirField));
            }
        }
        if (appAttachPanel.isVisible()) {
            String pidFieldText = PIDField.getText();
            String processNameText = processNameField.getText();
            // 选中则必须填写
            if (PIDSelected && StringUtils.isEmpty(pidFieldText)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_NOT_EMPTY, PIDField));
            }
            // 选中则必须填写
            if (processNameSelected && StringUtils.isEmpty(processNameText)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_NOT_EMPTY, processNameField));
            }
            // pid 不为空且 不符合正则表达式
            if (!StringUtils.isEmpty(pidFieldText) && !pidFieldText.matches(TaskManageContent.PARAM_PID_REGEX)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_INCORRECT_FORMAT, PIDField));
            }
        }
    }

    /**
     * 时间格式校验
     *
     * @param result 错误提示数组
     */
    private void timeValid(List<ValidationInfo> result) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String targetTime = targetTimeText.getText();
        try {
            timeFormat.parse(targetTime);
        } catch (ParseException e) {
            result.add(new ValidationInfo(SchTaskContent.TIME_FORMAT_NOTICE, targetTimeText));
        }
    }

    /**
     * 日期格式范围校验
     *
     * @param result 错误提示数组
     */
    private void dataValid(List<ValidationInfo> result) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTimeStamp = System.currentTimeMillis(); // 获取当前时间戳
        boolean cycleSampling = getTaskOperateType(); // 采集方式
        String targetTime = targetTimeText.getText();
        if (cycleSampling) {
            // 周期采集：开始日期时间，不能早于当前日期时间
            String cycleStartDateTime = cycleStartText.getText() + " " + targetTime;
            long sevenDateTimeLong = 7L * 24 * 60 * 60 * 1000;
            long thirtyDateTimeLong = 30L * 24 * 60 * 60 * 1000;
            long dateTimeLongStart = 1L;
            try {
                Date dateTimeStart = format.parse(cycleStartDateTime);
                dateTimeLongStart = dateTimeStart.getTime();
                if (dateTimeLongStart < currentTimeStamp) {
                    // 若开始的时间和日期 小于当前时间，则报错
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_START_NOTICE, cycleStartText));
                }
                if (currentTimeStamp + sevenDateTimeLong < dateTimeLongStart) {
                    // 若开始日期 晚于当前日期 7天，报错
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_START_NOTICE, cycleStartText));
                }
            } catch (ParseException e) {
                result.add(new ValidationInfo(SchTaskContent.DATE_FORMAT_NOTICE, cycleStartText));
            }

            // 结束时间，不能早于开始日期时间，不能晚于开始时间7天
            String cycleStop = cycleStopText.getText() + " 23:59:59";
            Date dateTimeStop = null;
            try {
                dateTimeStop = format.parse(cycleStop);
                long stopLong = dateTimeStop.getTime();
                if (stopLong < dateTimeLongStart) {
                    // 若 结束时间 早于 当前时间，则报错
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_STOP_NOTICE, cycleStopText));
                }
                if (dateTimeLongStart + thirtyDateTimeLong < stopLong) {
                    // 若结束日期晚于开始日期超过30天，报错
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_STOP_NOTICE, cycleStopText));
                }
            } catch (ParseException e) {
                result.add(new ValidationInfo(SchTaskContent.DATE_FORMAT_NOTICE, cycleStopText));
            }
        } else {
            // 单次采集
            String appointDateTime = appointmentText.getText() + " " + targetTime;
            Date dateTimeAppoint = null;
            long dateTimeLongAppoint = 1L;
            try {
                dateTimeAppoint = format.parse(appointDateTime);
                dateTimeLongAppoint = dateTimeAppoint.getTime();
                if (dateTimeLongAppoint < currentTimeStamp) {
                    // 若开始的时间和日期 小于当前时间，则报错
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_START_NOTICE, appointmentText));
                }
            } catch (ParseException e) {
                result.add(new ValidationInfo(SchTaskContent.DATE_FORMAT_NOTICE, appointmentText));
            }
        }
    }
}
