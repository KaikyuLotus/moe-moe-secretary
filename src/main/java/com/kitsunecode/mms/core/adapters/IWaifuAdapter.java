package com.kitsunecode.mms.core.adapters;

import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import com.kitsunecode.mms.core.utils.Util;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public interface IWaifuAdapter {

	int getSkinCount();

	String getName();

	String getShowableName();

	String getSkin(int skinNumber);

	String onTouchEventKey();

	String onIdleEventKey();

	String onLoginEventKey();

	List<Dialog> getDialogs();

	List<Dialog> getDialogs(String event);

	WaifuData getWaifuData();

	long getUptime();

	static File getDataFile(IWaifuAdapter waifu) {
		return Paths.get("resources", waifu.getName(), "data.json").toFile();
	}

	static void saveDataToFile(IWaifuAdapter waifu) throws IOException {
		File      file      = getDataFile(waifu);
		WaifuData waifuData = waifu.getWaifuData();
		System.out.println("Saving waifu " + waifu.getName() + " data...");
		FileUtils.writeStringToFile(file, Util.serializeWaifuData(waifuData), "UTF-8");
	}

	static WaifuData getDataFromFile(IWaifuAdapter waifu) throws IOException {
		File file = getDataFile(waifu);
		System.out.println("Reading waifu " + waifu.getName() + " data...");
		String jsonData = String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
		System.out.println("Readed " + (jsonData.length() * 2) + " bytes of waifu data!");
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
		File   resourceFile   = Paths.get("resources", waifu.getName(), specificFolder, fileName).toFile();

		byte[] resourceData = null;

		// Use local if exists
		if (resourceFile.exists()) {
			resourceData = Files.readAllBytes(resourceFile.toPath());
		}

		// Try to download
		if (resourceData == null) {
			resourceData = Util.downloadFile(url, resourceFile);
		}

		if (resourceData == null) {
			throw new RuntimeException("Can't download/find file " + fileName + " from url/path " + url);
		}

		return resourceData;

	}

	public static void saveData(IWaifuAdapter waifu) throws IOException {
		saveDataToFile(waifu);
	}

}
