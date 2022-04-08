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

package com.huawei.kunpeng.hyper.tuner.listener;

import com.huawei.kunpeng.hyper.tuner.common.CacheDataOpt;
import com.huawei.kunpeng.intellij.common.log.LogBackLogger;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.ide.AppLifecycleListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 应用初始化
 *
 * @since 2020-11-21
 */
public class IDEInitListener implements AppLifecycleListener {
    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        // 启用日志
        Logger.enableLogger(new LogBackLogger());
        // 初始化全局缓存
        CacheDataOpt.initGlobalCache();
    }
}
