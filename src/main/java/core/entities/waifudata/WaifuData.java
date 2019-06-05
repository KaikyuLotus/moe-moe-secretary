package core.entities.waifudata;

import com.google.gson.annotations.SerializedName;
import core.entities.Dialog;

import java.util.List;

/**
 * Data structure for waifus data files
 */
public class WaifuData {

	@SerializedName("dialogs")
	private List<Dialog> dialogs;

	@SerializedName("skins")
	private List<String> skins;

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
}
