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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.FileUtil;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;
import com.twelvemonkeys.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

/**
 * Tuning 文件重命名Panel 继承类
 * 相比父类，修改了面板 fileName 字段的校验规则，新的正则表达式更加复杂，校验更严苛。
 *
 * @since 2021-07-09
 */
public class TuningFileSaveAsPanel extends FileSaveAsPanel {
    /**
     * 父类字段值 新文件名
     */
    private final JTextField superFileName;

    public TuningFileSaveAsPanel(ToolWindow toolWindow, String defaultNewName) {
        super(toolWindow, defaultNewName);
        superFileName = super.getFileName();
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();
        if (StringUtil.isEmpty(superFileName.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_required_tip"), superFileName));
        }
        // 符合该正则表达式的为合法文件名
        String regex = "[^\\s\\\\/:*?\"<>|](\\x20|[^\\s\\\\/:*?\"<>|])*[^\\s\\\\/:*?\"<>|.]$";
        boolean fileNameValid = superFileName.getText().matches(regex);
        if (FileUtil.isContainChinese(superFileName.getText()) || !fileNameValid) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_file_name_illegal_tip"), superFileName));
        }
        return result;
    }
}
