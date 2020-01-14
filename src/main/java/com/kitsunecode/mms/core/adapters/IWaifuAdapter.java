package com.kitsunecode.mms.core.adapters;

import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import com.kitsunecode.mms.core.settings.Settings;
import com.kitsunecode.mms.core.utils.Util;
import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public abstract class IWaifuAdapter {

    private long startTimeMillis;

    protected String name;
    protected WaifuData data;

    public abstract String onTouchEventKey();

    public abstract String onIdleEventKey();

    public abstract String onLoginEventKey();

    protected abstract WaifuData loadFromCustomSource() throws Exception;

    public IWaifuAdapter(String name) {
        this.startTimeMillis = System.currentTimeMillis();
        this.name = name;

        try {
            if (hasSavedFile()) {
                data = getDataFromFile();
            } else {
                data = loadFromCustomSource();
                saveDataToFile();
            }

            if (data.getSkins().isEmpty()) {
                throw new StartFailedException("No images found for this waifu");
            }

        } catch (HttpStatusException e) {
            String message = "Wiki status code: " + e.getStatusCode();
            if (e.getStatusCode() == 404) {
                message += ", probably this waifu does not exist";
            }
            throw new StartFailedException(message);
        } catch (StartFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new StartFailedException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public File getDataFile() {
        return Paths.get("resources", getName(), "data." + Settings.getFileFormat().toLowerCase()).toFile();
    }

    public void saveDataToFile() throws IOException {
        File file = getDataFile();
        WaifuData waifuData = getWaifuData();
        System.out.println("Saving waifu " + getName() + " data...");
        FileUtils.writeStringToFile(file, Util.serializeWaifuData(waifuData), "UTF-8");
    }

    public WaifuData getDataFromFile() throws IOException {
        File file = getDataFile();
        System.out.println("Reading waifu " + getName() + " data...");
        String jsonData = String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        System.out.println("Readed " + (jsonData.length() * 2) + " bytes of waifu data!");
        return Util.deserializeWaifu(jsonData);
    }

    public boolean hasSavedFile() {
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
     * @throws IOException If something goes wrong while reading/wrinting
     */
    public byte[] downloadFile(String url, String fileName) throws IOException {
        String specificFolder = (Util.isUrl(url)) ? (url.endsWith(".ogg") ? "audios" : "skins") : "";
        File resourceFile = Paths.get("resources", getName(), specificFolder, fileName).toFile();

        byte[] resourceData = null;

        // Use local if exists
        if (resourceFile.exists()) {
            resourceData = Files.readAllBytes(resourceFile.toPath());
        }

        // Try to download
        if (resourceData == null && !Util.isUrl(url)) {
            throw new RuntimeException("Can't find file " + fileName + " from path " + url);
        }

        if (resourceData == null) {
            resourceData = Util.downloadFile(url, resourceFile);
        }

        if (resourceData == null) {
            throw new RuntimeException("Can't download/find file " + fileName + " from url/path " + url);
        }

        return resourceData;

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
        return this.data.getDialogs();
    }

    public String getShowableName() {
        return this.name;
    }

    public String getName() {
        return name;
    }

}
