package core.entities.waifudata;

import com.google.gson.annotations.SerializedName;
import core.entities.Dialog;

import java.util.List;

/**
 * Data structure for waifus data files
 */
public class WaifuData {

    @SerializedName("skinNames")
    private List<String> skinNames;

    @SerializedName("dialogs")
    private List<Dialog> dialogs;

    @SerializedName("skinUrls")
    private List<String> skinUrls;

    public WaifuData(List<String> skinNames, List<Dialog> dialogs, List<String> skinUrls) {
        this.skinNames = skinNames;
        this.dialogs = dialogs;
        this.skinUrls = skinUrls;
    }

    public List<String> getSkinNames() {
        return skinNames;
    }

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public List<String> getSkinUrls() {
        return skinUrls;
    }
}
