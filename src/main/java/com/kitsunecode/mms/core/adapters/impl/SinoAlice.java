package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

@Adapter
public class SinoAlice extends IWaifuAdapter {

    private static final String META_OGI_SELECTOR = "meta[property='og:image']";

    private static final String WIKI_URL = "https://sinoalice.game-db.tw/";

    private final String waifuId;

    public SinoAlice(String name) throws IOException {
        super(name);
        String charName = Settings.getWaifuName();

        try {
            Document mainDoc = Jsoup.connect("https://sinoalice.game-db.tw/characters/" + charName).get();
            waifuId = Selector.select(META_OGI_SELECTOR, mainDoc)
                    .first()
                    .attr("content")
                    .split("\\.jpg")[0]
                    .split("CharacterIcon")[1];
        } catch (Exception e) {
            throw new StartFailedException("There was an error while trying to get the character.<br>\n" +
                    "Please click here and search for your waifu, paste her name in waifu.name",
                    "https://sinoalice.game-db.tw/characters/", e);
        }
    }

    @Override
    public String getName() {
        return waifuId;
    }

    @Override
    protected WaifuData loadFromCustomSource() throws URISyntaxException {
        // Creates an URL like https://sinoalice.game-db.tw/images/character_l/245.png
        String skinUrl = new URIBuilder(WIKI_URL).setPath("/images/character_l/" + getName() + ".png").build().toString();
        return new WaifuData(Collections.emptyList(), Collections.singletonList(skinUrl));
    }
}
