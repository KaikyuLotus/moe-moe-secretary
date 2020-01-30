package com.kitsunecode.mms.core;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.utils.BootProcedures;
import com.kitsunecode.mms.core.utils.FileWatcher;
import com.kitsunecode.mms.core.entities.swing.Secretary;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.utils.Util;

import java.nio.file.Paths;

public class Main {

    private static Secretary secretary = null;

    private static void initialize() {
        if (secretary != null) {
            secretary.close();
        }

        Settings.reload();

        String adapter = Settings.getAdapter();
        String name = Settings.getWaifuName();

        System.out.println("Starting " + adapter + " with name " + name);

        Util.catchMoeMoeExceptionsAndExit(() -> {
            IWaifuAdapter waifu = Util.getWaifuFromAdapterName(adapter, name);
            secretary = new Secretary(waifu);
        });
    }

    public static void main(String[] args) {

        Util.catchMoeMoeExceptionsAndExit(() -> {
            BootProcedures.logToFile();
            BootProcedures.startupProcedure();
        });

        initialize();
        new FileWatcher(Paths.get(Settings.configPath), Main::initialize).watch();
    }
}
