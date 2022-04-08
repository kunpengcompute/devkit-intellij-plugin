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
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.ImpAndExpTaskFormatUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.ImpAndExpTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.ImpAndExpTaskDeleteDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.ImpAndExpTaskDownloadDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.ImpAndExpTaskReTryDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.ImpAndExpTaskDownloadPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.ImpAndExpTaskReTryPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.ImpAndExtTaskDeletePanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.intellij.notification.NotificationType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 导入导出任务 Action
 *
 * @since 2021-4-25
 */
public class ImpAndExpTaskAction extends IDEPanelBaseAction {
    private static ImpAndExpTaskAction instance = new ImpAndExpTaskAction();

    /**
     * 字体大小
     */
    private static final int FONT_SIZE = 12;

    /**
     * ImpAndExpTaskAction实例
     *
     * @return ImpAndExpTaskAction实例
     */
    public static ImpAndExpTaskAction getInstance() {
        return instance;
    }

    /**
     * 查询 任务列表
     *
     * @return 日志列表
     */
    public List<ImpAndExpTaskBean> getTaskList() {
        String currentLang = I18NServer.getCurrentLanguage();
        String url = "sys-perf/api/v2.2/import_export_tasks/";
        if ("en-us".equals(currentLang)) {
            url = url + "?language=en";
        }
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url,
                        HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<ImpAndExpTaskBean> impAndExpTaskBeans = new ArrayList<>();
        if (responseBean == null) {
            return impAndExpTaskBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object taskList = jsonMessage.get("taskList");
        if (taskList instanceof JSONArray) {
            JSONArray usersJson = (JSONArray) taskList;
            ImpAndExpTaskBean impAndExpTaskBean;
            for (int mun = 0; mun < usersJson.size(); mun++) {
                impAndExpTaskBean = usersJson.getObject(mun, ImpAndExpTaskBean.class);
                this.taskFormat(impAndExpTaskBean);
                impAndExpTaskBeans.add(impAndExpTaskBean);
            }
        }
        return impAndExpTaskBeans;
    }

    private void taskFormat(ImpAndExpTaskBean bean) {
        String taskNameFormat = bean.getTaskname() == null ? "--" : bean.getTaskname();
        bean.setTaskname(taskNameFormat);
        String projectnameFormat = bean.getProjectname() == null ? "--" : bean.getProjectname();
        bean.setProjectname(projectnameFormat);
        String operaTypeFormat = ImpAndExpTaskFormatUtil.operaTypeFormat(bean.getOperationType());
        String processStatus = ImpAndExpTaskFormatUtil.processStatusFormat(bean.getProcessStatus());
        String fileSizeFormat = ImpAndExpTaskFormatUtil.fileSizeFormat(bean.getTaskFilesize());
        bean.setTaskFilesize(fileSizeFormat);
        bean.setProcessStatus(processStatus);
        bean.setOperationType(operaTypeFormat);
    }

    /**
     * 重置任务
     *
     * @param projectName projectName
     * @param taskName    taskName
     * @param id          id
     */
    public void reTryTask(String projectName, String taskName, int id) {
        String title = ImpAndExpTaskContent.RETRY_ITEM;
        ImpAndExpTaskReTryDialog dialog = new ImpAndExpTaskReTryDialog(title, title, id,
                new ImpAndExpTaskReTryPanel(title, title, projectName, taskName));
        dialog.displayPanel();
    }

    /**
     * 删除导入导出任务
     * 管理员：可以删除所有的 任务
     * 普通用户：只能删除自己创建的任务
     *
     * @param id 待删除的任务id
     */
    public void deleteTaskItem(Integer id) {
        String title = ImpAndExpTaskContent.DELETE_ITEM;
        ImpAndExpTaskDeleteDialog dialog = new ImpAndExpTaskDeleteDialog(title, title, id,
                new ImpAndExtTaskDeletePanel(""));
        dialog.displayPanel();
    }

    /**
     * 下载导入导出任务
     *
     * @param id       待下载的任务id
     * @param section  分片
     * @param fileName fileName
     * @param fileSize fileSize
     */
    public void downloadTaskItemFile(Integer id, Integer section, String fileName, String fileSize) {
        String title = ImpAndExpTaskContent.DOWNLOAD_ALL;
        ImpAndExpTaskDownloadDialog dialog = new ImpAndExpTaskDownloadDialog(title, title, id, section,
                new ImpAndExpTaskDownloadPanel(title, title, fileName, fileSize, id, section));
        dialog.displayPanel();
    }

    /**
     * 下载导出任务
     *
     * @param path     地址路径
     * @param fileName 日志文件名称
     * @param id       id
     * @param section  section
     */
    public void downloadSelectLog(String path, String fileName, int id, int section) {
        String url = "sys-perf/api/v2.2/import_export_tasks/download/?id=" + id + "&section=" + section;
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.GET.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(false);
        message.setDownloadFileName(fileName);
        message.setDownloadPtah(path);
        message.setUrlParams(path + File.separator + fileName);
        File[] files = {new File(fileName)};
        message.setFile(files);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        Logger.info("responseBean", responseBean);
        if (responseBean == null) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(I18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_download_all"),
                            I18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_type_exp") + " "
                                    + I18NServer.toLocale("plugins_hyper_tuner_download_report_fail")
                                    + path,
                            NotificationType.ERROR));
            return;
        }
        if (responseBean.getCode() != null) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(I18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_download_all"),
                            I18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_download_not_exists"),
                            NotificationType.ERROR));
            File file = new File(path + File.separator + fileName);
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_download_all"),
                        ImpAndExpTaskContent.TYPE_EXP + " "
                                + I18NServer.toLocale("plugins_hyper_tuner_download_report_success")
                                + path,
                        NotificationType.INFORMATION));
    }
}
