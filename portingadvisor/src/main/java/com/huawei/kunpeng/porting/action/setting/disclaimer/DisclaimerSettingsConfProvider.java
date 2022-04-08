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

package com.huawei.kunpeng.porting.action.setting.disclaimer;

import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.porting.action.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.ConfigProperty;

import com.alibaba.fastjson.JSONArray;
import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * 免责声明设置界面配置器
 *
 * @since 2020-10-19
 */
public class DisclaimerSettingsConfProvider  extends ConfigurableAbstractProvider {
    /**
     * 免责声明已签署标记
     */
    private static final String DISCLAIMER_FLAG_OK = "1";

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new DisclaimerSettingsConfigurable();
    }

    @Override
    public boolean isCreate() {
        return isSignDisclaimer();
    }

    /**
     * 是否已签署免责声明
     *
     * @return 返回结果。
     */
    public static boolean isSignDisclaimer() {
        Map configDef = FileUtil.ConfigParser.parseJsonConfigFromFile(PortingIDEConstant.CONFIG_PATH);
        Object jsonArrObj = configDef.get(ConfigProperty.PORT_DISCLAIMER.vaLue());
        String disclaimerFlag = null;
        if (jsonArrObj instanceof JSONArray) {
            JSONArray usersJson = (JSONArray)jsonArrObj;
            if (usersJson.size() > 0) {
                disclaimerFlag = usersJson.getString(0);
            }
        }
        return Objects.equals(disclaimerFlag, DISCLAIMER_FLAG_OK);
    }
}