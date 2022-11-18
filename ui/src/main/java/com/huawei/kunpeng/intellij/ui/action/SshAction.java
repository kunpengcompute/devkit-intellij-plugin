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

package com.huawei.kunpeng.intellij.ui.action;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.SftpAction;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.utils.DeployUtil;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 安装、升级、卸载ssh操作
 *
 * @since 2021-04-01
 */
public abstract class SshAction extends IDEPanelBaseAction {
    /**
     * 检测连接
     *
     * @param params 部署配置信息参数
     * @param actionOperate 自定义操作
     */
    public void onNextAction(Map params, ActionOperate actionOperate) {
        Logger.info("SshAction, checkConn");
        DeployUtil.testConn(params, actionOperate);
    }

    /**
     * 部署配置信息
     *
     * @param params 部署配置信息参数
     */
    public void onOKAction(Map params) {
        Map<String, String> param = JsonUtil.getValueIgnoreCaseFromMap(params, "param", Map.class);
        SshConfig config = DeployUtil.getConfig(param);
        Session session = DeployUtil.getSession(config);
        if (Objects.isNull(session)) {
            return;
        }
        try {
            DeployUtil.setUserInfo(session, config);
            session.connect(30000);
        } catch (JSchException e) {
            Logger.error("ssh session connect error: {}", e.getMessage());
            return;
        }
        String dir = TMP_PATH + new SimpleDateFormat(TMP_FORMAT).format(new Date(System.currentTimeMillis()))
                + IDEConstant.PATH_SEPARATOR;
        // 创建dir目录
        DeployUtil.sftp(session, dir, SftpAction.MKDIR);
        // 上传脚本至dir下
        upload(session, dir);
        // 打开终端执行脚本
        openTerminal(param, dir);
        // 检查脚本执行状态
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkStatus(session, timer, dir);
            }
        };
        timer.schedule(task, 0, 1000);
    }


    /**
     * 上传脚本至dir下
     *
     * @param session Ssh session
     * @param dir 脚本目录
     */
    public abstract void upload(Session session, String dir);

    /**
     * 打开终端执行脚本
     *
     * @param param 面板配置参数
     * @param dir 脚本目录
     */
    public abstract void openTerminal(Map<String, String> param, String dir);

    /**
     * 检查脚本执行状态
     *
     * @param session Ssh session
     * @param timer 定时器
     * @param dir 脚本目录
     */
    public abstract void checkStatus(Session session, Timer timer, String dir);

    /**
     * 安装、卸载、升级成功之后的处理
     */
    protected abstract void successHandle();

    /**
     * 安装、卸载、升级失败之后的处理
     */
    protected abstract void failedHandle();
}
