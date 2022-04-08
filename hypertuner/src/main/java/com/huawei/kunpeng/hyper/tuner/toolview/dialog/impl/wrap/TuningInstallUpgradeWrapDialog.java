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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap;

import com.huawei.kunpeng.hyper.tuner.action.install.TuningInstallAction;
import com.huawei.kunpeng.hyper.tuner.action.upgrade.TuningUpgradeAction;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.InstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.InstallUpgradePanel;

import java.util.Map;

/**
 * 安装升级弹框
 *
 * @since 2020-09-25
 */
public class TuningInstallUpgradeWrapDialog extends InstallUpgradeWrapDialog {
    public TuningInstallUpgradeWrapDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    /**
     * 点击next前的校验事件
     *
     * @return boolean
     */
    protected boolean nextVerify() {
        Logger.info("InstallUpgradeWrapDialog, nextVerify");
        // 检测连接
        if (mainPanel.getAction() instanceof TuningInstallAction && mainPanel instanceof InstallUpgradePanel) {
            InstallUpgradePanel installUpgradePanel = (InstallUpgradePanel) mainPanel;
            TuningInstallAction action = (TuningInstallAction) mainPanel.getAction();
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
            Map<String, Object> mapObject = installUpgradePanel.getParams();
            Object object = mapObject.get("param");
            if (object instanceof Map) {
                params = (Map) object;
            } else {
                Logger.error("Can not get the params.");
            }
            if (installUpgradePanel.isUpgrade()) {
                TuningUpgradeAction action = new TuningUpgradeAction();
                Logger.info("Upgrade begin...");
                action.onOKAction(installUpgradePanel.getParams());
            } else {
                IDEPanelBaseAction baseAction = mainPanel.getAction();
                if (baseAction instanceof TuningInstallAction) {
                    TuningInstallAction action = (TuningInstallAction) baseAction;
                    Logger.info("Install begin...");
                    action.onOKAction(installUpgradePanel.getParams());
                }
            }
        }
        // 弹框消失则将两个组件缓存置空
        gifLabel = null;
        checkButton = null;
        mainPanel.clearPwd();
    }
}
