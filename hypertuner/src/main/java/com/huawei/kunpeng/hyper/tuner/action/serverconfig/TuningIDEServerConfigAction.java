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

package com.huawei.kunpeng.hyper.tuner.action.serverconfig;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 配置远端服务器
 *
 * @since 2020-10-09
 */
public class TuningIDEServerConfigAction extends AnAction {
    /**
     * 点击配置远端服务器事件
     *
     * @param anActionEvent 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 从配置文件读取信息
//        String ip = CommonUtil.readCurIpFromConfig();
//        if (!StringUtil.stringIsEmpty(ip)) {
//            // 加载切换服务器确认弹框
//            TuningCommonUtil.showConfigSaveConfirmDialog();
//        }else{
//            // 配置文件
//            IDEBasePanel panel = new TuningServerConfigPanel(null);
//            IDEBaseDialog dialog = new TuningServerConfigWrapDialog(TuningUserManageConstant.CONFIG_TITLE, panel);
//            dialog.displayPanel();
//        }
    }
}
