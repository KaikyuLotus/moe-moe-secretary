package com.kitsunecode.mms.core.utils;

import com.google.gson.Gson;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import com.kitsunecode.mms.core.settings.Settings;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class Util {

    private static final Gson GSON = generateGson();

    private static final Yaml YAML = generateYaml();

    private static final String AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

    private static Gson generateGson() {
        return new Gson();
    }

    private static Yaml generateYaml() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setIndent(4);
        dumperOptions.setIndicatorIndent(2);

        Yaml yaml = new Yaml(dumperOptions);
        yaml.setBeanAccess(BeanAccess.FIELD);

        return yaml;
    }

    public static Gson getGSON() {
        return GSON;
    }

    public static Yaml getYAML() {
        return YAML;
    }

    public static void sleep(long millDurationFloat) {
        try {
            Thread.sleep(millDurationFloat);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage flipImage(BufferedImage image) {
        // Flip the image horizontally
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
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
            if (!Util.isUrl(url)) {
                // Not an URL!
                return false;
            }

            System.out.println("Downloading: " + url + " to " + resourceFile);
            if (resourceFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(resourceFile.getAbsoluteFile())) {
                    URL myURL = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
                    connection.setRequestProperty("User-Agent", AGENT);
                    byte[] data = IOUtils.toByteArray(connection.getInputStream());
                    fos.write(data);
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

    public static String fileFromUrl(String url) {
        if (url != null && url.startsWith("http")) {
            String[] urlParts = url.split("/");
            return urlParts[urlParts.length - 1];
        }
        return url;
    }

    public static WaifuData deserializeWaifu(String data) {
        String fileFormat = Settings.getFileFormat();
        if (fileFormat.equals("YAML")) {
            return YAML.loadAs(data, WaifuData.class);
        } else if (fileFormat.equals("JSON")){
            return GSON.fromJson(data, WaifuData.class);
        } else {
            throw new IllegalArgumentException("Invalid adapter file type");
        }
    }

    public static String serializeWaifuData(WaifuData data) {
        String fileFormat = Settings.getFileFormat();
        if (fileFormat.equals("YAML")) {
            return YAML.dump(data);
        } else if (fileFormat.equals("JSON")){
            return GSON.toJson(data);
        } else {
            throw new IllegalArgumentException("Invalid adapter file type");
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static void openUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) { /* TODO: error handling */ }
        } else { /* TODO: error handling */ }
    }

    public static boolean isUrl(String url) {
        return url.startsWith("http");
    }

    public static BufferedImage toBufferedImage(ImageIcon icon) {
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = bufferedImage.createGraphics();
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();

        return bufferedImage;
    }

    public static String downloadString(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return IOUtils.toString(entity.getContent(), Charset.defaultCharset());
            }
        }
        throw new RuntimeException();
    }
}
