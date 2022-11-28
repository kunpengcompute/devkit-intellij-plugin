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

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_ICON;
import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_OPERATE_CLOSE;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.InstallConstant;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 版本不兼容 提示弹窗
 *
 * @since 2021-5-31
 */
public class CompatibilityDialog extends IdeaDialog {

    private String tipContent;

    /**
     * 带位置信息的构造函数
     *
     * @param title      弹窗标题
     * @param tipContent 提示文本
     */
    public CompatibilityDialog(String title, String tipContent) {

        this.title = ValidateUtils.isEmptyString(title) ? InstallConstant.BEFORE_INSTALL : title;
        this.dialogName =
                ValidateUtils.isEmptyString(dialogName) ? Dialogs.INSTALL_DISCLAIMER.dialogName() : dialogName;

        this.tipContent = tipContent;
        // 无位置信息时居中显示
        setCancelButtonText(TERM_OPERATE_CLOSE);
        // 初始化弹框内容
        initDialog();
        okAction.setEnabled(true);
        setResizable(false);
        setSize(720, 180);
    }

    /**
     * 版本不兼容 提示弹窗
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JLabel headerLabel = new JLabel();
        headerLabel.setText(tipContent);
        headerLabel.setIcon(ICON_INFO_ICON);
        // 水平方向文本在图片右边
        headerLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        // 垂直方向文本在图片中心
        headerLabel.setVerticalTextPosition(SwingConstants.TOP);
//        headerLabel.setPreferredSize(new Dimension(500, 230));
        // 主面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(500, 100));
        centerPanel.setMaximumSize(new Dimension(500, 100));
        centerPanel.add(headerLabel, BorderLayout.CENTER);
        return centerPanel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{getCancelAction()};
    }

    @Override
    protected void onOKAction() {

    }


    /**
     * 取消按钮事件
     */
    @Override
    protected void onCancelAction() {
        TuningCommonUtil.refreshServerConfigPanel();
        IDENotificationUtil.notificationCommon(new NotificationBean("",
                TuningI18NServer.toLocale("plugins_hyper_tuner_config_closure"), NotificationType.WARNING));
        ApplicationManager.getApplication().invokeLater(TuningCommonUtil::refreshServerConfigPanel);
        // 清空本地 ip 缓存
//        ConfigUtils.fillIp2JsonFile(TuningIDEConstant.TOOL_NAME_TUNING, "", "", "", "");
    }
}
