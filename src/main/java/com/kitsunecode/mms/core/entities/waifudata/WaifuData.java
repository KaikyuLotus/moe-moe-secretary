package com.kitsunecode.mms.core.entities.waifudata;

import com.google.gson.annotations.SerializedName;
import com.kitsunecode.mms.core.entities.Dialog;

import java.util.List;

/**
 * Data structure for waifus data files
 */
public class WaifuData {

	@SerializedName("dialogs")
	private List<Dialog> dialogs;

	@SerializedName("skins")
	private List<String> skins;

	@SerializedName("lastPosition")
	private int position;

	public WaifuData(List<Dialog> dialogs, List<String> skinUrls) {
		this.dialogs = dialogs;
		this.skins = skinUrls;
	}

	public List<Dialog> getDialogs() {
		return dialogs;
	}

	public List<String> getSkins() {
		return skins;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
