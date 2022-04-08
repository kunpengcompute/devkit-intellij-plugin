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

package com.huawei.kunpeng.hyper.tuner.common.constant.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;

import java.util.HashMap;

/**
 * Agent 证书 常量定义
 *
 * @since 2021-6-15
 */
public class AgentCertContent {
    /**
     * 响应码: 操作成功
     */
    public static final String RESPONSE_CODE = "SysPerf.Success";
    /**
     * 响应码：参数错误
     */
    public static final String RESPONSE_CODE_PARAM_ERROR = "SysPerf.Certificates.UpdateCert.ParameterErr";
    /**
     * 国际化：参数错误
     */
    public static final String NOTICE_CERT_PARAM_ERROR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_status_ParameterErr");
    /**
     * 响应码：任务正在执行，待完成后重试
     */
    public static final String RESPONSE_CODE_TASK_IS_EXECUTED = "SysPerf.Certificates.CreateCert.TaskIsExecuted";
    /**
     * 国际化：任务正在执行，待完成后重试
     */
    public static final String NOTICE_CERT_TASK_IS_EXECUTED = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_status_TaskIsExecuted");
    /**
     * 响应码：证书不存在，请重新生成
     */
    public static final String RESPONSE_CODE_CERT_NOT_EXIST =
            "SysPerf.Certificates.UpdateCert.CertificatesNotExist";
    /**
     * 国际化：证书不存在，请重新生成
     */
    public static final String NOTICE_CERT_CERT_NOT_EXIST = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_status_CertificatesNotExist");
    /**
     * 响应码：证书非当日生成，请重新生成
     */
    public static final String RESPONSE_CODE_CERT_INVALID_TIME =
            "SysPerf.Certificates.UpdateCert.CertificatesInvalidTime";
    /**
     * 国际化：证书非当日生成，请重新生成
     */
    public static final String NOTICE_CERT_INVALID_TIME = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_status_CertificatesInvalidTime");
    /**
     * 响应码：节点非在线或离线状态
     */
    public static final String RESPONSE_CODE_NODE_STATUS_INVALID =
            "SysPerf.Certificates.UpdateCert.NodeStatusInvalid";
    /**
     * 国际化：节点非在线或离线状态
     */
    public static final String NOTICE_CERT_NODE_STATUS_INVALID = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_status_NodeStatusInvalid");
    /**
     * 响应码：替换证书失败
     */
    public static final String RESPONSE_CODE_UPDATE_CERT_FAILED =
            "SysPerf.Certificates.UpdateCert.UpdateCertificateFailed";
    /**
     * 国际化：替换证书失败
     */
    public static final String NOTICE_CERT_UPDATE_CERT_FAILED = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_status_UpdateCertificateFailed");

    /**
     * 获取 响应码 和 提示信息 的Map
     *
     * @return 响应码 和 提示信息 的Map
     */
    public static HashMap<String, String> getTipCodeMap() {
        HashMap<String, String> codeTipsMap = new HashMap<>();

        // 成功
        codeTipsMap.put(RESPONSE_CODE, AGENT_CERT_CHANGE_SUCCESS);

        // 参数错误
        codeTipsMap.put(RESPONSE_CODE_PARAM_ERROR, NOTICE_CERT_PARAM_ERROR);

        // 任务正在执行，待完成后重试
        codeTipsMap.put(RESPONSE_CODE_TASK_IS_EXECUTED, NOTICE_CERT_TASK_IS_EXECUTED);

        // 证书不存在，请重新生成
        codeTipsMap.put(RESPONSE_CODE_CERT_NOT_EXIST, NOTICE_CERT_CERT_NOT_EXIST);

        // 证书非当日生成，请重新生成
        codeTipsMap.put(RESPONSE_CODE_CERT_INVALID_TIME, NOTICE_CERT_INVALID_TIME);

        // 节点非在线或离线状态
        codeTipsMap.put(RESPONSE_CODE_NODE_STATUS_INVALID, NOTICE_CERT_NODE_STATUS_INVALID);

        // 替换证书失败
        codeTipsMap.put(RESPONSE_CODE_UPDATE_CERT_FAILED, NOTICE_CERT_UPDATE_CERT_FAILED);
        return codeTipsMap;
    }

    /**
     * 国际化: Agent服务证书
     */
    public static final String AGENT_CERT_DIC = TuningI18NServer.toLocale("plugins_hyper_tuner_Title_agentcertDic");

    /**
     * 国际化: 生成证书标题
     */
    public static final String AGENT_CERT_ADD_TITLE =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_Add_Title");

    /**
     * 国际化: 生成证书成功
     */
    public static final String AGENT_CERT_ADD_SUCCESS =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_Add_Success");

    /**
     * 国际化: 生成证书失败
     */
    public static final String AGENT_CERT_ADD_FAILD =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_Add_Faild");

    /**
     * 国际化: 更换证书标题
     */
    public static final String AGENT_CERT_CHANGE_TITLE =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_Change_Title");

    /**
     * 国际化: 更换证书成功
     */
    public static final String AGENT_CERT_CHANGE_SUCCESS =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_Change_Success");

    /**
     * 国际化: 更换证书失败
     */
    public static final String AGENT_CERT_CHANGE_FAILD =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_Change_Faild");

    /**
     * 国际化: 更换工作密钥标题
     */
    public static final String WORK_KRY_CHANGE_TITLE =
            TuningI18NServer.toLocale("plugins_hyper_tuner_workkey_Change_Title");

    /**
     * 国际化: 更换证书成功
     */
    public static final String WORK_KRY_CHANGE_SUCCESS =
            TuningI18NServer.toLocale("plugins_hyper_tuner_workkey_Change_Success");

    /**
     * 国际化: 更换证书失败
     */
    public static final String WORK_KRY_CHANGE_FAILD =
            TuningI18NServer.toLocale("plugins_hyper_tuner_workkey_Change_Faild");
    /**
     * 国际化: 更换证书失败原因
     */
    public static final String WORK_KRY_CHANGE_FAILD_INFO =
            TuningI18NServer.toLocale("plugins_hyper_tuner_workkey_Change_Faild_info");
    /**
     * 国际化: 更换证书/工作密钥弹窗：节点IP
     */
    public static final String AGENT_CHANGE_NODE_IP = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_nodeIp");
    /**
     * 国际化: 更换证书/工作密钥弹窗：用户名
     */
    public static final String AGENT_CHANGE_USERNAME = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_userName");
    /**
     * 国际化: 更换证书/工作密钥弹窗：认证方式
     */
    public static final String AGENT_CHANGE_AUTH_MODE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_authMode");
    /**
     * 国际化: 更换证书/工作密钥弹窗：口令认证
     */
    public static final String AGENT_CHANGE_AUTH_MODE_PWD = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_authMode_pwd");
    /**
     * 国际化: 更换证书/工作密钥弹窗：口令
     */
    public static final String AGENT_CHANGE_AUTH_MODE_PWD_PASSWORD = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_authMode_pwd_password");
    /**
     * 国际化: 更换证书/工作密钥弹窗：密钥认证
     */
    public static final String AGENT_CHANGE_AUTH_MODE_KEY = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_authMode_key");
    /**
     * 国际化: 更换证书/工作密钥弹窗：私钥文件
     */
    public static final String AGENT_CHANGE_AUTH_MODE_KEY_FILE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_authMode_key_file");
    /**
     * 国际化: 更换证书/工作密钥弹窗：密码短语
     */
    public static final String AGENT_CHANGE_AUTH_MODE_KEY_PASSPHRASE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_agentcert_workkey_Change_authMode_key_passphrase");

    /**
     * 国际化: 节点IP
     */
    public static final String AGENT_TABLE_NODE_IP =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_table_nodeIp");

    /**
     * 国际化: 节点name
     */
    public static final String AGENT_TABLE_NODE_NAME =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_table_nodeName");

    /**
     * 国际化: 证书名称
     */
    public static final String AGENT_TABLE_CRET_NAME =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_table_certName");

    /**
     * 国际化: 证书到期时间
     */
    public static final String AGENT_TABLE_CRET_TIME =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_table_certTime");

    /**
     * 国际化: 状态
     */
    public static final String AGENT_TABLE_NODE_STATUS =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_table_NodeStatus");

    /**
     * 国际化: 操作
     */
    public static final String AGENT_TABLE_OPER =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_table_oper");

    /**
     * 国际化: 证书状态-没有证书
     */
    public static final String TABLE_CERT_STATUS10 =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_status_type10");

    /**
     * 国际化: 证书状态-有效
     */
    public static final String TABLE_CERT_STATUS0 =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_status_type0");

    /**
     * 国际化: 证书状态-即将过期
     */
    public static final String TABLE_CERT_STATUS1 =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_status_type1");

    /**
     * 国际化: 证书状态-已过期
     */
    public static final String TABLE_CERT_STATUS2 =
            TuningI18NServer.toLocale("plugins_hyper_tuner_agentcert_status_type2");

    /**
     * 生成证书按钮图标
     */
    public static final String AGENTCERT_ADD_PATH = "/assets/img/sysperf/addAgentCert.svg";
    /**
     * 成功状态标记图片
     */
    public static final String SUCCESS_PATH = "/assets/img/settings/success.png";

    /**
     * 失败状态标记图片
     */
    public static final String FAIL_PATH = "/assets/img/settings/fail.png";

    /**
     * 超时状态标记图片
     */
    public static final String TIMEOUT_PATH = "/assets/img/settings/timeout.png";

    /**
     * 超时状态标记图片
     */
    public static final String WARN_INFO_PATH = "/assets/img/settings/warn_info.png";
}
