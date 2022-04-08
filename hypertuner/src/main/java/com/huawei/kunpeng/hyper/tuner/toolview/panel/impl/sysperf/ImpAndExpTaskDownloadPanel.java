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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.ImpAndExpTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 导出任务下载
 *
 * @since 2021-5-6
 */
public class ImpAndExpTaskDownloadPanel extends IDEBasePanel {
    private static final long serialVersionUID = 5678649090321044594L;

    private static final String RUN_LOG_NAME = "/log.zip";

    private static final int BUTTON_LENGTH = 2;

    private String logFileName;

    private JPanel mainPanel;

    private JLabel downLoadTip;

    private JLabel iconLabel;
    private JLabel fileNameL1;
    private JLabel fileNameL2;
    private JLabel fileSizeL1;
    private JLabel fileSizeL2;
    private ImpAndExpTaskAction impAndExpTaskAction;
    private Project project;
    private ToolbarDecorator toolbarForRunTable;
    private String fileName;
    private String fileSize;
    private int id;
    private int section;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param panelName   面板名称
     * @param displayName 面板显示title
     */
    public ImpAndExpTaskDownloadPanel(
            String panelName, String displayName, String fileName, String fileSize, int id, int section) {
        setToolWindow(toolWindow);
        this.panelName = ImpAndExpTaskContent.DOWNLOAD_ALL;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.id = id;
        this.section = section;
        initPanel(mainPanel); // 初始化面板
    }

    @Override
    protected void initPanel(JPanel panel) {
        project = CommonUtil.getDefaultProject();
        super.initPanel(mainPanel);

        // 设置提示
        downLoadTip.setText(ImpAndExpTaskContent.DOWNLOAD_TIPS);
        downLoadTip.setPreferredSize(new Dimension(545, 30));
        iconLabel.setVisible(false);
        downLoadTip.setVisible(false);
        fileNameL1.setText(ImpAndExpTaskContent.DOWNLOAD_FILE_NAME);
        fileNameL2.setText(fileName);
        fileSizeL1.setText(ImpAndExpTaskContent.DOWNLOAD_FILE_SIZE);
        fileSizeL2.setText(fileSize);
        if (impAndExpTaskAction == null) {
            impAndExpTaskAction = new ImpAndExpTaskAction();
        }
    }

    @Override
    protected void registerComponentAction() {
    }

    /**
     * 确认操作
     *
     * @return 是否选择下载文件
     */
    public Boolean onOK() {
        impAndExpTaskAction = new ImpAndExpTaskAction();

        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        // 弹窗标题
        descriptor.setTitle(I18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_download_all"));
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return false;
        }
        String path = virtualFile.getPath();
        String fileNameAndSuffix = fileName + ".tar";
        String pathAndFile = path + File.separator + fileNameAndSuffix;

        // 判断下载文件是否存在
        if (ExportToFileUtil.isExistNotToContinue(pathAndFile, I18NServer.toLocale(
                "plugins_hyper_tuner_impAndExp_task_download_all"))) {
            return false;
        }
        impAndExpTaskAction.downloadSelectLog(path, fileNameAndSuffix, id, section);
        return true;
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}
