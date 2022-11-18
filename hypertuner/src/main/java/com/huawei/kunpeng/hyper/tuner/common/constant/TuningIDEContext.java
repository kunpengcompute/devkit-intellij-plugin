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

package com.huawei.kunpeng.hyper.tuner.common.constant;

import static com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus.IDE_STATUS_SERVER_CONFIG;

import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import com.intellij.openapi.application.PathManager;

import java.util.HashMap;
import java.util.Optional;

/**
 * IDE plugin全局上下文
 *
 * @since 2020-10-28
 */
public class TuningIDEContext<T> extends IDEContext {
    /**
     * 常量实例
     */
    public static TuningIDEContext instance = new TuningIDEContext();

    /**
     * get global PortingIDEPluginStatus
     *
     * @return IDEPluginStatus 获取PortingIDE状态值
     */
    public static IDEPluginStatus getTuningIDEPluginStatus() {
        return Optional.ofNullable(getIDEPluginStatus(TuningIDEConstant.TOOL_NAME_TUNING))
                .orElse(IDEPluginStatus.IDE_STATUS_INIT);
    }

    /**
     * set global setTuningIDEPluginStatus
     *
     * @param value 设置TuningIDE的状态值
     */
    public static void setTuningIDEPluginStatus(IDEPluginStatus value) {
        setIDEPluginStatus(TuningIDEConstant.TOOL_NAME_TUNING, value);
    }

    /**
     * 右键操作前check是否配置服务器，否则弹出相关窗口
     *
     * @return boolean check是否OK
     */
    public static boolean checkServerConfig() {
        int value = 0;
        IDEPluginStatus valueFromGlobalContext = getIDEPluginStatus(TuningIDEConstant.TOOL_NAME_TUNING);
        value = (valueFromGlobalContext).value();

        if (value < IDE_STATUS_SERVER_CONFIG.value()
                && StringUtil.stringIsEmpty(TuningCommonUtil.readCurIpFromConfig())) {
            System.out.println("not config yet!");
            // TODO 重新修改！！
//            IDEBasePanel panel = new TuningServerConfigPanel(null);
//
//            TuningServerConfigWrapDialog serverConfigDialog =
//                    new TuningServerConfigWrapDialog(TuningUserManageConstant.CONFIG_TITLE, panel);
//            serverConfigDialog.displayPanel();
//            final int exitCode = serverConfigDialog.getExitCode();
//            return exitCode != DialogWrapper.CANCEL_EXIT_CODE;
        }
        return true;
    }

    /**
     * 获取当前系统编码
     *
     * @return string value值
     */
    public static String getCurrentCharset() {
        return getValueFromGlobalContext(null, BaseCacheVal.CURRENT_CHARSET.vaLue());
    }

    // 初始化常用的基础变量缓存
    static {
        setValueForGlobalContext(null, TuningIDEConstant.TOOL_NAME_TUNING, new HashMap<>());
        setValueForGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.BASE_URL.vaLue(), "/");
        setValueForGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.IP.vaLue(), null);
        setValueForGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.PORT.vaLue(), null);
        setValueForGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.TOKEN.vaLue(), null);
        setValueForGlobalContext(
                TuningIDEConstant.TOOL_NAME_TUNING,
                BaseCacheVal.PORTING_ZH_PATH_UNICODE_STR.vaLue(),
                CommonUtil.generateRandomStr());
        setIDEPluginStatus(TuningIDEConstant.TOOL_NAME_TUNING, IDEPluginStatus.IDE_STATUS_INIT);
    }

    /**
     * 获取tuning插件webview页面index入口
     *
     * @return String
     */
    public static String getWebViewIndex() {
        if (getValueFromGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING,
                BaseCacheVal.TUNING_WEB_VIEW_INDEX.vaLue()) == null) {
            setValueForGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.TUNING_WEB_VIEW_INDEX.vaLue(),
                    PathManager.getPluginsPath() + IDEConstant.PATH_SEPARATOR +
                            TuningIDEConstant.TUNING_NAME +
                            TuningIDEConstant.WEB_VIEW_INDEX_HTML);
        }
        return getValueFromGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING,
                BaseCacheVal.TUNING_WEB_VIEW_INDEX.vaLue());
    }

    /**
     * 获取tuning插件login页面index入口
     * @return
     */
    public static String getLoginWebViewIndex() {
        String indexPath = PathManager.getPluginsPath() + IDEConstant.PATH_SEPARATOR +
                TuningIDEConstant.TUNING_NAME +
                TuningIDEConstant.TUNING_LOGIN_WEB_VIEW_INDEX_HTML;
        return indexPath;
    }

}
