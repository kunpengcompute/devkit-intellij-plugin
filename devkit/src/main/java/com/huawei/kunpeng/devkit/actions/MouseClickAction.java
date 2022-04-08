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

package com.huawei.kunpeng.devkit.actions;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 安装按钮
 *
 * @since 2021-5-17
 */
public class MouseClickAction extends MouseAdapter {
    private static final Cursor POINTER = new Cursor(Cursor.HAND_CURSOR);

    private static final Cursor DEFAULT = new Cursor(Cursor.HAND_CURSOR);

    private final Runnable onClick;

    /**
     * MouseClickAction MouseClickAction
     *
     * @param onClick onClick
     */
    public MouseClickAction(Runnable onClick) {
        this.onClick = onClick;
    }

    /**
     * mouseClicked mouseClicked
     *
     * @param event event
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        super.mouseClicked(event);
        if (event.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        onClick.run();
    }

    /**
     * mouseEntered mouseEntered
     *
     * @param event event
     */
    @Override
    public void mouseEntered(MouseEvent event) {
        super.mouseEntered(event);
        event.getComponent().setCursor(POINTER);
    }

    /**
     * mouseExited mouseExited
     *
     * @param event event
     */
    @Override
    public void mouseExited(MouseEvent event) {
        super.mouseExited(event);
        event.getComponent().setCursor(DEFAULT);
    }
}