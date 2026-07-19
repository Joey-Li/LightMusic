//package net.doge.ui.widget.textfield.highlighter.painter;
//
//import javax.swing.text.BadLocationException;
//import javax.swing.text.DefaultHighlighter;
//import javax.swing.text.JTextComponent;
//import javax.swing.text.View;
//import java.awt.*;
//
//public class CustomHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
//    public CustomHighlightPainter(Color color) {
//        super(color);
//    }
//
//    @Override
//    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
//        if (!(g instanceof Graphics2D) || offs0 == offs1) {
//            return super.paintLayer(g, offs0, offs1, bounds, c, view);
//        }
//
//        try {
//            String text = c.getText();
//            String sub = text.substring(offs0, offs1);
//
//            // 获取起始位置
//            Rectangle startRect = c.modelToView(offs0);
//            // 获取总宽度
//            FontMetrics fm = c.getFontMetrics(c.getFont());
//            int totalWidth = fm.stringWidth(sub);
//
//            // 获取绘制区域
//            Rectangle alloc = bounds.getBounds();
//            int y = alloc.y;
//            int height = alloc.height;
//
//            // 绘制
//            Color color = getColor();
//            if (color == null) color = c.getSelectionColor();
//            if (color != null) {
//                g.setColor(color);
//                g.fillRect(startRect.x, y, totalWidth, height);
//            }
//            return new Rectangle(startRect.x, y, totalWidth, height);
//        } catch (BadLocationException e) {
//            return super.paintLayer(g, offs0, offs1, bounds, c, view);
//        }
//    }
//}
