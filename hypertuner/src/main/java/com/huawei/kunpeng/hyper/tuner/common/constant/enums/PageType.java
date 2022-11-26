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

package com.huawei.kunpeng.hyper.tuner.common.constant.enums;

import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.*;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;

import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 页面类型
 *
 * @since 2020-11-16
 */
public enum PageType {
    NULL("null") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 代理首页
     */
    PROXY_INDEX("proxy_index") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new IDELoginEditor(file));
        }
    },

    /**
     * 免费试用
     */
    FREE_TRIAL("free_trial"){
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new FreeTrialEditor(file));
        }
    },

    DEPLOY_SERVER("deploy_server"){
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new DeployServerEditor(file));
        }
    },

    /**
     * 卸载工具
     */
    UNINSTALL_HYPER_TUNER("uninstall_hyper_tuner") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new UninstallEditor(file));
        }
    },

    /**
     * 升级服务器
     */
    UPGRADE_SERVER("upgrade_server"){
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new UpgradeServerEditor(file));
        }
    },

    /**
     * 配置服务器页面
     */
     CONFIGURE_SERVER("configure_server") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ConfigureServerEditor(file));
        }
    },

    /**
     * 配置指引页面
     */
    CONFIGURE_GUIDE("configure_guide") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ConfigGuideEditor(file));
        }
    },

    /**
     * 错误指示页面
     */
    ERROR_INSTRUCTION("error_instruction") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ErrorInstructionEditor(file));
        }
    };

    private final String type;

    PageType(String type) {
        this.type = type;
    }

    /**
     * 通过延伸信息value获取PageType类的一个枚举实例
     *
     * @param value 值
     * @return String
     */
    public static PageType getStatusByValue(String value) {
        for (PageType pageType : PageType.values()) {
            if (pageType.value().equals(value)) {
                return pageType;
            }
        }

        return PageType.NULL;
    }

    /**
     * 抽象函数 获取WebFileEditor
     *
     * @param file VirtualFile
     * @return WebFileEditor
     */
    public abstract Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file);

    /**
     * 获取函数名
     *
     * @return String
     */
    public String value() {
        return type;
    }
}
