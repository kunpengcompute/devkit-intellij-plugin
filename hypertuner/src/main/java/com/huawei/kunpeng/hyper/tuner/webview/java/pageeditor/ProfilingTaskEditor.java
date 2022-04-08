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

package com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pagewebview.JavaWebView;
import com.huawei.kunpeng.hyper.tuner.webview.java.pagewebview.ShowProfilingTaskWebView;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 扫描菜单事件
 *
 * @since 2021-01-06
 */
public class ProfilingTaskEditor extends TuningWebFileEditor {
    /**
     * 参数
     */
    public static MessageBean message;
    private static boolean isExport = false;
    private final ShowProfilingTaskWebView showProfilingTaskWebView;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描Webview虚拟文件
     */
    public ProfilingTaskEditor(VirtualFile file) {
        currentFile = file;
        showProfilingTaskWebView = new ShowProfilingTaskWebView(message, isExport);
    }

    /**
     * 创建任务入口
     *
     * @param msg      msg
     * @param exported exported
     * @param pageName pageName
     */
    public static void openPage(MessageBean msg, boolean exported, String pageName) {
        message = msg;
        isExport = exported;
        String fileName = new StringBuilder(TuningIDEConstant.TUNING_KPHT)
                .append(SysperfContent.PATH_SEPARATOR)
                .append(PageType.SHOW_PROFILING_TASK.value())
                .append(SysperfContent.PATH_SEPARATOR)
                .append(pageName)
                .append(".")
                .append(TuningIDEConstant.TUNING_KPHT)
                .toString();

        closeWebView(fileName);
        openWebView(fileName);
    }

    @Override
    public JavaWebView getWebView() {
        return showProfilingTaskWebView;
    }

    @Override
    public JComponent getComponent() {
        return showProfilingTaskWebView.getContent();
    }

    /**
     * 关闭连接
     */
    @Override
    public void dispose() {
        super.dispose();
        showProfilingTaskWebView.dispose();
    }
}
