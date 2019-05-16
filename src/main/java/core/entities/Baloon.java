package core.entities;

import azurlane.utils.Util;
import core.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Baloon extends JLabel {

    private static final boolean highQuality = Settings.get("baloon.highQuality", true);
    private static final boolean highQualityText = Settings.get("baloon.highQualityText", true);

    private static final int baloonYOffset  = Settings.get("baloon.yOffset", 300);
    private static final int baloonXOffset  = Settings.get("baloon.xOffset", 0);
    private static final int baloonWidth    = Settings.get("baloon.width", 400);
    private static final int baloonHeight   = Settings.get("baloon.height", 100);
    private static final int baloonFontSize = Settings.get("baloon.fontSize", 15);

    private static final String baloonFont = Settings.get("baloon.font", "Arial");

    private static final Color baloonBackground = Settings.get("baloon.background", Color.BLACK);
    private static final Color baloonForeground = Settings.get("baloon.foreground", Color.WHITE);

    private boolean isVisible = false;

    public Baloon(int windowWidth, int windowHeight) {

        setSize(baloonWidth, baloonHeight);
        setMinimumSize(new Dimension(baloonWidth, baloonHeight));
        setMaximumSize(new Dimension(baloonWidth, baloonHeight));

        setLocation((windowWidth / 2 - baloonWidth / 2) + baloonXOffset, windowHeight - baloonYOffset);
        setForeground(baloonForeground);
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);

        setFont(new Font(baloonFont, Font.PLAIN, baloonFontSize));
        setVerticalAlignment(SwingConstants.TOP);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void toggle(boolean visible) {
        isVisible = visible;
        System.out.println("Baloon " + (visible ? "enabled" : "disabled"));
    }

    @Override
    public void paint(Graphics g) {
        if (isVisible) {
            Graphics2D g2d = (Graphics2D) g;

            boolean qualitySet = false;

            if (highQuality) {
                Util.setHighQuality(g2d);
                qualitySet = true;
            }

            g2d.setPaint(baloonBackground);
            g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10.0, 10.0));

            if (highQualityText && !qualitySet) {
                Util.setHighQuality(g2d);
            }

            super.paint(g2d);
        }

    }
}
