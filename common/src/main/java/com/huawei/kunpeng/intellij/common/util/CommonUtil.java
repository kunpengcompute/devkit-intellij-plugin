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

package com.huawei.kunpeng.intellij.common.util;

import com.huawei.kunpeng.intellij.common.ConfigInfo;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.TaskInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import com.intellij.openapi.wm.impl.ProjectFrameHelper;
import com.intellij.openapi.wm.impl.WindowManagerImpl;

import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共工具类
 *
 * @since 1.0.0
 */
public class CommonUtil {
    /**
     * 项目名称
     */
    private static String projectName;

    /**
     * 插件安装包主目录
     */
    private static String pluginName;

    /**
     * webview页面index入口
     */
    private static String webViewIndex;

    private static String toolName;

    /**
     * 获取插件安装路径，需拼接项目名
     *
     * @return string pluginPath
     */
    public static String getPluginInstalledPath() {
        String pluginPath = PathManager.getPluginsPath();
        return pluginPath + IDEConstant.PATH_SEPARATOR + pluginName;
    }

    /**
     * getCurUserCryptRootPath
     *
     * @return String
     */
    public static String getCurUserCryptRootPath() {
        String key = getDefaultProject().getName() + "#" + toolName;
        ConfigInfo configInfo = IDEContext.getProjectConfig().get(key);
        String userName = "";
        String ip = "";
        if (configInfo == null) {
            userName = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "userName", String.class);
            ip = CommonUtil.readCurIpFromConfig();
        } else {
            userName = configInfo.getUserName();
            ip = configInfo.getIp();
        }
        return getPluginInstalledPath() + IDEConstant.PATH_SEPARATOR +
                ip + "#" + userName;
    }


    /**
     * 获取插件安装路径下的文件
     *
     * @param filePath 相对安装路径下的文件路径
     * @return string 安装路径下的文件
     */
    public static String getPluginInstalledPathFile(String filePath) {
        return getPluginInstalledPath() + IDEConstant.PATH_SEPARATOR + filePath;
    }

    /**
     * 获取插件安装路径下的插件包（jar）中的文件
     *
     * @param filePath 相对插件包中的文件路径
     * @return InputStream InputStream
     */
    public static InputStream getPluginInstalledFile(String filePath) {
        return CommonUtil.class.getResourceAsStream(filePath);
    }

    /**
     * 获取插件自定义的JCEF目录
     *
     * @return string JCEF目录
     */
    public static String getPluginJCEFPath() {
        return IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.LINUX
                ? getPluginInstalledPath() + IDEConstant.PATH_SEPARATOR + IDEConstant.JCEF + IDEConstant.PATH_SEPARATOR
                + SystemOS.LINUX.code()
                : getPluginInstalledPath() + IDEConstant.PATH_SEPARATOR + IDEConstant.JCEF + IDEConstant.PATH_SEPARATOR
                + SystemOS.WINDOWS.code();
    }

    /**
     * 获取插件安装路径下的webview文件
     *
     * @param filePath 相对安装路径下的文件路径
     * @return string webview文件路径
     */
    public static String getPluginWebViewFilePath(String filePath) {
        return getPluginInstalledPathFile(IDEConstant.PORTING_WEB_VIEW_PATH) + IDEConstant.PATH_SEPARATOR + filePath;
    }

    /**
     * 校验URL
     *
     * @param url url
     * @return string url
     */
    public static String encodeForURL(final String url) {
        String urlDef = url;

        // 校验协议头:scheme
        if (StringUtil.stringIsEmpty(urlDef)) {
            Logger.error("url is null");
            urlDef = null;
        }

        try {
            urlDef = new URI(url).normalize().toString();
        } catch (URISyntaxException e) {
            Logger.error("Normalize url error : {}", e.getMessage());
            urlDef = null;
        }

        return urlDef;
    }

    /**
     * 获取接口返回中的提示信息
     *
     * @param rsp 请求响应信息
     * @return String 根据当前中英文返回对应的提示信息
     */
    public static String getRspTipInfo(ResponseBean rsp) {
        if (rsp == null) {
            return "";
        }
        if (I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            return rsp.getInfo();
        } else {
            return rsp.getInfochinese();
        }
    }

    /**
     * 获取安装包链接
     *
     * @return url 安装包链接
     */
    public static Map getUrl() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        Map configDef = new HashMap();
        if (config.get(ConfigProperty.PKG_URL.vaLue()) instanceof List) {
            List configList = (List) config.get(ConfigProperty.PKG_URL.vaLue());
            if (configList.get(0) instanceof Map) {
                configDef = (Map) configList.get(0);
            }
        }
        return configDef;
    }

    /**
     * 获取升级安装包链接
     *
     * @return Map
     */
    public static Map getUpgUrl() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        Map configDef = new HashMap();
        if (config.get(ConfigProperty.UPG_PKG_URL.vaLue()) instanceof List) {
            List configList = (List) config.get(ConfigProperty.UPG_PKG_URL.vaLue());
            if (configList.get(0) instanceof Map) {
                configDef = (Map) configList.get(0);
            }
        }
        return configDef;
    }

    /**
     * 获取接口返回responseData.data中的提示信息
     *
     * @param rsp   求响应信息
     * @param enKey 英文key
     * @param zhKey 中文key
     * @return string 根据当前中英文返回对应的提示信息
     */
    public static String getRepDataInfo(ResponseBean rsp, String enKey, String zhKey) {
        if (rsp == null) {
            return "";
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(rsp.getData());
        if (I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            return String.valueOf(jsonMessage.get(enKey));
        } else {
            return String.valueOf(jsonMessage.get(zhKey));
        }
    }

    /**
     * 过滤http参数中的危险字符
     *
     * @param httpParam http参数
     * @return string 过滤后字符
     */
    public static String normalizeForString(final String httpParam) {
        String httpParamDef = httpParam;
        if (StringUtil.stringIsEmpty(httpParam)) {
            Logger.info("httpParam is null.");
            httpParamDef = null;
        } else {
            httpParamDef = Normalizer.normalize(httpParam, Normalizer.Form.NFKC);
        }

        return httpParamDef;
    }

    /**
     * 获取当前intellij窗口打开的Project
     *
     * @return Project Project
     */
    public static Project getDefaultProject() {
        Project project = null;
        Window window = getDefaultWindow();
        if (window instanceof IdeFrame) {
            project = ((IdeFrame) window).getProject();
        }
        if (project == null) {
            project = ProjectManager.getInstance().getOpenProjects().length == 0 ? ProjectManager.getInstance()
                    .getDefaultProject() : ProjectManager.getInstance().getOpenProjects()[0];
        }

        return project;
    }

    /**
     * 获取当前打开的intellij窗口
     *
     * @return window 发起任务的window
     */
    public static Window getDefaultWindow() {
        Window window = null;
        WindowManager windowManager = WindowManager.getInstance();
        if (windowManager instanceof WindowManagerImpl) {
            window = ((WindowManagerImpl) windowManager).getMostRecentFocusedWindow();
        }
        return window;
    }

    /**
     * 生成唯一随机字符串，来源华为《会话管理安全设计规范》
     *
     * @return string 随机字符串
     */
    public static String generateRandomStr() {
        SecureRandom random = new SecureRandom();
        String hv;
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);

        // 然后再将生成的随机数转换成字符串
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < bytes.length; i++) {
            // 转成16进制数
            int xff = bytes[i] & 0xFF;

            // 16进制数转换成字符
            hv = Integer.toHexString(xff);
            if (hv.length() == 1) {
                hv = "0" + hv;
            }
            stringBuilder.append(hv);
        }

        return stringBuilder.toString();
    }

    /**
     * 打开URI
     *
     * @param uriPath uriPath
     */
    public static void openURI(String uriPath) {
        // 获取当前系统桌面扩展
        Desktop dp = Desktop.getDesktop();
        if (dp.isDesktopSupported()) {
            try {
                // 创建一个URI实例
                URI uri = URI.create(uriPath);

                // 判断系统桌面是否支持要执行的功能
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    // 获取系统默认浏览器打开链接
                    dp.browse(uri);
                }
            } catch (IOException e) {
                Logger.error("IOException when open uri");
            }
        }
    }

    /**
     * 桌面窗口打开目录
     *
     * @param dirPath 需要被打开的目录。
     */
    public static void showFileDirOnDesktop(String dirPath) {
        try {
            Desktop.getDesktop().open(new File(dirPath));
        } catch (IOException e) {
            Logger.error("open file dir fail!!! IOException");
        }
    }

    /**
     * 类似node中的path.join 路径拼接
     *
     * @param paths 路径名称
     * @return string 路径
     */
    public static String join(List<String> paths) {
        String result;
        StringBuilder sb = new StringBuilder();
        paths.forEach(str -> {
            sb.append(str);
            sb.append(File.separator);
        });
        result = sb.toString();
        if (result.endsWith(File.separator)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 增加动态库的路径
     *
     * @param libraryPath 路径
     * @throws IOException 反射io异常
     */
    public static void addLibraryDir(String libraryPath) throws IOException {
        try {
            // 参考ClassLoader中的usr_paths字段，
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = null;
            if (field.get(null) instanceof String[]) {
                paths = (String[]) field.get(null);
            } else {
                return;
            }
            for (String path : paths) {
                if (libraryPath.equals(path)) {
                    return;
                }
            }
            String[] temp = new String[paths.length + 1];
            System.arraycopy(paths, 0, temp, 1, paths.length);
            temp[0] = libraryPath;
            field.set(null, temp);
        } catch (IllegalAccessException e) {
            throw new IOException("Failedto get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failedto get field handle to set library path");
        }
    }

    /**
     * 从配置文件中读取 ip和port
     *
     * @return string ip
     */
    public static String readCurIpFromConfig() {
        String ip = null;
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        List portConfig = JsonUtil.getValueIgnoreCaseFromMap(config, ConfigProperty.PORT_CONFIG.vaLue(), List.class);
        if (!ValidateUtils.isEmptyCollection(portConfig)) {
            if (portConfig.get(0) instanceof Map) {
                Map configDef = (Map) portConfig.get(0);
                ip = JsonUtil.getValueIgnoreCaseFromMap(configDef, "ip", String.class);
            }
        }
        return ip;
    }

    public static Map readCurIpAndPortFromConfig() {
        Map<String, String> info = null;
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        List serverConfig = JsonUtil.getValueIgnoreCaseFromMap(config, ConfigProperty.PORT_CONFIG.vaLue(), List.class);
        if (!ValidateUtils.isEmptyCollection(serverConfig)) {
            if (serverConfig.get(0) instanceof Map) {
                info = (Map) serverConfig.get(0);
                return info;
            }
        }
        return info;
    }

    /**
     * 设置Window-BackGround Tasks-Show是否选中
     *
     * @param isOpen true表示选中，show processes弹框; false表示取消选中，hide processes弹框。
     * @param window 发起任务的window
     */
    public static void setBackGroundProcessWindowOpen(boolean isOpen, Window window) {
        ProjectFrameHelper frame = ProjectFrameHelper
                .getFrameHelper(window);
        if (frame != null) {
            StatusBarEx statusBar = frame.getStatusBar();
            if (statusBar != null) {
                // 关闭之前判断一下是否有其他鲲鹏IDE插件自有的后台任务在执行，如果有则不关闭弹窗
                List<Pair<TaskInfo, ProgressIndicator>> list = statusBar.getBackgroundProcesses();
                if (!isOpen && list != null && list.size() > 1 && getIDETaskCount(list) > 1) {
                    return;
                }
                // 判断打开状态，是否需要操作
                if (statusBar.isProcessWindowOpen() ^ isOpen) {
                    statusBar.setProcessWindowOpen(isOpen);
                }
            }
        }
    }

    /**
     * 获取IDE插件自有启动的进度条数量
     *
     * @param list 进度条任务
     * @return IDE插件自有启动的进度条数量
     */
    private static int getIDETaskCount(List<Pair<TaskInfo, ProgressIndicator>> list) {
        int count = 0;
        if (list == null) {
            return count;
        }
        for (Pair<TaskInfo, ProgressIndicator> pair : list) {
            TaskInfo taskInfo = pair.getFirst();
            if (taskInfo == null) {
                continue;
            }
            // 判断是否是IDE插件自有启动的进度条。
            if (taskInfo.getClass().getName().contains("com.huawei.kunpeng")) {
                count++;
            }
        }
        return count;
    }

    public static String getProjectName() {
        return projectName;
    }

    public static void setProjectName(String projectName) {
        CommonUtil.projectName = projectName;
    }

    public static String getWebViewIndex() {
        return webViewIndex;
    }

    public static void setWebViewIndex(String webViewIndex) {
        CommonUtil.webViewIndex = webViewIndex;
    }

    public static String getPluginName() {
        return pluginName;
    }

    public static void setPluginName(String pluginName) {
        CommonUtil.pluginName = pluginName;
    }

    public static String getToolName() {
        return toolName;
    }

    public static void setToolName(String toolName) {
        CommonUtil.toolName = toolName;
    }
}
