package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.ConfigureServerWebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ConfigureServerEditor extends TuningWebFileEditor {
    private final ConfigureServerWebView configureServerWebView;


    public ConfigureServerEditor(VirtualFile file) {
        currentFile = file;
        configureServerWebView = new ConfigureServerWebView();
    }

    @Override
    public AbstractWebView getWebView() {
        return configureServerWebView;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        return configureServerWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        configureServerWebView.dispose();
    }

    public static void openPage() {
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.CONFIGURE_SERVER.value() +
                IDEConstant.PATH_SEPARATOR +
                "HyperTuner-ConfigureServer" +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
}
