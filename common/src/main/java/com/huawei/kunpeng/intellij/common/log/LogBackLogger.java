/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.kunpeng.intellij.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

/**
 * LogBack日志: slf4j+Logback
 *
 * @since 2.3.T10
 */
public class LogBackLogger implements BaseLogger {
    public LogBackLogger() {
        // 环境变量中设置logback日志保存路径
        File file = new File(Objects.requireNonNull(
            this.getClass().getClassLoader().getResource("logback.xml")).getPath());
        if (Objects.nonNull(file)) {
            String logPath = file.getParentFile().getParentFile().getParentFile().getPath();
            String realPath = logPath.substring(logPath.indexOf("\\") + 1).replaceAll("\\\\", "/");
            System.setProperty("log.path", realPath);
        }
    }

    private Logger getLog() {
        return LoggerFactory.getLogger(getCallerStackTrace(5));
    }

    @Override
    public void info(String message) {
        getLog().info(message);
    }

    @Override
    public void info(String message, Object o1) {
        getLog().info(message, o1);
    }

    /**
     * info
     *
     * @param message message
     * @param o1      o1
     */
    @Override
    public void info(String message, Object... o1) {
        getLog().info(message, o1);
    }

    @Override
    public void warn(String message) {
        getLog().warn(message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        getLog().warn(message, throwable);
    }

    @Override
    public void warn(String message, Object o1) {
        getLog().warn(message, o1);
    }

    @Override
    public void error(String message) {
        getLog().error(message);
    }

    @Override
    public void error(String message, Throwable throwable, Object... details) {
        getLog().error(message, throwable, details);
    }

    @Override
    public void error(String message, Object o1, Object o2) {
        getLog().error(message, o1, o2);
    }

    @Override
    public void error(String message, Object o1) {
        getLog().error(message, o1);
    }

    @Override
    public void error(String message, Throwable throwable) {
        getLog().error(message, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return getLog().isErrorEnabled();
    }
}