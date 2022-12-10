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
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.IDELoginWebView;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.WebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;

import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

/**
 * 代理页面Editor
 *
 * @since 2021-01-06
 */
public class IDELoginEditor extends TuningWebFileEditor {
    private final IDELoginWebView proxyIndexWebView;
    private static Integer localPort;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描WebView虚拟文件
     */
    public IDELoginEditor(VirtualFile file) {
        currentFile = file;
        proxyIndexWebView = new IDELoginWebView(localPort + "");
    }

    @Override
    public WebView getWebView() {
        return proxyIndexWebView;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        return proxyIndexWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        proxyIndexWebView.dispose();
    }

    /**
     * 打开性能分析工具端登录页
     */
    public static void openPage(String localPortStr) {
        localPort = Integer.parseInt(localPortStr);
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.PROXY_INDEX.value() +
                IDEConstant.PATH_SEPARATOR +
                "HyperTuner" +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
    public static void openPage() {
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.PROXY_INDEX.value() +
                IDEConstant.PATH_SEPARATOR +
                "HyperTuner" +
                "." +
                TuningIDEConstant.TUNING_KPHT;
        openWebView(fileName);
    }

    public static boolean isOpened() {
        String fileName = "HyperTuner." + TuningIDEConstant.TUNING_KPHT;
        return IDELoginEditor.isWebViewOpen(fileName);
    }
}
