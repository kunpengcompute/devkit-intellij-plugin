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

package com.huawei.kunpeng.porting.webview.pageeditor;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.porting.common.constant.enums.PageType;
import com.huawei.kunpeng.porting.webview.pagewebview.CloudEnvApplicationProcessWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 云环境申请流程视图编辑器
 *
 * @since 2.3.2
 */
public class CloudEnvApplicationProcessEditor extends WebFileEditor {
    /**
     * 云环境视图对象
     */
    private final CloudEnvApplicationProcessWebView cloudEnvWebView;

    /**
     * CloudEnvApplicationProcessEditor
     *
     * @param file file
     */
    public CloudEnvApplicationProcessEditor(VirtualFile file) {
        currentFile = file;
        cloudEnvWebView = new CloudEnvApplicationProcessWebView();
    }

    @Override
    public JComponent getComponent() {
        return cloudEnvWebView.getContent();
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    @Override
    public PortingWebView getWebView() {
        return cloudEnvWebView;
    }

    @Override
    public void dispose() {
        super.dispose();
        cloudEnvWebView.dispose();
    }

    /**
     * 打开页面
     *
     * @param pageName 页面名称
     */
    public static void openPage(String pageName) {
        String fileName = new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.CLOUD_ENV_APPLICATION_PROCESS.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(pageName)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();
        // 打开新文件
        openWebView(fileName);
    }
}

