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
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 *
 * @since 2020-09-25
 */
public class StringUtil {
    /**
     * 通过当前系统自动转换字符串编码，用于向服务器发送数据
     *
     * @param str 原始串
     * @return String 转换后的字符串
     */
    public static String getStrCharsetByOSToServer(final String str) {
        if (IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.LINUX) {
            return getStrFromDiffCharset(str, IDEConstant.CHARSET_UTF8, IDEConstant.CHARSET_UTF8);
        } else {
            return getStrFromDiffCharset(str, IDEConstant.CHARSET_UTF8, IDEConstant.CHARSET_ISO_8859_1);
        }
    }

    /**
     * 字符串编码从srcCharsetName转换为disCharsetName
     *
     * @param str 原始串
     * @param srcCharsetName 原始编码
     * @param disCharsetName 结果编码
     * @return String 原始串的结果编码字符串
     */
    public static String getStrFromDiffCharset(final String str, String srcCharsetName, String disCharsetName) {
        if (srcCharsetName.equals(disCharsetName) || str == null) {
            return str;
        }
        String result = str;
        try {
            result = new String(str.getBytes(srcCharsetName), disCharsetName);
        } catch (UnsupportedEncodingException e) {
            Logger.error("UnsupportedEncodingException error.");
        }
        return result;
    }

    /**
     * 解析JsonStr型的路径参数添加到url后，组成包含路径参数的完整url
     *
     * @param url url
     * @param urlParams url路径参数
     * @return String key01=value01&key02=value02
     */
    public static String getUrlIncludeParams(String url, String urlParams) {
        String result = url;

        // url如果也已经包含路径参数，则不会将urlParams添加至url
        if (!StringUtil.stringIsEmpty(url) && !(StringUtil.stringIsEmpty(urlParams) || url.indexOf("=") > -1)) {
            StringBuilder sb = new StringBuilder(url).append("?");
            Map<String, String> jsonObj = JsonUtil.getJsonObjFromJsonStr(urlParams);
            jsonObj.forEach((key, value) -> {
                try {
                    sb.append(key).append("=").append(URLEncoder.encode(value, IDEConstant.CHARSET_UTF8)).append("&");
                } catch (UnsupportedEncodingException e) {
                    Logger.error("getUrlIncludeParams error:UnsupportedEncodingException ");
                }
            });

            result = sb.substring(0, sb.length() - 1);
        }

        return result;
    }

    /**
     * 判断str是否为空
     *
     * @param str 字符串
     * @return boolean 是否为空
     */
    public static boolean stringIsEmpty(String str) {
        if (str == null) {
            return true;
        }

        return str.isEmpty();
    }

    /**
     * unicode转中文，以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格
     *
     * @param unicodeStr unicode字符串
     * @return String 转换后的中文
     */
    public static String unicodeToZh(String unicodeStr) {
        String[] stirs = unicodeStr.split("\\\\u");
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < stirs.length; i++) {
            sb.append((char) Integer.valueOf(stirs[i], 16).intValue());
        }

        return sb.toString();
    }

    /**
     * 中文转unicode
     *
     * @param zhStr 中文字符串
     * @return String 转换后的unicode
     */
    public static String zhToUnicode(String zhStr) {
        char[] chars = zhStr.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            sb.append("\\u").append(Integer.toString(chars[i], 16));
        }
        return sb.toString();
    }

    /**
     * 校验文件后缀名
     *
     * @param fileName 文件名
     * @param suffixs 文件后缀数组
     * @return boolean 是否符合校验
     */
    public static boolean verifyFileSuffix(String fileName, String[] suffixs) {
        StringBuilder sb = new StringBuilder(".*(");
        for (String suffix : suffixs) {
            sb.append(".").append(suffix).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")$");

        // 校验后缀
        Pattern pattern = Pattern.compile(sb.toString());
        String tempStr = Normalizer.normalize(fileName, Normalizer.Form.NFKC); // 校验前增加字符串标准化。
        Matcher matcher = pattern.matcher(tempStr);

        return matcher.matches();
    }

    /**
     * 获取文件前缀（porting支持上传的）
     *
     * @param fileName 文件名
     * @return String 前缀值
     */
    public static String getPortingPrefix(String fileName) {
        List<String> list = new LinkedList<>();
        list.add(".tar.gz");
        list.add(".tar.bz");
        list.add(".tar.bz2");
        list.add(".tar");
        list.add(".gz");
        list.add(".jar");
        list.add(".bz2");
        list.add(".zip");

        for (int i = 0; i < list.size(); i++) {
            if (fileName.endsWith(list.get(i))) {
                return fileName.split(list.get(i))[0];
            }
        }

        return fileName;
    }

    /**
     * 判断字符串strA中是否包含includeStrs不包含notIncludeStrs
     *
     * @param strA 需要判断的字符串
     * @param includeStrs 需要包含的字符串
     * @param notIncludeStrs 不需要包含的字符串
     * @return boolean 是否成功
     */
    public static boolean judgeStrAContent(String strA, String[] includeStrs, String[] notIncludeStrs) {
        // 按条件筛选排除
        for (int i = 0; i < includeStrs.length; i++) {
            if (strA.indexOf(includeStrs[i]) == -1) {
                return false;
            }
        }
        for (int i = 0; i < notIncludeStrs.length; i++) {
            if (strA.indexOf(notIncludeStrs[i]) > -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * parse id of date type to yyyy/mm/dd HH:mm:ss
     *
     * @param id id
     * @return String id值
     */
    public static String formatCreatedId(String id) {
        String years = id.substring(0, 4);
        String months = id.substring(4, 6);
        String days = id.substring(6, 8);
        String hours = id.substring(8, 10);
        String minutes = id.substring(10, 12);
        String seconds = id.substring(12, 14);
        return years + IDEConstant.PATH_SEPARATOR + months + IDEConstant.PATH_SEPARATOR + days + " "
                + hours + ':' + minutes + ':' + seconds;
    }

    /**
     * 判断字符串如果为null，返回默认值
     *
     * @param str 需要判断是否为null的字符串
     * @param defaultValue 默认值
     * @return 非null返回str，null返回defaultValue
     */
    public static String defaultValueIfNull(String str, String defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        return str;
    }
}
