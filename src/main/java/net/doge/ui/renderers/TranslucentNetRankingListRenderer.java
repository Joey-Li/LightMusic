package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetMvInfo;
import net.doge.models.NetRankingInfo;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslucentNetRankingListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon rankingIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "rankingItem.png"), ImageConstants.profileWidth));
    private ImageIcon rankingSIcon;

    public TranslucentNetRankingListRenderer(Font font) {
        this.customFont = font;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        rankingIcon = ImageUtils.dye(rankingIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        rankingSIcon = ImageUtils.dye(rankingIcon, selectedColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        JLabel label = (JLabel) component;
//        label.setForeground(isSelected ? selectedColor : foreColor);
//        setDrawBg(isSelected);
//
        NetRankingInfo netRankingInfo = (NetRankingInfo) value;
//        setIconTextGap(15);
//        setText(StringUtils.textToHtml(getText()));
//        setFont(customFont);
//        setIcon(rankingIcon);
//        setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
//        setIcon(netRankingInfo.hasCoverImgThumb() ? new ImageIcon(netRankingInfo.getCoverImgThumb()) : rankingIcon);
//
//        // 所有标签透明
//        label.setOpaque(false);
//        return this;

        CustomPanel outerPanel = new CustomPanel();
        JLabel iconLabel = new JLabel();
        JLabel nameLabel = new JLabel();
        JLabel playCountLabel = new JLabel();
        JLabel updateFreLabel = new JLabel();
        JLabel updateTimeLabel = new JLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIconTextGap(40);
        iconLabel.setIcon(netRankingInfo.hasCoverImgThumb() ? new ImageIcon(netRankingInfo.getCoverImgThumb()) : isSelected ? rankingSIcon : rankingIcon);

        iconLabel.setHorizontalAlignment(CENTER);
        nameLabel.setHorizontalAlignment(CENTER);
        playCountLabel.setHorizontalAlignment(CENTER);
        updateFreLabel.setHorizontalAlignment(CENTER);
        updateTimeLabel.setHorizontalAlignment(CENTER);

        iconLabel.setVerticalAlignment(CENTER);
        nameLabel.setVerticalAlignment(CENTER);
        playCountLabel.setVerticalAlignment(CENTER);
        updateFreLabel.setVerticalAlignment(CENTER);
        updateTimeLabel.setVerticalAlignment(CENTER);

        outerPanel.setOpaque(false);
        iconLabel.setOpaque(false);
        nameLabel.setOpaque(false);
        playCountLabel.setOpaque(false);
        updateFreLabel.setOpaque(false);
        updateTimeLabel.setOpaque(false);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(isSelected ? selectedColor : foreColor);
        playCountLabel.setForeground(isSelected ? selectedColor : foreColor);
        updateFreLabel.setForeground(isSelected ? selectedColor : foreColor);
        updateTimeLabel.setForeground(isSelected ? selectedColor : foreColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        playCountLabel.setFont(customFont);
        updateFreLabel.setFont(customFont);
        updateTimeLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(playCountLabel);
        outerPanel.add(updateFreLabel);
        outerPanel.add(updateTimeLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtils.textToHtml(NetMusicSource.names[netRankingInfo.getSource()]);
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRankingInfo.getName(), maxWidth));
        String playCount = netRankingInfo.hasPlayCount() ? StringUtils.formatNumber(netRankingInfo.getPlayCount()) : "";
        String updateFre = netRankingInfo.hasUpdateFre() ? netRankingInfo.getUpdateFre() : "";
        String updateTime = netRankingInfo.hasUpdateTime() ? netRankingInfo.getUpdateTime() + " 更新" : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        playCountLabel.setText(playCount);
        updateFreLabel.setText(updateFre);
        updateTimeLabel.setText(updateTime);

        Dimension ps = iconLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public void paint(Graphics g) {
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

        super.paint(g);
    }
}