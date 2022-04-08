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
 * 预约任务多节点展示对象
 *
 * @since 2021-9-11
 */
@Data
public class MultiNodeParam {
    private int nodeId;
    private String nodeIPStr;
    private String nodeNameStr;
    private boolean nodeStatus;
    private List<SchTaskOtherInfo> nodeParamList;

    /**
     * 构造函数
     *
     * @param nodeId      节点id
     * @param nodeIPStr   节点ip
     * @param nodeNameStr 节点名称
     * @param nodeStatus  节点状态
     */
    public MultiNodeParam(int nodeId, String nodeIPStr, String nodeNameStr, boolean nodeStatus) {
        this.nodeId = nodeId;
        this.nodeIPStr = nodeIPStr;
        this.nodeNameStr = nodeNameStr;
        this.nodeStatus = nodeStatus;
    }
}
