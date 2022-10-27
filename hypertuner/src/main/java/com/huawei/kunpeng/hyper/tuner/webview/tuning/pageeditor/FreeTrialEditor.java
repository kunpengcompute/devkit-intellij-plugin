package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.FreeTrialWebView;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.WebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class FreeTrialEditor extends TuningWebFileEditor {
    private final FreeTrialWebView freeTrialWebView;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描WebView虚拟文件
     */
    public FreeTrialEditor(VirtualFile file) {
        currentFile = file;
        freeTrialWebView = new FreeTrialWebView();
    }

    @Override
    public WebView getWebView() {
        return freeTrialWebView;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        return freeTrialWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        freeTrialWebView.dispose();
    }

    /**
     * 打开免费试用页
     */
    public static void openPage() {
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.FREE_TRIAL.value() +
                IDEConstant.PATH_SEPARATOR +
                TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_apply_trial") +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
}
