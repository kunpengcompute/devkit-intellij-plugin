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

package com.huawei.kunpeng.intellij.ui.utils;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;

import com.intellij.openapi.ui.Messages;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * IDE 纯文本消息类弹窗工具类
 * 包装原生消息弹窗返回值，统一管理已使用的按钮信息
 *
 * @since 1.0.0
 */
public class IDEMessageDialogUtil {
    /**
     * 警告类图标
     */
    private static final Icon WARN = Messages.getWarningIcon();

    /**
     * 信息类图标
     */
    private static final Icon INFO = Messages.getInformationIcon();

    /**
     * 错误类图标
     */
    private static final Icon ERROR = Messages.getErrorIcon();

    /**
     * 问题类图标
     */
    private static final Icon QUESTION = Messages.getQuestionIcon();

    /**
     * 工具类隐藏构造函数
     *
     */
    private IDEMessageDialogUtil() {
    }

    /**
     * 显示消息弹框
     *
     * @param bean 消息弹框数据体
     * @return 用户选择的按钮标识
     */
    public static String showDialog(MessageDialogBean bean) {
        Logger.info("Start show message dialog");
        String[] button = new String[bean.getButtonNames().size()];
        // 将枚举列表关键信息（按钮名）抽出转换为字符串数组
        for (int i = 0; i < bean.getButtonNames().size(); i++) {
            button[i] = bean.getButtonNames().get(i).getName();
        }
        // 方法返回的choose 范围一定是 [-1, button.length-1] 中的某一值
        int choose = Messages.showDialog(bean.getMessage(), bean.getTitle(), button,
                bean.getDefaultButton(), bean.getIcon());
        String select;
        // -1 代表点击右上角 'x' 退出
        if (choose == -1) {
            select = ButtonName.CANCEL.key;
            Logger.info("User cancel the weakPwds delete operation.");
        } else {
            // 其余索引代表按钮列表对应的索引
            select = bean.getButtonNames().get(choose).key;
        }
        return select;
    }

    /**
     * 返回警告图标
     *
     * @return warn 原生警告图标
     */
    public static Icon getWarn() {
        return WARN;
    }

    /**
     * 返回信息图标
     *
     * @return warn 原生信息图标
     */
    public static Icon getInformation() {
        return INFO;
    }

    /**
     * 返回错误图标
     *
     * @return warn 原生错误图标
     */
    public static Icon getError() {
        return ERROR;
    }

    /**
     * 返回问题图标
     *
     * @return warn 原生问题图标
     * */
    public static Icon getQuestion() {
        return QUESTION;
    }

    /**
     * 创建自定义图标
     *
     * @param path 图标文件路径
     * @return 返回自定义图标
     */
    public static Icon createIcon(String path) {
        File image = new File(path);
        if (!image.exists()) {
            Logger.error("The image path is empty.");
            // 文件路径错误时默认返回原生警告图标
            return WARN;
        }
        return new ImageIcon(path);
    }

    /**
     * 内部按钮枚举类
     * 将所有消息弹窗涉及的按钮信息均注册进枚举类以便于管理
     *
     */
    public enum ButtonName {
        OK("ok", CommonI18NServer.toLocale("common_term_operate_ok")),
        CANCEL("cancel", CommonI18NServer.toLocale("common_term_operate_cancel")),
        REPLACE("replace", CommonI18NServer.toLocale("common_replace")),
        SAVE_AS("save", CommonI18NServer.toLocale("common_save_as")),
        THINK_AGAIN("think_again", CommonI18NServer.toLocale("common_user_disclaimer_think_again_name")),
        LOG_OUT("log_out", CommonI18NServer.toLocale("common_user_disclaimer_Log_out_name")),
        DELETE("delete", CommonI18NServer.toLocale("common_term_operate_del"));

        // 按钮标识符
        private final String key;

        // 按钮名称，即显示的按钮信息
        private final String name;

        ButtonName(String key, String info) {
            this.key = key;
            this.name = info;
        }

        /**
         * 返回按钮标识
         *
         * @return key
         */
        public String getKey() {
            return key;
        }

        /**
         * 返回按钮名称
         *
         * @return name
         */
        public String getName() {
            return name;
        }
    }
}
