package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.entity.NetRadioInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetRadioListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon radioIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "radioItem.png"), ImageConstants.profileWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        radioIcon = ImageUtils.dye(radioIcon, iconColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetRadioInfo netRadioInfo = (NetRadioInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel dCustomLabel = new CustomLabel();
        CustomLabel categoryLabel = new CustomLabel();
        CustomLabel trackCountLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
//        CustomLabel createTimeLabel = new CustomLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIconTextGap(25);
        iconLabel.setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : radioIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        dCustomLabel.setForeground(textColor);
        categoryLabel.setForeground(textColor);
        trackCountLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
//        createTimeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        dCustomLabel.setFont(customFont);
        categoryLabel.setFont(customFont);
        trackCountLabel.setFont(customFont);
        playCountLabel.setFont(customFont);
//        createTimeLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(dCustomLabel);
        outerPanel.add(categoryLabel);
        outerPanel.add(trackCountLabel);
        outerPanel.add(playCountLabel);
//        outerPanel.add(createTimeLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtils.textToHtml(NetMusicSource.names[netRadioInfo.getSource()]);
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRadioInfo.getName(), maxWidth));
        String dj = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRadioInfo.hasDj() ? netRadioInfo.getDj() : "", maxWidth));
        String category = netRadioInfo.hasCategory() ? netRadioInfo.getCategory() : "";
        String trackCount = netRadioInfo.hasTrackCount() ? netRadioInfo.getTrackCount() + " 节目" : "";
        String playCount = netRadioInfo.hasPlayCount() ? StringUtils.formatNumber(netRadioInfo.getPlayCount()) : "";
//        String createTime = netRadioInfo.hasCreateTime() ? netRadioInfo.getCreateTime() : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        dCustomLabel.setText(dj);
        categoryLabel.setText(category);
        trackCountLabel.setText(trackCount);
        playCountLabel.setText(playCount);
//        createTimeLabel.setText(createTime);

        Dimension ps = iconLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        Dimension ps3 = dCustomLabel.getPreferredSize();
        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        // 画背景
//        if (drawBg) {
//            Graphics2D g2d = (Graphics2D) g;
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.setColor(getForeground());
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
//            // 注意这里不能用 getVisibleRect ！！！
//            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//        }
//
//        super.paintComponent(g);
//    }
}
