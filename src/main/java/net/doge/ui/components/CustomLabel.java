package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel {
    private boolean drawBg;

    public CustomLabel() {
        super();
    }

    public CustomLabel(String text) {
        super(text);
    }

    public CustomLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public CustomLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public void paint(Graphics g) {
        if(drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            // 画背景
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paint(g);
    }
}