package com.huawei.kunpeng.intellij.common.util;


import com.huawei.kunpeng.intellij.common.log.Logger;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 提供查看端口是否被占用的方法
 *
 * @since 2021-05-31
 */
public class IDENetUtils {
    private static final String HOST_IP = "127.0.0.1";

    private IDENetUtils() {
    }

    /**
     * 查看本机某端口是否被占用
     *
     * @param port 端口号
     * @return 如果被占用则返回true，否则返回false
     */
    public static boolean isLocalePortUsing(int port) {
        boolean isUsing = true;
        try {
            isUsing = isPortUsing(HOST_IP, port, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isUsing;
    }

    /**
     * 根据IP和端口号，查询其是否被占用
     *
     * @param host IP
     * @param port 端口号
     * @return 如果被占用，返回true；否则返回false
     * @throws UnknownHostException IP地址不通或错误，则会抛出此异常
     */
    public static boolean isPortUsing(String host, int port, int timeout) throws UnknownHostException {
        if (StringUtil.stringIsEmpty(host)) {
            host = HOST_IP;
        }
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (Socket socket = sslSocketFactory.createSocket()) {
            socket.connect(new InetSocketAddress(theAddress, port), timeout);
            flag = true;
        } catch (IOException e) {
            // 端口未被占用，输出当前端口
            Logger.info("the port (" + port + ") is using");
        }
        return flag;
    }
}