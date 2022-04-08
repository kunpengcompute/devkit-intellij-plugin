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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * <p>文件分割实用程序</p>
 * <p>StorageCapacityUnit</p>
 * B(Byte) = 8 bits<br/>
 * KB(KiloByte) = 1,024 B<br/>
 * MB(MegaByte) = 1,024 KB = 1,048,576 B<br/>
 * GB(GigaByte) = 1,024 MB = 1,073,741,824 B<br/>
 * TB(TeraByte) = 1,024 GB = 1,099,511,627,776 B<br/>
 * PB(PetaByte) = 1,024 TB = 1,125,899,906,842,624 B<br/>
 * EB(ExaByte) = 1,024 PB = 1,152,921,504,606,846,976 B<br/>
 * ZB(ZettaByte) = 1,024 EB = 1,180,591,620,717,411,303,424 B<br/>
 * YB(YottaByte) = 1,024 ZB = 1,208,925,819,614,629,174,706,176 B<br/>
 *
 * @since 2021/8/26
 */
public class FileSplitUtils {
    /**
     * 文件路径
     */
    private Path filePath;
    /**
     * 文件大小
     */
    private long fileLength;
    /**
     * 被分割文件块大小
     */
    private long blockSize;
    /**
     * 被分割文件块数量
     */
    private long blockLength;

    private FileSplitUtils() {
        this.fileLength = 0L;
        // 默认分割块大小为 16 MB(MegaByte) = 16384 KB(KiloByte) = 16777216 B(Byte) --> Hex:0x1000000
        this.blockSize = 0x1000000L;
        // 默认分割块数量 0 (表示不可分割)
        this.blockLength = 0L;
    }

    /**
     * Get Instance
     *
     * @return {@link FileSplitUtils}
     */
    public static FileSplitUtils getInstance() {
        return FileSplitUtilsHolder.INSTANCE;
    }

    /**
     * Get FileLength
     * <p>Please call {@link FileSplitUtils#getBlockLength(Path, long)} first</p>
     *
     * @return fileLength
     */
    public long getFileLength() {
        return this.fileLength;
    }

    /**
     * Get BlockSize
     * <p>Please call {@link FileSplitUtils#getBlockLength(Path, long)} first</p>
     *
     * @return blockSize
     */
    public long getBlockSize() {
        return this.blockSize;
    }

    /**
     * 根据文件分割块的大小获取文件可分割数量
     *
     * @param filePath  文件路径 {@link Path}
     * @param blockSize 分割的文件块儿大小 B(Byte).指定范围: <br>Min: blockSize &gt; 0  Max: blockSize &le; File.size()<br/>
     * @return 0 -> 不可分割
     */
    public long getBlockLength(Path filePath, long blockSize) {
        if (!isFileExistsAndReadable(filePath)) {
            Logger.error("Please check if the file exists and is readable [symbol path is not allowed]");
            return this.blockLength;
        }
        try {
            this.filePath = filePath;
            this.fileLength = Files.size(this.filePath);
            if (this.fileLength == 0L || blockSize == 0L) {
                return this.blockLength;
            }
            if (blockSize > 0L && blockSize <= this.fileLength) {
                this.blockSize = blockSize;
                this.blockLength = this.calculate(this.fileLength, blockSize);
            } else if (blockSize > this.fileLength) {
                this.blockSize = this.fileLength;
                this.blockLength = 1L;
            } else {
                this.blockLength = this.calculate(this.fileLength, this.blockSize);
            }
        } catch (IOException e) {
            Logger.error("Failed to read file[The file cannot be divided].");
        }
        return this.blockLength;
    }

    private long calculate(long valueA, long valueB) {
        long valueC = valueA / valueB;
        if (valueA % valueB > 0L) {
            valueC += 1;
        }
        return valueC;
    }

    private boolean isFileExistsAndReadable(Path filePath) {
        return Files.exists(filePath) && !Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS) &&
                Files.isReadable(filePath);
    }

    /**
     * 文件分割
     *
     * @param blockIndex 被分割文件的文件块索引[索引从1开始.如果索引为 {@code blockIndex <= 0L || blockIndex > blockLength} 则将视为不可读取文件,
     *                   将返回空白的IO]
     * @return {@link ByteBuffer}
     * @throws {@link IOException}
     */
    public ByteBuffer split(long blockIndex) throws IOException {
        long position = 0L;
        long size = this.blockSize;
        if (blockLength == position) {
            throw new IOException("Files that cannot be divided");
        }
        if (blockIndex <= position || blockIndex > blockLength) {
            size = position;
        } else {
            position = (blockIndex - 1) * blockSize;
            if (blockIndex == blockLength) {
                size = fileLength - position;
            }
        }
        try (FileChannel fileChannel = FileChannel.open(this.filePath, StandardOpenOption.READ)) {
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, position, size);
        } catch (IOException e) {
            throw new IOException("Files that cannot be divided");
        }
    }

    /**
     * Get InputStream
     *
     * @param byteBuffer {@link FileSplitUtils#split(long)}
     * @return {@link ByteBufferInputStreamAc}
     */
    public ByteBufferInputStreamAc getInputStream(ByteBuffer byteBuffer) {
        return new ByteBufferInputStreamAc(byteBuffer);
    }

    private static class FileSplitUtilsHolder {
        private static final FileSplitUtils INSTANCE = new FileSplitUtils();
    }
}