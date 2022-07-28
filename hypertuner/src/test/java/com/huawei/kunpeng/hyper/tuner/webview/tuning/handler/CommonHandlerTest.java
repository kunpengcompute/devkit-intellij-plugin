package com.huawei.kunpeng.hyper.tuner.webview.tuning.handler;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * webview cmd 函数类测试
 */
public class CommonHandlerTest {
    CommonHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new CommonHandler();
    }

    @Test
    public void getData() {
        MessageBean messageBean = new MessageBean();
        handler.getData(messageBean, "module");
    }
}