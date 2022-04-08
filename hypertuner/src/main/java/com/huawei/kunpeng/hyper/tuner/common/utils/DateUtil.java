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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.SimpleDateFormat;

/**
 * 时间操作工具类
 *
 * @since 2021-04-21
 */
public class DateUtil {
    private DateUtil() {
    }

    /**
     * DateUtil
     *
     * @return DateUtil
     */
    public static DateUtil getInstance() {
        return new DateUtil();
    }

    /**
     * str转时间
     *
     * @param timeStampStr 时间字符串
     * @return 时间
     */
    public String createTimeStr(String timeStampStr) {
        double timeStampDouble = Double.parseDouble(timeStampStr);
        timeStampDouble = Math.round(timeStampDouble * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return sdf.format(timeStampDouble).replaceAll(" ", "-");
    }

    /**
     * long转时间
     *
     * @param time 时间字符串
     * @return 时间
     */
    public String getLongToTime(String time) {
        long timeLong = Long.parseLong(time);
        String timeStr = DateFormatUtils.format(timeLong, "yyyy-MM-dd HH-mm-ss");
        return timeStr.replaceAll(" ", "-");
    }
}
