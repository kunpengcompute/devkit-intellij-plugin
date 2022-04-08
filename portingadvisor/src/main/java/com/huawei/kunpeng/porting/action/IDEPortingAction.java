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

package com.huawei.kunpeng.porting.action;

import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.LeftTreeTitleConstant;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingSourceEditor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 扫描菜单事件
 *
 * @since 2020-09-25
 */
public class IDEPortingAction extends AnAction {
    /**
     * 点击事件
     *
     * @param event 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 自动登录
        LoginUtils.autoLogin();

        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(getEventProject(event));
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(LeftTreeTitleConstant.SOURCE_CODE_PORTING))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            PortingSourceEditor.openPage();
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * change status
     *
     * @param anActionEvent 事件
     */
    @Override
    public void update(AnActionEvent anActionEvent) {
        int value = 0;
        anActionEvent.getPresentation().setEnabled(false);
        IDEPluginStatus valueFromGlobalContext = PortingIDEContext.getPortingIDEPluginStatus();
        value = (valueFromGlobalContext).value();

        /* 根据IDEPlugin status来更新菜单是否有效 */
        if (value >= IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            anActionEvent.getPresentation().setEnabled(true);
        }
    }
}
