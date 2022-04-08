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

import com.huawei.kunpeng.intellij.common.bean.DataBean;

import java.util.List;

/**
 * 软件包重构报告结果类
 *
 * @since vT4
 */
public class PkgRebuildingReportBean extends DataBean {
    private String packagePath;
    private String resultPath;
    private String reportTime;
    private String status;
    private List<PkgRebuildingReportBean.MissingFile> missing;
    private List<PkgRebuildingReportBean.ReplacedFile> replaced;

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MissingFile> getMissing() {
        return missing;
    }

    public void setMissing(List<MissingFile> missing) {
        this.missing = missing;
    }

    public List<ReplacedFile> getReplaced() {
        return replaced;
    }

    public void setReplaced(List<ReplacedFile> replaced) {
        this.replaced = replaced;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    /**
     * The class Task: 软件包重构报告依赖缺少文件
     */
    public static class MissingFile extends ReplacedFile {
    }

    /**
     * The class Task: 软件包重构报告依赖已更新文件
     */
    public static class ReplacedFile {
        private int status;
        private String path;
        private String name;
        private String url;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
