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
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.AgentCertContent;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;

import com.intellij.notification.NotificationType;
import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 生成证书
 *
 * @since 2020-10-30
 */
public class AgentCertAddAction extends IDETableCommonAction {
    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public AgentCertAddAction(JTable targetTable) {
        super(targetTable);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/certificates/", HttpMethod.POST.vaLue(), "");
        ResponseBean responseData = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            return;
        } else {
            if (responseData.getCode().equals(AgentCertContent.RESPONSE_CODE)) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(AgentCertContent.AGENT_CERT_ADD_TITLE,
                                AgentCertContent.AGENT_CERT_ADD_SUCCESS,
                                NotificationType.INFORMATION));
            } else {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(AgentCertContent.AGENT_CERT_ADD_TITLE,
                                AgentCertContent.AGENT_CERT_ADD_FAILD,
                                NotificationType.ERROR));
            }
        }
    }
}
