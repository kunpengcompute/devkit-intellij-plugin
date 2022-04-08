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

package com.huawei.kunpeng.porting.http.module.pkgrebuild;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.bean.AnalysisTaskBean;
import com.huawei.kunpeng.porting.bean.PkgRebuildingReportBean;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * AnalysisServer: 软件包重构与后端交互类
 *
 * @since v1.0
 */
public class PkgRebuildingHandler {
    /**
     * 下载URL。
     */
    private static final String DOWNLOAD_URL = "/portadv/download/";

    /**
     * 删除URL
     */
    private static final String DELETE_URL = "/portadv/autopack/history/";

    /**
     * 下载文件所在目录参数。
     */
    private static final String SUB_PATH_PARAMS = "sub_path";

    /**
     * 下载文件参数。
     */
    private static final String FILE_PATH_PARAMS = "file_path";

    private static final String LOG_DIR = "report/packagerebuild";

    private static final String ICON_SUCCESS = "assets/img/analysis/icon_success.svg";
    private static final String ICON_FAILURE = "assets/img/analysis/icon_failure.svg";
    private static final String ICON_NO_DATA = "assets/img/default-page/light-nodata-intellij.png";

    private static final String WINDOW_PATH_SEPARATOR = "\\\\";

    /**
     * 获取所有源码扫描任务
     *
     * @return List
     */
    public static List<AnalysisTaskBean.Task> selectAllTasks() {
        Logger.info("Start obtain all tasks.");
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/autopack/history/", HttpMethod.GET.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        Logger.info("Get tasks, response: {}", response);
        if (response == null) {
            return new ArrayList<>();
        }
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(response.getStatus())) {
            Logger.error("Get tasks error");
        }
        // 处理获取到的后端数据
        AnalysisTaskBean analysisTaskBean = JSONObject.parseObject(response.getData(), AnalysisTaskBean.class);
        return analysisTaskBean.getTaskList();
    }

    /**
     * 下载报名
     *
     * @param path             下载路劲
     * @param taskId           任务ID
     * @param packageName      下载包名
     * @param downloadFileName downloadFileName
     */
    public static void download(String path, String taskId, String packageName, String downloadFileName) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            DOWNLOAD_URL, HttpMethod.POST.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(false);
        message.setDownloadPtah(path);
        message.setDownloadFileName(downloadFileName);
        Map<String, String> map = new HashMap<>();
        map.put(SUB_PATH_PARAMS, LOG_DIR);
        map.put(FILE_PATH_PARAMS, taskId + PortingIDEConstant.PATH_SEPARATOR + packageName);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(map));
        PortingHttpsServer.INSTANCE.requestData(message);
        doSuccess(path);
    }

    /**
     * 下载软件包
     *
     * @param taskId      taskId
     * @param packageName packageName
     */
    public static void downloadPackage(String taskId, String packageName) {
        // 弹出弹框
        FileSaverDialog dialog = FileChooserFactory.getInstance()
            .createSaveFileDialog(new FileSaverDescriptor("Save File", "Select local file"),
                CommonUtil.getDefaultProject());
        VirtualFileWrapper fileWrapper = dialog.save(
            LocalFileSystem.getInstance().findFileByPath(CommonUtil.getDefaultProject().getBasePath()), packageName);
        if (fileWrapper == null) {
            return;
        }
        // 文件名称
        String path = fileWrapper.getFile().getParent();
        String downloadFileName = fileWrapper.getFile().getName();
        PkgRebuildingHandler.download(path, taskId, packageName, downloadFileName);
    }

    /**
     * 下载成功处理
     *
     * @param path 下载路径。
     */
    private static void doSuccess(String path) {
        Project project = CommonUtil.getDefaultProject();
        NotificationBean notificationBean
            = new NotificationBean(I18NServer.toLocale("plugins_porting_software_rebuilding_title"),
            I18NServer.toLocale("plugins_porting_csrdownload_success") + PortingIDEConstant.HTML_HEAD
                + path + PortingIDEConstant.HTML_FOOT, NotificationType.INFORMATION);
        notificationBean.setProject(project);
        IDENotificationUtil.notificationForHyperlink(notificationBean, new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                CommonUtil.showFileDirOnDesktop(path);
            }
        });
    }

    /**
     * 刷新软件包结果到左侧树。
     */
    public static void refreshRebuildResults() {
        LeftTreeUtil.refreshReports();
    }

    /**
     * 删除软件重构包
     * 请求类型：DELETE
     * 关键参数： {name: bigtop-jsvc-1.0.15-78.aarch64.rpm path: 20201228144158}
     *
     * @param path        path
     * @param packageName packageName
     */
    public static void deletePortingTask(String path, String packageName) {
        HashMap<String, String> urlParamsMap = new HashMap<>();
        urlParamsMap.put("name", packageName);
        urlParamsMap.put("path", path);
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, DELETE_URL,
            HttpMethod.DELETE.vaLue(), "");
        data.setUrlParams(JsonUtil.getJsonStrFromJsonObj(urlParamsMap));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        Logger.info("delete porting Task, Response: {}", responseBean);
        if (responseBean == null) {
            return;
        }
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value()
            .equals(responseBean.getStatus())
            ? NotificationType.INFORMATION
            : NotificationType.ERROR;

        String notContent = I18NServer.respToLocale(responseBean);
        IDENotificationUtil.notificationCommon(
            new NotificationBean(I18NServer.toLocale("plugins_porting_software_rebuilding_title"),
                notContent, notificationType));
        refreshRebuildResults();
    }

    /**
     * 删除所有软件重构包
     */
    public static void deleteAllTask() {
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, DELETE_URL,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        Logger.info("delete porting Task, Response: {}", responseBean);
        if (responseBean == null) {
            return;
        }
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())
            ? NotificationType.INFORMATION
            : NotificationType.ERROR;
        String notContent = I18NServer.respToLocale(responseBean);
        IDENotificationUtil.notificationCommon(
            new NotificationBean(I18NServer.toLocale("plugins_porting_software_rebuilding_title"),
                notContent, notificationType));
        refreshRebuildResults();
    }

    /**
     * 加载HTML文件。
     *
     * @param data 后端返回的数据data
     * @return 返回下载HTML内容
     */
    public static String loadHtml(String data) {
        // 获取html下载模板
        String path = CommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WEB_VIEW_PATH)
            + PortingIDEConstant.PATH_SEPARATOR
            + PortingIDEConstant.TOOL_NAME_PORTING + PortingIDEConstant.PATH_SEPARATOR
            + PortingIDEConstant.WEB_VIEW_ANALYSIS_REPORT_TEMPLATE_HTML;
        String analysisReportHtml = FileUtil.readFileContent(path);
        analysisReportHtml = analysisReportHtml.replaceFirst("base href=\"\\./\"", "base href=\"\"");

        // 解析后端返回的报告数据
        PkgRebuildingReportBean
            pkgRebuildingReportBean = JSONObject.parseObject(data, PkgRebuildingReportBean.class);

        String packageName = pkgRebuildingReportBean.getPackagePath();
        int lastIntdex = packageName.lastIndexOf(PortingIDEConstant.PATH_SEPARATOR);
        packageName = packageName.substring(lastIntdex + 1);
        // 替换扫描参数信息
        analysisReportHtml = analysisReportHtml.replace("#Package_name", packageName);
        analysisReportHtml =
            analysisReportHtml.replace("#Report_Generated", pkgRebuildingReportBean.getReportTime());
        analysisReportHtml =
            analysisReportHtml.replace("#Package_Path", pkgRebuildingReportBean.getPackagePath());
        if (Objects.equals(pkgRebuildingReportBean.getStatus(), "0")) {
            String resultPath = MessageFormat.format(
                I18NServer.toLocale("plugins_porting_rebuilding_temp_rebuild_successful_package_div"),
                pkgRebuildingReportBean.getResultPath());
            analysisReportHtml = analysisReportHtml.replace("#Software_Package_Rebuilt_Path", resultPath);
        } else {
            analysisReportHtml = analysisReportHtml.replace("#Software_Package_Rebuilt_Path", "");
        }
        String rebuildResult = I18NServer.analysisToLocale(pkgRebuildingReportBean);
        rebuildResult = rebuildResult == null ?
            I18NServer.toLocale("plugins_porting_rebuilding_temp_rebuild_successful") : rebuildResult;
        analysisReportHtml = analysisReportHtml.replace("#Rebuild_Result", rebuildResult);

        List<PkgRebuildingReportBean.MissingFile> missingFileList = pkgRebuildingReportBean.getMissing();
        List<PkgRebuildingReportBean.ReplacedFile> replaceFileList = pkgRebuildingReportBean.getReplaced();
        int totalCount = missingFileList.size() + replaceFileList.size();
        analysisReportHtml = analysisReportHtml.replace("#Total_Dependencies", String.valueOf(totalCount));
        analysisReportHtml = analysisReportHtml.replace("#Dependencies_Updated",
            String.valueOf(replaceFileList.size()));
        analysisReportHtml = analysisReportHtml.replace("#Dependencies_Missing",
            String.valueOf(missingFileList.size()));
        analysisReportHtml = replaceReplaceFile(analysisReportHtml, replaceFileList);

        if (Objects.equals(pkgRebuildingReportBean.getStatus(), "0")) {
            analysisReportHtml = analysisReportHtml.replace("#dependencies_missing_table", "");
        } else {
            analysisReportHtml = analysisReportHtml.replace("#dependencies_missing_table",
                I18NServer.toLocale("plugins_porting_rebuilding_temp_dependencies_missing_table"));
            analysisReportHtml = replaceMissingFile(analysisReportHtml, missingFileList);
        }

        analysisReportHtml = addTitle(analysisReportHtml);
        return replaceImg(analysisReportHtml, pkgRebuildingReportBean);
    }

    private static String replaceImg(String analysisReportHtml,
        PkgRebuildingReportBean pkgRebuildingReportBean) {
        String imgPath = CommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WEB_VIEW_PATH)
            + PortingIDEConstant.PATH_SEPARATOR
            + PortingIDEConstant.TOOL_NAME_PORTING + PortingIDEConstant.PATH_SEPARATOR;
        if (Objects.equals(pkgRebuildingReportBean.getStatus(), "0")) {
            imgPath = imgPath + ICON_SUCCESS;
        } else {
            imgPath = imgPath + ICON_FAILURE;
        }
        imgPath = imgPath.replace(WINDOW_PATH_SEPARATOR, PortingIDEConstant.PATH_SEPARATOR);

        String result = analysisReportHtml.replace("#result_icon", imgPath);
        String noDataImg = CommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WEB_VIEW_PATH)
            + PortingIDEConstant.PATH_SEPARATOR
            + PortingIDEConstant.TOOL_NAME_PORTING + PortingIDEConstant.PATH_SEPARATOR
            + ICON_NO_DATA;
        noDataImg = noDataImg.replace("\\\\", PortingIDEConstant.PATH_SEPARATOR);
        return result.replace("#icon_no_data", noDataImg);
    }

    private static String replaceReplaceFile(String analysisReportHtml,
        List<PkgRebuildingReportBean.ReplacedFile> replaceFileList) {
        String result = analysisReportHtml;
        if (ValidateUtils.isEmptyCollection(replaceFileList)) {
            return result.replace("#updated_files_body",
                    I18NServer.toLocale("plugins_porting_rebuilding_temp_no_data_tr"));
        }

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (PkgRebuildingReportBean.ReplacedFile replacedFile : replaceFileList) {
            String statusStr = getStrByStatus(replacedFile.getStatus(), replacedFile.getName());
            String tr = MessageFormat.format(I18NServer.toLocale("plugins_porting_rebuilding_temp_updated_files_tr"),
                count++, replacedFile.getName(), replacedFile.getPath(), statusStr);
            sb.append(tr);
        }
        return result.replace("#updated_files_body", sb.toString());
    }

    private static String replaceMissingFile(String analysisReportHtml,
        List<PkgRebuildingReportBean.MissingFile> missingFileList) {
        String result = analysisReportHtml;

        if (ValidateUtils.isEmptyCollection(missingFileList)) {
            return result.replace("#missing_files_body",
                    I18NServer.toLocale("plugins_porting_rebuilding_temp_no_data_tr"));
        }

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (PkgRebuildingReportBean.MissingFile missingFile : missingFileList) {
            String url = missingFile.getUrl();
            String operationStr = I18NServer.toLocale(
                "plugins_porting_rebuilding_temp_missing_files_operation_empty_td");
            if (StringUtil.isNotEmpty(url)) {
                operationStr = I18NServer.toLocale("plugins_porting_rebuilding_temp_missing_files_operation_td")
                    .replace("#URL", url);
            }
            String statusStr = getStrByStatus(missingFile.getStatus(), missingFile.getName());
            String tr = MessageFormat.format(I18NServer.toLocale("plugins_porting_rebuilding_temp_missing_files_tr"),
                count++, missingFile.getName(), missingFile.getPath(), statusStr, operationStr);
            sb.append(tr);
        }
        result = result.replace("#missing_files_body", sb.toString());
        return result;
    }

    private static String getStrByStatus(int status, String name) {
        switch (status) {
            case 0:
                return I18NServer.toLocale("plugins_porting_rebuilding_temp_tool_download_title");
            case 1:
                return I18NServer.toLocale("plugins_porting_rebuilding_temp_user_upload_title");
            default:
                if (StringUtil.isEmpty(name)) {
                    return I18NServer.toLocale("plugins_porting_rebuilding_temp_report_level_result");
                }
                if (name.endsWith(".jar")) {
                    return I18NServer.toLocale("plugins_porting_rebuilding_temp_suggestion_1");
                } else {
                    return I18NServer.toLocale("plugins_porting_rebuilding_temp_suggestion");
                }
        }
    }

    private static String addTitle(String analysisReportHtml) {
        String result = analysisReportHtml.replace("#rebuild_information_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_rebuild_information_title"));

        result = result.replace("#package_path_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_package_path_title"));

        result = result.replace("#report_generated_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_report_generated_title"));

        result = result.replace("#rebuild_result_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_rebuild_result_title"));

        result = result.replace("#total_dependencies_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_total_dependencies_title"));

        result = result.replace("#dependencies_updated_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_dependencies_updated_title"));

        result = result.replace("#dependencies_missing_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_dependencies_missing_title"));

        result = result.replace("#no_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_no_title"));

        result = result.replace("#file_name_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_file_name_title"));

        result = result.replace("#path_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_path_title"));

        result = result.replace("#file_source_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_file_source_title"));

        result = result.replace("#file_source_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_file_source_title"));

        result = result.replace("#handling_suggestions_title",
            I18NServer.toLocale("plugins_porting_rebuilding_temp_handling_suggestions_title"));

        return result.replace("#operation_title",
                I18NServer.toLocale("plugins_porting_rebuilding_temp_operation_title"));
    }
}
