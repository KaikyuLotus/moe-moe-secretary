package com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.voice;

import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.character.Character;
import com.kitsunecode.mms.core.utils.Util;

import java.util.HashMap;

public class VoicelineDetailMap extends HashMap<String, VoicelineDetails> {

    public static VoicelineDetailMap fromJson(String json) {
        return Util.getGSON().fromJson(json, VoicelineDetailMap.class);
    }

    public VoicelineDetails ofCharacter(Character character) {
        return get(character.getId());
    }

}
