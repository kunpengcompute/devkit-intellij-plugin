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

package com.huawei.kunpeng.porting.action.rightclick;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedFunctionPageEditor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

/**
 * 增强功能亲和检查
 *
 * @since 2.3.0
 */
public class PortingEnhancedAction extends PortingRightClickAction {
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setText(I18NServer.toLocale("plugins_porting_enhanced_right_click_check_title"));
    }

    /**
     * 打开webview页面
     *
     * @param file 选择文件
     */
    @Override
    public void openNewPageOrDialog(File file) {
        HashMap<String, String> params = new HashMap<>();
        params.put("filePath", file.getPath());
        params.put("fileName", file.getName());
        params.put("isSingle", "true");
        EnhancedFunctionPageEditor.openPageContainsParam(params);
    }
}
