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

package com.huawei.kunpeng.devkit.toolview.ui;

import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.ex.temp.TempFileSystem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Objects;

/**
 * PluginVirtualFile
 *
 * @since 2021-05-18
 */
public class PluginVirtualFile extends VirtualFile {
    private final PluginNode descriptor;

    public PluginVirtualFile(PluginNode descriptor) {
        this.descriptor = descriptor;
    }

    @NotNull
    @Override
    public String getName() {
        return String.format(Locale.ROOT, "%s.kpplugin", descriptor.getName());
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return TempFileSystem.getInstance();
    }


    @NotNull
    @Override
    public String getPath() {
        return StringUtil.defaultIfEmpty(descriptor.getUrl(), descriptor.getPluginId().getIdString());
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    @Override
    public @NotNull OutputStream getOutputStream(Object o, long l, long l1) {
        return null;
    }

    @Override
    public byte @NotNull [] contentsToByteArray() {
        return new byte[0];
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public void refresh(boolean b, boolean b1, @Nullable Runnable runnable) {
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    public PluginNode getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if ((object == null) || (getClass() != object.getClass())) {
            return false;
        }
        PluginVirtualFile that;
        if (object instanceof PluginVirtualFile) {
            that = (PluginVirtualFile) object;
            return Objects.equals(descriptor, that.descriptor);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }

    @Override
    public long getModificationStamp() {
        return System.currentTimeMillis();
    }
}