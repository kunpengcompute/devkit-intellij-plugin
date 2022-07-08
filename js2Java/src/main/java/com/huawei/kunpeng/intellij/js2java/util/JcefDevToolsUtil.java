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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.SystemInfoRt;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandlerAdapter;
import org.cef.handler.CefKeyboardHandler;
import org.cef.handler.CefKeyboardHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

/**
 * The class JcefDevToolsUtil
 *
 * @since 2.3.T10
 */
public class JcefDevToolsUtil {
    private static JDialog myDevtoolsFrame;

    private static CefBrowser myCefBrowser;

    private static CefKeyboardHandler myKeyboardHandler;

    private static String myProjectName;

    /**
     * register devTools for current page
     *
     * @param cefClient   cefClient
     * @param cefBrowser  cefBrowser
     * @param projectName projectName
     */
    public static void registerJcefDevTools(CefClient cefClient, CefBrowser cefBrowser, String projectName) {
        myProjectName = projectName;
        myDevtoolsFrame = null;
        myKeyboardHandler = new CefKeyboardHandlerAdapter() {
            /**
             * register devTools for current page
             *
             * @param browser browser
             * @param cefKeyEvent cefKeyEvent
             * @return boolean
             */
            @Override
            public boolean onKeyEvent(CefBrowser browser, CefKeyEvent cefKeyEvent) {
                Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                boolean consume = focusOwner != browser.getUIComponent();
                if (consume && SystemInfoRt.isMac && JcefEventUtil.isUpDownKeyEvent(cefKeyEvent)) {
                    return true;
                } else {
                    return systemInfoWindows(cefKeyEvent, consume);
                }
            }
        };
        myCefBrowser = cefBrowser;
        cefClient.addKeyboardHandler(myKeyboardHandler);
        cefClient.addContextMenuHandler(createDefaultContextMenuHandler());
    }

    private static boolean systemInfoWindows(CefKeyboardHandler.CefKeyEvent cefKeyEvent, boolean consume) {
        Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        if (focusedWindow == null) {
            return true;
        } else {
            KeyEvent javaKeyEvent = JcefEventUtil.convertCefKeyEvent(cefKeyEvent, focusedWindow);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(javaKeyEvent);
            if (javaKeyEvent.getID() == 401 && cefKeyEvent.modifiers == 0 && cefKeyEvent.character != 0) {
                javaKeyEvent = JcefEventUtil.javaKeyEventWithID(javaKeyEvent, 400);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(javaKeyEvent);
            }

            return consume;
        }
    }

    private static DefaultCefContextMenuHandler createDefaultContextMenuHandler() {
        boolean isInternal = ApplicationManager.getApplication().isInternal();
        return new DefaultCefContextMenuHandler(isInternal);
    }

    private static void openDevtools() {
        if (myDevtoolsFrame != null) {
            myDevtoolsFrame.toFront();
        } else {
            Optional<Window> frame = getActiveFrame();
            if (frame.isPresent()) {
                Window activeFrame = frame.get();
                Rectangle bounds = activeFrame.getGraphicsConfiguration().getBounds();
                myDevtoolsFrame = new JDialog(activeFrame);
                myDevtoolsFrame.setTitle(myProjectName + " devTools");
                myDevtoolsFrame.setDefaultCloseOperation(2);
                myDevtoolsFrame.setBounds(bounds.width / 4 + 100, bounds.height / 4 + 100,
                        bounds.width / 2, bounds.height / 2);
                myDevtoolsFrame.setLayout(new BorderLayout());
                CefBrowser devTools = myCefBrowser.getDevTools();
                myDevtoolsFrame.add(devTools.getUIComponent(), "Center");
                myDevtoolsFrame.addWindowListener(new WindowAdapter() {
                    /**
                     * windowClosed
                     *
                     * @param event WindowEvent
                     */
                    @Override
                    public void windowClosed(WindowEvent event) {
                        myDevtoolsFrame = null;
                        devTools.doClose();
                    }
                });
                myDevtoolsFrame.setVisible(true);
            }
        }
    }

    private static Optional<Window> getActiveFrame() {
        Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame.isActive()) {
                return Optional.of(frame);
            }
        }
        return Optional.empty();
    }

    private static class DefaultCefContextMenuHandler extends CefContextMenuHandlerAdapter {
        /**
         * DEBUG_COMMAND_ID
         */
        protected static final int DEBUG_COMMAND_ID = 28500;

        private final boolean isInternal;

        public DefaultCefContextMenuHandler(boolean isInternal) {
            this.isInternal = isInternal;
        }

        /**
         * onBeforeContextMenu
         *
         * @param browser browser
         * @param frame   frame
         * @param params  params
         * @param model   model
         */
        @Override
        public void onBeforeContextMenu(CefBrowser browser, CefFrame frame,
                                        CefContextMenuParams params, CefMenuModel model) {
            if (this.isInternal) {
                model.addItem(DEBUG_COMMAND_ID, "Open DevTools");
            }
        }

        /**
         * onContextMenuCommand
         *
         * @param browser    browser
         * @param frame      frame
         * @param params     params
         * @param commandId  commandId
         * @param eventFlags eventFlags
         * @return boolean
         */
        @Override
        public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame,
                                            CefContextMenuParams params, int commandId, int eventFlags) {
            if (commandId == DEBUG_COMMAND_ID) {
                openDevtools();
                return true;
            } else {
                return false;
            }
        }
    }
}
