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

/**
 * SoftwareAssessmentTask: 软件迁移评估数据获取
 *
 * @since 2021.1.13
 */
public class SoftwareAssessmentTaskBean {
    private int hisTaskNumStatus;
    private List<SoftwareAssessmentTaskBean.Task> taskList;

    public int getHisTaskNumStatus() {
        return hisTaskNumStatus;
    }

    public void setHisTaskNumStatus(int hisTaskNumStatus) {
        this.hisTaskNumStatus = hisTaskNumStatus;
    }

    public List<SoftwareAssessmentTaskBean.Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<SoftwareAssessmentTaskBean.Task> taskList) {
        this.taskList = taskList;
    }

    /**
     * The class Task: 软件迁移评估任务
     */
    public static class Task {
        private int status;
        private String id;
        private String userName;
        private int progress;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @Override
        public String toString() {
            return StringUtil.formatCreatedId(this.id);
        }
    }
}
