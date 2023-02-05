package com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.charword;

import java.util.List;
import java.util.stream.Collectors;

import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.character.Character;
import com.kitsunecode.mms.core.utils.Util;

public class CharwordMap {

    private CharWords charWords;

    public static CharwordMap fromJson(String json) {
        return Util.getGSON().fromJson(json, CharwordMap.class);
    }

    public List<Charword> ofCharacter(Character character) {
        return charWords.entrySet().parallelStream()
                .filter(e -> e.getValue().getCharId().equalsIgnoreCase(character.getId()))
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }

}
