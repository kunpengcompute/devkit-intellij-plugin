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

package com.huawei.kunpeng.intellij.ui.utils;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.enums.SftpAction;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.Procedure;
import com.huawei.kunpeng.intellij.common.util.ShellTerminalUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.AccountTipsDialog;
import com.huawei.kunpeng.intellij.ui.dialog.FingerTipDialog;
import com.huawei.kunpeng.intellij.ui.dialog.InstallServerConfirmDialog;
import com.huawei.kunpeng.intellij.ui.dialog.NewFingerTipDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.InstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.UninstallWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.CheckConnResponse;
import com.huawei.kunpeng.intellij.ui.enums.MaintenanceResponse;
import com.huawei.kunpeng.intellij.ui.panel.FingerPanel;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * jsch ssh工具类
 *
 * @since 1.0.0
 */
public class DeployUtil extends ShellTerminalUtil {
    // 连接超时
    private static final int CONNECTION_TIME_OUT_IN_MILLISECONDS = 30 * 1000;

    // 上传文件模式
    private static final int OVERWRITE = 0;
    private static final Pattern PATTERN =
            Pattern.compile("(.*)key fingerprint is ([0-9a-f:\\+/]+)\\.(.*)", Pattern.CASE_INSENSITIVE);
    private static final String SHELL_CHMOD = "500";
    private static final String DIR_CHMOD = "700";
    private static final int RESUME = 1;
    private static final int APPEND = 2;

    // 自动安装相关的信息数
    private static final int INSTALL_INFO_NUM = 3;

    // IP索引
    private static final int IP_INDEX = 1;

    // 端口索引
    private static final int PORT_INDEX = 2;
    private static JSch jsch = new JSch();
    private static Properties properties = new Properties();
    private static Session currentSession;

    /**
     * create session
     *
     * @param config 配置
     * @return session
     */
    public static Session getSession(SshConfig config) {
        Session newSession = null;
        try {
            jsch = new JSch();
            if (ValidateUtils.isNotEmptyString(config.getPassword())) {
                newSession = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
                newSession.setPassword(String.valueOf(config.getPassword()));
            } else if (ValidateUtils.isNotEmptyString(config.getIdentity())) {
                jsch.addIdentity(config.getIdentity(), config.getPassPhrase());
                newSession = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
            } else {
                Logger.warn("ssh password and identify is null.");
            }
            if (properties.isEmpty()) {
                try (InputStream stream = DeployUtil.class.getClassLoader()
                        .getResourceAsStream("assets/ssh.properties")) {
                    properties.load(stream);
                }
            }
            if (!properties.isEmpty() && newSession != null) {
                newSession.setConfig(properties);
            }
        } catch (JSchException | IOException e) {
            Logger.error("failed to connect remote " + "stack trace :JSchException | IOException ");
            if (e.getMessage().startsWith("invalid privatekey")) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                                CommonI18NServer.toLocale("plugins_common_button_connect"),
                                CommonI18NServer.toLocale("plugins_common_ssh_key_error"),
                                NotificationType.ERROR));
            }
        }
        return newSession;
    }

    /**
     * 指纹信息
     *
     * @param session session
     * @param message message
     * @return 指纹信息
     */
    public static Optional<String> readFingerprintFromPromptMessage(Session session, String message) {
        Objects.requireNonNull(session, "session is required");
        Objects.requireNonNull(jsch, "jsch is required");
        if (session.getHostKey() != null) {
            return Optional.ofNullable(session.getHostKey().getFingerPrint(jsch));
        }
        if (message == null) {
            return Optional.empty();
        }
        String fp = null;
        Matcher matcher = PATTERN.matcher(message);
        if (matcher.find()) {
            fp = matcher.group(2);
        }
        return Optional.ofNullable(fp);
    }

    /**
     * connect to server
     *
     * @param config ssh info (host, usr, pwd, )
     * @return Boolean Session instance if connect successfully
     */
    public static Boolean connect(SshConfig config) {
        try {
            currentSession = getSession(config);
            KeyPair keyPair = null;
            if (ValidateUtils.isNotEmptyString(config.getPassPhrase())) {
                keyPair = KeyPair.load(jsch, config.getIdentity(), null);
            }
            if (keyPair != null && !keyPair.isEncrypted()) {
                throw new JSchException();
            }
            if (Objects.isNull(currentSession)) {
                return false;
            }
            Session finalCurrentSession = currentSession;
            if (!Objects.isNull(currentSession)) {
                currentSession.setUserInfo(new UserInfo() {
                    @Override
                    public boolean promptPassword(String message) {
                        return true;
                    }

                    /***
                     * promptPassphrase
                     *
                     * @param message message
                     * @return message
                     */
                    @Override
                    public boolean promptPassphrase(String message) {
                        return !StringUtil.stringIsEmpty(config.getPassPhrase());
                    }

                    @Override
                    public boolean promptYesNo(String message) {
                        String fingerprint =
                                readFingerprintFromPromptMessage(finalCurrentSession, message).orElse(null);
                        return fingerprint != null && fingerprint.equals(config.getFingerprint());
                    }

                    /**
                     * 获取连接密码
                     *
                     * @return String
                     */
                    @Override
                    public String getPassword() {
                        return config.getPassword();
                    }

                    /**
                     * 获取连接
                     *
                     * @param message message
                     */
                    @Override
                    public void showMessage(String message) {
                    }

                    /**
                     * 指纹
                     *
                     * @return String
                     */
                    @Override
                    public String getPassphrase() {
                        return config.getPassPhrase();
                    }

                });
                currentSession.connect(CONNECTION_TIME_OUT_IN_MILLISECONDS);
            }
        } catch (JSchException e) {
            showErrorMessage(config);
            return false;
        }
        closeSession(currentSession);
        return true;
    }

    /**
     * setUserInfo without finger
     *
     * @param currentSession currentSession
     * @param config         config
     */
    public static void setUserInfo(Session currentSession, SshConfig config) {
        currentSession.setUserInfo(
                new UserInfo() {
                    @Override
                    public boolean promptPassword(String message) {
                        return true;
                    }

                    /***
                     * promptPassphrase
                     *
                     * @param message message
                     * @return message
                     */
                    @Override
                    public boolean promptPassphrase(String message) {
                        if (!StringUtil.stringIsEmpty(config.getPassPhrase())) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean promptYesNo(String message) {
                        return true;
                    }

                    @Override
                    public String getPassword() {
                        return config.getPassword();
                    }

                    @Override
                    public void showMessage(String message) {
                    }

                    @Override
                    public String getPassphrase() {
                        return config.getPassPhrase();
                    }
                });
    }

    private static void showErrorMessage(SshConfig config) {
        String message = CommonI18NServer.toLocale("plugins_common_testConn_keyFail");
        if (ValidateUtils.isNotEmptyString(config.getPassword())) {
            message = CommonI18NServer.toLocale("plugins_common_testConn_psdFail");
        }
        IDENotificationUtil.notificationCommon(
                new NotificationBean(
                        CommonI18NServer.toLocale("plugins_common_testConn_title"), message, NotificationType.ERROR));
    }

    /**
     * 将连接检测按钮恢复并隐藏加载状态
     */
    public static void recoveryCheckAndLoad() {
        InstallUpgradeWrapDialog.setCheckEnable(true);
        InstallUpgradeWrapDialog.setGifVisible(false);
        UninstallWrapDialog.setCheckEnable(true);
        UninstallWrapDialog.setGifVisible(false);
    }

    /**
     * 检测连接
     *
     * @param actionOperate 自定义操作
     * @param config        部署配置信息参数
     * @param consumer      consumer
     */
    public static void gotoTestConn(ActionOperate actionOperate, SshConfig config, Consumer<String> consumer) {
        String finger = readFingerprint(config, consumer);
        if ("noFirst".equals(finger)) {
            // 非首次连接 finger为noFirst
            executeOnPoolTestConn(actionOperate, config);
        } else if (ValidateUtils.isNotEmptyString(finger)) {
            // 首次连接打开弹窗警告 指纹不为null
            config.setFingerprint(finger);
            openTip(config, actionOperate);
        } else {
            Logger.warn("Finger have some problem.");
            // 检测完成后恢复检测按钮可用并去除动态图标
            recoveryCheckAndLoad();
        }
    }

    /**
     * 在后台线程执行连接测试
     *
     * @param actionOperate OK按钮动作
     * @param config        配置信息
     */
    public static void executeOnPoolTestConn(ActionOperate actionOperate, SshConfig config) {
        ApplicationManager.getApplication()
                .executeOnPooledThread(
                        () -> {
                            if (connect(config)) {
                                IDENotificationUtil.notificationCommon(
                                        new NotificationBean(
                                                CommonI18NServer.toLocale("plugins_common_testConn_title"),
                                                CommonI18NServer.toLocale("plugins_common_testConn_ok"),
                                                NotificationType.INFORMATION));
                                // 解开okAction禁用
                                actionOperate.actionOperate(true);
                            }
                            // 检测完成后恢复检测按钮可用并去除动态图标
                            recoveryCheckAndLoad();
                        });
    }

    /**
     * 查询服务器上临时install_porting.log日志内容。
     *
     * @param session          session
     * @param timer            定时器
     * @param logPath          logPath
     * @param failedProcedure  失败函数
     * @param successProcedure 成功函数
     */
    public static void checkInstallLog(
            Session session, Timer timer, String logPath, Procedure failedProcedure, Procedure successProcedure) {
        String res = sftp(session, logPath, SftpAction.READ);
        if (ValidateUtils.isEmptyString(res)) {
            return;
        }
        if (res.contains("failed")) {
            Logger.info("install failed");
            handleFailedResult(session, timer, logPath, failedProcedure);
        }
        if (res.contains("success")) {
            handleInstallSuccessResult(session, timer, res, logPath, successProcedure);
            Logger.info("install success");
        }
    }

    private static void handleFailedResult(Session session, Timer timer, String logPath, Procedure failedProcedure) {
        timer.cancel();
        failedProcedure.run();
        sftp(session, logPath, SftpAction.REMOVE);
        closeSession(session);
    }

    private static void handleInstallSuccessResult(
            Session session, Timer timer, String res, String logPath, Procedure procedure) {
        timer.cancel();
        String[] resArray = res.split(":");
        if (resArray.length >= INSTALL_INFO_NUM) {
            String ip = resArray[IP_INDEX];
            String port = resArray[PORT_INDEX];
            // 将日志中的ip和端口存入选择登录页面缓存
            InstallServerConfirmDialog.setVerifyIP(ip);
            InstallServerConfirmDialog.setVerifyPort(port);
        }
        sftp(session, logPath, SftpAction.REMOVE);
        closeSession(session);
        ApplicationManager.getApplication().invokeLater(procedure::run);
    }

    /**
     * 查询服务器上临时uninstall_porting.log日志内容。
     *
     * @param session          session
     * @param timer            定时器
     * @param logPath          dir
     * @param failedProcedure  失败函数
     * @param successProcedure 成功函数
     */
    public static void checkUninstallLog(
            Session session, Timer timer, String logPath, Procedure failedProcedure, Procedure successProcedure) {
        String res = sftp(session, logPath, SftpAction.READ);
        if (ValidateUtils.isEmptyString(res)) {
            return;
        }
        if (res.contains("failed")) {
            Logger.info("uninstall failed");
            handleFailedResult(session, timer, logPath, failedProcedure);
        }
        if (res.contains("success")) {
            handleSuccessResult(session, timer, logPath, successProcedure);
            Logger.info("uninstall success");
        }
    }

    private static void handleSuccessResult(Session session, Timer timer, String logPath, Procedure successProcedure) {
        timer.cancel();
        sftp(session, logPath, SftpAction.REMOVE);
        closeSession(session);
        ApplicationManager.getApplication().invokeLater(successProcedure::run);
    }

    /**
     * 查询服务器上临时upgrade_porting.log日志内容。
     *
     * @param session          session
     * @param timer            定时器
     * @param logPath          dir
     * @param failedProcedure  失败函数
     * @param successProcedure 成功函数
     */
    public static void checkUpgradeLog(
            Session session, Timer timer, String logPath, Procedure failedProcedure, Procedure successProcedure) {
        String res = sftp(session, logPath, SftpAction.READ);
        if (ValidateUtils.isEmptyString(res)) {
            return;
        }
        if (res.contains("failed")) {
            Logger.info("upgrade failed");
            handleFailedResult(session, timer, logPath, failedProcedure);
        }
        if (res.contains("success")) {
            handleSuccessResult(session, timer, logPath, successProcedure);
            Logger.info("upgrade success");
        }
        closeSession(session);
    }

    /**
     * 查询服务器上临时log日志内容。
     *
     * @param session session
     * @param logPath dir
     */
    public static void newCheckLog(
            Session session, Timer timer, String logPath, ActionOperate actionOperate, String tabName) {
        String res = sftp(session, logPath, SftpAction.READ);
        if (ValidateUtils.isEmptyString(res) && checkTerminal(tabName)) {
            return;
        } else if (ValidateUtils.isEmptyString(res) && !checkTerminal(tabName)) {
            Logger.info("upgrade failed");
            actionOperate.actionOperate(MaintenanceResponse.CLOSE_LOADING);
            actionOperate.actionOperate(MaintenanceResponse.FAILED);
            timer.cancel();
            sftp(session, logPath, SftpAction.REMOVE);
            closeSession(session);
            return;
        }
        if (res.contains("failed")) {
            Logger.info("upgrade failed");
            actionOperate.actionOperate(MaintenanceResponse.CLOSE_LOADING);
            actionOperate.actionOperate(MaintenanceResponse.FAILED);
            timer.cancel();
            sftp(session, logPath, SftpAction.REMOVE);
            closeSession(session);
        }
        if (res.contains("success")) {
            Logger.info("upgrade success");
            actionOperate.actionOperate(MaintenanceResponse.CLOSE_LOADING);
            // 成功条件下返回日志数据，包括IP:端口
            actionOperate.actionOperate(res);
            timer.cancel();
            sftp(session, logPath, SftpAction.REMOVE);
            closeSession(session);
        }
        closeSession(session);
    }


    /**
     * 打开指纹弹窗
     *
     * @param actionOperate 回调
     * @param config        返回配置
     */
    public static void openTip(SshConfig config, ActionOperate actionOperate) {
        FingerTipDialog dialog = new FingerTipDialog(null, new FingerPanel(null, null, null, config, true));
        dialog.setConfig(config);
        dialog.setActionOperate(actionOperate);
        dialog.displayPanel();
    }

    /**
     * Read fingerprint.
     *
     * @param config   config
     * @param consumer consumer
     * @return String string值
     */
    public static String readFingerprint(SshConfig config, Consumer<String> consumer) {
        String fingerprint = "noFirst";
        currentSession = getSession(config);
        if (Objects.isNull(currentSession)) {
            return "";
        }
        try {
            currentSession.connect(CONNECTION_TIME_OUT_IN_MILLISECONDS);
        } catch (JSchException e) {
            String message = e.getMessage();
            // 算法不匹配
            if (message != null && message.startsWith("Algorithm negotiation fail")) {
                IDENotificationUtil.notificationForHyperlink(new NotificationBean(
                        CommonI18NServer.toLocale("plugins_common_button_connect"),
                        CommonI18NServer.toLocale("plugins_ssh_algorithm_negotiation_error"),
                        NotificationType.ERROR), op ->
                        CommonUtil.openURI(CommonI18NServer.toLocale("plugins_ssh_algorithm_negotiation_error_faq")));
                fingerprint = "";
            } else {
                // 首次连接抓到unknowkey StrictHostKeyChecking=ask 获取该指纹弹窗确认
                fingerprint = readFingerprintFromPromptMessage(currentSession, message).orElse(null);
                if (fingerprint == null) {
                    Logger.error("failed to get ssh fingerprint of remote. Message: JSchException");
                    // 指纹为null 网络不通连接超时
                    consumer.accept(config.getHost());
                }
            }
        } finally {
            if (currentSession != null) {
                currentSession.disconnect();
            }
        }
        return fingerprint;
    }

    /**
     * 检测连接
     *
     * @param params        部署配置信息参数
     * @param actionOperate 自定义操作
     */
    public static void testConn(Map params, ActionOperate actionOperate) {
        Map<String, String> param = JsonUtil.getValueIgnoreCaseFromMap(params, "param", Map.class);
        AccountTipsDialog tipsDialog =
                new AccountTipsDialog(
                        CommonI18NServer.toLocale("common_using_account_title_name"), "", null, param, actionOperate);
        tipsDialog.displayPanel();
    }

    /**
     * Upload.
     *
     * @param session      the session
     * @param targetPath   the targetPath
     * @param shellContent 脚本内容
     */
    public static void upload(Session session, String shellContent, String targetPath) {
        if (session != null) {
            ChannelSftp sftp = null;
            try {
                Channel tmp = session.openChannel("sftp");
                if (!(tmp instanceof ChannelSftp)) {
                    return;
                }
                sftp = (ChannelSftp) tmp;
                sftp.connect(CONNECTION_TIME_OUT_IN_MILLISECONDS);
                ByteBuffer buffer = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(shellContent));
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                sftp.put(new ByteArrayInputStream(bytes), targetPath, OVERWRITE);
                // 脚本权限八进制500转换为十进制
                sftp.chmod(Integer.parseInt(SHELL_CHMOD, 8), targetPath);
            } catch (JSchException | SftpException | CharacterCodingException e) {
                Logger.error("failed to open ssh channel. stack trace : JSchException | SftpException");
            } finally {
                if (sftp != null && sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
        }
    }

    /**
     * Upload.
     *
     * @param session      the session
     * @param targetPath   the targetPath
     * @param shellContent 脚本内容
     */
    public static void newUpload(Session session, String shellContent, String targetPath, ActionOperate actionOperate) {
        if (session != null) {
            ChannelSftp sftp = null;
            try {
                Channel tmp = session.openChannel("sftp");
                if (!(tmp instanceof ChannelSftp)) {
                    return;
                }
                sftp = (ChannelSftp) tmp;
                sftp.connect(CONNECTION_TIME_OUT_IN_MILLISECONDS);
                ByteBuffer buffer = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(shellContent));
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                sftp.put(new ByteArrayInputStream(bytes), targetPath, OVERWRITE);
                // 脚本权限八进制500转换为十进制
                sftp.chmod(Integer.parseInt(SHELL_CHMOD, 8), targetPath);
            } catch (JSchException | SftpException | CharacterCodingException e) {
                Logger.error("failed to open ssh channel. stack trace : JSchException | SftpException");
                actionOperate.actionOperate(MaintenanceResponse.CLOSE_LOADING);
                actionOperate.actionOperate(MaintenanceResponse.UPLOAD_ERROR);
                closeSession(session);
            } finally {
                if (sftp != null && sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
        }
    }

    /**
     * Sftp
     *
     * @param session    the session
     * @param targetPath the targetPath
     * @param sftpAction the sftpAction
     * @return String content
     */
    public static String sftp(Session session, String targetPath, SftpAction sftpAction) {
        String outPut = null;
        if (session != null) {
            ChannelSftp sftp = null;
            BufferedReader buffReader = null;
            try {
                Channel tmp = session.openChannel("sftp");
                if (!(tmp instanceof ChannelSftp)) {
                    return "";
                }
                sftp = (ChannelSftp) tmp;
                sftp.connect(CONNECTION_TIME_OUT_IN_MILLISECONDS);
                switch (sftpAction) {
                    case REMOVE:
                        sftp.rm(targetPath);
                        break;
                    case MKDIR:
                        sftp.mkdir(targetPath);
                        sftp.chmod(Integer.parseInt(DIR_CHMOD, 8), targetPath);
                        break;
                    case READ:
                        buffReader = new BufferedReader(new InputStreamReader(sftp.get(targetPath)));
                        outPut = buffReader.readLine();
                        break;
                    default:
                        Logger.info("Please check out sftpAction {}", sftpAction);
                        break;
                }
            } catch (JSchException | SftpException | IOException e) {
                Logger.error("failed to open ssh channel. stack trace : JSchException | SftpException");
            } finally {
                FileUtil.closeStreams(buffReader, null);
                if (sftp != null && sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
        }
        return outPut;
    }

    /**
     * 关闭session
     *
     * @param session session
     */
    public static void closeSession(Session session) {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * 获取配置
     *
     * @param param 获取参数
     * @return config 返回配置
     */
    public static SshConfig getConfig(Map<String, String> param) {
        SshConfig config = new SshConfig();
        config.setHost(param.get("ip"));
        config.setPort(Integer.parseInt(param.get("port")));
        config.setPassword(param.get("password"));
        config.setUser(param.get("user"));
        config.setIdentity(param.get("privateKey"));
        config.setPassPhrase(param.get("passPhrase"));
        return config;
    }

    /**
     * 读取指纹
     * @param actionOperate 自定义操作
     * @param config        配置信息参数
     */
    public static void readFinger(ActionOperate actionOperate, SshConfig config) {
        // 读取指纹，返回读取结果
        String finger = newReadFingerprint(config, actionOperate);
        if (ValidateUtils.isNotEmptyString(finger)) {
            actionOperate.actionOperate(finger);
        } else {
            Logger.warn("reading finger gets some error");
            actionOperate.actionOperate(CheckConnResponse.FINGERPRINT_FAILURE);
        }
    }

    /**
     * 检测连接
     *
     * @param actionOperate 自定义操作
     * @param config        部署配置信息参数
     */
    public static void newTestConn(ActionOperate actionOperate, SshConfig config) {
        String finger = newReadFingerprint(config, actionOperate);
        if ("noFirst".equals(finger)) {
            // 非首次连接 finger为noFirst
            newExecuteOnPoolTestConn(config, actionOperate);
        } else if (ValidateUtils.isNotEmptyString(finger)) {
            // 首次连接打开弹窗警告 指纹不为null
            config.setFingerprint(finger);
            newOpenTip(config, actionOperate);
        } else {
            Logger.warn("Finger have some problem.");
            actionOperate.actionOperate(CheckConnResponse.FINGERPRINT_FAILURE);
        }
    }

    /**
     * 指纹弹框修改后，新检测连接
     */
    public static void realTestConn(ActionOperate actionOperate, SshConfig config) {
        // 直接执行连接
        Logger.info("this is real test config");
        newExecuteOnPoolTestConn(config, actionOperate);
    }

    /**
     * 在后台线程执行连接测试
     *
     * @param config 配置信息
     */
    public static void newExecuteOnPoolTestConn(SshConfig config, ActionOperate actionOperate) {
        ApplicationManager.getApplication()
                .executeOnPooledThread(
                        () -> {
                            if (connect(config)) {
                                actionOperate.actionOperate(CheckConnResponse.SUCCESS);
                            } else {
                                actionOperate.actionOperate(CheckConnResponse.USERAUTH_FAILURE);
                            }
                        });
    }

    /**
     * Read fingerprint.
     *
     * @param config config
     * @return String string值
     */
    public static String newReadFingerprint(SshConfig config, ActionOperate actionOperate) {
        String fingerprint = "noFirst";
        currentSession = newGetSession(config, actionOperate);
        if (Objects.isNull(currentSession)) {
            return "";
        }
        try {
            currentSession.connect(CONNECTION_TIME_OUT_IN_MILLISECONDS);
        } catch (JSchException e) {
            String message = e.getMessage();
            // 算法不匹配
            if (message != null && message.startsWith("Algorithm negotiation fail")) {
                actionOperate.actionOperate(CheckConnResponse.FINGERPRINT_FAILURE);
                fingerprint = "";
            } else {
                // 首次连接抓到unknowkey StrictHostKeyChecking=ask 获取该指纹弹窗确认
                fingerprint = readFingerprintFromPromptMessage(currentSession, message).orElse(null);
                if (fingerprint == null) {
                    Logger.error("failed to get ssh fingerprint of remote. Message: JSchException");
                    // 指纹为null 网络不通连接超时
                    actionOperate.actionOperate(CheckConnResponse.TIME_OUT);
                }
            }
        } finally {
            if (currentSession != null) {
                currentSession.disconnect();
            }
        }
        return fingerprint;
    }

    /**
     * create session
     *
     * @param config        配置
     * @param actionOperate 操作
     * @return session
     */
    public static Session newGetSession(SshConfig config, ActionOperate actionOperate) {
        Session newSession = null;
        try {
            jsch = new JSch();
            if (ValidateUtils.isNotEmptyString(config.getPassword())) {
                newSession = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
                newSession.setPassword(String.valueOf(config.getPassword()));
            } else if (ValidateUtils.isNotEmptyString(config.getIdentity())) {
                jsch.addIdentity(config.getIdentity(), config.getPassPhrase());
                newSession = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
            } else {
                Logger.warn("ssh password and identify is null.");
            }
            if (properties.isEmpty()) {
                try (InputStream stream = DeployUtil.class.getClassLoader()
                        .getResourceAsStream("assets/ssh.properties")) {
                    properties.load(stream);
                }
            }
            if (!properties.isEmpty() && newSession != null) {
                newSession.setConfig(properties);
            }
        } catch (JSchException | IOException e) {
            Logger.error("failed to connect remote " + "stack trace :JSchException | IOException ");
            if (e.getMessage().startsWith("invalid privatekey")) {
                actionOperate.actionOperate(CheckConnResponse.PASSPHRASE_FAILURE);
            }
        }
        return newSession;
    }

    /**
     * 打开指纹弹窗
     *
     * @param actionOperate 回调
     * @param config        返回配置
     */
    public static void newOpenTip(SshConfig config, ActionOperate actionOperate) {
        NewFingerTipDialog dialog = new NewFingerTipDialog(null, new FingerPanel(null, null, null, config, true));
        dialog.setConfig(config);
        dialog.setActionOperate(actionOperate);
        dialog.displayPanel();
    }
}
