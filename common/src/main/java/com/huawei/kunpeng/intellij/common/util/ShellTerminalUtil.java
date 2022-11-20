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

package com.huawei.kunpeng.intellij.common.util;

import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalView;
import org.jetbrains.plugins.terminal.arrangement.TerminalWorkingDirectoryManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * The Class ShellTerminalUtil.
 *
 * @since 2.2.T3
 */
public class ShellTerminalUtil {
    /**
     * 打开终端
     *
     * @param param 部署配置信息参数
     * @param exec  命令
     */
    public static void openTerminal(Map param, String exec) {
        Project project = CommonUtil.getDefaultProject();
        TerminalView terminalView = TerminalView.getInstance(project);
        ToolWindow terminal = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (terminal == null) {
            return;
        }
        String workingDirectory = "~";
        if (ValidateUtils.isEmptyMap(param)) {
            return;
        }
        String tabName = param.get("displayName").toString();
        String command = "ssh -t -p " + param.get("port") + " " + param.get("user") + "@" +
                param.get("ip") + exec;
        executeCommand(terminalView, terminal, workingDirectory, tabName, command);
    }

    /**
     * 检查终端状态
     *
     * @param tabName tab名称
     */
    public static boolean checkTerminal(String tabName) {
        Project project = CommonUtil.getDefaultProject();
        ToolWindow terminal = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (terminal == null) {
            return false;
        }
        ContentManager contentManager = terminal.getContentManager();
        Content selectedContent = contentManager.getSelectedContent();
        if (selectedContent != null && Objects.equals(selectedContent.getTabName(), tabName)) {
            JBTerminalWidget widget = TerminalView.getWidgetByContent(selectedContent);
            if (!(widget instanceof ShellTerminalWidget)) {
                return false;
            }
            ShellTerminalWidget shellTerminalWidget = (ShellTerminalWidget) widget;
            return shellTerminalWidget.hasRunningCommands();
        }
        return true;
    }

    private static void executeCommand(TerminalView terminalView, ToolWindow terminal,
                                        String workingDirectory, String tabName, String command) {
        ContentManager contentManager = terminal.getContentManager();
        Pair<Content, ShellTerminalWidget> pair = getSuitableProcess(contentManager, workingDirectory);
        try {
            if (pair == null) {
                terminalView.createLocalShellWidget(workingDirectory, tabName).executeCommand(command);
                return;
            }
            terminal.activate(null);
            contentManager.setSelectedContent(pair.first);
            pair.second.executeCommand(command);
        } catch (IOException e) {
            Logger.info("Cannot run command, it is IOException");
        }
    }

    /**
     * 打开终端
     *
     * @param path 部署配置信息参数
     */
    public static void openInstallCaTerminal(String path) {
        Project project = CommonUtil.getDefaultProject();
        TerminalView terminalView = TerminalView.getInstance(project);
        ToolWindow terminal = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (terminal == null) {
            return;
        }
        String workingDirectory = "~";
        String tabName = I18NServer.toLocale("plugins_hyper_tuner_javaperf_import_caCret");
        String command = "rundll32.exe cryptext.dll,CryptExtAddCER " + path;
        executeCommand(terminalView, terminal, workingDirectory, tabName, command);
    }

    @Nullable
    private static Pair<Content, ShellTerminalWidget> getSuitableProcess(@NotNull ContentManager contentManager,
        @NotNull String workingDirectory) {
        Content selectedContent = contentManager.getSelectedContent();
        if (selectedContent != null) {
            Pair<Content, ShellTerminalWidget> pair = getSuitableProcess(selectedContent, workingDirectory);
            if (pair != null) {
                return pair;
            }
        }

        return Arrays.stream(contentManager.getContents())
                .map(content -> getSuitableProcess(content, workingDirectory))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    private static Pair<Content, ShellTerminalWidget> getSuitableProcess(@NotNull Content content,
        @NotNull String workingDirectory) {
        JBTerminalWidget widget = TerminalView.getWidgetByContent(content);
        if (!(widget instanceof ShellTerminalWidget)) {
            return null;
        }
        ShellTerminalWidget shellTerminalWidget = (ShellTerminalWidget) widget;
        if (!shellTerminalWidget.getTypedShellCommand().isEmpty() || shellTerminalWidget.hasRunningCommands()) {
            return null;
        }
        String currentWorkingDirectory = TerminalWorkingDirectoryManager.getWorkingDirectory(shellTerminalWidget, null);
        if (currentWorkingDirectory == null || !currentWorkingDirectory.equals(workingDirectory)) {
            return null;
        }
        return Pair.create(content, shellTerminalWidget);
    }
}
