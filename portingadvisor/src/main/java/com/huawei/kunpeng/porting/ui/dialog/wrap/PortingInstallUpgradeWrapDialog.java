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

package com.huawei.kunpeng.porting.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.InstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.InstallUpgradePanel;
import com.huawei.kunpeng.porting.action.install.InstallAction;
import com.huawei.kunpeng.porting.action.upgrade.UpgradeAction;

import java.util.Map;

/**
 * 升级更新弹框
 *
 * @since 2021-04-10
 */
public class PortingInstallUpgradeWrapDialog extends InstallUpgradeWrapDialog {
    public PortingInstallUpgradeWrapDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    /**
     * 点击next前的校验事件
     *
     * @return boolean
     */
    @Override
    protected boolean nextVerify() {
        Logger.info("InstallUpgradeWrapDialog, nextVerify");
        // 检测连接
        if (mainPanel.getAction() instanceof InstallAction && mainPanel instanceof InstallUpgradePanel) {
            InstallUpgradePanel installUpgradePanel = (InstallUpgradePanel) mainPanel;
            InstallAction action = (InstallAction) mainPanel.getAction();
            action.onNextAction(installUpgradePanel.getParams(), this::actionOperate);
        }
        // 返回false，不关闭弹框
        return false;
    }

    /**
     * 点击确认事件
     */
    @Override
    protected void onOKAction() {
        if (mainPanel instanceof InstallUpgradePanel) {
            InstallUpgradePanel installUpgradePanel = (InstallUpgradePanel) mainPanel;
            // 保存ssh连接配置的ip和端口信息
            Map<String, Object> map = installUpgradePanel.getParams();
            Object object = map.get("param");
            if (object instanceof Map) {
                params = (Map) object;
            } else {
                Logger.error("Can not get the params.");
            }
            if (installUpgradePanel.isUpgrade()) {
                UpgradeAction action = new UpgradeAction();
                Logger.info("Upgrade begin...");
                action.onOKAction(installUpgradePanel.getParams());
            } else {
                IDEPanelBaseAction baseAction = mainPanel.getAction();
                if (baseAction instanceof InstallAction) {
                    InstallAction action = (InstallAction) baseAction;
                    Logger.info("Install begin...");
                    action.onOKAction(installUpgradePanel.getParams());
                }
            }
        }
        // 弹框消失则将两个组件缓存置空
        checkButton = null;
        gifLabel = null;
        mainPanel.clearPwd();
    }
}
