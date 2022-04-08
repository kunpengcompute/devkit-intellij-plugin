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

package com.huawei.kunpeng.intellij.common.util;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.RespondStatus;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.PluginManagerConfigurable;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;

import org.cef.OS;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

/**
 * IDE右下角通知栏工具
 *
 * @since 1.0.0
 */
public class IDENotificationUtil {
    /**
     * 右下角通知通用方式
     *
     * @param notificationBean 通知框信息
     */
    public static void notificationCommon(NotificationBean notificationBean) {
        Notification notification = new Notification(
                CommonUtil.getPluginName(),
            notificationBean.getTitle(), notificationBean.getContent(), notificationBean.getType());
        Notifications.Bus.notify(notification,
            (notificationBean.getProject() == null) ? CommonUtil.getDefaultProject() : notificationBean.getProject());
    }

    /**
     * "<html><a href=\\\"url\\>超链接</a> 内容." + "<br>换行.</html>"
     *
     * @param notificationBean 通知框信息
     * @param actionOperate 超链接后的操作
     */
    public static void notificationForHyperlink(NotificationBean notificationBean, ActionOperate actionOperate) {
        Notification notification = new Notification(CommonUtil.getPluginName(),
                notificationBean.getTitle(), notificationBean.getContent(), notificationBean.getType());
        notification.setListener((notification1, event) -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED && actionOperate != null) {
                Logger.info(event.getDescription());
                actionOperate.actionOperate(event);
            }
        });
        Notifications.Bus.notify(notification, (notificationBean.getProject() == null) ? CommonUtil.getDefaultProject()
                : notificationBean.getProject());
    }

    /**
     * 响应信息种类,右下角提示
     *
     * @param title 右下角弹框标题，对应操作
     * @param responseStatus 响应状态
     * @param responseInfo 响应信息
     */
    public static void notifyCommonForResponse(String title, String responseStatus, ResponseBean responseInfo) {
        switch (RespondStatus.getStatusByValue(responseStatus)) {
            case PROCESS_STATUS_NORMAL:
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(title, CommonUtil.getRspTipInfo(responseInfo),
                                NotificationType.INFORMATION));
                break;
            case PROCESS_STATUS_NOT_NORMAL:
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(title, CommonUtil.getRspTipInfo(responseInfo), NotificationType.WARNING));
                break;
            default:
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(title, CommonUtil.getRspTipInfo(responseInfo), NotificationType.ERROR));
                break;
        }
    }

    /**
     * 提示系统是否支持
     */
    public static void systemIsNotSupport() {
        Logger.error("detectVersionIsMatch jcef is null.");
        String sysTip = I18NServer.toLocale("version_is_not_window_support");
        if (IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.LINUX) {
            sysTip = I18NServer.toLocale("version_is_not_linux_support");
        } else if (IDEContext.getValueFromGlobalContext(null,
                BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
            sysTip = I18NServer.toLocale("version_is_not_window_support");
        } else {
            // 系统不支持
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", I18NServer.toLocale("version_is_not_support"),
                    NotificationType.ERROR));
        }

        IDENotificationUtil.notificationForHyperlink(new NotificationBean("", sysTip, NotificationType.WARNING),
            new ActionOperate() {
                @Override
                public void actionOperate(Object data) {
                    ApplicationManager.getApplication()
                        .invokeLater(() -> ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), PluginManagerConfigurable.class,
                                config -> PropertiesComponent.getInstance()
                                    .setValue("PluginConfigurable.selectionTab", 0, 0)));
                }
            });
    }

    /**
     * 发生弹窗提示
     *
     * @param content 内容
     * @param notificationType 通知类型
     * @param project 项目
     */
    public static void sendMessage(String content, NotificationType notificationType, Project project) {
        Notification notification = new Notification(CommonUtil.getPluginName(),
                CommonI18NServer.toLocale("message.title"), content, notificationType);
        notification.setListener(new OpenPluginPanel(project));
        notification.setDropDownText(CommonI18NServer.toLocale("drop.down.text"));
        notification.setImportant(true);
        Notifications.Bus.notify(notification, project);
    }

    /**
     * 在当前Project弹出notification
     *
     * @param title title
     * @param content content
     * @param type  type
     */
    public static void notifyInfo(String title, String content, NotificationType type) {
        NotificationBean notificationBean = new NotificationBean(title, content, type);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    /**
     * 打开插件面板
     *
     */
    private static class OpenPluginPanel extends NotificationListener.Adapter {
        private final Project project;

        private OpenPluginPanel(Project project) {
            super();
            this.project = project;
        }

        @Override
        protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
            if ("jumpPlugin".equalsIgnoreCase(event.getDescription())) {
                ApplicationManager.getApplication()
                        .invokeLater(() -> ShowSettingsUtil.getInstance()
                                .showSettingsDialog(project, PluginManagerConfigurable.class,
                                        config -> PropertiesComponent.getInstance()
                                                .setValue("PluginConfigurable.selectionTab", 0, 0)));
            }
            if ("jumpUrl".equalsIgnoreCase(event.getDescription())) {
                if (OS.isWindows()) {
                    UIUtil.invokeLaterIfNeeded(
                            () -> BrowserUtil.browse(PropertiesUtils.load("jcef_windows_download_location")));
                } else if (OS.isLinux()) {
                    UIUtil.invokeLaterIfNeeded(
                            () -> BrowserUtil.browse(PropertiesUtils.load("jcef_linux_download_location")));
                } else if (OS.isMacintosh()) {
                    UIUtil.invokeLaterIfNeeded(
                            () -> BrowserUtil.browse(PropertiesUtils.load("jcef_mac_download_location")));
                } else {
                    Logger.warn("OS type not match");
                }
            }
        }
    }
}
