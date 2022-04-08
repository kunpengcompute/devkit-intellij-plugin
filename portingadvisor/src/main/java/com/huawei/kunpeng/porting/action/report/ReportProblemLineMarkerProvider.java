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

package com.huawei.kunpeng.porting.action.report;

import com.huawei.kunpeng.porting.action.report.handle.EditorSourceFileHandle;
import com.huawei.kunpeng.porting.common.constant.SuggestionConstant;
import com.huawei.kunpeng.porting.common.utils.IntellijAllIcons;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.codeInsight.daemon.GutterName;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.FunctionUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 编辑区添加line mark
 *
 * @since 2020-11-13
 */
public class ReportProblemLineMarkerProvider extends LineMarkerProviderDescriptor {
    private final Option reportProblem = new Option("report.problem", "report.problem",
        IntellijAllIcons.ReportOperation.REPORT_PROBLEM);

    @Nullable("null means disabled")
    @Override
    @GutterName
    public String getName() {
        return "report problem";
    }

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo<?> result = null;
        return result;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements,
        @NotNull Collection<? super LineMarkerInfo<?>> result) {
        JSONObject json = EditorSourceFileHandle.portingJson;
        if (json == null) {
            return;
        }
        Set<Integer> set = new HashSet<>();
        for (PsiElement element : elements) {
            PsiFile containingFile = element.getContainingFile();
            final VirtualFile vFile = containingFile.getVirtualFile();
            if (vFile instanceof VirtualFileWindow) {
                continue;
            }
            final Document document = FileDocumentManager.getInstance().getDocument(vFile);
            if (document == null) {
                continue;
            }
            collectLineMarkers(document, element, result, set);
        }
    }

    private void collectLineMarkers(Document document, PsiElement element,
        Collection<? super LineMarkerInfo<?>> result, Set<Integer> set) {
        // 设置line mark信息
        JSONArray portingItems = EditorSourceFileHandle.getEditorSourceFileHandle().getPortingItems();
        for (int i = 0; i < portingItems.size(); i++) {
            JSONObject portingItem = portingItems.getJSONObject(i);
            if (portingItem.getBooleanValue(SuggestionConstant.PORTING_ITEM_REPLACED_KEY)) {
                continue;
            }
            int beginLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY);
            int endLine = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY);
            if (document.getLineCount() < endLine) {
                continue;
            }
            int startOffset = document.getLineStartOffset(beginLine - 1); // 获取需要修改在编辑文本起始位置
            int endOffset = document.getLineEndOffset(endLine - 1); // 获取需要修改在编辑文本终止位置
            TextRange textRange = new TextRange(startOffset, endOffset);
            if (set.contains(startOffset)) {
                // 去重
                continue;
            }
            set.add(startOffset);
            String description = "Description:"
                + portingItem.getString(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY)
                + System.lineSeparator()
                + "Suggestion:"
                + portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);
            result.add(new LineMarkerInfo<>(element, textRange, reportProblem.getIcon(),
                FunctionUtil.constant(description), null, GutterIconRenderer.Alignment.LEFT));
        }
    }
}
