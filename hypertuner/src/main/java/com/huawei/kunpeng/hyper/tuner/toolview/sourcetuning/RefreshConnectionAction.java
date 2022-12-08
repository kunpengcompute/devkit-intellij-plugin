package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RefreshConnectionAction extends AnAction implements DumbAware {
    private static final Icon icon = BaseIntellijIcons.load(IDEConstant.MENU_ICONS_PATH + IDEConstant.TITLE_REFRESH_ICON);

    private static final String REFRESH_CONNECTION = TuningI18NServer.toLocale("plugins_hyper_tuner_titlebar_refresh_connection");
    public RefreshConnectionAction() {
        super(REFRESH_CONNECTION, null, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // TODO 刷新连接按钮事件
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        String ip = CommonUtil.readCurIpFromConfig();
        if (!StringUtil.stringIsEmpty(ip)) {
            event.getPresentation().setEnabledAndVisible(true);
        } else {
            event.getPresentation().setEnabled(false);
        }
    }
}
