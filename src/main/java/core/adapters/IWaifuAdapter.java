package core.adapters;

import core.entities.Dialog;
import core.entities.waifudata.WaifuData;
import core.utils.Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


public interface IWaifuAdapter {

    int getSkinCount();

    String getName();

    String getSkinUrl(int skinNumber);

    String onTouchEventKey();

    String onIdleEventKey();

    String onLoginEventKey();

    List<String> getSkinNames();

    List<Dialog> getDialogs();

    List<Dialog> getDialogs(String event);

    WaifuData getWaifuData();

    long getUptime();

    static File getDataFile(IWaifuAdapter waifu) {
        return Paths.get("resources", waifu.getName(), "data.json").toFile();
    }

    static void saveDataToFile(IWaifuAdapter waifu) throws IOException {
        File file = getDataFile(waifu);
        WaifuData waifuData = waifu.getWaifuData();
        System.out.println("Saving waifu " + waifu.getName() + " data...");
        Files.write(file.toPath(), Util.serializeWaifuData(waifuData).getBytes(), StandardOpenOption.CREATE);
        System.out.println("Done!");
    }

    static WaifuData getDataFromFile(IWaifuAdapter waifu) throws IOException {
        File file = getDataFile(waifu);
        System.out.println("Reading waifu " + waifu.getName() + " data...");
        String jsonData = String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1));
        System.out.println("Readed " + (jsonData.length() * 2) + " bytes of hecking waifu data!");
        return Util.deserializeWaifuJson(jsonData);
    }

    static boolean hasSavedFile(IWaifuAdapter waifu) {
        return getDataFile(waifu).exists();
    }

    /**
     * Downloads a file only if it's not present in the folder
     * <p>
     * If the file ends with .ogg it's an audio, otherwise it's a .png
     * If the file is already present the this method just reads and returns it
     * If something goes wrong throws an exception, really angery waifu incoming in that case <3
     *
     * @param waifu    The waifu who owns the file
     * @param url      The file's url
     * @param fileName File's name
     * @return File's byte array
     * @throws IOException If something goes wrong while reading/wrinting
     */
    static byte[] downloadFile(IWaifuAdapter waifu, String url, String fileName) throws IOException {
        String specificFolder = url.endsWith(".ogg") ? "audios" : "skins";
        File resourceFile = Paths.get("resources", waifu.getName(), specificFolder, fileName).toFile();
        if (!resourceFile.exists() && !Util.downloadFile(url, resourceFile)) {
            throw new RuntimeException("Can't download file " + fileName + " from url " + url + "for waifu " + waifu.getName());
        }

        return Files.readAllBytes(resourceFile.toPath());
    }

}
