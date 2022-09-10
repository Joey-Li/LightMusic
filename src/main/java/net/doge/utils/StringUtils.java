package net.doge.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.moji4j.MojiConverter;
import net.doge.constants.Fonts;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author yzx
 * @Description 字符串工具类
 * @Date 2020/12/15
 */
public class StringUtils {
    private static final Map<Character, String> cMap = new HashMap<>();

    static {
        cMap.put(' ', "&nbsp;");
        cMap.put('<', "&lt;");
        cMap.put('>', "&gt;");
        cMap.put('\n', "<br>");
    }

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    private static MojiConverter mojiConverter = new MojiConverter();

    static {
        // 拼音小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 不带声调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // u 用 v 代替
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 判断字符串是否为 null 或 ""
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return null == s || "".equals(s);
    }

    /**
     * 判断字符串是否不为 null 和 ""
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s) {
        return null != s && !"".equals(s);
    }

    /**
     * 缩短字符串
     *
     * @param s
     * @param maxLen
     * @return
     */
    public static String shorten(String s, int maxLen) {
        if (maxLen <= 3 || s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    /**
     * 字符串转为 32 位 MD5
     *
     * @param s
     * @return
     */
    public static String toMD5(String s) {
        return DigestUtil.md5Hex(s);
    }

    /**
     * 字符串转为 32 位 MD5
     *
     * @param b
     * @return
     */
    public static String toMD5(byte[] b) {
        return DigestUtil.md5Hex(b);
    }

    /**
     * 比较两个字符串大小
     *
     * @param s1
     * @param s2
     * @return
     */
    public static int compare(String s1, String s2) throws BadHanyuPinyinOutputFormatCombination {
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        for (int i = 0, len = Math.min(s1.length(), s2.length()); i < len; i++) {
            char c1 = s1.charAt(i), c2 = s2.charAt(i);
            if (c1 == c2) continue;
            if ((c1 + "").matches("[\\u4E00-\\u9FA5]+") && (c2 + "").matches("[\\u4E00-\\u9FA5]+"))
                return PinyinHelper.toHanyuPinyinStringArray(c1, format)[0].compareTo(PinyinHelper.toHanyuPinyinStringArray(c2, format)[0]);
            return c1 - c2;
        }
        return s1.length() - s2.length();
    }

    /**
     * 转为繁体中文
     *
     * @param s
     * @return
     */
    public static String toTraditionalChinese(String s) {
        return ZhConverterUtil.toTraditional(s);
    }

    /**
     * 转为简体中文
     *
     * @param s
     * @return
     */
    public static String toSimplifiedChinese(String s) {
        return ZhConverterUtil.toSimple(s);
    }

    /**
     * 日语转为罗马音
     *
     * @param s
     * @return
     */
    public static String toRomaji(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = s.length(); i < len; i++) {
            char ch = s.charAt(i);
            String s1 = mojiConverter.convertKanaToRomaji(ch + "");
            // 遇到片假、平假加空格隔开
            if (s1.indexOf(ch) < 0) {
                if (i != 0) sb.append(' ');
                sb.append(s1);
                if (i != len - 1) sb.append(' ');
            } else sb.append(s1);
        }
        // 将连续空格缩成一个
        return sb.toString().replaceAll(" +", " ");
    }

    /**
     * 格式化数字使其带中文单位(万、亿等)
     *
     * @param n
     * @return
     */
    public static String formatNumber(long n) {
//        if (n < 10000) return n + "";
//        if (n < 100000000) return String.format("%.1f 万", (double) (n - 500) / 10000).replace(".0", "");
//        return String.format("%.1f 亿", (double) (n - 5000000) / 100000000).replace(".0", "");

        if (n < 10000) return n + " 播放";
        if (n < 100000000) return String.format("%s 万 播放", (double) (n / 1000) / 10).replace(".0", "");
        return String.format("%s 亿 播放", (double) (n / 10000000) / 10).replace(".0", "");
    }

    /**
     * 格式化数字使其带中文单位(万、亿等)
     *
     * @param n
     * @return
     */
    public static String formatNumberWithoutSuffix(long n) {
//        if (n < 10000) return n + "";
//        if (n < 100000000) return String.format("%.1f 万", (double) (n - 500) / 10000).replace(".0", "");
//        return String.format("%.1f 亿", (double) (n - 5000000) / 100000000).replace(".0", "");

        if (n < 10000) return n + "";
        if (n < 100000000) return String.format("%s 万", (double) (n / 1000) / 10).replace(".0", "");
        return String.format("%s 亿", (double) (n / 10000000) / 10).replace(".0", "");
    }

    /**
     * 反格式化数字 例如 7.6万 -> 76000
     *
     * @param s
     * @return
     */
    public static long antiFormatNumber(String s) {
        if (s.endsWith("万")) return (long) (Double.parseDouble(s.replace("万", "").trim()) * 10000);
        else if (s.endsWith("亿")) return (long) (Double.parseDouble(s.replace("亿", "").trim()) * 100000000);
        return Long.parseLong(s.trim());
    }

    /**
     * 生成评分星星字符串
     *
     * @param n
     * @return
     */
    public static String genStar(int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            if (i < n) sb.append('★');
            else sb.append('☆');
        }
        return sb.toString();
    }

    /**
     * 从元素提取文本，替换 <br> 为 \n
     *
     * @param parentElement
     * @return
     */
    public static String getPrettyText(Element parentElement) {
        if (parentElement == null) return "";
        StringBuffer working = new StringBuffer();
        for (Node child : parentElement.childNodes()) {
            if (child instanceof TextNode) {
                working.append(((TextNode) child).text());
            }
            if (child instanceof Element) {
                Element childElement = (Element) child;
                working.append(getPrettyText(childElement));
                String s = childElement.tag().getName().toLowerCase();
                if (s.equals("br") || s.equals("li") || s.equals("p")) {
                    working.append("\n");
                }
            }
        }
        return working.toString();
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示
     *
     * @param text
     * @return
     */
    public static String textToHtmlWithSpace(String text) {
        if (text == null || text.startsWith("<html>") || text.trim().equals("")) return text;
        StringBuffer sb = new StringBuffer();
        sb.append("<html><div style=\"font-family:'" + Fonts.NORMAL.getFontName() + "'\">");
        for (int i = 0, len = text.length(); i < len; i++) {
            int codePoint = text.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            if (cMap.containsKey(chars[0])) {
                sb.append(cMap.get(chars[0]));
                continue;
            }
            for (int j = 0, l = Fonts.TYPES.length; j < l; j++) {
                if (Fonts.TYPES[j].canDisplay(codePoint)) {
                    // 中文
                    if (j == 0) sb.append(chars[0]);
                    else
                        sb.append("<span style=\"font-family:'" + Fonts.TYPES[j].getFontName() + "'\">" + new String(chars) + "</span>");
                    if (chars.length == 2) i++;
                    break;
                }
            }
        }
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示，不替换 < >
     *
     * @param text
     * @return
     */
    public static String textToHtmlWithoutLtGt(String text) {
        if (text == null || text.startsWith("<html>") || text.trim().equals("")) return text;
        StringBuffer sb = new StringBuffer();
        sb.append("<html><div style=\"font-family:'" + Fonts.NORMAL.getFontName() + "'\">");
        for (int i = 0, len = text.length(); i < len; i++) {
            char ch = text.charAt(i);
            if (ch != ' ' && ch != '<' && ch != '>' && cMap.containsKey(ch)) {
                sb.append(cMap.get(ch));
                continue;
            }
            for (int j = 0, l = Fonts.TYPES.length; j < l; j++) {
                if (Fonts.TYPES[j].canDisplay(ch)) {
                    // 中文
                    if (j == 0) sb.append(ch);
                    else sb.append("<font face=\"" + Fonts.TYPES[j].getFontName() + "\">" + ch + "</font>");
                    break;
                }
            }
        }
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示，不替换空格
     *
     * @param text
     * @return
     */
    public static String textToHtml(String text) {
        if (text == null || text.startsWith("<html>") || text.trim().equals("")) return text;
        StringBuffer sb = new StringBuffer();
        sb.append("<html><div style=\"font-family:'" + Fonts.NORMAL.getFontName() + "'\">");
        for (int i = 0, len = text.length(); i < len; i++) {
            int codePoint = text.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            if (chars[0] != ' ' && cMap.containsKey(chars[0])) {
                sb.append(cMap.get(chars[0]));
                continue;
            }
            for (int j = 0, l = Fonts.TYPES.length; j < l; j++) {
                if (Fonts.TYPES[j].canDisplay(codePoint)) {
                    // 中文
                    if (j == 0) sb.append(chars[0]);
                    else
                        sb.append("<span style=\"font-family:'" + Fonts.TYPES[j].getFontName() + "'\">" + new String(chars) + "</span>");
                    if (chars.length == 2) i++;
                    break;
                }
            }
        }
        sb.append("</div></html>");
        return sb.toString();
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示
     *
     * @param text
     * @return
     */
    public static String textToHtml(String text, boolean autoWrap) {
        if (text.startsWith("<html>") || text.trim().equals("")) return text;
        StringBuffer sb = new StringBuffer();
        sb.append("<html><div style=\"white-space:nowrap;\">");
        for (int i = 0, len = text.length(); i < len; i++) {
            char ch = text.charAt(i);
            // 韩语
            if (CharsetUtils.isKnChar(ch)) sb.append("<font face=\"Malgun Gothic\">" + ch + "</font>");
            else sb.append(ch);
            if (autoWrap && i > 0 && i % 50 == 0) sb.append("<br>");
        }
        sb.append("</div></html>");
        return sb.toString();
    }

    /**
     * 去掉字符串中所有 HTML 标签，并将转义后的符号还原
     *
     * @param s
     * @return
     */
    public static String removeHTMLLabel(String s) {
        s = s.replaceAll("<br ?/?>", "\n");
        Pattern pattern = Pattern.compile("<[^>]+>");
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll("")
                .replace("&nbsp;", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&apos;", "'");
    }

    /**
     * 字符串根据每行字符数 threshold 加 <br/> 换行
     *
     * @param s
     * @param threshold
     * @return
     */
    public static String wrapLine(String s, int threshold) {
        String ts = removeHTMLLabel(s);
        if (ts.length() <= threshold) return s;
        StringBuffer sb = new StringBuffer();
        for (int i = 0, c = 0, len = ts.length(); i < len; i++) {
            char ch = ts.charAt(i);
            sb.append(ch);
            if (++c == threshold) {
                sb.append("<br/>");
                c = 0;
            }
        }
        return textToHtmlWithoutLtGt(sb.toString());
    }

    /**
     * 字符串宽度 thresholdWidth 加 <br/> 换行
     *
     * @param text
     * @param thresholdWidth
     * @return
     */
    public static String wrapLineByWidth(String text, int thresholdWidth) {
        if (thresholdWidth < 0) return text;
        StringBuffer sb = new StringBuffer();
        int sw = 0;
        JLabel label = new JLabel();
        for (int i = 0, len = text.length(); i < len; i++) {
            char ch = text.charAt(i);

            for (int j = 0, l = Fonts.TYPES.length; j < l; j++) {
                if (Fonts.TYPES[j].canDisplay(ch)) {
                    if (ch != '\n') {
                        int tw = label.getFontMetrics(Fonts.TYPES[j]).stringWidth(ch + "");
                        sw += tw;
                        if (sw >= thresholdWidth) {
                            sb.append('\n');
                            sw = tw;
                        }
                    } else sw = 0;
                    sb.append(ch);
                    break;
                }
            }
        }
        return sb.toString();
    }

    /**
     * url 编码
     *
     * @param s
     * @return
     */
    public static String encode(String s) {
        if (null == s) return null;
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * url 解码
     *
     * @param s
     * @return
     */
    public static String decode(String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 去除字符串前后指定字符
     *
     * @param str
     * @param c
     * @return
     */
    public static String trimStringWith(String str, char c) {
        char[] chars = str.toCharArray();
        int len = chars.length;
        int st = 0;
        while ((st < len) && (chars[st] == c)) st++;
        while ((st < len) && (chars[len - 1] == c)) len--;
        return (st > 0) || (len < chars.length) ? str.substring(st, len) : str;
    }
}