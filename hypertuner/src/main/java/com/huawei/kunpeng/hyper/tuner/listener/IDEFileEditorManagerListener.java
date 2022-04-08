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

package com.huawei.kunpeng.hyper.tuner.listener;

import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

/**
 * 监听编辑器中文件关闭之前的事件状态
 *
 * @date 2021/8/4 16:08
 * @since 2021/8/4
 */
public class IDEFileEditorManagerListener implements FileEditorManagerListener.Before {
    @Override
    public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file.getName().endsWith("_pro.kpht") || file.getName().contains("-profiling")) {
            JavaPerfToolWindowPanel.refreshProfilingNode(file.getName(), false);
            if (UserInfoContext.getInstance().getUserName() != null) {
                IDENotificationUtil.notificationCommon(new NotificationBean("",
                        JavaProviderSettingConstant.PROFILING_LIMIT, NotificationType.WARNING));
            }
        }
    }
}