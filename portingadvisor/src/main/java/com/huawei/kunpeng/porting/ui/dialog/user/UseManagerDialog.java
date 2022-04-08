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

package com.huawei.kunpeng.porting.ui.dialog.user;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.MANAGE_USER;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.CommonDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.ui.panel.userpanel.UserDialogAbstractPanel;

/**
 * 修改用户弹框。
 *
 * @since 2020-10-11
 */
public class UseManagerDialog extends CommonDialog {
    /**
     * 后续工作标记。
     */
    private String doWorKFlag;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public UseManagerDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? MANAGE_USER : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.LOGIN.dialogName() : dialogName;
        this.mainPanel = panel;

        // 设置弹框大小是否可变
        this.resizable = resizable;
        // 设置弹框中保存取消按钮的名称
        setButtonName();
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public UseManagerDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        mainPanel.clearPwd();
    }

    @Override
    protected boolean okVerify() {
        ResponseBean responseBean = new ResponseBean();
        if (mainPanel instanceof ActionOperate) {
            ((ActionOperate) mainPanel).actionOperate(responseBean);
            doWorKFlag = responseBean.getData(); // 设置工作标记
            return ValidateUtils.equals(responseBean.getStatus(), "0");
        }
        return true;
    }

    @Override
    protected void after() {
        if (ValidateUtils.equals(doWorKFlag, "0")) {
            if (mainPanel instanceof UserDialogAbstractPanel) {
                ((UserDialogAbstractPanel) mainPanel).logOut();
            }
        }
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
        Logger.info("the user cancel action.");
        mainPanel.clearPwd();
    }
}