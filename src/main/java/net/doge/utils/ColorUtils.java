package net.doge.utils;

import java.awt.*;

/**
 * @Author yzx
 * @Description 颜色工具类
 * @Date 2020/12/15
 */
public class ColorUtils {
    /**
     * Color 转为 RGB 字符串，格式 xxx,xxx,xxx
     *
     * @param color
     * @return
     */
    public static String colorToRGBString(Color color) {
        if (color == null) return "";
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    /**
     * RGB 字符串转为 Color，格式 xxx,xxx,xxx
     *
     * @param rgbStr
     * @return
     */
    public static Color RGBStringToColor(String rgbStr) {
        if (StringUtils.isEmpty(rgbStr)) return null;
        String[] split = rgbStr.split(",");
        return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    /**
     * 更亮的颜色
     *
     * @param color
     * @return
     */
    public static Color brighter(Color color) {
        final float factor = 0.6f;
        int red = (int) ((color.getRed() * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((color.getGreen() * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((color.getBlue() * (1 - factor) / 255 + factor) * 255);
        return new Color(red, green, blue, color.getAlpha());
    }

    /**
     * 更暗的颜色
     *
     * @param color
     * @return
     */
    public static Color darker(Color color) {
        final float factor = 0.15f;
        int red = (int) ((color.getRed() * (1 - factor)));
        int green = (int) ((color.getGreen() * (1 - factor)));
        int blue = (int) ((color.getBlue() * (1 - factor)));
        return new Color(red, green, blue, color.getAlpha());
    }

//    /**
//     * Color 转为十六进制字符串
//     *
//     * @param color
//     * @return
//     */
//    public static String colorToHexString(Color color) {
//        String r = Integer.toHexString(color.getRed());
//        r = r.length() < 2 ? ('0' + r) : r;
//        String g = Integer.toHexString(color.getGreen());
//        g = g.length() < 2 ? ('0' + g) : g;
//        String b = Integer.toHexString(color.getBlue());
//        b = b.length() < 2 ? ('0' + b) : b;
//        return '#' + r + g + b;
//    }
//
//    /**
//     * 十六进制字符串转为 Color
//     *
//     * @param str
//     * @return
//     */
//    public static Color hexStringToColor(String str) {
//        return new Color(
//                Integer.parseInt(str.substring(1, 3), 16),
//                Integer.parseInt(str.substring(3, 5), 16),
//                Integer.parseInt(str.substring(5), 16)
//        );
//    }

    /**
     * awt 的 Color 转为 JavaFx 的 Color
     *
     * @param color
     * @return
     */
    public static javafx.scene.paint.Color javaFxColor(Color color) {
        return javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), (double) color.getAlpha() / 255);
    }

//    public static Color getGradientColor(Color c1, Color c2, int i, int total) {
//        int r1 = c1.getRed(), r2 = c2.getRed(), g1 = c1.getGreen(), g2 = c2.getGreen(), b1 = c1.getBlue(), b2 = c2.getBlue();
//        return new Color(
//                Math.min(r1, r2) + Math.abs(r1 - r2) * i / total,
//                Math.min(g1, g2) + Math.abs(g1 - g2) * i / total,
//                Math.min(b1, b2) + Math.abs(b1 - b2) * i / total);
//    }
}
