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

package com.huawei.kunpeng.porting.webview;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.filetype.WebFileType;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.provider.AbstractWebFileProvider;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.PageType;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisReportEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.ByteShowPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.CloudEnvApplicationProcessEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedFunctionPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedReportPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.MigrationAppraiseEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.MigrationAppraiseReportEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.MigrationCenterPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingReportPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingSourceEditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

/**
 * 自定义webview文件Provider
 *
 * @since 2020-10-23
 */
public class WebFileProvider extends AbstractWebFileProvider {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if (file.getCanonicalPath().contains(PortingIDEConstant.PLUGIN_NAME)) {
            return file.getFileType().getDefaultExtension().equals(WebFileType.EXTENSION);
        }
        return false;
    }

    @Override
    public WebFileEditor getWebFileEditor(@NotNull VirtualFile file, String[] paths, String[] pathsAfter) {
        WebFileEditor webFileEditor = getOtherWebFileEditor(PageType.getStatusByValue(pathsAfter[0]), file);
        if (PortingIDEContext.checkPortingLogin()) {
            switch (PageType.getStatusByValue(pathsAfter[0])) {
                case REPORT:
                    String taskId = pathsAfter[1].split(IDEConstant.POINT_SEPARATOR)[0];
                    if (!StringUtil.stringIsEmpty(taskId)) {
                        webFileEditor = new PortingReportPageEditor(file,
                                pathsAfter[1].split(IDEConstant.POINT_SEPARATOR)[0]);
                    }
                    break;
                case SOURCE:
                    webFileEditor = new PortingSourceEditor(file);
                    break;
                case MIGRATION_CENTER:
                    webFileEditor = new MigrationCenterPageEditor(file);
                    break;
                case ANALYSIS_CENTER:
                    webFileEditor = new AnalysisEditor(file);
                    break;
                case ANALYSIS_CENTER_REPORT:
                    webFileEditor = new AnalysisReportEditor(
                            file, pathsAfter[1].split(IDEConstant.POINT_SEPARATOR)[0]);
                    break;
                case MIGRATION_APPRAISE:
                    webFileEditor = new MigrationAppraiseEditor(file);
                    break;
                case MIGRATION_APPRAISE_REPORT:
                    webFileEditor = new MigrationAppraiseReportEditor(file,
                            pathsAfter[1].split(IDEConstant.POINT_SEPARATOR)[0]);
                    break;
                case ENHANCED_FUNCTION:
                    webFileEditor = new EnhancedFunctionPageEditor(file);
                    break;
                case ENHANCED_REPORT:
                    String enhancedTaskId = pathsAfter[2].split(IDEConstant.POINT_SEPARATOR)[0];
                    if (!StringUtil.stringIsEmpty(enhancedTaskId)) {
                        webFileEditor = new EnhancedReportPageEditor(file, enhancedTaskId, pathsAfter[1]);
                    }
                    break;
                case BYTE_SHOW:
                    webFileEditor = createByteShowPageEditor(paths, file);
                    break;
                default:
                    webFileEditor = getOtherWebFileEditor(PageType.getStatusByValue(pathsAfter[0]), file);
                    break;
            }
        }
        return webFileEditor;
    }

    /**
     * 其他webView页面扩展
     *
     * @param statusByValue statusByValue
     * @param file file
     * @return 视图编辑器
     */
    private WebFileEditor getOtherWebFileEditor(PageType statusByValue, VirtualFile file) {
        WebFileEditor webFileEditor = new DefaultEditor(file);
        if (statusByValue == PageType.CLOUD_ENV_APPLICATION_PROCESS) {
            webFileEditor = new CloudEnvApplicationProcessEditor(file);
        }
        return webFileEditor;
    }

    @Override
    public boolean validateFilePathAndType(VirtualFile file) {
        if (file.getCanonicalPath().contains(PortingIDEConstant.PLUGIN_NAME)) {
            return file.getFileType().getDefaultExtension().equals(WebFileType.EXTENSION);
        }
        return false;
    }

    /**
     * 创建字节对齐页面编辑区
     *
     * @param paths 路径
     * @param file 文件
     * @return 页面编辑区
     */
    private WebFileEditor createByteShowPageEditor(String[] paths, VirtualFile file) {
        String[] pathList = paths[1].split(IDEConstant.PATH_SEPARATOR, 3);
        if (pathList.length == 3) {
            String diffPath = IDEConstant.PATH_SEPARATOR +
                    pathList[2].substring(0, pathList[2].lastIndexOf("."));
            if (!StringUtil.stringIsEmpty(pathList[1]) && !StringUtil.stringIsEmpty(diffPath)) {
                return new ByteShowPageEditor(file, pathList[1], diffPath);
            }
        }
        return new DefaultEditor(file);
    }
}
