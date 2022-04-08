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

package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.ReanalyzeTaskWebView;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.WebView;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 扫描菜单事件
 *
 * @since 2021-01-06
 */
public class ReanalyzeTaskEditor extends TuningWebFileEditor {
    private final ReanalyzeTaskWebView reanalyzeTaskWebView;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描Webview虚拟文件
     */
    public ReanalyzeTaskEditor(VirtualFile file) {
        currentFile = file;
        reanalyzeTaskWebView = new ReanalyzeTaskWebView();
    }

    @Override
    public WebView getWebView() {
        return reanalyzeTaskWebView;
    }

    @Override
    public JComponent getComponent() {
        return reanalyzeTaskWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        reanalyzeTaskWebView.dispose();
    }

    /**
     * 编辑任务入口
     */
    public static void openPage() {
        String pageName = LeftTreeSysperfPanel.getSelectTask().getTaskname() + '-' +
                LeftTreeSysperfPanel.getSelectTask().getNodeList().get(0).getNodeIP();
        String projectName = LeftTreeSysperfPanel.getSelectProject().getProjectName();
        String fileName = new StringBuilder(TuningIDEConstant.TUNING_KPHT)
                .append(SysperfContent.PATH_SEPARATOR)
                .append(PageType.REANALYZE_TASK.value())
                .append(SysperfContent.PATH_SEPARATOR)
                .append(projectName + "-" + pageName)
                .append(".")
                .append(TuningIDEConstant.TUNING_KPHT)
                .toString();

        closeWebView(fileName);
        openWebView(fileName);
    }
}
