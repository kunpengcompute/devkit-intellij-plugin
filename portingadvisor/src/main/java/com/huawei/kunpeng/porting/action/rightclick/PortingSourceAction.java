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
 * 源码项目分析
 *
 * @since 2020-10-09
 */
public class PortingSourceAction extends PortingRightClickAction {
    private static final long FILE_MAX_SIZE = IDEConstant.MAX_FILE_SIZE * 1024 * 1024L;
    private static final int BUTTON_NUM = 3;
    private static final String PLUGINS_PORTING_TIP_FILE_TITLE = "plugins_porting_tip_file_title";
    private static File zipFile;

    /**
     * 打开dialog
     *
     * @param file 源码文件
     */
    @Override
    void openNewPageOrDialog(File file) {
        // 源码迁移环境检查
        checkEnvForSourcePorting();
        // 弹出扫描参数设置框
        Map<String, Object> params = new HashMap<>();
        params.put(PortingPanel.UPLOAD_FILE, file);
        IDEBasePanel ideBasePanel = new PortingPanel(null, params);
        IDEBaseDialog dialog = new PortingWrapDialog(
            I18NServer.toLocale("plugins_porting_params_config"), ideBasePanel);
        ideBasePanel.setParentComponent(dialog);
        dialog.displayPanel();
    }

    /**
     * 源码迁移环境检查
     */
    public static void checkEnvForSourcePorting() {
        if (PortingCommonUtil.isSignEnvPrompt()) {
            sourcePortingEnvCheck();
        }
    }

    /**
     * 上传需要分析的源码压缩包
     *
     * @param file 需要上传的文件
     * @return String 上传成功返回解压后的文件名， 失败返回“”
     */
    public static String uploadFile(File file) {
        // 检查服务端文件状态
        if (!StringUtil.verifyFileSuffix(file.getName(), PortingIDEConstant.SOURCE_EXTENSIONS)) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale(PLUGINS_PORTING_TIP_FILE_TITLE),
                    I18NServer.toLocale("plugins_porting_tip_file_type_error"), NotificationType.ERROR));
            return "";
        }

        // 更新全局压缩包名
        zipFile = file;
        if (!preUpload(file)) {
            return "";
        }

        // 弹出上传文件进度条
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
        // 显示上传结果提示
        boolean isSuccess = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
        String title = I18NServer.toLocale(PLUGINS_PORTING_TIP_FILE_TITLE);
        String content = isSuccess ? zipFile.getName().split(IDEConstant.POINT_SEPARATOR)[0] + " "
            + I18NServer.toLocale("plugins_porting_tip_file_upload_suc") : CommonUtil.getRspTipInfo(responseBean);
        NotificationType type = isSuccess ? NotificationType.INFORMATION : NotificationType.ERROR;
        IDENotificationUtil.notificationCommon(new NotificationBean(title, content, type));

        return isSuccess ? responseBean.getData() : "";
    }

    /**
     * 点击源码包另存为时的处理逻辑
     *
     * @param file 重新命名的源码包
     * @return 是否可以上传到服务端
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
     * 源码压缩包上传前的检查
     *
     * @param file 源码压缩包
     * @return boolean 是否可以上传到服务端
     */
    private static boolean preUpload(File file) {
        if (file.length() >= FILE_MAX_SIZE) {
            String title = I18NServer.toLocale(PLUGINS_PORTING_TIP_FILE_TITLE);
            String content = I18NServer.toLocale("plugins_porting_webpack_file_size_exceed");
            IDENotificationUtil.notificationCommon(new NotificationBean(title, content, NotificationType.ERROR));
            return false;
        }

        // 右键直接覆盖上传
        ResponseBean responseBean = uploadInDiffMode(file, Mode.OVERRIDE);
        if (responseBean == null) {
            return false;
        }
        // 提示：文件或文件夹名中不能包含中文、空格以及^ ` / | ; & $ > < \ ! 等特殊字符，请修改后重试
        if (RespondStatus.UPLOAD_FILE_NAME_NOT_SUPPORT.value().equals(responseBean.getStatus())) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", CommonUtil.getRspTipInfo(responseBean), NotificationType.ERROR));
        }
        return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
    }

    /**
     * 源码已存在，且点击另存为的情况下，显示Dialog
     *
     * @param file        上传的源码文件
     * @param newFileName 服务端返回的对源码文件默认命名
     * @param suffix      文件后缀
     * @return boolean     是否可以上传到服务端
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
     * 上传源码已在服务端存在情况处理
     *
     * @param jsonData 服务端返回数据
     * @param file     源码文件
     * @return boolean 是否可以上传到服务端
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
        // 根据点击的按钮选择不同的操作
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.REPLACE.getKey())) {
            // 替换原文件
            ResponseBean responseBean = uploadInDiffMode(file, Mode.OVERRIDE);
            if (responseBean == null) {
                return false;
            }
            return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
        }
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.SAVE_AS.getKey())) {
            // 设置上传模式为 save_as
            IDEContext.setValueForGlobalContext(null, "fileModel", "save_as");
            // 重命名文件
            String newName = dataMap.get("new_name").toString();
            String suffix = dataMap.get("suffix").toString();
            return showRenameFileMessage(file, newName, suffix);
        }
        return false;
    }

    /**
     * 采用不同模式上传源码压缩包
     *
     * @param file 源码压缩包
     * @param mode 模式：包括 "normal"、"override"、"save_as"
     * @return ResponseBean: 服务端响应
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
     * 源码迁移前对环境进行检查
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
     * The class Mode: 上传文件的不同模式
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
