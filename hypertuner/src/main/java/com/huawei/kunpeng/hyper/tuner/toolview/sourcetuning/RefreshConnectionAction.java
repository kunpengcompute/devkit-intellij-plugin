package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.CompatibilityDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningConfigSuccessPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningConnectFailPanel;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class RefreshConnectionAction extends AnAction implements DumbAware {
    private static final Icon icon = BaseIntellijIcons.load(IDEConstant.MENU_ICONS_PATH + IDEConstant.TITLE_REFRESH_ICON);

    private static final String REFRESH_CONNECTION = TuningI18NServer.toLocale("plugins_hyper_tuner_titlebar_refresh_connection");

    public RefreshConnectionAction() {
        super(REFRESH_CONNECTION, null, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String failInfo = null;

        // 检查版本接口模拟

        Callable task = new Callable() {
            @Override
            public ResponseBean call() throws Exception {
                String SERVER_VERSION_URL = "/user-management/api/v2.2/users/version/";
                RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, SERVER_VERSION_URL,
                        HttpMethod.GET.vaLue(), false);
                ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
                return responseBean;
            }
        };
        ExecutorService exeservices = Executors.newSingleThreadExecutor();
        Future future = exeservices.submit(task);
        try {
            //超时处理，否则界面会卡住很久
            ResponseBean responseBean = (ResponseBean) future.get(2, TimeUnit.SECONDS);
            if (responseBean == null) {
                Logger.info("Server Version not found.");
                failInfo = TuningI18NServer.toLocale("plugins_hyper_tuner_refresh_connect_fail");
            } else {
                Logger.info(responseBean.toString());
                String responseBeanDataJsStr = responseBean.getData();
                JSONObject jsonObject = JSON.parseObject(responseBeanDataJsStr);
                String serverVersionStr = jsonObject.getString("version");
                boolean isContains = true; // 默认插件兼容所有版本插件
                Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
                Object configVersionObj = config.get(ConfigProperty.CONFIG_VERSION.vaLue());
                if (configVersionObj instanceof List) {
                    List configList = (List) configVersionObj;
                    if (!configList.isEmpty()) {
                        // 配置文件中兼容性版本不为空，则说明对兼容性有要求
                        isContains = configList.contains(serverVersionStr);
                    }
                }
                if (!isContains) {
                    failInfo = TuningI18NServer.toLocale("plugins_hyper_tuner_refresh_version_not_match");
                }
            }
            if (failInfo != null) synchronizedLeftTree(failInfo);
        } catch (Exception error) {
            Logger.info(error.getMessage());
            failInfo = TuningI18NServer.toLocale("plugins_hyper_tuner_refresh_connect_fail");
            synchronizedLeftTree(failInfo);
        }
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

    protected void refreshFailPanel(Project proj, String failInfo) {
        ToolWindow toolWindow =
                ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
        TuningConnectFailPanel tuningConnectFailPanel = new TuningConnectFailPanel(toolWindow, proj, failInfo);
        if (toolWindow != null) {
            toolWindow.getContentManager().removeAllContents(true);
            toolWindow.getContentManager().addContent(tuningConnectFailPanel.getContent());
            toolWindow.getContentManager().setSelectedContent(tuningConnectFailPanel.getContent());
        }
    }

    private void synchronizedLeftTree(String failInfo) {
        // 如果打开多个project， 同步每一个project左侧树状态
        Project[] openProjects = ProjectUtil.getOpenProjects();
        ApplicationManager.getApplication().invokeLater(() -> {
            for (Project proj : openProjects) {
                // 配置服务器完成后刷新左侧树面板为已配置面板
                refreshFailPanel(proj, failInfo);
            }
        });
    }
}
