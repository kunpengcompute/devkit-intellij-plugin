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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaperfContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.NotNull;

import java.awt.Dimension;
import java.text.MessageFormat;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 删除分析报告Panel
 *
 * @since 2021-8-12
 */
public class ReportDeletePanel extends IDEBasePanel {
    private static final String WARN_PATH = "/assets/img/common/icon_warn.png";
    private final boolean isAdmin = UserInfoContext.getInstance().getRole()
            .equals(TuningUserManageConstant.USER_ROLE_ADMIN);
    private JPanel mainPanel;
    private JLabel deleteLabel;
    private String type;
    private String name;
    private String userName;

    public ReportDeletePanel(String type, String name, String userName) {
        this.type = type;
        this.name = name;
        this.userName = userName;
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, ImpAndExpTaskContent.DELETE_ITEM, false);
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        String deleteContent = getDeleteContent(type);
        Icon deleteIcon = BaseIntellijIcons.load(WARN_PATH);
        this.deleteLabel.setIcon(deleteIcon);
        this.deleteLabel.setText(deleteContent);
        mainPanel.setPreferredSize(new Dimension(500, 40));
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    private String getDeleteContent(String type) {
        String deleteContent;
        if ("sampling".equals(type)) {
            deleteContent = getSamplingContent();
        } else if ("startProfiling".equals(type)) {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_start_profiling_tip");
        } else if ("ONLINE_ANALYSIS".equals(type)) {
            deleteContent = getOnlineAnalysisContent();
        } else if ("memoryGump".equals(type)) {
            deleteContent = getMemoryGumpContent();
        } else if ("gcLog".equals(type)) {
            deleteContent = getGcLogContent();
        } else if ("stopProfiling".equals(type)) {
            deleteContent = JavaperfContent.OK_STOP_ANALYSIS;
        } else {
            deleteContent = getThreaddumpContent();
        }
        return deleteContent;
    }

    @NotNull
    private String getThreaddumpContent() {
        String deleteContent;
        if (isAdmin && !"tunadmin".equals(userName)) {
            deleteContent =
                    TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_threaddump_report_delete_user_tip");
            deleteContent = MessageFormat.format(deleteContent, userName, name);
        } else {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_threaddump_report_delete_tip");
            deleteContent = MessageFormat.format(deleteContent, name);
        }
        return deleteContent;
    }

    @NotNull
    private String getGcLogContent() {
        String deleteContent;
        if (isAdmin && !"tunadmin".equals(userName)) {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_gclog_report_delete_user_tip");
            deleteContent = MessageFormat.format(deleteContent, userName, name);
        } else {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_gclog_report_delete_tip");
            deleteContent = MessageFormat.format(deleteContent, name);
        }
        return deleteContent;
    }

    @NotNull
    private String getMemoryGumpContent() {
        String deleteContent;
        if (isAdmin && !"tunadmin".equals(userName)) {
            deleteContent =
                    TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_heapdump_report_delete_user_tip");
            deleteContent = MessageFormat.format(deleteContent, userName, name);
        } else {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_heapdump_report_delete_tip");
            deleteContent = MessageFormat.format(deleteContent, name);
        }
        return deleteContent;
    }

    @NotNull
    private String getOnlineAnalysisContent() {
        String deleteContent;
        if (JavaPerfToolWindowPanel.isImporting()) {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaPerf_importProfiling_warningtip2");
        } else {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaPerf_importProfiling_warningtip");
        }
        return deleteContent;
    }

    @NotNull
    private String getSamplingContent() {
        String deleteContent;
        if (isAdmin && !"tunadmin".equals(userName)) {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_sampling_delete_user_tip");
            deleteContent = MessageFormat.format(deleteContent, userName, name);
        } else {
            deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_sampling_delete_tip");
            deleteContent = MessageFormat.format(deleteContent, name);
        }
        return deleteContent;
    }
}
