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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

public class NginxUtil {
    /**
     * 配置文件所在目录
     */
    private static final String CONF_PATH = "\\nginx\\nginx-1.18.0\\conf\\nginx.conf";

    /**
     * 配置文件所在目录
     */
    private static final String START_NGINX_BAT = "\\nginx\\nginx-1.18.0\\start_nginx.bat";
    private static final String START_NGINX_VBS = "\\nginx\\nginx-1.18.0\\start_nginx.vbs";

    /**
     * 停止nginx服务bat脚本
     */
    public static final String STOP_NGINX_BAT = "\\nginx\\nginx-1.18.0\\stop_nginx.bat";

    /**
     * 停止nginx服务Vbs脚本
     */
    public static final String STOP_NGINX_VBS = "\\nginx\\nginx-1.18.0\\stop_nginx.vbs";

    /**
     * 换行符
     */
    public static final String NEW_LINE = "\n";

    /**
     * 更新 nginx 配置文件
     *
     * @param ip        代理目标ip
     * @param port      代理目标端口
     * @param localPort 本地监听端口
     */
    public static void updateNginxConfig(String ip, String port, String localPort) {
        String content = MessageFormat.format(TuningI18NServer.toLocale(
                "plugins_hyper_tuner_nginx_config"), localPort, ip, port);
        // 写入到文件覆盖源文件内容
        saveAsFileWriter(content);

        // 写入启动nginx配置bat脚本
        writeNginxStartBat();
        writeNginxStartVbs();
        writeNginxStopBat();
        writeNginxStopVbs();
        try {
            // 启动 nginx bat脚本
            startVbs();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换源config文件
     *
     * @param content content
     */
    private static void saveAsFileWriter(String content) {
        FileWriter fileWriter = null;
        try {
            String pluginPath = CommonUtil.getPluginInstalledPath();
            if (content.contains("start nginx.exe")) {
                fileWriter = new FileWriter(pluginPath + START_NGINX_BAT);
            } else if (content.contains("title kill nginx service")) {
                fileWriter = new FileWriter(pluginPath + STOP_NGINX_BAT);
            } else if (content.contains(START_NGINX_BAT)) {
                fileWriter = new FileWriter(pluginPath + START_NGINX_VBS);
            } else if (content.contains(STOP_NGINX_BAT)) {
                fileWriter = new FileWriter(pluginPath + STOP_NGINX_VBS);
            } else {
                fileWriter = new FileWriter(pluginPath + CONF_PATH);
            }
            fileWriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 写入启动nginx配置bat脚本
     */
    private static void writeNginxStartBat() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        String pluginPathStr = pluginPath.substring(0, 2);
        String content = MessageFormat.format(TuningI18NServer.toLocale(
                "plugins_hyper_tuner_start_nginx_bat"), pluginPathStr, pluginPath);
        saveAsFileWriter(content);
    }

    /**
     * 写入启动nginx关闭服务bat脚本
     */
    public static void writeNginxStopBat() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        String pluginPathStr = pluginPath.substring(0, 2);
        String content = MessageFormat.format(TuningI18NServer.toLocale(
                "plugins_hyper_tuner_stop_nginx_bat"), pluginPathStr, pluginPath);
        saveAsFileWriter(content);
    }

    /**
     * 写入启动nginx关闭服务vbs脚本
     */
    public static void writeNginxStopVbs() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        String content = "set ws = WScript.CreateObject(\"WScript.Shell\")" + NEW_LINE +
                "ws.Run\t\"" + pluginPath + START_NGINX_BAT + "\", 0";
        saveAsFileWriter(content);
    }

    /**
     * 写入启动nginx关闭服务vbs脚本
     */
    public static void writeNginxStartVbs() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        String content = "set ws = WScript.CreateObject(\"WScript.Shell\")" + NEW_LINE +
                "ws.Run\t\"" + pluginPath + STOP_NGINX_BAT + "\", 0";
        saveAsFileWriter(content);
    }


    /**
     * 启动bat脚本
     */
    public static void startVbs() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        try {
            Runtime.getRuntime().exec("wscript.exe " + pluginPath + START_NGINX_VBS);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * 启动停止nginx服务bat脚本
     */
    public static void startStopVbs() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        try {
            Runtime.getRuntime().exec("wscript.exe " + pluginPath + STOP_NGINX_VBS);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
