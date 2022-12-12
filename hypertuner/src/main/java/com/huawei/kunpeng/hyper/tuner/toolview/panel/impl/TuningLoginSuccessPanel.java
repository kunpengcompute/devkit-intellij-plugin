package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl;

import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PanelType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.ButtonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.util.Map;

/**
 * 左侧树登录成功面板
 *
 * @since 2022-11-15
 */
public class TuningLoginSuccessPanel extends IDEBasePanel {
    // 滚动条面板
    private JScrollPane scrollPanel;

    private JPanel mainPanel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel ipInfoLabel;
    private JLabel portInfoLabel;
    private JButton unableLoginButton;
    private JPanel contentPanel;

    private Project project;

    // 已配置服务器的ip和端口
    private String ip;
    private String port;

    public TuningLoginSuccessPanel(ToolWindow toolWindow, String panelName, Project project) {
        this.project = project;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? PanelType.TUNING_LOGIN_SUCCESS.panelName() : panelName;
        initPanel();
        createContent(mainPanel, null, false);
    }

    /**
     * 反射调用的构造函数
     *
     * @param toolWindow 工具窗口
     * @param project    当前的项目
     */
    public TuningLoginSuccessPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project);
    }

    private void initPanel() {
        // 取消滚动条面板的边框
        scrollPanel.setBorder(null);
        Map<String, String> serverConfig = CommonUtil.readCurIpAndPortFromConfig();
        ip = serverConfig.get("ip");
        port = serverConfig.get("port");
        ipInfoLabel.setText(ip);
        portInfoLabel.setText(port);
        ipLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_ip_address"));
        portLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_port"));
        ButtonUtil.setCommonButtonStyle(unableLoginButton);
        unableLoginButton.setOpaque(false);
        unableLoginButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_login_button"));
        unableLoginButton.setEnabled(false);
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}
