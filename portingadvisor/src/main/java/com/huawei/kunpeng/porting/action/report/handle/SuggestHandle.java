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

package com.huawei.kunpeng.porting.action.report.handle;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.porting.common.constant.SuggestionConstant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.codeInspection.ex.InspectionProfileModifiableModelKt;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * the class handle porting suggestion
 *
 * @since 2020-11-12
 */
public class SuggestHandle {
    private static final String SUGGESTION = "Suggestion:";

    private static final String IF_DEFINED_AARCH_64 = "#if defined(__aarch64__)";

    private static final String SUGGESTION1 = "// Suggestion:";

    private static final String ELIF_DEFINED_AARCH_64 = "#elif defined(__aarch64__)";

    private static final String ORIGINAL_CONTENT = "originalContent";

    private static final String IF_DEFINED_X_86_64 = "#if defined(__x86_64__)";

    private static final String ENDIF = "#endif";

    private static volatile SuggestHandle suggestHandle = null;

    private static final char LINE_END = 10;

    private static final String SPACE = "    ";

    private static final String OLD_TEXT = "[oldText]";

    private static final int MIN_LINE = 3;

    /**
     * 非当前系统换行符，只是接口返回的数据以此为换行符
     */
    private static final String API_LINE_SEPARATOR = "\n";

    /**
     * 双检索单例
     *
     * @return SuggestHandle
     */
    public static SuggestHandle getSuggestHandle() {
        if (suggestHandle == null) {
            synchronized (SuggestHandle.class) {
                if (suggestHandle == null) {
                    suggestHandle = new SuggestHandle();
                }
            }
        }
        return suggestHandle;
    }

    /**
     * 单个修改意见替换
     * 迁移建议类型，取值：
     * 100：需要添加avx2neon.h头文件
     * 200：需要添加sse2neon.h头文件
     * 300：纯汇编文件中迁移关键字有明确建议
     * 400：纯汇编文件中迁移关键字有模糊建议
     * 500：.c等文件中迁移关键字有明确建议
     * 600：.c等文件中迁移关键字有模糊建议
     * 700：make相关文件中迁移关键字有明确建议
     * 800：make相关文件中迁移关键字有模糊建议
     * 900：fortan相关文件中移植关键字有模糊建议
     * 1000：预处理等文件中移植关键字有模糊建议
     * 1100：纯汇编中整个文件替换
     * 1200：内嵌汇编中没有迁移建议
     * 1400：fortan相关文件中移植关键字有明确建议
     * 1500：C调用Fortran内置函数iargc场景明确建议
     *
     * @param portingItem 单个portingitem
     * @param editor      the text editor
     * @param type        单个portingitem修改建议类型
     */
    public void handleSuggestReplace(JSONObject portingItem, Editor editor, int type) {
        String methodName = "handleSuggestionType" + type;
        Method method = null;
        try {
            method = SuggestHandle.class.getDeclaredMethod(methodName, JSONObject.class, Editor.class);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(this, portingItem, editor);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.error("Unexpected suggestionType : " + type);
        }
        portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true); // 设置已经被替换标志
    }

    /**
     * 处理是否按照建议修改过
     *
     * @param portingItem        单个portingitem数据
     * @param document           当前文件内容
     * @param translateBeginLine 变更的begin行，从0开始
     * @param translateEndLine   变更的end行，从0开始
     */
    public void handleSuggestionReplaced(JSONObject portingItem, Document document,
        int translateBeginLine, int translateEndLine) {
        int type = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY);
        if (!SuggestionConstant.SUGGEST_REPLACE_TYPE.contains(type)) {
            handleCompareWithOldText(portingItem, document, translateBeginLine, translateEndLine);
            return;
        }
        String methodName = "handleSuggestionReplaced" + type;
        Method method = null;
        try {
            method = SuggestHandle.class.getDeclaredMethod(methodName, JSONObject.class, Document.class);
            method.setAccessible(true);
            method.invoke(this, portingItem, document);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            try {
                method = SuggestHandle.class.getDeclaredMethod(methodName, JSONObject.class, Document.class,
                    Integer.class, Integer.class);
                method.setAccessible(true);
                method.invoke(this, portingItem, document, translateBeginLine, translateEndLine);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                Logger.error("Unexpected suggestionType : " + type);
            }
        }
    }

    /**
     * 判断文件头是否存在预期的内容
     *
     * @param document 文件内容
     * @param first    第一行内容
     * @param second   第二行内容
     * @param end      最后一行内容
     * @return true:表示存在三行预期内容
     */
    private boolean isInsertToFileHead(Document document, String first, String second, String end) {
        boolean isFirstLine = false;
        boolean isSecondLine = false;
        for (int i = 0; i < document.getLineCount(); i++) {
            String text = getTextFromBeginLineAndEndLine(document, i, i);
            final String trimText = text.trim();
            if (("").equals(trimText)) {
                continue;
            }
            if (trimText.startsWith("//")) {
                continue;
            }
            if (first.equals(trimText)) {
                isFirstLine = true;
                continue;
            }
            if (isFirstLine) {
                if (second.equals(trimText)) {
                    isSecondLine = true;
                } else {
                    isFirstLine = false;
                }
                continue;
            }
            if (isSecondLine && end.equals(trimText)) {
                return true;
            }
        }
        return false;
    }

    private void handleSuggestionReplaced100(JSONObject portingItem, Document document) {
        if (isInsertToFileHead(document, IF_DEFINED_AARCH_64,
            "#include \"avx2neon.h\"", ENDIF)) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true); // 设置已经被替换标志
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        }
    }

    private void handleSuggestionReplaced200(JSONObject portingItem, Document document) {
        if (isInsertToFileHead(document, IF_DEFINED_AARCH_64,
            "#include \"sse2neon.h\"", ENDIF)) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true); // 设置已经被替换标志
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        }
    }

    private void handleSuggestionReplaced300(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        String text = getTextFromBeginLineAndEndLine(document, translateBeginLine, translateEndLine);
        String[] split = portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY)
            .split(SUGGESTION, -1);
        if (text.trim().startsWith("//")
            && text.contains(split.length > 1 ? split[1] : split[0])) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false); // 设置已经被替换标志
        }
    }

    private boolean handleReplacedSecondLine(String oldText, Document document,
        int translateBeginLine, int translateEndLine) {
        String text = getTextFromBeginLineAndEndLine(document, translateBeginLine, translateEndLine);
        return text.contains(oldText.trim());
    }

    private boolean handleReplacedFirstLine(Document document, int translateBeginLine) {
        boolean isFirstLine = false;
        for (int i = translateBeginLine - 1; i >= 0; i--) {
            String text = getTextFromBeginLineAndEndLine(document, i, i);
            if (("").equals(text.trim())) {
                continue;
            }
            if (IF_DEFINED_X_86_64.equals(text.trim())) {
                isFirstLine = true;
                break;
            }
        }
        return isFirstLine;
    }

    private void judgeSuggestionReplaced500And600(JSONObject portingItem, Document document,
        int translateBeginLine, int translateEndLine, String fourth) {
        final String oldText = portingItem.getString(ORIGINAL_CONTENT);
        if (oldText == null) {
            return;
        }
        boolean isSecondLine = handleReplacedSecondLine(oldText, document, translateBeginLine, translateEndLine);
        boolean isThirdLine = false;
        boolean isFourthLine = false;
        boolean isFiveLine = false;
        if (!isSecondLine) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true); // 设置已经被替换标志
            return;
        }
        for (int i = translateBeginLine + 1; i < document.getLineCount(); i++) {
            String text = getTextFromBeginLineAndEndLine(document, i, i);
            if (("").equals(text.trim())) {
                continue;
            }
            if (ELIF_DEFINED_AARCH_64.equals(text.trim())) {
                isThirdLine = true;
                continue;
            }
            if (isThirdLine && !isFourthLine) {
                int startOffset = document.getLineStartOffset(i); // 获取需要修改在编辑文本起始位置
                isFourthLine = isStartWithIgnoreLineHeadAndLineEndSpaces(
                    document.getText(new TextRange(startOffset, document.getTextLength())).trim(), fourth.trim());
                continue;
            }
            if (isFourthLine) {
                if (fourth.contains(text.trim())) {
                    continue;
                }
                isFiveLine = text.trim().startsWith(ENDIF);
                break;
            }
        }
        boolean isFirstLine = handleReplacedFirstLine(document, translateBeginLine);
        if (isFirstLine && isThirdLine && isFourthLine && isFiveLine) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true); // 设置已经被替换标志
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        }
    }

    private void handleSuggestionReplaced500(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        String fourth = portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);
        judgeSuggestionReplaced500And600(portingItem, document, translateBeginLine, translateEndLine, fourth);
    }

    private void handleSuggestionReplaced600(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        String fourth = SUGGESTION1 + portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);
        judgeSuggestionReplaced500And600(portingItem, document, translateBeginLine, translateEndLine, fourth);
    }

    private void handleSuggestionReplaced700(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        handleCompareWithOldText(portingItem, document, translateBeginLine, translateEndLine);
    }

    /**
     * 比较当前问题行内容是否是原始内容，不是表示问题已经被修改
     *
     * @param portingItem        数据
     * @param document           文件内容
     * @param translateBeginLine 开始行
     * @param translateEndLine   结束行
     */
    private void handleCompareWithOldText(JSONObject portingItem, Document document,
        int translateBeginLine, int translateEndLine) {
        final String oldText = portingItem.getString(ORIGINAL_CONTENT);
        String text = getTextFromBeginLineAndEndLine(document, translateBeginLine, translateEndLine);
        if (oldText == null) {
            return;
        }
        if (text.trim().equals(oldText.trim())) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true); // 设置已经被替换标志
        }
    }

    private void handleSuggestionReplaced1000(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        boolean isFirstLine = false;
        boolean isSecondLine = false;
        for (int i = translateEndLine; i <= document.getLineCount() - 1; i++) {
            String lineText = getTextFromBeginLineAndEndLine(document, i, i);
            if (("").equals(lineText.trim())) {
                continue;
            }
            if (lineText.trim().startsWith(ELIF_DEFINED_AARCH_64)) {
                isFirstLine = true;
                continue;
            }
            if (isFirstLine) {
                isSecondLine = lineText.trim().startsWith("//");
                break;
            }
            if (ENDIF.equals(lineText.trim())) {
                break;
            }
        }
        if (isSecondLine) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        }
    }

    private void handleSuggestionReplaced1010(JSONObject portingItem, Document document, Integer translateBeginLine,
        Integer translateEndLine) {
        int insertNo = portingItem.getIntValue(SuggestionConstant.INSERT_NO);
        String lineText = getTextFromBeginLineAndEndLine(document, insertNo - 1, insertNo - 1);
        if (lineText.trim().startsWith(ELIF_DEFINED_AARCH_64)) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        }
    }

    private void handleSuggestionReplaced1100(JSONObject portingItem, Document document) {
        String strategy = portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);
        String[] split = strategy.split("\\n", -1);
        int notNullLineNum = 0;

        // docment数据与strategy数据去掉空行逐行对比，不一致表示未替换
        for (int i = 0; i < document.getLineCount(); i++) {
            String text = getTextFromBeginLineAndEndLine(document, i, i).trim();
            if (("").equals(text)) {
                continue;
            }
            if (notNullLineNum > split.length - 1) {
                backSuggestType1100(portingItem, document);
                return;
            }
            String originalText = split[notNullLineNum].trim();
            notNullLineNum++;
            while (("").equals(originalText)) {
                if (notNullLineNum > split.length - 1) {
                    backSuggestType1100(portingItem, document);
                    return;
                }
                originalText = split[notNullLineNum].trim();
                notNullLineNum++;
            }

            if (!originalText.equals(text)) {
                backSuggestType1100(portingItem, document);
                return;
            }
        }
        portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
    }

    /**
     * 1100类型检测出未被替换，更新首末行数据为第一非空行
     *
     * @param portingItem 数据
     * @param document    文件内容
     */
    private void backSuggestType1100(JSONObject portingItem, Document document) {
        for (int i = 0; i < document.getLineCount(); i++) {
            String textFromBeginLineAndEndLine = getTextFromBeginLineAndEndLine(document, i, i);
            if (textFromBeginLineAndEndLine.trim().length() > 0) {
                portingItem.put(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY, i + 1);
                portingItem.put(SuggestionConstant.PORTING_ITEM_LOC_END_KEY, i + 1);
                portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
                break;
            }
        }
    }

    private void handleSuggestionReplaced1300(JSONObject portingItem, Document document) {
        if (isInsertToFileHead(document, IF_DEFINED_AARCH_64,
            "#include \"KunpengTrans.h\"", ENDIF)) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true); // 设置已经被替换标志
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        }
    }

    private void handleSuggestionReplaced1400(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        int beginLine = translateBeginLine;
        int endLine = translateEndLine;
        String text = getTextFromBeginLineAndEndLine(document, beginLine, beginLine);
        if (text.equals(portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY))) {
            endLine = beginLine;
        } else {
            text = getTextFromBeginLineAndEndLine(document, endLine, endLine);
            while ("".equals(text.trim())) {
                endLine--;
                if (endLine < 0) {
                    break;
                }
                text = getTextFromBeginLineAndEndLine(document, endLine, endLine);
            }
        }

        portingItem.put(SuggestionConstant.PORTING_ITEM_LOC_END_KEY, endLine + 1);
        handleCompareWithOldText(portingItem, document, beginLine, endLine);
    }

    private void handleSuggestionReplaced1500(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        if (translateBeginLine < MIN_LINE) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
            return;
        }
        String text = getTextFromBeginLineAndEndLine(
            document, translateBeginLine - MIN_LINE, translateBeginLine - MIN_LINE);
        int i = 1;
        while ("".equals(text.trim())) {
            i++;
            if (i > translateBeginLine) {
                break;
            }
            text = getTextFromBeginLineAndEndLine(document, translateBeginLine - i, translateBeginLine - i);
        }
        if (text.trim().toLowerCase(Locale.ROOT).contains("gfortran")) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        }
    }

    private void handleSuggestionReplaced2200(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        String text = getTextFromBeginLineAndEndLine(document, translateBeginLine - 1, translateBeginLine - 1);
        int i = 1;
        while ("".equals(text.trim())) {
            i++;
            if (i > translateBeginLine) {
                break;
            }
            text = getTextFromBeginLineAndEndLine(document, translateBeginLine - i, translateBeginLine - i);
        }
        if (!IF_DEFINED_X_86_64.equals(text.trim())) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
        }
    }

    private void handleSuggestionReplaced1600(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        String text = getTextFromBeginLineAndEndLine(document, translateBeginLine, translateBeginLine);
        if (!IF_DEFINED_X_86_64.equals(text.trim())) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
        }
    }

    /**
     * 当前editor鼠标选中的类型
     *
     * @param json   接口返回的json数据
     * @param editor the text editor
     * @return 返回SuggestionType对应的数字，0表示没有匹配的修改意见，即不需要修改
     */
    public int selectedSuggestionType(JSONObject json, Editor editor) {
        int offset = editor.getCaretModel().getCurrentCaret().getOffset(); // 鼠标当前所在编辑区的offset
        Object jsonArray = json.get(SuggestionConstant.PORTING_ITEMS_KEY);
        if (jsonArray instanceof JSONArray) {
            JSONArray portingItems = (JSONArray) jsonArray;
            for (int i = 0; i < portingItems.size(); i++) {
                JSONObject jsonObject = portingItems.getJSONObject(i);
                int beginLine = jsonObject.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY);
                int endLine = jsonObject.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
                int startOffset = editor.getDocument().getLineStartOffset(beginLine - 1);
                int endOffset = editor.getDocument().getLineEndOffset(endLine - 1);
                if (offset >= startOffset && offset <= endOffset) {
                    return jsonObject.getIntValue(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY);
                }
            }
        }
        return 0;
    }

    /**
     * 替换光标当前的修改意见
     *
     * @param json   接口返回的json格式数据
     * @param editor the text editor
     */
    public void handleSuggestReplaceOne(JSONObject json, Editor editor) {
        int currentOffset = editor.getCaretModel().getCurrentCaret().getOffset();
        Object jsonArray = json.get(SuggestionConstant.PORTING_ITEMS_KEY);
        if (jsonArray instanceof JSONArray) {
            JSONArray portingItems = (JSONArray) jsonArray;
            for (int i = 0; i < portingItems.size(); i++) {
                JSONObject portingItem = portingItems.getJSONObject(i);
                int beginLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY);
                int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
                // 获取需要修改在编辑文本起始位置和终止位置
                int startOffset = editor.getDocument().getLineStartOffset(beginLine - 1);
                int endOffset = editor.getDocument().getLineEndOffset(endLine - 1);
                // 是否是当前光标选中的区域
                boolean isArea = currentOffset >= startOffset && currentOffset <= endOffset;
                if (isArea) {
                    int suggestionType = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY);
                    handleSuggestReplace(portingItem, editor, suggestionType);
                    break;
                }
            }
        }
    }

    /**
     * 替换所有同类型的修改意见
     *
     * @param json   接口返回的json格式数据
     * @param editor the text editor
     * @param type   SuggestionType对应的数字
     */
    public void handleSuggestReplaceAllByType(JSONObject json, Editor editor, int type) {
        Object jsonArray = json.get(SuggestionConstant.PORTING_ITEMS_KEY);
        if (jsonArray instanceof JSONArray) {
            JSONArray portingItems = (JSONArray) jsonArray;
            for (int i = 0; i < portingItems.size(); i++) {
                JSONObject portingItem = portingItems.getJSONObject(i);
                if (portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY) != type
                    || portingItem.getBooleanValue(SuggestionConstant.PORTING_ITEM_REPLACED_KEY)) {
                    continue;
                }
                handleSuggestReplace(portingItem, editor, type);
            }
        }
    }

    /**
     * 将建议信息插入文件头
     *
     * @param portingItem   当前portingItem
     * @param editor        编辑区实例
     * @param second        第二行内容
     * @param isAddSuggestion 是否需要添加suggestion建议
     */
    private void handleSuggestionInsertToFileHead(JSONObject portingItem, Editor editor,
        String second, boolean isAddSuggestion) {
        portingItem.put("oldLineNum", editor.getDocument().getLineCount());
        StringBuilder suggestion = new StringBuilder();
        if (isAddSuggestion) {
            suggestion.append("//suggestion: ")
                .append(portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY))
                .append(LINE_END);
        }
        String headTarget = IF_DEFINED_AARCH_64 + LINE_END +
            second + LINE_END +
            suggestion.toString() + ENDIF + LINE_END;
        if (isInsertToFileHead(editor.getDocument(), IF_DEFINED_AARCH_64,
            second, ENDIF)) {
            InspectionProfileModifiableModelKt.modifyAndCommitProjectProfile(editor.getProject(),
                it -> it.setModified(true));
            return;
        }
        editor.getDocument().insertString(0, headTarget);
        final int lineNumber = editor.getDocument().getLineNumber(headTarget.length() - 1);
        setReplacedBackgroundColor(0, lineNumber, editor);
    }

    private void handleSuggestionType100(JSONObject portingItem, Editor editor) {
        JSONArray portingItems = EditorSourceFileHandle.getEditorSourceFileHandle().getPortingItems();
        boolean isAddSuggestion = false;
        for (int i = 0; i < portingItems.size(); i++) {
            JSONObject item = portingItems.getJSONObject(i);

            // 100类型添加strategy注释
            if (item.getIntValue(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY) == 100) {
                isAddSuggestion = true;
                break;
            }
        }
        handleSuggestionInsertToFileHead(portingItem, editor, "#include \"avx2neon.h\"", isAddSuggestion);
    }

    private void handleSuggestionType200(JSONObject portingItem, Editor editor) {
        handleSuggestionInsertToFileHead(portingItem, editor, "#include \"sse2neon.h\"", false);
    }

    private void handleSuggestionType300(JSONObject portingItem, Editor editor) {
        String[] suggestions = portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY)
            .split(SUGGESTION, -1);
        String target = "//" + OLD_TEXT + LINE_END
            + (suggestions.length > 1 ? suggestions[1] : suggestions[0]) + LINE_END;
        handleSuggestionTypeWithTarget(portingItem, editor, target, 300);
    }

    private void handleSuggestionType500And600(JSONObject portingItem, Editor editor, String lineHead, int type) {
        String target = IF_DEFINED_X_86_64 + LINE_END +
            OLD_TEXT + LINE_END + ELIF_DEFINED_AARCH_64 + LINE_END +
            lineHead + portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY) + LINE_END + ENDIF;
        handleSuggestionTypeWithTarget(portingItem, editor, target, type);
    }

    private void handleSuggestionType500(JSONObject portingItem, Editor editor) {
        handleSuggestionType500And600(portingItem, editor, "\t", 500);
    }

    private void handleSuggestionType600(JSONObject portingItem, Editor editor) {
        handleSuggestionType500And600(portingItem, editor, SUGGESTION1, 600);
    }

    private void handleSuggestionType700(JSONObject portingItem, Editor editor) {
        Object replacement = portingItem.get(SuggestionConstant.PORTING_ITEM_REPLACEMENT_KEY);
        String replacementStr = "";
        if (replacement instanceof JSONArray) {
            if (((JSONArray) replacement).size() > 0) {
                replacementStr = ((JSONArray) replacement).getString(0);
            }
        }
        handleSuggestionTypeWithTarget(portingItem, editor, replacementStr, 700);
    }

    private void handleSuggestionType1000(JSONObject portingItem, Editor editor) {
        int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
        Document document = editor.getDocument();
        if (document.getLineCount() < endLine) {
            return;
        }
        int startOffset = document.getLineStartOffset(endLine - 1); // 获取需要修改的最后一行在编辑文本起始位置
        int endOffset = document.getLineEndOffset(endLine - 1); // 获取需要修改在编辑文本终止位置
        TextRange textRange = new TextRange(startOffset, endOffset);
        String text = document.getText(textRange);
        String target = ELIF_DEFINED_AARCH_64 + LINE_END +
                SUGGESTION1 + portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY) + LINE_END + text;
        document.replaceString(startOffset, endOffset, target);
        final int lineNumber = document.getLineNumber(startOffset + target.length());
        setReplacedBackgroundColor(endLine - 1, lineNumber, editor);
    }

    private void handleSuggestionType1010(JSONObject portingItem, Editor editor) {
        int insertNo = portingItem.getIntValue(SuggestionConstant.INSERT_NO);
        Document document = editor.getDocument();
        if (document.getLineCount() < insertNo) {
            return;
        }
        int startOffset = document.getLineStartOffset(insertNo - 1); // 获取需要修改的最后一行在编辑文本起始位置
        int endOffset = document.getLineEndOffset(insertNo - 1); // 获取需要修改在编辑文本终止位置
        TextRange textRange = new TextRange(startOffset, endOffset);
        String text = document.getText(textRange);
        String lineHead = "";
        if (text.startsWith(SPACE)) {
            lineHead = SPACE;
        }
        String target = lineHead + ELIF_DEFINED_AARCH_64 + LINE_END +
            lineHead + SUGGESTION1 + portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY)
            + LINE_END + text;
        document.replaceString(startOffset, endOffset, target);
        final int lineNumber = document.getLineNumber(startOffset + target.length());
        setReplacedBackgroundColor(insertNo - 1, lineNumber, editor);
    }

    private void handleSuggestionType1100(JSONObject portingItem, Editor editor) {
        int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
        Document document = editor.getDocument();
        if (document.getLineCount() < endLine) {
            return;
        }
        document.replaceString(0, document.getTextLength(),
            portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY));
        setReplacedBackgroundColor(0, document.getLineCount() - 1, editor);
    }

    private void handleSuggestionType1300(JSONObject portingItem, Editor editor) {
        handleSuggestionInsertToFileHead(portingItem, editor, "#include \"KunpengTrans.h\"", false);
    }

    private void handleSuggestionType1400(JSONObject portingItem, Editor editor) {
        String target = portingItem.get(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY).toString();
        handleSuggestionTypeWithTarget(portingItem, editor, target, 1400);
    }

    private void handleSuggestionType1500(JSONObject portingItem, Editor editor) {
        String target = portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);
        int beginLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY);
        int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
        if (!target.contains("\t")) {
            target = SPACE + target;
        }
        String oldText = getTextFromBeginLineAndEndLine(editor.getDocument(), beginLine - 1, endLine - 1);
        String space = "";
        if (!oldText.contains("\t")) {
            space = SPACE;
        }
        String headLineStr = "#ifdef __GFORTRAN__" + LINE_END + target + LINE_END + "#else" + LINE_END + space;
        String endLineStr = LINE_END + ENDIF;
        int startOffset = editor.getDocument().getLineStartOffset(beginLine - 1);
        int endOffset = editor.getDocument().getLineEndOffset(endLine - 1);
        editor.getDocument().insertString(endOffset, endLineStr);
        editor.getDocument().insertString(startOffset, headLineStr);
        setReplacedBackgroundColor(editor.getDocument().getLineNumber(startOffset),
            editor.getDocument().getLineNumber(endOffset + endLineStr.length() + headLineStr.length()), editor);
    }

    private void handleSuggestionType2200(JSONObject portingItem, Editor editor) {
        int beginLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY);
        int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
        Document document = editor.getDocument();
        if (document.getLineCount() < endLine) {
            return;
        }

        int startOffset = document.getLineStartOffset(beginLine - 1); // 获取需要修改的最后一行在编辑文本起始位置
        int endOffset = document.getLineEndOffset(endLine - 1); // 获取需要修改在编辑文本终止位置
        String headLineStr = IF_DEFINED_X_86_64 + LINE_END;
        String description = portingItem.getString(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY);
        String strategy = portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);

        String endLineStr = LINE_END + ELIF_DEFINED_AARCH_64 + LINE_END +
            joinOldTextAndStrategyByLineHead("",
                "Description:" + description, "// ") + LINE_END +
            joinOldTextAndStrategyByLineHead(SUGGESTION, strategy, "// ") + LINE_END + ENDIF;

        // 先在endOffset插入再startOffset处插入，不然endOffset会变化
        document.insertString(endOffset, endLineStr);
        document.insertString(startOffset, headLineStr);

        setReplacedBackgroundColor(document.getLineNumber(startOffset),
            document.getLineNumber(endOffset + endLineStr.length() + headLineStr.length()), editor);
    }

    private void handleSuggestionType1600(JSONObject portingItem, Editor editor) {
        String target = IF_DEFINED_X_86_64 + LINE_END +
            SPACE + OLD_TEXT + LINE_END + ENDIF;
        handleSuggestionTypeWithTarget(portingItem, editor, target, 1600);
    }

    private void handleSuggestionTypeWithTarget(JSONObject portingItem, Editor editor, String target, int type) {
        int beginLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY);
        int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
        Document document = editor.getDocument();
        if (document.getLineCount() < endLine) {
            return;
        }
        int startOffset = document.getLineStartOffset(beginLine - 1); // 获取需要修改在编辑文本起始位置
        int endOffset = document.getLineEndOffset(endLine - 1); // 获取需要修改在编辑文本终止位置
        TextRange textRange = new TextRange(startOffset, endOffset);
        String oldText = document.getText(textRange);
        String newText = target;
        if (target.contains(OLD_TEXT)) {
            if (type == 1600 && oldText.contains(API_LINE_SEPARATOR)) {
                oldText = oldText.replaceAll(API_LINE_SEPARATOR, API_LINE_SEPARATOR + SPACE);
            }
            newText = target.replace(OLD_TEXT, oldText);
        }
        if (type == 700) {
            newText = oldText.replace(portingItem.getString(
                    SuggestionConstant.PORTING_ITEM_KEYWORD_KEY).trim(), target);
        }
        if (type == 1400 && oldText.startsWith("\n")) {
            startOffset++;
        }
        document.replaceString(startOffset, endOffset, newText);
        final int lineNumber = document.getLineNumber(startOffset + newText.length());
        setReplacedBackgroundColor(beginLine - 1, lineNumber, editor);
        portingItem.put(ORIGINAL_CONTENT, oldText);
    }

    /**
     * suggestion strategy字符处理
     *
     * @param oldText  原来的内容
     * @param strategy 建议内容
     * @param lineHead 行头注释符号
     * @return 组装strategy建议之后的内容
     */
    private String joinOldTextAndStrategyByLineHead(String oldText, String strategy, String lineHead) {
        char[] chars = strategy.trim().toCharArray();
        StringBuilder stringBuilder = new StringBuilder(lineHead);
        char lineFeedCh = 10; // Line Feed
        for (char ch : chars) {
            if (ch == 10) {
                stringBuilder.append(ch).append(lineHead);
                continue;
            }
            stringBuilder.append(ch);
        }
        if (oldText.trim().length() == 0) {
            return stringBuilder.toString();
        }
        return lineHead + oldText + lineFeedCh + stringBuilder.toString();
    }

    /**
     * 获取从beginline的起始位置到endline的终止位置的text，取值范围从0开始
     *
     * @param document  文件内容
     * @param beginLine 开始行
     * @param endLine   结束行
     * @return 从beginline的起始位置到endline的终止位置的内容
     */
    private String getTextFromBeginLineAndEndLine(Document document, int beginLine, int endLine) {
        int startOffset = document.getLineStartOffset(beginLine); // 获取在编辑文本起始位置
        int endOffset = document.getLineEndOffset(endLine); // 获取在编辑文本终止位置
        TextRange textRange = new TextRange(startOffset, endOffset);
        return document.getText(textRange);
    }

    /**
     * 为选择的行之间设置背景色
     *
     * @param startLine 开始行
     * @param endLine   结束行
     * @param editor    编辑区编辑实例
     */
    private void setReplacedBackgroundColor(int startLine, int endLine, Editor editor) {
        MarkupModel markupModel = editor.getMarkupModel();
        TextAttributes textMarker = new TextAttributes();
        textMarker.setBackgroundColor(SuggestionConstant.REPLACED_BACKGROUND_COLOR);
        for (int i = startLine; i <= endLine; i++) {
            markupModel.addLineHighlighter(i, 1000, textMarker);
        }
    }

    /**
     * 判断去掉行尾行头的空格的originalText是否以去掉行尾行头的空格的targetText开头，以及忽略空行
     *
     * @param originalText 源文件内容
     * @param targetText   目标文件内容
     * @return true:忽略行首行尾空格以及空行，两种文件内容一致
     */
    private boolean isStartWithIgnoreLineHeadAndLineEndSpaces(String originalText, String targetText) {
        String[] originalSplit = originalText.split("\\n", -1);
        String[] targetSplit = targetText.split("\\n", -1);
        int i = 0;
        boolean isSame = true;
        A:
        for (String original : originalSplit) {
            String originalTrim = original.trim();
            if ("".equals(originalTrim)) {
                continue;
            }
            if (i >= targetSplit.length) {
                break;
            }
            String targetTrim = targetSplit[i].trim();
            i++;
            while ("".equals(targetTrim)) {
                if (i >= targetSplit.length) {
                    break A;
                }
                targetTrim = targetSplit[i].trim();
                i++;
            }
            if (!originalTrim.equals(targetTrim)) {
                isSame = false;
                break;
            }
        }
        return isSame;
    }

    private void handleSuggestionType9999(JSONObject portingItem, Editor editor) {
        String suggestion = "__asm__ volatile(\"dmb sy\");";
        String target = "\t" + suggestion + LINE_END + OLD_TEXT;
        handleSuggestionTypeWithTarget(portingItem, editor, target, 9999);
    }

    private void handleSuggestionReplaced9999(JSONObject portingItem, Document document,
        Integer translateBeginLine, Integer translateEndLine) {
        String newSuggestion = getTextFromBeginLineAndEndLine(document,
            translateBeginLine - 1, translateEndLine - 1);
        String suggestion = "__asm__ volatile(\"dmb sy\");";
        if (newSuggestion.contains(suggestion)) {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, true);
        } else {
            portingItem.put(SuggestionConstant.PORTING_ITEM_REPLACED_KEY, false); // 设置已经被替换标志
        }
    }
}
