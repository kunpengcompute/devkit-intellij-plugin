package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.ConfigGuideWebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ConfigGuideEditor extends TuningWebFileEditor {
    private final ConfigGuideWebView configGuideWebView;

    public ConfigGuideEditor(VirtualFile file) {
        currentFile = file;
        configGuideWebView = new ConfigGuideWebView();
    }

    public ConfigGuideEditor() {
        configGuideWebView = new ConfigGuideWebView();
    }

    @Override
    public AbstractWebView getWebView() {
        return configGuideWebView;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return configGuideWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        configGuideWebView.dispose();
    }

    public static void openPage() {
        System.out.println("opening page configure guide");
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.CONFIGURE_GUIDE.value() +
                IDEConstant.PATH_SEPARATOR +
                TuningI18NServer.toLocale("plugins_hyper_tuner_title_config_guide") +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
}
