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

package com.huawei.kunpeng.hyper.tuner.action.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.SchTaskFormatUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.SchTaskDeleteDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.SchTaskDetailDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.SchTaskUpdateDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskDeletePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskDetailPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskMultiDeletePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskNoticePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskUpdatePanel;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 预约任务Action
 *
 * @since 2021-5-6
 */
public class SchTaskAction extends IDEPanelBaseAction {
    /**
     * 查询 预约任务列表
     *
     * @return 预约任务对象列表
     */
    public List<SchTaskBean> getSchTaskList() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/schedule-tasks/batch/", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<SchTaskBean> schTaskBeans = new ArrayList<>();
        if (responseBean == null) {
            return schTaskBeans;
        }
        Object obj = JSONObject.parse(responseBean.getData());
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            Object scheduleTaskObj = jsonObject.get("scheduleTaskList");
            if (scheduleTaskObj instanceof JSONArray) {
                handleScheduleTaskList(schTaskBeans, (JSONArray) scheduleTaskObj);
            }
        }
        return schTaskBeans;
    }

    private void handleScheduleTaskList(List<SchTaskBean> schTaskBeans, JSONArray scheduleTaskObj) {
        SchTaskBean schTaskBean;
        for (Object taskItemObj : scheduleTaskObj) {
            if (taskItemObj instanceof JSONObject) {
                JSONObject taskItemJSONObj = (JSONObject) taskItemObj;
                schTaskBean = SchTaskFormatUtil.taskJson2JavaObj(taskItemJSONObj);
                schTaskBeans.add(schTaskBean);
            }
        }
    }

    /**
     * 获取 单个 预约任务
     * 预约任务返回值中的 taskInfo 包含（除预约状态之外的）所有的预约信息
     *
     * @param id 待查询的任务id
     * @return 返回单个任务详情
     */
    public SchTaskBean getSchTaskItem(Integer id) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/schedule-tasks/" + id + "/", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        SchTaskBean schTaskBean = new SchTaskBean();
        if (responseBean == null) {
            return schTaskBean;
        }
        Object obj = JSONObject.parse(responseBean.getData());
        if (obj instanceof JSONObject) {
            JSONObject taskItemJSONObj = (JSONObject) obj;
            schTaskBean = SchTaskFormatUtil.taskJson2JavaObj(taskItemJSONObj);
        }
        schTaskBean.setTaskId(id);
        return schTaskBean;
    }

    /**
     * 获取 单个 预约任务
     * 预约任务返回值中的 taskInfo 包含（除预约状态之外的）所有的预约信息
     *
     * @param id 待查询的任务id
     * @return 返回单个任务详情
     */
    public JSONObject getSchTaskInfoJSONObj(Integer id) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/schedule-tasks/" + id + "/", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        JSONObject taskInfoJsonObj = new JSONObject();
        if (responseBean == null) {
            return taskInfoJsonObj;
        }
        Object obj = JSONObject.parse(responseBean.getData());
        if (obj instanceof JSONObject) {
            JSONObject schTaskJsonObj = (JSONObject) obj;
            taskInfoJsonObj = schTaskJsonObj.getJSONObject("taskInfo");
        }
        return taskInfoJsonObj;
    }

    /**
     * 删除选中任务列表
     *
     * @param id       选中的任务ID
     * @param taskName 选中的任务名称
     */
    public void deleteTaskItem(Integer id, String taskName) {
        String title = SchTaskContent.SCH_TASK_DIALOG_DELETE_TITLE;
        SchTaskDeleteDialog dialog = new SchTaskDeleteDialog(title, title, id, new SchTaskDeletePanel(taskName));
        dialog.displayPanel();
    }

    /**
     * 批量删除预约任务
     *
     * @param taskBeans 待删除的任务Bean 列表
     */
    public void deleteMultiTask(List<SchTaskBean> taskBeans) {
        String title = SchTaskContent.SCH_TASK_DIALOG_DELETE_TITLE;
        SchTaskDeleteDialog dialog = new SchTaskDeleteDialog(title, title, 0, new SchTaskMultiDeletePanel(taskBeans));
        dialog.displayPanel();
    }


    /**
     * 查看选中的任务详细信息
     *
     * @param bean 选中的预约任务对象
     */
    public void showDetail(SchTaskBean bean) {
        String title = SchTaskContent.SCH_TASK_DIALOG_DETAIL_TITLE;
        SchTaskDetailDialog dialog = new SchTaskDetailDialog(title, title, new SchTaskDetailPanel(bean));
        dialog.displayPanel();
    }

    /**
     * 编辑选中的任务
     *
     * @param taskId 选中的任务ID
     */
    public void editTaskItem(Integer taskId) {
        String title = SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE;
        SchTaskUpdateDialog dialog = new SchTaskUpdateDialog(title, title, new SchTaskUpdatePanel(taskId));
        dialog.displayPanel();
    }

    /**
     * 无法编辑 提示用户
     *
     * @param taskNameStr 选中的任务 名称
     */
    public void notice(String taskNameStr) {
        String title = SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE;
        SchTaskUpdateDialog dialog = new SchTaskUpdateDialog(title, title, new SchTaskNoticePanel(taskNameStr));
        dialog.displayPanel();
    }
}