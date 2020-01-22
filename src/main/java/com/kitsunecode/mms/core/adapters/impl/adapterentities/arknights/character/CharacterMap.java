package com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.character;

import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.exceptions.CharacterNotFound;
import com.kitsunecode.mms.core.utils.Util;

import java.util.HashMap;

public class CharacterMap extends HashMap<String, Character> {

    public static CharacterMap fromJson(String json) {
        CharacterMap charList = Util.getGSON().fromJson(json, CharacterMap.class);
        for (Entry<String, Character> character : charList.entrySet()) {
            character.getValue().setId(character.getKey());
        }
        return charList;
    }

    public Character getWithName(String name) {
        for (Entry<String, Character> character : entrySet()) {
            if (character.getValue().getName().equalsIgnoreCase(name)) {
                return character.getValue();
            }
        }
        throw new CharacterNotFound("Character with name '" + name + "' not found");
    }

}
