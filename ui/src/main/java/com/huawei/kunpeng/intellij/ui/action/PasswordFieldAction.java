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

package com.huawei.kunpeng.intellij.ui.action;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * PasswordFieldAction
 *
 * @since 2020-04-06
 */
public class PasswordFieldAction {
    private boolean viewIcon = false;

    private boolean viewStartIcon = false;

    private boolean viewConformIcon = false;

    private boolean isCopy;

    /**
     * 密码显示明文
     *
     * @param eyePwdLab      图片
     * @param jPasswordField 输入框
     */
    public void registerMouseListener(JLabel eyePwdLab, JPasswordField jPasswordField) {
        jPasswordField.setEchoChar('*');
        eyePwdLab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (viewIcon) {
                    jPasswordField.setEchoChar('*');
                    eyePwdLab.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
                    viewIcon = false;
                } else {
                    jPasswordField.setEchoChar('\0');
                    eyePwdLab.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_VIEW));
                    viewIcon = true;
                }
            }
        });
    }

    /**
     * 新增密码显示明文
     *
     * @param eyePwdLab      图片
     * @param jPasswordField 输入框
     */
    public void registerMouseListenerOne(JLabel eyePwdLab, JPasswordField jPasswordField) {
        jPasswordField.setEchoChar('*');
        eyePwdLab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (viewStartIcon) {
                    jPasswordField.setEchoChar('*');
                    eyePwdLab.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
                    viewStartIcon = false;
                } else {
                    jPasswordField.setEchoChar('\0');
                    eyePwdLab.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_VIEW));
                    viewStartIcon = true;
                }
            }
        });
    }

    /**
     * 确认密码显示明文
     *
     * @param eyePwdLab      图片
     * @param jPasswordField 输入框
     */
    public void registerMouseListenerTwo(JLabel eyePwdLab, JPasswordField jPasswordField) {
        jPasswordField.setEchoChar('*');
        eyePwdLab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (viewConformIcon) {
                    jPasswordField.setEchoChar('*');
                    eyePwdLab.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
                    viewConformIcon = false;
                } else {
                    jPasswordField.setEchoChar('\0');
                    eyePwdLab.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_VIEW));
                    viewConformIcon = true;
                }
            }
        });
    }

    /**
     * 密码长度限制
     *
     * @param passwordField 事件
     */
    public void pwdDocument(JPasswordField passwordField) {
        passwordField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String textNew, AttributeSet attributeSet) throws BadLocationException {
                char[] text = passwordField.getPassword();
                if (text.length + textNew.length() > 32) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                super.insertString(offs, textNew, attributeSet);
            }
        });
    }

    /**
     * 键盘复制限制
     *
     * @param keyEvent 事件
     */
    public void keyEvent(KeyEvent keyEvent) {
        if (("C").equals(KeyEvent.getKeyText(keyEvent.getKeyCode())) || ("X")
                .equals(KeyEvent.getKeyText(keyEvent.getKeyCode()))) {
            isCopy = true;
            return;
        }
        if (!isCopy) {
            return;
        }
        if (("Ctrl").equals(KeyEvent.getKeyText(keyEvent.getKeyCode()))) {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection("");
            clip.setContents(tText, null);
        }
    }
}

