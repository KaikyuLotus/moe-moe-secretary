package azurlane.entities;

public class Dialog {
    private String dialog;
    private String event;
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
