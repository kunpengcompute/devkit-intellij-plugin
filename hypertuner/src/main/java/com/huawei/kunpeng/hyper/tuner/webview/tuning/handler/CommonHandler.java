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
import com.huawei.kunpeng.hyper.tuner.action.uninstall.TuningUninstallAction;
import com.huawei.kunpeng.hyper.tuner.action.upgrade.TuningUpgradeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.InstallManageConstant;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.NginxUtil;
import com.huawei.kunpeng.hyper.tuner.model.JavaPerfOperateLogBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningLoginSuccessPanel;
import com.huawei.kunpeng.hyper.tuner.webview.WebFileProvider;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ConfigureServerEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.IDELoginEditor;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.constant.FileManageConstant;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.*;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;

import com.alibaba.fastjson.JSONArray;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.intellij.ui.enums.CheckConnResponse;
import com.huawei.kunpeng.intellij.ui.enums.MaintenanceResponse;
import com.huawei.kunpeng.intellij.ui.utils.DeployUtil;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
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

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
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
    public void readUrlConfig(MessageBean message, String module) {
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
        Map<String, String> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String page = messageData.get("router");
        boolean closePage = Optional.of(Objects.equals(messageData.get("closePage"), "true")).orElse(false);

        if (closePage) {
            Project project = CommonUtil.getDefaultProject();
            VirtualFile file = IDEFileEditorManager.getInstance(project).getSelectFile();
            WebFileEditor webViewPage = WebFileProvider.getWebViewPage(project, file);
            if (webViewPage != null) {
                webViewPage.dispose();
            }
        }

        switch (page) {
            case "config":
                ConfigureServerEditor.openPage();
                break;
            case "login":
                // TODO 打开新页面ui878ui809ouio8
                Logger.info("openNewPage in intellij: login");
                Map<String, String> serverConfig = CommonUtil.readCurIpAndPortFromConfig();
                String localPort = NginxUtil.getLocalPort();
                NginxUtil.updateNginxConfig(serverConfig.get("ip"), serverConfig.get("port"), localPort);
                IDELoginEditor.openPage(localPort);
        }
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

//    /**
//     * webview显示右下角提示消息处理
//     *
//     * @param message 数据
//     * @param module  模块
//     */
//    public void readConfig(MessageBean message, String module) {
//        Map<String, Object> context = IDEContext.getValueFromGlobalContext(null, "tuning");
//        String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue()))
//                .map(Object::toString).orElse(null);
//        String config = "{\"sysPerfConfig\":[{\"port\":\"8086\",\"ip\":\"" + ip + "\"}]}";
//        // 回调
//        invokeCallback(message.getCmd(), message.getCbid(), config);
//    }

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
        Map<String, String> param = JsonUtil.getJsonObjFromJsonStr(message.getData());
        // 增加对应键值以对应方法参数
        param.put("ip", param.get("host"));
        param.put("user", param.get("username"));
        param.put("passPhrase", param.get("passphrase"));

        Logger.info(param.toString());

        SshConfig config = DeployUtil.getConfig(param);

        ActionOperate actionOperate = new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                CheckConnResponse response = (CheckConnResponse) data;
                invokeCallback(message.getCmd(), message.getCbid(), "\"" + response.value() + "\"");
            }
        };

        DeployUtil.newTestConn(actionOperate, config);
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
        String info = data.get("info");
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

    /**
     * 调用默认浏览器打开FAQ网页
     *
     * @param message 数据
     * @param module  模块
     */
    public void openUrlInBrowser(MessageBean message, String module) {
        try {
            String url = (String) JsonUtil.getJsonObjFromJsonStr(message.getData()).get("url");
            java.net.URI uri = java.net.URI.create(url);
            java.awt.Desktop dp = java.awt.Desktop.getDesktop();
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                dp.browse(uri);
            }
        } catch (NullPointerException | IOException e) {
            Logger.error(e.getMessage());
        }
    }

    /**
     * 升级服务器
     *
     * @param message
     * @param module
     */
    public void upgrade(MessageBean message, String module) {
        Map<String, String> param = JsonUtil.getJsonObjFromJsonStr(message.getData());
        // 增加对应键值以对应方法参数
        param.put("ip", param.get("host"));
        param.put("user", param.get("username"));
        param.put("passPhrase", param.get("passphrase"));
        param.put("displayName", InstallManageConstant.UPGRADE_TITLE);
        Map<String, Object> data = new HashMap<>();
        data.put("param", param);

        TuningUpgradeAction action = new TuningUpgradeAction();
        Logger.info("Upgrade begin...");
        ActionOperate actionOperate = new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                if(data instanceof MaintenanceResponse) {
                    MaintenanceResponse response = (MaintenanceResponse) data;
                    invokeCallback(message.getCmd(), message.getCbid(), "\"" + response.value() + "\"");
                }
                else{
                    invokeCallback(message.getCmd(), message.getCbid(), "\"" + data.toString() + "\"");
                }
            }
        };
//        action.newOKAction(data,actionOperate);
        actionOperate.actionOperate(MaintenanceResponse.FAKE_SUCCESS);
    }

    /**
     * 安装服务器
     *
     * @param message
     * @param module
     */
    public void install(MessageBean message, String module) {
        Map<String, String> param = JsonUtil.getJsonObjFromJsonStr(message.getData());
        // 增加对应键值以对应方法参数
        param.put("ip", param.get("host"));
        param.put("user", param.get("username"));
        param.put("passPhrase", param.get("passphrase"));
        param.put("displayName", InstallManageConstant.INSTALL_TITLE);
        Map<String, Object> data = new HashMap<>();
        data.put("param", param);

        TuningInstallAction action = new TuningInstallAction();
        Logger.info("Install begin...");
        ActionOperate actionOperate = new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                if(data instanceof MaintenanceResponse) {
                    MaintenanceResponse response = (MaintenanceResponse) data;
                    invokeCallback(message.getCmd(), message.getCbid(), "\"" + response.value() + "\"");
                }
                else{
                    invokeCallback(message.getCmd(), message.getCbid(), "\"" + data.toString() + "\"");
                }
            }
        };
//        action.newOKAction(data,actionOperate);
        actionOperate.actionOperate(MaintenanceResponse.FAKE_SUCCESS);
    }

    /**
     * 卸载服务器
     *
     * @param message
     * @param module
     */
    public void uninstall(MessageBean message, String module) {
        Map<String, String> param = JsonUtil.getJsonObjFromJsonStr(message.getData());
        // 增加对应键值以对应方法参数
        param.put("ip", param.get("host"));
        param.put("user", param.get("username"));
        param.put("passPhrase", param.get("passphrase"));
        param.put("displayName", InstallManageConstant.INSTALL_TITLE);
        Map<String, Object> data = new HashMap<>();
        data.put("param", param);

        TuningUninstallAction action = new TuningUninstallAction();
        Logger.info("Uninstall begin...");
        ActionOperate actionOperate = new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                if(data instanceof MaintenanceResponse) {
                    MaintenanceResponse response = (MaintenanceResponse) data;
                    invokeCallback(message.getCmd(), message.getCbid(), "\"" + response.value() + "\"");
                }
                else{
                    invokeCallback(message.getCmd(), message.getCbid(), "\"" + data.toString() + "\"");
                }
            }
        };
//        action.newOKAction(data,actionOperate);
        actionOperate.actionOperate(MaintenanceResponse.SUCCESS);
    }

    public void cleanConfig(MessageBean message, String module){
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        IDEContext.setIDEPluginStatus(TuningIDEConstant.TOOL_NAME_TUNING, IDEPluginStatus.IDE_STATUS_INIT);
//        for (Project proj : openProjects) {
//            // 配置服务器完成后刷新左侧树面板为配置服务器面板
//            ToolWindow toolWindow =
//                    ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
//            LeftTreeConfigPanel leftTreeConfigPanel = new LeftTreeConfigPanel(toolWindow, proj);
//            toolWindow.getContentManager().addContent(leftTreeConfigPanel.getContent());
//            toolWindow.getContentManager().setSelectedContent(leftTreeConfigPanel.getContent());
//        }
        // 清空本地 ip 缓存
        ConfigUtils.fillIp2JsonFile(TuningIDEConstant.TOOL_NAME_TUNING, "", "", "");
        config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        invokeCallback(message.getCmd(), message.getCbid(), null);
    }

    public void closeAllPanel(MessageBean message, String module){
        Project project = CommonUtil.getDefaultProject();
        VirtualFile file = IDEFileEditorManager.getInstance(project).getSelectFile();
        WebFileEditor webViewPage = WebFileProvider.getWebViewPage(project, file);
        if (webViewPage != null) {
            webViewPage.dispose();
        }
//        Project[] openProjects = ProjectUtil.getOpenProjects();
//        for (Project proj : openProjects) {
//            AbstractWebFileProvider.closeAllWebViewPage();
//        }
    }

    /**
     * 隐藏终端
     *
     * @param message
     * @param module
     */
    public void hideTerminal(MessageBean message, String module) {
        Project project = CommonUtil.getDefaultProject();
        ToolWindow terminal = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (terminal != null) {
            terminal.hide();
        }
    }

    /**
     * 读取配置服务器数据
     *
     * @param message 数据
     * @param module 模块
     */
    public void readConfig(MessageBean message, String module) {
        Logger.info("read config message");
        Logger.info("cmd is: " + message.getCmd());
        Logger.info("data is " + message.getData());
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(config));
//        Logger.info(data.values().toString());
    }

    public void saveConfig(MessageBean message, String module) {
        Logger.info("save config");
        Map<String, Object> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Logger.info("cmd is: " + message.getCmd());
        Logger.info("data is: " + message.getData());
        if (data.containsKey("showInfoBox") && data.get("showInfoBox").equals(Boolean.TRUE)) {
            // show info box
            Logger.info("show info box");
//            IDENotificationUtil.notificationCommon(new NotificationBean("Configure Server",
//                    "Server is configured successfully!", NotificationType.INFORMATION));
//            TuningCommonUtil.showNotification();
        }
        // 跳转到登录页面
        Map<String, String> params = new HashMap<>();
        Map<String, String> configData = JsonUtil.getJsonObjFromJsonStr((String)data.get("data"));
        Logger.info("config data is ", configData);
        params.put("ip", configData.get("ip"));
        params.put("port", configData.get("port"));
        params.put("localPort", NginxUtil.getLocalPort());

        Project project = CommonUtil.getDefaultProject();
        VirtualFile file = IDEFileEditorManager.getInstance(project).getSelectFile();
        WebFileEditor webViewPage = WebFileProvider.getWebViewPage(project, file);
        if (webViewPage instanceof ConfigureServerEditor) {
            String responseType = ((ConfigureServerEditor) webViewPage).saveConfig(params);
            Logger.info("response Type: " + responseType);
            // TODO save config成功后回调到webview显示立即登录弹框
            invokeCallback(message.getCmd(), message.getCbid(), "{\"type\":\"" + responseType + "\"}");
//            webViewPage.dispose();
        }
        else{
            ConfigureServerEditor tmpEditor=new ConfigureServerEditor();
            tmpEditor.saveConfig(params);
            webViewPage.dispose();
            tmpEditor.dispose();
        }
    }

    /**
     * 登录页面登录成功
     * @param message
     * @param module
     */
    public void loginSuccess(MessageBean message, String module) {
        Logger.info("login successfully!!!");
        String toolName = TuningIDEConstant.TOOL_NAME_TUNING;
        IDEContext.setIDEPluginStatus(toolName, IDEPluginStatus.IDE_STATUS_LOGIN);
        // 刷新左侧面板为已登录面板
        Project[] openProjects = ProjectUtil.getOpenProjects();
        ApplicationManager.getApplication().invokeLater(() -> {
            for (Project proj : openProjects) {
                // 登录成功后左侧面板刷新为已登录面板
                ToolWindow toolWindow =
                        ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
                TuningLoginSuccessPanel tuningLoginSuccessPanel = new TuningLoginSuccessPanel(toolWindow, proj);
                if (toolWindow != null) {
                    toolWindow.getContentManager().addContent(tuningLoginSuccessPanel.getContent());
                    toolWindow.getContentManager().setSelectedContent(tuningLoginSuccessPanel.getContent());
                }
            }
        });
    }
}

