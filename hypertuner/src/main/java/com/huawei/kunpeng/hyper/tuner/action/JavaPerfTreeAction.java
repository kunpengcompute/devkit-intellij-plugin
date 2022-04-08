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

package com.huawei.kunpeng.hyper.tuner.action;

import com.huawei.kunpeng.hyper.tuner.action.javaperf.DataListAction;
import com.huawei.kunpeng.hyper.tuner.action.javaperf.JavaSamplingAction;
import com.huawei.kunpeng.hyper.tuner.action.javaperf.ReportThresholdAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaperfContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.DateUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.hyper.tuner.http.JavaProjectServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.GcLog;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.MemoryDumpReprots;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.SamplingTaskInfo;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.ThreadDumpReports;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf.ReportDeleteDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.ReportDeletePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.AddTargetEnvironmentEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ProfilingTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowGcLogEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowGuardianProcessEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowMemoryDumpEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowSamplingTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowThreadDumpEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.handler.JavaPerfHandler;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.CreateTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ImportTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ModifyProjectEditor;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.http.HttpAPIServiceTrust;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.vfs.VirtualFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * javaPerf Action
 *
 * @since 2021-07-10
 */
public class JavaPerfTreeAction extends AnAction {
    /**
     * 导入数据类型
     */
    public static String importTypeStr;
    /**
     * 导入数据标识
     */
    public static boolean isImport = false;
    /**
     * 删除数据标识
     */
    public static boolean isDeleteDataList = false;

    private String webViewPage = "";

    private JavaPerfTreeAction() {
    }

    /**
     * 创建JavaPerfTreeAction实例
     *
     * @return LeftTreeAction
     */
    public static JavaPerfTreeAction instance() {
        return new JavaPerfTreeAction();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 检查当前webView是否已经打开
        String operation = event.getPresentation().getText();

        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(getEventProject(event));
        if (operation.equals(JavaperfContent.ADD_TARGET_ENVIRONMENT)) {
            // 添加目标环境
            webViewPage = JavaperfContent.ADD_TARGET_ENVIRONMENT;
        } else if (operation.equals(GuardianMangerConstant.DISPLAY_NAME)) {
            // 目标环境管理
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), GuardianMangerConstant.DISPLAY_NAME);
        } else if (operation.equals(SysperfContent.NODE_MANAGER_DIC)) {
            // 目标环境管理
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), GuardianMangerConstant.DISPLAY_NAME);
        } else if (operation.equals(JavaperfContent.IMPORT_ANALYSIS_RECORDS)) {
            // 打开导入分析任务文件选择
            if ("ONLINE_ANALYSIS".equals(JavaPerfToolWindowPanel.type.name()) &&
                    "running".equals(JavaPerfToolWindowPanel.stopProfiling)) {
                JavaPerfTreeAction.instance().openCommonDialog("ONLINE_ANALYSIS", null, null, null);
            } else if ("ONLINE_ANALYSIS".equals(JavaPerfToolWindowPanel.type.name()) &&
                    !"running".equals(JavaPerfToolWindowPanel.stopProfiling)) {
                importOpenFileDialog(JavaPerfToolWindowPanel.type);
            } else {
                importFile(JavaPerfToolWindowPanel.type);
            }
            return;
        } else if (operation.equals(JavaperfContent.EXPORT_ANALYSIS_RECORDS)) {
            // 打开导出分析任务文件选择
            exportOpenFileDialog(JavaPerfToolWindowPanel.type);
            return;
        } else if (operation.equals(SysperfContent.TASK_TEMPLETE_DIC)) {
            // 模板管理
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.TASK_TEMPLETE_DIC);
        } else {
            webViewPage = "";
            return;
        }

        List<VirtualFile> fileList = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(webViewPage))
                .collect(Collectors.toList());

        if (fileList.isEmpty()) {
            openWebViewPage(operation);
        } else {
            instance.openFile(fileList.get(0), true);
        }
    }

    /**
     * 导出在线分析
     */
    public void exportProfiling() {
        JavaPerfHandler.exportProfiling();
    }

    /**
     * 快捷键导出文件
     *
     * @param type 导出类型
     */
    public void exportOpenFileDialog(ImportType type) {
        // 在线分析
        if (ImportType.ONLINE_ANALYSIS.equals(type)) {
            exportProfiling();
            return;
        }
        // 采样分析
        if (ImportType.SAMPLING_ANALYSIS.equals(type)) {
            String fileId = JavaPerfToolWindowPanel.selectSamplingTask.getId();
            String fileName = JavaPerfToolWindowPanel.selectSamplingTask.getName();
            if (StringUtils.contains(fileName, "/")) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }
            downloadSamplingAnalysisFile(fileId, fileName);
            return;
        }
        // 内存转储
        if (ImportType.REPORT_LIST_MEMORY_DUMP.equals(type)) {
            String fileId = JavaPerfToolWindowPanel.selectMemoryDump.getId();
            String fileName = JavaPerfToolWindowPanel.selectMemoryDump.getAlias();
            exportDataListFile(fileId, fileName, type);
            return;
        }
        // Gc日志
        if (ImportType.REPORT_LIST_GC_LOGS.equals(type)) {
            String fileId = JavaPerfToolWindowPanel.selectGcLog.getId();
            String fileName = JavaPerfToolWindowPanel.selectGcLog.getLogName();
            exportDataListFile(fileId, fileName, type);
            return;
        }
        // 线程转储
        if (ImportType.REPORT_LIST_THREAD_DUMP.equals(type)) {
            String fileId = JavaPerfToolWindowPanel.selectThreadDump.getId();
            String fileName = JavaPerfToolWindowPanel.selectThreadDump.getReportName();
            exportDataListFile(fileId, fileName, type);
            return;
        }
    }

    /**
     * 采样分析文件导出
     *
     * @param id       id
     * @param fileName 文件名
     */
    public void downloadSamplingAnalysisFile(String id, String fileName) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_sampling_analysis"));
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, CommonUtil.getDefaultProject(), null);
        if (virtualFile != null) {
            // 文件存在且选择不继续
            String path = virtualFile.getPath();
            String samplingFileName = fileName;
            if (fileName.contains(" ")) {
                samplingFileName = fileName.substring(0, fileName.indexOf(" "));
            }
            HashMap<String, String> fileNameMap = getFileHashMap(samplingFileName, path);
            if (fileNameMap == null) {
                return;
            }
            String newFileName = fileNameMap.get("newFileName");
            JavaSamplingAction javaSamplingAction = new JavaSamplingAction();
            javaSamplingAction.downloadSampling(path, newFileName, id);
        }
    }

    @Nullable
    private HashMap<String, String> getFileHashMap(String fileName, String path) {
        HashMap<String, String> fileNameMap = new HashMap<>();
        fileNameMap.put("fileName", fileName);
        fileNameMap.put("newFileName", fileName);
        if (ExportToFileUtil.isExistNotToContinue(path, fileNameMap,
                TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_sampling_analysis"))) {
            return null;
        }
        return fileNameMap;
    }

    /**
     * 数据列表文件导出
     *
     * @param id       id
     * @param fileName 文件名
     * @param type     导出类型
     */
    public void exportDataListFile(String id, String fileName, ImportType type) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_data_list"));
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, CommonUtil.getDefaultProject(), null);
        if (virtualFile != null) {
            // 文件存在且选择不继续
            String path = virtualFile.getPath();
            String downFileName = fileName;
            if (fileName.contains(" ")) {
                downFileName = fileName.substring(0, fileName.indexOf(" "));
            }
            HashMap<String, String> fileNameMap = getFileHashMap(downFileName, path);
            if (fileNameMap == null) {
                return;
            }
            String newFileName = fileNameMap.get("newFileName");
            DataListAction dataListAction = new DataListAction();
            String url = "";
            if (ImportType.REPORT_LIST_MEMORY_DUMP.equals(type)) {
                url = "/java-perf/api/heap/actions/download/";
            }
            if (ImportType.REPORT_LIST_GC_LOGS.equals(type)) {
                url = "/java-perf/api/gcLog/actions/download/";
            }
            if (ImportType.REPORT_LIST_THREAD_DUMP.equals(type)) {
                url = "/java-perf/api/threadDump/actions/download/";
            }
            dataListAction.exportDataListFile(path, newFileName, id, url);
        }
    }

    private void openWebViewPage(String operation) {
        if (operation.equals(JavaperfContent.ADD_TARGET_ENVIRONMENT)) {
            AddTargetEnvironmentEditor.openPage();
        } else if (operation.equals(SysperfContent.CREATE_TASK)) {
            CreateTaskEditor.openPage();
        } else if (operation.equals(SysperfContent.MODIFY_PROJECT)) {
            ModifyProjectEditor.openPage();
        } else if (operation.equals(SysperfContent.IMPORT_TASK)) {
            ImportTaskEditor.openPage();
        } else {
            return;
        }
    }

    /**
     * 展示目标环境进程
     */
    public void showGuardianProcess() {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(JavaPerfToolWindowPanel.selectMember.getName() + "-" +
                        JavaPerfToolWindowPanel.selectMember.getOwner().getUsername()))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            ShowGuardianProcessEditor.openPage();
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * 展示采样分析
     *
     * @param isStop boolean
     */
    public void showSamplingTask(boolean isStop) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        String samplingPage = "";
        if ("UPLOAD".equalsIgnoreCase(JavaPerfToolWindowPanel.selectSamplingTask.getSource())) {
            samplingPage = JavaPerfToolWindowPanel.selectSamplingTask.getName() + JavaperfContent.DATA_LIST_IMPORT_TIME
                    + DateUtil.getInstance().createTimeStr(JavaPerfToolWindowPanel.selectSamplingTask.getCreateTime());
        } else {
            samplingPage = JavaPerfToolWindowPanel.selectSamplingTask.getName() + JavaperfContent.DATA_LIST_CREATE_TIME
                    + DateUtil.getInstance().createTimeStr(JavaPerfToolWindowPanel.selectSamplingTask.getCreateTime());
        }
        String page = samplingPage;
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(page))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            ShowSamplingTaskEditor.openPage(new MessageBean(), false, page);
        } else {
            if (isStop) {
                instance.closeFile(collect.get(0));
                ShowSamplingTaskEditor.openPage(new MessageBean(), false, page);
            } else {
                instance.openFile(collect.get(0), true);
            }
        }
    }

    /**
     * 采样/数据列表导入文件
     *
     * @param type ImportType
     */
    public void importFile(ImportType type) {
        JSONObject thrInfo = getReportThreshold(type);
        int size = (int) thrInfo.get("dataListSize");
        int maxCount = (int) thrInfo.get("maxCount");
        int alarmCount = (int) thrInfo.get("alarmCount");
        // 实际列表值跟阈值进行判断
        if (size < alarmCount) {
            importOpenFileDialog(type);
        }
        if (alarmCount <= size && size < maxCount) {
            // 增加提示跟上传
            thresholdTip(type, true, size, alarmCount);
            importOpenFileDialog(type);
        }
        if (maxCount <= size) {
            // 增加提示并禁止上传
            thresholdTip(type, false, size, maxCount);
        }
    }

    private void thresholdTip(ImportType type, boolean isAlarm, int actualSize, int sholdSize) {
        String tip = "";
        switch (type) {
            case SAMPLING_ANALYSIS:
                if (isAlarm) {
                    tip = MessageFormat.format(JavaperfContent.SAMPLING_TIPS_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.SAMPLING_ANALYSIS,
                            tip, NotificationType.INFORMATION));
                } else {
                    tip = MessageFormat.format(JavaperfContent.SAMPLING_WARN_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.SAMPLING_ANALYSIS,
                            tip, NotificationType.WARNING));
                }
                break;
            case REPORT_LIST_THREAD_DUMP:
                if (isAlarm) {
                    tip = MessageFormat.format(JavaperfContent.THREADDUMP_TIPS_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_THREAD_DUMP,
                            tip, NotificationType.INFORMATION));
                } else {
                    tip = MessageFormat.format(JavaperfContent.THREADDUMP_WARN_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(
                            new NotificationBean(JavaperfContent.DATA_LIST_THREAD_DUMP, tip, NotificationType.WARNING));
                }
                break;
            case REPORT_LIST_MEMORY_DUMP:
                if (isAlarm) {
                    tip = MessageFormat.format(JavaperfContent.HEAPDUMP_TIPS_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_MEMORY_DUMP,
                            tip, NotificationType.INFORMATION));
                } else {
                    tip = MessageFormat.format(JavaperfContent.HEAPDUMP_WARN_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_MEMORY_DUMP,
                            tip, NotificationType.WARNING));
                }
                break;
            case REPORT_LIST_GC_LOGS:
                if (isAlarm) {
                    tip = MessageFormat.format(JavaperfContent.GCLOG_TIPS_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_GC_LOGS,
                            tip, NotificationType.INFORMATION));
                } else {
                    tip = MessageFormat.format(JavaperfContent.GCLOG_WARN_CONTENT, actualSize, sholdSize);
                    IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_GC_LOGS,
                            tip, NotificationType.WARNING));
                }
                break;
            default:
                return;
        }
    }

    private JSONObject getReportThreshold(ImportType type) {
        JSONObject jsonObject = new JSONObject();
        ReportThresholdAction reportThresholdAction = new ReportThresholdAction();
        switch (type) {
            case SAMPLING_ANALYSIS:
                JSONObject sampling = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/report/");
                jsonObject.put("maxCount", sampling.get("maxJFRCount"));
                jsonObject.put("alarmCount", sampling.get("alarmJFRCount"));
                List<SamplingTaskInfo> samplingLists = JavaProjectServer.getUserRecord();
                jsonObject.put("dataListSize", samplingLists.size());
                break;
            case REPORT_LIST_THREAD_DUMP:
                JSONObject threadDump = reportThresholdAction.getReportConfig("java-perf/api/tools/settings" +
                        "/threadDump/");
                jsonObject.put("maxCount", threadDump.get("maxThreadDumpCount"));
                jsonObject.put("alarmCount", threadDump.get("alarmThreadDumpCount"));
                List<ThreadDumpReports> memoryLists = JavaProjectServer.getUserThreadDumpReports();
                jsonObject.put("dataListSize", memoryLists.size());
                break;
            case REPORT_LIST_MEMORY_DUMP:
                JSONObject memory = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/heap/");
                jsonObject.put("maxCount", memory.get("maxHeapCount"));
                jsonObject.put("alarmCount", memory.get("alarmHeapCount"));
                List<MemoryDumpReprots> threadLists = JavaProjectServer.getUserMemoryDumpReports();
                jsonObject.put("dataListSize", threadLists.size());
                break;
            case REPORT_LIST_GC_LOGS:
                JSONObject gcLog = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/gcLog/");
                jsonObject.put("maxCount", gcLog.get("maxGcLogCount"));
                jsonObject.put("alarmCount", gcLog.get("alarmGcLogCount"));
                List<GcLog> gcLogLists = JavaProjectServer.getUserDcLogsReports();
                jsonObject.put("dataListSize", gcLogLists.size());
                break;
            default:
                break;
        }
        return jsonObject;
    }

    /**
     * 导入任务弹出选择文件弹框 1.在线、2采样、3.数据列表-线程转储、4.报告列表列表-内存转储、5.报告列表-GC日志
     *
     * @param type 类型
     */
    public void importOpenFileDialog(ImportType type) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, false, false, false);
        descriptor.setTitle(getJavaperftType(type));
        descriptor.withFileFilter(file -> {
            if (file.isDirectory()) {
                return false;
            }
            return true;
        });
        FileChooserDialog fileChooser = FileChooserFactory.getInstance()
                .createFileChooser(descriptor, CommonUtil.getDefaultProject(), null);
        String path = getVirtualFilePath(fileChooser);
        if ("".equals(path)) {
            return;
        } else {
            File file = new File(path);
            javaPerformanceAnalysisFileUpload(type, file);
        }
    }

    private String getJavaperftType(ImportType type) {
        String dataType = "";
        switch (type) {
            case SAMPLING_ANALYSIS:
                dataType = JavaperfContent.SAMPLING_ANALYSIS;
                break;
            case ONLINE_ANALYSIS:
                dataType = JavaperfContent.ONLINE_ANALYSIS;
                break;
            case REPORT_LIST_THREAD_DUMP:
                dataType = JavaperfContent.DATA_LIST_THREAD_DUMP;
                break;
            case REPORT_LIST_MEMORY_DUMP:
                dataType = JavaperfContent.DATA_LIST_MEMORY_DUMP;
                break;
            case REPORT_LIST_GC_LOGS:
                dataType = JavaperfContent.DATA_LIST_GC_LOGS;
                break;
            default:
                break;
        }
        return dataType;
    }

    private String getVirtualFilePath(FileChooserDialog fileChooser) {
        final VirtualFile[] virtualFiles = fileChooser.choose(CommonUtil.getDefaultProject(),
                VirtualFile.EMPTY_ARRAY);
        VirtualFile virtualFile = virtualFiles.length > 0 ? virtualFiles[0] : null;
        if (virtualFile == null) {
            return "";
        }
        return virtualFile.getPath().substring(0, virtualFile.getPath().lastIndexOf(virtualFile.getName()))
                + virtualFile.getName();
    }

    /**
     * Java性能分析模块文件统一上传
     *
     * @param type 导入类型
     * @param file 目标文件File对象
     * @return 文件上传成功则返回 true , 反之返回 false .
     */
    public boolean javaPerformanceAnalysisFileUpload(ImportType type, File file) {
        if (StringUtils.endsWithAny(file.getName(), type.getFileType())) {
            if (ImportType.ONLINE_ANALYSIS.equals(type)) {
                return onlineAnalysis(type, file);
            } else { // 文件上传
                ProgressManager.getInstance().run(new Task.Backgroundable(CommonUtil.getDefaultProject(),
                        ImpAndExpTaskContent.TYPE_IMP, false,
                        PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        uploadFile(type, file);
                    }
                });
                return true;
            }
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(type.getNotificationTitle(), TuningI18NServer.toLocale
                            ("plugins_hyper_tuner_javaperf_import_report_file_type_error") + Arrays.toString
                            (type.getFileType()), NotificationType.ERROR));
            return false;
        }
    }

    private boolean onlineAnalysis(ImportType type, File file) {
        if (jsonFileValidate(file)) {
            ProgressManager.getInstance().run(new Task.Backgroundable(CommonUtil.getDefaultProject(),
                    ImpAndExpTaskContent.TYPE_IMP, false,
                    PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    readJsonFile(file).orElse("{}");
                }
            });
            String jsonStr = readJsonFile(file).orElse("{}");
            String nodeName = getJsonNode(jsonStr, "/profileInfo/jvmName");
            MessageBean messageBean = new MessageBean();
            messageBean.setData(file.getPath());
            messageBean.setCbid(nodeName);
            messageBean.setCmd(getJsonNode(jsonStr, "/profileInfo/jvmId"));
            JavaPerfToolWindowPanel.stopProfiling = "running";
            JavaPerfToolWindowPanel.profilingMessage = null;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String page = nodeName + "-profiling" + TuningI18NServer.toLocale(
                    "plugins_hyper_tuner_profiling_import_time") + df.format(new Date()) + "_pro";
            ProfilingTaskEditor.openPage(messageBean, true, page);
            JavaPerfToolWindowPanel.refreshProfilingNode(page, true);
            isImport = true;
            importTypeStr = type.toString();
            return true;
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(type.getNotificationTitle(), TuningI18NServer
                            .toLocale("plugins_hyper_tuner_javaperf_import_online_analysis_report_tip"),
                            NotificationType.ERROR));
            return false;
        }
    }

    /**
     * 文件上传
     *
     * @param type 上传类型
     * @param file 文件名
     * @return 返回标志
     */
    public Boolean uploadFile(ImportType type, File file) {
        String dataType = getJavaperftType(type);
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                type.getImportInterface(), HttpMethod.POST.vaLue(), "");
        Object ctxObj = IDEContext.getValueFromGlobalContext(null, message.getModule());
        String result = "";
        boolean uploadSuccess = true;
        if (ctxObj instanceof Map) {
            Map<String, Object> context = (Map<String, Object>) ctxObj;
            String url = HttpAPIServiceTrust.getCurrentRequestUrl(context, message);
            JSONObject jsonParam = new JSONObject();
            String token = Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue()))
                    .map(Object::toString).orElse(null);
            jsonParam.put("file", file);
            try {
                result = HttpAPIServiceTrust.getResponseString(url, jsonParam, "POST", token, "TUNING");
            } catch (IOException e) {
                Logger.error("file upload exception：message is {}", e.getMessage());
            }
            Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(result);
            String id = jsonMessage.get("id");
            if (jsonMessage != null && id != null) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(dataType, dataType + " "
                                + TuningI18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_status_imp_success"),
                                NotificationType.INFORMATION));
                uploadSuccess = true;
                isImport = true;
                importTypeStr = type.toString();
                JavaPerfToolWindowPanel.isCreateSampling = true;
            } else {
                // 内存转储需要判断文件大小是否超过阈值
                if (memoryFileSizeTip(type, file, dataType, jsonMessage)) {
                    return false;
                }
                IDENotificationUtil.notificationCommon(new NotificationBean(dataType, jsonMessage.get("message"),
                        NotificationType.ERROR));
                uploadSuccess = false;
            }
        }
        return uploadSuccess;
    }

    private boolean memoryFileSizeTip(ImportType type, File file, String dataType, Map<String, String> jsonMessage) {
        if (ImportType.REPORT_LIST_MEMORY_DUMP.equals(type) &&
                jsonMessage.get("message").contains(JavaperfContent.UPDATE_MAXSIZE_FLAG)) {
            ReportThresholdAction reportThresholdAction = new ReportThresholdAction();
            JSONObject memory = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/heap/");
            String tipString = "";
            boolean isAdmin =
                    UserInfoContext.getInstance().getRole().equals(TuningUserManageConstant.USER_ROLE_ADMIN);
            if (isAdmin) {
                tipString = MessageFormat.format(JavaperfContent.HEAPDUMP_MAX_SIZE_ADMIN, file.getName(),
                        memory.get("maxHeapSize"));
            } else {
                tipString = MessageFormat.format(JavaperfContent.THREADDUMP_MAX_SIZE_NORMAL, file.getName(),
                        memory.get("maxHeapSize"));
            }
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(dataType, tipString,
                            NotificationType.WARNING));
            return true;
        }
        return false;
    }

    /**
     * Read the content of the Json file.
     *
     * @param jsonFile Json File object.
     * @return json string.
     */
    public Optional<String> readJsonFile(File jsonFile) {
        try {
            return Optional.ofNullable(IOUtils.toString(jsonFile.toURI(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            Logger.error("readJsonFile IOException.");
            return Optional.empty();
        }
    }

    /**
     * getJsonNode.
     *
     * @param jsonString jsonString
     * @param key        key
     * @return String
     */
    public String getJsonNode(String jsonString, String key) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            return StringUtils.strip(jsonNode.at(key).toString(), "\"");
        } catch (IOException e) {
            Logger.error("getJsonNode IOException.");
            return "";
        }
    }

    private boolean jsonFileValidate(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(file);
            return Stream.of("/profileInfo", "/profileInfo/jvmName", "/overview", "/overview/environment",
                    "/overview/keyword").filter(keyword -> jsonNode
                    .at(StringUtils.left(keyword, StringUtils.lastIndexOf(keyword, "/")))
                    .has(StringUtils.substringAfterLast(keyword, "/"))
            ).count() >= 5;
        } catch (IOException e) {
            Logger.error("jsonFileValidate IOException.");
            return false;
        }
    }

    /**
     * 导入类型枚举,便于根据导入类型读取指定文件类型 & 上传接口URL  & 通知信息标题头
     */
    public enum ImportType {
        /**
         * 在线分析
         */
        ONLINE_ANALYSIS(new String[]{".json"},
                "",
                TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_import_online_analysis_report")
        ),
        /**
         * 采样分析
         */
        SAMPLING_ANALYSIS(new String[]{""},
                "/java-perf/api/records/actions/upload",
                TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_import_sampling_analysis_report")
        ),
        /**
         * 报告列表-线程转储
         */
        REPORT_LIST_THREAD_DUMP(new String[]{""},
                "/java-perf/api/threadDump/actions/upload",
                TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_import_report_list_thread_dump_report")
        ),
        /**
         * 报告列表-内存转储
         */
        REPORT_LIST_MEMORY_DUMP(new String[]{""},
                "/java-perf/api/heap/actions/upload",
                TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_import_report_list_memory_dump_report")
        ),
        /**
         * 报告列表-GC日志
         */
        REPORT_LIST_GC_LOGS(new String[]{""},
                "/java-perf/api/gcLog/actions/upload",
                TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_import_report_list_gc_logs_report")
        );

        /**
         * 对应导入接口的文件类型
         */
        private String[] fileType;

        /**
         * 导入的接口 URL
         */
        private String importInterface;

        /**
         * 通知消息标题
         */
        private String notificationTitle;

        ImportType(String[] fileType, String importInterface, String notificationTitle) {
            this.fileType = fileType;
            this.importInterface = importInterface;
            this.notificationTitle = notificationTitle;
        }

        public String[] getFileType() {
            return fileType;
        }

        public String getImportInterface() {
            return importInterface;
        }

        public String getNotificationTitle() {
            return notificationTitle;
        }
    }

    private void javaPerAnalysisRspMes(String title, ResponseBean resp) {
        if (resp.getCode() != null) {
            IDENotificationUtil.notificationCommon(new NotificationBean(title, resp.getMessage(),
                    NotificationType.INFORMATION));
        } else {
            IDENotificationUtil.notificationCommon(new NotificationBean(title,
                    title + TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_import_analysis_success"),
                    NotificationType.INFORMATION));
        }
    }

    /**
     * 停止在线分析
     */
    public void stopProfilingTask() {
        JavaPerfTreeAction.instance().openCommonDialog("stopProfiling", null, null, null);
    }

    /**
     * 数据列表详情
     *
     * @param type 类型
     */
    public void showDataDetail(String type) {
        String page = "";
        if ("memoryGump".equals(type)) {
            if ("IMPORT".equalsIgnoreCase(JavaPerfToolWindowPanel.selectMemoryDump.getSource())) {
                page = JavaPerfToolWindowPanel.selectMemoryDump.getAlias() + JavaperfContent.DATA_LIST_IMPORT_TIME +
                        DateUtil.getInstance().getLongToTime(JavaPerfToolWindowPanel.selectMemoryDump.getCreateTime());
            } else {
                page = JavaPerfToolWindowPanel.selectMemoryDump.getAlias() + JavaperfContent.DATA_LIST_CREATE_TIME +
                        DateUtil.getInstance().getLongToTime(JavaPerfToolWindowPanel.selectMemoryDump.getCreateTime());
            }
            dealTreeClick("showMemoryDump", page);
        } else if ("gcLog".equals(type)) {
            if ("IMPORT".equalsIgnoreCase(JavaPerfToolWindowPanel.selectGcLog.getReportSource())) {
                page = JavaPerfToolWindowPanel.selectGcLog.getLogName() + JavaperfContent.DATA_LIST_IMPORT_TIME +
                        DateUtil.getInstance().getLongToTime(JavaPerfToolWindowPanel.selectGcLog.getCreateTime());
            } else {
                page = JavaPerfToolWindowPanel.selectGcLog.getLogName() + JavaperfContent.DATA_LIST_CREATE_TIME +
                        DateUtil.getInstance().getLongToTime(JavaPerfToolWindowPanel.selectGcLog.getCreateTime());
            }
            dealTreeClick("showGcLogEditor", page);
        } else {
            if ("IMPORT".equalsIgnoreCase(JavaPerfToolWindowPanel.selectThreadDump.getReportSource())) {
                page = JavaPerfToolWindowPanel.selectThreadDump.getReportName()
                        + JavaperfContent.DATA_LIST_IMPORT_TIME +
                        DateUtil.getInstance().getLongToTime(JavaPerfToolWindowPanel.selectThreadDump.getCreateTime());
            } else {
                page = JavaPerfToolWindowPanel.selectThreadDump.getReportName()
                        + JavaperfContent.DATA_LIST_CREATE_TIME +
                        DateUtil.getInstance().getLongToTime(JavaPerfToolWindowPanel.selectThreadDump.getCreateTime());
            }
            dealTreeClick("showThreadDump", page);
        }
    }

    private void dealTreeClick(String type, String page) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(page))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            switch (type) {
                case "showGcLogEditor":
                    ShowGcLogEditor.openPage(page);
                    break;
                case "showMemoryDump":
                    ShowMemoryDumpEditor.openPage(page);
                    break;
                case "showThreadDump":
                    ShowThreadDumpEditor.openPage(page);
                    break;
                default:
                    return;
            }
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * 删出数据列表
     *
     * @param url 地址
     */
    public void deleteDataList(String url) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                url, HttpMethod.DELETE.vaLue(), "");
        TuningHttpsServer.INSTANCE.requestData(message);
        isDeleteDataList = true;
    }

    /**
     * 通用对话框
     *
     * @param type     类型
     * @param id       id
     * @param name     title
     * @param userName 用户
     */
    public void openCommonDialog(String type, String id, String name, String userName) {
        ReportDeleteDialog dialog = new ReportDeleteDialog(type, id, name,
                new ReportDeletePanel(type, name, userName));
        dialog.displayPanel();
    }
}
