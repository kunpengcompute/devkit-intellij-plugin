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

package com.huawei.kunpeng.intellij.ui.panel;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.AboutDialog;

import javax.swing.*;

/**
 * The class BaseAboutPanel
 *
 * @since v1.0
 */
public class BaseAboutPanel {
    /**
     * 主面板
     */
    private JPanel mainPanel;

    /**
     * 版本号
     */
    private JLabel versionLabel;

    /**
     * 发行时间
     */
    private JLabel serverVersionLabel;

    /**
     * 版权
     */
    private JLabel copyRightLabel;
    private JLabel imgLabel;

    /**
     * 版本号
     */
    private String productVersion;

    /**
     * 发布时间
     */
    private String productServerVersion;

    private static final String PATH = "/assets/img/common/icon_info.png";

    public BaseAboutPanel(String productVersion, String productServerVersion) {
        this.productVersion = productVersion;
        this.productServerVersion = productServerVersion;
        this.imgLabel.setIcon(new ImageIcon(AboutDialog.class.getResource(PATH)));
    }

    /**
     * 获取主面板
     *
     * @return JPanel
     */
    public JPanel getComponent() {
        versionLabel.setText(productVersion);
        if(productServerVersion==null){
            serverVersionLabel.setVisible(false);
        }
        serverVersionLabel.setText(productServerVersion);
        copyRightLabel.setText(CommonI18NServer.toLocale("common_about_product_copyright", "©"));
        return mainPanel;
    }
}
