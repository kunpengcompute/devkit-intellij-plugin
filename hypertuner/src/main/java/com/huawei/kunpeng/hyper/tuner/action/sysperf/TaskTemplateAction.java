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
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskTemplateContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskTemplateFormatUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.TaskTemplateDeleteDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.TaskTemplateDetailDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.TaskTemplateDeletePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.TaskTemplateDetailPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.TaskTemplateMultiDeletePanel;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 弱口令设置实现类
 *
 * @since 2021-4-26
 */
public class TaskTemplateAction extends IDEPanelBaseAction {
    private static TaskTemplateAction instance = new TaskTemplateAction();

    public static TaskTemplateAction getInstance() {
        return instance;
    }

    /**
     * 字体大小
     */
    private static final int FONT_SIZE = 12;


    /**
     * 查询任务模板列表
     *
     * @return 任务模板列表
     */
    public List<TaskTemplateBean> getTaskList() {
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "sys-perf/api/v2.2/tasks/templates/",
                        HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<TaskTemplateBean> taskTemplateBeans = new ArrayList<>();
        if (responseBean == null) {
            return taskTemplateBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object templateList = jsonMessage.get("template-list");
        if (templateList instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) templateList;
            TaskTemplateBean taskTemplateBean;
            for (int mun = 0; mun < jsonArray.size(); mun++) {
                JSONObject taskTemplateJSONObj = jsonArray.getJSONObject(mun);
                taskTemplateBean = TaskTemplateFormatUtil.js2JavaObj(taskTemplateJSONObj);
                taskTemplateBeans.add(taskTemplateBean);
            }
        }
        return taskTemplateBeans;
    }

    /**
     * 删除选中任务模板
     *
     * @param tempId   选中的任务模板ID
     * @param tempName 选中的任务模板名称
     */
    public void deleteTempItem(Integer tempId, String tempName) {
        String title = TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DELETE;
        TaskTemplateDeleteDialog dialog = new TaskTemplateDeleteDialog(title, title, tempId,
                new TaskTemplateDeletePanel(tempName));
        dialog.displayPanel();
    }

    /**
     * 批量删除选中任务模板
     *
     * @param templateBeans 选中的任务模板列表
     */
    public void deleteMultiTask(List<TaskTemplateBean> templateBeans) {
        String title = TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DELETE;
        TaskTemplateDeleteDialog dialog = new TaskTemplateDeleteDialog(title, title,
                -1, new TaskTemplateMultiDeletePanel(templateBeans));
        dialog.displayPanel();
    }


    /**
     * 查看选中的任务模板详细信息
     *
     * @param bean 选中的任务模板对象
     */
    public void showDetail(TaskTemplateBean bean) {
        String title = TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DETAIL;
        TaskTemplateDetailDialog dialog = new TaskTemplateDetailDialog(title, title,
                new TaskTemplateDetailPanel(bean));
        dialog.displayPanel();
    }
}