package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import com.kitsunecode.mms.core.settings.Settings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Arknights extends IWaifuAdapter {

    private static final String BASE_URL = "http://en.rhinelab.org";

    private static final String SD_IMG_SELECTOR = ".gallerybox a";
    private static final String HD_IMG_SELECTOR = ".fullImageLink a";

    private static final String lang = Settings.getWaifuLanguage();

    public Arknights(String name) {
        super(name);
    }

    /**
     * Loads data from Wiki, we MUST use it only once in a while
     */
    @Override
    protected WaifuData loadFromCustomSource() throws IOException {
        System.out.println("Getting ship home page");
        Document mainDoc = Jsoup.connect(BASE_URL + "/" + name).get();
        System.out.println("Parsing data...");
        return new WaifuData(new ArrayList<>(), loadSkinUrls(mainDoc));
    }

    private List<String> loadSkinUrls(Document doc) throws IOException {
        List<String> urls = new ArrayList<>();
        for (Element element : Selector.select(SD_IMG_SELECTOR, doc)) {
            String hdUrl = element.attr("href");
            Document hdImgDoc = Jsoup.connect(BASE_URL + hdUrl).get();
            String hdUrlString = BASE_URL + Selector.select(HD_IMG_SELECTOR, hdImgDoc).first().attr("href");
            urls.add(hdUrlString);
        }

        return urls;
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return this.data.getDialogs().stream()
                .filter(d -> d.getEvent().equals(event))
                .filter(d -> d.getLanguage().equalsIgnoreCase(lang))
                .collect(Collectors.toList());
    }

    @Override
    public String onTouchEventKey() {
        return "Secretary (Touch)";
    }

    @Override
    public String onIdleEventKey() {
        return "Idle";
    }

    @Override
    public String onLoginEventKey() {
        return "Login";
    }

}
