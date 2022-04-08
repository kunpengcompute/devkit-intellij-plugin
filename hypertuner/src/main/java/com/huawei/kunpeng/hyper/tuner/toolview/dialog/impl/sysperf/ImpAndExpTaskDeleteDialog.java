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
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.notification.NotificationType;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 删除 导入导出任务  确认弹窗
 *
 * @since 2012-10-12
 */
public class ImpAndExpTaskDeleteDialog extends IdeaDialog {
    /**
     * 主要内容展示面板
     */
    protected IDEBasePanel mainPanel;

    private Integer delTaskId;

    /**
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param delTaskId  待删除的任务编号
     * @param panel      需要展示的面板之一
     */
    public ImpAndExpTaskDeleteDialog(String title, String dialogName, Integer delTaskId, IDEBasePanel panel) {
        this.delTaskId = delTaskId;
        this.title = StringUtil.stringIsEmpty(title) ? ImpAndExpTaskContent.DELETE_ITEM : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? ImpAndExpTaskContent.DELETE_ITEM : dialogName;
        this.mainPanel = panel;
        setOKAndCancelName(ImpAndExpTaskContent.OPERATE_OK, ImpAndExpTaskContent.OPERATE_CANCEL);
        initDialog(); // 初始化弹框内容
    }

    /**
     * 获取mainPanel
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
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/import_export_tasks/" + delTaskId + "/",
                        HttpMethod.DELETE.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return;
        }
        if ("SysPerf.Success".equals(responseBean.getCode())) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            ImpAndExpTaskContent.OPERATE_SUCESS,
                            ImpAndExpTaskContent.DELETE_SUCCESS,
                            NotificationType.INFORMATION));
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            ImpAndExpTaskContent.OPERATE_FAILD,
                            ImpAndExpTaskContent.DELETE_FAIL,
                            NotificationType.ERROR));
        }
    }

    /**
     * 取消操作
     */
    @Override
    protected void onCancelAction() {
    }
}
