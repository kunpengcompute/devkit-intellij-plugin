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

package com.huawei.kunpeng.hyper.tuner.webview.tuning.handler;

import com.huawei.kunpeng.hyper.tuner.action.JavaPerfTreeAction;
import com.huawei.kunpeng.hyper.tuner.action.javaperf.ReportThresholdAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaperfContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.WebViewUtil;
import com.huawei.kunpeng.hyper.tuner.http.JavaProjectServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningLoginWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ProfilingTaskEditor;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ShellTerminalUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @since 2021-08-03
 */
public class JavaPerfHandler extends FunctionHandler {
    /**
     * ????????????????????????
     */
    public static boolean isSave = false;
    /**
     * ????????????title
     */
    public static List<String> GUARDIAN_PAGE = new ArrayList<>();
    private static MessageBean message;

    /**
     * ??????????????????
     */
    public static void stopProfilingIntellij() {
        for (String pageId : GUARDIAN_PAGE) {
            invokeCallback("stopProfiling", "21455123456#ShowGuardianProcessWebView" + pageId, "true");
        }
        invokeCallback("stopProfiling", "21455123456#ShowProfilingTaskWebView", "true");
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    public static void sendDiscAlarm() {
        boolean flag = TuningLoginWrapDialog.getDiscAlarm(false);
        for (String pageId : GUARDIAN_PAGE) {
            invokeCallback("sendProfilingStata", "21455123456#ShowGuardianProcessWebView" + pageId,
                    String.valueOf(flag));
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    public static void sendProfilingCurrentStata() {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(JavaPerfToolWindowPanel.profilingMessage.getData());
        map.put("status", "true");
        map.put("id", String.valueOf(JsonUtil.getJsonObjFromJsonStr(String.valueOf(data.get("message"))).get("jvmId")));
        for (String pageId : GUARDIAN_PAGE) {
            invokeCallback("qryProfilingCurrentStatus", "21455123456#ShowGuardianProcessWebView" + pageId,
                    JsonUtil.getJsonStrFromJsonObj(map));
        }
    }

    /**
     * ????????????
     *
     * @param stata ??????
     */
    public static void startProfiling(boolean stata) {
        if (stata) {
            invokeCallback(message.getCmd(), message.getCbid(), "true");
        } else {
            invokeCallback(message.getCmd(), message.getCbid(), "false");
        }
    }

    /**
     * ????????????????????????????????????
     */
    public static void refreshGuardinsByIDE() {
        for (String guardianId : GUARDIAN_PAGE) {
            String result = JavaProjectServer.qryGuardianDetail(guardianId);
            if (result.isEmpty()) {
                return;
            }
            invokeCallback("refreshGuardinsByIDE", "21455123456#ShowGuardianProcessWebView" + guardianId, result);
        }
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    public static void queryGuardinsByIDE() {
        String result = JavaProjectServer.queryGuardins();
        if (result.isEmpty()) {
            return;
        }
        invokeCallback("queryGuardinsByIDE", "21455123456#ShowProfilingTaskWebView", "true");
    }

    /**
     * ??????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public static void exportProfiling(MessageBean message, String module) {
        invokeCallback(message.getCmd(), message.getCbid(), "true");
    }

    /**
     * ????????????????????????
     */
    public static void exportProfiling() {
        invokeCallback("exportProfiling", "21455123456#ShowProfilingTaskWebView", null);
    }

    /**
     * ????????????????????????
     *
     * @param type type
     * @param mode mode
     */
    public static void updateReportConfig(String type, String mode) {
        ReportThresholdAction reportThresholdAction = new ReportThresholdAction();
        JSONObject heapReportConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings" +
                "/heap/");
        Integer maxHeapCount = Integer.valueOf(heapReportConfig.getString("maxHeapCount"));
        Integer heapReportNum = JavaProjectServer.getUserMemoryDumpReports().size();
        JSONObject gcReportConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/gcLog/");
        Integer maxGcLogCount = Integer.valueOf(gcReportConfig.getString("maxGcLogCount"));
        Integer gclogReportNum = JavaProjectServer.getUserDcLogsReports().size();
        JSONObject threadHistoryConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings" +
                "/threadDump/");
        Integer maxThreadDumpCount = Integer.valueOf(threadHistoryConfig.getString("maxThreadDumpCount"));
        Integer threadReportNum = JavaProjectServer.getUserThreadDumpReports().size();
        switch (type) {
            case "memoryGump":
                Map<String, Object> memoryParams = new HashMap<>();
                memoryParams.put("maxHeapCount", maxHeapCount);
                memoryParams.put("heapReportNum", heapReportNum);
                addOrDel(mode, "sendHeapdumpReport", memoryParams, heapReportNum, maxHeapCount);
                break;
            case "gcLog":
                Map<String, Object> gcParams = new HashMap<>();
                gcParams.put("maxGcLogCount", maxGcLogCount);
                gcParams.put("gclogReportNum", gclogReportNum);
                addOrDel(mode, "sendGClogReport", gcParams, gclogReportNum, maxGcLogCount);
                break;
            default:
                Map<String, Object> threadParams = new HashMap<>();
                threadParams.put("maxThreadDumpCount", maxThreadDumpCount);
                threadParams.put("threadReportNum", threadReportNum);
                addOrDel(mode, "sendThreaddumpReport", threadParams, threadReportNum, maxThreadDumpCount);
                break;
        }
    }

    /**
     * ???????????????
     *
     * @param mode   mode
     * @param cmd    cmd
     * @param params params
     * @param num1   num1
     * @param num2   num2
     */
    public static void addOrDel(String mode, String cmd, Map<String, Object> params, Integer num1, Integer num2) {
        if ("add".equals(mode)) {
            if (num2 <= num1 + 1) {
                invokeCallback(cmd,
                        "21455123456#ShowProfilingTaskWebView", JsonUtil.getJsonStrFromJsonObj(params));
            }
        } else if ("delete".equals(mode)) {
            if (num2 == num1 + 1) {
                invokeCallback(cmd,
                        "21455123456#ShowProfilingTaskWebView", JsonUtil.getJsonStrFromJsonObj(params));
            }
        } else {
            Logger.info("Type mismatch");
        }
    }

    /**
     * checkProfilingCurrentStata
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void checkProfilingCurrentStata(MessageBean message, String module) {
        this.message = message;
        if (JavaPerfToolWindowPanel.checkProfilingStata()) {
            JavaPerfTreeAction.instance().openCommonDialog("startProfiling", null, null, null);
        } else {
            invokeCallback(message.getCmd(), message.getCbid(), "true");
        }
    }

    /**
     * setGlobleState
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void setGlobleState(MessageBean message, String module) {
        JSONObject data = JsonUtil.getJsonObjectFromJsonStr(message.getData());
        data = data.getJSONObject("data");
        Map<Object, Object> map = new HashMap();
        JSONArray list = data.getJSONArray("list");
        list.stream().forEach((object) -> {
            if (object instanceof Map) {
                // ????????????state??????
                Object key = map.get("key");
                Object value = map.get("value");
                map.put(key, value);
            }
        });
        IDEContext.setValueForGlobalContext(null, TuningIDEConstant.JAVA_WEB_VIEW_GLOBLE_STATE, map);
        invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(map));
    }

    /**
     * webview?????????????????????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void showJavaPerfInfoBox(MessageBean message, String module) {
        Logger.info("showJavaPerfInfoBox start");
        Map<String, String> map = JsonUtil.getJsonObjFromJsonStr(message.getData());
        switch (map.get("type")) {
            case "error":
                showFailInfo(map);
                break;
            case "warn":
                showWarnInfo(map);
                break;
            default:
                if ("cancel".equals(map.get("info"))) {
                    WebViewUtil.closeCurrentPage();
                } else {
                    showSuccessInfo(map);
                }
                break;
        }
        Logger.info("showJavaPerfInfoBox end.");
    }

    private void showSuccessInfo(Map<String, String> data) {
        switch (data.get("operation")) {
            case "AddTargetEnvironment":
                JavaPerfToolWindowPanel.addGuardiansFinally(data.get("info"));
                IDENotificationUtil.notificationCommon(new NotificationBean(GuardianMangerConstant.GUARDIAN_ADD_TITLE,
                        TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_add_success"),
                        NotificationType.INFORMATION));
                WebViewUtil.closePage(JavaperfContent.ADD_TARGET_ENVIRONMENT + "."
                        + TuningIDEConstant.TUNING_KPHT);
                break;
            case "dumpHandle":
                IDENotificationUtil.notificationCommon(new NotificationBean(
                        JavaProviderSettingConstant.DUMPHANDLE_TITLE,
                        TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_dumpHandle_success"),
                        NotificationType.INFORMATION));
                break;
            case "ioSnapshot":
                IDENotificationUtil.notificationCommon(new NotificationBean(JavaProviderSettingConstant.SNAPSHOT_TITLE,
                        TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_snapshot_success"),
                        NotificationType.INFORMATION));
                break;
            case "dataLimitation":
                IDENotificationUtil.notificationCommon(new NotificationBean(
                        JavaProviderSettingConstant.DATA_LIMIT_TITLE,
                        TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_dataLimit_success"),
                        NotificationType.INFORMATION));
                break;
            case "JFRCount":
                // ??????????????????
                IDENotificationUtil.notificationCommon
                        (new NotificationBean("", data.get("info"), NotificationType.INFORMATION));
                break;
            case "saveData":
                IDENotificationUtil.notificationCommon
                        (new NotificationBean("", JavaProviderSettingConstant.SAVE_REPORT,
                                NotificationType.INFORMATION));
                isSave = true;
                break;
            default:
                break;
        }
    }

    private void showWarnInfo(Map<String, String> data) {
        switch (data.get("operation")) {
            case "dataLimitation":
                IDENotificationUtil.notificationCommon(new NotificationBean(
                        JavaProviderSettingConstant.DATA_LIMIT_TITLE,
                        TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_dataLimit_warn"),
                        NotificationType.WARNING));
                break;
            case "JFRCount":
                // ????????????????????????
                // $FALL-THROUGH$
            case "saveData":
                // ????????????
                IDENotificationUtil.notificationCommon
                        (new NotificationBean("", data.get("info"), NotificationType.WARNING));
                updateReportConfig("memoryGump", "add");
                updateReportConfig("gcLog", "add");
                updateReportConfig("threadDump", "add");
                break;
            default:
                break;
        }
    }

    private void showFailInfo(Map<String, String> data) {
        switch (data.get("operation")) {
            case "AddTargetEnvironment":
                IDENotificationUtil.notificationCommon(new NotificationBean(GuardianMangerConstant.GUARDIAN_ADD_TITLE,
                        data.get("info"), NotificationType.ERROR));
                break;
            case "killThread":
                JavaPerfToolWindowPanel.refreshProfilingNode("", false);
                IDENotificationUtil.notificationCommon(new NotificationBean("",
                        data.get("info"), NotificationType.ERROR));
                break;
            case "samplingTip":
                IDENotificationUtil.notificationCommon(new NotificationBean("", data.get("info"),
                        NotificationType.ERROR));
                break;
            default:
                break;
        }
    }

    /**
     * getGlobleState
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void getGlobleState(MessageBean message, String module) {
        Map<Object, Object> obj = IDEContext.getValueFromGlobalContext(null,
                TuningIDEConstant.JAVA_WEB_VIEW_GLOBLE_STATE);
        Map<String, Object> context = IDEContext.getValueFromGlobalContext(null, "tuning");
        String token = Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue()))
                .map(Object::toString).orElse(null);
        if (obj == null) {
            obj = new HashMap<Object, Object>();
        }
        obj.put("sysPerfToken", token);
        invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(obj));
    }

    /**
     * webview?????????????????????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void checkThreaddumpReportThreshold(MessageBean message, String module) {
        ReportThresholdAction reportThresholdAction = new ReportThresholdAction();
        JSONObject threadHistoryConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings" +
                "/threadDump/");
        Integer maxThreadDumpCount = Integer.valueOf(threadHistoryConfig.getString("maxThreadDumpCount"));
        Integer alarmThreadDumpCount = Integer.valueOf(threadHistoryConfig.getString("alarmThreadDumpCount"));
        Integer threadReportNum = JavaProjectServer.getUserThreadDumpReports().size();
        String tips = "";
        if (maxThreadDumpCount == threadReportNum) {
            tips = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_data_thread_tip");
            tips = tips.replace("{0}", maxThreadDumpCount.toString());
            tips = tips.replace("{1}", threadReportNum.toString());
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.WARNING));
        } else if (threadReportNum >= alarmThreadDumpCount && threadReportNum < maxThreadDumpCount) {
            tips = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_data_thread_hints_tip");
            tips = tips.replace("{0}", alarmThreadDumpCount.toString());
            tips = tips.replace("{1}", threadReportNum.toString());
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.WARNING));
        } else {
            Logger.warn("Type mismatch");
        }
        JSONObject data = JsonUtil.getJsonObjectFromJsonStr(message.getData());
        // ??????
        invokeCallback(message.getCmd(), message.getCbid(), "true");
    }

    /**
     * webview?????????????????????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void checkHeapdumpReportThreshold(MessageBean message, String module) {
        ReportThresholdAction reportThresholdAction = new ReportThresholdAction();
        JSONObject heapReportConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/heap/");
        Integer maxHeapCount = Integer.valueOf(heapReportConfig.getString("maxHeapCount"));
        Integer alarmHeapCount = Integer.valueOf(heapReportConfig.getString("alarmHeapCount"));
        Integer heapReportNum = JavaProjectServer.getUserMemoryDumpReports().size();
        String tips = "";
        if (maxHeapCount == heapReportNum) {
            tips = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_data_Heapdump_tip");
            tips = tips.replace("{0}", maxHeapCount.toString());
            tips = tips.replace("{1}", heapReportNum.toString());
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.WARNING));
        } else if (heapReportNum >= alarmHeapCount && heapReportNum < maxHeapCount) {
            tips = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_data_Heapdump_hints_tip");
            tips = tips.replace("{0}", alarmHeapCount.toString());
            tips = tips.replace("{1}", heapReportNum.toString());
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.WARNING));
        } else {
            Logger.warn("Type mismatch");
        }
        JSONObject data = JsonUtil.getJsonObjectFromJsonStr(message.getData());

        // ??????
        invokeCallback(message.getCmd(), message.getCbid(), "false");
    }

    /**
     * webview?????????????????????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void checkGclogReportThreshold(MessageBean message, String module) {
        ReportThresholdAction reportThresholdAction = new ReportThresholdAction();
        JSONObject gcReportConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/gcLog/");
        Integer maxGcLogCount = Integer.valueOf(gcReportConfig.getString("maxGcLogCount"));
        Integer alarmGcLogCount = Integer.valueOf(gcReportConfig.getString("alarmGcLogCount"));
        Integer gclogReportNum = JavaProjectServer.getUserDcLogsReports().size();
        String tips = "";
        if (maxGcLogCount == gclogReportNum) {
            tips = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_data_gclog_tip");
            tips = tips.replace("{0}", maxGcLogCount.toString());
            tips = tips.replace("{1}", gclogReportNum.toString());
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.WARNING));
        } else if (gclogReportNum >= alarmGcLogCount && gclogReportNum < maxGcLogCount) {
            tips = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_data_gclog_hints_tip");
            tips = tips.replace("{0}", alarmGcLogCount.toString());
            tips = tips.replace("{1}", gclogReportNum.toString());
            IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.WARNING));
        } else {
            Logger.warn("Type mismatch");
        }
        JSONObject data = JsonUtil.getJsonObjectFromJsonStr(message.getData());
        // ??????
        invokeCallback(message.getCmd(), message.getCbid(), "true");
    }

    /**
     * ????????????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void openJavaProfilingPage(MessageBean message, String module) {
        // ??????
        invokeCallback(message.getCmd(), message.getCbid(), "true");
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        // ????????????webView??????????????????
        String pageName = String.valueOf(data.get("viewTitle").replaceAll(" ", "-"))
                .replaceAll(":", "-") + "_pro";
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(pageName))
                .collect(Collectors.toList());
        JavaPerfToolWindowPanel.stopProfiling = "running";
        if (collect.isEmpty()) {
            ProfilingTaskEditor.openPage(message, false, pageName);
            sendProfilingCurrentStata();
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * webview?????????????????????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void readConfig(MessageBean message, String module) {
        Map<String, Object> context = IDEContext.getValueFromGlobalContext(null, "tuning");
        String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue()))
                .map(Object::toString).orElse(null);
        String config = "{\"sysPerfConfig\":[{\"port\":\"8086\",\"ip\":\"" + ip + "\"}]}";
        // ??????
        invokeCallback(message.getCmd(), message.getCbid(), config);
    }

    /**
     * ??????????????????
     *
     * @param message ????????????
     * @param module  ??????
     */
    public void showWarningMessage(MessageBean message, String module) {
    }

    /**
     * jsToJava??????
     * ????????????????????????/????????????
     *
     * @param message js ???????????????
     * @param module  js ???????????????
     */
    public void downloadFile(MessageBean message, String module) {
        String data = message.getData();
        JSONObject dataJsonObj = JsonUtil.getJsonObjectFromJsonStr(data);
        String fileName = dataJsonObj.getString("fileName");
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        // ??????????????????
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project project = CommonUtil.getDefaultProject();
        // ???????????????????????????
        boolean isCa = ("ca.crt").equals(fileName);
        String title = TuningI18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_download_all");
        if (!isCa) {
            title = JavaperfContent.ONLINE_ANALYSIS_EXPORT;
        }
        descriptor.setTitle(title);
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        HashMap<String, String> fileNameMap = new HashMap<>();
        fileNameMap.put("fileName", fileName);
        fileNameMap.put("newFileName", fileName);
        if (ExportToFileUtil.isExistNotToContinue(path, fileNameMap, title)) {
            return;
        }
        fileName = fileNameMap.get("newFileName");
        boolean saveFlag = false;
        String fileContentStr = dataJsonObj.getString("fileContent");
        if (fileContentStr == null) {
            IDENotificationUtil.notificationCommon
                    (new NotificationBean("????????????", "?????????????????????", NotificationType.ERROR));
        }
        try {
            saveFlag = FileUtil.createJsonFile(fileContentStr, path, fileName);
        } catch (IOException e) {
            Logger.error("export Profiling sampling error!!");
        }
        if (isCa) {
            String successCont = I18NServer.toLocale("plugins_hyper_tuner_javaperf_import_caCret_success");
            IDENotificationUtil.notificationCommon(new NotificationBean("", successCont, NotificationType.INFORMATION));
            if (TuningIDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
                ShellTerminalUtil.openInstallCaTerminal(path + File.separator + fileName);
            }
            return;
        }
        downloadNotify(saveFlag, path);
    }

    /**
     * ???????????????????????????
     *
     * @param saveFlag ????????????
     * @param path     ????????????
     */
    private void downloadNotify(boolean saveFlag, String path) {
        if (saveFlag) {
            NotificationBean notificationBean = new NotificationBean(
                    JavaperfContent.ONLINE_EXPORT_SUCCESS,
                    JavaperfContent.ONLINE_ANALYSIS_EXPORT + "  " +
                            TuningI18NServer.toLocale("plugins_hyper_tuner_download_report_success") +
                            "<html> <a href=\"#\">" + path + "</a></html>",
                    NotificationType.INFORMATION);
            IDENotificationUtil.notificationForHyperlink
                    (notificationBean, obj -> CommonUtil.showFileDirOnDesktop(path));
        } else {
            IDENotificationUtil.notificationCommon
                    (new NotificationBean("", JavaperfContent.ONLINE_EXPORT_FAIL, NotificationType.ERROR));
        }
    }
}