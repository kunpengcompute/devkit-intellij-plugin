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
 * ?????? ???????????? panel
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
     * ???????????? ???????????????????????????????????????
     */
    private JPanel analysisModePanel;
    private JLabel analysisModeLabel1;
    private JLabel analysisModeLabel2;
    private JLabel analysisType1;
    private JLabel analysisType2;
    private JLabel cycle; // ????????????
    private JCheckBox onceCheckBox;
    private JCheckBox cycleCheckBox;
    private ButtonGroup cycleBtnGroup;
    private ButtonGroup targetParamBtnGroup;
    private JLabel targetTimeLabel;
    private JTextField targetTimeText;
    private JLabel cycleDateLabel;
    private JTextField appointmentText; // ????????????????????????
    private JTextField cycleStartText; // ???????????????????????????
    private JTextField cycleStopText; // ????????????????????????
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
     * ??????????????? ??????????????? ?????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????
     */
    private HashMap<String, String> selectN81IMap;
    /**
     * ???????????? ????????? ?????????Map
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
        initPanel(mainPanel); // ???????????????
        registerComponentAction(); // ??????????????????????????????
        createContent(mainPanel, SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE, false); // ?????????content??????
    }

    /**
     * ?????????????????????
     *
     * @return ????????????
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        return vi;
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        cycleBtnGroup = new ButtonGroup();
        cycleBtnGroup.add(onceCheckBox);
        cycleBtnGroup.add(cycleCheckBox);

        // ????????????
        taskNameLabel1.setText(TaskManageContent.TASK_NAME);
        taskNameLabel2.setText(schTaskItem.getTaskName());
        analysisTarget1.setText(TaskManageContent.ANALYSIS_TARGET); // ????????????
        String analysisTarget = SchTaskFormatUtil.analysisTargetFormat(schTaskItem.getTaskInfo().getAnalysisTarget());
        // ????????????
        if (TaskManageContent.ANALYSIS_TARGET_APP.equals(analysisTarget)) {
            analysisModePanel.setVisible(true);
            analysisModeLabel1.setText(TaskManageContent.ANALYSIS_MODE);
            analysisModeLabel2.setText(schTaskItem.getTaskInfo().getAnalysisTarget());
        } else {
            analysisModePanel.setVisible(false);
        }
        analysisTarget2.setText(analysisTarget);
        analysisType1.setText(TaskManageContent.ANALYSIS_TYPE); // ????????????
        String analysisType = SchTaskFormatUtil.analysisTypeFormat(schTaskItem.getAnalysisType());
        analysisType2.setText(analysisType);

        // ???????????????????????????????????????????????????????????????setAnalysisTypeInfoShow?????? ?????????
        memAnalysisPanel.setVisible(false);
        // ??????????????????
        setAnalysisTypeInfoShow();

        // ????????????
        cycle.setText(TaskManageContent.PARAM_OPERATE_TYPE); // ????????????
        cycleDateLabel.setText(TaskManageContent.PARAM_SAMPLE_DATE); // ????????????
        onceCheckBox.setText(TaskManageContent.PARAM_OPERATE_TYPE_ONCE);
        cycleCheckBox.setText(TaskManageContent.PARAM_OPERATE_TYPE_CYCLE);
        setDateShow();
        targetTimeLabel.setText(TaskManageContent.PARAM_TARGET_TIME); // ????????????
        targetTimeText.setText(schTaskItem.getTargetTime());
        addListener();
    }

    /**
     * ?????????????????????????????????
     *
     * @param target      ????????????
     * @param dir         ????????????
     * @param param       ????????????
     * @param processName ????????????
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
     * ?????? App Launch ???????????????
     *
     * @param appDir   ????????????
     * @param appParam ????????????
     */
    private void setAppLaunchInfoShow(String appDir, String appParam) {
        // ????????????
        appDirLabel.setText(TaskManageContent.PARAM_APPLICATION_PATH);
        appDirField.setText(appDir);

        // ????????????
        appparamLabel.setText(TaskManageContent.PARAM_APPLICATION_PARAM);
        appparamField.setText(appParam);
    }

    /**
     * ?????? App Attach ???????????????
     *
     * @param processName ????????????
     * @param pid         pid
     */
    private void setAppAttachInfoShow(String processName, String pid) {
        // ?????????
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
            // ????????????
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
        gridLayout.setRows(count); // ????????????
        gridLayout.setColumns(1); // ????????????
        gridLayout.setVgap(8); // ??????????????????
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
        String app = taskInfoJsonObj.getJSONObject("task_param").getString("app"); // ????????????
        String appArgs = taskInfoJsonObj.getJSONObject("task_param").getString("appArgs"); // ????????????
        String processName = taskInfoJsonObj.getJSONObject("task_param").getString("process_name"); // ?????????
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
     * ??????????????????panel ??????
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
        String appDir = schTaskItem.getTaskInfo().getAppDir(); // ????????????
        String appParam = schTaskItem.getTaskInfo().getAppParameters(); // ????????????
        String processName = schTaskItem.getTaskInfo().getProcessName(); // ?????????
        String pid = schTaskItem.getTaskInfo().getPid(); // PID
        setAnalysisTargetInfoShow(analysisTarget, appDir, appParam, processName, pid);
    }

    private void setAppInfo2() {
        String analysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        String appDir = schTaskItem.getTaskInfo().getAppDir(); // ????????????
        String appParam = schTaskItem.getTaskInfo().getAppParameters(); // ????????????
        String processName = schTaskItem.getTaskInfo().getProcessName(); // ?????????
        String pid = schTaskItem.getTaskInfo().getTargetPid(); // PID
        setAnalysisTargetInfoShow(analysisTarget, appDir, appParam, processName, pid);
    }

    /**
     * ???????????? ????????????????????????????????????
     */
    private void setDateShow() {
        if (schTaskItem.isCycle()) { // ??????????????????????????????????????? ??????????????????  cycleStart -> 2021-4-9
            cycleCheckBox.setSelected(true);
            cycleStopText.setText(schTaskItem.getTaskInfo().getCycleStop());
            cycleStartText.setText(schTaskItem.getTaskInfo().getCycleStart());
            cyclePanel.setVisible(true);
            appointPanel.setVisible(false);
        } else { // ???????????????????????????????????????
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
     * @param action ????????????
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof SchTaskAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * ??????????????????????????????ID
     *
     * @return ID
     */
    public int getCurrentSchTaskId() {
        return schTaskItem.getTaskId();
    }

    /**
     * ??????????????????????????????????????? Json??????
     *
     * @return ????????????
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
            // ????????? ?????????
            JSONObject taskParamObj = nodeConfigItemJsonObj.getJSONObject("taskParam");
            if (taskParamObj == null) {
                // ???????????? ?????????
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
     * ??????????????????
     *
     * @return ?????????????????????
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
     * ?????? ???????????????
     */
    private void addListener() {
        // ????????????
        this.onceCheckBox.addMouseListener(
                new MouseAdapter() {
                    /**
                     * ?????????????????? #447ff5
                     *
                     *  @param event ??????
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        appointPanel.setVisible(true);
                        cyclePanel.setVisible(false);
                    }
                });
        // ????????????
        this.cycleCheckBox.addMouseListener(
                new MouseAdapter() {
                    /**
                     * ?????????????????? #447ff5
                     *
                     *  @param event ??????
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        appointPanel.setVisible(false);
                        cyclePanel.setVisible(true);
                    }
                });

        // ????????????
        this.processNameCheckBox.addMouseListener(
                new MouseAdapter() {
                    /**
                     * ?????????????????? #447ff5
                     *
                     *  @param event ??????
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        // ??????pid??????????????????????????? ????????????
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
                     * ?????????????????? #447ff5
                     *
                     *  @param event ??????
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        // ???????????????????????????pid?????? ????????????
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
     * ????????????
     *
     * @return ??????????????????
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();
        analysisTargetInfoValid(result); // ????????????????????????
        timeValid(result); // ????????????
        dataValid(result); // ????????????
        otherInfoHandle.inputValid(result);
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param result ??????????????????
     */
    private void analysisTargetInfoValid(List<ValidationInfo> result) {
        if (appLaunchPanel.isVisible()) {
            String dirText = appDirField.getText();
            // ????????????????????????????????????????????????????????????
            if (StringUtils.isEmpty(dirText) || !dirText.matches(TaskManageContent.PARAM_APP_DIR_REGEX)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_INCORRECT_FORMAT, appDirField));
            }
        }
        if (appAttachPanel.isVisible()) {
            String pidFieldText = PIDField.getText();
            String processNameText = processNameField.getText();
            // ?????????????????????
            if (PIDSelected && StringUtils.isEmpty(pidFieldText)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_NOT_EMPTY, PIDField));
            }
            // ?????????????????????
            if (processNameSelected && StringUtils.isEmpty(processNameText)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_NOT_EMPTY, processNameField));
            }
            // pid ???????????? ????????????????????????
            if (!StringUtils.isEmpty(pidFieldText) && !pidFieldText.matches(TaskManageContent.PARAM_PID_REGEX)) {
                result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_INCORRECT_FORMAT, PIDField));
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param result ??????????????????
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
     * ????????????????????????
     *
     * @param result ??????????????????
     */
    private void dataValid(List<ValidationInfo> result) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTimeStamp = System.currentTimeMillis(); // ?????????????????????
        boolean cycleSampling = getTaskOperateType(); // ????????????
        String targetTime = targetTimeText.getText();
        if (cycleSampling) {
            // ??????????????????????????????????????????????????????????????????
            String cycleStartDateTime = cycleStartText.getText() + " " + targetTime;
            long sevenDateTimeLong = 7L * 24 * 60 * 60 * 1000;
            long thirtyDateTimeLong = 30L * 24 * 60 * 60 * 1000;
            long dateTimeLongStart = 1L;
            try {
                Date dateTimeStart = format.parse(cycleStartDateTime);
                dateTimeLongStart = dateTimeStart.getTime();
                if (dateTimeLongStart < currentTimeStamp) {
                    // ??????????????????????????? ??????????????????????????????
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_START_NOTICE, cycleStartText));
                }
                if (currentTimeStamp + sevenDateTimeLong < dateTimeLongStart) {
                    // ??????????????? ?????????????????? 7????????????
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_START_NOTICE, cycleStartText));
                }
            } catch (ParseException e) {
                result.add(new ValidationInfo(SchTaskContent.DATE_FORMAT_NOTICE, cycleStartText));
            }

            // ????????????????????????????????????????????????????????????????????????7???
            String cycleStop = cycleStopText.getText() + " 23:59:59";
            Date dateTimeStop = null;
            try {
                dateTimeStop = format.parse(cycleStop);
                long stopLong = dateTimeStop.getTime();
                if (stopLong < dateTimeLongStart) {
                    // ??? ???????????? ?????? ????????????????????????
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_STOP_NOTICE, cycleStopText));
                }
                if (dateTimeLongStart + thirtyDateTimeLong < stopLong) {
                    // ???????????????????????????????????????30????????????
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_STOP_NOTICE, cycleStopText));
                }
            } catch (ParseException e) {
                result.add(new ValidationInfo(SchTaskContent.DATE_FORMAT_NOTICE, cycleStopText));
            }
        } else {
            // ????????????
            String appointDateTime = appointmentText.getText() + " " + targetTime;
            Date dateTimeAppoint = null;
            long dateTimeLongAppoint = 1L;
            try {
                dateTimeAppoint = format.parse(appointDateTime);
                dateTimeLongAppoint = dateTimeAppoint.getTime();
                if (dateTimeLongAppoint < currentTimeStamp) {
                    // ??????????????????????????? ??????????????????????????????
                    result.add(new ValidationInfo(SchTaskContent.DATE_RANGE_START_NOTICE, appointmentText));
                }
            } catch (ParseException e) {
                result.add(new ValidationInfo(SchTaskContent.DATE_FORMAT_NOTICE, appointmentText));
            }
        }
    }
}
