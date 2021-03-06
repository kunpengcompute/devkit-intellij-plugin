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

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.porting.action.report.handle.EditorSourceFileHandle;
import com.huawei.kunpeng.porting.common.constant.SuggestionConstant;
import com.huawei.kunpeng.porting.common.utils.IDEInlayUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

/**
 * ????????????????????????????????????????????????????????????
 *
 * @since 2020-11-13
 */
public class PortingInspectionAction extends LocalInspectionTool {
    /**
     * PortingInspection ???shortname ??? xml?????????shortname????????????
     */
    public static final String SHORT_NAME = "portingInspection";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        PsiFile file = holder.getFile();
        final VirtualFile vFile = file.getVirtualFile();
        if (vFile instanceof VirtualFileWindow) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }
        final Document document = FileDocumentManager.getInstance().getDocument(vFile);
        if (document == null) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }
        return new ModifiersVisitor(holder, document);
    }

    private class ModifiersVisitor extends PsiElementVisitor {
        private ProblemsHolder holder;
        private Document document;
        private Editor editor;

        /**
         * ????????????
         *
         * @param holder   holder
         * @param document document
         */
        ModifiersVisitor(ProblemsHolder holder, Document document) {
            this.holder = holder;
            this.document = document;
            FileEditorManager projectFileEditorManager = IDEFileEditorManager.getInstance()
                .getProjectFileEditorManager();
            if (projectFileEditorManager instanceof FileEditorManagerImpl) {
                this.editor = ((FileEditorManagerImpl) projectFileEditorManager).getSelectedTextEditor(true);
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (editor != null) {
                        removeAllBackgroundColor(editor);
                        removeAllInlays(editor.getProject());
                    }
                });
            }
        }

        /**
         * ??????PsiElementVisitor??????
         * ??????????????????????????????
         *
         * @param element PsiElement
         */
        @Override
        public void visitElement(@NotNull PsiElement element) {
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
                int startOffset = document.getLineStartOffset(beginLine - 1);
                int endOffset = document.getLineEndOffset(endLine - 1);
                String keyword = portingItem.getString(SuggestionConstant.PORTING_ITEM_KEYWORD_KEY);

                TextRange problemTextRange = getProblemTextRange(startOffset, endOffset, keyword);
                int suggestionType = portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY);
                // ?????????????????????????????????
                specialItemHandle(portingItem, suggestionType);
                // ???????????????????????????ProblemsHolder???
                String suggestion = createSuggestion(portingItem, suggestionType);
                if (checkProblem(suggestion, problemTextRange)) {
                    continue;
                }
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (editor != null) {
                        setReplacedBackgroundColor(beginLine - 1, endLine - 1, editor);
                        setInlay(beginLine - 1, editor, portingItem);
                    }
                });
                ArrayList<LocalQuickFix> fixes = new ArrayList<>();
                if (SuggestionConstant.SUGGEST_REPLACE_TYPE.contains(suggestionType)) {
                    if (suggestionType != SuggestionConstant.NO_SINGLE_QUICKFIX_TYPE) {
                        fixes.add(new IDEQuickFix(element,
                            I18NServer.toLocale("plugins_porting_inspection_quickfix_first_name"), false));
                    }
                    fixes.add(new IDEQuickFix(element,
                        I18NServer.toLocale("plugins_porting_inspection_quickfix_second_name"), true));
                } else {
                    portingItem.put("originalContent", document.getText(new TextRange(startOffset, endOffset)));
                }
                holder.registerProblem(element, // PsiElement???intellij??????text????????????????????????
                    suggestion,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    problemTextRange,
                    fixes.toArray(LocalQuickFix.EMPTY_ARRAY));
            }
        }

        /**
         * ?????????beginline???endline??????????????????????????????????????????range???
         * ????????????startOffset???endOffset???range
         *
         * @param startOffset beginLine???startOffset
         * @param endOffset   endLine???endOffset
         * @param keyword     ???????????????????????????
         * @return TextRange
         */
        private TextRange getProblemTextRange(int startOffset, int endOffset, String keyword) {
            TextRange textRange = new TextRange(startOffset, endOffset);
            if (!StringUtil.stringIsEmpty(keyword) && keyword.trim().length() > 0) {
                int indexOf = document.getText(textRange)
                    .toLowerCase(Locale.ENGLISH).indexOf(keyword.trim().toLowerCase(Locale.ENGLISH));
                if (indexOf > -1) {
                    textRange = new TextRange(textRange.getStartOffset() + indexOf,
                        textRange.getStartOffset() + indexOf + keyword.trim().length());
                }
            }
            return textRange;
        }

        /**
         * ??????problem????????????????????????
         *
         * @param description ????????????
         * @param textRange   ???????????????????????????textRange
         * @return true or false
         */
        private boolean checkProblem(String description, TextRange textRange) {
            List<ProblemDescriptor> results = holder.getResults();
            for (ProblemDescriptor problemDescriptor : results) {
                // ????????????????????????????????????????????????????????????problem
                if (problemDescriptor.getDescriptionTemplate().equals(description)
                    && problemDescriptor.getTextRangeInElement()
                    .equalsToRange(textRange.getStartOffset(), textRange.getEndOffset())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * ???????????????????????????????????????????????????
         *
         * @param startLine startLine
         * @param endLine   endLine
         * @param editor    editor
         */
        private void setReplacedBackgroundColor(int startLine, int endLine, Editor editor) {
            MarkupModel markupModel = editor.getMarkupModel();
            TextAttributes textMarker = new TextAttributes();
            textMarker.setBackgroundColor(SuggestionConstant.SUGGESTION_BACKGROUND_COLOR);
            for (int i = startLine; i <= endLine; i++) {
                markupModel.addLineHighlighter(i, 1001, textMarker);
            }
        }

        /**
         * ????????????Layer == 1001??????
         *
         * @param editor editor
         */
        private void removeAllBackgroundColor(Editor editor) {
            MarkupModel markupModel = editor.getMarkupModel();
            RangeHighlighter[] allHighlighters = markupModel.getAllHighlighters();
            for (RangeHighlighter rangeHighlighter : allHighlighters) {
                if (rangeHighlighter.getLayer() == 1001) {
                    markupModel.removeHighlighter(rangeHighlighter);
                }
            }
        }

        /**
         * ????????????????????????
         *
         * @param beginLine   ?????????
         * @param editor      editor
         * @param portingItem portingItem
         */
        private void setInlay(int beginLine, Editor editor, JSONObject portingItem) {
            String inlayText = portingItem.getString(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY);
            if (portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY) == 2200) {
                inlayText = inlayText.split("\\.")[0] + ".";
            }
            VirtualFile vFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
            int lineEndOffset = editor.getDocument().getLineEndOffset(beginLine);
            IDEInlayUtil.createInlay(editor.getProject(), vFile, lineEndOffset, "  " + inlayText);
        }

        private void removeAllInlays(Project project) {
            IDEInlayUtil.clearInlays(project);
        }
    }

    private void specialItemHandle(JSONObject portingItem, int suggestionType) {
        // 2500??????Suggestion???????????????????????????????????????
        if (suggestionType == SuggestionConstant.BYTE_ALIGN_NO_QUICK_FIX_SUGGESTION_TYPE) {
            String regex = "in line";
            String suggestion = portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);
            String[] suggestions = suggestion.split(regex);
            suggestion =
                suggestions[0] + regex + " "
                    + portingItem.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY) + ".";
            portingItem.put(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY, suggestion);
        }
    }

    private String createSuggestion(JSONObject portingItem, int suggestionType) {
        String description = "";
        if (suggestionType != SuggestionConstant.ENHANCED_NO_QUICK_FIX_SUGGESTION_TYPE
            && suggestionType != SuggestionConstant.ENHANCED_QUICK_FIX_SUGGESTION_TYPE) {
            description = "Description:"
                + portingItem.getString(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY)
                + System.lineSeparator();
        }
        return description + "Suggestion:"
            + portingItem.getString(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY);
    }
}
