package com.huawei.kunpeng.intellij.ui.enums;

public enum CheckConnResponse {
    SUCCESS("SUCCESS"),
    USERAUTH_FAILURE("USERAUTH_FAILURE"),
    FINGERPRINT_FAILURE("host fingerprint verification failed"),
    TIME_OUT("TIMEOUT"),
    PASSPHRASE_FAILURE("Cannot parse privateKey"),
    CANCEL("Cancel");

    private final String value;

    CheckConnResponse(String value) {
        this.value=value;
    }

    public String value() {
        return value;
    }
}
