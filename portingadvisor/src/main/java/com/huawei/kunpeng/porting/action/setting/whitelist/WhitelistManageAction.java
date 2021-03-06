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

package com.huawei.kunpeng.porting.action.setting.whitelist;

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
import com.huawei.kunpeng.porting.process.WhitelistManageProcess;
import com.huawei.kunpeng.porting.ui.panel.WhitelistManagePanel;

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
 * WhitelistManageAction
 *
 * @since 2010-10-13
 */
public class WhitelistManageAction extends IDEPanelBaseAction {
    private static final String SUCCESS = "0";

    private static final String FAIL = "1";

    private static final String INSUFFICIENT_SPACE = RespondStatus.DISK_NOT_ENOUGH.value();

    private static final int RECOVERY = 1;

    private static final int UPDATE = 2;

    private IDEBasePanel cancelPanel = new IDEBasePanel() {
        @Override
        protected void registerComponentAction() {
        }

        @Override
        protected void setAction(IDEPanelBaseAction action) {
        }
    };

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param operationType ????????????
     * @param password      ???????????????
     */
    public void whitelistManagement(int operationType, String password) {
        String requestUrl = "/portadv/tasks/dependency_dictionary_manage/";
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl,
            HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("password", password);
        obj.put("option", operationType);
        message.setBodyData(obj.toJSONString());
        ResponseBean responseData = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            return;
        }
        parseTaskCreationResponseBean(responseData, operationType);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param response ??????????????????
     * @param option   ??????????????????
     */
    public void parseTaskCreationResponseBean(ResponseBean response, int option) {
        String statusCode = response.getStatus();
        if (SUCCESS.equals(statusCode)) {
            String data = response.getData();
            Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(data);
            String taskName = jsonMessage.get("task_name"); // ???????????????????????????????????????????????????ID
            whitelistManagementProcess(taskName, option);
        } else if (INSUFFICIENT_SPACE.equals(statusCode)) {
            // ????????????????????????
            DiskUtil.sendDiskAlertMessage();
        } else {
            // ????????????,????????????????????????????????????????????????VSCODE????????????
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", CommonUtil.getRspTipInfo(response), NotificationType.ERROR));
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param panel    ??????
     * @param file     ????????????????????????
     * @param fileSize ????????????
     */
    public void uploadButtonClicked(WhitelistManagePanel panel, File file, long fileSize) {
        String requestUrl = String.format(Locale.ROOT,
            "/portadv/tasks/dependency_dictionary/package/?filename=%s&filesize=%s",
            file.getName(), fileSize);
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl, HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        parseUploadResponseBean(panel, responseBean, file);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param panel        ??????
     * @param responseData ??????????????????
     * @param file         ?????????????????????
     */
    public void parseUploadResponseBean(WhitelistManagePanel panel, ResponseBean responseData, File file) {
        String data = responseData.getData();
        String status = responseData.getStatus();
        String statusCode = "";
        if (!ValidateUtils.isEmptyString(data)) {
            Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(data);
            statusCode = String.valueOf(jsonMessage.get("status"));
        }
        if (SUCCESS.equals(status)) {
            Logger.info("Query file existence successfully.");
            uploadWhitelistPackage(panel, file); // ???????????????????????????
        } else if (INSUFFICIENT_SPACE.equals(status)) {
            DiskUtil.sendDiskAlertMessage();
        } else {
            if (FAIL.equals(statusCode)) {
                reUpload(responseData, panel, file);
            } else {
                String msg = CommonUtil.getRspTipInfo(responseData) + I18NServer.toLocale("plugins_porting_faq_tips");
                IDENotificationUtil.notificationForHyperlink(new NotificationBean("", msg, NotificationType.ERROR),
                    op -> CommonUtil.openURI(I18NServer.toLocale("plugins_porting_dependency_dictionary_url")));
            }
        }
    }

    private void reUpload(ResponseBean responseData, WhitelistManagePanel panel, File file) {
        CancelDialog dialog = new CancelDialog(cancelPanel, responseData.getInfo()) {
            @Nullable
            @Override
            protected JComponent createCenterPanel() {
                JPanel jPanel = new JPanel();
                JLabel label = new JLabel();
                label.setText(I18NServer.respToLocale(responseData));
                jPanel.add(label);
                return jPanel;
            }

            @Override
            protected void onOKAction() {
                Logger.info("Delete existed file.");
                deleteExistedPackage(panel);
                Logger.info("Try to upload file.");
                uploadWhitelistPackage(panel, file); // ???????????????????????????
            }
        };
        dialog.displayPanel();
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param panel ??????
     */
    public void deleteExistedPackage(WhitelistManagePanel panel) {
        String requestUrl = "/portadv/tasks/delete/dependency_dictionary/package/";
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl,
            HttpMethod.POST.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return;
        }
        if (SUCCESS.equals(responseBean.getStatus())) {
            panel.isUploaded = false;
        }
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     * @param file  ????????????
     */
    public void uploadWhitelistPackage(WhitelistManagePanel panel, File file) {
        if (StringUtil.verifyFileSuffix(file.getName(), new String[] {"gz", "zip", "jar", "tar", "bz", "bz2"})) {
            // ???????????????????????????
            RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/portadv/tasks/dependency_dictionary/package/", HttpMethod.POST.vaLue(), "");
            panel.isUploaded = ActionHelper.uploadPackage(message, file);
        } else {
            IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale("plugins_porting_tip_file_title"),
                    I18NServer.toLocale("plugins_porting_tip_file_type_error"), NotificationType.ERROR));
        }
    }

    /**
     * ?????????????????????
     *
     * @param taskName ??????ID
     * @param option   ????????????
     */
    public void whitelistManagementProcess(String taskName, int option) {
        String progressBarTitle = "";
        switch (option) {
            case RECOVERY:
                progressBarTitle = I18NServer.toLocale("plugins_common_message_restoring");
                break;
            case UPDATE:
                progressBarTitle = I18NServer.toLocale("plugins_common_message_upgrading");
                break;
            default:
                break;
        }
        // ???????????????????????????
        WhitelistManageProcess process = new WhitelistManageProcess(taskName);
        process.processForONFore(null, progressBarTitle, process, true);
    }
}
