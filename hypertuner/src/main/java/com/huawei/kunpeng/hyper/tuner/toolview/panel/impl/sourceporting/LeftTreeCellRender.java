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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting;

import com.huawei.kunpeng.hyper.tuner.http.SysperfProjectServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.project.SysperfProject;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.NodeList;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.Tasklist;

import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.util.IconLoader;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * 自定义左侧树渲染类
 * 为分析报告设置图标
 *
 * @since 2020-11-18
 */
public class LeftTreeCellRender extends NodeRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
    boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            // 判断是否为根节点
            if (node.isRoot()) {
                // 若不对根节点图标置空则该方法会自动填充默认图标
                this.setIcon(null);
            }
            String nodeName = node.getUserObject().toString();
            int index = node.getPath().length;
            // 项目
            if (index == 2) {
                SysperfProject project = LeftTreeSysperfPanel.getProjectMap().get(nodeName);
                if (project == null) {
                    return;
                }
                if ("normal".equals(project.getStatus())) {
                    this.setIcon(BaseIntellijIcons.load("/assets/img/settings/success.png"));
                } else {
                    this.setIcon(BaseIntellijIcons.load("/assets/img/settings/fail.png"));
                }
                if (project.isImport()) {
                    this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/importProject.svg"));
                }
            }

            refreshStatus(node, nodeName, index);
        }
    }

    private void refreshStatus(DefaultMutableTreeNode node, String name, int index) {
        // 任务
        if (index == 3) {
            TreeNode parent = node.getParent();
            if (!(parent instanceof DefaultMutableTreeNode)) {
                return;
            }
            String projectName = ((DefaultMutableTreeNode) parent).getUserObject().toString();
            Tasklist task = LeftTreeSysperfPanel.getTaskMap().get(projectName + name);
            if (task == null) {
                return;
            }
            if ("Created".equals(task.getTaskstatus())) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/settings/schedule.svg"));
            }
            String taskName = task.getTaskname();
            boolean isSchedule = SysperfProjectServer.scheduleTaskJudge(taskName);
            if (isSchedule) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/settings/schedule_task.svg"));
            }
        }

        // 节点
        if (index == 4) {
            String key = node.getParent().getParent().toString() + node.getParent().toString();
            Tasklist task = LeftTreeSysperfPanel.getTaskMap().get(key);
            if (task == null) {
                return;
            }
            String status = getNode(task, name);
            if ("Completed".equals(status)) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/task_success.svg"));
            } else if ("Failed".equals(status) || "Cancelled".equals(status)) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/task_fail.svg"));
            } else if ("Sampling".equals(status) || "Wating".equalsIgnoreCase(status)) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/sampling.svg"));
            } else if ("Created".equals(status)) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/createNode.svg"));
            } else {
                this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/task_success.svg"));
            }
        }
    }

    private String getNode(Tasklist task, String nodeIp) {
        List<NodeList> list = task.getNodeList();
        for (NodeList nodeList : list) {
            if (nodeIp.equals(nodeList.getNodeIP())) {
                return nodeList.getSampleStatus();
            }
        }
        return "";
    }
}
