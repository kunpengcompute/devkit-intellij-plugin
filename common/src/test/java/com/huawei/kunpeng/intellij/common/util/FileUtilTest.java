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

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;

import org.junit.Assert;
import org.junit.Test;

/**
 * FileUtil test class
 *
 * @since 2.3.0
 */
public class FileUtilTest {
    /**
     * test FileUtil.validateFileName
     */
    @Test
    public void validateFileName() {
        String fileName1 = "^";
        Assert.assertFalse(FileUtil.validateFileName(fileName1));
        String fileName2 = "`";
        Assert.assertFalse(FileUtil.validateFileName(fileName2));
        String fileName3 = IDEConstant.PATH_SEPARATOR;
        Assert.assertFalse(FileUtil.validateFileName(fileName3));
        String fileName4 = "|";
        Assert.assertFalse(FileUtil.validateFileName(fileName4));
        String fileName5 = ";";
        Assert.assertFalse(FileUtil.validateFileName(fileName5));
        String fileName6 = "&";
        Assert.assertFalse(FileUtil.validateFileName(fileName6));
        String fileName7 = "$";
        Assert.assertFalse(FileUtil.validateFileName(fileName7));
        String fileName8 = ">";
        Assert.assertFalse(FileUtil.validateFileName(fileName8));
        String fileName9 = "<";
        Assert.assertFalse(FileUtil.validateFileName(fileName9));
        String fileName10 = "!";
        Assert.assertFalse(FileUtil.validateFileName(fileName10));
        String fileName11 = " ";
        Assert.assertFalse(FileUtil.validateFileName(fileName11));
        String fileName12 = "a^&*bb";
        Assert.assertFalse(FileUtil.validateFileName(fileName12));
        String fileName13 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Assert.assertTrue(FileUtil.validateFileName(fileName13));
    }

    /**
     * test FileUtil.validateUnzipFileName
     */
    @Test
    public void validateUnzipFileName() {
        String unzipFileName1 = IDEConstant.WINDOW_PATH_SEPARATOR;
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName1));
        String unzipFileName2 = IDEConstant.PATH_SEPARATOR;
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName2));
        String unzipFileName3 = ":";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName3));
        String unzipFileName4 = "*";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName4));
        String unzipFileName5 = "?";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName5));
        String unzipFileName6 = "\"";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName6));
        String unzipFileName7 = "<";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName7));
        String unzipFileName8 = ">";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName8));
        String unzipFileName9 = "|";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName9));
        String unzipFileName10 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Assert.assertTrue(FileUtil.validateUnzipFileName(unzipFileName10));
        String unzipFileName11 = "aA|:32";
        Assert.assertFalse(FileUtil.validateUnzipFileName(unzipFileName11));
    }
}