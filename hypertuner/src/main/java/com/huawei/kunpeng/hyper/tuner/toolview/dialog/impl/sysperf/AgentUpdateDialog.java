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

import com.huawei.kunpeng.hyper.tuner.action.sysperf.AgentCertAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.FingerprintsBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.AgentUpdatePanel;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

/**
 * Agent 服务证书更新弹窗
 *
 * @since 2021-6-16
 */
public class AgentUpdateDialog extends IdeaDialog implements ActionOperate {
    private AgentCertAction agentCertAction;
    private ActionOperate actionOperate = this;
    /**
     * 操作类型 初始化传值
     */
    private final String operateType;

    /**
     * 构造函数
     *
     * @param title       标题
     * @param dialogName  弹窗标题
     * @param operateType 操作类型：更换证书/更换工作密钥
     * @param panel       主面板
     */
    public AgentUpdateDialog(String title, String dialogName, String operateType, IDEBasePanel panel) {
        this.title = StringUtil.stringIsEmpty(title) ? "title" : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? "dialogName" : dialogName;
        this.mainPanel = panel;
        this.operateType = operateType;
        if (agentCertAction == null) {
            agentCertAction = new AgentCertAction();
        }
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        super.initDialog();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void onOKAction() {
        if (mainPanel instanceof AgentUpdatePanel) {
            AgentUpdatePanel updatePanel = (AgentUpdatePanel) mainPanel;
            Map<String, String> paramMap = updatePanel.getParams();
            List<FingerprintsBean> fingerprintsBeans = agentCertAction.fingerPrintList(paramMap.get("ip"));
            AgentTipsDialog dialog =
                    new AgentTipsDialog(
                            NodeManagerContent.NODE_MANAGER_FINGERPRINTS,
                            updatePanel,
                            paramMap,
                            fingerprintsBeans,
                            actionOperate,
                            operateType);
            dialog.displayPanel();
        }
    }

    @Override
    protected void onCancelAction() {
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    protected ValidationInfo doValidate() {
        return this.mainPanel.doValidate();
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    protected @NotNull List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();
        if (mainPanel instanceof AgentUpdatePanel) {
            AgentUpdatePanel updatePanel = (AgentUpdatePanel) mainPanel;
            result = updatePanel.doValidateAll();
            if (ValidateUtils.isNotEmptyCollection(result)) {
                this.okAction.setEnabled(false);
            } else {
                this.okAction.setEnabled(true);
            }
        }
        return result;
    }

    @Override
    public void actionOperate(Object data) {
        if (data instanceof Boolean) {
            okAction.setEnabled((boolean) data);
        }
    }
}
