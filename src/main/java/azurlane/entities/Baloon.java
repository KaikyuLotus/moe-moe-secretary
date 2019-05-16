package azurlane.entities;

import core.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Baloon extends JLabel {

    private static final int baloonYOffset = Settings.get("baloon.yOffset", 300);
    private static final int baloonFontSize = Settings.get("baloon.fontSize", 15);
    private static final String baloonFont = Settings.get("baloon.font", "Arial");
    private static final Color baloonBackground = Settings.get("baloon.background", Color.BLACK);
    private static final Color baloonForeground = Settings.get("baloon.foreground", Color.WHITE);

    private static final int baloonWidth = 400;
    private static final int baloonHeight = 100;



    public Baloon(int windowWidth, int windowHeight) {
        setSize(baloonWidth, baloonHeight);
        setLocation(windowWidth / 2 - baloonWidth / 2, windowHeight - baloonYOffset);
        setForeground(baloonForeground);
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);
        setVisible(false);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(baloonBackground);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10.0, 10.0));

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        setFont(new Font(baloonFont, Font.PLAIN, baloonFontSize));
        setVerticalAlignment(SwingConstants.TOP);
        setHorizontalAlignment(SwingConstants.CENTER);

        super.paint(g2d);
    }
}
