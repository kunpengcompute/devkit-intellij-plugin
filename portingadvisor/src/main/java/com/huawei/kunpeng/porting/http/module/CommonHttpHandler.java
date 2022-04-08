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

package com.huawei.kunpeng.porting.http.module;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import org.jetbrains.annotations.NotNull;

/**
 * The class CommonHandler: porting server common handler
 *
 * @since 2021/10/11
 */
public class CommonHttpHandler {
    /**
     * Porting 插件 统一查询后端任务进度接口
     *
     * @param taskType taskType
     * @param taskId   taskId
     * @return ResponseBean
     */
    public static ResponseBean queryTaskProgress(@NotNull String taskType, @NotNull String taskId) {
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        RequestDataBean request = new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING, url, HttpMethod.GET.vaLue(), "");
        return PortingHttpsServer.INSTANCE.requestData(request);
    }
}
