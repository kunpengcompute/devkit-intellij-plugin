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

package com.huawei.kunpeng.porting.action.setting.threshold;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import java.util.HashMap;
import java.util.Map;

/**
 * 阈值设置Action类
 *
 * @since v1.0
 */
public class ThresholdConfigAction extends IDEPanelBaseAction {
    /**
     * 获取历史报告阈值
     *
     * @return 响应体
     */
    public ResponseBean queryReportMask() {
        return PortingHttpsServer.INSTANCE.requestData(new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/histasknums/", HttpMethod.GET.vaLue(), ""));
    }

    /**
     * 保存阈值设置
     *
     * @param safeNum      最小阈值
     * @param dangerousNum 最大阈值
     * @return 响应体
     */
    public ResponseBean saveConfig(int safeNum, int dangerousNum) {
        RequestDataBean dataBean = new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/modifyhistasknums/", HttpMethod.POST.vaLue(), "");
        Map<String, Integer> params = new HashMap<>();
        params.put("safenums", safeNum);
        params.put("dangerousnums", dangerousNum);
        dataBean.setBodyData(JsonUtil.getJsonStrFromJsonObj(params));
        return PortingHttpsServer.INSTANCE.requestData(dataBean);
    }
}
