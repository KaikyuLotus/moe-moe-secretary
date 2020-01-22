package com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.skin;

import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.character.Character;
import com.kitsunecode.mms.core.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkinData {

    private CharSkins charSkins;

    public static SkinData fromJson(String json) {
        return Util.getGSON().fromJson(json, SkinData.class);
    }

    public List<Skin> ofCharacter(Character character) {
        List<Skin> skins = new ArrayList<>();
        for (Map.Entry<String, Skin> skinSet : charSkins.entrySet()) {
            if (skinSet.getValue().getCharId().equalsIgnoreCase(character.getId())) {
                skins.add(skinSet.getValue());
            }
        }
        return skins;
    }

}
