package core.settings;

import core.Main;

import java.awt.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Settings {

    private static final String configPath = "config/config.properties";

    private static final Map<String, String> data = new HashMap<>();

    private static void load(InputStream is) throws Exception {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            int lineno = 0;

            String line;
            while ((line = br.readLine()) != null) {

                lineno++;

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (!line.contains("=")) {
                    System.out.println("Line #" + lineno + " is invalid, skipping...");
                    continue;
                }

                String[] parts = line.split("=", 2);
                data.put(parts[0].trim(), parts[1].trim());

            }
        }
    }

    private static void init() throws Exception {
        InputStream s;

        System.out.println("Loading default config first");
        s = Main.class.getClassLoader().getResourceAsStream(configPath);
        if (s != null) {
            load(s);
        }

        Path config = Paths.get(configPath);
        if (config.toFile().exists()) {
            System.out.println("Found custom config file, loading it...");
            load(new ByteArrayInputStream(Files.readAllBytes(config)));
        }
    }

    public static Color get(String key, Color defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }

        String[] p = data.get(key).split(",");
        if (p.length != 4) {
            System.out.println("Invalid color: " + data.get(key));
            return defaultValue;
        }

        int[] c = Arrays.stream(p).mapToInt(Integer::parseInt).toArray();

        return new Color(c[0], c[1], c[2], c[3]);
    }

    public static boolean get(String key, boolean defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return Boolean.valueOf(data.get(key));
    }

    public static String get(String key, String defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return data.get(key);
    }

    public static long getLong(String key, long defaultValue) {
        return get(key, defaultValue);
    }

    public static long get(String key, long defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return Long.parseLong(data.get(key));
    }

    public static int get(String key, int defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return Integer.parseInt(data.get(key));
    }

    public static float get(String key, float defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return Float.parseFloat(data.get(key));
    }

    public static double get(String key, double defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return Double.parseDouble(data.get(key));
    }

    public static String[] getArray(String key, String divider) {
        if (!data.containsKey(key)) {
            return new String[0];
        }
        return Arrays.stream(data.get(key).split(divider)).map(String::trim).toArray(String[]::new);
    }

    public static int[] getIntArray(String key, String divider) {
        if (!data.containsKey(key)) {
            return new int[0];
        }
        return Arrays.stream(data.get(key).split(divider)).mapToInt(Integer::parseInt).toArray();
    }

    public static double[] getDoubleArray(String key, String divider) {
        if (!data.containsKey(key)) {
            return new double[0];
        }
        return Arrays.stream(data.get(key).split(divider)).mapToDouble(Double::parseDouble).toArray();
    }

    static {
        try {
            init();
        } catch (Exception e) {
            System.out.println("Cannot load configuration...");
            e.printStackTrace();
        }
    }
}
