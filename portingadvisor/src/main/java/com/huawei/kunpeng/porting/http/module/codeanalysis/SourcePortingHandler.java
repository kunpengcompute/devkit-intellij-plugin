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

package com.huawei.kunpeng.porting.http.module.codeanalysis;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.bean.PortingTaskBean;
import com.huawei.kunpeng.porting.bean.SourcePortingReportBean;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.assesment.SoftwareAssessmentHandler;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingReportPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingSourceEditor;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The class SourcePortingServer: 源码迁移与后端交互类
 *
 * @since v1.0
 */
public class SourcePortingHandler {
    private static final String CUSTOM_PATH = "customize_path";

    private static final String SOURCE_DETAIL_REPORT_TEMPLATE_NAME = "source_detail_report_template.html";

    private static final String IMG_NO_DATA = "/webview/porting/assets/img/default-page/light-nodata-intellij.png";

    private static final String WINDOW_PATH_SEPARATOR = "\\\\";

    private static final String WINDOW_PATH_SEPARATOR_THREE = "///";

    /**
     * Total Dependencies
     */
    private static int soFilesTotal = 0;

    /**
     * Compatible
     */
    private static int soFilesNeed = 0;

    private static int totalLine = 0;

    private static SourcePortingHandler instance = new SourcePortingHandler();

    private SourcePortingHandler() {
    }

    /**
     * singleton
     *
     * @return SourcePortingHandler
     */
    public static SourcePortingHandler getInstance() {
        return instance;
    }

    /**
     * 获取所有源码扫描任务
     *
     * @return List
     */
    public static List<PortingTaskBean.Task> obtainAllTasks() {
        Logger.info("Start obtain all tasks.");
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/",
            HttpMethod.GET.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return new ArrayList<>();
        }
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(response.getStatus())) {
            Logger.error("Get tasks error");
            IDENotificationUtil.notifyCommonForResponse("", response.getStatus(), response);
        }
        // 处理获取到的后端数据
        PortingTaskBean portingTaskBean = JSONObject.parseObject(response.getData(), PortingTaskBean.class);
        return portingTaskBean.getTaskList();
    }

    /**
     * 删除指定的历史报告
     *
     * @param taskId taskId
     */
    public static void deleteReport(String taskId) {
        Logger.info("Start delete reports, taskId: {}", taskId);
        String deleteUrl = "/portadv/tasks/report/" + taskId + IDEConstant.PATH_SEPARATOR;
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, deleteUrl,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return;
        }
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(response.getStatus())
            ? NotificationType.INFORMATION : NotificationType.ERROR;
        IDENotificationUtil.notificationCommon(
            new NotificationBean(I18NServer.toLocale("plugins_porting_delete_all_reports_title"),
                I18NServer.respToLocale(response), notificationType));
        LeftTreeUtil.refreshReports();
    }

    /**
     * 查看Server端是否是x86平台
     *
     * @return boolean
     */
    public static boolean confirmIsX86PlatForm() {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/platform/",
            HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(request);
        if (responseBean == null) {
            return false;
        }
        return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
    }

    /**
     * 获取用户自定义安装路径
     *
     * @return path
     */
    public static String getCustomInstallPath() {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/customize/",
            HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(request);
        String customPath = null;
        if (responseBean != null) {
            if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
                Map jsonObjFromJsonStr = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
                customPath = JsonUtil.getValueIgnoreCaseFromMap(jsonObjFromJsonStr, CUSTOM_PATH, String.class);
                PortingUserInfoContext.getInstance().setCustomPath(customPath);
            }
        }

        return customPath;
    }

    /**
     * 请求source porting的报告详情
     *
     * @param taskId 任务id
     * @return ResponseBean
     */
    public static Optional<ResponseBean> getSourcePoringReport(String taskId) {
        // 获取报告详情信息
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/task/progress/?task_type=0&task_id=" + taskId, HttpMethod.GET.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        return Optional.ofNullable(response);
    }

    private static boolean isCanDownLoad(String taskId) {
        Optional<ResponseBean> sourcePoringReport = getSourcePoringReport(taskId);
        if (sourcePoringReport.isEmpty()) {
            return false;
        }
        ResponseBean response = sourcePoringReport.get();
        if (RespondStatus.REPORT_FILE_LOCKED.value().equals(response.getStatus())) {
            executeNotification(response, "");
            return false;
        }
        if (RespondStatus.REPORT_NOT_NEW.value().equals(response.getStatus())) {
            // 带打开源码迁移首页的通知
            IDENotificationUtil.notificationForHyperlink(
                new NotificationBean(I18NServer.toLocale("common_term_operate_lockedTitle"),
                    I18NServer.toLocale("create_a_new_analysis_task"),
                    NotificationType.INFORMATION),
                op -> PortingSourceEditor.openPage());
            return false;
        }
        return true;
    }

    /**
     * 源码迁移提示打开最新报告的通知
     *
     * @param response ResponseBean
     * @param type     历史报告操作类型
     */
    public static void executeNotification(ResponseBean response, String type) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(response.getData());
        // 最新报告id
        String newReportId = data.get("id");
        if (newReportId != null) {
            String message = "";
            if ("view".equals(type)) {
                message = I18NServer.toLocale("download_the_latest_analysis_view_suggestion",
                    StringUtil.formatCreatedId(newReportId));
            } else {
                message = I18NServer.toLocale("download_the_latest_analysis_report",
                    StringUtil.formatCreatedId(newReportId));
            }
            // 带打开新报告操作的通知
            IDENotificationUtil.notificationForHyperlink(new NotificationBean("", message,
                NotificationType.INFORMATION), op -> PortingReportPageEditor.openPage(newReportId));
        }
    }

    /**
     * 下载源码迁移报告
     *
     * @param reportType 0表示csv报告，1表示html报告
     * @param taskId     报告任务id
     */
    public static void downLoadReport(int reportType, String taskId) {
        // 后端无法在下载报告接口中添加错误码，只能在下载报告之前调用一下打开报告接口，检查是否可以下载报告
        if (!isCanDownLoad(taskId)) {
            return;
        }
        // 下载报告接口
        ResponseBean responseBean =
            PortingHttpsServer.INSTANCE.requestData(new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/portadv/tasks/" + taskId + "/download/?report_type=" + reportType, HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        String content = "";
        if (reportType == 1) {
            content = loadHtml(responseBean.getData(), taskId);
        }
        saveReport(reportType, taskId, responseBean, content);
    }

    /**
     * 保存报告到本地
     *
     * @param reportType   报告类型
     * @param taskId       报告id
     * @param responseBean 接口返回bean
     * @param content      html报告内容
     */
    public static void saveReport(int reportType, String taskId, ResponseBean responseBean, String content) {
        if (reportType == 0) {
            // 文件名称
            String fileName = taskId + ".csv";
            // 弹出保存文件选择框
            NotificationBean notification = new NotificationBean("",
                I18NServer.toLocale("plugins_porting_report_download_success", fileName),
                NotificationType.INFORMATION);
            notification.setProject(PortingCommonUtil.getDefaultProject());
            UIUtils.saveTXTFileToLocalForDialog(responseBean.getData(), fileName, notification);
        } else {
            // 获取报告详情信息
            String htmlContent = content;
            // 弹出保存文件选择框
            String fileName = taskId + ".html";
            NotificationBean notification = new NotificationBean("",
                I18NServer.toLocale("plugins_porting_report_download_success", fileName),
                NotificationType.INFORMATION);
            notification.setProject(PortingCommonUtil.getDefaultProject());
            UIUtils.saveTXTFileToLocalForDialog(htmlContent, fileName, notification);
        }
    }

    private static String loadHtml(String data, String taskId) {
        // 获取html下载模板
        String path = PortingCommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WEB_VIEW_PATH)
            + PortingIDEConstant.PATH_SEPARATOR
            + PortingIDEConstant.TOOL_NAME_PORTING + PortingIDEConstant.PATH_SEPARATOR
            + SOURCE_DETAIL_REPORT_TEMPLATE_NAME;
        String analysisReportHtml = FileUtil.readFileContent(path);
        Object content = JSONObject.parseObject(data).get("content");
        if (content == null) {
            return "";
        }
        SourcePortingReportBean sourcePortingReportBean = JSONObject.parseObject(content.toString(),
            SourcePortingReportBean.class);
        List<SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo>
            binDetailSrcData = setBinDetailSrcData(sourcePortingReportBean);
        analysisReportHtml = setArgs(analysisReportHtml, sourcePortingReportBean);
        String scanTemp = getScanTemp(binDetailSrcData, sourcePortingReportBean);
        analysisReportHtml = analysisReportHtml.replace("#report_created#", StringUtil.formatCreatedId(taskId));
        analysisReportHtml = analysisReportHtml.replace("#scanTemp#", scanTemp);
        analysisReportHtml = analysisReportHtml.replace("#imgNoData#",
            "file:" + WINDOW_PATH_SEPARATOR_THREE +
                    PortingCommonUtil.getPluginInstalledPath().replace(WINDOW_PATH_SEPARATOR,
                PortingIDEConstant.PATH_SEPARATOR) +
                IMG_NO_DATA);
        return analysisReportHtml;
    }

    private static List<SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo>
    setBinDetailSrcData(SourcePortingReportBean sourcePortingReportBean) {
        HashMap<String, SourcePortingReportBean.PortingResult.PortingLevel.Item> obj = new HashMap<>();
        SourcePortingReportBean.PortingResult.PortingLevel
            portingLevel = sourcePortingReportBean.getPortingresult().getPortingLevel();
        obj.putIfAbsent("0", portingLevel.getZero());
        obj.putIfAbsent("1", portingLevel.getOne());
        obj.putIfAbsent("2", portingLevel.getTwo());
        obj.putIfAbsent("3", portingLevel.getThree());
        obj.putIfAbsent("4", portingLevel.getFour());
        soFilesNeed = portingLevel.getTwo().getAmount() + portingLevel.getThree().getAmount();

        List<SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo> array = new ArrayList<>();
        Set<String> objKeySet = obj.keySet();
        for (String key : objKeySet) {
            SourcePortingReportBean.PortingResult.PortingLevel.Item item = obj.get(key);
            if (item == null) {
                continue;
            }
            item.getBinDetailInfo().forEach(info -> {
                info.setLevel(key);
            });
            array.addAll(item.getBinDetailInfo());
        }
        soFilesTotal = array.size();
        if (soFilesTotal == 0) {
            return array;
        }
        SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo info = array.get(0);
        int rowSpan = 0;
        int i = 0;
        for (SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo item : array) {
            item.setNumber(++i);
            if (item.getUrl() == null) {
                item.setUrl("");
            }
            item.setResult(formatSoResult(item.getLevel()));
            if (!info.getUrl().equals(item.getUrl()) || !info.getResult().equals(item.getResult())) {
                info = item;
                rowSpan = 0;
            }
            info.setRowSpan(++rowSpan);
            int num = item.getUrl().lastIndexOf(PortingIDEConstant.PATH_SEPARATOR);
            item.setPathName(item.getUrl().substring(num + 1));
            item.setOper(formatSuggestion(item.getLevel()));
            item.setType(SoftwareAssessmentHandler.formatSoFileType(item.getType()));
        }
        return array;
    }

    private static String setLeftArgs(String html, SourcePortingReportBean sourcePortingReportBean) {
        String result = html;
        result = result.replace("#plugins_porting_setting_label", I18NServer.toLocale(
            "plugins_porting_setting_label"));
        result = result.replace("#common_term_ipt_label.source_code_path", I18NServer.toLocale(
            "common_term_ipt_label_source_code_path"));
        result = result.replace("#common_term_ipt_label.compiler_version", I18NServer.toLocale(
            "common_term_ipt_label_compiler_version"));
        result = result.replace("#common_term_ipt_label.construct_tool", I18NServer.toLocale(
            "common_term_ipt_label_construct_tool"));
        result = result.replace("#common_term_ipt_label.compile_command", I18NServer.toLocale(
            "common_term_ipt_label_compile_command"));
        result = result.replace("#common_term_ipt_label.target_os", I18NServer.toLocale(
            "common_term_ipt_label_target_os"));
        result = result.replace("#common_term_ipt_label.target_system_kernel_version", I18NServer.toLocale(
            "common_term_ipt_label_target_system_kernel_version"));
        result = result.replace("#textForm1.firstItem.value", sourcePortingReportBean.getInfo().getSourcedir());
        result = setSourceEnhanceCheck(result, sourcePortingReportBean);
        String gfortran = sourcePortingReportBean.getInfo().getGfortran();
        String type = sourcePortingReportBean.getInfo().getCompiler().getType();
        String secondItemValue = "";
        if (type != null && !"".equals(type)) {
            secondItemValue = type.toUpperCase(Locale.ENGLISH)
                + " " + sourcePortingReportBean.getInfo().getCompiler().getVersion();
        }
        if ("".equals(secondItemValue.trim()) && sourcePortingReportBean.getInfo().getCgocompiler() != null) {
            secondItemValue = sourcePortingReportBean.getInfo().getCgocompiler().getType().toUpperCase(Locale.ENGLISH)
                + " " + sourcePortingReportBean.getInfo().getCgocompiler().getVersion();
        }
        if (gfortran != null && !"".equals(gfortran)) {
            if ("".equals(secondItemValue)) {
                secondItemValue = gfortran.toUpperCase(Locale.ENGLISH);
            } else {
                secondItemValue = secondItemValue + ", " + gfortran.toUpperCase(Locale.ENGLISH);
            }
        }
        result = result.replace("#textForm1.secondItem.value", replaceLeftItem(secondItemValue));
        result = result.replace("#textForm1.thirdItem.value",
            replaceLeftItem(sourcePortingReportBean.getInfo().getConstructtool()));
        result = result.replace("#textForm1.fourthItem.value",
            replaceLeftItem(sourcePortingReportBean.getInfo().getCompilecommand()));
        String targetOs = sourcePortingReportBean.getInfo().getTargetos();
        if ("centos7.6".equals(targetOs)) {
            targetOs = "CentOS 7.6";
        }
        result = result.replace("#textForm1.fifthItem.value", targetOs);
        result = result.replace("#textForm1.sixthItem.value", sourcePortingReportBean.getInfo().getTargetkernel());
        return result;
    }

    private static String setSourceEnhanceCheck(String html, SourcePortingReportBean sourcePortingReportBean) {
        String result = html;
        if (sourcePortingReportBean.getInfo().getSourceenhancecheck() == null) {
            result = result.replace("#source_enhance_check#", "none");
        } else {
            String value = CommonI18NServer.toLocale("common_no");
            if ("true".equals(sourcePortingReportBean.getInfo().getSourceenhancecheck())) {
                value = CommonI18NServer.toLocale("common_yes");
            }
            result = result.replace("#source_enhance_check#", "flex")
                .replace("#common_term_ipt_label.source_enhance_check",
                    I18NServer.toLocale("plugins_common_porting_source_enhance_check"))
                .replace("#textForm1.source_enhance_check.value", value);
        }
        return result;
    }

    private static String replaceLeftItem(String itemValue) {
        if (itemValue == null || "".equals(itemValue.trim())) {
            return "--";
        }
        return itemValue;
    }

    private static String setRightArgs(String html, SourcePortingReportBean sourcePortingReportBean) {
        String result = html;
        result = result.replace("#plugins_port_label_soFileSummary", I18NServer.toLocale(
            "plugins_port_label_soFileSummary"));
        result = result.replace("#plugins_port_label_cFileSummary", I18NServer.toLocale(
            "plugins_port_label_cFileSummary"));
        result = result.replace("#plugins_port_label_linesSummary", I18NServer.toLocale(
            "plugins_port_label_linesSummary"));
        result = result.replace("#common_term_report_right_info4", I18NServer.toLocale(
            "common_term_report_right_info4"));
        result = result.replace("#common_term_migrate_result_cFile", I18NServer.toLocale(
            "common_term_migrate_result_cFile"));
        result = result.replace("#common_term_migrate_result_lines", I18NServer.toLocale(
            "common_term_migrate_result_lines"));
        String humanBudget = "";
        BigDecimal workload = BigDecimal.valueOf(sourcePortingReportBean.getPortingresult().getWorkload());
        if (I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            if (workload.compareTo(BigDecimal.ZERO) > 0 && workload.compareTo(BigDecimal.ONE) < 1) {
                humanBudget = I18NServer.toLocale("plugins_port_Estimated_standard_subinfo2");
            } else {
                humanBudget = I18NServer.toLocale("plugins_port_Estimated_standard_subinfo1");
            }
        } else {
            humanBudget = I18NServer.toLocale("plugins_port_Estimated_standard_subinfo");
        }
        String humanBudgetNum = workload + "";
        if (workload.compareTo(BigDecimal.ZERO) == 0) {
            humanBudgetNum = "0";
        }
        result = result.replace("#humanBudget#", humanBudget);
        result = result.replace("#humanBudgetNum#", humanBudgetNum);
        String humanStandard = MessageFormat.format(
            I18NServer.toLocale("plugins_porting_Estimated_standard_subtitle"),
            sourcePortingReportBean.getPortingresult().getCLine(),
            sourcePortingReportBean.getPortingresult().getAsmLine());
        result = result.replace("#humanStandard#", humanStandard);
        return result;
    }

    private static String setArgs(String html, SourcePortingReportBean sourcePortingReportBean) {
        String result = setLeftArgs(html, sourcePortingReportBean);
        result = setRightArgs(result, sourcePortingReportBean);
        result = result.replace("#soFilesNeed#", soFilesNeed + "");
        result = result.replace("#soFilesTotal#", soFilesTotal + "");
        result = result.replace("#soFilesTotal-soFilesNeed#", soFilesTotal - soFilesNeed + "");

        int cmakeNeedTrans = 0;
        if (sourcePortingReportBean.getPortingresult().getCmakelistsinfo() != null) {
            cmakeNeedTrans = sourcePortingReportBean.getPortingresult().getCmakelistsinfo().getNeedtranscount();
        }
        int automakeNeedTrans = 0;
        if (sourcePortingReportBean.getPortingresult().getAutomakeinfo() != null) {
            automakeNeedTrans = sourcePortingReportBean.getPortingresult().getAutomakeinfo().getNeedtranscount();
        }

        int cFileLine = sourcePortingReportBean.getPortingresult().getCodefileinfo().getNeedtranscount()
            + sourcePortingReportBean.getPortingresult().getMakefileinfo().getNeedtranscount()
            + sourcePortingReportBean.getPortingresult().getAsmfileinfo().getNeedtranscount()
            + cmakeNeedTrans + automakeNeedTrans
            + sourcePortingReportBean.getPortingresult().getPythonfileinfo().getNeedtranscount()
            + sourcePortingReportBean.getPortingresult().getFortranfileinfo().getNeedtranscount()
            + sourcePortingReportBean.getPortingresult().getJavafileinfo().getNeedtranscount()
            + sourcePortingReportBean.getPortingresult().getScalafileinfo().getNeedtranscount();
        if (sourcePortingReportBean.getPortingresult().getGolangfileinfo() != null) {
            cFileLine = cFileLine + sourcePortingReportBean.getPortingresult().getGolangfileinfo().getNeedtranscount();
        }
        result = result.replace("#cfileLine#", cFileLine + "");
        totalLine = sourcePortingReportBean.getPortingresult().getCodelines()
            + sourcePortingReportBean.getPortingresult().getMakefilelines()
            + sourcePortingReportBean.getPortingresult().getCmakelistslines()
            + sourcePortingReportBean.getPortingresult().getAutomakelines()
            + sourcePortingReportBean.getPortingresult().getFortranlines()
            + sourcePortingReportBean.getPortingresult().getAsmfilelines()
            + sourcePortingReportBean.getPortingresult().getAsmlines()
            + sourcePortingReportBean.getPortingresult().getInterpretedlines()
            + sourcePortingReportBean.getPortingresult().getGolanglines();
        if (totalLine > 100000) {
            String totalLineStr = totalLine + "";
            result = result.replace("#totalLine#", totalLineStr.substring(0, 2) + "w+");
        } else {
            result = result.replace("#totalLine#", totalLine + "");
        }
        return result;
    }

    private static String getScanTemp(
        List<SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo> binDetailInfo,
        SourcePortingReportBean sourcePortingReportBean) {
        StringBuilder scanTemp = new StringBuilder();
        String[] scanItems = {"soFile", "cFile", "lines"};
        for (String item : scanItems) {
            scanTemp.append(handleScanItem(item, binDetailInfo, sourcePortingReportBean));
        }
        return scanTemp.toString();
    }

    private static String handleScanItem(String item,
        List<SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo> binDetailSrcData,
        SourcePortingReportBean sourcePortingReportBean) {
        StringBuilder itemFile = new StringBuilder();
        String fileListCon = "";
        String itemLabel = "";
        String itemContent = "";
        if ("soFile".equals(item)) {
            itemLabel = I18NServer.toLocale("common_term_result_soFile");
            itemContent = MessageFormat.format(I18NServer.toLocale("common_term_report_soFile_dependent"),
                soFilesTotal, soFilesNeed);
            if (soFilesNeed == 1) {
                itemContent = MessageFormat.format(I18NServer.toLocale("common_term_report_soFile_dependent2"),
                    soFilesTotal, soFilesNeed);
            }
            fileListCon += handleSoFile(binDetailSrcData, itemFile);
        }
        if ("cFile".equals(item)) {
            return handleCFileItem(sourcePortingReportBean);
        }
        if ("lines".equals(item) && sourcePortingReportBean.getPortingresultlist().size() > 0) {
            itemLabel = I18NServer.toLocale("common_term_result_lines");
            itemContent = getCFileItemContent(sourcePortingReportBean.getPortingresult());
            fileListCon += handleLines(sourcePortingReportBean);
        }
        return I18NServer.toLocale("source_porting_report_scanTemp").replace("#item_label#", itemLabel)
            .replace("#item_content#", itemContent).replace("#fileListCon#", fileListCon);
    }

    private static String handleCFileItem(SourcePortingReportBean sourcePortingReportBean) {
        String itemLabel = "";
        itemLabel = I18NServer.toLocale("common_term_result_cFile");
        HashMap<String, List<String>> fileMap = new HashMap<>();
        List<String> itemFiles = new ArrayList<>();
        SourcePortingReportBean.PortingResult portingResult = sourcePortingReportBean.getPortingresult();
        itemFiles.addAll(portingResult.getMakefileinfo().getFiles());
        itemFiles.addAll(portingResult.getCmakelistsinfo().getFiles());
        itemFiles.addAll(portingResult.getAutomakeinfo().getFiles());
        fileMap.put("makefile", itemFiles);
        fileMap.put("C/C++ Source File", portingResult.getCodefileinfo().getFiles());
        fileMap.put("ASM File", portingResult.getAsmfileinfo().getFiles());
        fileMap.put("Fortran", portingResult.getFortranfileinfo().getFiles());
        fileMap.put("Python", portingResult.getPythonfileinfo().getFiles());
        int fileSize = itemFiles.size() + portingResult.getCodefileinfo().getFiles().size()
            + portingResult.getAsmfileinfo().getFiles().size()
            + portingResult.getFortranfileinfo().getFiles().size()
            + portingResult.getPythonfileinfo().getFiles().size();
        if (portingResult.getGolangfileinfo() != null) {
            fileMap.put("Go", portingResult.getGolangfileinfo().getFiles());
            fileSize = fileSize + portingResult.getGolangfileinfo().getFiles().size();
        }
        if (portingResult.getJavafileinfo() != null) {
            fileMap.put("Java", portingResult.getJavafileinfo().getFiles());
            fileSize = fileSize + portingResult.getJavafileinfo().getFiles().size();
        }
        if (portingResult.getScalafileinfo() != null) {
            fileMap.put("Scala", portingResult.getScalafileinfo().getFiles());
            fileSize = fileSize + portingResult.getScalafileinfo().getFiles().size();
        }
        String fileListCon = "";
        String itemContent = "";
        if (fileSize > 0) {
            itemContent = MessageFormat.format(I18NServer.toLocale("common_term_report_cFile_dependent"),
                getCFileLine(sourcePortingReportBean.getPortingresult()));
            StringBuilder itemFile = new StringBuilder();
            fileListCon += handleCFile(sourcePortingReportBean, itemFile, fileMap);
        }
        return I18NServer.toLocale("source_porting_report_scanTemp").replace("#item_label#", itemLabel)
            .replace("#item_content#", itemContent).replace("#fileListCon#", fileListCon);
    }

    private static String getCFileItemContent(SourcePortingReportBean.PortingResult portingResult) {
        StringBuilder builder = new StringBuilder();
        builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_all_code_line"), totalLine));
        int makefileLines = portingResult.getMakefilelines()
            + portingResult.getCmakelistslines() + portingResult.getAutomakelines();
        if (makefileLines > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_makefile_code_line"),
                makefileLines));
        }
        if (portingResult.getCodelines() + portingResult.getAsmlines() > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_c_code_line"),
                portingResult.getCodelines() + portingResult.getAsmlines()));
        }
        if (portingResult.getAsmfilelines() > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_s_code_line"),
                portingResult.getAsmfilelines()));
        }
        if (portingResult.getFortranlines() > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_fortran_code_line"),
                portingResult.getFortranlines()));
        }
        if (portingResult.getGolanglines() > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_go_code_line"),
                portingResult.getGolanglines()));
        }
        if (portingResult.getPythonlines() > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_python_code_line"),
                portingResult.getPythonlines()));
        }
        if (portingResult.getJavalines() > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_java_code_line"),
                portingResult.getJavalines()));
        }
        if (portingResult.getScalalines() > 0) {
            builder.append(MessageFormat.format(I18NServer.toLocale("plugins_port_report_scala_code_line"),
                portingResult.getScalalines()));
        }
        return builder.toString();
    }

    private static String handleSoFile(List<SourcePortingReportBean.PortingResult
        .PortingLevel.Item.BinDetailInfo> binDetailSrcData, StringBuilder itemFile) {
        if (binDetailSrcData.size() > 0) {
            for (SourcePortingReportBean.PortingResult.PortingLevel.Item.BinDetailInfo bin : binDetailSrcData) {
                String optionStr = "";
                String softwarePackageToDownloadTd = "";
                String analysisResultsTd = "";
                String url = StringUtil.defaultValueIfNull(bin.getUrl(), "");
                if (bin.getRowSpan() > 0) {
                    if ("--".equals(url)) {
                        optionStr = MessageFormat.format(I18NServer.toLocale("source_porting_report_optionStr1"),
                            bin.getRowSpan(), I18NServer.toLocale("common_term_operate_download1"));
                    } else if ("".equals(url)) {
                        optionStr = MessageFormat.format(I18NServer.toLocale("source_porting_report_optionStr3"),
                            bin.getRowSpan(), bin.getResult(), bin.getResult());
                        bin.setPathName("--");
                    } else {
                        optionStr = I18NServer.toLocale("source_porting_report_optionStr2")
                            .replace("#rowspan#", bin.getRowSpan() + "").replace("#bin_url", url)
                            .replace("#common_term_operate_download",
                                I18NServer.toLocale("common_term_operate_download"))
                            .replace("#common_term_report_copy_link",
                                I18NServer.toLocale("common_term_report_copy_link"));
                    }
                    softwarePackageToDownloadTd = MessageFormat.format(I18NServer.toLocale(
                            "source_porting_report_itemFile_software_package_to_download"),
                        bin.getRowSpan(), bin.getPathName(), bin.getPathName());
                    analysisResultsTd = MessageFormat.format(I18NServer.toLocale(
                            "source_porting_report_itemFile_analysis_results"),
                        bin.getRowSpan(), bin.getOper(), bin.getOper());
                }
                itemFile.append(I18NServer.toLocale("source_porting_report_itemFile1.regexp")
                    .replace("#bin_number", bin.getNumber() + "")
                    .replace("#bin_name", StringUtil.defaultValueIfNull(bin.getLibname(), ""))
                    .replace("#optionstr#", optionStr).replace("#bin_file_type#", bin.getType())
                    .replace("#analysis_results_td#", analysisResultsTd)
                    .replace("#software_package_to_download_td#", softwarePackageToDownloadTd));
            }
        } else {
            itemFile.append(I18NServer.toLocale("source_porting_report_itemFile2"));
        }
        String fileListConHtml = I18NServer.toLocale("source_porting_report_fileListCon")
            .replace("#itemFile#", itemFile);
        return fileListConHtml.replace("#common_term_no_label", I18NServer.toLocale("common_term_no_label"))
            .replace("#common_term_name_label", I18NServer.toLocale("common_term_name_label"))
            .replace("#common_term_operate_sugg_label", I18NServer.toLocale("common_term_operate_sugg_label"))
            .replace("#common_term_analysis_results_label", I18NServer.toLocale("common_term_report_result"))
            .replace("#common_term_file_type_label#", I18NServer.toLocale("plugins_porting_label_file_type"))
            .replace("#common_term_software_package_name_label#",
                I18NServer.toLocale("plugins_porting_label_software_package"));
    }

    private static int getCFileLine(SourcePortingReportBean.PortingResult portingResult) {
        int needTransCount = portingResult.getCodefileinfo().getNeedtranscount()
            + portingResult.getMakefileinfo().getNeedtranscount()
            + portingResult.getAsmfileinfo().getNeedtranscount()
            + portingResult.getCmakelistsinfo().getNeedtranscount()
            + portingResult.getAutomakeinfo().getNeedtranscount()
            + portingResult.getFortranfileinfo().getNeedtranscount()
            + portingResult.getPythonfileinfo().getNeedtranscount()
            + portingResult.getJavafileinfo().getNeedtranscount()
            + portingResult.getScalafileinfo().getNeedtranscount();
        if (portingResult.getGolangfileinfo() != null) {
            needTransCount = needTransCount + portingResult.getGolangfileinfo().getNeedtranscount();
        }
        return needTransCount;
    }

    private static String handleCFile(SourcePortingReportBean sourcePortingReportBean, StringBuilder itemFile,
        HashMap<String, List<String>> fileMap) {
        AtomicInteger id = new AtomicInteger(1);
        String folderName = sourcePortingReportBean.getInfo().getOsMappingDir();
        AtomicReference<String> showLineCount = new AtomicReference<>("display:none");
        fileMap.forEach((key, value) -> {
            for (String innerItem : value) {
                // 判断innerItem字符串是否以"{","}"开头和结尾，如果是表示为新版本数据结构，不是则是旧版本数据结构
                String lineCount = "";
                if (innerItem.startsWith("{") && innerItem.endsWith("}")) {
                    SourcePortingReportBean.Files files = JSONObject.parseObject(innerItem,
                        SourcePortingReportBean.Files.class);
                    innerItem = files.getFiledirectory();
                    lineCount = files.getLinecount() + "";
                    showLineCount.set("");
                }
                String[] cFileNameArr = innerItem.split(PortingIDEConstant.PATH_SEPARATOR);
                String localPath = "";
                if (folderName != null && !"".equals(folderName)) {
                    String sourceDir = sourcePortingReportBean.getInfo().getSourcedir();
                    int index = sourceDir.lastIndexOf(",");
                    sourceDir = sourceDir.substring(index == -1 ? 0 : index);
                    localPath = folderName + innerItem.substring(sourceDir.length());
                    localPath = localPath.replace("//", WINDOW_PATH_SEPARATOR);
                }
                itemFile.append(I18NServer.toLocale("source_porting_report_itemFile3")
                    .replace("#dataItem_id", id.getAndIncrement() + "")
                    .replace("#dataItem_filename", cFileNameArr[cFileNameArr.length - 1])
                    .replace("#dataItem_path",
                        localPath.length() > 0 ? localPath : innerItem)
                    .replace("#dataItem_fileType", key)
                    .replace("#dataItem_lineCount", lineCount)
                    .replace("#showLineCount#", showLineCount.get()));
            }
        });
        String fileListConHtml = handleGoTip(sourcePortingReportBean) +
            I18NServer.toLocale("source_porting_report_fileListCon2");
        return fileListConHtml
            .replace("#common_term_no_label#",
                I18NServer.toLocale("common_term_no_label"))
            .replace("#common_term_name_label#",
                I18NServer.toLocale("common_term_name_label"))
            .replace("#plugins_porting_label_cFile_path#",
                I18NServer.toLocale("plugins_porting_label_cFile_path"))
            .replace("#plugins_porting_label_file_type#",
                I18NServer.toLocale("plugins_porting_label_file_type"))
            .replace("#plugins_porting_label_file_lineCount#",
                I18NServer.toLocale("plugins_porting_label_line_count"))
            .replace("#itemFile#", itemFile)
            .replace("#showLineCount#", showLineCount.get());
    }

    private static String handleGoTip(SourcePortingReportBean sourcePortingReportBean) {
        List<SourcePortingReportBean.PortingResult.TipsInfo> tips =
            sourcePortingReportBean.getPortingresult().getTips();
        if (tips == null) {
            return "";
        }
        StringBuilder tipStr = new StringBuilder();
        for (SourcePortingReportBean.PortingResult.TipsInfo tip : tips) {
            if (I18NServer.getCurrentLanguage().equals(Language.ZH.code())) {
                tipStr.append(I18NServer.toLocale("source_porting_report_goTip", tip.getInfoCn()));
            } else {
                tipStr.append(I18NServer.toLocale("source_porting_report_goTip", tip.getInfoEn()));
            }
        }
        return tipStr.toString();
    }

    private static String handleLines(SourcePortingReportBean sourcePortingReportBean) {
        StringBuilder itemLines = new StringBuilder();
        for (SourcePortingReportBean.PortingResultList resultList : sourcePortingReportBean.getPortingresultlist()) {
            for (SourcePortingReportBean.PortingResultList.PortingItem portingItem : resultList.getPortingItems()) {
                itemLines.append(MessageFormat.format(I18NServer.toLocale("source_porting_report_itemLines"),
                    resultList.getContent(), resultList.getContent(),
                    portingItem.getLocbegin(), portingItem.getLocend(),
                    portingItem.getKeyword(), portingItem.getKeyword(),
                    portingItem.getStrategy()));
            }
        }
        return I18NServer.toLocale("source_porting_report_fileListCon3")
            .replace("#common_term_download_html_filename",
                I18NServer.toLocale("common_term_download_html_filename"))
            .replace("#common_term_download_html_lineno",
                I18NServer.toLocale("common_term_download_html_lineno"))
            .replace("#common_term_download_html_keyword",
                I18NServer.toLocale("common_term_download_html_keyword"))
            .replace("#common_term_download_html_suggestion",
                I18NServer.toLocale("common_term_download_html_suggestion"))
            .replace("#itemLines#", itemLines);
    }

    private static String formatSuggestion(String level) {
        String suggestion = "";
        switch (level) {
            case "0":
                suggestion = I18NServer.toLocale("plugins_port_report_level0_desc");
                break;
            case "1":
                suggestion = I18NServer.toLocale("plugins_port_report_level1_desc");
                break;
            case "2":
                suggestion = I18NServer.toLocale("plugins_port_report_level2_desc");
                break;
            case "3":
                suggestion = I18NServer.toLocale("plugins_port_report_level7_desc");
                break;
            default:
                break;
        }
        return suggestion;
    }

    private static String formatSoResult(String level) {
        String result = "";
        switch (level) {
            case "0":
                result = I18NServer.toLocale("plugins_port_report_level0_result");
                break;
            case "1":
                result = I18NServer.toLocale("plugins_port_report_level1_result");
                break;
            case "2":
                result = I18NServer.toLocale("plugins_port_report_level2_result");
                break;
            case "3":
                result = I18NServer.toLocale("plugins_port_report_level7_result");
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 获取扫描配置参数系统信息
     *
     * @return Optional<ResponseBean>
     */
    public Optional<ResponseBean> getSystemInfo() {
        return Optional.ofNullable(PortingHttpsServer.INSTANCE.requestData(new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/systeminfo/", HttpMethod.GET.vaLue(), "")));
    }

    /**
     * 查询分析进度
     *
     * @param taskType taskType
     * @param taskId   taskId
     * @return ResponseBean
     */
    public ResponseBean getAnalysisProgress(String taskType, String taskId) {
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.GET.vaLue(), "");
        data.setNeedProcess(true);
        return PortingHttpsServer.INSTANCE.requestData(data);
    }
}
