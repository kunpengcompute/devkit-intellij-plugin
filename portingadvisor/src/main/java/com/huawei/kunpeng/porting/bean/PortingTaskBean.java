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

package com.huawei.kunpeng.porting.bean;

import com.huawei.kunpeng.intellij.common.util.StringUtil;

import java.util.List;
import lombok.Data;

/**
 * The class PortingTask:
 *
 * @since v1.0
 */
@Data
public class PortingTaskBean {
    private int totalCount;
    private List<Task> taskList;

    /**
     * The class Task: 源码扫描任务
     */
    @Data
    public static class Task {
        private int status;
        private String id;
        private String userName;
        private int progress;

        /**
         * 显示在左侧树的源码迁移报告名
         *
         * @return 源码迁移报告显示在左侧树的报告名
         */
        @Override
        public String toString() {
            return StringUtil.formatCreatedId(this.id);
        }
    }
}
