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

package com.huawei.kunpeng.intellij.ui.action;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.ui.dialog.NoNetworkForFeedbackDialog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * The class FeedBackAction
 *
 * @since v1.0
 */
public class FeedBackAction extends AnAction implements DumbAware {
    private static final String FEEDBACK = CommonI18NServer.toLocale("common_feedback");
    private static final int TIME_OUT = 1000 * 3;

    private static final Icon icon = BaseIntellijIcons.load(IDEConstant.MENU_ICONS_PATH + IDEConstant.MENU_FEEDBACK_ICON);

    private String feedBackUrl;

    /**
     * 左侧树反馈菜单动作
     */
    public FeedBackAction(String feedBackUrl) {
        super(FEEDBACK, null, icon);
        this.feedBackUrl = feedBackUrl;
    }

    /**
     * 跳转到建议反馈网站
     *
     * @param url 建议反馈网站url
     */
    public static void goFeedBack(String url) {
        if (isHasNetWork(url, TIME_OUT)) {
            CommonUtil.openURI(url);
        } else {
            NoNetworkForFeedbackDialog networkDialog =
                    new NoNetworkForFeedbackDialog(CommonI18NServer.toLocale("common_plugins_connection_failed"), null);
            networkDialog.displayPanel();
        }
    }

    /**
     * 浏览器打开鲲鹏社区论坛
     *
     * @param event event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        goFeedBack(feedBackUrl);
    }

    private static boolean isHasNetWork(String urlString, int timeOutMillSeconds) {
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(urlString);
            URLConnection co = url.openConnection();
            if (co instanceof HttpURLConnection) {
                conn = (HttpURLConnection) co;
                conn.setConnectTimeout(timeOutMillSeconds);
                return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
            }
            return false;
        } catch (IOException e) {
            Logger.error("test netWork for url is fail!");
            return false;
        } finally {
            if (conn != null) {
                try {
                    FileUtil.closeStreams(conn.getInputStream(), null);
                } catch (IOException e) {
                    Logger.error("close inputStream fail!");
                }
                FileUtil.closeStreams(conn.getErrorStream(), null);
                conn.disconnect();
            }
        }
    }
}
