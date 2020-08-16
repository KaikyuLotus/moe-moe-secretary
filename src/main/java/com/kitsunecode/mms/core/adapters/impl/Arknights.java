package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.character.Character;
import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.character.CharacterMap;
import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.charword.Charword;
import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.charword.CharwordMap;
import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.skin.Skin;
import com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.skin.SkinData;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.utils.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Adapter
public class Arknights extends IWaifuAdapter {

    private static final String CHAR_DATA_URL    = "https://raw.githubusercontent.com/Aceship/AN-EN-Tags/master/json/gamedata/en_US/gamedata/excel/character_table.json";
    private static final String SKIN_DATA_URL    = "https://raw.githubusercontent.com/Aceship/AN-EN-Tags/master/json/gamedata/en_US/gamedata/excel/skin_table.json";
    private static final String CHWD_EN_DATA_URL = "https://raw.githubusercontent.com/Aceship/AN-EN-Tags/master/json/gamedata/en_US/gamedata/excel/charword_table.json";

    public Arknights(String name) throws IOException {
        super(name);
    }

    @Override
    protected WaifuData loadFromCustomSource() throws InterruptedException {
        System.out.println("Getting Arknights character data");
        AtomicReference<CharacterMap> characterMap = new AtomicReference<>();
        AtomicReference<SkinData> skinData = new AtomicReference<>();
        AtomicReference<CharwordMap> charwordMap = new AtomicReference<>();

        // Maybe this is useless
        List<Runnable> runnables = Arrays.asList(() -> {
            try {
                System.out.println("Loading chwd data...");
                charwordMap.set(CharwordMap.fromJson(Util.downloadString(CHWD_EN_DATA_URL)));
                System.out.println("chwd loaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
        },() -> {
            try {
                System.out.println("Loading character data...");
                characterMap.set(CharacterMap.fromJson(Util.downloadString(CHAR_DATA_URL)));
                System.out.println("Character data loaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
        },() -> {
            try {
                System.out.println("Loading skins...");
                skinData.set(SkinData.fromJson(Util.downloadString(SKIN_DATA_URL)));
                System.out.println("Skins loaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        List<Thread> threads = runnables.stream().map(Thread::new).collect(Collectors.toList());
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Parsing data...");
        Character character = characterMap.get().getWithName(name);
        List<Skin> skins = skinData.get().ofCharacter(character);
        List<String> skinsUrls = skins.stream().map(Skin::composeUrl).collect(Collectors.toList());
        List<Dialog> dialogs = charwordMap.get().ofCharacter(character).parallelStream().map(Charword::asDialog).collect(Collectors.toList());
        return new WaifuData(dialogs, orderSkins(skinsUrls));
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return super.getDialogs(event).stream()
                .filter(e -> e.getLanguage().equalsIgnoreCase(Settings.getArknightsLanguage()))
                .filter(e -> !"onLogin".equals(event) || e.getEvent().equalsIgnoreCase(event))
                .map((e) -> e.setDialog(e.getDialog().replace("{@nickname}", Settings.getArknightsNickname())))
                .collect(Collectors.toList());
    }

    private double calculateSkinValue(String skinUrl) {
        Matcher matcher = Pattern.compile("_(\\d)(\\+*).png").matcher(skinUrl);
        if (!matcher.find()) return 20; // Extra skin
        double nValue = Double.parseDouble(matcher.group(1));
        boolean hasPlus = !"".equals(matcher.group(2));
        return (hasPlus) ? nValue + 0.5d : nValue; // Special skin with order or standard skin
    }

    private List<String> orderSkins(List<String> skins) {
        return skins.stream().sorted(Comparator.comparingDouble(this::calculateSkinValue)).collect(Collectors.toList());
    }

}
