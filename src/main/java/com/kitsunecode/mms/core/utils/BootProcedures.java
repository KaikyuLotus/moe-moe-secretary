package com.kitsunecode.mms.core.utils;

import com.kitsunecode.mms.core.entities.CommandExecutor;
import com.kitsunecode.mms.core.entities.Settings;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public final class BootProcedures {

    private BootProcedures() {
        // Private impl
    }

    public static void windowsRegiterAutoStartup() throws IOException, URISyntaxException {

        File mmsPath = Paths.get(System.getenv("APPDATA"), "mms").toFile();
        File batRunnerFile = Paths.get(mmsPath.toString(), "Moe Moe Secretary.bat").toFile();
        File jarPathName = new File(Util.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path regfilepath = Paths.get("regfile");

        if (!mmsPath.exists() && !mmsPath.mkdir()) {
            throw new RuntimeException("Cannot create file in APPDATA (" + mmsPath.toString() + ")");
        }

        String regString = Util.readResourceString("utilfiles/regtemplate");
        String batString = Util.readResourceString("utilfiles/battemplate");

        if (regString == null || batString == null) {
            throw new RuntimeException("Cannot read template files.");
        }

        if (!"25120671a9a31ccb19c4aac41bc13178".equals(Util.md5(regString))
                || !"0f3aa7cbef25f2d25f0df1de6cafbef1".equals(Util.md5(batString))) {
            System.out.println("Corrupted resources found, skipping.");
            return; // Sometimes bugs can happen, just disable the functionality instead of crashing the waifu
        }

        regString = regString.replace("{batpath}", batRunnerFile.toString().replace("\\", "\\\\"));
        batString = batString.replace("{jarpath}", jarPathName.getParent())
                .replace("{jarname}", jarPathName.getName());

        Files.write(batRunnerFile.toPath(), batString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(regfilepath, regString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        try {
            if (new CommandExecutor().executeCommand("reg", "IMPORT", regfilepath.toString()).getExitCode() != 0) {
                System.out.println("String used:\n" + regString);
                throw new RuntimeException("Cannot add the boot key to the registry");
            }
        } finally {
            regfilepath.toFile().delete();
        }
        System.out.println("Registered on boot");
    }

    public static void windowsUnregisterAutoStartup() throws IOException {
        // Ignore command failure, if it fails we have nothing to do since the key is already not there
        new CommandExecutor().executeCommand("REG DELETE HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v \"Moe Moe Secretary\" /f");
        Path mmsPath = Paths.get(System.getenv("APPDATA"), "mms");
        Path batRunnerPath = Paths.get(mmsPath.toString(), "Moe Moe Secretary.bat");
        Files.deleteIfExists(batRunnerPath);
        System.out.println("Unregistered from boot");
    }

    public static void windowsStartupProcedure() throws IOException, URISyntaxException {
        if (Settings.isAutoStartupEnabled()) {
            windowsRegiterAutoStartup();
        } else {
            windowsUnregisterAutoStartup();
        }
    }

    public static void unixStartupProcedure() {
        System.out.println("NOT IMPLEMENTED YET");
    }

    public static void startupProcedure() throws Exception {
        if(Util.isWindows()) {
            windowsStartupProcedure();
        } else {
            unixStartupProcedure();
        }
    }

    public static void logToFile() throws IOException {
        if(!Paths.get("logs").toFile().exists() && !Paths.get("logs").toFile().mkdir()) {
            System.out.println("Cannot create logs directory, check you MMS folder");
            System.out.println("Logging to console or /dev/null if the console is not attached");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.'log'", Locale.ENGLISH);
        Comparator<File> fileDateComparator = (e1, e2) ->
                dateFormat.parse(e2.getName(), new ParsePosition(0)).compareTo(dateFormat.parse(e1.getName(), new ParsePosition(0)));

        File[] files = new File("logs").listFiles();
        if (files != null) {
            Arrays.stream(files)
                    .sorted(fileDateComparator)
                    .skip(2)
                    .forEach(File::delete);
        }

        // Creating a File object that represents the disk file.
        File output = new File("logs", dateFormat.format(new Date()));
        if (!output.createNewFile()) {
            System.out.println("Cannot create log file!");
            return;
        }

        System.out.println("Sending logs to file: " + output.getAbsolutePath());
        PrintStream o = new PrintStream(output);
        System.setOut(o);
        System.setErr(o);
    }

}
