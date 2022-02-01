package com.kitsunecode.mms.core.adapters;

import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.utils.Util;
import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public abstract class IWaifuAdapter {

    private static final String ON_IDLE_EVENT_KEY = "onIdle";
    private static final String ON_LOGIN_EVENT_KEY = "onLogin";
    private static final String ON_LOGOUT_EVENT_KEY = "onLogout";
    private static final String ON_CLICK_EVENT_KEY = "onClick";
    private static final String ON_LOW_BATTERY_EVENT_KEY = "onLowBattery";
    private static final String ON_HIGH_CPU_USAGE_KEY = "onHighCpu";

    private final long startTimeMillis;

    private final String configName;

    protected WaifuData data;

    protected abstract WaifuData loadFromCustomSource() throws Exception;

    public IWaifuAdapter(String name) {
        this.startTimeMillis = System.currentTimeMillis();
        this.configName = name;
    }

    public final void init() {
        try {
            Util.checkFolders(getName());

            if (hasSavedFile()) {
                data = getDataFromFile();
            } else {
                data = loadFromCustomSource();
                saveDataToFile();
            }

            if (data.getSkins().isEmpty()) {
                throw new StartFailedException("No images found for this waifu");
            }

            afterInit();
        } catch (HttpStatusException e) {
            String message = "Wiki status code: " + e.getStatusCode();
            if (e.getStatusCode() == 404) {
                message += ", probably this waifu does not exist";
            }
            throw new StartFailedException(message, e);
        } catch (StartFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new StartFailedException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    public File getDataFile() {
        return Paths.get("resources", getName(), "data." + Settings.getFileFormat().toLowerCase(Locale.ENGLISH)).toFile();
    }

    public final void saveDataToFile() throws IOException {
        File file = getDataFile();
        WaifuData waifuData = getWaifuData();
        System.out.println("Saving waifu " + getName() + " data...");
        FileUtils.writeStringToFile(file, Util.serializeWaifuData(waifuData), "UTF-8");
    }

    public final WaifuData getDataFromFile() throws IOException {
        File file = getDataFile();
        System.out.println("Reading waifu " + getName() + " data...");
        String jsonData = String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        System.out.println("Readed " + (jsonData.length() * 2) + " bytes of waifu data!");
        String fileFormat = Settings.getFileFormat();
        return Util.deserializeWaifu(jsonData, fileFormat);
    }

    public final boolean hasSavedFile() {
        return getDataFile().exists();
    }

    /**
     * Downloads a file only if it's not present in the folder
     * <p>
     * If the file ends with .ogg it's an audio, otherwise it's a .png
     * If the file is already present the this method just reads and returns it
     * If something goes wrong throws an exception, really angery waifu incoming in that case <3
     *
     * @param url      The file's url
     * @param fileName File's name
     * @return File's byte array
     */
    public File downloadFile(String url, String fileName) {
        String specificFolder = (Util.isUrl(url)) ? (url.endsWith(".png") || url.endsWith(".jpg") ? "skins" : "audios") : "";
        File resourceFile = Paths.get("resources", getName(), specificFolder, Util.safeFileName(fileName)).toFile();

        // Use local if exists
        if (resourceFile.exists()) {
            return resourceFile;
        }

        // Try to download
        if (!Util.isUrl(url)) {
            throw new RuntimeException("Can't find file " + fileName + " from path " + url);
        }

        boolean downloaded = Util.downloadFile(url, resourceFile);

        if (!downloaded) {
            throw new RuntimeException("Can't download/find file " + fileName + " from url/path " + url);
        }

        return resourceFile;

    }

    public WaifuData getWaifuData() {
        return this.data;
    }

    public String getSkin(int skinNumber) {
        return this.data.getSkins().get(skinNumber);
    }

    public int getSkinCount() {
        return this.data.getSkins().size();
    }

    public long getUptime() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public List<Dialog> getDialogs() {
        return data.getDialogs();
    }

    public List<Dialog> getDialogs(String event) {
        return this.data.getDialogs().stream().filter(e -> e.getEvent().equals(event)).collect(Collectors.toList());
    }

    public String getShowableName() {
        return this.configName;
    }

    public String getName() {
        return configName;
    }

    public String onTouchEventKey() {
        return ON_CLICK_EVENT_KEY;
    }

    public String onIdleEventKey() {
        return ON_IDLE_EVENT_KEY;
    }

    public String onLogoutEventKey() {
        return ON_LOGOUT_EVENT_KEY;
    }

    public String onLowBatteryEventKey() {
        return ON_LOW_BATTERY_EVENT_KEY;
    }

    public String onHighCpuUsageKey() {
        return ON_HIGH_CPU_USAGE_KEY;
    }

    public String onLoginEventKey() {
        return ON_LOGIN_EVENT_KEY;
    }

    public abstract void afterInit();
}
