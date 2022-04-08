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

package com.huawei.kunpeng.porting.listener;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.porting.action.report.handle.EditorSourceFileHandle;
import com.huawei.kunpeng.porting.action.report.handle.SuggestHandle;
import com.huawei.kunpeng.porting.common.constant.SuggestionConstant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.event.DocumentEventImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.diff.FilesTooBigForDiffException;

import org.jetbrains.annotations.NotNull;

/**
 * IDE 源码文档监听器
 * 在需要监听的文档注册自定义文档监听器
 * <code>document.addDocumentListener(IDEDocumentListener.getInstance());</code>
 *
 * @since 2020-11-3
 */
public class IDEDocumentListener implements DocumentListener {
    private static IDEDocumentListener instance = new IDEDocumentListener();

    private static final char LINE_END = 10;

    private IDEDocumentListener() {
    }

    /**
     * 获取自定义文档监听器实例
     *
     * @return instance 监听器实例
     */
    public static IDEDocumentListener getInstance() {
        return instance;
    }

    /**
     * 当源码文档内容变化前的时刻 执行一定动作
     *
     * @param event 文档事件
     */
    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        return;
    }

    /**
     * 批量更新完成之后处理portingItems
     *
     * @param document 正在监听的文档
     */
    @Override
    public void bulkUpdateFinished(@NotNull Document document) {
        JSONArray portingItems = EditorSourceFileHandle.getEditorSourceFileHandle().getPortingItems();
        for (int i = 0; i < portingItems.size(); i++) {
            JSONObject portingItem = portingItems.getJSONObject(i);
            int translateBeginLine = portingItem.getInteger(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY) - 1;
            int translateEndLine = portingItem.getInteger(SuggestionConstant.PORTING_ITEM_LOC_END_KEY) - 1;
            // 检测porting item是否按照建议进行替换
            SuggestHandle.getSuggestHandle().handleSuggestionReplaced(portingItem, document,
                translateBeginLine, translateEndLine);
        }
    }

    /**
     * 当源码文档内容变化时 监听对应标记行信息的变动
     *
     * @param event 文档事件
     */
    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        if (EditorSourceFileHandle.portingJson == null) {
            return;
        }
        JSONArray portingItems = EditorSourceFileHandle.getEditorSourceFileHandle().getPortingItems();
        for (int i = 0; i < portingItems.size(); i++) {
            JSONObject portingItem = portingItems.getJSONObject(i);
            changePorting(event, portingItem);
        }
    }

    private void changePorting(DocumentEvent event, JSONObject portingItem) {
        int beginLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY);
        int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);

        if (event instanceof DocumentEventImpl) {
            DocumentEventImpl documentEvent = (DocumentEventImpl) event;
            try {
                // 对porting item中每个开始和结束行都进行监听
                int translateBeginLine = documentEvent.translateLineViaDiff(beginLine - 1);
                int translateEndLine = documentEvent.translateLineViaDiff(endLine - 1);

                // 行头换行translateLine不变化处理
                if (translateBeginLine == beginLine - 1) {
                    translateBeginLine = handleLineHead(event, translateBeginLine);
                }
                if (translateEndLine == endLine - 1) {
                    translateEndLine = handleLineHead(event, translateEndLine);
                }
                // 1400类型对endLine进行重新跟踪处理
                if (portingItem.getInteger(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY) == 1400) {
                    translateEndLine = handleEndLine(translateEndLine, event, translateBeginLine);
                }
                portingItem.put(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY, translateBeginLine + 1);
                portingItem.put(SuggestionConstant.PORTING_ITEM_LOC_END_KEY, translateEndLine + 1);
                handleInsertNo(portingItem, documentEvent);

                // 非批量修改触发检测porting item是否按照建议进行替换
                if (!documentEvent.getDocument().isInBulkUpdate()) {
                    SuggestHandle.getSuggestHandle().handleSuggestionReplaced(portingItem, documentEvent.getDocument(),
                        translateBeginLine, translateEndLine);
                }
            } catch (FilesTooBigForDiffException e) {
                Logger.error("Can not calculate diff. File is too big and there are too many changes.");
            }
        }
    }

    private void handleInsertNo(JSONObject portingItem, DocumentEventImpl event) {
        int insertNo = 0;
        if (portingItem.getInteger(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY)
            == SuggestionConstant.SOURCE_CODE_CHECK_OPTIMIZATION_TYPE) {
            insertNo = portingItem.getIntValue(SuggestionConstant.INSERT_NO);
        }
        if (insertNo != 0) {
            int translateInsertNo = 0;
            try {
                translateInsertNo = event.translateLineViaDiff(insertNo - 1);
            } catch (FilesTooBigForDiffException e) {
                Logger.error("Can not calculate diff. File is too big and there are too many changes.");
            }
            portingItem.put(SuggestionConstant.INSERT_NO, translateInsertNo + 1);
        }
    }

    private int handleEndLine(int translateEndLine, DocumentEvent event, int translateBeginLine) {
        int lineNumber = event.getDocument().getLineNumber(event.getOffset());
        int endLine = translateEndLine;
        if (lineNumber >= translateBeginLine && lineNumber <= translateEndLine
                && translateBeginLine == translateEndLine) {
                int lineSeparatorDiff = lineSeparatorDiff(event);
                if (lineSeparatorDiff > 0) {
                    endLine = endLine + lineSeparatorDiff;
                }
        }
        return endLine;
    }

    /**
     * 处理问题行，行头换行不变化
     *
     * @param event         文本事件
     * @param translateLine 变化的行
     * @return 真实行号
     */
    private int handleLineHead(DocumentEvent event, int translateLine) {
        int lineNumber = event.getDocument().getLineNumber(event.getOffset());
        int lineSeparatorDiff = lineSeparatorDiff(event);
        if (lineSeparatorDiff >= 0 && translateLine == lineNumber) {
            int lineStartOffset = event.getDocument().getLineStartOffset(lineNumber);
            String text = event.getDocument().getText(new TextRange(lineStartOffset, event.getOffset()));
            if ("".equals(text.trim())) {
                return translateLine + lineSeparatorDiff;
            }
        }
        if (lineSeparatorDiff < 0 && translateLine + lineSeparatorDiff == lineNumber) {
            return translateLine + lineSeparatorDiff;
        }
        return translateLine;
    }

    /**
     * 比较原来的和新添加的字段的换行数量，返回负值表示换行减少，正值换行增加
     *
     * @param event 文本事件
     * @return 换行符的增减数量
     */
    private int lineSeparatorDiff(DocumentEvent event) {
        char[] newChars = event.getNewFragment().toString().toCharArray();
        char[] oldChars = event.getOldFragment().toString().toCharArray();
        int lineSeparator = 0;
        for (char character : newChars) {
            // 换行符的ASCII码值为10
            if (character == LINE_END) {
                lineSeparator++;
            }
        }
        for (char character : oldChars) {
            // 换行符的ASCII码值为10
            if (character == LINE_END) {
                lineSeparator--;
            }
        }
        return lineSeparator;
    }
}
