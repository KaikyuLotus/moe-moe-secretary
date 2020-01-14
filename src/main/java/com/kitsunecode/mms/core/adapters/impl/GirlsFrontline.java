package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GirlsFrontline extends IWaifuAdapter {

    private static final String BASE_URL = "https://en.gfwiki.com";

    private static final String IMAGES_URL_SELECTOR = "ul.gallery.mw-gallery-traditional > li > * a";
    private static final String FULL_IMAGE_URL_SELECTOR = ".fullMedia > a";
    private static final String QUOTE_ROWS = ".tabbertab > * tr";
    private static final String ALL_TDS = "td";
    private static final String SOUNDS = "span.audio-button";

    public GirlsFrontline(String name) {
        super(name);
    }

    /**
     * Loads data from Wiki, we MUST use it only once in a while
     */
    protected WaifuData loadFromCustomSource() throws IOException {
        System.out.println("Getting weapon home page");
        Document mainDoc = Jsoup.connect(BASE_URL + "/wiki/" + name).get();
        System.out.println("Getting weapon quotes");
        Document quotesDoc = Jsoup.connect(BASE_URL + "/wiki/" + name + "/Quotes").get();
        System.out.println("Parsing data...");

        return new WaifuData(loadDialogs(quotesDoc), loadImageSources(mainDoc));
    }

    private String getFullImageLink(String path) {
        try {
            Document imgDoc = Jsoup.connect(BASE_URL + path).get();
            return BASE_URL + Selector.select(FULL_IMAGE_URL_SELECTOR, imgDoc).attr("href");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Dialog> loadDialogs(Document doc) {

        Elements rows = Selector.select(QUOTE_ROWS, doc);

        List<Dialog> dialogs = new ArrayList<>();

        String lastEvent = "";

        for (Element row : rows) {
            Elements tds = row.select(ALL_TDS);
            Elements sound = row.select(SOUNDS);

            if (tds.isEmpty()) {
                continue; // Skip tr(s)
            }
            if (tds.size() < 5) {
                // Create missing dialog td
                tds.add(0, new Element("td").text(lastEvent));
            }

            lastEvent = tds.get(0).text();
            String audioURl = null;
            String dialogString = tds.get(4).text();

            if (!sound.isEmpty()) {
                audioURl = sound.attr("data-src");
            }

            dialogs.add(new Dialog("english", dialogString, lastEvent, audioURl));
        }

        return dialogs;
    }

    private List<String> loadImageSources(Document doc) {
        return Selector.select(IMAGES_URL_SELECTOR, doc)
                .stream()
                .map(e -> e.attr("href"))
                .filter(a -> !a.contains("_S"))
                .filter(a -> a.contains(name))
                .map(this::getFullImageLink)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return data.getDialogs().stream().filter(d -> d.getEvent().equals(event)).collect(Collectors.toList());
    }

    @Override
    public String onTouchEventKey() {
        return "Secretary";
    }

    @Override
    public String onIdleEventKey() {
        return "Secretary";
    }

    @Override
    public String onLoginEventKey() {
        return "Greeting";
    }

}
