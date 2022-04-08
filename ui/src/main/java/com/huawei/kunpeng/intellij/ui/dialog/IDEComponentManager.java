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

package com.huawei.kunpeng.intellij.ui.dialog;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ide自定义组件缓存管理
 *
 * @since 2020-09-25
 */
public class IDEComponentManager {
    /**
     * IDEComponentManager实例
     */
    public static IDEComponentManager instance = new IDEComponentManager();

    // 有效的弹窗缓存
    private static Map<String, IDEBaseDialog> viableDialogMap = new ConcurrentHashMap<>();

    // 有效的弹窗缓存队列，控制缓存大小
    private static Queue<IDEBaseDialog> viableDialogQueue = new LinkedList<>();

    private IDEComponentManager() {
    }

    /**
     * 获取组件管理器
     *
     * @return IDEComponentManager
     */
    public static IDEComponentManager getInstance() {
        return instance;
    }

    /**
     * 在缓存中获取有效的弹窗
     *
     * @param dialogName 显示名称
     * @return IDEBaseDialog
     */
    public static IDEBaseDialog getViableDialog(String dialogName) {
        IDEBaseDialog dialog = viableDialogMap.get(dialogName);
        if (dialog != null) {
            if (!dialog.isValid()) {
                removeDialog(null, viableDialogMap.get(dialogName));
                dialog = null;
            }
        }

        return dialog;
    }

    /**
     * 添加有效的弹框缓存
     *
     * @param dialogName 显示名称
     * @param dialog 弹框
     */
    public synchronized void addViableDialog(final String dialogName, IDEBaseDialog dialog) {
        String dialogNameDef = dialogName;
        dialogNameDef = StringUtil.stringIsEmpty(dialogNameDef) ? dialog.getDialogName() : dialogNameDef;
        viableDialogMap.put(dialogNameDef, dialog);
        viableDialogQueue.add(dialog);

        // 缓存队列容量校验
        if (viableDialogQueue.size() > IDEConstant.VIABLE_DIALOG_NUM) {
            removeDialog(null, viableDialogQueue.poll());
        }
    }

    /**
     * 移除并销毁弹框
     *
     * @param dialogName 显示名称
     * @param dialog 弹框
     */
    private static void removeDialog(String dialogName, final IDEBaseDialog dialog) {
        IDEBaseDialog dialogDef = dialog;
        if (dialog == null) {
            dialogDef = viableDialogMap.get(dialogName);
        }

        if (dialogDef == null) {
            return;
        }

        dialogDef.dispose();
        IDEBaseDialog finalDialog = dialogDef;
        viableDialogMap.values().removeIf(value -> finalDialog.equals(value));
    }
}
