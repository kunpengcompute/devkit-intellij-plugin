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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.listener.IDEThemeListener;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.NodeList;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.Tasklist;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 源码扫描Webview页面
 *
 * @since 2021-01-05
 */
public class ShowNodeWebView extends WebView {
    /**
     * 默认构造函数
     */
    public ShowNodeWebView() {
        // 组装与webview交互的message
        Tasklist selectTask = LeftTreeSysperfPanel.getSelectTask();
        NodeList node = LeftTreeSysperfPanel.getSelectNode();
        if (LeftTreeSysperfPanel.isCreateTask) {
            selectTask = LeftTreeSysperfPanel.getNewTask();
            node = LeftTreeSysperfPanel.getNewNode();
        }
        node.getTaskParam().setTaskname(selectTask.getTaskname());
        node.getTaskParam().setAnalysistarget(selectTask.getAnalysistarget());
        node.getTaskParam().setAnalysistype(selectTask.getAnalysistype());
        node.setLinkageTask(false);
        node.setTaskId(selectTask.getId());
        String selfInfo = JSON.toJSONString(node)
                .replaceAll(":", "#");
        selfInfo = selfInfo.replace("\"", "%");
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("taskId", node.getTaskId());
        queryParams.put("nodeId", node.getNodeId());
        queryParams.put("sampleStatus", node.getSampleStatus());
        queryParams.put("selfInfo", selfInfo);
        String panelId = node.getNodeNickName() + "-";
        queryParams.put("operation", "createTask1234");
        queryParams.put("sendMessage", "{}");
        queryParams.put("panelId", panelId);
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("queryParams", queryParams);
        // 主题信息
        if (IDEContext.getValueFromGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING,
                BaseCacheVal.LIGHT_THEME.vaLue())) {
            queryParams.put("currentTheme", IDEThemeListener.LIGHT_THEME);
        } else {
            queryParams.put("currentTheme", IDEThemeListener.DARCULA_THEME);
        }
        NavigatorPageBean navigatorPage = MessageRouterHandler.generateNavigatorPage("/navigate",
                "/home", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "ShowNodeWebView" + node.getNodeId());
    }
}
