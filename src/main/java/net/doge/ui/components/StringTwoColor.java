package net.doge.ui.components;

import lombok.Data;
import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

@Data
public class StringTwoColor {
    private int width;
    private int height;
    private String txt;
    private boolean isDesktopLyric;
    private Color c1;
    private Color c2;
    private double ratio;
    private int widthThreshold;
    private BufferedImage buffImg;
    private BufferedImage buffImg1;
    private BufferedImage buffImg2;
    private ImageIcon imgIcon;

    /**
     * @param label          显示字体的JLabel
     * @param c1             颜色1(走过的颜色)
     * @param c2             颜色2(未走过的颜色)
     * @param ratio          颜色1与颜色2所占部分的比值
     * @param isDesktopLyric 是否是桌面歌词
     * @param widthThreshold 文字最大宽度
     */
    public StringTwoColor(JLabel label, Color c1, Color c2, double ratio, boolean isDesktopLyric, int widthThreshold) {
        this.txt = label.getText();
        this.c1 = c1;
        this.c2 = c2;
        this.isDesktopLyric = isDesktopLyric;
        this.widthThreshold = widthThreshold;

        //获取字符串的宽（显示在屏幕上所占的像素px）
        Font labelFont = label.getFont();

        FontMetrics metrics = label.getFontMetrics(labelFont);
        FontMetrics[] metricsBig = new FontMetrics[Fonts.TYPES_BIG.length];
        FontMetrics[] metricsHuge = new FontMetrics[Fonts.TYPES_HUGE.length];
        for (int i = 0, len = metricsBig.length; i < len; i++) metricsBig[i] = label.getFontMetrics(Fonts.TYPES_BIG[i]);
        for (int i = 0, len = metricsHuge.length; i < len; i++)
            metricsHuge[i] = label.getFontMetrics(Fonts.TYPES_HUGE[i]);

        String text = StringUtils.removeHTMLLabel(txt);

        if (StringUtils.isEmpty(text)) return;

//        Color borderColor = c2.darker().darker();
        int shadowXOffset = 2, shadowYOffset = 2;
        Color shadowColor = Colors.BLACK;

        // 计算宽度
        for (int i = 0, len = text.length(); i < len; i++) {
            char ch = text.charAt(i);
            for (int j = 0, l = metricsBig.length; j < l; j++) {
                if (labelFont == Fonts.NORMAL_BIG && Fonts.TYPES_BIG[j].canDisplay(ch)) {
                    width += metricsBig[j].stringWidth(ch + "");
                    break;
                }
            }
            for (int j = 0, l = metricsHuge.length; j < l; j++) {
                if (labelFont == Fonts.NORMAL_HUGE && Fonts.TYPES_HUGE[j].canDisplay(ch)) {
                    width += metricsHuge[j].stringWidth(ch + "");
                    break;
                }
            }
        }
        if (isDesktopLyric) width += shadowXOffset;
        height = metrics.getHeight();
        height += label.getFont().getSize();

        // 构造一个具有指定尺寸及类型为预定义图像类型之一的 BufferedImage
//        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        buffImg1 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        buffImg2 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        // 通过 BufferedImage 创建 Graphics2D 对象
        Graphics2D g1 = buffImg1.createGraphics();
        Graphics2D g2 = buffImg2.createGraphics();

        // 设置抗锯齿
        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置字体
//        g1.setFont(label.getFont());
//        g2.setFont(label.getFont());

        // 设置颜色
        if (!isDesktopLyric) {
            g1.setColor(c1);
            g2.setColor(c2);
        } else {
            g1.translate(0, height - labelFont.getSize());
            g2.translate(0, height - labelFont.getSize());
        }

        // 画字符串
        int widthDrawn = 0;
        if (!isDesktopLyric) {
            for (int i = 0, len = text.length(); i < len; i++) {
                char ch = text.charAt(i);
                for (int j = 0, l = metricsBig.length; j < l; j++) {
                    if (Fonts.TYPES_BIG[j].canDisplay(ch)) {
                        g1.setFont(Fonts.TYPES_BIG[j]);
                        g2.setFont(Fonts.TYPES_BIG[j]);
                        g1.drawString(ch + "", widthDrawn, height - labelFont.getSize());
                        g2.drawString(ch + "", widthDrawn, height - labelFont.getSize());
                        widthDrawn += metricsBig[j].stringWidth(ch + "");
                        break;
                    }
                }
            }
        } else {
            for (int i = 0, len = text.length(); i < len; i++) {
                char ch = text.charAt(i);
                for (int j = 0, l = metricsHuge.length; j < l; j++) {
                    if (Fonts.TYPES_HUGE[j].canDisplay(ch)) {
                        Shape shape = Fonts.TYPES_HUGE[j].createGlyphVector(metricsHuge[j].getFontRenderContext(), String.valueOf(ch)).getOutline();
                        // 文字阴影
//                        g1.setColor(shadowColor);
//                        g2.setColor(shadowColor);
//                        g1.translate(shadowXOffset, shadowYOffset);
//                        g2.translate(shadowXOffset, shadowYOffset);
//                        g1.fill(shape);
//                        g2.fill(shape);
//                        g1.translate(-shadowXOffset, -shadowYOffset);
//                        g2.translate(-shadowXOffset, -shadowYOffset);
                        // 文字本体
                        g1.setColor(c1);
                        g2.setColor(c2);
                        g1.fill(shape);
                        g2.fill(shape);
                        // 文字描边
//                        g1.setColor(borderColor);
//                        g2.setColor(borderColor);
//                        g1.draw(shape);
//                        g2.draw(shape);
                        widthDrawn = metricsHuge[j].stringWidth(ch + "");
                        g1.translate(widthDrawn, 0);
                        g2.translate(widthDrawn, 0);
                        break;
                    }
                }
            }

            // 文字阴影
            buffImg1 = ImageUtils.shadow(buffImg1);
            buffImg2 = ImageUtils.shadow(buffImg2);
        }

        g1.dispose();
        g2.dispose();

        // 按照比例清除相关的像素点
        if (ratio <= 1 && ratio >= 0) {
            setRatio(ratio);
        }
    }

//    public void setRatio(double ratio) {
//        double nt = width * ratio;
//        boolean ow = width > widthThreshold, imgNull = buffImg == null;
//        if (ow || imgNull) {
//            int w = ow ? widthThreshold : width;
//            if (imgNull) buffImg = new BufferedImage(w, height, BufferedImage.TYPE_4BYTE_ABGR);
//
//            if (!ow) {
//                // 将 buffImg 的左半部分用 buffImg1 的左半部分替换
//                for (int y = 0; y < height; y++) {
//                    for (int x = 0; x < nt; x++) {
//                        buffImg.setRGB(x, y, buffImg1.getRGB(x, y));
//                    }
//                }
//                // 将 buffImg 的右半部分用 buffImg2 的右半部分替换
//                for (int y = 0; y < height; y++) {
//                    for (int x = w - 1; x >= nt; x--) {
//                        buffImg.setRGB(x, y, buffImg2.getRGB(x, y));
//                    }
//                }
//            } else {
//                if (nt <= widthThreshold / 2) {
//                    // 将 buffImg 的左半部分用 buffImg1 的左半部分替换
//                    for (int y = 0; y < height; y++) {
//                        for (int x = 0; x < nt; x++) {
//                            buffImg.setRGB(x, y, buffImg1.getRGB(x, y));
//                        }
//                    }
//                    // 将 buffImg 的右半部分用 buffImg2 的右半部分替换
//                    for (int y = 0; y < height; y++) {
//                        for (int x = w - 1; x >= nt; x--) {
//                            buffImg.setRGB(x, y, buffImg2.getRGB(x, y));
//                        }
//                    }
//                } else if (width - nt <= widthThreshold / 2) {
//                    int offset = width - widthThreshold, bound = (int) (widthThreshold - width + nt);
//                    // 将 buffImg 的左半部分用 buffImg1 的左半部分替换
//                    for (int y = 0; y < height; y++) {
//                        for (int x = 0; x < bound; x++) {
//                            buffImg.setRGB(x, y, buffImg1.getRGB(offset + x, y));
//                        }
//                    }
//                    // 将 buffImg 的右半部分用 buffImg2 的右半部分替换
//                    for (int y = 0; y < height; y++) {
//                        for (int x = w - 1; x >= bound; x--) {
//                            buffImg.setRGB(x, y, buffImg2.getRGB(offset + x, y));
//                        }
//                    }
//                } else {
//                    int offset = (int) (nt - widthThreshold / 2), bound = widthThreshold / 2;
//                    // 将 buffImg 的左半部分用 buffImg1 的左半部分替换
//                    for (int y = 0; y < height; y++) {
//                        for (int x = 0; x < bound; x++) {
//                            buffImg.setRGB(x, y, buffImg1.getRGB(offset + x, y));
//                        }
//                    }
//                    // 将 buffImg 的右半部分用 buffImg2 的右半部分替换
//                    for (int y = 0; y < height; y++) {
//                        for (int x = w - 1; x >= bound; x--) {
//                            buffImg.setRGB(x, y, buffImg2.getRGB(offset + x, y));
//                        }
//                    }
//                }
//            }
//
////            cropImg();
//            makeIcon();
//        } else {
//            int ot = (int) (width * this.ratio);
//            if (this.ratio < ratio) {
//                // 将 buffImg 的需要更新的部分用 buffImg1 的对应部分替换
//                for (int y = 0; y < height; y++) {
//                    for (int x = ot; x < nt; x++) {
//                        buffImg.setRGB(x, y, buffImg1.getRGB(x, y));
//                    }
//                }
//            } else if (this.ratio > ratio) {
//                // 将 buffImg 的需要更新的部分用 buffImg2 的对应部分替换
//                for (int y = 0; y < height; y++) {
//                    for (int x = ot; x >= nt; x--) {
//                        buffImg.setRGB(x, y, buffImg2.getRGB(x, y));
//                    }
//                }
//            }
//        }
//        this.ratio = ratio;
//    }

    public void setRatio(double ratio) {
        double t = width * ratio;
        if (width > widthThreshold || buffImg == null) {
            buffImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            // 将 buffImg 的左半部分用 buffImg1 的左半部分替换
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < t; x++) {
                    buffImg.setRGB(x, y, buffImg1.getRGB(x, y));
                }
            }
            // 将 buffImg 的右半部分用 buffImg2 的右半部分替换
            for (int y = 0; y < height; y++) {
                for (int x = width - 1; x >= t; x--) {
                    buffImg.setRGB(x, y, buffImg2.getRGB(x, y));
                }
            }
            cropImg();
            makeIcon();
        } else {
            if (this.ratio < ratio) {
                // 将 buffImg 的需要更新的部分用 buffImg1 的对应部分替换
                for (int y = 0; y < height; y++) {
                    for (int x = (int) (width * this.ratio); x < t; x++) {
                        buffImg.setRGB(x, y, buffImg1.getRGB(x, y));
                    }
                }
            } else if (this.ratio > ratio) {
                // 将 buffImg 的需要更新的部分用 buffImg2 的对应部分替换
                for (int y = 0; y < height; y++) {
                    for (int x = (int) (width * this.ratio); x >= t; x--) {
                        buffImg.setRGB(x, y, buffImg2.getRGB(x, y));
                    }
                }
            }
        }
        this.ratio = ratio;
    }

    // 裁剪图片使之宽度不超过阈值
    private void cropImg() {
        if (width > widthThreshold) {
            int foreWidth = (int) (width * ratio);
            try {
                if (foreWidth <= widthThreshold / 2)
                    buffImg = Thumbnails.of(buffImg).scale(1).sourceRegion(new Rectangle(0, 0, widthThreshold, height)).asBufferedImage();
                else if (width - foreWidth > widthThreshold / 2)
                    buffImg = Thumbnails.of(buffImg).scale(1).sourceRegion(new Rectangle(foreWidth - widthThreshold / 2, 0, widthThreshold, height)).asBufferedImage();
                else
                    buffImg = Thumbnails.of(buffImg).scale(1).sourceRegion(new Rectangle(width - widthThreshold, 0, widthThreshold, height)).asBufferedImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeIcon() {
        if (imgIcon == null) imgIcon = new ImageIcon(buffImg);
        imgIcon.setImage(buffImg);
    }

    /**
     * 获取处理完的 ImageIcon
     *
     * @return
     */
    public ImageIcon getImageIcon() {
        return imgIcon;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}