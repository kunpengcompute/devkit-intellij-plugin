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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaperfContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.DateUtil;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.Members;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.SamplingTaskInfo;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;

import com.intellij.ide.util.treeView.NodeRenderer;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
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
public class LeftTreeCellRenderJava extends NodeRenderer {
    /**
     * 图标-目标环境管理
     */
    public static final String GUARDIAN_MANAGEMENT_ICON_PATH =
            "/assets/img/javaperf/guardian/guardian_management@1x.svg";
    /**
     * 图标-重启
     */
    public static final String RESTART_ICON_PATH = "/assets/img/javaperf/common/restart@1x.svg";
    /**
     * 图标-删除按钮
     */
    public static final String DELETE_ICON_PATH = "/assets/img/javaperf/common/delete@1x.svg";
    /**
     * 图标-导入
     */
    public static final String IMPORT_ICON_PATH = "/assets/img/javaperf/common/import@1x.svg";
    /**
     * 图标-导出
     */
    public static final String EXPORT_ICON_PATH = "/assets/img/javaperf/common/export@1x.svg";
    /**
     * 图标-分析记录管理
     */
    public static final String RECORD_MANAGE_ICON_PATH = "/assets/img/javaperf/common/analysis_record_manage@1x.svg";
    /**
     * 图标-停止分析
     */
    public static final String STOP_ICON_PATH = "/assets/img/javaperf/online/stop_analysis@1x.svg";

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
            String name = node.getUserObject().toString();
            int index = node.getPath().length;
            if (index == 2) {
                // 二级节点：目标环境/在线分析/采样分析/数据列表
                level2NodeShowHandle(name);
            }
            if (index == 3) {
                if (node != null) {
                    // 三级节点：服务器/在线分析demo/用户/用户
                    level3NodeShowHandle(node, name);
                }
            }
            if (index == 4) {
                if (node != null) {
                    level4NodeShowHandle(node);
                }
            }
            if (index == 5) {
                level5NodeShowHandle();
            }
            return;
        }
    }

    // 二级节点
    private void level2NodeShowHandle(String name) {
        if (JavaperfContent.MEMBERS_LIST.equals(name)) {
            this.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/guardian/guardian_list@1x.svg"));
        } else if (JavaperfContent.ONLINE_ANALYSIS.equals(name)) {
            this.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/online/online_analysis@1x.svg"));
        } else if (JavaperfContent.SAMPLING_ANALYSIS.equals(name)) {
            this.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/sampling/sampling_analisys@1x.svg"));
        } else if (JavaperfContent.DATA_LIST.equals(name)) {
            this.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/datalist/data_list@1x.svg"));
        } else {
            return;
        }
    }

    // 三级节点
    private void level3NodeShowHandle(DefaultMutableTreeNode node, String name) {
        String parentName = node.getParent().toString();
        if (JavaperfContent.MEMBERS_LIST.equals(parentName)) {
            memberListSetIcon(name);
        } else if (JavaperfContent.ONLINE_ANALYSIS.equals(parentName)) {
            if (name.contains(TuningI18NServer.toLocale("plugins_hyper_tuner_profiling_import_time"))) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/task_success.svg"));
            } else {
                this.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/online/analysing.svg"));
            }
        } else if (JavaperfContent.SAMPLING_ANALYSIS.equals(parentName)) {
            if (UserInfoContext.getInstance().getRole() != null &&
                    UserInfoContext.getInstance().getRole().equals(TuningUserManageConstant.USER_ROLE_ADMIN)) {
                this.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/user@1x.svg"));
            } else {
                setSamplingSvg(UserInfoContext.getInstance().getUserName(), name);
            }
        } else if (JavaperfContent.DATA_LIST.equals(parentName)) {
            this.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/datalist/data_type(thread_dump)@1x.svg"));
        } else {
            return;
        }
    }

    private void memberListSetIcon(String name) {
        List<Members> userEnvironmentList = JavaPerfToolWindowPanel.getUserEnvironmentList();
        Iterator<Members> iterator = userEnvironmentList.iterator();
        while (iterator.hasNext()) {
            Members member = iterator.next();
            if (TuningUserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole())) {
                if ("tunadmin".equals(member.getOwner().getUsername())) {
                    if (name.equals(member.getName())) {
                        setGuardianStatusIconPath(member.isRunningInContainer(), member.getState());
                    }
                } else {
                    if (name.equals(member.getName() + '(' + member.getOwner().getUsername() + ')')) {
                        setGuardianStatusIconPath(member.isRunningInContainer(), member.getState());
                    }
                }
            } else {
                if (name.equals(member.getName())) {
                    setGuardianStatusIconPath(member.isRunningInContainer(), member.getState());
                }
            }
        }
    }

    /**
     * 设置目标环境列表子节点
     * 服务器状态图标路径（分为在线/离线和容器/物理）
     *
     * @param isRunningInContainer 是否为容器
     * @param status               状态
     * @return 图标路径
     */
    private void setGuardianStatusIconPath(boolean isRunningInContainer, String status) {
        String statusIcon = "";
        if (isRunningInContainer) {
            if (GuardianMangerConstant.TABLE_COL_STATUS_CONNECTED.equals(status)) {
                statusIcon = "/assets/img/javaperf/guardian/server_online_container@1x.svg";
            } else {
                statusIcon = "/assets/img/javaperf/guardian/server_offline_container@1x.svg";
            }
        } else {
            if (GuardianMangerConstant.TABLE_COL_STATUS_CONNECTED.equals(status)) {
                statusIcon = "/assets/img/javaperf/guardian/server_online_physic@1x.svg";
            } else {
                statusIcon = "/assets/img/javaperf/guardian/server_offline_physic@1x.svg";
            }
        }
        this.setIcon(BaseIntellijIcons.load(statusIcon));
    }

    private void level4NodeShowHandle(DefaultMutableTreeNode node) {
        // 获取四级 当前节点名称 -任务名称
        String nodeNameL4 = node.getUserObject().toString();

        // 获取三级 父亲节点（线程转储/内存转储）/用户列表
        TreeNode parentNode = node.getParent();
        if (!(parentNode instanceof DefaultMutableTreeNode)) {
            return;
        }
        String parentNameL3 = ((DefaultMutableTreeNode) parentNode).getUserObject().toString();

        // 获取二级 爷爷节点 （目标环境/在线分析/采样分析/数据列表）
        TreeNode grandpaNode = parentNode.getParent();
        if (!(grandpaNode instanceof DefaultMutableTreeNode)) {
            return;
        }
        String grandpaNameL2 = ((DefaultMutableTreeNode) grandpaNode).getUserObject().toString();
        if (JavaperfContent.SAMPLING_ANALYSIS.equals(grandpaNameL2)) {
            // 此节点为用户的采样分析的一个实例,此时三级节点为用户名
            setSamplingSvg(parentNameL3, nodeNameL4);
        }
        if (JavaperfContent.DATA_LIST.equals(grandpaNameL2)) {
            String iconPath = "";
            if (!TuningUserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole())) {
                iconPath = "/assets/img/sysperf/task_success.svg";
            } else {
                iconPath = "/assets/img/javaperf/user@1x.svg";
            }
            this.setIcon(BaseIntellijIcons.load(iconPath));
        }
    }

    private void level5NodeShowHandle() {
        this.setIcon(BaseIntellijIcons.load("/assets/img/sysperf/task_success.svg"));
    }

    private void setSamplingSvg(String parentNameL3, String nodeNameL4) {
        List<SamplingTaskInfo> taskList = JavaPerfToolWindowPanel.getUserTask().get(parentNameL3);
        if (taskList == null) {
            return;
        }
        String stata = getSamplingTaskInfoState(taskList, nodeNameL4);
        String iconPath = "";
        if (stata == null) {
            return;
        }
        if (JavaperfContent.SAMPLING_STATUS_FINISHED.equals(stata)) {
            iconPath = "/assets/img/sysperf/task_success.svg";
        } else {
            iconPath = "/assets/img/sysperf/sampling.svg";
        }
        this.setIcon(BaseIntellijIcons.load(iconPath));
    }

    private String getSamplingTaskInfoState(List<SamplingTaskInfo> taskList, String keyName) {
        for (SamplingTaskInfo samplingTaskInfo : taskList) {
            if (keyName.equals(
                    samplingTaskInfo.getName()
                            + JavaperfContent.DATA_LIST_CREATE_TIME
                            + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()))) {
                return samplingTaskInfo.getState();
            }
            if (keyName.equals(
                    samplingTaskInfo.getName()
                            + JavaperfContent.DATA_LIST_IMPORT_TIME
                            + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()))) {
                return samplingTaskInfo.getState();
            }
        }
        return "";
    }
}
