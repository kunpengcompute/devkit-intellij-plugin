/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.kunpeng.intellij.common.log;

/**
 * 日志记录操作类
 *
 * @since 2.3.T10
 */
public final class Logger {
    private static BaseLogger mainLogger = null;

    private Logger() {
        super();
    }

    /**
     * 初始化日志记录实例对象
     *
     * @param baseLogger 日志对象
     */
    public static synchronized void enableLogger(BaseLogger baseLogger) {
        if (baseLogger == null) {
            return;
        }
        mainLogger = baseLogger;
    }

    /**
     * error
     *
     * @param message message
     */
    public static synchronized void error(String message) {
        if (mainLogger != null) {
            mainLogger.error(message);
        }
    }

    /**
     * error
     *
     * @param message   message
     * @param throwable throwable
     * @param details   details
     */
    public static synchronized void error(String message, Throwable throwable, Object... details) {
        if (mainLogger != null) {
            mainLogger.error(message, throwable, details);
        }
    }

    /**
     * error
     *
     * @param message message
     * @param details details
     */
    public static synchronized void error(String message, Object details) {
        if (mainLogger != null) {
            mainLogger.error(message, details);
        }
    }

    /**
     * error
     *
     * @param message message
     * @param o1      o1
     * @param o2      o2
     */
    public static synchronized void error(String message, Object o1, Object o2) {
        if (mainLogger != null) {
            mainLogger.error(message, o1, o2);
        }
    }

    /**
     * error
     *
     * @param message   message
     * @param throwable throwable
     */
    public static synchronized void error(String message, Throwable throwable) {
        if (mainLogger != null) {
            mainLogger.error(message, throwable);
        }
    }

    /**
     * info
     *
     * @param message message
     */
    public static synchronized void info(String message) {
        if (mainLogger != null) {
            mainLogger.info(message);
        }
    }

    /**
     * info
     *
     * @param message message
     * @param o1      o1
     */
    public static synchronized void info(String message, Object o1) {
        if (mainLogger != null) {
            mainLogger.info(message, o1);
        }
    }

    /**
     * info
     *
     * @param message message
     * @param args    args
     */
    public static synchronized void info(String message, Object... args) {
        if (mainLogger != null) {
            mainLogger.info(message, args);
        }
    }

    /**
     * warn
     *
     * @param message message
     */
    public static synchronized void warn(String message) {
        if (mainLogger != null) {
            mainLogger.warn(message);
        }
    }

    /**
     * warn
     *
     * @param message   message
     * @param throwable throwable
     */
    public static synchronized void warn(String message, Throwable throwable) {
        if (mainLogger != null) {
            mainLogger.warn(message, throwable);
        }
    }

    /**
     * warn
     *
     * @param message message
     * @param o1      o1
     */
    public static synchronized void warn(String message, Object o1) {
        if (mainLogger != null) {
            mainLogger.warn(message, o1);
        }
    }

    /**
     * isErrorEnabled
     *
     * @return Error 是否可用
     */
    public static synchronized boolean isErrorEnabled() {
        return mainLogger.isErrorEnabled();
    }
}
