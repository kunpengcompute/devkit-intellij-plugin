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

package com.huawei.kunpeng.porting.common.constant;

import com.intellij.ui.JBColor;

import java.awt.Color;
import java.util.HashSet;

/**
 * 扫描porting的接口返回数据的key以及一些相关key
 *
 * @since 2020-11-12
 */
public class SuggestionConstant {
    /**
     * portingItem data数据的key
     */
    public static final String PORTING_ITEMS_KEY = "portingitems";

    /**
     * line 迁移预检建议key
     */
    public static final String LINE_KEY = "line";

    /**
     * portingItem已经被替换的标志
     */
    public static final String PORTING_ITEM_REPLACED_KEY = "replaced";

    /**
     * portingItem建议key
     */
    public static final String PORTING_ITEM_STRATEGY_KEY = "strategy";

    /**
     * portingItem问题描述key
     */
    public static final String PORTING_ITEM_DESCRIPTION_KEY = "description";

    /**
     * portingItem问题开始行key
     */
    public static final String PORTING_ITEM_LOC_BEGIN_KEY = "locbegin";

    /**
     * portingItem问题结束行key
     */
    public static final String PORTING_ITEM_LOC_END_KEY = "locend";

    /**
     * portingItem问题建议类型key
     */
    public static final String PORTING_ITEM_SUGGESTION_TYPE_KEY = "suggestiontype";

    /**
     * portingItem问题关键词key
     */
    public static final String PORTING_ITEM_KEYWORD_KEY = "keyword";

    /**
     * portingItem问题关键词替换内容key，目前只有700类型的在用
     */
    public static final String PORTING_ITEM_REPLACEMENT_KEY = "replacement";

    /**
     * col
     */
    public static final String PORTING_COL_KEY = "col";

    /**
     * portingItem问题替换航，目前1010类型使用
     */
    public static final String INSERT_NO = "insertno";

    /**
     * 迁移预检、弱内存序专用建议类型：不能快速修复
     */
    public static final int ENHANCED_NO_QUICK_FIX_SUGGESTION_TYPE = 9998;

    /**
     * 迁移预检、弱内存序专用建议类型：能快速修复
     */
    public static final int ENHANCED_QUICK_FIX_SUGGESTION_TYPE = 9999;

    /**
     * 源码迁移功能国家WA中心反馈的字节对齐专用建议类型：不能快速修复
     */
    public static final int BYTE_ALIGN_NO_QUICK_FIX_SUGGESTION_TYPE = 2500;

    /**
     * 源码迁移功能：C/C++源码扫描检查优化类型
     */
    public static final int SOURCE_CODE_CHECK_OPTIMIZATION_TYPE = 1010;

    /**
     * 建议替换的类型集合（有quickfix）
     */
    public static final HashSet<Integer> SUGGEST_REPLACE_TYPE = new HashSet<Integer>() {
        {
            add(100);
            add(200);
            add(300);
            add(500);
            add(600);
            add(700);
            add(1000);
            add(1100);
            add(1300);
            add(1400);
            add(2200);
            add(1500);
            add(ENHANCED_QUICK_FIX_SUGGESTION_TYPE);
            add(1600);
            add(SOURCE_CODE_CHECK_OPTIMIZATION_TYPE);
        }
    };

    /**
     * 不支持单个替换quickfix的类型
     */
    public static final int NO_SINGLE_QUICKFIX_TYPE = 1600;

    /**
     * 按照建议需要修改的行背景
     */
    public static final JBColor SUGGESTION_BACKGROUND_COLOR = JBColor.namedColor("suggestion.color",
            new JBColor(new Color(0xF5EAC1), new Color(0x4D3732)));

    /**
     * 按照建议修改后的行背景
     */
    public static final JBColor REPLACED_BACKGROUND_COLOR = JBColor.namedColor("replaced.color",
            new JBColor(new Color(0xEFFAE7), new Color(0x32464D)));
}