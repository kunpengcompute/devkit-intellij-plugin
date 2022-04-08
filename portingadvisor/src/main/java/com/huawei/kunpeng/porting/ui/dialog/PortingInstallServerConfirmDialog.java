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

package com.huawei.kunpeng.porting.ui.dialog;

import com.huawei.kunpeng.intellij.ui.dialog.InstallServerConfirmDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.serverconfig.PortingServerConfigAction;

import java.util.HashMap;
import java.util.Map;

/**
 * PortingInstallServerConfirmDialog
 *
 * @since 2012-12-05
 */
public class PortingInstallServerConfirmDialog extends InstallServerConfirmDialog {
    /**
     * PortingInstallServerConfirmDialog
     */
    public PortingInstallServerConfirmDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    /**
     * jumpLogin
     *
     * @param ip   ip
     * @param port port
     */
    @Override
    public void jumpLogin(String ip, String port) {
        // 展示登录页面时再次判断服务器状态，防止这期间服务器的工具被卸载
        Map<String, String> params = new HashMap<>();
        params.put("ip", ip);
        params.put("port", port);
        params.put("certFile", null);
        params.put("useCertFlag", "false");
        PortingServerConfigAction.instance.save(params);
        PortingServerConfigAction.instance.notificationForHyperlinkAction();
    }
}
