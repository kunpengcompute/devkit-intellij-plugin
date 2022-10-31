package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.UpgradeServerWebView;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.WebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class UpgradeServerEditor extends TuningWebFileEditor {
    private final UpgradeServerWebView upgradeServerWebView;
    private static Integer localPort;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描WebView虚拟文件
     */
    public UpgradeServerEditor(VirtualFile file) {
        currentFile = file;
        upgradeServerWebView = new UpgradeServerWebView();
    }

    @Override
    public WebView getWebView() {
        return upgradeServerWebView;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        return upgradeServerWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        upgradeServerWebView.dispose();
    }

    /**
     * 打开性能分析工具端登录页
     */
    public static void openPage() {
//        localPort = Integer.parseInt(localPortStr);
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.UPGRADE_SERVER.value() +
                IDEConstant.PATH_SEPARATOR +
                TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_upgrade") +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
}
