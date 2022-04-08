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

import com.huawei.kunpeng.porting.common.utils.IntellijAllIcons;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.SeveritiesProvider;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * 增加一个PORTING类型的波浪线标记Severity
 *
 * @since 2020-11-18
 */
public class PortingSeveritiesProvider extends SeveritiesProvider {
    private static final TextAttributesKey PORTING_KEY = TextAttributesKey.createTextAttributesKey("PORTING");
    private static final HighlightSeverity PORTING = new HighlightSeverity("PORTING",
        HighlightSeverity.INFORMATION.myVal + 10);

    @NotNull
    @Override
    public List<HighlightInfoType> getSeveritiesHighlightInfoTypes() {
        final class Porting extends HighlightInfoType.HighlightInfoTypeImpl implements HighlightInfoType.Iconable {
            private Porting(@NotNull HighlightSeverity severity, @NotNull TextAttributesKey attributesKey) {
                super(severity, attributesKey);
            }

            @NotNull
            @Override
            public Icon getIcon() {
                return IntellijAllIcons.ReportOperation.REPORT_PROBLEM;
            }
        }

        return Collections.singletonList(new Porting(PORTING, PORTING_KEY));
    }

    @Override
    public boolean isGotoBySeverityEnabled(HighlightSeverity minSeverity) {
        return minSeverity != PORTING;
    }
}
