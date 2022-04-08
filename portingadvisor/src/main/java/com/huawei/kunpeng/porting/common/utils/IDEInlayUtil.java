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

package com.huawei.kunpeng.porting.common.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.FontPreferences;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.impl.ComplementaryFontsRegistry;
import com.intellij.openapi.editor.impl.FontInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * 为编辑区文本添加inline信息
 *
 * @since 2020-11-23
 */
public class IDEInlayUtil {
    private static final TextAttributesKey PORTING_INLINED_VALUES_DESCRIPTION_KEY = TextAttributesKey
            .createTextAttributesKey("PORTING_INLINED_VALUES_DESCRIPTION");

    /**
     * 创建inlay
     *
     * @param project 当前项目project
     * @param file 当前打开的文件
     * @param offset 添加inlayText的起始offset
     * @param inlayText inlayText
     */
    public static void createInlay(@NotNull Project project, @NotNull VirtualFile file, int offset, String inlayText) {
        UIUtil.invokeLaterIfNeeded(() -> {
            FileEditor fileEditor = FileEditorManager.getInstance(project).getSelectedEditor(file);
            if (fileEditor instanceof TextEditor) {
                Editor editor = ((TextEditor)fileEditor).getEditor();
                editor.getInlayModel().addAfterLineEndElement(offset, false, new IDERenderer(inlayText));
            }
        });
    }

    /**
     * 清除所有Inlays
     *
     * @param project 当前项目project
     */
    public static void clearInlays(@NotNull Project project) {
        UIUtil.invokeLaterIfNeeded(() -> {
            FileEditor[] editors = FileEditorManager.getInstance(project).getAllEditors();
            for (FileEditor fEditor : editors) {
                if (fEditor instanceof TextEditor) {
                    Editor editor = ((TextEditor)fEditor).getEditor();
                    editor.getInlayModel().getAfterLineEndElementsInRange(0, editor.getDocument().getTextLength(),
                            IDERenderer.class).forEach(Disposer::dispose);
                }
            }
        });
    }

    private static class IDERenderer implements EditorCustomElementRenderer {
        private final String myText;

        IDERenderer(String myText) {
            this.myText = myText;
        }

        private static FontInfo getFontInfo(@NotNull Editor editor) {
            EditorColorsScheme colorsScheme = editor.getColorsScheme();
            FontPreferences fontPreferences = colorsScheme.getFontPreferences();
            TextAttributes attributes = PORTING_INLINED_VALUES_DESCRIPTION_KEY.getDefaultAttributes();
            int fontStyle = attributes == null ? Font.PLAIN : attributes.getFontType();
            return ComplementaryFontsRegistry.getFontAbleToDisplay('a', fontStyle, fontPreferences,
                    FontInfo.getFontRenderContext(editor.getContentComponent()));
        }

        @Override
        public int calcWidthInPixels(@NotNull Inlay inlay) {
            FontInfo fontInfo = getFontInfo(inlay.getEditor());
            FontMetrics metrics = fontInfo.fontMetrics();
            if (metrics == null) {
                return 0;
            }
            return metrics.stringWidth(myText);
        }

        @Override
        public void paint(@NotNull Inlay inlay, @NotNull Graphics graphics,
                            @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
            Editor editor = inlay.getEditor();
            TextAttributes attributes = PORTING_INLINED_VALUES_DESCRIPTION_KEY.getDefaultAttributes();
            if (attributes == null) {
                return;
            }
            Color fgColor = attributes.getForegroundColor();
            if (fgColor == null) {
                return;
            }
            graphics.setColor(fgColor);
            FontInfo fontInfo = getFontInfo(editor);
            graphics.setFont(fontInfo.getFont());
            graphics.drawString(myText, new Double(targetRegion.getX()).intValue(),
                    new Double(targetRegion.getY()).intValue() +
                            ((new Double(inlay.getBounds().getHeight()).intValue() + fontInfo.getSize()) / 2));
        }
    }
}
