package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;

import java.io.File;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Util 工具类 测试
 */
public class NginxUtilTest {

    @Before
    public void setUp() throws Exception {
        // 设置插件名称
        TuningCommonUtil.setPluginName(TuningIDEConstant.PLUGIN_NAME);
        // 拷贝nginx代理文件
        Optional<File> optionalFile2 = FileUtil.getFile(
                CommonUtil.getPluginInstalledPath() + TuningIDEConstant.NGINX_PLUGIN_NAME, true);
        optionalFile2.ifPresent(file -> FileUtil.readAndWriterFileFromJar(file, TuningIDEConstant.NGINX_PLUGIN_NAME,
                true));

        Logger.info("=====start unzip nginx  test");
        FileUtil.unzipFile(CommonUtil.getPluginInstalledPath() + TuningIDEConstant.NGINX_PLUGIN_NAME,
                CommonUtil.getPluginInstalledPathFile(TuningIDEConstant.TUNING_NGINX_PATH));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void saveAsFileWriter() {
        NginxUtil.saveAsFileWriter("start nginx.exe");
        NginxUtil.saveAsFileWriter("title kill nginx service");
        NginxUtil.saveAsFileWriter("");
    }

    @Test
    public void writeNginxStopBat() {
        NginxUtil.writeNginxStopBat();
    }

    @Test
    public void updateNginxConfig() {
        NginxUtil.updateNginxConfig("iptest", "portTest", "localPortTest");
    }

    @Test
    public void writeNginxStartBat() {
        NginxUtil.writeNginxStartBat();
    }


}