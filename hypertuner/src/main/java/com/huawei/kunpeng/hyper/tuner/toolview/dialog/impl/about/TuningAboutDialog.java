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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.about;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.AboutDialog;

import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.Nullable;

/**
 * The class PortingAboutDialog: 弹出关于版本的Dialog
 *
 * @since v1.0
 */
public class TuningAboutDialog extends AboutDialog {
    public TuningAboutDialog(@Nullable Project project) {
        super(project);
    }

    @Override
    protected String getProductInfo() {
        return I18NServer.toLocale("plugins_hyper_tuner_about_product_info");
    }

    @Override
    protected String getProductTitle() {
        return I18NServer.toLocale("plugins_hyper_tuner_about_product_title");
    }

    @Override
    protected String getProductVersion() {
        return I18NServer.toLocale("plugins_hyper_tuner_about_product_version");
    }

    @Override
    protected String getProductReleaseTime() {
        return I18NServer.toLocale("plugins_hyper_tuner_about_product_release_time");
    }
}
