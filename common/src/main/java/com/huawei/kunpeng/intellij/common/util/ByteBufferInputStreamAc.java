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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * ByteBuffer's InputStream implementation
 *
 * @see java.io.InputStream
 * @see java.io.ByteArrayInputStream
 * @see java.nio.ByteBuffer
 * @since 2021/9/4
 */
public class ByteBufferInputStreamAc extends InputStream {
    private ByteBuffer byteBuffer;

    public ByteBufferInputStreamAc(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public int read() throws IOException {
        return this.byteBuffer.hasRemaining() ? this.byteBuffer.get() & 0xff : -1;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return this.read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (this.byteBuffer.hasRemaining()) {
            int len1 = Math.min(len, this.byteBuffer.remaining());
            this.byteBuffer.get(bytes, off, len1);
            return len1;
        }
        return -1;
    }

    @Override
    public long skip(long num) throws IOException {
        if (num > Integer.MAX_VALUE) {
            throw new IOException("Exceeded the maximum boundary of skip");
        }
        int availableSize = this.available();
        int skippedIndex = (int) num;
        if (skippedIndex < availableSize) {
            availableSize = Math.max(skippedIndex, 0);
        }
        this.byteBuffer.position(this.byteBuffer.position() + availableSize);
        return availableSize;
    }

    @Override
    public int available() {
        return this.byteBuffer.remaining();
    }

    @Override
    public void close() throws IOException {
        this.byteBuffer = null;
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.byteBuffer.mark();
    }

    @Override
    public synchronized void reset() throws IOException {
        this.byteBuffer.reset();
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}