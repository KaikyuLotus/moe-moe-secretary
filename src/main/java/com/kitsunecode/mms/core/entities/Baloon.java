package com.kitsunecode.mms.core.entities;

import com.kitsunecode.mms.core.settings.Settings;
import com.kitsunecode.mms.core.utils.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Baloon extends JLabel {

    private boolean isVisible = false;

    // TODO fix this mess
    public Baloon(int windowWidth, int windowHeight) {

        setSize(Settings.getBaloonWidth(), Settings.getBaloonHeight());
        setMinimumSize(new Dimension(Settings.getBaloonWidth(), Settings.getBaloonHeight()));
        setMaximumSize(new Dimension(Settings.getBaloonWidth(), Settings.getBaloonHeight() * 3));

        setLocation((windowWidth / 2 - Settings.getBaloonWidth() / 2) + Settings.getBaloonXOffset(),
                windowHeight - Settings.getBaloonYOffset());
        setForeground(Settings.getBaloonForeground());
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);

        setFont(new Font(Settings.getBaloonFont("Arial"), Font.PLAIN, Settings.getBaloonFontSize()));
        setVerticalAlignment(SwingConstants.TOP);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public Rectangle getDesiredSize(int parentWidth, int parentHeight) {
        return new Rectangle(
                (parentWidth / 2 - Settings.getBaloonWidth() / 2) + Settings.getBaloonXOffset(),
                parentHeight - Settings.getBaloonYOffset(),
                Settings.getBaloonWidth(),
                Settings.getBaloonHeight() * 3
        );
    }

    public void toggle(boolean visible) {
        isVisible = visible;
        setVisible(visible);
    }

    @Override
    public void paint(Graphics g) {
        if (isVisible) {

            setSize(getWidth(), getPreferredSize().height);

            Graphics2D g2d = (Graphics2D) g;

            boolean qualitySet = Settings.isBaloonHighQualityText();

            if (qualitySet) {
                Util.setHighQuality(g2d);
            }

            g2d.setPaint(Settings.getBaloonBackground(Color.BLACK));
            g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10.0, 10.0));

            if (Settings.isBaloonHighQualityText() && !qualitySet) {
                Util.setHighQuality(g2d);
            }

            super.paint(g2d);
        }
    }
}
