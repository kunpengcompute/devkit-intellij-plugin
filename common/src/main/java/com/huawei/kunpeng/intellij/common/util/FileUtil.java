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

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.notification.NotificationType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.Normalizer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ?????????????????????
 *
 * @since 1.0.0
 */
public class FileUtil {
    /**
     * ^ ` / | ; & $ > < ! spaces
     */
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[`^/|;&$><!\\s]");

    /**
     * \/:*?"<>|
     */
    private static final Pattern UNZIP_NOT_SUPPORT_FILE_NAME = Pattern.compile("[\\\\/:*?\"<>|]");

    private static final Pattern FILE_PATH_PATTERN = Pattern.compile(".*\\.\\..*");

    private static final Pattern FILE_NAME_CHECK_CN = Pattern.compile("[\u4E00-\u9FA5" +
            "|\\???|\\???|\\???|\\???|\\???|\\???|\\???|\\???|\\???|\\???|\\???|\\???|\\???|\\???]");

    /**
     * ????????????????????????
     */
    public static class ConfigParser {
        /**
         * ??????Properties??????
         *
         * @param propFilePath Properties??????
         * @return map propFile???????????????map
         */
        public static Map<String, String> parseProperties(String propFilePath) {
            if (StringUtil.stringIsEmpty(propFilePath)) {
                throw new IllegalArgumentException("property file cannot be empty");
            }
            if (!propFilePath.toLowerCase(Locale.ROOT).endsWith("properties")) {
                throw new IllegalArgumentException("the file should be end with .properties");
            }
            final Map<String, String> map = new HashMap<>();
            try (InputStream propIn = CommonUtil.getPluginInstalledFile(propFilePath)) {
                propIsNotNull(propFilePath, map, propIn);
            } catch (IOException e) {
                Logger.error("get stream from {} failed", propFilePath, e);
            }
            return map;
        }

        /**
         * ??????json????????? ???????????????
         *
         * @param jsonFile json??????
         * @return String
         */
        public static String readJsonFile(InputStream jsonFile) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(jsonFile, StandardCharsets.UTF_8))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                Logger.error("it is IOException when read Json File failed!!!");
            }
            return sb.toString();
        }

        /**
         * ??????json???????????????Map???JsonObject
         *
         * @param jsonFilePath ????????????/resources???Json????????????
         * @return map json???????????????map
         */
        public static Map parseJsonConfigFromFile(String jsonFilePath) {
            Map map = new HashMap();
            try {
                if (!StringUtil.stringIsEmpty(jsonFilePath) || jsonFilePath.toLowerCase(Locale.ROOT).endsWith("json")) {
                    String jsonString = null;
                    String completePath = CommonUtil.getPluginInstalledPathFile(jsonFilePath);
                    File file = new File(completePath);
                    // ????????????????????????
                    if (!file.exists()) {
                        getFile(CommonUtil.getPluginInstalledPathFile(jsonFilePath), true);
                        // ????????????config.json?????? ???????????????????????????
                        jsonString = readJsonFile(CommonUtil.getPluginInstalledFile(jsonFilePath));
                        saveJsonConfigToFile(jsonString, jsonFilePath);
                    } else {
                        jsonString = readJsonFile(new FileInputStream(file));
                    }
                    map = JsonUtil.getJsonObjFromJsonStr(jsonString);
                }
            } catch (IOException e) {
                Logger.error("it is IOException when parse Json Config failed!!");
            }
            return map;
        }

        /**
         * ???json?????????????????????Map
         *
         * @param jsonFilePath path of file
         * @return Map
         */
        public static Map parseJsonFile2Map(String jsonFilePath) {
            Map result = new HashMap();
            if (!validateFilePath(jsonFilePath)) {
                return result;
            }
            try {
                if (!StringUtil.stringIsEmpty(jsonFilePath) || jsonFilePath.toLowerCase(Locale.ROOT).endsWith("json")) {
                    String jsonString = readJsonFile(new FileInputStream(new File(jsonFilePath)));
                    result = JsonUtil.getJsonObjFromJsonStr(jsonString);
                }
            } catch (FileNotFoundException e) {
                Logger.error("it is IOException when parse Json Config failed!!");
            }
            return result;
        }

        /**
         * ??????Json??????????????????json?????????
         *
         * @param jsonString Json?????????
         * @param jsonFilePath ????????????/resources???Json????????????
         */
        public static void saveJsonConfigToFile(String jsonString, String jsonFilePath) {
            if (!jsonFilePath.toLowerCase(Locale.ROOT).endsWith("json")) {
                return;
            }
            // ????????????????????????
            Optional<File> optionalFile = getFile(CommonUtil.getPluginInstalledPathFile(jsonFilePath), true);
            if (optionalFile.isPresent()) {
                File file = optionalFile.get();
                writeFile(jsonString, file.getPath(), file);
                // ??????????????????
                FileUtil.changeFoldersPermission600(file);
            }
        }
    }

    private static void propIsNotNull(String propFilePath, Map<String, String> map, InputStream propIn) {
        Optional.ofNullable(propIn).ifPresent(inputStream -> {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
                properties.forEach((key, value) -> {
                    if (key instanceof String && value instanceof String) {
                        map.put((String) key, (String) value);
                    }
                });
            } catch (IOException e) {
                Logger.error("can't parse {}", propFilePath, e);
            }
        });
    }

    /**
     * ?????????????????????
     *
     * @param stream ??????????????????
     * @param streamsOption ????????????????????????
     */
    public static void closeStreams(Closeable stream, StreamOption streamsOption) {
        if (stream != null) {
            try {
                stream.close();
                if (streamsOption != null) {
                    streamsOption.closeStreams();
                }
            } catch (IOException e) {
                Logger.error("closeStreams error IOException");
            }
        }
        if (streamsOption != null) {
            streamsOption.closeStreams();
        }
    }

    /**
     * ??????????????? inExistentIsNew???true??????????????????????????????
     *
     * @param fileCompletePath ??????????????????
     * @param inExistentIsNew ?????????????????????
     * @return File file
     */
    public static Optional<File> getFile(String fileCompletePath, boolean inExistentIsNew) {
        String[] paths = fileCompletePath.split(IDEConstant.PATH_SEPARATOR);
        if (!validateFilePath(fileCompletePath)) {
            return Optional.empty();
        }
        File file = new File(fileCompletePath);
        File path = new File(
                fileCompletePath.substring(0, fileCompletePath.length() - paths[paths.length - 1].length()));
        // ?????????????????????????????????
        try {
            if (inExistentIsNew) {
                if (!path.exists() && !path.mkdirs()) {
                    Logger.error("mkdirs error when getFile");
                }
                if (!file.exists() && !file.createNewFile()) {
                    Logger.error("createNewFile error when getFile");
                }
            }
        } catch (IOException e) {
            Logger.error("getFile error IOException");
        }
        return Optional.of(file);
    }

    /**
     * ?????????ZIP
     *
     * @param file ?????????????????????????????????
     * @return out ?????????????????????
     * @throws RuntimeException ????????????????????????????????????
     */
    public static File fileToZip(File file) {
        if (file == null || file.isFile()) {
            return file;
        }
        if (!FileUtil.validateFileName(file.getName())) {
            Logger.error("validateFileName error!!!");
            return file;
        }
        // ??????????????????
        Optional<File> fileOptional = getFile(CommonUtil.getPluginInstalledPathFile(
                IDEConstant.PORTING_WORKSPACE_TEMP + IDEConstant.PATH_SEPARATOR + file.getName()) + ".zip", true);
        File outFile = null;
        if (!fileOptional.isPresent()) {
            return outFile;
        }
        outFile = fileOptional.get();

        // ????????????????????????????????????
        try (OutputStream out = new FileOutputStream(outFile); ZipOutputStream zos = new ZipOutputStream(out)) {
            compressFile(file, zos, file.getName());
        } catch (IOException e) {
            Logger.error("it is IOException when zip fileToZip error!!!");
        }
        return outFile;
    }

    /**
     * zip????????????
     *
     * @param inputZipFile ?????????zip??????
     * @param destDirPath ????????????
     */
    public static void unzipFile(String inputZipFile, String destDirPath) {
        if (!FileUtil.validateFilePath(inputZipFile) || !FileUtil.validateFilePath(destDirPath)) {
            return;
        }
        File srcFile = new File(inputZipFile);
        if (!srcFile.exists()) {
            Logger.error("unzipFile, the file does not exist.");
        }
        doUnzipDetails(srcFile, destDirPath);
    }

    /**
     * ????????????????????????
     *
     * @param dir dir
     * @return ?????????????????????
     */
    public static boolean isNotEmptyDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (dir.isFile()) {
            return true;
        }
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files == null) {
                return false;
            }
            for (File file : files) {
                if (isNotEmptyDir(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void doUnzipDetails(File srcFile, String destDirPath) {
        if (!validateFilePath(srcFile.getPath()) || !FileUtil.validateFilePath(destDirPath)) {
            return;
        }
        try (ZipFile zipFile = new ZipFile(srcFile, Charset.forName(IDEConstant.CHARSET_GBK))) {
            // ????????????
            ZipEntry entry = null;
            Object obj = null;
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                obj = entries.nextElement();
                if (!(obj instanceof ZipEntry)) {
                    return;
                }
                entry = (ZipEntry) obj;
                // ???????????????????????????porting/??????porting/assert/???????????????porting/assert/image.jpg
                String[] folderNames = entry.getName().split(IDEConstant.PATH_SEPARATOR);
                if (!FileUtil.validateUnzipFileName(folderNames[folderNames.length - 1])) {
                    Logger.error("validateFileName error when unzipFile");
                    break;
                }
                // ??????????????????????????????
                if (entry.isDirectory()) {
                    mkdirForDest(destDirPath, entry);
                    continue;
                }
                // ???????????????????????????
                File targetFile = new File(destDirPath + IDEConstant.PATH_SEPARATOR + entry.getName());
                if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()
                        && !targetFile.getParentFile().mkdirs()) {
                    Logger.error("mk Parent dirs error  when unzipFile");
                }

                if (!targetFile.createNewFile()) {
                    Logger.error("createNewFile error  when unzipFile");
                }

                // ?????????????????????????????????????????????
                writeZipToFile(zipFile, entry, targetFile);
            }
        } catch (IOException e) {
            Logger.error("unzipFile error, IOException!!!");
        }
    }

    private static void writeZipToFile(ZipFile zipFile, ZipEntry entry, File targetFile) {
        if (!validateFilePath(targetFile.getPath())) {
            return;
        }
        try (InputStream is = zipFile.getInputStream(entry); FileOutputStream fos = new FileOutputStream(targetFile)) {
            int len;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        } catch (IOException exception) {
            Logger.error("unzipFile error, IOException!!!");
        }
    }

    /**
     * ???????????????????????????
     *
     * @param sourcePath ???????????????
     * @param targetPath ??????????????????
     */
    public static void copyFolder(String sourcePath, String targetPath) {
        // ??????????????????
        File sourceFile = new File(sourcePath);
        // ?????????????????????
        File targetFile = new File(targetPath);
        if (!sourceFile.exists()) {
            Logger.error("The file is not exist.");
            return;
        }
        if (!sourceFile.isDirectory()) {
            Logger.error("Source file is not directory.");
            return;
        }
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        if (!targetFile.isDirectory()) {
            Logger.error("Target file is not directory.");
        }
        File[] files = sourceFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            helpCopy(file, targetFile);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param file       ???????????????
     * @param targetFile ????????????
     */
    private static void helpCopy(File file, File targetFile) {
        String movePath = targetFile + File.separator + file.getName();
        if (file.isDirectory()) {
            try {
                copyFolder(file.getCanonicalPath(), movePath);
            } catch (IOException exception) {
                Logger.error("File get path have error.");
            }
        } else {
            try (
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(movePath))
            ) {
                byte[] bytes = new byte[1024];
                int temp;
                while ((temp = in.read(bytes)) != -1) {
                    out.write(bytes, 0, temp);
                }
            } catch (IOException exception) {
                Logger.error("Copy folder have error.");
            }
        }
    }

    /**
     * ????????????dir
     *
     * @param destDirPath destDirPath
     * @param entry       entry
     */
    private static void mkdirForDest(String destDirPath, ZipEntry entry) {
        File dirPath = new File(destDirPath + IDEConstant.PATH_SEPARATOR + entry.getName());
        if (!dirPath.mkdirs()) {
            Logger.error("mkdirs error  when unzipFile");
        }
    }

    /**
     * ?????????????????????
     *
     * @param folder ?????????
     * @param zos zip?????????
     * @param fileName ?????????
     */
    private static void compressFile(File folder, ZipOutputStream zos, String fileName) {
        byte[] buf = new byte[2 * 1024];
        FileInputStream in = null;
        try {
            if (folder.isFile()) {
                int length;
                // ???zip??????????????????zip??????
                zos.putNextEntry(new ZipEntry(fileName));
                in = new FileInputStream(folder);
                while ((length = in.read(buf)) != -1) {
                    zos.write(buf, 0, length);
                }

                // ????????????
                zos.closeEntry();
            } else {
                File[] listFiles = folder.listFiles();
                if (listFiles == null || listFiles.length == 0) {
                    // ????????????
                    zos.putNextEntry(new ZipEntry(fileName + IDEConstant.PATH_SEPARATOR));
                    zos.closeEntry();
                    return;
                }
                // ????????????
                for (File file : listFiles) {
                    compressFile(file, zos, fileName + IDEConstant.PATH_SEPARATOR + file.getName());
                }
            }
        } catch (IOException e) {
            Logger.error("it is Exception when zip compressFile error!!!");
        } finally {
            closeStreams(in, null);
        }
    }

    /**
     * ?????????????????????file??????null?????????file??????
     *
     * @param dirFile ?????????????????????
     * @param dirPath ?????????????????????
     * @return boolean ????????????
     */
    public static boolean deleteDir(File dirFile, String dirPath) {
        File file = dirFile;
        if (file == null) {
            if (StringUtil.stringIsEmpty(dirPath)) {
                return true;
            }
            file = new File(dirPath);
        }

        if (!file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            String[] fileList = file.list();
            // ????????????????????????????????????
            for (String child : fileList) {
                boolean isSuccess = deleteDir(new File(file, child), null);
                if (!isSuccess) {
                    return false;
                }
            }
        }
        // ??????dirPath???????????????
        return file.delete();
    }

    /**
     * ????????????????????????,???file??????file??????
     *
     * @param content ??????
     * @param filePath ????????????
     * @param file ??????
     */
    public static void writeFile(String content, String filePath, File file) {
        File fileDef = file;
        if (fileDef == null) {
            fileDef = new File(filePath);
            changeFoldersPermission600(fileDef);
        }
        // ????????????
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileDef), StandardCharsets.UTF_8))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            Logger.error("IOException when writeFile error!!");
        }
    }

    /**
     * string???????????????TXT??????
     *
     * @param content ??????
     * @param filePath ????????????
     */
    public static void saveAsFileWriter(String content, String filePath) {
        // true???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        try (FileWriter fwt = new FileWriter(filePath, true)) {
            fwt.write(content);
        } catch (IOException e) {
            Logger.error("IOException when writeFile error!!");
        }
    }

    /**
     * ?????????????????????600
     *
     * @param file ??????
     */
    public static void changeFoldersPermission600(File file) {
        if (file == null) {
            return;
        }
        if (IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
            file.setReadable(true);
            file.setWritable(true);
        } else {
            try {
                Set<PosixFilePermission> set = new HashSet<>();
                set.add(PosixFilePermission.OWNER_READ);
                set.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(file.toPath(), set);
            } catch (IOException e) {
                Logger.error("change file permission error???");
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param fileName ????????????
     * @return string fileContent
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        StringBuilder sbf = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr).append(IDEConstant.LINE_SEPARATOR);
            }
        } catch (IOException e) {
            Logger.error("IOException when Read file content occur error.");
        }
        return sbf.toString();
    }

    /**
     * ??????????????????jar??????????????????basePath?????????destFile
     *
     * @param destFile ????????????
     * @param basePath ???jar?????????????????????
     * @param inExistentIsNew ?????????????????????
     */
    public static void readAndWriterFileFromJar(File destFile, String basePath, boolean inExistentIsNew) {
        if (!destFile.exists() || inExistentIsNew) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = CommonUtil.getPluginInstalledFile(basePath);
                os = new FileOutputStream(String.valueOf(destFile.toPath()));
                int readBytes;
                byte[] buffer = new byte[1024];
                while ((readBytes = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }
            } catch (IOException e) {
                Logger.error("getDPAPIExe error");
            } finally {
                closeStreams(os, null);
                closeStreams(is, null);
            }
        }
    }


    /**
     * ?????????????????????
     *
     * @since 2020-09-25
     */
    public interface StreamOption {
        /**
         * ?????????????????????
         */
        void closeStreams();
    }

    /**
     * ??????????????????
     *
     * @param filePath filePath
     * @return ture or false
     */
    public static boolean checkKey(String filePath) {
        if (!isValidateFile(filePath, 10)) {
            return false;
        }
        String keyData = readFileContent(filePath);
        if (keyData.contains("-----BEGIN") && keyData.contains("-----END")) {
            return true;
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param filePath filePath
     * @param sizeInMb sizeInMb
     * @return boolean validateResult
     */
    public static boolean isValidateFile(String filePath, long sizeInMb) {
        if (!ValidateUtils.isNotEmptyString(filePath) && FileUtil.validateFilePath(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            Logger.info("File specified not existed.");
            IDENotificationUtil.notificationCommon(
                    new NotificationBean("", I18NServer.toLocale("plugins_common_message_localFileNotExist"),
                            NotificationType.ERROR));
            return false;
        }
        long size = file.length() / 1024 / 1024;
        if (size > sizeInMb) {
            Logger.info("File size greater than {} MB.", sizeInMb);
            IDENotificationUtil.notificationCommon(
                    new NotificationBean("", I18NServer.toLocale("plugins_common_keyFileExceedMaxSize")
                            .replace("10", Long.toString(sizeInMb)),
                            NotificationType.WARNING));
            return false;
        }
        return true;
    }

    /**
     * validate path of the file???throw exception when '../' exists in path
     *
     * @param path path
     * @return boolean validateResult
     */
    public static boolean validateFilePath(String path) {
        if (!StringUtil.stringIsEmpty(path)) {
            String tempStr = Normalizer.normalize(path, Normalizer.Form.NFKC);
            boolean check = FILE_PATH_PATTERN.matcher(tempStr).matches();
            if (check) {
                Logger.error("the is unsafe path");
            }
            return !check;
        }
        return true;
    }

    /**
     * ?????????????????????????????????
     *
     * @param str ??????????????????
     * @return true ????????????????????? ????????????
     */
    public static boolean isContainChinese(String str) {
        if (!StringUtil.stringIsEmpty(str)) {
            String tempStr = Normalizer.normalize(str, Normalizer.Form.NFKC);
            Matcher check = FILE_NAME_CHECK_CN.matcher(tempStr);
            if (check.find()) {
                Logger.info("The string is contain chinese.");
                return true;
            }
        }
        return false;
    }

    /**
     * validate name of the file???return false when '^`/|;&$><!' or spaces exists in name
     *
     * @param fileName name
     * @return boolean validateResult
     */
    public static boolean validateFileName(String fileName) {
        return regularMatch(fileName, FILE_NAME_PATTERN);
    }

    /**
     * validate name of the file???return false when '\/:*?"<>|' exists in name
     *
     * @param fileName name
     * @return boolean validateResult
     */
    public static boolean validateUnzipFileName(String fileName) {
        return regularMatch(fileName, UNZIP_NOT_SUPPORT_FILE_NAME);
    }

    private static boolean regularMatch(String fileName, Pattern pattern) {
        if (!StringUtil.stringIsEmpty(fileName)) {
            String tempStr = Normalizer.normalize(fileName, Normalizer.Form.NFKC);
            boolean check = pattern.matcher(tempStr).find();
            if (check) {
                Logger.error("unsafe fileName: {}", fileName);
            }
            return !check;
        }
        return true;
    }

    /**
     * update config.json cert
     *
     * @param certFilePath file
     */
    public static void updateCertConfig(String certFilePath) {
        if (!FileUtil.validateFilePath(certFilePath)) {
            return;
        }
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        config.put("certPath", StringUtil.stringIsEmpty(certFilePath) ? "" : certFilePath);
        FileUtil.ConfigParser.saveJsonConfigToFile(config.toString(), IDEConstant.CONFIG_PATH);
    }

    /**
     * remove config.json cert
     */
    public static void removeCertConfig() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        config.remove("certPath");
        FileUtil.ConfigParser.saveJsonConfigToFile(config.toString(), IDEConstant.CONFIG_PATH);
    }

    /**
     * ????????????????????????
     *
     * @param path ????????????
     */
    public static void clearFileContent(String path) {
        writeDataToFile("", path);
    }

    /**
     * ?????????content?????????????????????????????????????????????/????????????
     *
     * @param content content
     * @param path ????????????
     */
    public static void writeDataToFile(String content, String path) {
        try {
            writeToFile(content, path);
            RestrictedFileUtils.restrictSecurityFileAccess(path);
        } catch (IOException e) {
            Logger.error("failed to write content to file {}. stack trace : ", path, e);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param content content
     * @param path ????????????
     * @throws IOException
     */
    public static void writeToFile(String content, String path) throws IOException {
        File file = new File(path);
        String firstNoExistParentPath = findFirstNoExistParentPath(path);
        if (!file.exists() && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!StringUtil.stringIsEmpty(firstNoExistParentPath)) {
            RestrictedFileUtils.restrictReadOnlyFileAccess(firstNoExistParentPath);
        }
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            writer.flush();
        }
    }

    /**
     * ????????????path????????????????????????????????????
     *
     * @param path path
     * @return ???????????????
     */
    public static String findFirstNoExistParentPath(String path) {
        String tempPath = null;
        File file = new File(path);
        while (!file.exists()) {
            tempPath = file.getPath();
            file = file.getParentFile();
        }
        return tempPath;
    }

    /**
     * ???.p??????????????????key???val
     *
     * @param keyStr use key to find value from property file
     * @return val
     */
    public static Optional<String> getPropertiesVal(String keyStr) {
        String filePath = getPropertiesFilePath();
        Properties prop = new Properties();
        String result = null;
        try (
                FileInputStream fi = new FileInputStream(filePath);
                InputStreamReader in = new InputStreamReader(fi, StandardCharsets.UTF_8)
        ) {
            prop.load(in);
            result = prop.getProperty(keyStr);
        } catch (IOException e) {
            Logger.warn("failed to get properties val. The prop file does not exist.");
        }
        return Optional.ofNullable(result);
    }

    /**
     * get .p file path
     *
     * @return .p file path
     */
    public static String getPropertiesFilePath() {
        if (!validateFilePath(CommonUtil.getCurUserCryptRootPath())) {
            return "";
        }
        return Paths.get(CommonUtil.getCurUserCryptRootPath(), IDEConstant.CRYPT_DIR,
                IDEConstant.PROPERTIES_NAME).toString();
    }

    /**
     * save working key to .p file
     *
     * @param keyStr           key id
     * @param workingKeyCipher cipher text
     * @return the result
     */
    public static boolean saveWorkingKeyCipherText(String keyStr, String workingKeyCipher) {
        boolean result = false;
        String filePath = getPropertiesFilePath();
        File file = Paths.get(filePath).toFile();
        if (!Files.exists(Paths.get(filePath))) {
            try {
                touch(file);
                RestrictedFileUtils.restrictSecurityFileAccess(filePath);
            } catch (IOException e) {
                Logger.warn("failed to get working key. The certificate file does not exist.");
                return false;
            }
        }
        try (
                InputStream inputStream = new FileInputStream(filePath)
        ) {
            Properties prop = new Properties();
            prop.load(inputStream);
            prop.setProperty(keyStr, workingKeyCipher);
            try (
                    OutputStream outputStream = new FileOutputStream(filePath);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
            ) {
                prop.store(outputStreamWriter, "Update value");
            }
            result = true;
            Logger.info("success to save the cipher text of working key.");
        } catch (IOException e) {
            Logger.error("failed to save the cipher text of working key. e.getMessage() : {}", e.getMessage());
        }
        return result;
    }

    /**
     * ??????????????????????????????
     *
     * @param file ????????????
     * @throws IOException
     */
    private static void touch(File file) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.mkdirs() && !parentFile.isDirectory()) {
            throw new IOException();
        }
        new FileOutputStream(file).close();
    }

    /**
     * ???????????????????????????
     *
     * @param path filePath
     * @return content
     */
    public static String cat(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            Logger.error("failed to read file {}. e.getMessage : {}", path, e.getMessage());
        }
        return "";
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param file ??????
     * @param maxSize ??????????????????????????????maxSize????????????
     * @return ?????????????????????
     */
    public static long getTotalSizeOfDirectory(File file, long maxSize) {
        long total = 0;
        if (file.isFile()) {
            return file.length();
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File child : files) {
                total = total + getTotalSizeOfDirectory(child, maxSize);
                if (total > maxSize) {
                    return total;
                }
            }
        }
        return total;
    }

    /**
     * ???json???????????????????????????????????????????????????
     *
     * @param jsonString json?????????
     * @param filePath   ????????????
     * @param fileName   ????????????
     * @return ??????????????????
     * @throws IOException IOException
     */
    public static boolean createJsonFile(String jsonString, String filePath, String fileName) throws IOException {
        String strFormat = jsonString;
        if (fileName.endsWith(".json")) {
            // ??????json????????????
            if (strFormat.contains("'")) {
                // ?????????????????????????????????JSON???????????????????????????????????????????????????
                strFormat = strFormat.replaceAll("'", "\\'");
            }
            if (strFormat.contains("\"")) {
                // ?????????????????????????????????JSON???????????????????????????????????????????????????
                strFormat = strFormat.replaceAll("\"", "\\\"");
            }
            if (strFormat.contains("\r\n")) {
                // ????????????????????????????????????JSON????????????????????????????????????????????????
                strFormat = strFormat.replaceAll("\r\n", "\\u000d\\u000a");
            }
            if (strFormat.contains("\n")) {
                // ??????????????????????????????JSON??????????????????????????????????????????
                strFormat = strFormat.replaceAll("\n", "\\u000a");
            }
        }
        // ????????????????????????
        String fullPath = filePath + File.separator + fileName;
        if (!validateFilePath(fullPath)) {
            Logger.error("File path invalid.");
            return false;
        }
        // ???????????????????????????
        File file = new File(fullPath);
        if (!file.getParentFile().exists()) {
            // ??????????????????????????????????????????
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            // ???????????????,???????????????
            file.delete();
        }
        // ??????????????????????????????
        boolean flag = true;
        boolean isFile = file.createNewFile();
        if (!isFile) {
            Logger.error("Its create new File fail when export Profiling sampling!");
            flag = false;
        }
        // ???????????????????????????????????????
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                Writer write = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        ) {
            write.write(strFormat);
            write.flush();
        } catch (IOException ex) {
            throw new IOException("Its IOException when export Profiling sampling!");
        }
        // ???????????????????????????
        return flag;
    }
}
