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

package com.huawei.kunpeng.intellij.js2java.util;

import org.cef.handler.CefKeyboardHandler;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * The class JcefEventUtil
 *
 * @since 2.3.T10
 */
final class JcefEventUtil {
    private static final Map<Integer, Integer> CEF_2_JAVA_KEYCODES = new HashMap<>();

    private static final Map<Integer, Integer> CEF_2_JAVA_MODIFIERS = new HashMap<>();

    static {
        CEF_2_JAVA_KEYCODES.put(13, 10);
        CEF_2_JAVA_KEYCODES.put(8, 8);
        CEF_2_JAVA_KEYCODES.put(9, 9);
        CEF_2_JAVA_MODIFIERS.put(4, 128);
        CEF_2_JAVA_MODIFIERS.put(2, 64);
        CEF_2_JAVA_MODIFIERS.put(8, 512);
        CEF_2_JAVA_MODIFIERS.put(16, 1024);
        CEF_2_JAVA_MODIFIERS.put(32, 2048);
        CEF_2_JAVA_MODIFIERS.put(64, 4096);
    }
    /**
     * KeyEvent
     *
     * @param javaKeyEvent javaKeyEvent
     * @param id id
     * @return KeyEvent
     */

    protected static KeyEvent javaKeyEventWithID(KeyEvent javaKeyEvent, int id) {
        return new KeyEvent(
                javaKeyEvent.getComponent(), id, javaKeyEvent.getWhen(), javaKeyEvent.getModifiers(),
                javaKeyEvent.getKeyCode(), javaKeyEvent.getKeyChar(), javaKeyEvent.getKeyLocation());
    }
    /**
     * KeyEvent
     *
     * @param cefKeyEvent cefKeyEvent
     *
     * @return KeyEvent
     */

    protected static boolean isUpDownKeyEvent(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
        return cefKeyEvent.windows_key_code == 38 || cefKeyEvent.windows_key_code == 40;
    }

    /**
     * KeyEvent
     *
     * @param cefKeyEvent cefKeyEvent
     * @param source source
     * @return KeyEvent
     */

    protected static KeyEvent convertCefKeyEvent(CefKeyboardHandler.CefKeyEvent cefKeyEvent, Component source) {
        return new KeyEvent(
                source, convertCefKeyEventType(cefKeyEvent), System.currentTimeMillis(),
                convertCefKeyEventModifiers(cefKeyEvent), convertCefKeyEventKeyCode(cefKeyEvent),
                cefKeyEvent.character, 0);
    }

    private static int convertCefKeyEventModifiers(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
        int javaModifiers = 0;
        for (Map.Entry<Integer, Integer> next : CEF_2_JAVA_MODIFIERS.entrySet()) {
            if (next == null) {
                continue;
            }
            if ((cefKeyEvent.modifiers & next.getKey()) != 0) {
                javaModifiers |= next.getValue();
            }
        }
        return javaModifiers;
    }

    private static int convertCefKeyEventType(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
        switch (cefKeyEvent.type) {
            case KEYEVENT_RAWKEYDOWN:
            case KEYEVENT_KEYDOWN:
                return 401;
            case KEYEVENT_KEYUP:
                return 402;
            case KEYEVENT_CHAR:
                return 400;
            default:
                assert false;
                return -1;
        }
    }

    private static int convertCefKeyEventKeyCode(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
        Integer keyCode = CEF_2_JAVA_KEYCODES.get(cefKeyEvent.windows_key_code);
        return keyCode != null ? keyCode : cefKeyEvent.windows_key_code;
    }
}
