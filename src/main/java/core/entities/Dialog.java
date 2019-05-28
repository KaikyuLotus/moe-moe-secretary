package core.entities;

import com.google.gson.annotations.SerializedName;

public class Dialog {

    @SerializedName("dialog")
    private String dialog;

    @SerializedName("event")
    private String event;

    @SerializedName("audio")
    private String audio;

    public Dialog(String dialog, String event, String audio) {
        this.dialog = dialog;
        this.event = event;
        this.audio = audio;
    }

    public String getDialog() {
        return dialog;
    }

    public String getEvent() {
        return event;
    }

    public String getAudio() {
        return audio;
    }
}
