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

package com.huawei.kunpeng.devkit.model;

import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.util.text.StringUtil;

import java.io.File;
import java.text.NumberFormat;

/**
 * KPPluginNode
 *
 * @since 2021-8-25
 */
public class KPPluginNode {
    private PluginNode pluginNode;

    private boolean isCertificate;

    private boolean isFeatured;

    private File installedFile;

    private boolean isOffline;

    private boolean isBeta;

    private String insteadPluginId;

    /**
     * KPPluginNode
     *
     * @param pluginNode pluginNode
     */
    public KPPluginNode(PluginNode pluginNode) {
        this.pluginNode = pluginNode;
    }

    /**
     * getPluginNode
     *
     * @return PluginNode
     */
    public PluginNode getPluginNode() {
        return pluginNode;
    }

    /**
     * setPluginNode
     *
     * @param pluginNode pluginNode
     */
    public void setPluginNode(PluginNode pluginNode) {
        this.pluginNode = pluginNode;
    }

    /**
     * isCertificate
     *
     * @return boolean
     */
    public boolean isCertificate() {
        return isCertificate;
    }

    /**
     * setCertificate
     *
     * @param certificate certificate
     */
    public void setCertificate(String certificate) {
        this.isCertificate = "true".equals(certificate);
    }

    /**
     * setCertificate
     *
     * @param isCertificate isCertificate
     */
    public void setCertificate(boolean isCertificate) {
        this.isCertificate = isCertificate;
    }

    /**
     * isOffline isOffline
     *
     * @return boolean
     */
    public boolean isOffline() {
        return isOffline;
    }

    /**
     * setIsOffline
     *
     * @param isOffline isOffline
     */
    public void setIsOffline(String isOffline) {
        this.isOffline = "true".equals(isOffline);
    }

    /**
     * isFeatured
     *
     * @return featured
     */
    public boolean isFeatured() {
        return isFeatured;
    }

    /**
     * setFeatured
     *
     * @param featured featured
     */
    public void setFeatured(String featured) {
        this.isFeatured = "true".equals(featured);
    }

    /**
     * setFeatured
     *
     * @param isFeatured isFeatured
     */
    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    /**
     * getTotalDownloads
     *
     * @return String
     */
    public String getTotalDownloads() {
        String downloadString = pluginNode.getDownloads();
        if (StringUtil.isEmpty(downloadString)) {
            return "0";
        }
        if (downloadString.matches("^[0-9]+$")) {
            return NumberFormat.getInstance().format(Long.parseLong(downloadString));
        }
        return downloadString;
    }

    /**
     * getRatingFloat
     *
     * @return float
     */
    public float getRatingFloat() {
        String rating = pluginNode.getRating();
        if (StringUtil.isEmpty(rating)) {
            return 0.0f;
        }
        if (rating.matches("^[0-9]+(\\.[0-9]+)?$")) {
            return Float.parseFloat(rating);
        }
        return 0.0f;
    }

    /**
     * getInstalledFile
     *
     * @return File
     */
    public File getInstalledFile() {
        return installedFile;
    }

    /**
     * setInstalledFile
     *
     * @param installedFile installedFile
     */
    public void setInstalledFile(File installedFile) {
        this.installedFile = installedFile;
    }

    /**
     * isBeta
     *
     * @return boolean
     */
    public boolean isBeta() {
        return isBeta;
    }

    /**
     * setBeta
     *
     * @param beta beta
     */
    public void setBeta(String beta) {
        this.isBeta = "true".equals(beta);
    }

    /**
     * getInsteadPluginId
     *
     * @return String
     */
    public String getInsteadPluginId() {
        return insteadPluginId;
    }

    /**
     * setInsteadPluginId
     *
     * @param insteadPluginId insteadPluginId
     */
    public void setInsteadPluginId(String insteadPluginId) {
        this.insteadPluginId = insteadPluginId;
    }
}