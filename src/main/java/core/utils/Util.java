package core.utils;

import com.google.gson.Gson;
import core.entities.waifudata.WaifuData;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Util {

    private static final Gson GSON = new Gson();

    private static final long   SLEEP_PRECISION      = TimeUnit.MILLISECONDS.toNanos(1);
    private static final String AGENT                = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

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

    /**
     * Warning, this function does not check if the file already exists, do it before calling it!
     */
    public static boolean downloadFile(String url, File resourceFile) {
        try {
            System.out.println("Downloading: " + url + " to " + resourceFile);
            if (resourceFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(resourceFile.getAbsoluteFile())) {
                    URL myURL = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
                    connection.setRequestProperty("User-Agent", AGENT);
                    fos.write(IOUtils.toByteArray(connection.getInputStream()));
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Oh no, I failed...
        return false;
    }

    public static void checkFolders(String name) {
        File[] files = new File[]{
                Paths.get("resources").toFile(),
                Paths.get("resources", name).toFile(),
                Paths.get("resources", name, "audios").toFile(),
                Paths.get("resources", name, "skins").toFile()
        };

        for (File file : files) {
            if (!file.exists()) {
                if (!file.mkdir()) {
                    throw new RuntimeException("Cannot create folder: " + file.getAbsolutePath());
                }
            }
        }
    }

    public static WaifuData deserializeWaifuJson(String jsonData) {
        return GSON.fromJson(jsonData, WaifuData.class);
    }

    public static String serializeWaifuData(WaifuData data) {
        return GSON.toJson(data);
    }
}
