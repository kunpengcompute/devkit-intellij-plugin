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

import com.huawei.kunpeng.hyper.tuner.action.install.TuningInstallAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.InstallManageConstant;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.model.JavaPerfOperateLogBean;
import com.huawei.kunpeng.hyper.tuner.webview.WebFileProvider;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.FileManageConstant;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ShellTerminalUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;

import com.alibaba.fastjson.JSONArray;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * 公共的function处理器
 *
 * @since 2020-11-18
 */
public class CommonHandler extends FunctionHandler {
    /**
     * webview通用请求函数
     *
     * @param message 数据
     * @param module  模块
     */
    public void getData(MessageBean message, String module) {
        Logger.info("CommonHandler function getData() is invoked! message=", message);
    }

    /**
     * jsToJava函数
     * 在线分析导出报告/下载证书
     *
     * @param message js 传来的参数
     * @param module  js 传来的参数
     */
    public void downloadCertificate(MessageBean message, String module) {
        Logger.info("CommonHandler function downloadFile() is invoked! message=" + message);

        // 获取message传来的参数，进行校验
        Map<String, Map<String, String>> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Map<String, String> data = messageData.get("data");

        String fileName = data.get("fileName");
        // 判读是否为下载证书
        boolean isCertFileDownload = ("ca.crt").equals(fileName);
        String fileContent = data.get("fileContent");
        // 下载内容
        if (fileContent == null) {
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    FileManageConstant.DOWNLOAD_FAIL, FileManageConstant.DOWNLOAD_EMPTY, NotificationType.ERROR));
            return;
        }
        // 弹出选择存储路径弹窗
        String title = FileManageConstant.DOWNLOAD_TITLE;
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(title);
        Project project = CommonUtil.getDefaultProject();
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        try {
            FileUtil.writeFile(fileContent, path + File.separator + fileName);
        } catch (IOException exception) {
            Logger.error("IOException when writeFile error!!");
            downloadNotify(false, null);
        }
        downloadNotify(true, path);
        if (isCertFileDownload) {
            certFileInstallHandle(fileName, path);
        }
    }

    /**
     * 执行证书安装
     *
     * @param fileName 证书文件名
     * @param path     证书存储路径
     */
    private void certFileInstallHandle(String fileName, String path) {
        IDENotificationUtil.notificationCommon(new NotificationBean(
                InstallManageConstant.IMPORT_CA_TITLE,
                InstallManageConstant.IMPORT_CA_SUCCESS,
                NotificationType.INFORMATION));
        Object osType = IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue());
        if (osType == SystemOS.WINDOWS) {
            ShellTerminalUtil.openInstallCaTerminal(path + File.separator + fileName);
        }
    }

    /**
     * 下载结果右下角提示
     *
     * @param saveFlag 保存结果
     * @param path     保存路径
     */
    private void downloadNotify(boolean saveFlag, String path) {
        if (saveFlag) {
            NotificationBean notificationBean = new NotificationBean(
                    FileManageConstant.DOWNLOAD_SUCCESS,
                    FileManageConstant.DOWNLOAD_SUCCESS_TIP + "<html> <a href=\"#\">" + path + "</a></html>",
                    NotificationType.INFORMATION);
            IDENotificationUtil.notificationForHyperlink(
                    notificationBean,
                    obj -> CommonUtil.showFileDirOnDesktop(path)
            );
        } else {
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    FileManageConstant.DOWNLOAD_FAIL,
                    FileManageConstant.DOWNLOAD_FAIL_TIP,
                    NotificationType.ERROR
            ));
        }
    }

    /**
     * 下载Base64格式图片文件
     *
     * @param baseStr   baseStr
     * @param imagePath imagePath
     */
    public boolean base64ChangeImage(String baseStr, String imagePath) {
        if (baseStr == null) {
            return false;
        }
        baseStr = baseStr.substring(baseStr.indexOf(",") + 1);
        Base64.Decoder decoder = Base64.getDecoder();
        try (OutputStream out = new FileOutputStream(imagePath)) {
            // 解密
            byte[] bytes = decoder.decode(baseStr);
            // 处理数据
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            out.write(bytes);
            out.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 下载Bolb格式文件
     *
     * @param message message
     * @param module  module
     */
    public void downloadFileByBlob(MessageBean message, String module) {
        // 获取message传来的参数，进行校验
        Map<String, String> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());

        String fileName = messageData.get("fileName");
        String fileContent = messageData.get("fileContent");
        // 下载内容
        if (fileContent == null) {
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    FileManageConstant.DOWNLOAD_FAIL, FileManageConstant.DOWNLOAD_EMPTY, NotificationType.ERROR));
            return;
        }
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project project = CommonUtil.getDefaultProject();
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        try {
            FileUtil.writeFile(fileContent, path + File.separator + fileName);
        } catch (IOException exception) {
            Logger.error("IOException when writeFile error!!");
            downloadNotify(false, null);
        }
        downloadNotify(true, path);
    }

    /**
     * 下载JSON格式文件
     *
     * @param message message
     * @param module  module
     */
    public void downloadFileByJson(MessageBean message, String module) {
        // 获取message传来的参数，进行校验
        Map<String, String> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String fileName = messageData.get("fileName");
        String fileContent = messageData.get("fileContent");
        if (fileName.contains("/")) {
            fileName = fileName.replace("/", "_");
        }
        // 下载内容
        if (fileContent == null) {
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    FileManageConstant.DOWNLOAD_FAIL, FileManageConstant.DOWNLOAD_EMPTY, NotificationType.ERROR));
            return;
        }
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project project = CommonUtil.getDefaultProject();
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        try {
            FileUtil.createJsonFile(fileContent, path, fileName);
        } catch (IOException exception) {
            Logger.error("IOException when writeFile error!!");
            downloadNotify(false, null);
        }
        downloadNotify(true, path);
    }

    /**
     * 下载JAVA操作日志
     *
     * @param message message
     * @param module  module
     */
    public void downloadJavaOperLog(MessageBean message, String module) throws IOException {
        // 获取message传来的参数，进行校验
        Map<String, Object> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String fileName = (String) messageData.get("fileName");
        Object membersObj = messageData.get("fileContent");
        List<JavaPerfOperateLogBean> sysPerfOperateLogBeans = new ArrayList<>();
        if (membersObj instanceof JSONArray) {
            JSONArray logArr = (JSONArray) membersObj;
            JavaPerfOperateLogBean sysPerfOperateLogBean;
            for (int mun = 0; mun < logArr.size(); mun++) {
                sysPerfOperateLogBean = logArr.getObject(mun, JavaPerfOperateLogBean.class);
                sysPerfOperateLogBeans.add(sysPerfOperateLogBean);
            }
        }
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project project = CommonUtil.getDefaultProject();
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        CSVPrinter csvPrinter = buildCSVPrinter(sysPerfOperateLogBeans, path, fileName);
        csvPrinter.close();
        downloadNotify(true, path);
    }

    /**
     * 构建CSV
     *
     * @param list     list
     * @param pathLog  pathLog
     * @param fileName fileName
     * @return CSVPrinter
     * @throws IOException IOException
     */
    private static CSVPrinter buildCSVPrinter(List<JavaPerfOperateLogBean> list, String pathLog, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(pathLog + IDEConstant.PATH_SEPARATOR + fileName);
        CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator(System.lineSeparator());
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
        CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);

        csvPrinter.printRecord(Stream.of("username", "operation", "resource", "clientIp", "succeed", "createTime")
                .collect(Collectors.toList()));


        if (!list.isEmpty()) {
            for (JavaPerfOperateLogBean operateLogBean : list) {
                csvPrinter.printRecord(operateLogBean.getUsername(), operateLogBean.getOperation(),
                        operateLogBean.getResource(), operateLogBean.getClientIp() + "\t",
                        operateLogBean.getSucceed(), operateLogBean.getCreateTime());
            }
        }
        return csvPrinter;
    }

    /**
     * 下载base64编码图片
     */
    public void downloadBase64Png(MessageBean message, String module) {
        // 获取message传来的参数，进行校验
        Map<String, String> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String fileName = messageData.get("fileName");
        String fileContent = messageData.get("fileContent");

        // 下载内容
        if (fileContent == null) {
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    FileManageConstant.DOWNLOAD_FAIL, FileManageConstant.DOWNLOAD_EMPTY, NotificationType.ERROR));
            return;
        }
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project project = CommonUtil.getDefaultProject();
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        boolean isSaveFlag = base64ChangeImage(fileContent, path + File.separator + fileName);
        downloadNotify(isSaveFlag, path);
    }

    /**
     * 读取url
     *
     * @param message 数据
     * @param module  模块
     */
    public void readURLConfig(MessageBean message, String module) {
        Map urlConfig = FileUtil.ConfigParser.parseJsonConfigFromFile(TuningIDEConstant.URL_CONFIG_PATH);
        invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(urlConfig));
    }

    /**
     * 打开网页
     *
     * @param message 数据
     * @param module  模块
     */
    public void openNewPage(MessageBean message, String module) {

    }

    /**
     * 关闭文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void closePanel(MessageBean message, String module) {
        Logger.info("closePanel start.");

        Project project = CommonUtil.getDefaultProject();
        VirtualFile file = IDEFileEditorManager.getInstance(project).getSelectFile();
        WebFileEditor webViewPage = WebFileProvider.getWebViewPage(project, file);
        if (webViewPage != null) {
            webViewPage.dispose();
        }
        Logger.info("closePanel end.");
    }

    /**
     * webview显示右下角提示消息处理
     *
     * @param message 数据
     * @param module  模块
     */
    public void readConfig(MessageBean message, String module) {
        Map<String, Object> context = IDEContext.getValueFromGlobalContext(null, "tuning");
        String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue()))
                .map(Object::toString).orElse(null);
        String config = "{\"sysPerfConfig\":[{\"port\":\"8086\",\"ip\":\"" + ip + "\"}]}";
        // 回调
        invokeCallback(message.getCmd(), message.getCbid(), config);
    }

    /**
     * 登出操作
     *
     * @param message 数据
     * @param module  模块
     */
    public void loginOut(MessageBean message, String module) {

    }

    /**
     * 检测ssh连接
     *
     * @param message 数据
     * @param module  模块
     */
    public void checkConn(MessageBean message, String module) {
        ActionOperate actionOperate = new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                Logger.info(String.valueOf(1));
            }
        };

        Map<String, Object> params = JsonUtil.getJsonObjFromJsonStr(message.getData());
        params.put("ip", params.get("host"));
        params.put("user", params.get("username"));
        params.put("passPhrase", params.get("passphrase"));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("param", params);
        // 调试用
        try {
            FileUtil.writeFile(message.getCmd()+"+"+message.getCbid(), PathManager.getPluginsPath() +
                    "/Kunpeng-DevKit-IDE-hyper-tuner-plugin/webview/tuning/a.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        invokeCallback(message.getCmd(), message.getCbid(), "SUCCESS");

        TuningInstallAction action = new TuningInstallAction();
        action.onNextAction(result, actionOperate);


    }

    /**
     * webview显示右下角提示消息处理
     *
     * @param message 显示信息
     * @param module  模块
     */
    public void showInfoBox(MessageBean message, String module) {
        Logger.info("showInfoBox start");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String info = "";

        // 调试用
        try {
            FileUtil.writeFile(info, PathManager.getPluginsPath() +
                    "/Kunpeng-DevKit-IDE-hyper-tuner-plugin/webview/tuning/a.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch (data.get("type")) {
            case "error":
                IDENotificationUtil.notificationCommon(new NotificationBean("", info, NotificationType.ERROR));
                break;
            case "warn":
                IDENotificationUtil.notificationCommon(new NotificationBean("", info, NotificationType.WARNING));
                break;
            default:
                IDENotificationUtil.notificationCommon(new NotificationBean("", info, NotificationType.INFORMATION));
                break;
        }
        Logger.info("showInfoBox end.");
    }


}

