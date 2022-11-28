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

import com.huawei.kunpeng.hyper.tuner.common.TuningUserInfoContext;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.CMDFunction;
import com.huawei.kunpeng.intellij.common.bean.WebSessionBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;

import com.intellij.openapi.application.ApplicationManager;

import javax.swing.JComponent;

/**
 * 自定义webview
 *
 * @since 2020-10-23
 */
public abstract class WebView extends AbstractWebView {
    /**
     * 用户session信息
     */
    public WebSessionBean sessionBean = TuningUserInfoContext.getInstance().getDefaultWebSessionBean();

    /**
     * 获取页面内容
     *
     * @return JComponent
     */
    public JComponent getContent() {
        return jPanel;
    }

    /**
     * 关闭该页面
     */
    public void dispose() {
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
                .executeFunction(messageBean.getCmd(), messageBean, TuningIDEConstant.TOOL_NAME_TUNING));
    }

    /**
     * indexHtmlKey使用tuning插件特定值
     * @return tuning插件静态webview代理html位置
     */
    @Override
    public String getIndexHtmlKey() {
        return TuningIDEConstant.WEB_VIEW_INDEX_HTML;
    }
}
