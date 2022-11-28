package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.ErrorInstructionWebView;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;

public class ErrorInstructionEditor extends TuningWebFileEditor {
    private final ErrorInstructionWebView errorInstructionWebView;
    private static Map<String, String> pageParams;

    public ErrorInstructionEditor(VirtualFile file) {
        currentFile = file;
        errorInstructionWebView = new ErrorInstructionWebView(pageParams);
    }
    @Override
    public AbstractWebView getWebView() {
        return errorInstructionWebView;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return errorInstructionWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        errorInstructionWebView.dispose();
    }

    public static void openPage(Map<String, String> pageParamsMap) {
        pageParams = pageParamsMap;
        System.out.println("opening page error instruction");
        String fileName = TuningIDEConstant.TUNING_KPHT +
                IDEConstant.PATH_SEPARATOR +
                PageType.ERROR_INSTRUCTION.value() +
                IDEConstant.PATH_SEPARATOR +
                TuningI18NServer.toLocale("plugins_hyper_tuner_title_error_instruction") +
                "." +
                TuningIDEConstant.TUNING_KPHT;

        closeWebView(fileName);
        openWebView(fileName);
    }
}
