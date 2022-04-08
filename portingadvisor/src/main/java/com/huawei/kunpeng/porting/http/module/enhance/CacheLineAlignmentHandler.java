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

package com.huawei.kunpeng.porting.http.module.enhance;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import java.util.HashMap;
import java.util.Map;

/**
 * The class CacheLineHandler
 *
 * @since 2021/10/9
 */
public class CacheLineAlignmentHandler {
    /**
     * cache line alignment url
     */
    public static final String CACHE_LINE_TASK_URL = "/portadv/tasks/migration/cachelinealignment/task/";

    /**
     * query task result url
     */
    public static final String CACHE_LINE_TASK_RESULT_URL = "/portadv/tasks/migration/cachelinealignment/taskresult/";

    /**
     * singleton instance
     */
    private static final CacheLineAlignmentHandler CACHE_LINE_ALIGNMENT_HANDLER = new CacheLineAlignmentHandler();

    private CacheLineAlignmentHandler() {
    }

    /**
     * 获取单例
     *
     * @return CacheLineAlignmentHandler单实例
     */
    public static CacheLineAlignmentHandler getInstance() {
        return CACHE_LINE_ALIGNMENT_HANDLER;
    }

    /**
     * When cache line task completed, get result info: include suggestions and modified lines
     *
     * @param filePath filePath
     * @param taskName taskName
     * @return task result
     */
    public ResponseBean queryCacheLineTaskResult(String filePath, String taskName) {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            CACHE_LINE_TASK_RESULT_URL, HttpMethod.POST.vaLue(), "");
        Map<String, String> params = new HashMap<>();
        params.put("file_path", filePath);
        params.put("task_name", taskName);
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(params));
        return PortingHttpsServer.INSTANCE.requestData(request);
    }

    /**
     * Query all cache line alignment tasks in current time
     *
     * @return all cache line tasks
     */
    public ResponseBean queryCacheLineTaskInfo() {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            CACHE_LINE_TASK_URL, HttpMethod.GET.vaLue(), "");
        return PortingHttpsServer.INSTANCE.requestData(request);
    }

    /**
     * Delete cache line task by task id
     *
     * @param taskId taskId
     * @return abort success or not
     */
    public ResponseBean deleteCacheLineTask(String taskId) {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            CACHE_LINE_TASK_URL + taskId + "/", HttpMethod.DELETE.vaLue(), "");
        return PortingHttpsServer.INSTANCE.requestData(request);
    }
}
