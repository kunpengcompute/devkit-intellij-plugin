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

package com.huawei.kunpeng.hyper.tuner.toolview.renderer.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.InternalCommCertConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaPerfLogsConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import com.intellij.openapi.util.IconLoader;

import java.awt.Component;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * JavaPerf 模块 表格公用渲染器
 * 日志管理：operateLogTable
 *
 * @since 2021-4-25
 */
public class JavaPerfTableRenderer extends DefaultTableCellRenderer {
    /**
     * 成功状态标记图片 绿色
     */
    private static final String SUCCESS_PATH = "/assets/img/settings/success.png";

    /**
     * 警告状态标记图片 黄色
     */
    private static final String WARN_PATH = "/assets/img/settings/warn_info.png";
    /**
     * 超时状态标记图片 灰色
     */
    private static final String TIME_OUT_PATH = "/assets/img/settings/timeout.png";

    /**
     * 失败状态标记图片 红色
     */
    private static final String FAIL_PATH = "/assets/img/settings/fail.png";

    /**
     * 创建中状态标记图片
     */
    private static final String CREATE_LOADING_PATH = "/assets/img/sysperf/sampling.svg";

    /**
     * 内部通信证书-状态：有效
     */
    private static final String INTERNAL_STATUS_VALID = "VALID";
    /**
     * 内部通信证书-状态：即将过期
     */
    private static final String INTERNAL_STATUS_EXPIRING = "EXPIRING";
    /**
     * 内部通信证书-状态：失效
     */
    private static final String INTERNAL_STATUS_EXPIRED = "EXPIRED";
    /**
     * 内部通信证书-状态：永久有效
     */
    private static final String INTERNAL_STATUS_NONE = "NONE";

    /**
     * 成功状态-on
     */
    private static final String SUCCESS_STATUS_ON =
            CommonI18NServer.toLocale("common_node_on").toLowerCase(Locale.ROOT);

    /**
     * 超时状态-off
     */
    private static final String OFF_STATUS = CommonI18NServer.toLocale("common_node_off").toLowerCase(Locale.ROOT);

    /**
     * 默认渲染器。
     */
    private static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    /**
     * 类型
     */
    private String type;

    public JavaPerfTableRenderer(String type) {
        this.type = type;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer =
                DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        JLabel textIconLabel = (JLabel) renderer;
        textIconLabel.removeAll();
        Object statusObj = table.getModel().getValueAt(row, column);
        String status = statusObj.toString();
        String statusText = "";
        String iconPath = "";
        if (renderer instanceof JLabel) {
            if ("operateLogTable".equals(type)) {
                // Java性能分析日志
                status = statusObj.toString();
                iconPath = getIconPath(status);
                statusText = getOperateLogTableStatusText(status);
                if (ValidateUtils.isNotEmptyString(iconPath)) {
                    textIconLabel.setIcon(new ImageIcon(JavaPerfTableRenderer.class.getResource(iconPath)));
                }
                textIconLabel.setText(statusText);
                textIconLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
                textIconLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
                return textIconLabel;
            } else if ("internalCommCertTable".equals(type)) {
                // 内部通信证书表格
                statusText = getInternalStatusFormat(status);
                iconPath = getInternalTableStatusIcon(status);
                textIconLabel.setText(" " + statusText);
                textIconLabel.setIcon(BaseIntellijIcons.load(iconPath));
                return textIconLabel;
            } else if ("guardianTable".equals(type)) {
                // 目标环境
                statusText = getInternalStatusFormat(status);
                iconPath = getIconPath(status.toLowerCase(Locale.ROOT));
                textIconLabel.setText(statusText);
                textIconLabel.setIcon(BaseIntellijIcons.load(iconPath));
                return textIconLabel;
            } else {
                return renderer;
            }
        } else {
            // 其他表格类型
            return renderer;
        }
    }

    /**
     * javaPerf模块操作日志
     * 国际化展示状态
     *
     * @param status 状态
     * @return 返回内容
     */
    public static String getOperateLogTableStatusText(String status) {
        String iconText = "";
        if ("true".equals(status)) {
            iconText = JavaPerfLogsConstant.JAVA_PERF_TABLE_STATUS_SUCCESS;
        } else if ("false".equals(status)) {
            iconText = JavaPerfLogsConstant.JAVA_PERF_TABLE_STATUS_FAIL;
        } else if ("timeout".equals(status)) {
            iconText = JavaPerfLogsConstant.JAVA_PERF_TABLE_STATUS_TIMEOUT;
        } else {
            iconText = status;
        }
        return iconText;
    }

    /**
     * 内部通信证书 状态格式化
     *
     * @param status 未格式化状态
     * @return 格式化之后的状态
     */
    private String getInternalStatusFormat(String status) {
        if (status == null) {
            return "";
        }
        String statusFormat = "";
        switch (status) {
            case INTERNAL_STATUS_VALID:
                statusFormat = InternalCommCertConstant.TABLE_COL_NAME_STATUS_VALID;
                break;
            case INTERNAL_STATUS_EXPIRING:
                statusFormat = InternalCommCertConstant.TABLE_COL_NAME_STATUS_EXPIRING;
                break;
            case INTERNAL_STATUS_EXPIRED:
                statusFormat = InternalCommCertConstant.TABLE_COL_NAME_STATUS_EXPIRED;
                break;
            case INTERNAL_STATUS_NONE:
                statusFormat = InternalCommCertConstant.TABLE_COL_NAME_STATUS_NONE;
                break;
            default:
                statusFormat = status;
                break;
        }
        return statusFormat;
    }

    /**
     * 內部通信证书 获取状态图标
     *
     * @param status 未格式化状态
     * @return 图标路径
     */
    private String getInternalTableStatusIcon(String status) {
        if (status == null) {
            return "";
        }
        String iconPath = "";
        switch (status) {
            case INTERNAL_STATUS_VALID:
                // $FALL-THROUGH$
            case INTERNAL_STATUS_NONE:
                iconPath = SUCCESS_PATH;
                break;
            case INTERNAL_STATUS_EXPIRING:
                iconPath = WARN_PATH;
                break;
            case INTERNAL_STATUS_EXPIRED:
                iconPath = FAIL_PATH;
                break;
            default:
                iconPath = FAIL_PATH;
                break;
        }
        return iconPath;
    }

    /**
     * 通过状态获取对应的状态图片。
     *
     * @param status 状态
     * @return 返回图片路径
     */
    private String getIconPath(String status) {
        String path = "";
        if (ValidateUtils.isEmptyString(status)) {
            return path;
        }
        if (status.contains(SUCCESS_STATUS_ON) || "true".equals(status)) {
            path = SUCCESS_PATH;
        } else if (status.contains(OFF_STATUS) || "false".equals(status)) {
            path = FAIL_PATH;
        } else if (GuardianMangerConstant.TABLE_COL_CREATEING.equalsIgnoreCase(status)) {
            path = CREATE_LOADING_PATH;
        } else {
            path = "";
        }
        return path;
    }
}
