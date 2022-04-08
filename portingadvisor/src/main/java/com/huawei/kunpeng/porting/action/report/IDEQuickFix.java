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
import com.huawei.kunpeng.porting.action.report.handle.SuggestHandle;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.DocumentUtil;
import com.intellij.util.IncorrectOperationException;

import org.jetbrains.annotations.NotNull;

/**
 * 对需要修改建议的地方进行quickfix操作
 *
 * @since 2020-11-13
 */
public class IDEQuickFix extends LocalQuickFixOnPsiElement implements IntentionAction {
    private final String textValue;
    private final boolean isAllReplace;

    /**
     * 构造函数
     *
     * @param element    element
     * @param textValue  textValue
     * @param isAllReplace isAllReplace
     */
    protected IDEQuickFix(@NotNull PsiElement element, @NotNull String textValue, boolean isAllReplace) {
        super(element);
        this.textValue = textValue;
        this.isAllReplace = isAllReplace;
    }


    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return EditorSourceFileHandle.portingJson != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (isAllReplace) {
            int type = SuggestHandle.getSuggestHandle()
                .selectedSuggestionType(EditorSourceFileHandle.portingJson, editor);
            // 设置Document的批量更新
            DocumentUtil.executeInBulk(editor.getDocument(), true, () -> {
                SuggestHandle.getSuggestHandle()
                    .handleSuggestReplaceAllByType(EditorSourceFileHandle.portingJson, editor, type);
            });
        } else {
            SuggestHandle.getSuggestHandle()
                .handleSuggestReplaceOne(EditorSourceFileHandle.portingJson, editor);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @NotNull
    @Override
    @IntentionName
    public String getText() {
        return textValue;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file,
        @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
    }

    @NotNull
    @Override
    @IntentionFamilyName
    public String getFamilyName() {
        return getText();
    }
}
