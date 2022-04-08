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

import com.huawei.kunpeng.hyper.tuner.common.utils.SchTaskFormatUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import java.awt.Component;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 预约任务管理 表格渲染器。
 *
 * @since 2021-4-25
 */
public class SchTaskTableRenderer extends DefaultTableCellRenderer {
    /**
     * 任务状态： 预约
     */
    private static final String RESERVE_STATUS = "reserve";
    /**
     * 任务状态：下发中
     */
    private static final String DISTRIBUTE_STATUS = "running";
    /**
     * 任务状态： 完成
     */
    private static final String SUCCESS_STATUS = "success";
    /**
     * 任务状态：失败
     */
    private static final String FAILED_STATUS = "fail";
    /**
     * 成功状态标记图片 绿色
     */
    private static final String SUCCESS_PATH = "/assets/img/settings/success.png";

    /**
     * 下发状态标记图片 深蓝色
     */
    private static final String DELIVER_PATH = "/assets/img/settings/delivering.png";

    /**
     * 失败状态标记图片 红色
     */
    private static final String FAIL_PATH = "/assets/img/settings/fail.png";

    /**
     * 预约状态标记图片 灰色
     */
    private static final String RESERVE_PATH = "/assets/img/settings/timeout.png";

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
                String status = statusObj.toString();
                String iconPath = getIconPath(status);
                if (ValidateUtils.isNotEmptyString(iconPath)) {
                    textIconLabel.setIcon(new ImageIcon(SchTaskTableRenderer.class.getResource(iconPath)));
                }
                String statusFormat = SchTaskFormatUtil.stateFormat(status);
                textIconLabel.setText(statusFormat);
                textIconLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
                textIconLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
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
        if (Objects.equals(status, SUCCESS_STATUS)) {
            // 成功
            path = SUCCESS_PATH;
        } else if (Objects.equals(status, RESERVE_STATUS)) {
            // 预约
            path = RESERVE_PATH;
        } else if (Objects.equals(status, DISTRIBUTE_STATUS)) {
            // 下发
            path = DELIVER_PATH;
        } else {
            path = FAIL_PATH;
        }
        return path;
    }

    /**
     * 获取图标
     *
     * @param status status
     * @return ImageIcon
     */
    public ImageIcon getStatusIcon(String status) {
        String iconPath = getIconPath(status);
        return new ImageIcon(SchTaskTableRenderer.class.getResource(iconPath));
    }
}
