package net.doge.util.core;

import java.util.Random;

/**
 * @author Doge
 * @description 随机工具类
 * @date 2020/12/15
 */
public class RandomUtil {
    /**
     * 生成随机 [0, limit) 整数
     *
     * @return
     */
    public static int randomInt(int limitExclude) {
        return cn.hutool.core.util.RandomUtil.randomInt(limitExclude);
    }

    /**
     * 生成随机 [min, max) 整数
     *
     * @return
     */
    public static int randomInt(int minInclude, int maxExclude) {
        return cn.hutool.core.util.RandomUtil.randomInt(minInclude, maxExclude);
    }

    /**
     * 生成随机 Ipv4 地址
     *
     * @return
     */
    public static String randomIpv4() {
        Random random = new Random();
        return String.format("%d.%d.%d.%d", random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 生成随机指定位数字
     *
     * @return
     */
    public static String randomNumbers(int n) {
        return cn.hutool.core.util.RandomUtil.randomNumbers(n);
    }
}
