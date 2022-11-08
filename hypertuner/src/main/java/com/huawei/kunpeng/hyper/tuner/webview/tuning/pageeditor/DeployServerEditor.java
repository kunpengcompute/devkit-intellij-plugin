package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.DeployServerWebView;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.WebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DeployServerEditor extends TuningWebFileEditor {
    private final DeployServerWebView deployServerWebView;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描WebView虚拟文件
     */
    public DeployServerEditor(VirtualFile file) {
        currentFile = file;
        deployServerWebView = new DeployServerWebView();
    }

    @Override
    public WebView getWebView() {
        return deployServerWebView;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        return deployServerWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        deployServerWebView.dispose();
    }

    /**
     * 打开免费试用页
     */
    public static void openPage() {
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.DEPLOY_SERVER.value() +
                IDEConstant.PATH_SEPARATOR +
                TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_deploy_server") +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
}
