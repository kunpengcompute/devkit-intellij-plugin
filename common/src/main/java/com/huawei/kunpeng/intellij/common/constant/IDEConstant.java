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

package com.huawei.kunpeng.intellij.common.constant;

/**
 * 全局常量
 *
 * @since 1.0.0
 */
public class IDEConstant {
    /**
     * url请求前缀
     */
    public static final String URL_PREFIX = "https://";

    /**
     * 系统配置文件config.json路径
     */
    public static final String CONFIG_PATH = "/assets/config.json";

    /**
     * config.json 配置文件
     */
    public static final String CONFIG_FILE = "config.json";

    /**
     * 系统默认弹框宽
     */
    public static final int DIALOG_DEFAULT_WIDTH = 780;

    /**
     * 系统默认弹框高
     */
    public static final int DIALOG_DEFAULT_HEIGHT = 200;

    /**
     * 存活的主窗口最大数量
     */
    public static final int VIABLE_WINDOW_NUM = 10;

    /**
     * 存活的弹框最大数量
     */
    public static final int VIABLE_DIALOG_NUM = 10;

    /**
     * 换行符
     */
    public static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * 文件路径分割符
     */
    public static final String PATH_SEPARATOR = "/";

    /**
     * window文件路径分割符
     */
    public static final String WINDOW_PATH_SEPARATOR = "\\";

    /**
     * 小数点分割符
     */
    public static final String POINT_SEPARATOR = "\\.";


    /**
     * 插件webview页面地址
     */
    public static final String PORTING_WEB_VIEW_PATH = "webview";

    /**
     * porting插件webview页面地址
     */
    public static final String PORTING_WEB_VIEW_INDEX_HTML = "index.html";

    /**
     * hyper tuner插件webview页面文件类型
     */
    public static final String TUNING_KPHT = "kpht";

    /**
     * utf-8编码
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * GBK编码
     */
    public static final String CHARSET_GBK = "GBK";

    /**
     * ISO-8859-1编码
     */
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    /**
     * JCEF名称
     */
    public static final String JCEF = "jcef";


    /**
     * 上传扫描文件时before字段的结束符
     */
    public static final String UPLOAD_FILE_LINE_END = "\r\n";

    /**
     * 加载中动图深色主题色图片
     */
    public static final String LOADING_DARCULA_GIF = "/assets/load/loading.gif";

    /**
     * 小眼睛闭眼图片
     */
    public static final String EYE_HIDE = "/assets/img/icon-hide.svg";

    /**
     * 小眼睛睁眼图片
     */
    public static final String EYE_VIEW = "/assets/img/icon-view.svg";

    /**
     * 提示图片
     */
    public static final String ICON_INFO = "/assets/img/common/icon_info.svg";

    /**
     * 提示图片
     */
    public static final String WARN_INFO = "/assets/img/common/icon_warn.svg";

    /**
     * 加载中动图其他主题色图片
     */
    public static final String LOADING_OTHER_GIF = "/assets/load/loading_w.gif";

    /**
     * 红点图片
     */
    public static final String RED_POINT_PATH = "/assets/img/red_point.img";

    /**
     * 右上角菜单选项图标资源地址
     */
    public static final String MENU_ICONS_PATH = "/assets/img/menu/";

    /**
     * 右上角菜单-关于-Icon
     */
    public static final String MENU_ABOUT_ICON = "about.svg";

    /**
     * 右上角菜单-申请远程实验室-Icon
     */
    public static final String MENU_APPLY_LAB_ICON = "apply_lab.svg";

    /**
     * 右上角菜单-配置远端服务器-Icon
     */
    public static final String MENU_CONFIG_SERVER_ICON = "config_server.svg";

    /**
     * 右上角菜单-部署服务器-Icon
     */
    public static final String MENU_DEPLOY_SERVER_ICON = "deploy_server.svg";

    /**
     * 右上角菜单-建议反馈-Icon
     */
    public static final String MENU_FEEDBACK_ICON = "feedback.svg";

    public static final String TITLE_REFRESH_ICON = "refresh.svg";

    /**
     * a标签head
     */
    public static final String HTML_HEAD = "<html> <a href=\"#\">";

    /**
     * a标签foot
     */
    public static final String HTML_FOOT = "</a></html>";

    /**
     * 加解密组件父路径
     */
    public static final String CRYPT_DIR = ".e";

    /**
     * 加密信息文件名
     */
    public static final String PROPERTIES_NAME = ".p";

    /**
     * 最大上传包大小（单位为M）
     */
    public static final int MAX_FILE_SIZE = 1024;
}
