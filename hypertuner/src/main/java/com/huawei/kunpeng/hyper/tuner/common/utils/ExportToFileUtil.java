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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;

import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningFileSaveAsPanel;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.dialog.FileSaveAsDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 导出文件名称重复工具类
 *
 * @since 2021/8/20 17:54
 */
public class ExportToFileUtil {
    /**
     * <p>
     * 判断文件是否已存在：
     * </p>
     * <ul>不存在直接返回 false；</ul>
     * <ul> 若存在则弹窗提示【文件已存在，是否覆盖】判断是否覆盖。
     *     <ul> 选择【替换】则返回 false ； </ul>
     *     <ul> 选择【另存为】则弹窗等待用户修改文件名，最终返回false，新的文件名存入map，若在修改文件名时取消则返回true； </ul>
     *     <ul> 选择【取消】则返回 true。 </ul>
     *
     * @param filePath    文件存储路径
     * @param fileNameMap 文件名Map，包含旧的文件名（fileName），新文件名（newFileName）
     * @param title       标题
     * @return 是否取消操作
     */
    public static boolean isExistNotToContinue(String filePath, HashMap<String, String> fileNameMap, String title) {
        String fileName = fileNameMap.get("fileName");
        String fullPath = filePath + File.separator + fileName;
        File file = new File(fullPath);
        if (!file.exists()) {
            // 文件不存在
            return false;
        }
        int buttonLength = 3;
        String message = I18NServer.toLocale("plugins_hyper_tuner_download_replace_tips");
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>(buttonLength);
        button.add(IDEMessageDialogUtil.ButtonName.REPLACE);
        button.add(IDEMessageDialogUtil.ButtonName.SAVE_AS);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String exitCode = IDEMessageDialogUtil.showDialog(
                new MessageDialogBean(message, title, button, 0, IDEMessageDialogUtil.getWarn()));
        // 根据点击的按钮选择不同的操作
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.CANCEL.getKey())) {
            // 取消则返回
            return true;
        } else if (exitCode.equals(IDEMessageDialogUtil.ButtonName.SAVE_AS.getKey())) {
            // 重命名
            return !showRenameFileMessage(fileNameMap);
        } else {
            // 覆盖/或其他场景
            return false;
        }
    }

    /**
     * 判断文件是否 已存在
     *
     * @param filePath 文件路径
     * @param title    标题
     * @return 是否继续下载
     */
    public static boolean isExistNotToContinue(String filePath, String title) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        int buttonLenth = 2;
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>(buttonLenth);
        button.add(IDEMessageDialogUtil.ButtonName.OK);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String message = I18NServer.toLocale("plugins_hyper_tuner_download_replace_tips");
        String select = getSelectString(title, button, message);
        if (select.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            return false;
        }
        return true;
    }

    private static String getSelectString(String title, List<IDEMessageDialogUtil.ButtonName> button, String message) {
        return IDEMessageDialogUtil.showDialog(
                new MessageDialogBean(message,
                        title,
                        button,
                        0,
                        IDEMessageDialogUtil.getWarn()
                ));
    }

    /**
     * 文件已存在，且点击另存为的情况下，显示Dialog,输入新的文件名，对文件名进行校验
     *
     * @param fileNameMap 文件名Map，包含旧的文件名（fileName），新文件名（newFileName）
     * @return boolean     是否可以保存
     */
    private static boolean showRenameFileMessage(HashMap<String, String> fileNameMap) {
        String fileName = fileNameMap.get("fileName");
        String prefix = "";
        String suffix = "";
        if (!StringUtils.contains(fileName, ".")) {
            prefix = fileName;
        } else {
            int dotLastIndex = fileName.lastIndexOf(".");
            prefix = fileName.substring(0, dotLastIndex);
            suffix = fileName.substring(dotLastIndex + 1);
        }
        IDEBasePanel panel = new TuningFileSaveAsPanel(null, prefix);
        FileSaveAsDialog dialog = new FileSaveAsDialog(null, panel);
        dialog.displayPanel();
        if (dialog.getExitCode() == OK_EXIT_CODE) {
            fileNameMap.put("newFileName", dialog.getFileName() + "." + suffix);
            return true;
        }
        return false;
    }
}