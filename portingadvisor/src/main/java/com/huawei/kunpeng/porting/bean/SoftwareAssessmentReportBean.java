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

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import lombok.Data;

/**
 * SoftwareAssessmentReportBean: 软件迁移评估报告实体类
 *
 * @since 2021.3.1
 */
@Data
public class SoftwareAssessmentReportBean {
    private String version;
    private FilePath installationPackagePath;
    private FilePath softwareInstallationPath;
    private String targetOs;
    private String targetSystemKernelVersion;
    private List<DependencyPackages> dependencyPackages;

    /**
     * 文件路径
     */
    @Data
    public class FilePath {
        private String[] path;
    }

    /**
     * 依赖包
     */
    @Data
    public class DependencyPackages {
        private String packageName;
        private PortingLevel portingLevel;

        /**
         * Porting级别
         */
        @Data
        public class PortingLevel {
            private boolean needporting;

            @JSONField(name = "0")
            private Item zero;

            @JSONField(name = "1")
            private Item one;

            @JSONField(name = "2")
            private Item two;

            @JSONField(name = "3")
            private Item three;

            @JSONField(name = "4")
            private Item four;

            @JSONField(name = "5")
            private Item five;

            @JSONField(name = "6")
            private Item six;

            /**
             * 与架构相关的依赖库文件
             */
            @Data
            public class Item {
                private int amount;
                private List<BinDetailInfo> binDetailInfo;

                /**
                 * 与架构相关的依赖库文件详细信息
                 */
                @Data
                public class BinDetailInfo {
                    private int number;
                    private String libname;
                    private String libversion;
                    private String path;
                    private String url;
                    private String desc;
                    private String oper;
                    private String[] pathExt;
                    private String type;
                    private String level;
                    private String downloadDesc;
                    private String result;
                    private boolean soFileHasUrl;
                    private List<String>[] soInfo;
                    private boolean isAarch64;

                    // 非后端接口数据，拼接html时重复url合并
                    private int urlRowSpan;
                }
            }
        }
    }
}
