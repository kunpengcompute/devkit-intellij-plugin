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
 * SourcePortingReportBean: 源码迁移报告实体类
 *
 * @since 2021.3.5
 */
@Data
public class SourcePortingReportBean {
    private String version;
    private String[] scanMethod;
    private Info info;
    private PortingResult portingresult;
    private int portingresultnum;
    private List<PortingResultList> portingresultlist;

    /**
     * 源码扫描结果列表
     */
    @Data
    public class PortingResultList {
        private String content;
        private int total;
        private List<PortingItem> portingItems;

        /**
         * 源码扫描需要修改的项
         */
        @Data
        public class PortingItem {
            private int locbegin;
            private int locend;
            private String keyword;
            private int type;
            private String strategy;
            private String description;
            private int suggestiontype;
            private String[] replacement;
        }
    }

    /**
     * 基本信息
     */
    @Data
    public class Info {
        private String constructtool;
        private String sourcedir;
        private String gfortran;
        private String targetkernel;
        private String compilecommand;
        private Compiler compiler;
        private String targetos;
        private String osMappingDir;
        private String sourceenhancecheck;
        private Compiler cgocompiler;

        /**
         * 编译器信息
         */
        @Data
        public class Compiler {
            private String type;
            private String version;
        }
    }

    /**
     * 报告结果
     */
    @Data
    public class PortingResult {
        private String scanningrules;
        private boolean needporting;
        private FileInfo codefileinfo;
        private FileInfo makefileinfo;
        private FileInfo cmakelistsinfo;
        private FileInfo automakeinfo;
        private FileInfo asmfileinfo;
        private FileInfo fortranfileinfo;
        private FileInfo pythonfileinfo;
        private FileInfo golangfileinfo;
        private FileInfo javafileinfo;
        private FileInfo scalafileinfo;
        private int pythonlines;
        private int codelines;
        private int asmlines;
        private int fortranlines;
        private int automakelines;
        private int makefilelines;
        private int cmakelistslines;
        private int asmfilelines;
        private int golanglines;
        private int interpretedlines;
        private int javalines;
        private int scalalines;
        private int cLine;
        private int asmLine;
        private double workload;
        private PortingLevel portingLevel;
        private List<TipsInfo> tips;

        /**
         * 文件信息
         */
        @Data
        public class FileInfo {
            private int totalcount;
            private int needtranscount;
            private List<String> files;
        }

        /**
         * Porting级别类型
         */
        @Data
        public class PortingLevel {
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
                    private String pathName;
                    private String url;
                    private String desc;
                    private String oper;
                    private String[] pathExt;
                    private String type;
                    private String level;
                    private String downloadDesc;
                    private String result;

                    // html合并字段，非后端接口返回数据
                    private int rowSpan;
                }
            }
        }

        /**
         * 扫描Go源码返回的提示信息
         */
        @Data
        public class TipsInfo {
            private String infoCn;
            private String infoEn;
        }
    }

    /**
     * 2.3.T10源码迁移报告优化，后端修改返回数据结构，为兼容旧版本特殊处理类
     */
    @Data
    public static class Files {
        private String filetype;
        private int linecount;
        private String filedirectory;
    }
}
