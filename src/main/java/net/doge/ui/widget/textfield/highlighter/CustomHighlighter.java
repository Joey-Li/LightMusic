//package net.doge.ui.widget.textfield.highlighter;
//
//import net.doge.ui.widget.textfield.highlighter.painter.CustomHighlightPainter;
//
//import javax.swing.text.BadLocationException;
//import javax.swing.text.DefaultHighlighter;
//import javax.swing.text.Highlighter;
//
//public class CustomHighlighter extends DefaultHighlighter {
//    @Override
//    public Object addHighlight(int p0, int p1, Highlighter.HighlightPainter p) throws BadLocationException {
//        // 如果传入的是默认的 Painter，替换成我们的精确版本
//        if (p instanceof DefaultHighlighter.DefaultHighlightPainter) {
//            p = new CustomHighlightPainter(((DefaultHighlightPainter) p).getColor());
//        }
//        return super.addHighlight(p0, p1, p);
//    }
//}
