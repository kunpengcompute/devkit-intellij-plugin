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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.action.JavaPerfTreeAction;
import com.huawei.kunpeng.hyper.tuner.action.javaperf.JavaSamplingAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaperfContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.WebViewUtil;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.handler.JavaPerfHandler;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 删除报告
 *
 * @since 2021-8-12
 */
public class ReportDeleteDialog extends IdeaDialog {
    /**
     * 主要内容展示面板
     */
    protected IDEBasePanel mainPanel;

    private String id;

    private String name;

    private String type;

    /**
     * @param type  删除类型
     * @param id    删除分析ID
     * @param name  删除分析名称
     * @param panel 需要展示的面板之一
     */
    public ReportDeleteDialog(String type, String id, String name, IDEBasePanel panel) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.title = getdelTitle();
        this.dialogName = getdelTitle();
        this.mainPanel = panel;
        setOKAndCancelName(ImpAndExpTaskContent.OPERATE_OK, ImpAndExpTaskContent.OPERATE_CANCEL);
        initDialog(); // 初始化弹框内容
    }

    /**
     * 获取mainPanel
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认操作
     */
    @Override
    protected void onOKAction() {
        if ("sampling".equals(type)) {
            JavaSamplingAction.deleteSampling(id);
        } else if ("startProfiling".equals(type)) {
            JavaPerfToolWindowPanel.stopProfiling = "stopping";
            JavaPerfHandler.stopProfilingIntellij();
            JavaPerfHandler.startProfiling(true);
        } else if ("ONLINE_ANALYSIS".equals(type)) {
            JavaPerfToolWindowPanel.stopProfiling = "stopping";
            if (JavaPerfToolWindowPanel.profilingMessage != null) {
                JavaPerfHandler.stopProfilingIntellij();
            }
            JavaPerfToolWindowPanel.refreshProfilingNode("", false);
            JavaPerfTreeAction.instance().importOpenFileDialog(JavaPerfToolWindowPanel.type);
        } else if ("stopProfiling".equals(type)) {
            JavaPerfToolWindowPanel.stopProfiling = "stopping";
            JavaPerfHandler.stopProfilingIntellij();
            JavaPerfToolWindowPanel.refreshProfilingNode("", false);
        } else {
            String url = "";
            if ("memoryGump".equals(type)) {
                url = "java-perf/api/heap/actions/delete/" + id;
            } else if ("gcLog".equals(type)) {
                url = "java-perf/api/gcLog/" + id;
            } else {
                url = "java-perf/api/threadDump/" + id;
            }
            JavaPerfTreeAction.instance().deleteDataList(url);
        }

        if (StringUtils.isNotBlank(name)) {
            // 处理带有目录的文件
            if (StringUtils.contains(name, "/")) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
            WebViewUtil.closePage(name.trim()
                    + new StringBuilder().append(".").append(TuningIDEConstant.TUNING_KPHT).toString());
        }
    }

    /**
     * 取消操作
     */
    @Override
    protected void onCancelAction() {
        if ("startProfiling".equals(type)) {
            JavaPerfHandler.startProfiling(false);
        }
    }

    private String getdelTitle() {
        String title = "";
        if ("sampling".equals(type)) {
            title = TuningI18NServer.toLocale("plugins_hyper_tuner_sampling_delete_title");
        } else if ("startProfiling".equals(type)) {
            title = "";
        } else if ("ONLINE_ANALYSIS".equals(type)) {
            title = TuningI18NServer.toLocale("plugins_hyper_tuner_javaPerf_importProfiling_title");
        } else if ("memoryGump".equals(type)) {
            title = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_heapdump_report_delete_title");
        } else if ("gcLog".equals(type)) {
            title = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_gclog_report_delete_title");
        } else if ("stopProfiling".equals(type)) {
            title = JavaperfContent.STOP_ANALYSIS;
        } else {
            title = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_threaddump_report_delete_title");
        }
        return title;
    }
}
