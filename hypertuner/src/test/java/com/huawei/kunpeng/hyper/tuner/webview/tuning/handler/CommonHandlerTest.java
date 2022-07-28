package com.huawei.kunpeng.hyper.tuner.webview.tuning.handler;

import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;

import org.junit.Before;
import org.junit.Test;

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