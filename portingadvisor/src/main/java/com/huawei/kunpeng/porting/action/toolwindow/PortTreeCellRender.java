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

package com.huawei.kunpeng.porting.action.toolwindow;

import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.porting.bean.AnalysisTaskBean;
import com.huawei.kunpeng.porting.common.constant.LeftTreeTitleConstant;

import com.intellij.ide.util.treeView.NodeRenderer;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 自定义左侧树渲染类
 * 为分析报告设置图标
 *
 * @since 2020-11-18
 */
public class PortTreeCellRender extends NodeRenderer {
    private static final String TXT_PNG = "/assets/img/analysis/txt.png";
    private static final String RPM_PNG = "/assets/img/analysis/rpm.png";

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
        boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            this.setIcon(null);
            Object nodeObj = node.getUserObject();
            if (nodeObj instanceof AnalysisTaskBean.Task) {
                AnalysisTaskBean.Task analysisTask = (AnalysisTaskBean.Task) nodeObj;
                String iconPath = analysisTask.getStatus() != 0 ? TXT_PNG : RPM_PNG;
                this.setIcon(BaseIntellijIcons.load(iconPath));
            }
            if (nodeObj instanceof String && LeftTreeTitleConstant.NO_REPORTS.equals(nodeObj)) {
                this.setForeground(new Color(0xA6A6A6));
            }
        }
    }
}
