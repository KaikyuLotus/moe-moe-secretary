package core.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Util {
    private static final long SLEEP_PRECISION      = TimeUnit.MILLISECONDS.toNanos(1);  //TODO: Determine for current machine
    private static final long SPIN_YIELD_PRECISION = TimeUnit.MILLISECONDS.toNanos(1);  //TODO: Determine for current machine

    public static void sleep(float millDurationFloat) {
        sleepNano((long) (1000000 * millDurationFloat));
    }

    public static void sleepNano(long nanoDuration) {

        try {
            final long end = System.nanoTime() + nanoDuration;
            long timeLeft = nanoDuration;
            do {

                if (timeLeft > SLEEP_PRECISION) {
                    Thread.sleep(1);
                }

                timeLeft = end - System.nanoTime();

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

            } while (timeLeft > 0);
        } catch (Exception e) {
            // Ignore
        }
    }

    public static void sleepNanos(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    public static BufferedImage flipImage(BufferedImage image) {
        // Flip the image horizontally
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static int getYStartPosition(int height) {
        return (int) getScreenSize().getHeight() - height;
    }

    public static Graphics2D setHighQuality(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return g2d;
    }
}
