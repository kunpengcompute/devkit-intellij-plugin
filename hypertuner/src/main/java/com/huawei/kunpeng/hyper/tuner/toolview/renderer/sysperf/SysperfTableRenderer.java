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

package com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 节点管理表格渲染器。
 *
 * @since 2020-11-5
 */
public class SysperfTableRenderer extends DefaultTableCellRenderer {
    /**
     * 容量监控1级图标-蓝色
     */
    private static final String CAPACITY_MONOTORING1 = "/assets/img/sysperf/cap_mon1.svg";

    /**
     * 容量监控2级图标-橙色
     */
    private static final String CAPACITY_MONOTORING2 = "/assets/img/sysperf/cap_mon2.svg";

    /**
     * 容量监控3级图标-红色
     */
    private static final String CAPACITY_MONOTORING3 = "/assets/img/sysperf/cap_mon3.svg";

    /**
     * 失败状态 导出
     */
    private static final String FAILED_STATUS_IMPORT = "export_failed";
    /**
     * 失败状态 导入
     */
    private static final String FAILED_STATUS_EXPORT = "import_failed";
    /**
     * 加载中标记动态图
     */
    private static final String LOADING_PATH = "/assets/load/loading_w.jpg";

    private static final String LOADING_PATH_B = "/assets/load/loading_b.jpg";
    /**
     * 成功状态标记图片
     */
    private static final String SUCCESS_PATH = "/assets/img/settings/success.png";

    /**
     * 失败状态标记图片
     */
    private static final String FAIL_PATH = "/assets/img/settings/fail.png";

    /**
     * 超时状态标记图片
     */
    private static final String TIMEOUT_PATH = "/assets/img/settings/timeout.png";

    /**
     * 超时状态标记图片
     */
    private static final String WARN_INFO_PATH = "/assets/img/settings/warn_info.png";
    /**
     * 默认渲染器。
     */
    private static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    private String type;

    public SysperfTableRenderer(String type) {
        this.type = type;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel textIconLabel = new JLabel();
        textIconLabel.removeAll();
        Component renderer =
                DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if ("nodeTable".equals(type)) {
            JLabel jLabel = (JLabel) renderer;
            jLabel.removeAll();
            if (("--").equals(value) || ("").equals(value)) {
                jLabel.setText("--");
                jLabel.setIcon(new ImageIcon());
                return jLabel;
            }
        }
        textIconLabel = (JLabel) renderer;
        textIconLabel.removeAll();
        if (renderer instanceof JLabel) {
            textIconLabel = (JLabel) renderer;
            textIconLabel.removeAll();
            Object statusObj = table.getModel().getValueAt(row, column);
            String status = statusObj.toString();
            String iconPath = getIconPath(status, column);
            String icontext = getIcontext(status);
            if (ValidateUtils.isNotEmptyString(iconPath)) {
                textIconLabel.setIcon(BaseIntellijIcons.load(iconPath));
            }
            textIconLabel.setText(icontext);
            textIconLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
            textIconLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        }
        return renderer;
    }

    /**
     * 通过状态获取对应的状态图片。
     *
     * @param status 状态
     * @param col    列数
     * @return 返回图片路径
     */
    private String getIconPath(String status, int col) {
        String path = "";
        if ("nodeTable".equals(type)) {
            int num = Integer.parseInt(status);
            path = colSetReturnPath(col, num);
        } else {
            if (ValidateUtils.isEmptyString(status)) {
                return path;
            }
            if (Objects.equals(status, ImpAndExpTaskContent.STATUS_IMP_SUCCESS)
                    || Objects.equals(status, ImpAndExpTaskContent.STATUS_EXP_SUCCESS)) {
                // 成功
                path = SUCCESS_PATH;
            } else if (Objects.equals(status, ImpAndExpTaskContent.STATUS_IMP_UPLOADING)
                    || Objects.equals(status, ImpAndExpTaskContent.STATUS_IMP_IMPORTING)) {
                // 上传中
                Boolean isLightThemeInContext =
                        TuningIDEContext.getValueFromGlobalContext(
                                TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.LIGHT_THEME.vaLue());
                if (isLightThemeInContext == null || isLightThemeInContext) {
                    path = LOADING_PATH;
                } else {
                    path = LOADING_PATH_B;
                }
            } else {
                // 失败
                path = FAIL_PATH;
            }
        }
        return path;
    }

    private String colSetReturnPath(int col, int num) {
        String path = "";
        if (col == 7) {
            if (num >= 0.4 * 1024) {
                path = CAPACITY_MONOTORING1;
            } else if (num < 0.4 * 1024 && num >= 0.2 * 1024) {
                path = CAPACITY_MONOTORING2;
            } else if (num < 0.2 * 1024) {
                path = CAPACITY_MONOTORING3;
            } else {
                return path;
            }
        } else if (col == 8) {
            if (num >= 150 * 0.2) {
                path = CAPACITY_MONOTORING1;
            } else if (num < 150 * 0.2 && num >= 150 * 0.1) {
                path = CAPACITY_MONOTORING2;
            } else if (num < 150 * 0.1) {
                path = CAPACITY_MONOTORING3;
            } else {
                return path;
            }
        } else {
            return path;
        }
        return path;
    }

    /**
     * 通过状态获取对应的内容。
     *
     * @param status 状态
     * @return 返回内容
     */
    private String getIcontext(String status) {
        String icontext;
        if ("nodeTable".equals(type)) {
            int num = Integer.parseInt(status);
            if (num > 1024) {
                icontext = num / (1000) + " GB";
            } else {
                icontext = num + " MB";
            }
            icontext =
                    MessageFormat.format(I18NServer.toLocale("plugins_hyper_tuner_node_table_render_tips"), icontext);
        } else {
            icontext = status;
        }
        return icontext;
    }
}
