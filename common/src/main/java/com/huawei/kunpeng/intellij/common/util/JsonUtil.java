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

import com.huawei.kunpeng.intellij.common.log.Logger;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * JSON处理工具类
 *
 * @since 1.0.0
 */
public class JsonUtil {
    /**
     * 根据message中匹配到的DataMode的set方法设置dataModel数据体对应值
     *
     * @param message json消息串
     * @param cls     class
     * @param <T>     泛型
     * @return DataBean
     */
    public static <T> T jsonToDataModel(String message, final Class<T> cls) {
        T dataBean = null;
        if (StringUtil.stringIsEmpty(message)) {
            return dataBean;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(message);
        try {
            dataBean = cls.newInstance();
            // 存储类中的方法名
            Set<String> methodSet = new HashSet();
            for (Method method : cls.getMethods()) {
                methodSet.add(method.getName());
            }
            // 开始反射
            String methodName = null;
            String value = null;
            // 设置dataModel数据体的值
            for (Map.Entry<String, Object> entry : jsonMessage.entrySet()) {
                methodName = "set" + entry.getKey().substring(0, 1).toUpperCase(Locale.ROOT) + entry.getKey()
                    .substring(1);
                // 如果类中无该方法，则不设置值
                if (!methodSet.contains(methodName)) {
                    continue;
                }
                // 开始反射设置值
                if (entry.getValue() == null) {
                    value = "";
                } else {
                    value = (entry.getValue() instanceof JSONObject)
                        ? JsonUtil.getJsonStrFromJsonObj(entry.getValue())
                        : entry.getValue().toString();
                }
                cls.getMethod(methodName, String.class).invoke(dataBean, value);
            }
        } catch (ReflectiveOperationException e) {
            Logger.error("jsonToDataModel error: ", e);
        }
        return dataBean;
    }

    /**
     * 将Json字符串转换为json对象
     *
     * @param jsonString Json字符串
     * @param <T>        泛型
     * @return Map
     */
    public static <T> Map<String, T> getJsonObjFromJsonStr(String jsonString) {
        Map<String, T> map = new HashMap<>();
        if (StringUtil.stringIsEmpty(jsonString)) {
            return map;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            if (jsonObject instanceof Map) {
                map = (Map<String, T>) jsonObject;
            }
        } catch (JSONException e) {
            Logger.error("getJsonObjFromJsonStr error.");
        }
        return map;
    }

    /**
     * 将Json字符串转换为json对象
     *
     * @param jsonString json字符串
     * @return JSONObject json对象
     */
    public static JSONObject getJsonObjectFromJsonStr(String jsonString) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtil.stringIsEmpty(jsonString)) {
            return jsonObject;
        }
        try {
            jsonObject = JSONObject.parseObject(jsonString);
        } catch (JSONException e) {
            Logger.error("getJsonObjFromJsonStr error.");
        }
        return jsonObject;
    }

    /**
     * 将Json对象转换为json字符串
     *
     * @param <T>        泛型
     * @param jsonObject Json对象
     * @return String
     */
    public static <T> String getJsonStrFromJsonObj(T jsonObject) {
        String str = null;
        try {
            str = JSONObject.toJSONString(jsonObject);
        } catch (JSONException e) {
            Logger.error("getJsonStrFromJsonObj error: JSONException");
        }
        return str;
    }

    /**
     * 从map型对象中忽略大小写获取
     *
     * @param jsonObject Json对象
     * @param key        属性key
     * @param cla        class
     * @param <T>        泛型
     * @return Object
     */
    public static <T> T getValueIgnoreCaseFromMap(Map<String, Object> jsonObject, String key, Class<T> cla) {
        T result = null;
        if (ValidateUtils.isEmptyMap(jsonObject)) {
            return result;
        }
        Map<String, Object> mapDef = jsonObject;
        LinkedList<Map> queue = new LinkedList<Map>();
        queue.add(mapDef);
        while (!queue.isEmpty()) {
            mapDef = queue.poll();
            if (mapDef == null) {
                continue;
            }
            Map map = null;
            for (Map.Entry<String, Object> entry : mapDef.entrySet()) {
                if (entry.getKey().toLowerCase(Locale.ROOT).equals(key.toLowerCase(Locale.ROOT))) {
                    result = (T) entry.getValue();
                }

                if (entry.getValue() instanceof Map) {
                    map = (Map<String, Object>) entry.getValue();
                    queue.add(map);
                }
            }
        }
        return result;
    }
}