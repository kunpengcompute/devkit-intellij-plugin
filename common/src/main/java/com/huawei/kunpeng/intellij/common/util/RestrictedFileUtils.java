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

package com.huawei.kunpeng.intellij.common.util;

import com.huawei.kunpeng.intellij.common.log.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * restricted file utils
 *
 * @since 1.0.0
 */
public class RestrictedFileUtils {
    private RestrictedFileUtils(){}

    /**
     * restrict security file permission
     *
     * @param path file path
     */
    public static void restrictSecurityFileAccess(String path) {
        try {
            if (SystemUtil.isWindows()) {
                File file = new File(path);
                file.setReadable(true, true);
                file.setWritable(true, true);
            } else {
                Files.setPosixFilePermissions(Paths.get(path), PosixFilePermissions.fromString("rw-------"));
            }
        } catch (UnsupportedOperationException | IOException e) {
            Logger.warn("failed to restrict security files. stack trace : ", e);
        }
    }

    /**
     * restrict readOnly file permission
     *
     * @param path file path
     */
    public static void restrictReadOnlyFileAccess(String path) {
        try {
            setFileOrDirAccessIsReadOnly(new File(path));
        } catch (UnsupportedOperationException | IOException e) {
            Logger.warn("failed to restrict security files. stack trace : {}", e.getMessage());
        }
    }

    private static void setFileOrDirAccessIsReadOnly(File dir) throws IOException {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children == null) {
                return;
            }
            // 递归设置子目录权限
            for (String child : children) {
                setFileOrDirAccessIsReadOnly(new File(dir, child));
            }
            setDirAccessIs700(dir);
        } else {
            setFileAccessIsReadOnly(dir);
        }
    }

    private static void setDirAccessIs700(File dir) throws IOException {
        // 设置文件夹权限为700
        if (SystemUtil.isWindows()) {
            dir.setReadable(true, true);
            dir.setWritable(true, true);
            dir.setExecutable(true, true);
        } else {
            Files.setPosixFilePermissions(dir.toPath(), PosixFilePermissions.fromString("rwx------"));
        }
    }

    private static void setFileAccessIsReadOnly(File dir) throws IOException {
        // 设置文件权限为400
        if (SystemUtil.isWindows()) {
            dir.setReadable(true, true);
            dir.setWritable(false, false);
        } else {
            Files.setPosixFilePermissions(dir.toPath(), PosixFilePermissions.fromString("r--------"));
        }
    }
}
