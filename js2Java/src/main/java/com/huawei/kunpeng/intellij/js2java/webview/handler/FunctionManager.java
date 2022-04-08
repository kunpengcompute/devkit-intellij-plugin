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

package com.huawei.kunpeng.intellij.js2java.webview.handler;

import com.huawei.kunpeng.intellij.common.log.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * js与java交互函数管理器
 *
 * @since 1.0.0
 */
public class FunctionManager {
    private static Map<Class<? extends FunctionHandler>, FunctionHandler> functionMap = new HashMap<>();

    /**
     * 获取函数实例-单例
     *
     * @param cla 具体处理函数
     * @return FunctionHandler
     */
    public static FunctionHandler getFunctionHandler(Class<? extends FunctionHandler> cla) {
        if (cla == null || !FunctionHandler.class.isAssignableFrom(cla)) {
            return functionMap.get(cla);
        }
        try {
            if (functionMap.get(cla) == null) {
                FunctionHandler obj = null;
                obj = cla.newInstance();

                functionMap.put(cla, obj);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.error("getFunctionHandler error, InstantiationException | IllegalAccessException");
        }
        return functionMap.get(cla);
    }
}
