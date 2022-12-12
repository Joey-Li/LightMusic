package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurType;
import net.doge.constants.Colors;
import net.doge.constants.EqualizerData;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.componentui.VSliderUI;
import net.doge.ui.listeners.ButtonMouseListener;
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

    private CustomPanel centerPanel = new CustomPanel();

    private CustomPanel topPanel = new CustomPanel();
    private CustomLabel titleLabel = new CustomLabel();
    private CustomPanel windowCtrlPanel = new CustomPanel();
    private CustomButton closeButton = new CustomButton();

    private CustomPanel soundEffectPanel = new CustomPanel();
    private CustomPanel sliderPanel = new CustomPanel();

    private CustomLabel soundEffectLabel = new CustomLabel("音效：");
    private CustomComboBox<String> comboBox = new CustomComboBox<>();
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
        this.style = f.currUIStyle;

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
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        if (f.blurType != BlurType.OFF && f.player.loadedMusic()) {
            bufferedImage = f.player.getMusicInfo().getAlbumImage();
            if (bufferedImage == f.defaultAlbumImage) bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG)
                bufferedImage = ImageUtils.toGradient(bufferedImage);
        } else {
            UIStyle style = f.currUIStyle;
            bufferedImage = style.getImg();
        }
        doBlur(bufferedImage);
    }

    // 初始化标题栏
    private void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setText(TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        closeButton.setIcon(ImageUtils.dye(f.closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(f.closeWindowIcon.getIconWidth() + 2, f.closeWindowIcon.getIconHeight()));
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

    private void initView() {
        centerPanel.setLayout(new BorderLayout());

        Color labelColor = style.getLabelColor();
        Color sliderColor = style.getSliderColor();

        // 音效选择面板
        soundEffectLabel.setForeground(labelColor);
        comboBox.addItemListener(e -> {
            // 避免事件被处理 2 次！
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            int index = comboBox.getSelectedIndex();
            // 记录当前音效
            f.currSoundEffect = index;
            double[][] eds = EqualizerData.data;
            if (index >= eds.length) return;

            double[] newEd = eds[index];
            // 记录当前均衡
            f.ed = newEd;
            f.player.adjustEqualizerBands(newEd);
            fitData(newEd);
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
            CustomPanel p = panels[i];
            CustomSlider s = sliders[i];
            CustomLabel val = vals[i];
            CustomLabel hz = hzs[i];

            // 滑动条
            s.setUI(new VSliderUI(s, sliderColor, sliderColor));
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
                if (fitting) return;
                comboBox.setSelectedItem("自定义");
                // 调整并记录当前均衡
                f.player.adjustEqualizerBands(f.ed = getData());
            });

            // 值
            val.setForeground(labelColor);
            // 频率
            hz.setForeground(labelColor);

            p.setLayout(new BorderLayout());
            p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            p.add(val, BorderLayout.NORTH);
            p.add(s, BorderLayout.CENTER);
            p.add(hz, BorderLayout.SOUTH);

            sliderPanel.add(p);
        }

        // 加载当前音效
        comboBox.setSelectedIndex(f.currSoundEffect);
        // 加载当前均衡
        fitData(f.ed);

        centerPanel.add(sliderPanel, BorderLayout.CENTER);
    }

    // 根据滑动条的值获取均衡数据
    private double[] getData() {
        double[] data = new double[EqualizerData.BAND_NUM];
        int i = 0;
        for (CustomSlider slider : sliders) data[i++] = slider.getValue();
        return data;
    }

    // 根据均衡数据调整滑动条
    private void fitData(double[] data) {
        fitting = true;
        for (int i = 0, len = data.length; i < len; i++) sliders[i].setValue((int) data[i]);
        fitting = false;
    }

    // 更新值显示
    private void updateVals() {
        for (int i = 0, len = sliders.length; i < len; i++) {
            int val = sliders[i].getValue();
            String s = String.valueOf(val > 0 ? "+" + val : val);
            vals[i].setText(s);
        }
    }

    private void doBlur(BufferedImage bufferedImage) {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        try {
            boolean loadedMusic = f.player.loadedMusic();
            // 截取中间的一部分(有的图片是长方形)
            if (loadedMusic && f.blurType == BlurType.CV) bufferedImage = ImageUtils.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            if (f.gsOn) bufferedImage = ImageUtils.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            if (f.gsOn) bufferedImage = ImageUtils.doBlur(bufferedImage);
            if (f.darkerOn) bufferedImage = ImageUtils.darker(bufferedImage);
            // 放大至窗口大小
            bufferedImage = ImageUtils.width(bufferedImage, dw);
            if (dh > bufferedImage.getHeight())
                bufferedImage = ImageUtils.height(bufferedImage, dh);
            // 裁剪中间的一部分
            if (!loadedMusic || f.blurType == BlurType.CV || f.blurType == BlurType.OFF) {
                int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
                bufferedImage = Thumbnails.of(bufferedImage)
                        .scale(1f)
                        .sourceRegion(iw > dw ? (iw - dw) / 2 : 0, iw > dw ? 0 : (ih - dh) / 2, dw, dh)
                        .outputQuality(0.1)
                        .asBufferedImage();
            } else {
                bufferedImage = ImageUtils.forceSize(bufferedImage, dw, dh);
            }
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
