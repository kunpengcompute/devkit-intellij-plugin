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

package com.huawei.kunpeng.porting.action.setting.template;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.CancelDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.ActionHelper;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.DiskUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.process.PortingTemplateProcess;
import com.huawei.kunpeng.porting.ui.panel.PortingTemplatePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * PortingTemplateAction
 *
 * @since 2021-01-26
 */
public class PortingTemplateAction extends IDEPanelBaseAction {
    private static final String SUCCESS = "0";

    private static final String FAIL = "1";

    private static final String INSUFFICIENT_SPACE = RespondStatus.DISK_NOT_ENOUGH.value();

    private IDEBasePanel cancelPanel = new IDEBasePanel() {
        @Override
        protected void registerComponentAction() {
        }

        @Override
        protected void setAction(IDEPanelBaseAction action) {
        }
    };

    /**
     * 创建管理任务
     *
     * @param operationType 任务类型
     * @param password      管理员密码
     */
    public void portingTemplateManagement(String operationType, String password) {
        String requestUrl = "/portadv/solution/management/";
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl,
            HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("password", password);
        obj.put("operation", operationType);
        message.setBodyData(obj.toJSONString());
        ResponseBean responseData = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            return;
        }
        parseTaskCreationResponseBean(responseData, operationType);
    }

    /**
     * 请求接口后的返回数据处理
     *
     * @param response      创建任务响应
     * @param operationType 管理任务类型
     */
    public void parseTaskCreationResponseBean(ResponseBean response, String operationType) {
        String statusCode = response.getStatus();
        if (SUCCESS.equals(statusCode)) {
            String data = response.getData();
            Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(data);
            String id = jsonMessage.get("id");
            portingTemplateProcess(id, operationType);
        } else if (INSUFFICIENT_SPACE.equals(statusCode)) {
            // 磁盘空间不足提醒
            DiskUtil.sendDiskAlertMessage();
        } else {
            // 错误提示,仅对密码错误类型进行弹窗提示，与VSCODE保持一致
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", CommonUtil.getRspTipInfo(response), NotificationType.ERROR));
        }
    }

    /**
     * 上传按钮点击事件对应处理方法
     *
     * @param panel    面板
     * @param file     待上传的文件对象
     * @param fileSize 文件大小
     */
    public void uploadButtonClicked(PortingTemplatePanel panel, File file, long fileSize) {
        String requestUrl = String.format(Locale.ROOT, "/portadv/solution/package/status/?filename=%s&filesize=%s",
            file.getName(), fileSize);
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl, HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        parseUploadResponseBean(panel, responseBean, file);
    }

    /**
     * 解析处理文件上传校验接口返回
     *
     * @param panel 面板
     * @param res   接口返回数据
     * @param file  待上传文件对象
     */
    public void parseUploadResponseBean(PortingTemplatePanel panel, ResponseBean res, File file) {
        String data = res.getData();
        String status = res.getStatus();
        String statusCode = "";
        if (!ValidateUtils.isEmptyString(data)) {
            Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(data);
            statusCode = String.valueOf(jsonMessage.get("status"));
        }
        if (SUCCESS.equals(status)) {
            Logger.info("Query file existence successfully.");
            uploadPackage(panel, file);
        } else if (INSUFFICIENT_SPACE.equals(status)) {
            DiskUtil.sendDiskAlertMessage();
        } else {
            if (FAIL.equals(statusCode)) {
                reUpload(res, panel, file);
            } else {
                IDENotificationUtil.notificationCommon(
                    new NotificationBean("", CommonUtil.getRspTipInfo(res), NotificationType.ERROR));
            }
        }
    }

    private void reUpload(ResponseBean responseData, PortingTemplatePanel panel, File file) {
        CancelDialog dialog = new CancelDialog(cancelPanel, responseData.getInfo()) {
            @Nullable
            @Override
            protected JComponent createCenterPanel() {
                JLabel label = new JLabel();
                label.setText(CommonUtil.getRspTipInfo(responseData));
                JPanel jPanel = new JPanel();
                jPanel.add(label);
                return jPanel;
            }

            @Override
            protected void onOKAction() {
                Logger.info("Delete existed file.");
                deleteExistedPackage(panel, file);
                Logger.info("Try to upload file.");
                uploadPackage(panel, file);
            }

            @Override
            protected void onCancelAction() {
            }
        };
        dialog.displayPanel();
    }

    /**
     * 删除服务器文件系统中已存在的白名单包
     *
     * @param panel 面板
     * @param file  文件
     */
    public void deleteExistedPackage(PortingTemplatePanel panel, File file) {
        String requestUrl = "/portadv/solution/package/status/";
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl,
            HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("filename", file.getName());
        message.setBodyData(obj.toJSONString());
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return;
        }
        if (SUCCESS.equals(responseBean.getStatus())) {
            panel.isUploaded = false;
        }
    }

    /**
     * 上传包
     *
     * @param panel 面板
     * @param file  文件对象
     */
    public void uploadPackage(PortingTemplatePanel panel, File file) {
        if (StringUtil.verifyFileSuffix(file.getName(), new String[] {"gz", "zip", "jar", "tar", "bz", "bz2"})) {
            // 弹出上传文件进度条
            RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/portadv/solution/package/", HttpMethod.POST.vaLue(), "");
            panel.isUploaded = ActionHelper.uploadPackage(message, file);
        } else {
            IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale("plugins_porting_tip_file_title"),
                    I18NServer.toLocale("plugins_porting_tip_file_type_error"), NotificationType.ERROR));
        }
    }

    /**
     * 任务处理
     *
     * @param taskId        任务ID
     * @param operationType 任务类型
     */
    public void portingTemplateProcess(String taskId, String operationType) {
        String progressBarTitle = "";
        switch (operationType) {
            case PortingTemplatePanel.UPGRADE:
                progressBarTitle = I18NServer.toLocale("plugins_common_message_upgrading");
                break;
            case PortingTemplatePanel.RESTORE:
                progressBarTitle = I18NServer.toLocale("plugins_common_message_restoring");
                break;
            default:
                break;
        }
        // 创建上传文件进度条
        PortingTemplateProcess process = new PortingTemplateProcess(taskId);
        process.processForONFore(null, progressBarTitle, process, true);
    }
}
