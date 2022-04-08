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

package com.huawei.kunpeng.hyper.tuner.action.sysperf;

import com.huawei.kunpeng.intellij.common.util.StringUtil;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 自定义 添加节点到JTree中 的Action
 *
 * @since 2021-7-29 11:26
 */
public class CustomAddNodeAction {
    DefaultMutableTreeNode oneNodeNode;

    public CustomAddNodeAction(DefaultMutableTreeNode oneNodeNode) {
        this.oneNodeNode = oneNodeNode;
    }

    /**
     * 对value进行校验，将不为空的值直接加入到目标树节点中
     * 空的值 转化为 -- 加入
     *
     * @param i18nText 国际化字段名称
     * @param value    字段值
     * @return 返回树节点
     */
    public CustomAddNodeAction addNotNullToNode(String i18nText, String value) {
        String showStr;
        if (!StringUtil.stringIsEmpty(value)) {
            showStr = value;
        } else {
            showStr = "--";
        }
        this.oneNodeNode.add(getTreeNode(i18nText, showStr));
        return this;
    }

    private DefaultMutableTreeNode getTreeNode(String keyStr, String valurStr) {
        return new DefaultMutableTreeNode(keyStr + "    " + valurStr);
    }
}
