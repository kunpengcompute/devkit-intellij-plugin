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
import com.huawei.kunpeng.intellij.common.bean.DataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.I18NTitle;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 国际化服务
 *
 * @since 2020-09-25
 */
public class I18NServer {
    /**
     * resource bundle message's name
     */
    protected static final String MESSAGES = "messages";

    /**
     * 更新当前系统语言
     *
     * @return Locale
     */
    public static Locale updateCurrentLocale() {
        Logger.info("=====start update current Locale=====");
        Locale locale = getLocale();

        // 更新缓存
        IDEContext.setValueForGlobalContext(null, BaseCacheVal.CURRENT_LOCALE.vaLue(), locale);

        // 更新工具语言国际化标签
        updateTitleDisplay();
        Logger.info("=====update current Locale successful=====");
        return locale;
    }

    /**
     * getLocale
     *
     * @return Locale
     */
    @NotNull
    protected static Locale getLocale() {
        return ("File".equals(ActionManager.getInstance().getAction("FileMenu").getTemplateText()))
                ? new Locale( "en",  "US")
                : new Locale( "zh",  "CN");
    }

    /**
     * 获取当前系统Locale
     *
     * @return Locale
     */
    public static Locale getCurrentLocale() {
        return IDEContext.getValueFromGlobalContext(null, BaseCacheVal.CURRENT_LOCALE.vaLue());
    }

    /**
     * 获取当前系统语言
     *
     * @return String
     */
    public static String getCurrentLanguage() {
        return (getCurrentLocale().getLanguage() + "-" + getCurrentLocale().getCountry()).toLowerCase();
    }

    /**
     * 无参国际化
     *
     * @param codeSTR code
     * @return string
     */
    public static String toLocale(String codeSTR) {
        Locale currentLocale = getCurrentLocale();
        return ResourceBundle.getBundle(MESSAGES, currentLocale).getString(codeSTR);
    }

    /**
     * 带参国际化
     *
     * @param code code
     * @param val value
     * @return string
     */
    public static String toLocale(String code, String val) {
        String result = toLocale(code);
        if (val == null || val.isEmpty()) {
            Logger.error("code: {}, args is null or empty.", code);
            return result;
        }
        return MessageFormat.format(result, val);
    }

    /**
     * 带参国际化
     *
     * @param code code
     * @param param arms
     * @return string
     */
    public static String toLocale(String code, List<?> param) {
        String result = toLocale(code);
        if (param == null || param.isEmpty()) {
            Logger.error("code: {}, args is null or empty.", code);
            return result;
        }
        return MessageFormat.format(result, param.toArray());
    }

    /**
     * 根据系统环境返回请求接口中获取到的返回参数
     *
     * @param resp ResponseBean
     * @return String
     */
    public static String respToLocale(ResponseBean resp) {
        if (resp == null) {
            return "";
        }
        if (("en").equals(getCurrentLocale().getLanguage())) {
            return resp.getInfo();
        } else {
            return resp.getInfochinese();
        }
    }

    /**
     * 根据系统环境返回请求接口中获取到的返回参数
     *
     * @param resp DataBean
     * @return String
     */
    public static String analysisToLocale(DataBean resp) {
        if (resp == null) {
            return "";
        }
        if (("en").equals(getCurrentLocale().getLanguage())) {
            return resp.getInfo();
        } else {
            return resp.getInfoChinese();
        }
    }

    /**
     * 根据系统环境返回请求接口中获取到的返回参数
     *
     * @param data data
     * @return string
     */
    public static String dataToLocale(Map<String, ?> data) {
        if (data == null) {
            return "";
        }
        Object info = data.get("infochinese");
        if (("en").equals(getCurrentLocale().getLanguage())) {
            info = data.get("info");
        }
        if (info instanceof String) {
            return (String) info;
        } else {
            return "";
        }
    }

    /**
     * 判断terminal tab是否存在
     *
     * @param key key值
     * @param content 需要填充的值
     * @return string value值
     */
    public static Boolean handleTab(Content content, String key) {
        return content.getDisplayName().contains(toLocale(key));
    }

    /**
     * 更新工具栏的国际化标签
     */
    public static void updateTitleDisplay() {
        for (I18NTitle title : I18NTitle.values()) {
            switch (title.titleType()) {
                case ACTION:
                    ActionManager.getInstance()
                        .getAction(title.titleId())
                        .getTemplatePresentation()
                        .setText(toLocale(title.i18nKey()));
                    break;
                case TOOL_WINDOW:
                    ToolWindowManager instance = ToolWindowManager.getInstance(CommonUtil.getDefaultProject());
                    ToolWindow toolWindow = instance.getToolWindow(title.titleId());
                    if (toolWindow != null) {
                        toolWindow.setTitle(toLocale(title.i18nKey()));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
