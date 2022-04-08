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
import com.huawei.kunpeng.intellij.common.ConfigInfo;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * @param certFile certFile
     */
    public static void fillIp2JsonFile(String toolName, String ip, String port, String certFile) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        if (config.get(ConfigProperty.PORT_CONFIG.vaLue()) instanceof List) {
            List configList = (List) config.get(ConfigProperty.PORT_CONFIG.vaLue());
            if (configList.get(0) instanceof Map) {
                Map configDef = (Map) configList.get(0);
                configDef.put(BaseCacheVal.IP.vaLue(), ip);
                configDef.put(BaseCacheVal.PORT.vaLue(), port);
            }
            // 更新全局ip及端口
            BaseCacheDataOpt.updateGlobalIPAndPort(toolName, ip, port);
            FileUtil.ConfigParser.saveJsonConfigToFile(config.toString(), IDEConstant.CONFIG_PATH);
        }
        FileUtil.updateCertConfig(certFile);
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
     *  获取自动登录信息
     *
     * @param config config
     *
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

    /**
     * 更新当前用户的config文件
     *
     * @param configKey configKey
     * @param userName userName
     * @param isSavePassword isSavePassword
     * @param isAutoLogin isAutoLogin
     */
    public static void updateCurUserAutoLoginModelInConfig(
            String configKey, String userName, boolean isSavePassword, boolean isAutoLogin) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        if (config.get(configKey) instanceof List) {
            List configList = (List) config.get(ConfigProperty.AUTO_LOGIN_CONFIG.vaLue());
            if (configList.get(0) instanceof Map) {
                Map configDef = (Map) configList.get(0);
                configDef.put("userName", userName);
                configDef.put("savePassword", isSavePassword);
                configDef.put("autoLogin", isAutoLogin);
            }
            FileUtil.ConfigParser.saveJsonConfigToFile(config.toString(), getCurUserConfigPath(userName));
        }
    }

    private static String getCurUserConfigPath(String userName) {
        return CommonUtil.readCurIpFromConfig() + "#" + userName + IDEConstant.PATH_SEPARATOR + IDEConstant.CONFIG_FILE;
    }

    /**
     * 从配置文件中读取 某个key对应value
     *
     * @param configPath ip
     * @param key        key ConfigProperty.PORT_CONFIG.vaLue()
     * @param subKey     subKey 具体取值key
     * @return 配置信息
     */
    public static String readValueFromConfig(String configPath, String key, String subKey) {
        String value = null;
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(configPath);
        List portConfig = JsonUtil.getValueIgnoreCaseFromMap(config, key, List.class);
        if (!ValidateUtils.isEmptyCollection(portConfig)) {
            if (portConfig.get(0) instanceof Map) {
                Map configDef = (Map) portConfig.get(0);
                value = JsonUtil.getValueIgnoreCaseFromMap(configDef, subKey, String.class);
            }
        }
        return value;
    }

    /**
     * 给定用户名，判断是否含有用户
     *
     * @param userName userName
     * @return boolean
     */
    public static boolean isContainsCurUser(String userName) {
        String key = CommonUtil.readCurIpFromConfig() + "#" + userName;
        return getCurrentUsersConfig().contains(key);
    }

    private static List<String> getCurrentUsersConfig() {
        List<String> result = new ArrayList<>();
        File pluginDir = new File(CommonUtil.getPluginInstalledPath());
        if (!pluginDir.isDirectory()) {
            log.error("Plugin installed directory is not existed.");
            return result;
        }
        return Objects.isNull(pluginDir.listFiles()) ? result : Arrays.stream(pluginDir.listFiles())
                .filter(File::isDirectory)
                .filter(dir -> dir.getName().contains("#"))
                .map(file -> file.getName())
                .collect(Collectors.toList());
    }

    /**
     * 当前用户是否设置过记住密码
     *
     * @param userName userName
     * @return boolean
     */
    public static boolean isSavePassword(String userName) {
        return JsonUtil.getValueIgnoreCaseFromMap(getAutoLoginMap(userName), "savePassword", boolean.class);
    }

    /**
     * 当前用户是否设置过自动登录
     *
     * @param userName userName
     * @return boolean
     */
    public static boolean isAutoLogin(String userName) {
        return JsonUtil.getValueIgnoreCaseFromMap(getAutoLoginMap(userName), "autoLogin", boolean.class);
    }

    /**
     * 获取当前用户的登录信息
     *
     * @param userName userName
     * @return Map
     */
    public static Map getAutoLoginMap(String userName) {
        String curUserCryptoRootPath = CommonUtil.getPluginInstalledPath() + IDEConstant.PATH_SEPARATOR +
                CommonUtil.readCurIpFromConfig() + "#" + userName;
        return getAutoLoginInfo(FileUtil.ConfigParser.parseJsonFile2Map(curUserCryptoRootPath +
                IDEConstant.PATH_SEPARATOR + IDEConstant.CONFIG_FILE));
    }

    /**
     * 更新当前缓存中的ConfigInfo信息
     *
     * @param configInfo configInfo
     */
    public static void updateIDEContextConfigInfoByConfigFile(ConfigInfo configInfo) {
        String key = CommonUtil.getDefaultProject().getName() + "#" + CommonUtil.getToolName();
        String userName = JsonUtil.getValueIgnoreCaseFromMap(getUserConfig(), "userName", String.class);
        String ip = CommonUtil.readCurIpFromConfig();
        String curUserName = (configInfo == null || StringUtil.stringIsEmpty(configInfo.getUserName())) ?
                userName : configInfo.getUserName();
        String curIp = configInfo == null || StringUtil.stringIsEmpty(configInfo.getIp()) ?
                ip : configInfo.getIp();
        IDEContext.getProjectConfig().put(key, new ConfigInfo(curIp, curUserName));
    }
}
