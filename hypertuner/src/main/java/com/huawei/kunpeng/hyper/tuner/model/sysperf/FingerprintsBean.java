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

package com.huawei.kunpeng.hyper.tuner.model.sysperf;

/**
 * 节点管理实体类
 *
 * @since 2021/03/23
 */
public class FingerprintsBean {
    private String key_length;
    private String hash_type;
    private String finger_print;
    private String node_ip;
    private String key_type;

    public String getKeyLength() {
        return key_length;
    }

    public void setKeyLength(String key_length) {
        this.key_length = key_length;
    }

    public String getHashType() {
        return hash_type;
    }

    public void setHashType(String hash_type) {
        this.hash_type = hash_type;
    }

    public String getFingerPrint() {
        return finger_print;
    }

    public void setFingerPrint(String finger_print) {
        this.finger_print = finger_print;
    }

    public String getNodeIp() {
        return node_ip;
    }

    public void setNodeIp(String node_ip) {
        this.node_ip = node_ip;
    }

    public String getKeyType() {
        return key_type;
    }

    public void setKeyType(String key_type) {
        this.key_type = key_type;
    }
}
