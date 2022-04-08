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

package com.huawei.kunpeng.porting.http.module.assesment;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.action.toolwindow.PortingMouseEventImpl;
import com.huawei.kunpeng.porting.bean.SoftwareAssessmentReportBean;
import com.huawei.kunpeng.porting.bean.SoftwareAssessmentTaskBean;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

/**
 * SoftwareAssessmentServer: 软件迁移评估与后端交互类
 *
 * @since 2021.1.13
 */
public class SoftwareAssessmentHandler {
    private static final String PORT_REPORT_QUERY_URI = "/portadv/binary/";

    private static final String PORT_REPORT_DELETE_URI = "/portadv/binary/";

    private static final String DEP_DETAIL_REPORT_TEMPLATE_NAME = "dep_detail_report_template.html";

    private static final String IMG_DOWN = "/webview/porting/assets/img/home/row.svg";

    private static final String IMG_NO_DATA = "/webview/porting/assets/img/default-page/light-nodata-intellij.png";

    private static final String WINDOW_PATH_SEPARATOR = "\\\\";

    private static final String BIN_NAME = "#bin_name";

    private static final String BIN_TYPE = "#bin_type";

    private static final String TR = "</tr>";

    /**
     * Total Dependencies
     */
    private static int soFilesTotal = 0;

    /**
     * Compatible
     */
    private static int soFilesNeed = 0;

    private static int count;

    /**
     * 获取所有源码扫描任务
     *
     * @return List
     */
    public static List<SoftwareAssessmentTaskBean.Task> selectAllTasks() {
        Logger.info("Start obtain all tasks.");
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, PORT_REPORT_QUERY_URI,
            HttpMethod.GET.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return new ArrayList<>();
        }
        Logger.info("Get tasks, response: {}", response);
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(response.getStatus())) {
            Logger.error("Get tasks error");
        }
        // 处理获取到的后端数据
        SoftwareAssessmentTaskBean analysisTask =
            JSONObject.parseObject(response.getData(), SoftwareAssessmentTaskBean.class);
        return analysisTask.getTaskList();
    }

    /**
     * 刷新左侧树
     */
    public static void refreshRebuildResults() {
        LeftTreeUtil.refreshReports();
    }

    /**
     * 删除软件评估报告记录
     *
     * @param taskId taskId
     */
    public static void deleteAssessmentReport(String taskId) {
        String url =
            PORT_REPORT_DELETE_URI + "report" + IDEConstant.PATH_SEPARATOR + taskId + IDEConstant.PATH_SEPARATOR;
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        NotificationType notificationType = getNotificationType(responseBean);
        if (notificationType == null) {
            return;
        }
        refreshRebuildResults();
    }

    /**
     * 删除所有软件评估报告记录
     *
     * @param allReportTitle 所有报告title
     */
    public static void deleteAllAssessmentReport(Set<String> allReportTitle) {
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, PORT_REPORT_DELETE_URI,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        NotificationType notificationType = getNotificationType(responseBean);
        if (notificationType == null) {
            return;
        }
        if (notificationType == NotificationType.INFORMATION) {
            PortingMouseEventImpl.closeAllDeletedReport(allReportTitle);
        }
        refreshRebuildResults();
    }

    @Nullable
    private static NotificationType getNotificationType(ResponseBean responseBean) {
        Logger.info("delete porting Task, Response: {}", responseBean);
        if (responseBean == null) {
            return null;
        }
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())
            ? NotificationType.INFORMATION
            : NotificationType.ERROR;
        String notContent = I18NServer.respToLocale(responseBean);
        IDENotificationUtil.notificationCommon(
            new NotificationBean(I18NServer.toLocale("plugins_porting_assessment_label"),
                notContent, notificationType));
        return notificationType;
    }

    /**
     * 下载报告
     *
     * @param reportType 0表示csv报告，其余表示html报告
     * @param taskId     报告任务id
     */
    public static void downLoadReport(int reportType, String taskId) {
        ResponseBean responseBean =
            PortingHttpsServer.INSTANCE.requestData(new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/portadv/binary/" + taskId + "/?report_type=" + reportType, HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        String content = "";
        if (reportType == 1) {
            content = loadHtml(responseBean.getData(), taskId);
        }
        SourcePortingHandler.saveReport(reportType, taskId, responseBean, content);
    }

    private static String loadHtml(String data, String taskId) {
        // 获取html下载模板
        String path = PortingCommonUtil.getPluginInstalledPathFile(IDEConstant.PORTING_WEB_VIEW_PATH)
            + IDEConstant.PATH_SEPARATOR
            + PortingIDEConstant.TOOL_NAME_PORTING + IDEConstant.PATH_SEPARATOR
            + DEP_DETAIL_REPORT_TEMPLATE_NAME;
        String analysisReportHtml = FileUtil.readFileContent(path);
        JSONObject content = JSONObject.parseObject(data).getJSONObject("content");
        SoftwareAssessmentReportBean assessmentReportBean = JSONObject.parseObject(content.toString(),
            SoftwareAssessmentReportBean.class);
        List<SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo>
            binDetailSrcData = setBinDetailSrcData(assessmentReportBean);
        analysisReportHtml = setPath(analysisReportHtml, assessmentReportBean);
        String scanTemp = getScanTemp(binDetailSrcData);
        analysisReportHtml = replaceTitleAndLabel(analysisReportHtml, assessmentReportBean, taskId);
        return replaceHtml(analysisReportHtml, scanTemp);
    }

    private static String replaceHtml(String html, String scanTemp) {
        String result = html;
        result = result.replace("#soFilesTotal#", soFilesTotal + "");
        result = result.replace("#soFilesNeed#", soFilesNeed + "");
        result = result.replace("#soFilesTotal_soFilesNeed#", soFilesTotal - soFilesNeed + "");
        result = result.replace("#scanTemp#", scanTemp);
        result = result.replace("#imgNoData#",
            "file:///" + PortingCommonUtil.getPluginInstalledPath().replace(WINDOW_PATH_SEPARATOR,
                PortingIDEConstant.PATH_SEPARATOR) +
                IMG_NO_DATA);
        return result;
    }

    private static String replaceTitleAndLabel(String html, SoftwareAssessmentReportBean assessmentReportBean,
        String taskId) {
        String result = html;
        result = result.replace("#currentReport#", StringUtil.formatCreatedId(taskId));
        result = result.replace("#depArgs.softwareCode.item2.target_os.label",
            I18NServer.toLocale("common_term_ipt_label_target_os"));
        String targetOs = assessmentReportBean.getTargetOs();
        if ("centos7.6".equals(targetOs)) {
            targetOs = "CentOS 7.6";
        }
        result = result.replace("#depArgs.softwareCode.item2.target_os.value", targetOs);
        result = result.replace("#depArgs.softwareCode.item2.target_system_kernel_version.label",
            I18NServer.toLocale("common_term_ipt_label_target_system_kernel_version"));
        result = result.replace("#depArgs.softwareCode.item2.target_system_kernel_version.value",
            assessmentReportBean.getTargetSystemKernelVersion());

        result = result.replace("#common_term_report_right_info#",
            I18NServer.toLocale("common_term_report_right_info"));
        result = result.replace("#common_term_report_right_info2#",
            I18NServer.toLocale("common_term_report_right_info2"));
        result = result.replace("#common_term_report_right_info3#",
            I18NServer.toLocale("common_term_report_right_info3"));
        result = result.replace("#plugins_porting_setting_label",
            I18NServer.toLocale("plugins_porting_setting_label"));

        return result;
    }

    private static List<SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo>
    setBinDetailSrcData(SoftwareAssessmentReportBean assessmentReportBean) {
        HashMap<String, SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item> obj = new HashMap<>();
        List<SoftwareAssessmentReportBean.DependencyPackages>
            dependencyPackages = assessmentReportBean.getDependencyPackages();
        soFilesTotal = 0;
        soFilesNeed = 0;
        obj = computeBinDetail(dependencyPackages);
        List<SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo> array = new ArrayList<>();
        Set<String> objKeySet = obj.keySet();
        for (String key : objKeySet) {
            SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item item = obj.get(key);
            if (item == null) {
                continue;
            }
            item.getBinDetailInfo().forEach(info -> {
                info.setLevel(key);
            });
            array.addAll(item.getBinDetailInfo());
        }
        List<SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo> binDetailInfo;
        binDetailInfo = array;
        soFilesTotal = binDetailInfo.size();
        if (soFilesTotal == 0) {
            return binDetailInfo;
        }
        int i = 1;
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo urlItem = binDetailInfo.get(0);
        int urlRowSpan = 0;
        for (SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo item : binDetailInfo) {
            item.setResult(formatSoResult(item.getLevel()));
            if (urlItem.getUrl().equals(item.getUrl()) && urlItem.getResult().equals(item.getResult())) {
                urlItem.setUrlRowSpan(++urlRowSpan);
            } else {
                urlItem = item;
                urlRowSpan = 1;
                urlItem.setUrlRowSpan(urlRowSpan);
            }
            item.setNumber(i++);
            String soFileType = formatSoFileType(item.getType());
            item.setPath(StringUtil.stringIsEmpty(item.getPath()) ?
                I18NServer.toLocale("common_term_runlib", soFileType) : item.getPath());
            item.setDesc(StringUtil.stringIsEmpty(item.getDesc()) ? "--" : item.getDesc());
            item.setOper(formatSoFileSuggestion(item));
            item.setType(soFileType);
        }
        return binDetailInfo;
    }

    private static HashMap<String, SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item>
    computeBinDetail(List<SoftwareAssessmentReportBean.DependencyPackages> dependencyPackages) {
        HashMap<String, SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item> obj = new HashMap<>();
        for (SoftwareAssessmentReportBean.DependencyPackages pkg : dependencyPackages) {
            SoftwareAssessmentReportBean.DependencyPackages.PortingLevel portingLevel = pkg.getPortingLevel();
            obj.computeIfPresent("0", (key, value) -> {
                value.getBinDetailInfo().addAll(portingLevel.getZero().getBinDetailInfo());
                return value;
            });
            obj.computeIfPresent("1", (key, value) -> {
                value.getBinDetailInfo().addAll(portingLevel.getOne().getBinDetailInfo());
                return value;
            });
            obj.computeIfPresent("2", (key, value) -> {
                value.getBinDetailInfo().addAll(portingLevel.getTwo().getBinDetailInfo());
                return value;
            });
            obj.computeIfPresent("3", (key, value) -> {
                value.getBinDetailInfo().addAll(portingLevel.getThree().getBinDetailInfo());
                return value;
            });
            obj.computeIfPresent("4", (key, value) -> {
                value.getBinDetailInfo().addAll(portingLevel.getFour().getBinDetailInfo());
                return value;
            });
            obj.computeIfPresent("5", (key, value) -> {
                value.getBinDetailInfo().addAll(portingLevel.getFive().getBinDetailInfo());
                return value;
            });
            obj.computeIfPresent("6", (key, value) -> {
                value.getBinDetailInfo().addAll(portingLevel.getSix().getBinDetailInfo());
                return value;
            });
            obj.putIfAbsent("0", portingLevel.getZero());
            obj.putIfAbsent("1", portingLevel.getOne());
            obj.putIfAbsent("2", portingLevel.getTwo());
            obj.putIfAbsent("3", portingLevel.getThree());
            obj.putIfAbsent("4", portingLevel.getFour());
            obj.putIfAbsent("5", portingLevel.getFive());
            obj.putIfAbsent("6", portingLevel.getSix());
            soFilesNeed += portingLevel.getZero().getAmount() + portingLevel.getOne().getAmount()
                + portingLevel.getThree().getAmount() + portingLevel.getFour().getAmount()
                + portingLevel.getSix().getAmount();
        }
        return obj;
    }

    private static String getScanTemp(
        List<SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo> binDetailInfo) {
        StringBuilder scanTemp = new StringBuilder();
        String[] scanItems = {"soFile", "cFile", "lines"};
        for (String item : scanItems) {
            scanTemp.append(handleSoFile(item, binDetailInfo));
        }
        return scanTemp.toString();
    }

    private static String handleSoFile(String item, List<SoftwareAssessmentReportBean
        .DependencyPackages.PortingLevel.Item.BinDetailInfo> binDetailSrcData) {
        StringBuilder itemFile = new StringBuilder();
        String fileListCon = "";
        String scanTemp = "";
        String soFilePadding = "";
        if ("soFile".equals(item)) {
            count = -1;
            soFilePadding = binDetailSrcData.size() > 10 ? "padding-right: 17px;" : "";
            if (binDetailSrcData.size() > 0) {
                for (SoftwareAssessmentReportBean
                    .DependencyPackages.PortingLevel.Item.BinDetailInfo bin : binDetailSrcData) {
                    itemFile.append(createItemFile(bin));
                }
            } else {
                itemFile.append(I18NServer.toLocale("item_file_tr5"));
            }
            String fileListConHtml = I18NServer.toLocale("fileListCon_html").replace("#itemFile", itemFile)
                .replace("#soFoilePadding", soFilePadding);
            fileListCon += fileListConHtml.replace("#common_term_no_label",
                    I18NServer.toLocale("common_term_no_label"))
                .replace("#common_term_name_label", I18NServer.toLocale("common_term_name_label"))
                .replace("#plugins_common_term_type_label",
                    I18NServer.toLocale("plugins_common_term_type_label"))
                .replace("#common_term_path_label", I18NServer.toLocale("common_term_path_label"))
                .replace("#plugins_port_option_soFileType_software_package",
                    I18NServer.toLocale("plugins_port_option_soFileType_software_package"))
                .replace("#common_term_report_result", I18NServer.toLocale("common_term_report_result"))
                .replace("#common_term_operate_sugg_label",
                    I18NServer.toLocale("common_term_operate_sugg_label"));
            scanTemp = I18NServer.toLocale("scanTemp_html")
                .replace("#common_term_result_soFile", I18NServer.toLocale("common_term_result_soFile"))
                .replace("#fileListCon", fileListCon);
        }
        return scanTemp;
    }

    private static String createItemFile(
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo bin) {
        StringBuilder itemFile = new StringBuilder();
        String optionStr = "";
        if (bin.getUrlRowSpan() > 0) {
            if ("".equals(bin.getUrl())) {
                String level = bin.getLevel();
                if (("0".equals(level) || "3".equals(level) || "6".equals(level))) {
                    optionStr = MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_optionStr1"),
                        bin.getUrlRowSpan());
                } else {
                    optionStr = MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_optionStr2"),
                        bin.getUrlRowSpan(), bin.getResult());
                }
            } else {
                optionStr = createOtherOptionStr(bin);
            }
        }
        processItemFile(bin, itemFile, optionStr);
        return itemFile.toString();
    }

    private static void processItemFile(
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo bin,
        StringBuilder itemFile, String optionStr) {
        String url = StringUtil.stringIsEmpty(bin.getUrl()) ? "--" : bin.getUrl();
        int num = url.lastIndexOf(IDEConstant.PATH_SEPARATOR);
        int pathExtLength = bin.getPathExt() == null ? 0 : bin.getPathExt().length;
        if (pathExtLength > 0) {
            count++;
            String oper = formatSoSuggestion(bin.getLevel());
            String itemFileTemp = I18NServer.toLocale("item_file_tr1")
                .replace("#bin_number", bin.getNumber() + "")
                .replace(BIN_NAME, StringUtil.defaultValueIfNull(bin.getLibname(), ""))
                .replace(BIN_TYPE, bin.getType())
                .replace("#bin_path#", bin.getPath())
                .replace("#imgDown", "file:///" + PortingCommonUtil.getPluginInstalledPath() + IMG_DOWN)
                .replace("#bin_pathName", url.substring(num + 1))
                .replace("#bin_oper", oper)
                .replace("#optionstr", optionStr);
            itemFile.append(itemFileTemp);

            handleSoFileHasUrl(bin);
            handleSubInfo(bin, itemFile, optionStr, pathExtLength, oper);
        } else {
            String str = I18NServer.toLocale("item_file_tr4")
                .replace("#bin_number", bin.getNumber() + "")
                .replace(BIN_NAME, StringUtil.defaultValueIfNull(bin.getLibname(), ""))
                .replace(BIN_TYPE, bin.getType())
                .replace("#bin_path#", bin.getPath());

            if (bin.getUrlRowSpan() > 0) {
                String title = formatSoSuggestion(bin.getLevel());
                itemFile.append(str).append(MessageFormat.format(I18NServer.toLocale("item_file_td"),
                        bin.getUrlRowSpan(), url.substring(num + 1)))
                    .append(MessageFormat.format(I18NServer.toLocale("item_file_td"),
                        bin.getUrlRowSpan(), title)).append(optionStr).append(TR);
            } else {
                itemFile.append(str).append(TR);
            }
        }
    }

    private static void handleSoFileHasUrl(
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo bin) {
        List<String>[] soInfoArray = bin.getSoInfo();
        if (soInfoArray != null && soInfoArray.length > 0) {
            for (List<String> soInfo : soInfoArray) {
                if (soInfo.size() > 1 && !StringUtil.stringIsEmpty(soInfo.get(1))) {
                    bin.setSoFileHasUrl(true);
                    break;
                }
            }
        }
    }

    private static void handleSubInfo(
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo bin,
        StringBuilder itemFile, String optionStr, int pathExtLength, String oper) {
        if (!bin.isSoFileHasUrl()) {
            for (int i = 0; i < pathExtLength; i++) {
                String str;
                str = I18NServer.toLocale("item_file_tr2").replace("#count#", count + "")
                    .replace("#bin_path_ext", bin.getPathExt()[i])
                    .replace(BIN_TYPE, bin.getType())
                    .replace(BIN_NAME, "")
                    .replace("#urlName", "")
                    .replace("#oper", oper)
                    .replace("#result", optionStr)
                ;
                itemFile.append(str);
            }
        } else {
            if (bin.getSoInfo() == null || bin.getSoInfo().length == 0) {
                return;
            }
            handleSoInfo(bin);
            for (int i = 0; i < bin.getSoInfo().length; i++) {
                Map<String, String> soInfoMap = new HashMap<>();
                if (bin.getSoInfo()[i].size() < 2) {
                    continue;
                }
                handleSubInfo(bin.getLevel(), bin.getSoInfo()[i], soInfoMap);
                itemFile.append(joinItemFile(soInfoMap, bin));
            }
        }
    }

    private static void handleSoInfo(
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo bin) {
        int soInfoRowSpan = 0;
        List<String> soInfo = bin.getSoInfo()[0];
        for (List<String> info : bin.getSoInfo()) {
            if (info.get(1).equals(soInfo.get(1))) {
                soInfoRowSpan++;
            } else {
                soInfo = info;
                soInfoRowSpan = 1;
            }
            if (soInfo.size() > 2) {
                soInfo.set(2, soInfoRowSpan + "");
            } else {
                soInfo.add(soInfoRowSpan + "");
            }
        }
    }

    private static String joinItemFile(Map<String, String> soInfoMap,
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo bin) {
        StringBuilder itemFile = new StringBuilder();
        String sonResults = "";
        String rowSpan = soInfoMap.get("rowSpan");
        if ("--".equals(soInfoMap.get("urlName"))) {
            // 没有软件包
            sonResults = MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_downloadDesc5"),
                rowSpan, bin.getResult());
        } else if ("--".equals(soInfoMap.get("result"))) {
            // 有软件包无下载链接
            sonResults = MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_downloadDesc5"),
                rowSpan, I18NServer.toLocale("common_upload_unable"));
        } else {
            // 有下载链接
            sonResults = MessageFormat.format(I18NServer.toLocale("software_assessment_report_optionStr2"),
                rowSpan, soInfoMap.get("url"), I18NServer.toLocale("common_term_operate_download"),
                soInfoMap.get("url"), I18NServer.toLocale("common_term_report_copy_link"));
        }
        String str = I18NServer.toLocale("item_file_tr2").replace("#count#", count + "")
            .replace("#bin_path_ext", StringUtil.defaultValueIfNull(soInfoMap.get("path"), ""))
            .replace(BIN_TYPE, bin.getType())
            .replace(BIN_NAME, StringUtil.defaultValueIfNull(bin.getLibname(), ""));

        if (rowSpan != null) {
            itemFile.append(str).append(MessageFormat.format(I18NServer.toLocale("item_file_tr2_td"),
                    rowSpan, soInfoMap.get("urlName")))
                .append(MessageFormat.format(I18NServer.toLocale("item_file_tr2_td"),
                    rowSpan, soInfoMap.get("oper"))).append(sonResults).append(TR);
        } else {
            itemFile.append(str).append(TR);
        }
        return itemFile.toString();
    }


    private static void handleSubInfo(String level, List<String> soInfo, Map<String, String> soInfoMap) {
        soInfoMap.put("path", soInfo.get(0));
        String pathUrl = soInfo.get(1);
        if (!StringUtil.stringIsEmpty(pathUrl)) {
            if (pathUrl.contains("http")) {
                soInfoMap.put("urlName", handleDownloadUrl(pathUrl));
                soInfoMap.put("oper", formatSoSuggestion("0"));
                soInfoMap.put("result", formatSoResult("0"));
                soInfoMap.put("url", pathUrl);
            } else {
                soInfoMap.put("urlName", pathUrl);
                soInfoMap.put("oper", formatSoSuggestion(level));
                soInfoMap.put("result", "--");
                soInfoMap.put("url", "");
            }
        } else {
            soInfoMap.put("urlName", "--");
            soInfoMap.put("oper", formatSoSuggestion(level));
            soInfoMap.put("result", formatSoResult(level));
            soInfoMap.put("url", "");
        }
        if (soInfo.size() > 2) {
            soInfoMap.put("rowSpan", soInfo.get(2));
        }
    }

    // 对下载url进行切割
    private static String handleDownloadUrl(String url) {
        if (url.lastIndexOf('/') > -1) {
            int lastIndex = url.lastIndexOf('/');
            return url.substring(lastIndex + 1);
        } else {
            return url;
        }
    }

    private static String createOtherOptionStr(
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo bin) {
        String optionStr = "";
        String downloadDesc = "";
        if (bin.getUrlRowSpan() == 0) {
            return optionStr;
        }
        if (bin.getDownloadDesc() == null) {
            String level = bin.getLevel();
            if (("0".equals(level) || "3".equals(level) || "6".equals(level))) {
                downloadDesc = MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_downloadDesc1"),
                    bin.getUrl(), bin.getResult());
            } else {
                String result = bin.getResult();
                if (!result.equals(I18NServer.toLocale("download_source_code"))) {
                    downloadDesc = MessageFormat.format(I18NServer.toLocale(
                        "software_assessment_itemFile_downloadDesc2"), bin.getUrl(), bin.getResult());
                } else {
                    downloadDesc = MessageFormat.format(I18NServer.toLocale(
                        "software_assessment_itemFile_downloadDesc3"), bin.getUrl(), result);
                }
            }
            String compatibleText = "";
            if (bin.isAarch64()) {
                compatibleText = "<br>" + I18NServer.toLocale("plugin_porting_compatible_text");
            }
            optionStr =
                MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_optionStr3"),
                    bin.getUrlRowSpan(), downloadDesc, bin.getUrl(),
                    I18NServer.toLocale("common_term_report_copy_link"), compatibleText);
        } else {
            String title = bin.getDownloadDesc() + " " + bin.getUrl();
            downloadDesc = MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_downloadDesc4"),
                title);
            optionStr = MessageFormat.format(I18NServer.toLocale("software_assessment_itemFile_optionStr4"),
                bin.getUrlRowSpan(), title,
                downloadDesc);
        }
        return optionStr;
    }

    private static String formatSoSuggestion(String level) {
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
                suggestion = I18NServer.toLocale("plugins_port_report_level3_desc");
                break;
            case "4":
                suggestion = I18NServer.toLocale("plugins_port_report_level4_desc");
                break;
            case "5":
                suggestion = I18NServer.toLocale("plugins_port_report_level5_desc");
                break;
            case "6":
                suggestion = I18NServer.toLocale("plugins_port_report_level6_desc");
                break;
            default:
                break;
        }
        return suggestion;
    }

    private static String setPath(String html, SoftwareAssessmentReportBean assessmentReportBean) {
        String[] installationPackagePathArray = assessmentReportBean.getInstallationPackagePath().getPath();
        String[] softwareInstallationPathArray = assessmentReportBean.getSoftwareInstallationPath().getPath();

        StringBuilder packageStr = new StringBuilder();
        StringBuilder softwareStr = new StringBuilder();

        for (String path : installationPackagePathArray) {
            packageStr.append(path);
        }
        for (String path : softwareInstallationPathArray) {
            softwareStr.append(path);
        }
        String nonePack = "flex";
        String noneSoft = "flex";
        if (packageStr.length() == 0) {
            nonePack = "none";
        }
        if (softwareStr.length() == 0) {
            noneSoft = "none";
        }
        String result = html;
        String noneCode = "flex";
        result = result.replace("#nonePack", nonePack);
        result = result.replace("#noneSoft", noneSoft);
        result = result.replace("#noneCode", noneCode);
        result = result.replace("#common_term_ipt_label_package",
            I18NServer.toLocale("common_term_ipt_label_package"));
        result = result.replace("#common_term_ipt_label_path",
            I18NServer.toLocale("common_term_ipt_label_path"));
        result = result.replace("#common_term_ipt_label_source_code_path", I18NServer.toLocale(
            "common_term_ipt_label_source_code_path"));
        result = result.replace("#packageStr", packageStr);
        result = result.replace("#softwareStr", softwareStr);

        return result;
    }

    /**
     * 不同soFile依赖包的显示不同名称
     *
     * @param fileType soFile依赖包类型
     * @return 依赖包显示名称
     */
    public static String formatSoFileType(String fileType) {
        String typeName = "--";
        switch (fileType) {
            case "DYNAMIC_LIBRARY":
                typeName = I18NServer.toLocale("plugins_dep_option_soFileType_dynamic_library");
                break;
            case "STATIC_LIBRARY":
                typeName = I18NServer.toLocale("plugins_dep_option_soFileType_static_library");
                break;
            case "EXEC":
                typeName = I18NServer.toLocale("plugins_dep_option_soFileType_executable_file");
                break;
            case "SOFTWARE":
                typeName = I18NServer.toLocale("plugins_dep_option_soFileType_software_package");
                break;
            case "JAR":
                typeName = I18NServer.toLocale("plugins_dep_option_soFileType_jar_packagey");
                break;
            default:
                break;
        }
        return typeName;
    }

    private static String formatSoFileSuggestion(
        SoftwareAssessmentReportBean.DependencyPackages.PortingLevel.Item.BinDetailInfo item) {
        String suggestion = "--";
        String level = item.getLevel();
        String url = item.getUrl();
        switch (level) {
            case "0":
                suggestion = I18NServer.toLocale("plugins_dep_message_level0Desc");
                suggestion = StringUtil.stringIsEmpty(url) ? suggestion : I18NServer.toLocale(
                    "plugins_dep_message_reportLevel01NotUrlDesc");
                break;
            case "1":
                suggestion = I18NServer.toLocale("plugins_dep_message_level1Desc");
                suggestion = StringUtil.stringIsEmpty(url) ? suggestion : I18NServer.toLocale(
                    "plugins_dep_message_reportLevel01NotUrlDesc");
                break;
            case "2":
                suggestion = I18NServer.toLocale("plugins_dep_message_level2Desc");
                break;
            case "3":
                suggestion = I18NServer.toLocale("plugins_dep_message_level3Desc");
                suggestion = StringUtil.stringIsEmpty(url) ? suggestion : I18NServer.toLocale(
                    "plugins_dep_message_reportLevel34NotUrlDesc");
                break;
            case "4":
                suggestion = I18NServer.toLocale("plugins_dep_message_level4Desc");
                suggestion = StringUtil.stringIsEmpty(url) ? suggestion : I18NServer.toLocale(
                    "plugins_dep_message_reportLevel34NotUrlDesc");
                break;
            case "5":
                suggestion = I18NServer.toLocale("plugins_dep_message_level5Desc");
                break;
            case "6":
                suggestion = I18NServer.toLocale("plugins_dep_message_level6Desc");
                suggestion = StringUtil.stringIsEmpty(url) ? suggestion : I18NServer.toLocale(
                    "plugins_dep_message_reportLevel6NotUrlDesc");
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
                result = I18NServer.toLocale("plugins_port_report_level3_result");
                break;
            case "4":
                result = I18NServer.toLocale("plugins_port_report_level4_result");
                break;
            case "5":
                result = I18NServer.toLocale("plugins_port_report_level5_result");
                break;
            case "6":
                result = I18NServer.toLocale("plugins_port_report_level6_result");
                break;
            default:
                break;
        }
        return result;
    }
}
