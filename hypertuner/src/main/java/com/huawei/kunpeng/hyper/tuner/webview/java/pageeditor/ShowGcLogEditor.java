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
import com.huawei.kunpeng.hyper.tuner.webview.java.pagewebview.ShowGcLogWebView;
import com.huawei.kunpeng.hyper.tuner.webview.java.pagewebview.ShowSamplingTaskWebView;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 扫描菜单事件
 *
 * @since 2021-01-06
 */
public class ShowGcLogEditor extends TuningWebFileEditor {
    private final ShowGcLogWebView showGcLogWebView;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描Webview虚拟文件
     */
    public ShowGcLogEditor(VirtualFile file) {
        currentFile = file;
        showGcLogWebView = new ShowGcLogWebView();
    }

    @Override
    public JavaWebView getWebView() {
        return showGcLogWebView;
    }

    @Override
    public JComponent getComponent() {
        return showGcLogWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        showGcLogWebView.dispose();
    }

    /**
     * 展示采样分析
     *
     * @param pageName pageName
     */
    public static void openPage(String pageName) {
        ;
        String fileName = new StringBuilder(TuningIDEConstant.TUNING_KPHT)
                .append(SysperfContent.PATH_SEPARATOR)
                .append(PageType.SHOW_GC_LOG.value())
                .append(SysperfContent.PATH_SEPARATOR)
                .append(pageName)
                .append(".")
                .append(TuningIDEConstant.TUNING_KPHT)
                .toString();

        closeWebView(fileName);
        openWebView(fileName);
    }
}
