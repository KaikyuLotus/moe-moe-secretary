package com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.charword;

import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.character.Character;
import com.kitsunecode.mms.core.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CharwordMap extends HashMap<String, Charword> {

    public static CharwordMap fromJson(String json) {
        return Util.getGSON().fromJson(json, CharwordMap.class);
    }

    public List<Charword> ofCharacter(Character character) {
        return entrySet().parallelStream()
                .filter(e -> e.getValue().getCharId().equalsIgnoreCase(character.getId()))
                .map(Entry::getValue)
                .collect(Collectors.toList());
    }

}
