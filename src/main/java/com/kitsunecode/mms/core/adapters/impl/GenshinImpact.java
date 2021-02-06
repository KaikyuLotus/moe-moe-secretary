package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Adapter
public class GenshinImpact extends IWaifuAdapter {

    private static final String BASE_URL = "https://genshin-impact.fandom.com/wiki";

    private static final String IMAGES_SELECTOR = "a[title='Portrait'] img";
    private static final String FEMALE_IMAGE_SELECTOR = "a[title='Female Portrait'] img";
    private static final String MALE_IMAGE_SELECTOR = "a[title='Male Portrait'] img";

    public GenshinImpact(String name) {
        super(name);
    }

    @Override
    protected WaifuData loadFromCustomSource() throws Exception {
        System.out.println("Getting waifu image");
        Document mainDoc = Jsoup.connect(BASE_URL + "/" + getName()).get();
        Document dialogsDoc = Jsoup.connect(BASE_URL + "/" + getName() + "/Voicelines").get();
        return new WaifuData(loadDialogs(dialogsDoc), loadSkinUrls(mainDoc));
    }

    @Override
    public void afterInit() {
        // Empty impl
    }

    private List<String> loadSkinUrls(Document document) {
        return Stream.of(
                Selector.select(IMAGES_SELECTOR, document).first(),
                Selector.select(MALE_IMAGE_SELECTOR, document).first(),
                Selector.select(FEMALE_IMAGE_SELECTOR, document).first()
        ).filter(Objects::nonNull)
                .map((e) -> e.attr("src").split("/revision/latest")[0])
                .collect(Collectors.toList());
    }

    private List<Dialog> loadDialogs(Document document) {
        List<Dialog> dialogs = new ArrayList<>();

        Elements elements = document.getElementById("Battle_and_Exploration")
                .parent()
                .nextElementSibling()
                .getElementsByTag("tr");

        for (Element element : elements) {

            for (Element td : element.getElementsByTag("td")) {

                Element th = td.parent().getElementsByTag("th").first();

                if (th != null && (th.hasAttr("width") || th.hasAttr("style"))) continue;

                String audio = null;
                try {
                    audio = td.getElementsByTag("span").first()
                            .getElementsByTag("audio").first()
                            .getElementsByTag("source").first()
                            .attr("src")
                            .split("/revision")[0];
                } catch (NullPointerException e) {
                    // Ignore
                }

                td.getElementsByTag("i").remove();
                td.getElementsByTag("small").remove();

                dialogs.add(new Dialog("en", td.text(), onTouchEventKey(), audio));
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
