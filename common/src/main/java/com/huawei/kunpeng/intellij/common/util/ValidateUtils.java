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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 通用校验工具类
 *
 * @since 1.0.0
 */
public final class ValidateUtils {
    private ValidateUtils() {
    }

    /**
     * 判断对象字符串是否为空
     * 判断对象字符串是否是null或者字符串的长度是0
     *
     * @param str 目标字符串
     * @return false 非空字符串， true 空字符串
     */
    public static boolean isEmptyString(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断对象字符串是否非空
     *
     * @param str String对象
     * @return true 非空字符串；false空字符串
     */
    public static boolean isNotEmptyString(String str) {
        return !isEmptyString(str);
    }

    /**
     * 判断是否是空集合
     * 判断对象集合是否是null或者集合中不含有其他元素
     *
     * @param collection 目标集合
     * @return true 空集合， false 非空集合
     */
    public static boolean isEmptyCollection(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空
     * 跟isEmptyCollection 返回值相对
     *
     * @param collection 对象集合
     * @return true 非空集合 ， false 空集合
     */
    public static boolean isNotEmptyCollection(Collection<?> collection) {
        return !isEmptyCollection(collection);
    }

    /**
     * 判断Map是否非空
     * 判断对象Map是否是null或者对象中没有其他元素
     *
     * @param map 对象map
     * @return true 空map， false 非空map
     */
    public static boolean isEmptyMap(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断对象map是否是非空map
     * 判断对象map是否是null或者不包含其他元素
     *
     * @param map 对象map
     * @return true 非空map, false 空map
     */
    public static boolean isNotEmptyMap(Map<?, ?> map) {
        return !isEmptyMap(map);
    }

    /**
     * 判断两个字符串是否相等，并返回相应的布尔值。
     *
     * @param left 对象left
     * @param right 对象right
     * @return boolean true：相等，false:不相等,left,right为空相等.
     */
    public static boolean equals(String left, String right) {
        if (left != null) {
            return left.equals(right);
        }

        return right == null;
    }

    /**
     * 判断数组是否为空
     *
     * @param arrayObj [数组对象]
     * @param <T> 泛型
     * @return boolean
     */
    public static <T> boolean isEmptyArray(T arrayObj) {
        if (arrayObj == null) {
            return true;
        }
        Class<?> targetClazz = arrayObj.getClass();
        if (!targetClazz.isArray()) {
            return true;
        }
        if (Array.getLength(arrayObj) == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断数组是否不为空
     *
     * @param arrayObj [数组对象]
     * @param <T> 泛型
     * @return boolean
     */
    public static <T> boolean isNotEmptyArray(T arrayObj) {
        return !isEmptyArray(arrayObj);
    }
}
