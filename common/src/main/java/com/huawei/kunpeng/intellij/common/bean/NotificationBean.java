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

package com.huawei.kunpeng.intellij.common.bean;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 右下通知栏数据体
 *
 * @since 1.0.0
 */
@Data
@ToString(of = {"title", "content", "type", "project"})
@EqualsAndHashCode(callSuper = false)
public class NotificationBean extends DataBean {
    /**
     * 默认构造函数
     */
    public NotificationBean() {
        this(null, null, null);
    }

    private String title;

    private String content;

    private NotificationType type;

    private Project project;

    /**
     * 推荐构造函数
     *
     * @param title   通知框title
     * @param content 通知内容
     * @param type    通知类型
     */
    public NotificationBean(String title, String content, NotificationType type) {
        this.title = title;
        this.content = content;
        this.type = type;
    }
}
