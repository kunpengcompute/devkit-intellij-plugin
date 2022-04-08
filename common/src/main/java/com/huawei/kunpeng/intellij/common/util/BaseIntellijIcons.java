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

package com.huawei.kunpeng.intellij.common.util;

import com.intellij.ui.IconManager;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * 图片加载类
 *
 * @since 2021-04-07
 */
public class BaseIntellijIcons {
    /**
     * Icon
     *
     * @param path path
     * @return Icon
     */
    @NotNull
    public static Icon load(@NotNull String path) {
        return IconManager.getInstance().getIcon(path, BaseIntellijIcons.class);
    }

    /**
     * 设置相关图片
     */
    public static final class Settings {
        /**
         * 未登录时 小人图片
         */
        @NotNull
        public static final Icon LOGIN = load("/assets/img/lefttree/login.svg");

        /**
         * server 图片
         */
        @NotNull
        public static final Icon SERVER = load("/assets/img/lefttree/server.svg");

        /**
         * cert tip images
         */
        @NotNull
        public static final Icon CERT_TIPS = load("/assets/img/settings/cert_tip.png");

        /**
         * 标志信息。
         */
        public static final Icon LOGO = load("/assets/img/logo.png");
    }
}
