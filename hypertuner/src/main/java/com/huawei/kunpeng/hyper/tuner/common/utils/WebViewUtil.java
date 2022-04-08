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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;

import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * webview页面工具类
 *
 * @since 2021-01-06
 */
public class WebViewUtil {
    /**
     * 关闭页面
     *
     * @param pageName 页面名称
     */
    public static void closePage(String pageName) {
        // 关闭打开的对应文件
        for (VirtualFile virtualFile : IDEFileEditorManager.getInstance().getOpenFiles()) {
            if (virtualFile.getCanonicalPath().endsWith(pageName)) {
                IDEFileEditorManager.getInstance().closeFile(virtualFile);
            }
        }
    }

    /**
     * 关闭当前页面
     *
     */
    public static void closeCurrentPage() {
        // 关闭打开的对应文件
        VirtualFile file = FileEditorManagerEx.getInstanceEx(CommonUtil.getDefaultProject()).getCurrentFile();
        IDEFileEditorManager.getInstance().closeFile(file);
    }
}
