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

package com.huawei.kunpeng.hyper.tuner.action.panel.weakpwd;

import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningWeakPwdConstant;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.intellij.notification.NotificationType;

/**
 * 删除弱口令事件处理器
 *
 * @since 2020-10-13
 */
public class DelWeakPwdAction extends IDEPanelBaseAction {
    private static final String RESPONSE_SUCESS = TuningWeakPwdConstant.RESPONSE_SUCESS;

    private static final String RESPONSE_FAILD = TuningWeakPwdConstant.RESPONSE_FAILD;

    /**
     * 删除弱口令
     *
     * @param delRow        需要删除的数据行
     * @param actionOperate actionOperate
     * @return boolean
     */
    public boolean deleteWeakPwd(String delRow, ActionOperate actionOperate) {
        Logger.info("delete WeakPwd.");
        // 开始删除数据
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/weak-passwords/" + delRow + UserManagerAction.FOLDER_SEPARATOR,
                        HttpMethod.DELETE.vaLue(),
                        "");

        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return false;
        }
        if (TuningIDEConstant.SUCCESS_CODE.equals(responseBean.getCode())) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            TuningWeakPwdConstant.WEAK_PASSWORD_DEL_TITLE,
                            RESPONSE_SUCESS,
                            NotificationType.INFORMATION));
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            TuningWeakPwdConstant.WEAK_PASSWORD_DEL_TITLE,
                            RESPONSE_FAILD,
                            NotificationType.INFORMATION));
        }

        return TuningIDEConstant.SUCCESS_CODE.equals(responseBean.getCode());
    }
}
