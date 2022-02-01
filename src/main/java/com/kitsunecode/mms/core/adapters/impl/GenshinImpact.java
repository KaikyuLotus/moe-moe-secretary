package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Adapter
public class GenshinImpact extends IWaifuAdapter {

    private static final String BASE_URL = "https://genshin-impact.fandom.com";

    private static final String IMAGES_SELECTOR = "a[title='Portrait'] img";
    private static final String FEMALE_IMAGE_SELECTOR = "a[title='Female Portrait'] img";
    private static final String MALE_IMAGE_SELECTOR = "a[title='Male Portrait'] img";

    public GenshinImpact(String name) {
        super(name);
    }

    @Override
    protected WaifuData loadFromCustomSource() throws Exception {
        System.out.println("Getting waifu image");
        Document mainDoc = Jsoup.connect(BASE_URL + "/wiki/" + getName()).get();
        Document outfitsDoc = Jsoup.connect(BASE_URL + "/wiki/" + getName() + "/Outfits").get();
        Document dialogsDoc = Jsoup.connect(BASE_URL + "/wiki/" + getName() + "/Voice-Overs").get();
        return new WaifuData(loadDialogs(dialogsDoc), loadSkinUrls(mainDoc, outfitsDoc));
    }

    @Override
    public void afterInit() {
        // Empty impl
    }

    private List<String> loadSkinUrls(Document mainDoc, Document outfitsDoc) {
        List<String> urls = Stream.of(
                        Selector.select(IMAGES_SELECTOR, mainDoc).first(),
                        Selector.select(MALE_IMAGE_SELECTOR, mainDoc).first(),
                        Selector.select(FEMALE_IMAGE_SELECTOR, mainDoc).first()
                ).filter(Objects::nonNull)
                .map((e) -> e.attr("src").split("/revision/latest")[0])
                .collect(Collectors.toList());

        List<Element> rows = Selector.select("table.article-table", outfitsDoc)
                .first().getElementsByTag("tbody")
                .first().getElementsByTag("tr")
                .stream().skip(2)
                .collect(Collectors.toList());

        if (!rows.isEmpty()) {
            for (Element row : rows) {
                String path = row.getElementsByTag("td").first().getElementsByTag("a").first().attr("href");
                try {
                    Document outfitDoc = Jsoup.connect(BASE_URL + path).get();
                    Element portraitElement = Selector.select("a[title='Portrait']", outfitDoc).first();
                    if (portraitElement != null) {
                        urls.add(portraitElement.attr("href").split("/revision")[0]);
                    }
                    Element previewElement = Selector.select("a[title='Preview']", outfitDoc).first();
                    if (previewElement != null) {
                        urls.add(previewElement.attr("href").split("/revision")[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return urls;

    }

    private List<Dialog> loadDialogs(Document document) {

        List<Dialog> dialogs = new ArrayList<>();

        for (Element table : Selector.select("table.wikitable", document)) {
            for (Element row : table.child(0).getElementsByTag("tr").stream().skip(1).toArray(Element[]::new)) {
                // String event = row.getElementsByTag("th").text();
                Element td = row.getElementsByTag("td").first();
                Element audioSpan = td.getElementsByTag("span").stream().findAny().orElse(null);
                String audioUrl = null;
                if (audioSpan != null) {
                    audioUrl = audioSpan.getElementsByTag("a").first().attr("href").split("/revision")[0];
                    audioSpan.remove();
                }

                String dialog = row.getElementsByTag("td").text();

                dialogs.add(new Dialog("en", dialog, onTouchEventKey(), audioUrl));
            }
        }

        return dialogs;

    }

    @Override
    public String getSkin(int skinIndex) {
        return this.data.getSkins().get(skinIndex);
    }

    @Override
    public int getSkinCount() {
        return this.data.getSkins().size();
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        if (event.equals(onLogoutEventKey())) return Collections.emptyList();
        List<Dialog> dialogs = this.data.getDialogs();
        if (Settings.isOnlyAudioDialogs()) {
            return dialogs.stream().filter((d) -> d.getAudio() != null).collect(Collectors.toList());
        }
        return dialogs;
    }

}
