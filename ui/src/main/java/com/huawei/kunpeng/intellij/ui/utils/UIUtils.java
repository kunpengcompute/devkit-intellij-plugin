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

package com.huawei.kunpeng.intellij.ui.utils;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.RespondStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LeftTreeLoadingPanel;

import com.intellij.ide.actions.RevealFileAction;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * 弹框工具类
 *
 * @since 1.0.0
 */
public class UIUtils {
    /**
     * responseMessage
     *
     * @param responseStatus 响应状态
     * @param parentPanel 父面板
     * @param responseInfo 响应信息
     */
    public static void responseMessage(String responseStatus, IDEBasePanel parentPanel, ResponseBean responseInfo) {
        switch (RespondStatus.getStatusByValue(responseStatus)) {
            case PROCESS_STATUS_NORMAL:
                JOptionPane.showMessageDialog(parentPanel, CommonUtil.getRspTipInfo(responseInfo), "SUCCESS",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
            case PROCESS_STATUS_NOT_NORMAL:
                JOptionPane.showMessageDialog(parentPanel, CommonUtil.getRspTipInfo(responseInfo), "WARN",
                    JOptionPane.WARNING_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(parentPanel, CommonUtil.getRspTipInfo(responseInfo), "FAIL",
                    JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    /**
     * 弹出保存文本文件选择框并保存文件给出提示通知
     *
     * @param content 保存的内容
     * @param fileName 默认文件名
     * @param notification 通知信息
     */
    public static void saveTXTFileToLocalForDialog(String content, String fileName, NotificationBean notification) {
        Logger.info("saveFileToLocalForDialog start.");
        // 弹出弹框
        FileSaverDialog dialog = FileChooserFactory.getInstance()
            .createSaveFileDialog(new FileSaverDescriptor("Save File", "Select local file"),
                CommonUtil.getDefaultProject());
        VirtualFileWrapper fileWrapper = dialog.save(
                LocalFileSystem.getInstance().findFileByPath(CommonUtil.getDefaultProject().getBasePath()), fileName);
        if (fileWrapper == null) {
            return;
        }
        // 写文件到本地
        FileUtil.writeFile(content, fileWrapper.getFile().getPath(), fileWrapper.getFile());
        // 修改文件权限
        FileUtil.changeFoldersPermission600(fileWrapper.getFile());
        // 弹出通知信息
        if (notification == null) {
            return;
        }
        notification.setContent(notification.getContent().replace(fileName, fileWrapper.getFile().getPath()));
        IDENotificationUtil.notificationForHyperlink(notification, new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                RevealFileAction.openFile(fileWrapper.getFile());
            }
        });
        Logger.info("downloadReport successful:{}", fileName);
    }

    /**
     * toolWindow面板更换
     *
     * @param destPanel destPanelName
     * @param toolWindow toolWindow
     */
    public static void changeToolWindowToDestPanel(IDEBasePanel destPanel, ToolWindow toolWindow) {
        if (toolWindow == null) {
            return;
        }
        Content[] contents = toolWindow.getContentManager().getContents();
        for (Content cont : contents) {
            cont.dispose();
            toolWindow.getContentManager().removeContent(cont, false);
        }
        if (destPanel == null) {
            Logger.error("changeToolWindowToDestPanel destPanel is null.");
            return;
        }
        toolWindow.getContentManager().addContent(destPanel.getContent());
        toolWindow.getContentManager().setSelectedContent(destPanel.getContent());
    }

    /**
     * 左侧树面板更换为loading面板
     *
     * @param project 打开project
     * @param loadingText loading面板显示内容
     * @param toolWindowId 工具窗口id
     */
    public static void changeToolWindowToLoadingPanel(Project project, String loadingText, String toolWindowId) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
        if (toolWindow == null) {
            return;
        }
        IDEBasePanel destPanel;
        if (loadingText == null) {
            destPanel = new LeftTreeLoadingPanel(toolWindow, project);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put("loadingText", loadingText);
            destPanel = new LeftTreeLoadingPanel(toolWindow, param);
        }
        UIUtils.changeToolWindowToDestPanel(destPanel, toolWindow);
    }
}
