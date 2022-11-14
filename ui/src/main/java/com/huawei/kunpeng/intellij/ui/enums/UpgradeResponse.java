package com.huawei.kunpeng.intellij.ui.enums;

public enum UpgradeResponse {
    SUCCESS("SUCCESS"),
    SSH_ERROR("Error:sshError"),
    UPLOAD_ERROR("uploadErr"),
    FAILED("failed");

    private final String value;

    UpgradeResponse(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
