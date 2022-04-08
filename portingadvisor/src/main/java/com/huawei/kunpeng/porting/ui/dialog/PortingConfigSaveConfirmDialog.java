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

package com.huawei.kunpeng.porting.ui.dialog;

import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.ConfigSaveConfirmDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.serverconfig.PortingServerConfigAction;
import com.huawei.kunpeng.porting.action.toolwindow.DeleteAllReportsAction;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import java.util.Map;

/**
 * 保存config确认弹窗
 *
 * @since 2012-12-05
 */
public class PortingConfigSaveConfirmDialog extends ConfigSaveConfirmDialog {
    public PortingConfigSaveConfirmDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    @Override
    protected void customizeOKAction(Map<String, String> params) {
        // 关闭打开的历史报告页面
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project openProject : openProjects) {
            DeleteAllReportsAction.closeAllOpenedReports(openProject);
        }
        // 左侧树面板加载loading，loadingText为系统默认
        UIUtils.changeToolWindowToLoadingPanel(CommonUtil.getDefaultProject(), null,
            PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (!PortingServerConfigAction.instance.save(params)) {
                // 左侧树面板刷新到配置服务器面板
                ApplicationManager.getApplication().invokeLater(LeftTreeUtil::refresh2ConfigPanel);
            }
        });
    }
}
