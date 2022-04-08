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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IDEComponentManager;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;

import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 安装账户判断提示的弹窗
 *
 * @since 2020-12-21
 */
public class NodeInstallLogDialog extends IdeaDialog {
    private static final String LOG_NAME = "/log.txt";

    private String nodeId;
    private String nodeIp;

    private String logContent;

    private Project project;

    private String messages;

    // 获得系统剪贴板
    private Clipboard clipbd = Toolkit.getDefaultToolkit().getSystemClipboard();

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param nodeId 弹框名称
     * @param nodeIp nodeIp
     */
    public NodeInstallLogDialog(String nodeId, String nodeIp, IDEBasePanel panel) {
        this.project = CommonUtil.getDefaultProject();
        this.nodeId = nodeId;
        this.nodeIp = nodeIp;
        this.title = this.nodeIp + " : " + NodeManagerContent.NODE_INSTALL_DIALOG;
        this.dialogName = NodeManagerContent.NODE_INSTALL_LOG;
        // 无位置信息时居中显示
        this.rectangle = new Rectangle(600, 360);
        setOKAndCancelAndNextName(
                NodeManagerContent.NODE_INSTALL_DIADOWN,
                NodeManagerContent.NODE_INSTALL_DIACANCEL,
                NodeManagerContent.NODE_INSTALL_DIADUP);

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 安装部署内容声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        this.messages = getLogContent();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(600, 360));
        JBScrollPane jbScrollPane = new JBScrollPane(panelWithHtmlListener(messages));
        jbScrollPane.setMaximumSize(new Dimension(579, 236));
        jbScrollPane.setBorder(null); // 设置无边框。
        // 滚动条总是垂直的。
        jbScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jbScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(jbScrollPane, BorderLayout.CENTER);
        return centerPanel;
    }

    /**
     * 下載
     */
    @Override
    protected void onOKAction() {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(NodeManagerContent.NODE_INSTALL_DIADOWN);
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            // 文件存在且选择不继续
            if (CommonTableUtil.isExistNotToContinue(path + LOG_NAME, NodeManagerContent.NODE_INSTALL_DIADOWN)) {
                return;
            }
            FileUtil.saveAsFileWriter(this.messages, path + LOG_NAME);
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            TuningI18NServer.toLocale("plugins_hyper_tuner_node_install_dialog"),
                            TuningI18NServer.toLocale("plugins_hyper_tuner_node_install_dialog")
                                    + " "
                                    + TuningI18NServer.toLocale("plugins_hyper_tuner_download_report_success")
                                    + path
                                    + LOG_NAME,
                            NotificationType.INFORMATION));
        }
    }

    /**
     * 安装部署外部超链接
     *
     * @param msg 链接内容
     * @return jPanel 主面板
     */
    private static JPanel panelWithHtmlListener(String msg) {
        JEditorPane jEditorPane = new JEditorPane("html", msg);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);
        jEditorPane.setCaretPosition(0);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout(5, 5));
        jPanel.add(jEditorPane, BorderLayout.CENTER);
        return jPanel;
    }

    /**
     * 复制
     */
    @Override
    protected void onCancelAction() {
        StringSelection clipString = new StringSelection(messages);
        clipbd.setContents(clipString, clipString);
        IDENotificationUtil.notificationCommon(
                new NotificationBean(
                        TuningI18NServer.toLocale("plugins_hyper_tuner_node_install_dialog"),
                        TuningI18NServer.toLocale("plugins_hyper_tuner_node_install_dialog")
                                + " "
                                + TuningI18NServer.toLocale("plugins_hyper_tuner_node_install_diaDup_sucess"),
                        NotificationType.INFORMATION));
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onNextAction() {
    }

    /**
     * 打开弹框
     */
    public void displayPanel() {
        IDEComponentManager.instance.addViableDialog(dialogName, this);
        closed = false;
        showAndGet();
        switch (getExitCode()) {
            case OK_EXIT_CODE:
                closed = true;
                if (isNeedInvokeLaterPanel()) {
                    ApplicationManager.getApplication()
                            .invokeLater(
                                    () -> {
                                        onOKAction();
                                    });
                } else {
                    onOKAction();
                }
                break;
            case CANCEL_EXIT_CODE:
                closed = true;
                break;
            case NEXT_USER_EXIT_CODE:
                onCancelAction();
                break;
            default:
                closed = true;
                break;
        }
    }

    /**
     * 获取日志内容
     *
     * @return 日志内容
     */
    public String getLogContent() {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/nodes/" + this.nodeId + "/logs/",
                        HttpMethod.GET.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (jsonMessage.get("log_msg") instanceof String) {
            logContent = (String) jsonMessage.get("log_msg");
        }
        logContent =
                logContent
                        .replace("[0m", "")
                        .replace("\u001B", "")
                        .replace("[1;31m", "")
                        .replace("[1;32m", "")
                        .replace("[1;33m", "")
                        .replace("[1;34m", "");
        return logContent;
    }
}
