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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base;

import lombok.Data;

import java.util.List;

/**
 * 分析任务展示对象
 *
 * @since 2021-9-9
 */
@Data
public class SchTaskOtherInfo {
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 字段名称（国际化）展示
     */
    private String fieldNameI18N;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 字段值
     */
    private String fieldValue;
    /**
     * 多选框
     */
    private List<String> showList;
    private List<String> selected;

    private int maxIntValue;
    private int minIntValue;
    private String verifyRegex;
    private boolean isMultiNodeParam;

    public SchTaskOtherInfo(EditPanelTypeEnum fieldType, String fieldName, String fieldNameI18N, String fieldValue,
    boolean isMultiNodeParam) {
        this.fieldType = fieldType.value();
        this.fieldName = fieldName;
        this.fieldNameI18N = fieldNameI18N;
        this.fieldValue = fieldValue == null ? "" : fieldValue;
        this.isMultiNodeParam = isMultiNodeParam;
    }

    public SchTaskOtherInfo(EditPanelTypeEnum fieldType, String fieldName, String fieldNameI18N, String fieldValue,
    String verifyRegex ) {
        this.fieldType = fieldType.value();
        this.fieldName = fieldName;
        this.fieldNameI18N = fieldNameI18N;
        this.fieldValue = fieldValue == null ? "" : fieldValue;
        this.verifyRegex = verifyRegex;
    }

    public SchTaskOtherInfo(EditPanelTypeEnum fieldType, String fieldName, String fieldNameI18N, List<String> showList,
    List<String> selected) {
        this.fieldType = fieldType.value();
        this.fieldName = fieldName;
        this.fieldNameI18N = fieldNameI18N;
        this.showList = showList;
        this.selected = selected;
        this.isMultiNodeParam = false;
    }

    public boolean isMultiNodeParam() {
        return isMultiNodeParam;
    }
}
