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

package com.huawei.kunpeng.intellij.common.crypto.km;

import static com.huawei.kunpeng.intellij.common.util.FileUtil.clearFileContent;
import static com.huawei.kunpeng.intellij.common.util.FileUtil.writeDataToFile;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.crypto.AsymmetricCrypto;
import com.huawei.kunpeng.intellij.common.crypto.AsymmetricEncryptors;
import com.huawei.kunpeng.intellij.common.crypto.RandomStringGenerator;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * root key management
 *
 * @since 1.0.0
 */
public class RootKeyManager {
    /**
     * 根密钥组件长度(字节)
     */
    private static final int ROOT_KEY_LENGTH = 1024;

    /**
     * 根密钥组件1名称
     */
    private static final String ROOT_KEY_FIRST_COMPONENT_FILE = "N7oRkH0yfWyv7iHpB7fX9ZCsQWrLSXQP";

    /**
     * 根密钥组件2名称
     */
    private static final String ROOT_KEY_SECOND_COMPONENT_FILE = "Iqd6GuJfaavpBZwjhf5hGWWlVID1qat4";

    /**
     * 根秘钥组件1路径
     */
    private static final String ROOT_KEY_FIRST_COMPONENT_DIR = ".f";

    /**
     * 根秘钥组件2
     */
    private static final String ROOT_KEY_SECOND_COMPONENT_DIR = ".s";

    /**
     * 根秘钥组件3：1024bit
     */
    private static final String ROOT_KEY_COMPONENT = "a0cjJLNZhiriR0rVBTTi4eruR6WWwZDR66ao2gt2k3k16lv15ieUlhThfKXv" +
            "CLF55qwB9QMUiPOWhfo11OLUxLeDim5Vs7mwTWsW9b36aSZ7JIZvUOoyPZ0gKjar8S5HFDdp7JZIW5nmeUTg0xqa4UaLclAU1MxjT" +
            "If1HgDtBCVYXsphkhBss72VqLM5XVx5Wo0NvYXI0fBmIvqNjrVOwotbkEh5xZujPTkIJpwMxRtiBZxcVNdpn4k2EtuAlrGcUnIz8m" +
            "2ZoSwD3SuDSdBBae7KD8iHGustAyAbwTSGUPDtyl9ZFuKoE9jIJMd9Ott4FNefr5fLZHrZXDhz3kauZfiGdmjkFDdzxNrao4knr13" +
            "E38rzaUATHeXnHHxFjoN3Iuo6CoqR8BDqS5HsOzBoLOT51wqcOBWQhcXJpWnfe90tVfCQUHcf6SOfUggIJ2gAmRIoUyhYBjqjvsAy" +
            "QXNUZxzuX11A6mavThHlEVhKhu4mf74zGEhPk2XoppDMCahCakN4QIGWWsU1N3SiQfFYEZ14G9Ll6AtIGDAr9SFxBxOJJXJi6CWuk" +
            "EXhYbKGqoMJUR7OIuCwjiydkvmk7d97Pw4TSWiIK9hIxBZdufkTL7ImyQqsNOhFkn5KnMYMU5rEEhqHQAG7CoCHCY0IuFbd5MbxBZH" +
            "wrCNlwQPGjUQM7Fwc2ktWd5ye7plHFLnacXUCNu5zkpgkpT9s7uKFmQAbbKOQO7Y0b7lnYCW9FDsSC8DdNgkGRXTBbS01oykL0dJ7" +
            "LmIrkUSxwusvLW0Itlwpj6jEALuWpPhszrpVzTbBonZUBSAt3Iig6VQnpxyT5lsKerMFjBgmzhtbe56rEjw6MSNQ4nxHp3UoR7wAT" +
            "YwVT6jTSQE0HsX6wgGzjvc5qW7LS1wk9JKtb9BI5JLWmGHm16Emo2PL88IXcl7vAHU3t7ZA6s0Cacs57lIhTzxWYHbLC9lc7QkaVL" +
            "p82fe1cWVl80aEnZpxJM4bnw8RoAdFqRZzBnN4i4OWpEO54Rq8k1aU";

    /**
     * get root key first component file
     *
     * @return first component path
     */
    public static String getRootKeyFirstComponentFile() {
        if (!FileUtil.validateFilePath(CommonUtil.getCurUserCryptRootPath())) {
            return "";
        }
        return Paths.get(CommonUtil.getCurUserCryptRootPath(), IDEConstant.CRYPT_DIR, ROOT_KEY_FIRST_COMPONENT_DIR,
                ROOT_KEY_FIRST_COMPONENT_FILE).toString();
    }

    /**
     * get root key second component file
     *
     * @return second component path
     */
    public static String getRootKeySecondComponentFile() {
        if (!FileUtil.validateFilePath(CommonUtil.getCurUserCryptRootPath())) {
            return "";
        }
        return Paths.get(CommonUtil.getCurUserCryptRootPath(), IDEConstant.CRYPT_DIR, ROOT_KEY_SECOND_COMPONENT_DIR,
                ROOT_KEY_SECOND_COMPONENT_FILE).toString();
    }

    /**
     * get root key from file
     *
     * @return root key
     */
    public String getRootKey() {
        initRootKeyIfNot();
        String keyStringFirst = FileUtil.cat(getRootKeyFirstComponentFile());
        String keyStringSecond = FileUtil.cat(getRootKeySecondComponentFile());
        return mixRootKey(keyStringFirst, keyStringSecond, ROOT_KEY_COMPONENT);
    }

    private String mixRootKey(String keyStringWork, String keyStringConf, String keyStringRoot) {
        if (keyStringWork.isEmpty() || keyStringConf.isEmpty()) {
            Logger.error("root key initialized error because keyStringWork/keyStringConf is empty.");
            return "";
        }
        if (keyStringConf.getBytes(StandardCharsets.UTF_8).length != keyStringWork.getBytes(
                StandardCharsets.UTF_8).length) {
            Logger.error("the length of key components not equal ");
            return "";
        }
        AsymmetricCrypto crypto = AsymmetricEncryptors.getInstance();
        String keyString = mix(mix(keyStringWork, keyStringConf), keyStringRoot);
        // root_key
        return crypto.encrypt(keyString);
    }

    /**
     * root key initialization
     */
    public synchronized void initRootKeyIfNot() {
        String conf = getRootKeyFirstComponentFile();
        String work = getRootKeySecondComponentFile();
        if (Files.exists(Paths.get(conf)) && Files.exists(Paths.get(work))) {
            return;
        }
        createRandomForRoot(conf, work);
    }

    /**
     * create root key
     *
     * @param conf conf file
     * @param work work file
     * @return Whether the root key is generated successfully
     */
    public synchronized boolean createRandomForRoot(String conf, String work) {
        String keyStringConf = RandomStringGenerator.generate(ROOT_KEY_LENGTH);
        String keyStringWork = RandomStringGenerator.generate(ROOT_KEY_LENGTH);
        if (keyStringWork.isEmpty() || keyStringConf.isEmpty()) {
            Logger.error("root key initialized error because keyStringWork/keyStringConf is empty.");
            return false;
        }
        if (keyStringConf.getBytes(StandardCharsets.UTF_8).length != keyStringWork.getBytes(
                StandardCharsets.UTF_8).length) {
            Logger.error("the length of key components not equal.");
            return false;
        }
        clearFileContent(conf);
        writeDataToFile(keyStringConf, conf);
        clearFileContent(work);
        writeDataToFile(keyStringWork, work);
        return true;
    }

    private String mix(String first, String second) {
        byte[] firstBytes = first.getBytes(StandardCharsets.UTF_8);
        byte[] secondBytes = second.getBytes(StandardCharsets.UTF_8);
        int length = firstBytes.length;
        if (firstBytes.length != secondBytes.length) {
            throw new IllegalArgumentException("non equal length.");
        }
        byte[] mixed = new byte[length];
        for (int i = 0; i < length; i++) {
            mixed[i] = (byte) (firstBytes[i] ^ secondBytes[i]);
        }
        return new String(mixed, StandardCharsets.UTF_8);
    }
}
