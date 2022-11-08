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

import com.huawei.kunpeng.intellij.common.BaseCacheDataOpt;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置文件工具类
 *
 * @since 2021-04-12
 */
@Slf4j
public class ConfigUtils {
    /**
     * 将环境信息写入到json文件中
     *
     * @param toolName toolName
     * @param ip       ip
     * @param port     port
     * @param localPort     代理本地port
     */
    public static void fillIp2JsonFile(String toolName, String ip, String port, String localPort) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        if (config.get(ConfigProperty.PORT_CONFIG.vaLue()) instanceof List) {
            List configList = (List) config.get(ConfigProperty.PORT_CONFIG.vaLue());
            if (configList.get(0) instanceof Map) {
                Map configDef = (Map) configList.get(0);
                configDef.put(BaseCacheVal.IP.vaLue(), ip);
                configDef.put(BaseCacheVal.PORT.vaLue(), port);
                configDef.put(BaseCacheVal.LOCAL_PORT.vaLue(), localPort);
            }
            // 更新全局ip及端口
            BaseCacheDataOpt.updateGlobalIPAndPort(toolName, ip, port, localPort);
            FileUtil.ConfigParser.saveJsonConfigToFile(config.toString(), IDEConstant.CONFIG_PATH);
        }
//        FileUtil.updateCertConfig(certFile);
    }

    /**
     * 获取 config.json 配置
     *
     * @return Map
     */
    public static Map getUserConfig() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        return getAutoLoginInfo(config);
    }

    /**
     * 获取自动登录信息
     *
     * @param config config
     * @return Map
     */
    public static Map getAutoLoginInfo(Map config) {
        Map configDef = new HashMap<String, List<String>>();
        if (config.get(ConfigProperty.AUTO_LOGIN_CONFIG.vaLue()) instanceof List) {
            List configList = (List) config.get(ConfigProperty.AUTO_LOGIN_CONFIG.vaLue());
            if (configList.get(0) instanceof Map) {
                configDef = (Map) configList.get(0);
            }
        }
        return configDef;
    }

    /**
     * 更新config.json 配置
     *
     * @param configKey      配置文件key
     * @param userName       用户名
     * @param isSavePassword 是否记住密码
     * @param isAutoLogin    是否自动登录
     */
    public static void updateUserConfig(String configKey, String userName, boolean isSavePassword,
                                        boolean isAutoLogin) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        if (config.get(configKey) instanceof List) {
            List configList = (List) config.get(ConfigProperty.AUTO_LOGIN_CONFIG.vaLue());
            if (configList.get(0) instanceof Map) {
                Map configDef = (Map) configList.get(0);
                if (isSavePassword) {
                    configDef.put("userName", userName);
                } else {
                    configDef.put("userName", "");
                }
                configDef.put("savePassword", isSavePassword);
                configDef.put("autoLogin", isAutoLogin);
            }
            FileUtil.ConfigParser.saveJsonConfigToFile(config.toString(), IDEConstant.CONFIG_PATH);
        }
    }
}
