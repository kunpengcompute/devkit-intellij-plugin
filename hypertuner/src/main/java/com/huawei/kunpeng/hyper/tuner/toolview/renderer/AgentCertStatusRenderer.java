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

package com.huawei.kunpeng.hyper.tuner.toolview.renderer;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.AgentCertContent;
import com.huawei.kunpeng.intellij.common.log.Logger;

import java.awt.Component;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Agent服务证书，证书状态渲染
 *
 * @since 2021-04-21
 */
public class AgentCertStatusRenderer extends DefaultTableCellRenderer {
    /**
     * 默认渲染器。
     */
    private static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer =
                DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (renderer instanceof JLabel) {
            JLabel textIconLabel = (JLabel) renderer;
            textIconLabel.removeAll();
            Object statusObj = table.getModel().getValueAt(row, column);
            if (!Objects.isNull(statusObj)) {
                String status = "<html>";
                String[] statusArray = statusObj.toString().split("  ");
                for (int i = 0; i < statusArray.length; i++) {
                    if (statusArray[i].trim().length() > 0) {
                        String imgPath = getIconPath(statusArray[i]);
                        String content = getContentText(statusArray[i]);
                        status +=
                                "<img src ="
                                        + imgPath
                                        + " style='height: 3px;width: 3px'"
                                        + ">"
                                        + "<span height='20px'>"
                                        + content
                                        + "</span><br>";
                    }
                }
                status += "</html>";
                textIconLabel.setText(status);
            }
        }
        return renderer;
    }

    private String getContentText(String status) {
        switch (status) {
            case "-1":
                return AgentCertContent.TABLE_CERT_STATUS10;
            case "0":
                return AgentCertContent.TABLE_CERT_STATUS0;
            case "1":
                return AgentCertContent.TABLE_CERT_STATUS1;
            case "2":
                return AgentCertContent.TABLE_CERT_STATUS2;
            default:
                Logger.warn("status {} not match", status);
        }
        return AgentCertContent.TABLE_CERT_STATUS10;
    }

    /**
     * 通过状态获取对应的状态图片。
     *
     * @param status 状态
     * @return 返回图片路径
     */
    private String getIconPath(String status) {
        if (Objects.equals(status, "0")) {
            return AgentCertNameRenderer.class.getResource(AgentCertContent.SUCCESS_PATH).toString();
        } else if (Objects.equals(status, "1")) {
            return AgentCertNameRenderer.class.getResource(AgentCertContent.TIMEOUT_PATH).toString();
        } else if (Objects.equals(status, "2")) {
            return AgentCertNameRenderer.class.getResource(AgentCertContent.FAIL_PATH).toString();
        } else if (Objects.equals(status, "3")) {
            return AgentCertNameRenderer.class.getResource(AgentCertContent.WARN_INFO_PATH).toString();
        } else {
            return "";
        }
    }
}
