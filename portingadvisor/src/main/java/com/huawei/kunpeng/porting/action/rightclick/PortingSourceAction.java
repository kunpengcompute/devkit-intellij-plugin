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

package com.huawei.kunpeng.porting.action.rightclick;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.dialog.FileSaveAsDialog;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.FileSaveAsPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.dialog.sourceporting.SourcePortingEnvCheckDialog;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingWrapDialog;
import com.huawei.kunpeng.porting.ui.panel.PortingPanel;

import com.intellij.lang.Language;
import com.intellij.notification.NotificationType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ??????????????????
 *
 * @since 2020-10-09
 */
public class PortingSourceAction extends PortingRightClickAction {
    private static final long FILE_MAX_SIZE = IDEConstant.MAX_FILE_SIZE * 1024 * 1024L;
    private static final int BUTTON_NUM = 3;
    private static final String PLUGINS_PORTING_TIP_FILE_TITLE = "plugins_porting_tip_file_title";
    private static File zipFile;

    /**
     * ??????dialog
     *
     * @param file ????????????
     */
    @Override
    void openNewPageOrDialog(File file) {
        // ????????????????????????
        checkEnvForSourcePorting();
        // ???????????????????????????
        Map<String, Object> params = new HashMap<>();
        params.put(PortingPanel.UPLOAD_FILE, file);
        IDEBasePanel ideBasePanel = new PortingPanel(null, params);
        IDEBaseDialog dialog = new PortingWrapDialog(
            I18NServer.toLocale("plugins_porting_params_config"), ideBasePanel);
        ideBasePanel.setParentComponent(dialog);
        dialog.displayPanel();
    }

    /**
     * ????????????????????????
     */
    public static void checkEnvForSourcePorting() {
        if (PortingCommonUtil.isSignEnvPrompt()) {
            sourcePortingEnvCheck();
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param file ?????????????????????
     * @return String ?????????????????????????????????????????? ??????????????????
     */
    public static String uploadFile(File file) {
        // ???????????????????????????
        if (!StringUtil.verifyFileSuffix(file.getName(), PortingIDEConstant.SOURCE_EXTENSIONS)) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale(PLUGINS_PORTING_TIP_FILE_TITLE),
                    I18NServer.toLocale("plugins_porting_tip_file_type_error"), NotificationType.ERROR));
            return "";
        }

        // ????????????????????????
        zipFile = file;
        if (!preUpload(file)) {
            return "";
        }

        // ???????????????????????????
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/upload/",
            HttpMethod.POST.vaLue(), "");
        message.setFile(new File[] {zipFile});
        message.setNeedUploadFile(true);
        message.setNeedProcess(true);
        Language.getRegisteredLanguages();
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return "";
        }
        // ????????????????????????
        boolean isSuccess = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
        String title = I18NServer.toLocale(PLUGINS_PORTING_TIP_FILE_TITLE);
        String content = isSuccess ? zipFile.getName().split(IDEConstant.POINT_SEPARATOR)[0] + " "
            + I18NServer.toLocale("plugins_porting_tip_file_upload_suc") : CommonUtil.getRspTipInfo(responseBean);
        NotificationType type = isSuccess ? NotificationType.INFORMATION : NotificationType.ERROR;
        IDENotificationUtil.notificationCommon(new NotificationBean(title, content, type));

        return isSuccess ? responseBean.getData() : "";
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param file ????????????????????????
     * @return ??????????????????????????????
     */
    private static boolean saveAsUpload(File file) {
        ResponseBean responseBean = uploadInDiffMode(file, Mode.SAVE_AS);
        if (responseBean == null) {
            return false;
        }

        if (RespondStatus.UPLOAD_FILE_EXIST.value().equals(responseBean.getStatus())) {
            return handleFileDuplicatedCase(responseBean.getData(), file);
        }

        return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
    }

    /**
     * ?????????????????????????????????
     *
     * @param file ???????????????
     * @return boolean ??????????????????????????????
     */
    private static boolean preUpload(File file) {
        if (file.length() >= FILE_MAX_SIZE) {
            String title = I18NServer.toLocale(PLUGINS_PORTING_TIP_FILE_TITLE);
            String content = I18NServer.toLocale("plugins_porting_webpack_file_size_exceed");
            IDENotificationUtil.notificationCommon(new NotificationBean(title, content, NotificationType.ERROR));
            return false;
        }

        // ????????????????????????
        ResponseBean responseBean = uploadInDiffMode(file, Mode.OVERRIDE);
        if (responseBean == null) {
            return false;
        }
        // ??????????????????????????????????????????????????????????????????^ ` / | ; & $ > < \ ! ????????????????????????????????????
        if (RespondStatus.UPLOAD_FILE_NAME_NOT_SUPPORT.value().equals(responseBean.getStatus())) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", CommonUtil.getRspTipInfo(responseBean), NotificationType.ERROR));
        }
        return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
    }

    /**
     * ?????????????????????????????????????????????????????????Dialog
     *
     * @param file        ?????????????????????
     * @param newFileName ?????????????????????????????????????????????
     * @param suffix      ????????????
     * @return boolean     ??????????????????????????????
     */
    private static boolean showRenameFileMessage(File file, String newFileName, String suffix) {
        IDEBasePanel panel = new FileSaveAsPanel(null, newFileName);
        FileSaveAsDialog dialog = new FileSaveAsDialog(null, panel);
        dialog.displayPanel();
        if (dialog.getExitCode() == OK_EXIT_CODE) {
            String newName = dialog.getFileName() + suffix;
            if (!FileUtil.validateFileName(newName)) {
                Logger.error("newName error when showRenameFileMessage");
                return false;
            }
            if (!FileUtil.validateFilePath(file.getParent())) {
                Logger.error("file path error when showRenameFileMessage");
                return false;
            }
            File newFile = new File(file.getParent() + IDEConstant.PATH_SEPARATOR + newName);
            if (!file.renameTo(newFile)) {
                Logger.error("rename error when showRenameFileMessage");
            }
            zipFile = newFile;
            Logger.info("File already existed, rename");
            return saveAsUpload(zipFile);
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param jsonData ?????????????????????
     * @param file     ????????????
     * @return boolean ??????????????????????????????
     */
    private static boolean handleFileDuplicatedCase(String jsonData, File file) {
        Map dataMap = JsonUtil.getJsonObjFromJsonStr(jsonData);
        String oldName = dataMap.get("old_name").toString();
        String message = oldName + I18NServer.toLocale("plugins_porting_already_exist");
        String title = I18NServer.toLocale("plugins_porting_duplicate_file");
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(BUTTON_NUM);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.REPLACE);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.SAVE_AS);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String exitCode = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(message, title, buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // ??????????????????????????????????????????
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.REPLACE.getKey())) {
            // ???????????????
            ResponseBean responseBean = uploadInDiffMode(file, Mode.OVERRIDE);
            if (responseBean == null) {
                return false;
            }
            return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
        }
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.SAVE_AS.getKey())) {
            // ????????????????????? save_as
            IDEContext.setValueForGlobalContext(null, "fileModel", "save_as");
            // ???????????????
            String newName = dataMap.get("new_name").toString();
            String suffix = dataMap.get("suffix").toString();
            return showRenameFileMessage(file, newName, suffix);
        }
        return false;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param file ???????????????
     * @param mode ??????????????? "normal"???"override"???"save_as"
     * @return ResponseBean: ???????????????
     */
    private static ResponseBean uploadInDiffMode(File file, Mode mode) {
        Logger.info("Start to uploadInDiffMode!");
        Map<String, Object> obj = new HashMap<>();
        obj.put("choice", mode.getMode());
        obj.put("file_name", file.getName());
        obj.put("file_size", file.length());
        obj.put("need_unzip", String.valueOf(true));
        obj.put("scan_type", String.valueOf(0));
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/tasks/check_upload/", HttpMethod.POST.vaLue(), "");
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(request);
        if (responseBean == null) {
            Logger.info("the responseBean is null");
            return responseBean;
        }
        if (RespondStatus.UPLOAD_FILE_EXIST.value().equals(responseBean.getStatus())) {
            Map dataMap = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            if (dataMap.size() == 0) {
                IDENotificationUtil.notifyCommonForResponse("", responseBean.getStatus(), responseBean);
                responseBean.setStatus(RespondStatus.PROCESS_STATUS_FAILED.value());
            }
        }
        return responseBean;
    }

    /**
     * ????????????????????????????????????
     */
    public static void sourcePortingEnvCheck() {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/tasks/checkasmenv/", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(request);
        if (responseBean == null) {
            return;
        }
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            String title = "Prompt";
            boolean responseIsEmpty = responseBean.getInfo().isEmpty();
            String messagesTitle = responseIsEmpty ? null : responseBean.getInfo();
            SourcePortingEnvCheckDialog dialog = new SourcePortingEnvCheckDialog(title, null, messagesTitle,
                responseIsEmpty);
            dialog.displayPanel();
        }
    }

    /**
     * The class Mode: ???????????????????????????
     *
     * @since v1.0
     */
    private enum Mode {
        OVERRIDE("override"),
        NORMAL("normal"),
        SAVE_AS("save_as");

        private String mode;

        Mode(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }
    }
}
