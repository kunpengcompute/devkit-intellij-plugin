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

import java.util.List;

/**
 * AnalysisServer: 软件包重构与后端交互类
 *
 * @since v1.0
 */
public class AnalysisTaskBean {
    private int hisTaskNumStatus;
    private List<AnalysisTaskBean.Task> taskList;

    public int getHisTaskNumStatus() {
        return hisTaskNumStatus;
    }

    public void setHisTaskNumStatus(int hisTaskNumStatus) {
        this.hisTaskNumStatus = hisTaskNumStatus;
    }

    public List<AnalysisTaskBean.Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<AnalysisTaskBean.Task> taskList) {
        this.taskList = taskList;
    }

    /**
     * The class Task: 软件包重构任务
     */
    public static class Task {
        private int status;
        private String path;
        private String name;
        private String createTime;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return name + " " + this.createTime.replace('-','/');
        }
    }
}
