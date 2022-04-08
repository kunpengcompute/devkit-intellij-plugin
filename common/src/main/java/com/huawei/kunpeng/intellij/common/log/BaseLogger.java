/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.kunpeng.intellij.common.log;

/**
 * 日志基础接口
 *
 * @since 2.3.T10
 */
public interface BaseLogger {
    void info(String message);

    void info(String message, Object o1);

    void info(String message, Object... o1);

    void warn(String message);

    void warn(String message, Object o1);

    void warn(String message, Throwable throwable);

    void error(String message);

    void error(String message, Object o1);

    void error(String message, Throwable throwable, Object... details);

    void error(String message, Object o1, Object o2);

    void error(String message, Throwable throwable);

    boolean isErrorEnabled();

    default String getCallerStackTrace(int depth) {
        StackTraceElement directCaller = Thread.currentThread().getStackTrace()[depth];
        return directCaller.getFileName() + "." +
            directCaller.getMethodName() + "(line: " + directCaller.getLineNumber() + ")";
    }
}