package net.doge.util.core.net;

import java.util.Random;

/**
 * @author Doge
 * @description IP 工具类
 * @date 2020/12/15
 */
public class IpUtil {
    /**
     * 生成随机 Ipv4 地址
     *
     * @return
     */
    public static String randomIpv4() {
        Random random = new Random();
        return String.format("%d.%d.%d.%d", random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
