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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.setting.javaperf.guardianmanager.GuardianMangerConfigurable;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.CommonDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.intellij.notification.NotificationType;

import java.util.Map;

/**
 * 删除 离线目标环境
 *
 * @since 2021-10-12
 */
public class GuardianPartDeleteTipDialog extends CommonDialog {
    private String url;

    private Map<String, String> paramMap;

    /**
     * @param title       弹窗标题
     * @param id          待删除的任务编号
     * @param panel       需要展示的面板之一
     * @param isConnected 是否在线目标环境
     */
    public GuardianPartDeleteTipDialog(String title, String id, IDEBasePanel panel, Boolean isConnected,
        String url, Map<String, String> paramMap) {
        this.title = StringUtil.stringIsEmpty(title) ? GuardianMangerConstant.GUARDIAN_DELETE_TITLE : title;
        this.dialogName = StringUtil.stringIsEmpty(title) ? GuardianMangerConstant.GUARDIAN_DELETE_TITLE : title;
        this.mainPanel = panel;
        this.url = url;
        this.paramMap = paramMap;
        // 设置弹框中保存取消按钮的名称
        setButtonName();
        initDialog(); // 初始化弹框内容
    }

    /**
     * 确认操作
     */
    @Override
    protected void onOKAction() {
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.DELETE.vaLue(), "");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(paramMap));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return;
        }
        // 返回code为null则删除失败，请求成功直接刷新表格
        if (responseBean.getCode() != null) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            GuardianMangerConstant.GUARDIAN_DELETE_TITLE,
                            responseBean.getMessage(),
                            NotificationType.ERROR));
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            GuardianMangerConstant.GUARDIAN_DELETE_TITLE,
                            TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_delete_success"),
                            NotificationType.INFORMATION));
            if (JavaPerfToolWindowPanel.isNeedUpdate) {
                GuardianMangerConfigurable.getGuardianSettingsComponent().updateGuardianTable();
            }
        }
    }
}
