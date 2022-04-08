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

package com.huawei.kunpeng.hyper.tuner.setting.webservercert;

import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert.TuningWebServerCertificatePanel;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.panel.webservercert.WebServerCertificate;

import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * WebServerCertificate
 *
 * @since 2020-10-07
 */
public class TuningWebServerCertificate extends WebServerCertificate {
    private TuningWebServerCertificatePanel tuningWebServerCertificatePanel;

    /**
     * 创建面板
     *
     * @return tuningWebServerCertificatePanel
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        this.tuningWebServerCertificatePanel = new TuningWebServerCertificatePanel(null, null, false);
        return this.tuningWebServerCertificatePanel;
    }

    /**
     * 是否修改判断
     *
     * @return boolean
     */
    @Override
    public boolean isModified() {
        return this.tuningWebServerCertificatePanel != null && this.tuningWebServerCertificatePanel.isModified();
    }

    /**
     * 点击Apply事件
     *
     * @throws com.intellij.openapi.options.ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {
        if (this.tuningWebServerCertificatePanel != null) {
            this.tuningWebServerCertificatePanel.apply();
            Logger.info("Change WebServerCertificate parameter");
        }
    }

    /**
     * 点击reset按钮事件
     */
    @Override
    public void reset() {
        if (this.tuningWebServerCertificatePanel != null) {
            this.tuningWebServerCertificatePanel.reset();
        }
    }

    /**
     * disposeUIResources方法
     */
    @Override
    public void disposeUIResources() {
        this.tuningWebServerCertificatePanel = null;
    }
}