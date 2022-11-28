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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.about;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.AboutDialog;

import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * The class PortingAboutDialog: 弹出关于版本的Dialog
 *
 * @since v1.0
 */
public class TuningAboutDialog extends AboutDialog {
    public TuningAboutDialog(@Nullable Project project) {
        super(project);
    }

    @Override
    protected String getProductInfo() {
        return I18NServer.toLocale("plugins_hyper_tuner_about_product_info");
    }

    @Override
    protected String getProductTitle() {
        return I18NServer.toLocale("plugins_hyper_tuner_about_product_title");
    }

    @Override
    protected String getProductVersion() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        Object configVersionObj = config.get(ConfigProperty.PORT_VERSION.vaLue());
        if (configVersionObj instanceof List) {
            List configList = (List) configVersionObj;
            if (!configList.isEmpty()) {
                String port_version = configList.get(0) + "";
                return I18NServer.toLocale("plugins_hyper_tuner_about_product_port_version")+port_version;
            }
        }
        return I18NServer.toLocale("plugins_hyper_tuner_about_product_version");
    }

    @Override
    protected String getProductServerVersion() {
        String product_server_version = I18NServer.toLocale("plugins_hyper_tuner_about_product_server_version");

        String SERVER_VERSION_URL = "/user-management/api/v2.2/users/version/";
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, SERVER_VERSION_URL,
                HttpMethod.GET.vaLue(), false);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            Logger.info("Server Version not found.");
            product_server_version = null;
        } else {
            String responseBeanDataJsStr = responseBean.getData();
            JSONObject jsonObject = JSON.parseObject(responseBeanDataJsStr);
            String serverVersionStr = jsonObject.getString("version");
            product_server_version = product_server_version + serverVersionStr;
        }
        return product_server_version;
    }
}
