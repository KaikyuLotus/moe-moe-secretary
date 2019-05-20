package core;

import core.entities.FileWatcher;
import core.entities.Secretary;
import core.settings.Settings;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<Secretary> windows = new ArrayList<>();

    private static void inizialize() {
        try {

            for (Secretary sec : windows) {
                sec.close();
                sec.dispose();
            }
            windows.clear();

            Settings.reload();

            for (String shipName : Settings.getArray("ship.names", ",")) {
                windows.add(new Secretary(shipName));
            }

            String baseName = Settings.get("ship.name", "");
            if (!"".equals(baseName)) {
                windows.add(new Secretary(baseName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public static void main(String[] args) {

        System.out.println("Starting...");

        inizialize();

        new FileWatcher(Paths.get(Settings.configPath), Main::inizialize).watch();

        System.out.println("Done!");
    }
}
