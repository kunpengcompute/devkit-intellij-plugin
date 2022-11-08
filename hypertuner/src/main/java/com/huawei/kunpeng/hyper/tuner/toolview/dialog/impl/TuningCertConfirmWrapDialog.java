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
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.CertConfirmWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

/**
 * Intellij 类型弹框
 *
 * @since 2020-09-25
 */
public class TuningCertConfirmWrapDialog extends CertConfirmWrapDialog {
    private final String toolName;

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public TuningCertConfirmWrapDialog(String title, IDEBasePanel panel, String toolName) {
        super(title, null, panel, false);
        this.toolName = toolName;
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        // 提示语添加登录跳转
        TuningServerConfigAction.instance.showNotification();
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
        // 将plugin设置为初始状态
        IDEContext.setIDEPluginStatus(toolName, IDEPluginStatus.IDE_STATUS_INIT);
        // 清空本地 ip 缓存
//        ConfigUtils.fillIp2JsonFile(toolName, "", "", "","");
    }
}
