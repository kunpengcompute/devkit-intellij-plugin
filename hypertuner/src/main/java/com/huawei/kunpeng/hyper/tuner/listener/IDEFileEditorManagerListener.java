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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.utils.NginxUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * 监听编辑器中文件关闭之前的事件状态
 *
 * @since 2021-08-04
 */
public class IDEFileEditorManagerListener implements FileEditorManagerListener.Before {
    @Override
    public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("file closing: " + file.getName());
        // 关闭登录页面的时候关闭nginx，leftPanel刷新为已配置未登录面
        if (file.getName().contains("HyperTuner")) {
            NginxUtil.stopNginx();
            TuningCommonUtil.refreshServerConfigSuccessPanel();
            TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
            // 清空本地 ip 缓存
//            ConfigUtils.fillIp2JsonFile(TuningIDEConstant.TOOL_NAME_TUNING, "", "","");
        }
    }
}