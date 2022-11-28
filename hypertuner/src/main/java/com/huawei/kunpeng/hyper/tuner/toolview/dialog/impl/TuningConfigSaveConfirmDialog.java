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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.ConfigSaveConfirmDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;

import java.util.Map;

/**
 * 保存config确认弹窗
 *
 * @since 2012-12-05
 */
public class TuningConfigSaveConfirmDialog extends ConfigSaveConfirmDialog {
    public TuningConfigSaveConfirmDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    @Override
    protected void customizeOKAction(Map<String, String> params) {
        // 左侧树面板加载loading，loadingText为系统默认
        UIUtils.changeToolWindowToLoadingPanel(
                CommonUtil.getDefaultProject(), null, TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
        TuningCommonUtil.refreshServerConfigPanel();
        mouseClickedDisplayPanel();
    }

    private void mouseClickedDisplayPanel() {
//        IDEBasePanel panel = new TuningServerConfigPanel(null);
//        IDEBaseDialog dialog = new TuningServerConfigWrapDialog(TuningUserManageConstant.CONFIG_TITLE, panel);
//        AbstractWebFileProvider.closeAllWebViewPage();
//        dialog.displayPanel();
    }
}
