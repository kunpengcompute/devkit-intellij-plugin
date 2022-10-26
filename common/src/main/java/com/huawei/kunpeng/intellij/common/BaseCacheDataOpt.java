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

package com.huawei.kunpeng.intellij.common;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import com.intellij.notification.NotificationType;

import org.cef.OS;

/**
 * IDE 请求相关缓存数据操作
 *
 * @since 1.0.0
 */
public class BaseCacheDataOpt {
    /**
     * 更新全局token
     *
     * @param module 模块
     * @param token  token口令
     */
    public static void updateGlobalToken(String module, String token) {
        if (!StringUtil.stringIsEmpty(module) && !StringUtil.stringIsEmpty(token)) {
            IDEContext.setValueForGlobalContext(module, BaseCacheVal.TOKEN.vaLue(), token);
        }
    }

    /**
     * 更新全局ip和端口
     *
     * @param module    模块
     * @param ip        ip地址
     * @param port      端口
     * @param localPort 本地nginx代理端口
     */
    public static void updateGlobalIPAndPort(String module, String ip, String port, String localPort) {
        Logger.info("update Global IP And Port");
        if (!StringUtil.stringIsEmpty(module) && ip != null && port != null) {
            IDEContext.setValueForGlobalContext(module, BaseCacheVal.IP.vaLue(), ip);
            IDEContext.setValueForGlobalContext(module, BaseCacheVal.PORT.vaLue(), port);
            IDEContext.setValueForGlobalContext(module, BaseCacheVal.LOCAL_PORT.vaLue(), localPort);
        }
        Logger.info("update Global IP , Port And LocalPort successful");
    }

    /**
     * 获取系统信息及动态库的环境path
     */
    public static void loadingSystemOS() {
        Logger.info("loading SystemOS");
        if (OS.isLinux()) {
            Logger.info("SystemOS is linux");
            IDEContext.setValueForGlobalContext(
                    null, BaseCacheVal.CURRENT_CHARSET.vaLue(), IDEConstant.CHARSET_UTF8);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue(), SystemOS.LINUX);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.JCEF_DLL_EVN_PATH.vaLue(),
                    CommonUtil.getPluginJCEFPath());
            osNotSupportTip();
        } else if (OS.isWindows()) {
            Logger.info("SystemOS is windows");
            IDEContext.setValueForGlobalContext(null,
                    BaseCacheVal.CURRENT_CHARSET.vaLue(), IDEConstant.CHARSET_ISO_8859_1);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue(), SystemOS.WINDOWS);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.JCEF_DLL_EVN_PATH.vaLue(),
                    CommonUtil.getPluginJCEFPath());
        } else if (OS.isMacintosh()) {
            // MAC系统，应该与linux和others区分开？
//            osNotSupportTip();
            Logger.info("SystemOS is mac");
            IDEContext.setValueForGlobalContext(null,
                    BaseCacheVal.CURRENT_CHARSET.vaLue(), IDEConstant.CHARSET_ISO_8859_1);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue(), SystemOS.MAC);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.JCEF_DLL_EVN_PATH.vaLue(),
                    CommonUtil.getPluginJCEFPath());
        } else {
            osNotSupportTip();
            Logger.info("SystemOS is others");
            IDEContext.setValueForGlobalContext(null,
                    BaseCacheVal.CURRENT_CHARSET.vaLue(), IDEConstant.CHARSET_ISO_8859_1);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue(), SystemOS.OTHER);
            IDEContext.setValueForGlobalContext(null, BaseCacheVal.JCEF_DLL_EVN_PATH.vaLue(), null);
        }
        Logger.info("loading SystemOS successful");
    }

    public static void osNotSupportTip() {
        Logger.warn("The plugin version not supports current Operate system");
        String osTypeNotSupportTip = CommonI18NServer.toLocale("plugins_common_os_type_not_support");
        IDENotificationUtil.notificationCommon(new NotificationBean("",
                osTypeNotSupportTip, NotificationType.WARNING));
    }
}
