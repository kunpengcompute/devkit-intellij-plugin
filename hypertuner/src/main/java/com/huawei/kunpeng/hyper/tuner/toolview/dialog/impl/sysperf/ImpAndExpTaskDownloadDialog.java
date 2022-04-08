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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.ImpAndExpTaskDownloadPanel;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 下载导入导出任务 弹窗
 *
 * @since 2021-5-18
 */
public class ImpAndExpTaskDownloadDialog extends IdeaDialog {
    /**
     * 主要内容展示面板
     */
    protected IDEBasePanel mainPanel;

    private Integer downloadTaskId;
    private Integer section;

    public ImpAndExpTaskDownloadDialog(
            String title, String dialogName, Integer downloadTaskId, Integer section, IDEBasePanel panel) {
        this.downloadTaskId = downloadTaskId;
        this.section = section;
        this.title = StringUtil.stringIsEmpty(title) ? ImpAndExpTaskContent.DELETE_ITEM : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? ImpAndExpTaskContent.DELETE_ITEM : dialogName;
        this.mainPanel = panel;
        setOKAndCancelName(ImpAndExpTaskContent.OPERATE_OK, ImpAndExpTaskContent.OPERATE_CANCEL);
        initDialog(); // 初始化弹框内容
    }

    /**
     * 获取mainPanel
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认操作
     */
    @Override
    protected void onOKAction() {
    }

    /**
     * 验证
     *
     * @return 是否通过验证
     */
    protected boolean okVerify() {
        if (mainPanel instanceof ImpAndExpTaskDownloadPanel) {
            ImpAndExpTaskDownloadPanel panel = (ImpAndExpTaskDownloadPanel) mainPanel;
            return panel.onOK();
        } else {
            return false;
        }
    }

    /**
     * 取消操作
     */
    @Override
    protected void onCancelAction() {
    }
}
