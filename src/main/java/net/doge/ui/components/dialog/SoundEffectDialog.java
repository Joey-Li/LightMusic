package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.*;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.componentui.VSliderUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ColorThiefUtils;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * @Author yzx
 * @Description 音效对话框
 * @Date 2020/12/15
 */
public class SoundEffectDialog extends JDialog {
    private final String TITLE = "音效";

    private SoundEffectDialogPanel globalPanel = new SoundEffectDialogPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    private CustomPanel centerPanel = new CustomPanel();

    private CustomPanel topPanel = new CustomPanel();
    private CustomLabel titleLabel = new CustomLabel();
    private CustomPanel windowCtrlPanel = new CustomPanel();
    private CustomButton closeButton = new CustomButton(closeWindowIcon);

    private CustomPanel soundEffectPanel = new CustomPanel();
    private CustomPanel sliderPanel = new CustomPanel();

    private CustomLabel soundEffectLabel = new CustomLabel("音效：");
    private CustomComboBox comboBox = new CustomComboBox<>();
    private final CustomPanel[] panels = {
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel()
    };
    private final CustomLabel[] vals = {
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel()
    };
    private final CustomSlider[] sliders = {
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider()
    };
    private final CustomLabel[] hzs = {
            new CustomLabel("31"),
            new CustomLabel("62"),
            new CustomLabel("125"),
            new CustomLabel("250"),
            new CustomLabel("500"),
            new CustomLabel("1k"),
            new CustomLabel("2k"),
            new CustomLabel("4k"),
            new CustomLabel("8k"),
            new CustomLabel("16k")
    };
    private boolean fitting;

    private PlayerFrame f;
    private UIStyle style;

    // 父窗口和是否是模态，传入 OK 按钮文字，要展示的文件
    public SoundEffectDialog(PlayerFrame f, boolean isModel) {
        super(f, isModel);
        this.f = f;
        this.style = f.getCurrUIStyle();

        for (String se : EqualizerData.names) comboBox.addItem(se);
        comboBox.addItem("自定义");
    }

    public void showDialog() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        topPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // mouseDragged 不能正确返回 button 值，需要借助此方法
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });

        setTitle(TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocation(400, 200);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        add(globalPanel, BorderLayout.CENTER);

        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        pack();
        setLocationRelativeTo(f);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        boolean slight = false;
        if (f.blurType != BlurType.OFF && f.getPlayer().loadedMusic()) {
            bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG) {
                List<Color> colors = ColorThiefUtils.getPalette(bufferedImage, 2);
                bufferedImage = ImageUtils.horizontalGradient(bufferedImage.getWidth(), bufferedImage.getHeight(), colors.get(0), colors.get(colors.size() > 1 ? 1 : 0));
            }
        } else {
            UIStyle style = f.getCurrUIStyle();
            bufferedImage = style.getImg();
            slight = style.isPureColor();
        }
        if (bufferedImage == null) bufferedImage = f.getDefaultAlbumImage();
        doBlur(bufferedImage, slight);
    }

    // 初始化标题栏
    void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setText(TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        closeButton.setIcon(ImageUtils.dye(closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(closeWindowIcon.getIconWidth() + 2, closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            f.currDialogs.remove(this);
            dispose();
        });
        // 鼠标事件
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));
        FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new Dimension(40, 30));
        windowCtrlPanel.add(closeButton);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        globalPanel.add(topPanel, BorderLayout.NORTH);
    }

    void initView() {
        centerPanel.setLayout(new BorderLayout());

        // 音效选择面板
        soundEffectLabel.setForeground(style.getLabelColor());
        comboBox.addItemListener(e -> {
            // 避免事件被处理 2 次！
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            String s = (String) comboBox.getSelectedItem();
            // 记录当前音效
            f.currSoundEffectName = s;
            for (int i = 0, len = EqualizerData.names.length; i < len; i++) {
                if (EqualizerData.names[i].equals(s)) {
                    // 记录当前均衡
                    f.ed = EqualizerData.data[i];
                    f.getPlayer().adjustEqualizerBands(EqualizerData.data[i]);
                    fitData(EqualizerData.data[i]);
                    break;
                }
            }
        });
        // 下拉框 UI
        Color buttonColor = style.getButtonColor();
        comboBox.setUI(new ComboBoxUI(comboBox, f, buttonColor, 220));

        soundEffectPanel.add(soundEffectLabel);
        soundEffectPanel.add(comboBox);
        centerPanel.add(soundEffectPanel, BorderLayout.NORTH);

        // 滑动条面板
        sliderPanel.setLayout(new GridLayout(1, 10));
        for (int i = 0, len = panels.length; i < len; i++) {
            CustomSlider s = sliders[i];
            // 滑动条
            s.setUI(new VSliderUI(s, style.getSliderColor(), style.getSliderColor()));
            s.setPreferredSize(new Dimension(30, 300));
            s.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            s.setMinimum(EqualizerData.MIN_GAIN);
            s.setMaximum(EqualizerData.MAX_GAIN);
            s.setOrientation(SwingConstants.VERTICAL);
//            s.setPaintTicks(true);
//            s.setPaintLabels(true);
//            s.setMajorTickSpacing(4);
//            s.setMinorTickSpacing(1);
//            s.setSnapToTicks(true);
            s.addChangeListener(e -> {
                // 更新值
                updateVals();
                if (!fitting) {
                    comboBox.setSelectedItem("自定义");
                    // 调整并记录当前均衡
                    f.getPlayer().adjustEqualizerBands(f.ed = getData());
                }
            });

            Color labelColor = style.getLabelColor();
            // 值
            vals[i].setForeground(labelColor);
            // 频率
            hzs[i].setForeground(labelColor);

            panels[i].setLayout(new BorderLayout());
            panels[i].setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            panels[i].add(vals[i], BorderLayout.NORTH);
            panels[i].add(s, BorderLayout.CENTER);
            panels[i].add(hzs[i], BorderLayout.SOUTH);

            sliderPanel.add(panels[i]);
        }

        // 加载当前音效
        comboBox.setSelectedItem(f.currSoundEffectName);
        // 加载当前均衡
        fitData(f.ed);

        centerPanel.add(sliderPanel, BorderLayout.CENTER);
    }

    // 根据滑动条的值获取均衡数据
    double[] getData() {
        double[] data = new double[EqualizerData.BAND_NUM];
        int i = 0;
        for (CustomSlider slider : sliders) data[i++] = slider.getValue();
        return data;
    }

    // 根据均衡数据调整滑动条
    void fitData(double[] data) {
        fitting = true;
        for (int i = 0, len = data.length; i < len; i++) sliders[i].setValue((int) data[i]);
        fitting = false;
    }

    // 更新值显示
    void updateVals() {
        for (int i = 0, len = sliders.length; i < len; i++) {
            int val = sliders[i].getValue();
            String s = String.valueOf(val > 0 ? "+" + val : val);
            vals[i].setText(s);
        }
    }

    void doBlur(BufferedImage bufferedImage, boolean slight) {
        Dimension size = getSize();
        int dw = size.width, dh = size.height;
        try {
            // 截取中间的一部分(有的图片是长方形)
            bufferedImage = ImageUtils.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            bufferedImage = ImageUtils.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            bufferedImage = slight ? ImageUtils.slightDarker(bufferedImage) : ImageUtils.darker(ImageUtils.doBlur(bufferedImage));
            // 放大至窗口大小
            bufferedImage = dw > dh ? ImageUtils.width(bufferedImage, dw) : ImageUtils.height(bufferedImage, dh);
            int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
            // 裁剪中间的一部分
            bufferedImage = Thumbnails.of(bufferedImage)
                    .scale(1f)
                    .sourceRegion(dw > dh ? 0 : (iw - dw) / 2, dw > dh ? (ih - dh) / 2 : 0, dw, dh)
                    .outputQuality(0.1)
                    .asBufferedImage();
            // 设置圆角
            bufferedImage = ImageUtils.setRadius(bufferedImage, 10);
            globalPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private class SoundEffectDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public SoundEffectDialogPanel() {
            // 阴影边框
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
        }

        public void setBackgroundImage(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            // 避免锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundImage != null) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.drawImage(backgroundImage, pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, this);
            }

            // 画边框阴影
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
                g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }
}
