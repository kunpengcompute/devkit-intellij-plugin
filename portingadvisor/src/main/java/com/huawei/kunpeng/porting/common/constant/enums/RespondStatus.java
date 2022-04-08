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

package com.huawei.kunpeng.porting.common.constant.enums;

/**
 * 接口响应状态枚举
 *
 * @since 2020-10-14
 */
public enum RespondStatus {
    PROCESS_STATUS_ERROR("-1"),
    PROCESS_STATUS_NORMAL("0"),
    PROCESS_STATUS_NOT_NORMAL("1"),

    PROCESS_STATUS_FAILED("2"),
    PROCESS_STATUS_RUNNING("1"),
    PROCESS_STATUS_SUCCESS("0"),
    PROCESS_FINISH("3"),

    PROCESS_PORTING_TEMPLATE_STATUS_FAILED("-1"),
    PROCESS_PORTING_TEMPLATE_STATUS_SUCCESS("0"),

    /**
     * 弱密码提示
     */
    LOGIN_PWD_WEAK("0x040300"),

    /**
     * 登录成功并是首次登录
     */
    LOGIN_FIRST_SUCCESS("0x040301"),

    /**
     * 登录成功并密码已过期
     */
    LOGIN_SUCCESS_PWD_EXPIRED("0x040302"),

    /**
     * 密码即将过期
     */
    LOGIN_PWD_EXPIRED("0x040312"),

    /**
     * 上传文件已存在
     */
    UPLOAD_FILE_EXIST("0x010115"),

    /**
     * 上传文件不支持
     */
    UPLOAD_FILE_NOT_SUPPORT("0x01071a"),

    /**
     * 上传文件类型错误
     */
    WRONG_FILE_TYPE("0x010114"),

    /**
     * 磁盘空间不足
     */
    DISK_NOT_ENOUGH("0x010611"),

    /**
     * 分析扫描不支持类型，目标OS类型不同，无法支持
     */
    UN_SUPPORT_TYPE("0x0d0313"),

    /**
     * 文件上传任务已达到上限
     */
    UPLOAD_FILE_TOP_LIMIT("0x010125"),

    /**
     * web服务证书不存在/密钥组件被删除
     */
    CER_NOT_EXIST("0x060610"),

    /**
     * 服务器重启中
     */
    NGINX_STARTING("0x060601"),

    /**
     * 文件或文件夹名中不能包含中文、空格以及^ ` / | ; & $ > < \ ! 等特殊字符，请修改后重试
     */
    UPLOAD_FILE_NAME_NOT_SUPPORT("0x010416"),

    /**
     * 报告文件被锁定无法查看和修改(源码迁移)
     */
    REPORT_FILE_LOCKED("0x0d0223"),

    /**
     * 该报告不是最新的，请重新创建分析任务
     */
    REPORT_NOT_NEW("0x0d0112"),

    /**
     * 报告文件被锁定无法查看和修改(内存一致性)
     */
    REPORT_FILE_LOCKED_MEMORY_CONSISTENCY("0x0d0a20"),

    /**
     * 专项软件迁移yum运行失败
     */
    MIGRATION_CENTER_YUM_FAILED("0x0d0604");

    private final String valueStatus;

    RespondStatus(String valueStatus) {
        this.valueStatus = valueStatus;
    }

    /**
     * 获取字符串类型值
     *
     * @return String
     */
    public String value() {
        return valueStatus;
    }

    /**
     * 通过延伸信息value获取RespondStatus类的一个枚举实例
     *
     * @param valueStatus 响应状态
     * @return String
     */
    public static RespondStatus getStatusByValue(String valueStatus) {
        for (RespondStatus status : RespondStatus.values()) {
            if (status.value().equals(valueStatus)) {
                return status;
            }
        }
        return RespondStatus.PROCESS_STATUS_NORMAL;
    }
}
