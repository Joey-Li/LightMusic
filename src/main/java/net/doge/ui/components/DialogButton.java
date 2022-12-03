package net.doge.ui.components;

import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.utils.ColorUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @Author yzx
 * @Description 对话框中的按钮自定义 UI
 * @Date 2020/12/13
 */
public class DialogButton extends JButton implements MouseListener {
    private Color foreColor;
    private Color foreColorBk;
    private float alpha = 0.2f;

    void init() {
        addMouseListener(this);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public DialogButton() {
        setForeColor(Colors.WHITE);
        init();
    }

    // 关键词按钮，需显示多种字符
    public DialogButton(String text) {
        super(StringUtils.textToHtml(text));
        setForeColor(Colors.WHITE);
        init();
    }

    // 常规按钮
    public DialogButton(String text, Color foreColor) {
        super(text);
        setForeColor(foreColor);
        init();
    }

    public void setForeColor(Color foreColor) {
        setForeground(foreColor);
        this.foreColor = foreColor;
        this.foreColorBk = foreColor;
        repaint();
    }

    public String getPlainText() {
        return StringUtils.removeHTMLLabel(getText());
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip tooltip = new CustomToolTip(this);
        tooltip.setVisible(false);
        return tooltip;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // 画背景
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(foreColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        super.paintComponent(g);

//        // 画文字
//        String text = getText();
//        FontMetrics fontMetrics = getFontMetrics(getFont());
//        int stringHeight = fontMetrics.getHeight();
//        g2d.setColor(foreColor);
//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//
//        FontMetrics[] metrics = new FontMetrics[Fonts.TYPES.size()];
//        for (int i = 0, len = metrics.length; i < len; i++) {
//            metrics[i] = getFontMetrics(Fonts.TYPES.get(i));
//        }
//
//        // 计算宽度
//        int stringWidth = 0;
//        for (int i = 0, len = text.length(); i < len; i++) {
//            int codePoint = text.codePointAt(i);
//            char[] chars = Character.toChars(codePoint);
//            String str = new String(chars);
//            for (int j = 0, l = metrics.length; j < l; j++) {
//                if (Fonts.TYPES.get(j).canDisplay(codePoint)) {
//                    stringWidth += metrics[j].stringWidth(str);
//                    i += chars.length - 1;
//                    break;
//                }
//            }
//        }
//
//        int widthDrawn = 0, width = getWidth(), height = getHeight();
//        for (int i = 0, len = text.length(); i < len; i++) {
//            int codePoint = text.codePointAt(i);
//            char[] chars = Character.toChars(codePoint);
//            String str = new String(chars);
//            for (int j = 0, l = metrics.length; j < l; j++) {
//                if (Fonts.TYPES.get(j).canDisplay(codePoint)) {
//                    // 只画显示不出的文字
//                    if (j == 0) continue;
//                    g2d.setFont(Fonts.TYPES.get(j));
//                    g2d.drawString(str, (width - stringWidth) / 2 + widthDrawn, (height - stringHeight) / 2 + 16);
//                    widthDrawn += metrics[j].stringWidth(str);
//                    i += chars.length - 1;
//                    break;
//                }
//            }
//        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        foreColor = ColorUtils.darker(foreColor);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        foreColor = foreColorBk;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        alpha = 0.4f;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        alpha = 0.2f;
    }
}
