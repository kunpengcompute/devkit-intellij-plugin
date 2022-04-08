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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * properties类
 *
 * @since 2.3.T10
 */
public class PropertiesUtils {
    /**
     * 加载
     *
     * @param key key
     * @return 值
     */
    public static String load(String key) {
        InputStream inputStream = null;
        Properties prop = new Properties();
        try {
            inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream("location.properties");
            prop.load(inputStream);
            return prop.getProperty(key);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                }
            }
        }
        return "";
    }
}
