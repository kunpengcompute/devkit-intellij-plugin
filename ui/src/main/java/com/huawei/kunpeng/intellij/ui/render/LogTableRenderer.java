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

package com.huawei.kunpeng.intellij.ui.render;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import java.awt.Component;
import java.util.Locale;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * log日志管理表格渲染器。
 *
 * @since 2021-04-06
 */
public class LogTableRenderer extends DefaultTableCellRenderer {
    /**
     * 成功状态
     */
    private static final String SUCCESS_STATUS = "successful";

    /**
     * 成功状态-on
     */
    private static final String SUCCESS_STATUS_ON = CommonI18NServer.toLocale
            ("common_node_on").toLowerCase(Locale.ROOT);
    /**
     * 有效状态
     */
    private static final String VALID_STATUS = CommonI18NServer.toLocale
            ("common_certificate_valid").toLowerCase(Locale.ROOT);

    /**
     * 失败状态
     */
    private static final String FAILED_STATUS = CommonI18NServer.toLocale
            ("common_node_fail").toLowerCase(Locale.ROOT);

    /**
     * 失败状态
     */
    private static final String FAILED_STATUS1 = "failed";

    /**
     * 超期Expired状态
     */
    private static final String EXPIRED_STATUS =
            CommonI18NServer.toLocale("common_certificate_failure").toLowerCase(Locale.ROOT);

    /**
     * 超时状态
     */
    private static final String TIMEOUT_STATUS = "timeout";

    /**
     * 超时状态-off
     */
    private static final String OFF_STATUS = CommonI18NServer.toLocale
            ("common_node_off").toLowerCase(Locale.ROOT);

    /**
     * 超时状态-init
     */
    private static final String INIT_STATUS = CommonI18NServer.toLocale
            ("common_node_init").toLowerCase(Locale.ROOT);

    /**
     * 超时状态-lock
     */
    private static final String LOCK_STATUS = "lock";

    /**
     * 超时状态-update
     */
    private static final String UPDATE_STATUS = "update";

    /**
     * 即将过期状态
     */
    private static final String ABOUT_EXPIRE_STATUS
            = CommonI18NServer.toLocale("common_certificate_nearFailure").toLowerCase(Locale.ROOT);

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
    private static final DefaultTableCellRenderer DEFAULT_RENDERER =
            new DefaultTableCellRenderer();

    private static final String SUCCESS_STATUS_ONLINE
            = CommonI18NServer.toLocale("common_node_online").toLowerCase(Locale.ROOT);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {
        Component renderer =
                DEFAULT_RENDERER.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
        if (renderer instanceof JLabel) {
            JLabel textIconLabel = (JLabel) renderer;
            textIconLabel.removeAll();
            Object statusObj = table.getModel().getValueAt(row, column);
            if (!Objects.isNull(statusObj)) {
                String status = statusObj.toString();
                String iconPath = getIconPath(status.toLowerCase(Locale.ROOT));
                if (ValidateUtils.isNotEmptyString(iconPath)) {
                    textIconLabel.setIcon(new ImageIcon(LogTableRenderer.class.getResource(iconPath)));
                }
                textIconLabel.setText(status);
                textIconLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
                textIconLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
            }
        }
        return renderer;
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
        if (isSuccess(status)) {
            path = SUCCESS_PATH;
        } else if (isFailed(status)) {
            path = FAIL_PATH;
        } else if (isTimeOut(status)) {
            path = TIMEOUT_PATH;
        } else if (isWarn(status)) {
            path = WARN_INFO_PATH;
        } else {
            path = "";
        }
        return path;
    }

    private boolean isSuccess(String status) {
        return Objects.equals(status, SUCCESS_STATUS) || Objects.equals(status, VALID_STATUS)
                || Objects.equals(status, SUCCESS_STATUS_ONLINE) || Objects.equals(status, SUCCESS_STATUS_ON);
    }

    private boolean isFailed(String status) {
        return Objects.equals(status, FAILED_STATUS) || Objects.equals(status, EXPIRED_STATUS)
                || Objects.equals(status, FAILED_STATUS1);
    }

    private boolean isTimeOut(String status) {
        return Objects.equals(status, TIMEOUT_STATUS) || Objects.equals(status, OFF_STATUS);
    }

    private boolean isWarn(String status) {
        return Objects.equals(status, ABOUT_EXPIRE_STATUS) || Objects.equals(status, LOCK_STATUS)
                || Objects.equals(status, UPDATE_STATUS) || Objects.equals(status, INIT_STATUS);
    }
}
