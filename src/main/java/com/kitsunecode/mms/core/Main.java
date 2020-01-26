package com.kitsunecode.mms.core;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.swing.BootFailedFrame;
import com.kitsunecode.mms.core.utils.FileWatcher;
import com.kitsunecode.mms.core.entities.swing.Secretary;
import com.kitsunecode.mms.core.utils.Settings;
import com.kitsunecode.mms.core.utils.Util;

import java.nio.file.Paths;

public class Main {

    private static Secretary secretary = null;

    private static void inizialize() {
        if (secretary != null) {
            secretary.close();
        }

        Settings.reload();

        String adapter = Settings.getAdapter();
        String name = Settings.getWaifuName();

        System.out.println("Starting " + adapter + " with name " + name);

        try {
            IWaifuAdapter waifu = Util.getWaifuFromAdapterName(adapter, name);
            secretary = new Secretary(waifu);
        } catch (Exception e) {
            e.printStackTrace();
            BootFailedFrame frame = new BootFailedFrame(e);
        }
    }

    public static void main(String[] args) {
        inizialize();
        new FileWatcher(Paths.get(Settings.configPath), Main::inizialize).watch();
    }
}
