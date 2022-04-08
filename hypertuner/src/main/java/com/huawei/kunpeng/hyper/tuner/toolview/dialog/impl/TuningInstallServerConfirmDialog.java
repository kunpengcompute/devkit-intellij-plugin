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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl;

import com.huawei.kunpeng.hyper.tuner.action.serverconfig.TuningServerConfigAction;
import com.huawei.kunpeng.intellij.ui.dialog.InstallServerConfirmDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import java.util.HashMap;
import java.util.Map;

/**
 * 安装成功后登录弹框
 *
 * @since 2020/12/16
 */
public class TuningInstallServerConfirmDialog extends InstallServerConfirmDialog {
    public TuningInstallServerConfirmDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    /**
     * jumpLogin
     *
     * @param ip   ip
     * @param port port
     */
    public void jumpLogin(String ip, String port) {
        // 展示登录页面时再次判断服务器状态，防止这期间服务器的工具被卸载
        Map<String, String> params = new HashMap<>();
        params.put("ip", ip);
        params.put("port", port);
        params.put("certFile", null);
        params.put("useCertFlag", "false");
        TuningServerConfigAction.instance.save(params);
        TuningServerConfigAction.instance.notificationForHyperlinkAction();
    }
}
