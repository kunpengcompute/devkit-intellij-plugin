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

package com.huawei.kunpeng.porting.webview.pagewebview;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.WebSessionBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.CMDFunction;

import com.intellij.openapi.application.ApplicationManager;

import javax.swing.JComponent;

/**
 * 自定义webview
 *
 * @since 2020-10-23
 */
public class PortingWebView extends AbstractWebView {
    /**
     * 用户session信息
     */
    WebSessionBean sessionBean = UserInfoContext.getInstance().getDefaultWebSessionBean();

    /**
     * 获取页面内容
     *
     * @return JComponent
     */
    @Override
    public JComponent getContent() {
        return jPanel;
    }

    @Override
    public void routingCmd(MessageBean messageBean) {
        if (CMDFunction.getStatusByValue(messageBean.getCmd()).equals(CMDFunction.NULL)) {
            Logger.error("not found function: {}", messageBean.getCmd());
            return;
        }

        // 执行具体处理方法
        ApplicationManager.getApplication().invokeLater(() -> CMDFunction.getStatusByValue(messageBean.getCmd())
            .functionHandler()
            .executeFunction(messageBean.getCmd(), messageBean, PortingIDEConstant.TOOL_NAME_PORTING));
    }
}
