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

package com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 源码扫描Webview页面
 *
 * @since 2021-01-05
 */
public class UpdataTaskWebView extends WebView {
    /**
     * 默认构造函数
     */
    public UpdataTaskWebView() {
        // 组装与webview交互的message
        Map<String, Object> paramMaps = new HashMap<>();
        String pageName = SysperfContent.MODIFY_TASK + '-' +
                LeftTreeSysperfPanel.getSelectProject().getProjectName() + '-' +
                LeftTreeSysperfPanel.getSelectTask().getTaskname();
        paramMaps.put("pageName", pageName);
        paramMaps.put("response", "###");
        paramMaps.put("intelliJFlag", true);
        paramMaps.put("isLoginRouter", true);
        paramMaps.put("projectId", LeftTreeSysperfPanel.getSelectProject().getProjectId());
        paramMaps.put("projectName", LeftTreeSysperfPanel.getSelectProject().getProjectName());
        paramMaps.put("operation", "modifyTask");
        paramMaps.put("taskId", LeftTreeSysperfPanel.getSelectTask().getId());
        paramMaps.put("taskName", LeftTreeSysperfPanel.getSelectTask().getTaskname());
        paramMaps.put("analysisType", LeftTreeSysperfPanel.getSelectTask().getAnalysistype());
        String panelId = LeftTreeSysperfPanel.getSelectProject().getProjectName() + "-"
                + LeftTreeSysperfPanel.getSelectTask().getTaskname() + "-" + new Date().getTime();
        paramMaps.put("panelId", panelId);
        // 系统信息
        if (IDEContext.getValueFromGlobalContext(null,
                BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
            paramMaps.put("OS", "windows");
        } else {
            paramMaps.put("OS", "linux");
        }
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("queryParams", paramMaps);
        NavigatorPageBean navigatorPage = MessageRouterHandler.generateNavigatorPage("/navigate",
                "/home", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "UpdataTaskWebView" + LeftTreeSysperfPanel.getSelectTask().getId());
    }
}
