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
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.actions.ActionsCollector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogEarthquakeShaker;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.wm.IdeFocusManager;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * 基于intellIJ-父组件的弹框封装，为标准Dialog，且浮现于顶层，被关闭前不可操作其他界面
 *
 * @since 1.0.0
 */
public abstract class IdeaDialog extends DialogWrapper implements IDEBaseDialog {
    /**
     * 弹框是否关闭
     */
    protected boolean closed = true;

    /**
     * 弹框标题
     */
    protected String title;

    /**
     * 弹框名称
     */
    protected String dialogName;

    /**
     * 主要内容展示面板
     */
    protected IDEBasePanel mainPanel;

    /**
     * 弹框位置大小
     */
    protected Rectangle rectangle;

    /**
     * 弹框大小是否可变
     */
    protected boolean resizable;

    /**
     * 自定义确认按钮
     */
    protected Action okAction = getOKAction();

    /**
     * 自定义next按钮
     */
    protected NextAction nextAction;

    /**
     * 自定义取消按钮
     */
    protected Action cancelAction = getCancelAction();

    /**
     * 自定义帮助按钮
     */
    protected Action helpAction = getHelpAction();

    /**
     * 自定义确认按钮名称
     */
    private String okName;

    /**
     * 自定义取消按钮名称
     */
    private String cancelName;

    /**
     * 自定义next按钮名称
     */
    private String nextName;

    /**
     * 帮助tool tip
     */
    private String helpTip;

    /**
     * 帮助超链接
     */
    private String helpUrl;

    /**
     * 构造函数
     */
    public IdeaDialog() {
        super(true);
    }

    /**
     * 创建内容面板
     *
     * @return JComponent
     */
    @Nullable
    protected abstract JComponent createCenterPanel();

    /**
     * 初始化弹框内容
     */
    @Nullable
    protected void initDialog() {
        // 创建主面板
        init();

        // 设置弹框title
        setTitle(title);

        // 设置位置信息
        if (rectangle == null) {
            rectangle = new Rectangle(0, 0, IDEConstant.DIALOG_DEFAULT_WIDTH, IDEConstant.DIALOG_DEFAULT_HEIGHT);
        }
        if (rectangle.getX() > 0 || rectangle.getY() > 0) {
            setLocation((int) rectangle.getX(), (int) rectangle.getY());
        }
        setSize(rectangle.width, rectangle.height);

        // 设置面板大小是否可变
        setResizable(resizable);
    }

    /**
     * 设置ok/cancel按钮名称，自定义时，两个按钮必须都设置
     *
     * @param okName 确认按钮名称
     * @param cancelName 取消按钮名称
     */
    protected void setOKAndCancelName(String okName, String cancelName) {
        this.okName = okName;
        this.cancelName = cancelName;
    }

    /**
     * 设置ok/cancel按钮名称，自定义时，两个按钮必须都设置
     *
     * @param okName 确认按钮名称
     * @param cancelName 取消按钮名称
     * @param nextName next按钮名称
     */
    protected void setOKAndCancelAndNextName(String okName, String cancelName, String nextName) {
        this.okName = okName;
        this.cancelName = cancelName;
        this.nextName = nextName;
    }

    /**
     * 覆盖默认的ok/cancel按钮
     *
     * @return Action[]
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        List<Action> actions = new ArrayList<>();
        if (ValidateUtils.isNotEmptyString(okName)) {
            okAction = new OKAction(okName);

            // 设置默认的焦点按钮
            okAction.putValue("DefaultAction", true);
            actions.add(okAction);
        }

        if (ValidateUtils.isNotEmptyString(cancelName)) {
            cancelAction = new DialogWrapperExitAction(cancelName, CANCEL_EXIT_CODE);
            actions.add(cancelAction);
        }

        if (ValidateUtils.isNotEmptyString(nextName)) {
            nextAction = new NextAction(nextName);
            actions.add(nextAction);
        }

        if (ValidateUtils.isNotEmptyString(helpTip) || ValidateUtils.isNotEmptyString(helpUrl)) {
            myHelpAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    BrowserUtil.browse(helpUrl);
                }
            };
            myHelpAction.setEnabled(true);
            actions.add(myHelpAction);
        }

        // 自定义时，两个按钮必须都设置
        if (ValidateUtils.isNotEmptyCollection(actions)) {
            return actions.toArray(new Action[0]);
        } else {
            return super.createActions();
        }
    }

    /**
     * 设置help按钮tooltip及超链接
     *
     * @param tip tooltip
     * @param url 超链接
     */
    protected void setHelp(String tip, String url) {
        helpTip = tip;
        helpUrl = url;
    }

    @Override
    protected void setHelpTooltip(@NotNull JButton helpButton) {
        helpButton.setToolTipText(helpTip);
    }

    /**
     * 弹框是否有效存在
     *
     * @return boolean
     */
    @Override
    public boolean isValid() {
        return !closed;
    }

    /**
     * 销毁弹框
     */
    @Override
    public void dispose() {
        super.dispose();
        closed = true;
    }

    /**
     * 点击确定事件
     */
    protected abstract void onOKAction();

    /**
     * 点击取消或关闭事件
     */
    protected abstract void onCancelAction();

    /**
     * 点击next
     */
    protected void onNextAction() {
    }

    /**
     * 是否延后处理的面板
     *
     * @return boolean result
     */
    protected boolean isNeedInvokeLaterPanel() {
        return false;
    }

    /**
     * 显示弹框
     */
    public void displayPanel() {
        IDEComponentManager.instance.addViableDialog(dialogName, this);
        closed = false;
        showAndGet();
        switch (getExitCode()) {
            case OK_EXIT_CODE:
                closed = true;
                if (isNeedInvokeLaterPanel()) {
                    // 登录成功后的处理放在最后
                    ApplicationManager.getApplication().invokeLater(() -> {
                        onOKAction();
                    });
                } else {
                    onOKAction();
                }
                break;
            case CANCEL_EXIT_CODE:
                closed = true;
                onCancelAction();
                break;
            case NEXT_USER_EXIT_CODE:
                onNextAction();
                break;
            default:
                closed = true;
                break;
        }
    }

    /**
     * 点击ok前的校验事件
     *
     * @return boolean
     */
    protected boolean okVerify() {
        return true;
    }

    /**
     * 点击next前的校验事件
     *
     * @return boolean
     */
    protected boolean nextVerify() {
        return true;
    }

    /**
     * 处理玩弹框之后需要做的事情。
     */
    protected void after() {
    }

    /**
     * 获取弹框名称
     *
     * @return String
     */
    public String getDialogName() {
        return dialogName;
    }

    /**
     * 做ok事件前的校验
     */
    public void doOkValidate() {
        if (okAction instanceof OKAction) {
            ((OKAction) okAction).validate();
            updateDialog();
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        updateDialog();
        if (mainPanel != null) {
            return mainPanel.doValidate();
        }
        return super.doValidate();
    }

    /**
     * 自定义确认按钮
     *
     * @since 2020-10-08
     */
    protected class OKAction extends DialogWrapperAction {
        // 按钮名称
        private String name;

        /**
         * 构造函数
         *
         * @param name 按钮名
         */
        protected OKAction(String name) {
            super(name);
            this.name = name;
        }

        /**
         * 点击确认事件
         *
         * @param event 假如ovVerify不通过，则不会触发确定事件
         */
        @Override
        public void doAction(ActionEvent event) {
            if (!validate()) {
                return;
            }
            // 登录面板动作在对应的弹框逻辑中处理
            if (isNeedInvokeLaterPanel()) {
                return;
            } else {
                if (okVerify()) {
                    close(OK_EXIT_CODE);
                }
                after();
            }
        }

        /**
         * OK事件条件校验
         *
         * @return true:条件满足；false:条件不满足
         */
        public boolean validate() {
            // 增加提示信息处理。
            recordAction("IDEDialogOkAction", EventQueue.getCurrentEvent());
            List<ValidationInfo> infoList = doValidateAll();
            if (ValidateUtils.isNotEmptyCollection(infoList)) {
                doValidationInfo(infoList);
                if (infoList.stream().anyMatch(info1 -> !info1.okEnabled)) {
                    return false;
                }
            }
            return true;
        }
    }

    private void doValidationInfo(List<ValidationInfo> infoList) {
        ValidationInfo info = infoList.get(0);
        if (info.component != null && info.component.isVisible()) {
            IdeFocusManager.getInstance(null).requestFocus(info.component, true);
        }
        if (!isInplaceValidationToolTipEnabled()) {
            DialogEarthquakeShaker.shake(getPeer().getWindow());
        }
        startTrackingValidation();
    }

    /**
     * 注册校验事件
     *
     * @param name name
     * @param event event
     */
    private void recordAction(@NonNls String name, AWTEvent event) {
        if (event instanceof KeyEvent && ApplicationManager.getApplication() != null) {
            ActionsCollector.getInstance().record(name, (KeyEvent) event, getClass());
        }
    }

    /**
     * 注册校验提示信息。
     *
     * @return 返回注册结果
     */
    public boolean isInplaceValidationToolTipEnabled() {
        return Registry.is("ide.inplace.validation.tooltip", true);
    }

    /**
     * 自定义第三个按钮
     *
     * @since 2020-10-08
     */
    protected class NextAction extends DialogWrapperAction {
        /**
         * 构造函数
         *
         * @param name 按钮名
         */
        protected NextAction(String name) {
            super(name);
        }

        /**
         * 点击确认事件
         *
         * @param event 假如ovVerify不通过，则不会触发确定事件
         */
        @Override
        protected void doAction(ActionEvent event) {
            recordAction("IDEDialogNextAction", EventQueue.getCurrentEvent());
            List<ValidationInfo> infoList = doValidateAll();
            if (ValidateUtils.isNotEmptyCollection(infoList)) {
                doValidationInfo(infoList);
                if (infoList.stream().anyMatch(info1 -> !info1.okEnabled)) {
                    return;
                }
            }
            if (nextVerify()) {
                close(NEXT_USER_EXIT_CODE);
            }
            after();
        }
    }

    /**
     * 刷新弹框
     */
    @Override
    public void updateDialog() {
    }
}
