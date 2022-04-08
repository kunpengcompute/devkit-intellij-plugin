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

package com.huawei.kunpeng.intellij.common.bean;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Vector;

/**
 * The class CertRevListInfoBean: CRL数据结构
 *
 * @since 2021-8-10
 */
@Getter
@Setter
public class CertRevListInfoBean {
    /**
     * 查看详情
     */
    public static final String CRL_DETAIL = I18NServer.toLocale("plugins_porting_setting_crl_detail");
    private String certName;
    private CertRevListDetailBean certRevListDetailBean;

    /**
     * toVector
     *
     * @return Vector
     */
    public Vector<String> toVector() {
        Vector<String> crlVec = new Vector<>();
        crlVec.add(certName);
        crlVec.add(certRevListDetailBean.getIssuer());
        crlVec.add(certRevListDetailBean.getEffectiveDate());
        crlVec.add(certRevListDetailBean.getNextUpdateDate());
        crlVec.add(I18NServer.toLocale(certRevListDetailBean.getStatus()));
        crlVec.add(CRL_DETAIL);
        return crlVec;
    }

    /**
     * CertRevListDetailBean 内部类
     */
    @Getter
    @Setter
    public static class CertRevListDetailBean {
        private String nextUpdateDate;
        private String effectiveDate;
        private List<CertDetailBean> crlDetail;
        private String issuer;
        private String status;
    }

    /**
     * CertDetailBean 内部类
     */
    @Getter
    @Setter
    public static class CertDetailBean {
        private String serial_number;
        private String revoke_date;

        /**
         * toVector
         *
         * @return Vector
         */
        public Vector<String> toVector() {
            Vector<String> detailVec = new Vector<>();
            detailVec.add(serial_number);
            detailVec.add(revoke_date);
            return detailVec;
        }
    }
}
