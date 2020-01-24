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
    private int position = 20;

    @SerializedName("skinIndex")
    private int skinIndex = 0;

    @SerializedName("mirrored")
    private boolean mirrored = false;

    @SerializedName("alwaysOnTop")
    private boolean alwaysOnTop = true;

    @SerializedName("floatingEnabled")
    private boolean floatingEnabled = true;

    private WaifuData() {
        // Private impl
    }

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

    public int getSkinIndex() {
        return skinIndex;
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public boolean isFloatingEnabled() {
        return floatingEnabled;
    }

    public void setSkinIndex(int skinIndex) {
        this.skinIndex = skinIndex;
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }

    public void setFloatingEnabled(boolean floatingEnabled) {
        this.floatingEnabled = floatingEnabled;
    }
}
