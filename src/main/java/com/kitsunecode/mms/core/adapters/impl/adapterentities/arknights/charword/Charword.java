package com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.charword;

import com.kitsunecode.mms.core.entities.Dialog;

public class Charword {

    private static final String VOICE_URL = "https://raw.githubusercontent.com/Aceship/Arknight-voices/main/voice/%s.mp3";

    private String charWordId;
    private String charId;
    private String voiceId;
    private String voiceText;
    private String voiceAsset;
    private String voiceTitle;

    public String getCharWordId() {
        return charWordId;
    }

    public String getCharId() {
        return charId;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public String getVoiceAsset() {
        return voiceAsset;
    }

    public String getVoiceTitle() {
        return voiceTitle;
    }

    public String getVoiceTitleMMSCompatible() {
        if (voiceTitle.contains("Greeting")) {
            return voiceTitle.replace("Greeting", "onLogin");
        }
        return voiceTitle.replace(voiceTitle, "onClick");
    }

    public String getVoiceUrl() {
        return String.format(VOICE_URL, voiceAsset);
    }

    public Dialog asDialog() {
        return new Dialog("en", getVoiceText(), getVoiceTitleMMSCompatible(), getVoiceUrl());
    }
}
