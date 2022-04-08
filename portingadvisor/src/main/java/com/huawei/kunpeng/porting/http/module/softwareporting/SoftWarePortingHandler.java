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

package com.huawei.kunpeng.porting.http.module.softwareporting;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import java.util.Optional;

/**
 * The class: SoftWarePortingHandler handle server request
 *
 * @since 2021/10/12
 */
public class SoftWarePortingHandler {
    private static SoftWarePortingHandler instance = new SoftWarePortingHandler();

    /**
     * Singleton
     */
    private SoftWarePortingHandler() {
    }

    /**
     * get instance
     *
     * @return SoftWarePortingHandler
     */
    public static SoftWarePortingHandler getInstance() {
        return instance;
    }

    /**
     * 获取报告详情
     *
     * @param taskId taskId
     * @return Optional<ResponseBean>
     */
    public Optional<ResponseBean> getReportDetails(String taskId) {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/autopack/history/" + taskId + "/", HttpMethod.GET.vaLue(), "");
        return Optional.ofNullable(PortingHttpsServer.INSTANCE.requestData(request));
    }
}
