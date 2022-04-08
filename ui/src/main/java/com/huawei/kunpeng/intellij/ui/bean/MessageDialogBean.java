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

package com.huawei.kunpeng.intellij.ui.bean;

import com.huawei.kunpeng.intellij.common.bean.DataBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import javax.swing.Icon;

/**
 * 消息弹框数据体
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MessageDialogBean extends DataBean {
    /**
     * 消息文本
     */
    private String message;

    /**
     * 弹框标题
     */
    private String title;

    /**
     * 按钮信息数组， 数组成员为按钮信息枚举
     */
    private List<IDEMessageDialogUtil.ButtonName> buttonNames;

    /**
     * 弹框默认选中的按钮下标
     */
    private int defaultButton = 0;

    /**
     * 图标可为空
     */
    private Icon icon;

    /**
     * 自定义toString方法，返回数据体的基本信息
     *
     * @return 数据体基本信息字符串格式
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: " + title + System.getProperty("line.separator"));
        sb.append("Message: " + message + System.getProperty("line.separator"));
        int count = 0;
        for (IDEMessageDialogUtil.ButtonName button : buttonNames) {
            sb.append("Button" + count++ + ": key: " + button.getKey() + " name: " + button.getName());
            sb.append(System.getProperty("line.separator"));
        }
        sb.append("Default Button name: " + buttonNames.get(getDefaultButton()).getName());
        return sb.toString();
    }
}
