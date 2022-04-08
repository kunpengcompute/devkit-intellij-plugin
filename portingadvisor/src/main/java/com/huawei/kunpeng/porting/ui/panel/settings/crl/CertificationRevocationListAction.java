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

package com.huawei.kunpeng.porting.ui.panel.settings.crl;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.CertRevListInfoBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.http.HttpAPIServiceTrust;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The class: CertificationRevocationListAction:对CRL与后端交互的操作类
 *
 * @since 2021-8-10
 */
public class CertificationRevocationListAction extends IDEPanelBaseAction {
    private static CertificationRevocationListAction action;

    private CertificationRevocationListAction(){}

    /**
     * 单例模式
     *
     * @return CertificationRevocationListAction
     */
    public static CertificationRevocationListAction getInstance() {
        if (action == null) {
            action = new CertificationRevocationListAction();
        }
        return action;
    }

    /**
     * 证书吊销列表展示
     *
     * @return List<CertRevListInfoBean>
     */
    public List<CertRevListInfoBean> showCertRevList() {
        final RequestDataBean showCRLReq = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/cert/crl_info/", HttpMethod.GET.vaLue(), "");
        final ResponseBean response = PortingHttpsServer.INSTANCE.requestData(showCRLReq);

        List<CertRevListInfoBean> crlInfos = new ArrayList<>();
        if (response == null) {
            Logger.error("Get CRL error");
            return crlInfos;
        }

        // 将json字串转换为Java对象
        final JSONObject jsonObjectCRL = JsonUtil.getJsonObjectFromJsonStr(response.getData());
        final Object crlList = jsonObjectCRL.get("crl_list");
        if (crlList instanceof JSONArray) {
            JSONArray array = (JSONArray)crlList;
            // 循环遍历 JSONArray
            for (int i = 0; i < array.size(); i++) {
                JSONObject objectCRL = array.getJSONObject(i);
                Map<String, Object> itemMap = JSONObject.toJavaObject(objectCRL, Map.class);
                for (String key : itemMap.keySet()) {
                    CertRevListInfoBean bean = new CertRevListInfoBean();
                    bean.setCertName(key);
                    final Object o = itemMap.get(key);
                    CertRevListInfoBean.CertRevListDetailBean certRevListDetailBean =
                            JSONObject.parseObject(o.toString(), CertRevListInfoBean.CertRevListDetailBean.class);
                    bean.setCertRevListDetailBean(certRevListDetailBean);
                    crlInfos.add(bean);
                }
            }
        }
        // 将解析好的CRL信息存在缓存中
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, PortingIDEConstant.CRL_MAP_KEY,
                crlInfos.stream().collect(Collectors.toMap(CertRevListInfoBean::getCertName,
                        CertRevListInfoBean::getCertRevListDetailBean)));
        return crlInfos;
    }


    /**
     * 上传CRL文件前的校验
     *
     * @param file file
     * @param choice choice
     * @return ResponseBean
     */
    public ResponseBean perCheckForImportCRL(File file, String choice) {
        final RequestDataBean perCheckReq = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/cert/check_upload/", HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("file_size", file.length());
        obj.put("file_name", file.getName());
        obj.put("choice", choice);
        perCheckReq.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        return PortingHttpsServer.INSTANCE.requestData(perCheckReq);
    }

    /**
     * 上传CRL文件
     *
     * @param file file
     * @return ResponseBean
     */
    public ResponseBean importCertRevList(File file) {
        if (!StringUtil.verifyFileSuffix(file.getName(), new String[]{"crl"})) {
            return new ResponseBean();
        }
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/cert/upload/", HttpMethod.POST.vaLue(), "");
        Object ctxObj = IDEContext.getValueFromGlobalContext(null, message.getModule());
        String responseStr = null;
        if (ctxObj instanceof Map) {
            Map<String, Object> context = (Map<String, Object>) ctxObj;
            String url = HttpAPIServiceTrust.getCurrentRequestUrl(context, message);
            JSONObject jsonParam = new JSONObject();
            String token = Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue()))
                    .map(Object::toString).orElse(null);
            jsonParam.put("file", file);
            try {
                responseStr = HttpAPIServiceTrust.getResponseString(url, jsonParam, "POST", token, "PORTING");
            } catch (IOException e) {
                Logger.error("file upload exception：message is {}" , e.getMessage());
            }
        }
        if (responseStr == null) {
            return new ResponseBean();
        }
        return JSONObject.parseObject(responseStr, ResponseBean.class);
    }

    /**
     * 删除指定CRL文件
     *
     * @param crlName crlName
     * @return ResponseBean
     */
    public ResponseBean deleteCertRevList(String crlName) {
        RequestDataBean deleteReq = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/cert/crl_file/", HttpMethod.DELETE.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("file", crlName);
        deleteReq.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        return PortingHttpsServer.INSTANCE.requestData(deleteReq);
    }
}
