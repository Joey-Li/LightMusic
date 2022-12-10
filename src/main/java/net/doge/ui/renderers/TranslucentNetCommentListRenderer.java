package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.SimplePath;
import net.doge.models.NetCommentInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslucentNetCommentListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon defaultProfile = new ImageIcon(ImageUtils.setRadius(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "profile.png"), ImageConstants.profileWidth), 0.1));
    private ImageIcon defaultProfileS;

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        defaultProfile = ImageUtils.dye(defaultProfile, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        defaultProfileS = ImageUtils.dye(defaultProfile, selectedColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        CustomLabel label = new CustomLabel();
        label.setForeground(isSelected ? selectedColor : foreColor);
        label.setDrawBg(isSelected || index == hoverIndex);

        NetCommentInfo netCommentInfo = (NetCommentInfo) value;
        boolean sub = netCommentInfo.isSub();
        BufferedImage profile = netCommentInfo.getProfile();

        int lw = list.getVisibleRect().width - 10;

        // 使图标靠上
        label.setVerticalTextPosition(TOP);
        label.setHorizontalAlignment(LEFT);
        label.setText(StringUtils.textToHtmlWithSpace(StringUtils.wrapLineByWidth(netCommentInfo.toString(), lw - (sub ? 235 : 160))));
        label.setIconTextGap(15);
        label.setBorder(BorderFactory.createEmptyBorder(0, sub ? 120 : 45, 0, 0));
        label.setFont(customFont);
        label.setIcon(profile != null ? new ImageIcon(profile) : isSelected ? defaultProfileS : defaultProfile);

        Dimension ps = label.getPreferredSize();
        label.setPreferredSize(new Dimension(ps.width, ps.height + 16));
        list.setFixedCellWidth(lw);

        return label;
    }

    @Override
    public void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            // 注意这里不能用 getVisibleRect ！！！
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paintComponent(g);
    }
}
