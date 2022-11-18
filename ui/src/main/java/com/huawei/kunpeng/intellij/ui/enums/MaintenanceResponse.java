package com.huawei.kunpeng.intellij.ui.enums;

public enum MaintenanceResponse {
    FAKE_SUCCESS("listen 172.29.7.105:8086"),
    SUCCESS("success"),
    CLOSE_LOADING("closeLoading"),
    SSH_ERROR("Error:sshError"),
    UPLOAD_ERROR("Error:uploadErr"),
    FAILED("failed");

    private final String value;

    MaintenanceResponse(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
