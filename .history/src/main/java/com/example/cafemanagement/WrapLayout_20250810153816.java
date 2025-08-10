package com.example.cafemanagement;

import javax.swing.*;
import java.awt.*;

/**
 * A FlowLayout that supports wrapping like FlowLayout but properly sizes container.
 * (Common helper used in many Swing apps)
 */
public class WrapLayout extends FlowLayout {
    public WrapLayout() { super(); }
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right + hgap*2);
            int x = 0, y = insets.top + vgap;
            int rowHeight = 0;
            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (!m.isVisible()) continue;
                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                if (x == 0 || (x + d.width) <= maxWidth) {
                    if (x > 0) x += hgap;
                    x += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                } else {
                    x = d.width;
                    y += vgap + rowHeight;
                    rowHeight = d.height;
                }
            }
            y += rowHeight + insets.bottom;
            return new Dimension(maxWidth, y);
        }
    }
}
