package com.huawei.kunpeng.hyper.tuner.common.constant.enums;

import com.huawei.kunpeng.intellij.ui.enums.Panels;

import java.awt.*;

/**
 * 性能分析工具的自定义sidebar类型
 */
public enum PanelType {
    TUNING_SERVER_CONFIG("TUNING_SERVER_CONFIG"),
    TUNING_CONFIG_SUCCESS("TUNING_CONFIG_SUCCESS"),
    TUNING_LOGIN_SUCCESS("TUNING_LOGIN_SUCCESS"),

    TUNING_CONNECT_FAIL("TUNING_CONNECT_FAIL");

    private final String panelName;
    PanelType(String panelName) {
        this.panelName = panelName;
    }

    public String panelName() {
        return panelName;
    }
}
