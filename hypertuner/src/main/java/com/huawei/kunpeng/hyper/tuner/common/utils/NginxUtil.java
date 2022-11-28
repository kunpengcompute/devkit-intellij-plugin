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
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENetUtils;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.intellij.notification.NotificationType;

import java.io.*;
import java.text.MessageFormat;
import java.util.Random;

public class NginxUtil {
    /**
     * 配置文件所在目录
     */
    private static final String CONF_PATH = "\\nginx\\nginx-1.18.0\\conf\\nginx.conf";
    private static final String CONF_MAC_PATH = "/nginx/nginx/conf/nginx.conf";

    /**
     * 启动nginx服务脚本
     */
    private static final String START_NGINX_BAT = "\\nginx\\nginx-1.18.0\\start_nginx.bat";
    private static final String START_NGINX_BASH = "/nginx/nginx/start_nginx.sh";

    /**
     * 停止nginx服务脚本
     */
    public static final String STOP_NGINX_BAT = "\\nginx\\nginx-1.18.0\\stop_nginx.bat";
    public static final String STOP_NGINX_BASH = "/nginx/nginx/stop_nginx.sh";

    private static SystemOS systemOS = IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue());

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
        if (systemOS.equals(SystemOS.WINDOWS)) {
            // 写入到文件覆盖源文件内容
            saveAsFileWriter(content);

            // 写入启动nginx配置bat脚本
            // 原先的nginx.zip中无自带的启动和关闭脚本
            writeNginxStartBat();
            writeNginxStopBat();
            try {
                // 启动 nginx bat脚本
                startBat();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (systemOS.equals(SystemOS.MAC)) {
            System.out.print(content);
            writeToFile(content);
            writeNginxStartBash();
            writeNginxStopBash();
            try {
                startBash();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 替换源config文件
     *
     * @param content content
     */
    public static void saveAsFileWriter(String content) {
        FileWriter fileWriter = null;
        try {
            String pluginPath = CommonUtil.getPluginInstalledPath();
            if (content.contains("start nginx.exe")) {
                // nginx 启动服务脚本写入
                fileWriter = new FileWriter(pluginPath + START_NGINX_BAT);
            } else if (content.contains("title kill nginx service")) {
                // nginx 关闭服务脚本写入
                fileWriter = new FileWriter(pluginPath + STOP_NGINX_BAT);
            } else {
                // nginx配置文件写入
                fileWriter = new FileWriter(pluginPath + CONF_PATH);
            }
            fileWriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * nginx相关文件写入
     */
    public static void writeToFile(String content) {
        FileWriter fileWriter = null;
        File file = null;
        try {
            String pluginPath = CommonUtil.getPluginInstalledPath();
            if (content.contains("nginx -s reload")) {
                // nginx 启动服务脚本写入
                file = new File(pluginPath + START_NGINX_BASH);
            } else if (content.contains("nginx -s quit")) {
                // nginx 关闭服务脚本写入
                file = new File(pluginPath + STOP_NGINX_BASH);
            } else {
                // nginx配置文件写入
                file = new File(pluginPath + CONF_MAC_PATH);
            }
            file.createNewFile();
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 写入启动nginx配置bat脚本
     */
    public static void writeNginxStartBat() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        String pluginPathStr = pluginPath.substring(0, 2);
        String content = MessageFormat.format(TuningI18NServer.toLocale(
                "plugins_hyper_tuner_start_nginx_bat"), pluginPathStr, pluginPath);
        saveAsFileWriter(content);
    }

    /**
     * 写入启动nginx配置bash脚本（mac）
     */
    public static void writeNginxStartBash() {
        System.out.println("writing start nginx script");
        String pluginPath = CommonUtil.getPluginInstalledPath();
        String content = MessageFormat.format(TuningI18NServer.toLocale(
                "plugins_hyper_tuner_start_nginx_bash"), pluginPath);
        writeToFile(content);
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
     * 写入启动nginx关闭服务bash脚本（mac）
     */
    public static void writeNginxStopBash() {
        System.out.println("writing nginx stop bash");
        String pluginPath = CommonUtil.getPluginInstalledPath();
        String content = MessageFormat.format(TuningI18NServer.toLocale(
                "plugins_hyper_tuner_stop_nginx_bash"), pluginPath);
        writeToFile(content);
    }

    /**
     * 启动bat脚本
     */
    public static void startBat() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        try {
            Runtime.getRuntime().exec("cmd /c " + pluginPath + START_NGINX_BAT);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * 启动nginx开启服务bash脚本
     */
    public static void startBash() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        System.out.println("starting nginx!!!");
        try {
            // 修改文件权限以执行bash脚本
            Runtime.getRuntime().exec(new String[]{"chmod", "777", pluginPath + START_NGINX_BASH});
            Runtime.getRuntime().exec(new String[]{"bash", pluginPath + START_NGINX_BASH});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动关闭Nginx服务bat脚本
     */
    public static void startStopBat() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        try {
            Runtime.getRuntime().exec("cmd /c " + pluginPath + STOP_NGINX_BAT);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * 启动nginx关闭服务bash脚本
     */
    public static void startStopBash() {
        String pluginPath = CommonUtil.getPluginInstalledPath();
        try {
            Runtime.getRuntime().exec(new String[]{"chmod", "777", pluginPath + STOP_NGINX_BASH});
            Runtime.getRuntime().exec(new String[]{"bash", pluginPath + STOP_NGINX_BASH});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Nginx
     */
    public static void stopNginx() {
        System.out.println("stopping nginx!!!");
        if (systemOS.equals(SystemOS.WINDOWS)) {
            startStopBat();
        } else {
            startStopBash();
        }
    }

    public static String getLocalPort() {
        Random random = new Random();
        random.setSeed(10000L);
        int randomPort = random.nextInt(10240) + 45295;
        // 随机寻找一个未被占用的端口
        while (IDENetUtils.isLocalePortUsing(randomPort)) {
            randomPort = random.nextInt(10240) + 45295;
        }
        return randomPort + "";
    }
}

