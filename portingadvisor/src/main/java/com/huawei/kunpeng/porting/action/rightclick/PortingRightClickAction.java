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

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

/**
 * 右键分析抽象类
 *
 * @since 2.3.0
 */
public abstract class PortingRightClickAction extends AnAction {
    private static final long MAX_FILE_SIZE = IDEConstant.MAX_FILE_SIZE << 20;

    /**
     * 右键事件
     *
     * @param anActionEvent 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 1. 先判断是否配置服务器 2. 若已配置则再判断是否登录
        if (PortingIDEContext.checkServerConfig()) {
            PortingIDEContext.checkLoginForDialog();
        }
        // 登录框弹出之后用户可能取消登录，需要在checkAndHandleFile方法中判断一下是否成功登录
        ApplicationManager.getApplication().invokeLater(() -> checkAndHandleFile(anActionEvent));
    }

    private void checkAndHandleFile(AnActionEvent anActionEvent) {
        // 操作之前先判断一下是否登录成功
        if (!PortingIDEContext.checkPortingLogin()) {
            return;
        }
        Optional<File> optionalFile = checkAndGetSelectFile(anActionEvent);
        // 开始处理源码文件
        optionalFile.ifPresent(this::openNewPageOrDialog);
    }

    /**
     * 源码迁移分析打开dialog，增强功能（亲和扫描）打开webview
     *
     * @param file 选择文件
     */
    abstract void openNewPageOrDialog(File file);

    private Optional<File> checkAndGetSelectFile(AnActionEvent anActionEvent) {
        VirtualFile[] files = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        // 不支持同时选中多个文件夹
        if (files == null || files.length > 1) {
            errorNotification(I18NServer.toLocale("plugins_porting_tip_mul_file_warn"), anActionEvent.getProject());
            return Optional.empty();
        }

        File file = new File(getPaths(files));

        // 暂时不支持单文件扫描
        if (file.isFile()) {
            errorNotification(I18NServer.toLocale("plugins_porting_tip_file_warn"), anActionEvent.getProject());
            return Optional.empty();
        }
        // 提示：文件或文件夹名中不能包含中文、空格以及^ ` / | ; & $ > < \ ! 等特殊字符，请修改后重试
        if (FileUtil.isContainChinese(file.getName()) || !FileUtil.validateFileName(file.getName())) {
            errorNotification(I18NServer.toLocale("plugins_port_file_name_illegal_tip"),
                    anActionEvent.getProject());
            return Optional.empty();
        }
        // 文件大小超过1G给出提示
        if (FileUtil.getTotalSizeOfDirectory(file, MAX_FILE_SIZE) > MAX_FILE_SIZE) {
            errorNotification(I18NServer.toLocale("plugins_porting_message_fileExceedMaxSize"),
                    anActionEvent.getProject());
            return Optional.empty();
        }
        return Optional.of(file);
    }

    private void errorNotification(String message, Project project) {
        NotificationBean notificationBean = new NotificationBean(
                I18NServer.toLocale("plugins_common_porting_settings"), message, NotificationType.ERROR);
        notificationBean.setProject(project);
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    /**
     * 获取选中的文件全路径
     *
     * @param files files
     * @return String
     */
    private static String getPaths(VirtualFile[] files) {
        StringBuilder buf = new StringBuilder(files.length * 64);
        for (VirtualFile file : files) {
            if (buf.length() > 0) {
                buf.append(PortingIDEConstant.LINE_SEPARATOR);
            }
            buf.append(file.getPresentableUrl());
        }
        return buf.toString();
    }
}
