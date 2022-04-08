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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskMultiDeletePanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * 删除 预约任务  确认弹窗
 *
 * @since 2012-10-12
 */
public class SchTaskDeleteDialog extends IdeaDialog {
    /**
     * 主要内容展示面板
     */
    protected IDEBasePanel mainPanel;
    /**
     * 待删除的任务编号
     */
    private Integer delSchTaskId;
    /**
     * 选中的带删除的编号数组
     */
    private Integer[] delSchTaskIdArr;

    /**
     * @param title        弹窗标题
     * @param dialogName   弹框名称
     * @param delSchTaskId 待删除的任务编号
     * @param panel        需要展示的面板之一
     */
    public SchTaskDeleteDialog(String title, String dialogName, Integer delSchTaskId, IDEBasePanel panel) {
        this.delSchTaskId = delSchTaskId;
        this.title = StringUtil.stringIsEmpty(title) ? SchTaskContent.SCH_TASK_DIALOG_DELETE_TITLE : title;
        this.dialogName =
                StringUtil.stringIsEmpty(dialogName) ? SchTaskContent.SCH_TASK_DIALOG_DELETE_TITLE : dialogName;
        this.mainPanel = panel;
        setOKAndCancelName(SchTaskContent.SCH_TASK_OPERATE_OK, SchTaskContent.SCH_TASK_OPERATE_CANCEL);
        initDialog(); // 初始化弹框内容
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        super.initDialog(); // 初始化面板容器
    }

    /**
     * 获取 mainPanel
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认操作
     */
    @Override
    protected void onOKAction() {
        RequestDataBean message = null;
        if (mainPanel instanceof SchTaskMultiDeletePanel) {
            // 批量删除
            SchTaskMultiDeletePanel multiDeletePanel = (SchTaskMultiDeletePanel) mainPanel;
            delSchTaskIdArr = multiDeletePanel.getSelectIdArr();
            message =
                    new RequestDataBean(
                            TuningIDEConstant.TOOL_NAME_TUNING,
                            "sys-perf/api/v2.2/schedule-tasks/batch-delete/",
                            HttpMethod.DELETE.vaLue(),
                            "");
            JSONObject paramJsonObj = new JSONObject();
            JSONArray idJsonArr = new JSONArray();
            idJsonArr.addAll(Arrays.asList(delSchTaskIdArr));
            paramJsonObj.put("scheduletask_ids", idJsonArr);
            String objStr = JsonUtil.getJsonStrFromJsonObj(paramJsonObj);
            message.setBodyData(objStr);
        } else {
            // 单个删除
            message =
                    new RequestDataBean(
                            TuningIDEConstant.TOOL_NAME_TUNING,
                            "sys-perf/api/v2.2/schedule-tasks/" + delSchTaskId + "/",
                            HttpMethod.DELETE.vaLue(),
                            "");
        }
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return;
        }
        String title = SchTaskContent.SCH_TASK_DIALOG_DELETE_TITLE;
        if ("SysPerf.Success".equals(responseBean.getCode())) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            title, SchTaskContent.SCH_TASK_DIALOG_DELETE_SUCCESS, NotificationType.INFORMATION));
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(title, SchTaskContent.SCH_TASK_DIALOG_DELETE_FAIL, NotificationType.ERROR));
        }
    }

    /**
     * 取消操作
     */
    @Override
    protected void onCancelAction() {
    }

    /**
     * 批量删除时
     * 添加表格监视事件。当用户选择待删除按钮的时候。将按钮置为可用
     */
    private void addListenerToTable() {
        if (mainPanel instanceof SchTaskMultiDeletePanel) {
            SchTaskMultiDeletePanel multiDelPanel = (SchTaskMultiDeletePanel) mainPanel;
            JTable delListTable = multiDelPanel.getTable();
            int rowCount = delListTable.getRowCount();
            delListTable
                    .getTableHeader()
                    .addMouseListener(
                            new MouseAdapter() {
                                /**
                                 * 鼠标点击事件
                                 *
                                 *  @param event 事件
                                 */
                                public void mouseClicked(MouseEvent event) {
                                    listenHandle(rowCount, delListTable);
                                }
                            });
            delListTable.addMouseListener(
                    new MouseAdapter() {
                        /**
                         * 鼠标点击事件
                         *
                         *  @param event 事件
                         */
                        public void mouseClicked(MouseEvent event) {
                            listenHandle(rowCount, delListTable);
                        }
                    });
        }
    }

    private void listenHandle(int rowCount, JTable delListTable) {
        boolean ifCheck = false; // 是否存在选中行
        for (int row = 0; row < rowCount; row++) {
            Object selectObj = delListTable.getValueAt(row, 0);
            if (selectObj instanceof Boolean) {
                boolean isSelect = (boolean) selectObj;
                if (isSelect) { // 只要有一个选中行，既可以执行ok操作
                    ifCheck = true;
                    break;
                }
            }
        }
        okAction.setEnabled(ifCheck);
    }
}
