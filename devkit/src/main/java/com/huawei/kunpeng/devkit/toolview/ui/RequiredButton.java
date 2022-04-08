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

package com.huawei.kunpeng.devkit.toolview.ui;

import com.huawei.kunpeng.devkit.MarketPlaceManager;
import com.huawei.kunpeng.devkit.common.utils.CommonUtil;

import com.intellij.openapi.extensions.PluginId;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * RequiredButton
 *
 * @since 2021-05-18
 */
public class RequiredButton extends GreenButton {
    private final PluginId pluginId;
    private RequiredType requiredType;

    /**
     * RequiredButton
     *
     * @param requiredType requiredType
     * @param pluginId     pluginId
     */
    public RequiredButton(RequiredType requiredType, PluginId pluginId) {
        super(requiredType.text);
        this.pluginId = pluginId;
        this.setRequiredType(requiredType);
        this.addMouseListener(new MouseAdapter() {
            /**
             * 点击事件
             *
             * @param event 点击事件
             */
            @Override
            public void mouseClicked(MouseEvent event) {
                super.mouseClicked(event);
                if (event.getButton() == 1) {
                    RequiredButton.this.action();
                }
            }
        });
    }

    /**
     * getTypeByPluginId
     *
     * @param pluginId PluginId
     * @return RequiredType
     */
    public static RequiredType getTypeByPluginId(PluginId pluginId) {
        if (MarketPlaceManager.needRestart(pluginId)) {
            return RequiredType.RESTART;
        } else if (!MarketPlaceManager.isInstalled(pluginId)) {
            return RequiredType.INSTALL;
        } else {
            return MarketPlaceManager.isEnable(pluginId)
                    && MarketPlaceManager.hasNewerVersion(pluginId) ? RequiredType.UPDATE : RequiredType.NONE;
        }
    }

    private void action() {
        switch (this.requiredType) {
            case NONE:
            default:
                break;
            case INSTALL:
            case UPDATE:
                MarketPlaceManager.installPlugin(new PluginId[]{pluginId});
                break;
            case RESTART:
                CommonUtil.restartIDE();
        }
    }

    public void setRequiredType(RequiredType requiredType) {
        this.requiredType = requiredType;
        this.setText(requiredType.text);
        if (requiredType == RequiredType.NONE) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
            if (requiredType != RequiredType.UPDATE) {
                this.setToolTipText("");
            }
        }
    }

    /**
     * RequiredType 枚举
     */
    public enum RequiredType {
        NONE(""),
        INSTALL("Install"),
        UPDATE("Update"),
        RESTART("Restart IDE");

        private final String text;

        RequiredType(String text) {
            this.text = text;
        }
    }
}

