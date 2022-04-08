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

package com.huawei.kunpeng.intellij.common.http;

import java.util.HashMap;
import java.util.Map;

/**
 * The class: HttpHeaderFactory
 *
 * @since 2021-8-26
 */
public class HttpHeaderFactory {
    private HttpHeader httpHeader;

    /**
     * Constructor: init the field httpHeader
     *
     * @param type type
     * @param token token
     */
    public HttpHeaderFactory(String type, String token) {
        switch (type) {
            case "PORTING":
                httpHeader = new PortingHttpHeader(token);
                break;
            case "TUNING":
                httpHeader = new TuningHttpHeader(token);
                break;
            default:
                break;
        }
    }

    /**
     * Create HttpHeader Map
     *
     * @return Map<String, String>
     */
    public Map<String, String> createHttpHeaderMap() {
        if (httpHeader != null) {
            return httpHeader.createReqHeaderMap();
        }
        return new HashMap<>();
    }
}
