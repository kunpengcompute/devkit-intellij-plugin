package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.UninstallWebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class UninstallEditor extends TuningWebFileEditor {
    private final UninstallWebView uninstallWebView;

//    protected static String toolName;
    public UninstallEditor(VirtualFile file) {
        currentFile = file;
        uninstallWebView = new UninstallWebView();
    }

    @Override
    public AbstractWebView getWebView() {
        return uninstallWebView;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        return uninstallWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        uninstallWebView.dispose();
    }

    public static void openPage() {
        System.out.println("opening page uninstall");
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.UNINSTALL_HYPER_TUNER.value() +
                IDEConstant.PATH_SEPARATOR +
                TuningI18NServer.toLocale("plugins_hyper_tuner_uninstall_title") +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
}


