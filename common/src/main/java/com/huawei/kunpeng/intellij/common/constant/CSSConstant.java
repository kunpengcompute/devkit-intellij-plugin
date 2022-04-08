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

package com.huawei.kunpeng.intellij.common.constant;

import java.awt.Color;

import javax.swing.ImageIcon;

/**
 * 用户管理样式常量定义
 *
 * @since 2020-10-21
 */
public class CSSConstant {
    /**
     * 用户管理界面 重置密码、删除密码颜色:#2F65CA rgba(47,101,202,1)
     */
    public static final Color RESET_DELETE_LABEL_COLOR = new Color(47, 101, 202);

    /**
     * 红星图片路径
     */
    public static final String RED_STAR_PATH = "/assets/img/settings/redstar.png";

    /**
     * 红星图片
     */
    public static final ImageIcon RED_STAR_ICON = new ImageIcon(CSSConstant.class.getResource(RED_STAR_PATH));

    /**
     * 提示图片
     */
    public static final String ICON_INFO = "/assets/img/common/icon_info.png";

    /**
     * 建议反馈二维码图片路径
     */
    public static final String QR_INFO = "/assets/img/common/voc_qr.png";

    /**
     * 告警图片
     */
    public static final String ICON_WARN = "/assets/img/common/icon_warn.png";

    /**
     * 告警图片
     */
    public static final ImageIcon ICON_INFO_WARN = new ImageIcon(CSSConstant.class.getResource(ICON_WARN));

    /**
     * 提示图片
     */
    public static final ImageIcon ICON_INFO_ICON = new ImageIcon(CSSConstant.class.getResource(ICON_INFO));

    /**
     * 建议反馈二维码图片
     */
    public static final ImageIcon VOC_QR_ICON = new ImageIcon(CSSConstant.class.getResource(QR_INFO));

    /**
     * 提示信息字体颜色
     */
    public static final Color TEXT_TIP_INFO_COLOR = new Color(110, 110, 110);

    /**
     * 输入框低暗字体颜色
     */
    public static final Color TEXT_BOX_TIP_INFO_COLOR = new Color(88, 88, 88);
}
