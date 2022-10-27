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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.NginxUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 监听编辑器中文件关闭之前的事件状态
 *
 * @since 2021-08-04
 */
public class IDEFileEditorManagerListener implements FileEditorManagerListener.Before {
    // 关闭HyperTuner页面时关闭nginx服务
    @Override
    public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file.getName().contains("HyperTuner")) {
//            String pluginPath = CommonUtil.getPluginInstalledPath() + NginxUtil.STOP_NGINX_BAT;
//            File stopNginxBatFile = new File(pluginPath);
//            if (stopNginxBatFile.exists()) {
//                NginxUtil.stopNginx();
//            } else {
//                Logger.info("file does not exist");
//            }
            NginxUtil.stopNginx();
            TuningCommonUtil.refreshServerConfigPanel();
            IDENotificationUtil.notificationCommon(new NotificationBean("",
                    TuningI18NServer.toLocale("plugins_hyper_tuner_config_closure"), NotificationType.WARNING));
            // 清空本地 ip 缓存
            ConfigUtils.fillIp2JsonFile(TuningIDEConstant.TOOL_NAME_TUNING, "", "","", "");
        }
    }
}