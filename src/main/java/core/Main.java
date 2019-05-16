package core;

import core.entities.Secretary;
import core.settings.Settings;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        for (String shipName : Settings.getArray("ship.names", ",")) {
            Secretary sec = new Secretary(shipName);
        }

        String baseName = Settings.get("ship.name", "");
        if (!"".equals(baseName)) {
            Secretary sec2 = new Secretary(baseName);
        }

        System.out.println("Done!");
    }
}
