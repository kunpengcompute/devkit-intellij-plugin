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

package com.huawei.kunpeng.intellij.common;

import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * IDE plugin全局上下文
 *
 * @since 1.0.0
 */
public class IDEContext<T> {
    /**
     * 常量实例
     */
    public static IDEContext instance = new IDEContext();

    /**
     * IDEPluginStatus key
     */
    private static String IDE_PLUGIN_STATUS = "IDEPluginStatus";
    /**
     * key 为 project_name#tool_name
     */
    @Setter
    @Getter
    private static Map<String, ConfigInfo> projectConfig = new HashMap<>();

    // 初始化常用的基础变量缓存
    static {
        setValueForGlobalContext(null, BaseCacheVal.CURRENT_LOCALE.vaLue(), Locale.getDefault());
    }

    /**
     * 全局上下文
     */
    private Map<String, T> globalContext = new HashMap<>();

    /**
     * get global IDEPluginStatus
     *
     * @param toolName toolName
     * @return IDEPluginStatus 获取IDE状态值
     */
    public static IDEPluginStatus getIDEPluginStatus(String toolName) {
        return getValueFromGlobalContext(toolName, IDE_PLUGIN_STATUS);
    }

    /**
     * set global IDEPluginStatus
     *
     * @param value    设置IDE的状态值
     * @param toolName toolName
     */
    public static void setIDEPluginStatus(String toolName, IDEPluginStatus value) {
        setValueForGlobalContext(toolName, IDE_PLUGIN_STATUS, value);
    }

    /**
     * check是否登陆
     *
     * @param toolName toolName
     * @return boolean check是否OK
     */
    public static boolean checkLogin(String toolName) {
        int value = 0;
        IDEPluginStatus valueFromGlobalContext = getIDEPluginStatus(toolName);
        value = (valueFromGlobalContext).value();

        if (value >= IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            return true;
        }

        return false;
    }

    /**
     * 获取全局上下文中缓存的信息，module不为空时，到module下的map查询
     *
     * @param module 模块
     * @param key    key值
     * @return Object value值
     */
    public static <T> T getValueFromGlobalContext(String module, String key) {
        Map<String, T> context = instance.globalContext;
        if (instance.globalContext.get(module) instanceof Map) {
            context = (Map) instance.globalContext.get(module);
        }

        return context.get(key);
    }

    /**
     * 同步设置全局上下文中缓存的信息，module不为空时，到module下的map存储
     *
     * @param module 模块
     * @param key    key值
     * @param value  value值
     * @return <T> value值
     */
    public static synchronized <T> void setValueForGlobalContext(String module, String key, T value) {
        if (module == null) {
            instance.globalContext.put(key, value);
        } else {
            if (instance.globalContext.get(module) instanceof Map) {
                Map context = (Map) instance.globalContext.get(module);
                context.put(key, value);
            }
        }
    }

    /**
     * 获取当前系统编码
     *
     * @return string value值
     */
    public static String getCurrentCharset() {
        return IDEContext.getValueFromGlobalContext(null, BaseCacheVal.CURRENT_CHARSET.vaLue());
    }
}
