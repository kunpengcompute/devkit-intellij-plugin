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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.action.panel.javaperf.WorkingKeyAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.IconLoader;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Java性能分析-工作密钥
 *
 * @since 2021-07-09
 */
public class WorkingKeyPanel extends IDEBasePanel {
    private static final String MESSAGE_SUCESS = JavaProviderSettingConstant.UPDATE_SUCESS;

    private static final String MESSAGE_FAILD = JavaProviderSettingConstant.UPDATE_FAILD;
    private JPanel mainPanel; // 主面板
    /**
     * 配置类
     */
    private WorkingKeyAction workingKeyAction = new WorkingKeyAction();

    public WorkingKeyPanel() {
        // 初始化数据
        mainPanel = new JPanel();
        initPanel(mainPanel);
    }

    /**
     * 初始化面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel panel1 = new JPanel();
        JButton button = new JButton(JavaProviderSettingConstant.REFRESH_WORKING_KEY);
        button.setBounds(10, 10, 200, 30);
        panel1.add(button);
        JLabel label = new JLabel(TuningI18NServer.toLocale("plugins_hyper_tuner_javaPerf_refresh_success"));
        label.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/status_point@1x.svg"));
        label.setVisible(false);
        JLabel label2 = new JLabel(TuningI18NServer.toLocale("plugins_hyper_tuner_javaPerf_refresh_doing"));
        label2.setIcon(BaseIntellijIcons.load("/assets/img/javaperf/online/analysing.svg"));
        label2.setVisible(false);
        JPanel panel2 = new JPanel();
        panel2.add(label);
        panel2.add(label2);
        mainPanel.add(panel1);
        mainPanel.add(panel2);
        button.setBorderPainted(false);
        // 添加按钮的点击事件监听器
        button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 获取到的事件源就是按钮本身
                        label.setVisible(false);
                        label2.setVisible(true);
                        Map<String, Object> jsonMessage = workingKeyAction.getRefreshStatus();
                        String result = null;
                        if (jsonMessage.get("code") instanceof String) {
                            result = (String) jsonMessage.get("code");
                        }
                        AtomicBoolean succeeded = new AtomicBoolean(false);
                        ApplicationManager.getApplication()
                                .invokeLater(
                                        () -> {
                                            label2.setVisible(false);
                                            label.setVisible(true);
                                        });
                        if (result == "0") {
                            notifyInfo(
                                    JavaProviderSettingConstant.WORKING_KEY,
                                    MESSAGE_SUCESS,
                                    NotificationType.INFORMATION);
                            succeeded.set(true);
                        } else {
                            notifyInfo(
                                    JavaProviderSettingConstant.WORKING_KEY, MESSAGE_FAILD, NotificationType.WARNING);
                        }
                    }
                });
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 是否添加apply
     *
     * @return 结果
     */
    public boolean isModified() {
        return false;
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 应用
     */
    public void apply() {
    }

    private void notifyInfo(String name, String value, NotificationType type) {
        NotificationBean notificationBean = new NotificationBean(name, value, type);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }
}
