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

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.toolwindow.DeleteAllReportsAction;
import com.huawei.kunpeng.porting.common.CacheDataOpt;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeLoginPanel;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * Intellij 类型弹框
 *
 * @since 2020-09-25
 */
public class LogoutWrapDialog extends IdeaDialog {
    /**
     * 登录成功
     */
    public static final int LOGIN_OK = 0;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public LogoutWrapDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? I18NServer.toLocale("plugins_porting_login_logOut") : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.LOGIN.dialogName() : dialogName;
        this.mainPanel = panel;

        // 设置弹框大小是否可变
        this.resizable = resizable;

        // 设置弹框中确认取消按钮的名称
        setOKAndCancelName(I18NServer.toLocale("plugins_common_button_confirm"),
            I18NServer.toLocale("plugins_common_button_cancel"));

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public LogoutWrapDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/logout/",
            HttpMethod.POST.vaLue(), "");

        // 调用登出接口
        ResponseBean rsp = PortingHttpsServer.INSTANCE.requestData(message);
        if (rsp == null) {
            return;
        }
        // 清除所有状态
        PortingUserInfoContext.clearStatus();

        // update global IDEPluginStatus
        PortingIDEContext.setPortingIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        CacheDataOpt.clearUserFiles();
        IDENotificationUtil.notifyCommonForResponse("", rsp.getStatus(), rsp);
        // 对每个project更新左侧树
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // 重新配置后关闭打开的历史报告页面
            DeleteAllReportsAction.closeAllOpenedReports(CommonUtil.getDefaultProject());
            // 用户退出后更新左侧树
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
        }
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
    }

    /**
     * 创建面板内容
     *
     * @return JComponent
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}
