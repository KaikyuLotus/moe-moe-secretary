package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import com.kitsunecode.mms.core.entities.Settings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Adapter
public class AzurLane extends IWaifuAdapter {

    private static final String BASE_URL = "https://azurlane.koumakan.jp/wiki";

    private static final String IMAGES_SELECTOR = "div[id='mw-content-text'] > div[class='mw-parser-output'] > div > section > article";
    private static final String IMAGE_SELECTOR = "div.shipskin-image > a.image > img";
    private static final String TABLE_ROWS_JAP = "article[data-title='Japanese Server'] > table:nth-child(3) > * tr";
    private static final String TABLE_ROWS_CN = "article[data-title='Chinese Server'] > table:nth-child(3) > * tr";
    private static final String TABLE_ROWS_EN = "article[data-title='English Server'] > table:nth-child(3) > * tr";

    private static final String AUDIO_COL = "td:nth-child(2) > a";
    private static final String EVENT_COL = "th:nth-child(1)";
    private static final String DIALOG_COL = "td:nth-child(3)";

    private static final String WIKI_ON_CLICK_EVENT_KEY = "Secretary (Touch)";
    private static final String WIKI_ON_LOGIN_EVENT_KEY = "Login";
    private static final String WIKI_ON_IDLE_EVENT_KEY = "Idle";

    private static final String lang = Settings.getWaifuLanguage();

    public AzurLane(String name) throws IOException {
        super(name);
    }

    @Override
    public void afterInit() {
        // Empty impl
    }

    @Override
    protected WaifuData loadFromCustomSource() throws IOException {
        System.out.println("Getting ship quotes");
        Document quotesDoc = Jsoup.connect(BASE_URL + "/" + getName() + "/Quotes").get();
        System.out.println("Getting ship images");
        Document skinsDoc = Jsoup.connect(BASE_URL + "/" + getName() + "/Gallery").get();
        System.out.println("Parsing data...");
        return new WaifuData(loadDialogs(quotesDoc), loadSkinUrls(skinsDoc));
    }

    private List<String> loadSkinUrls(Document doc) {
        return Selector.select(IMAGES_SELECTOR, doc).stream()
                .map(e -> e.select(IMAGE_SELECTOR).first())
                .map(e -> e.hasAttr("srcset") ? e.attr("srcset") : e.attr("src"))
                .map(set -> Arrays.stream(set.split(","))
                        .map(s -> s.trim().split(" ")[0])
                        .reduce((first, second) -> second)
                        .orElse(null))
                .collect(Collectors.toList());
    }

    private List<Dialog> loadDialogs(Document doc) {
        System.out.println("Loading dialogs");
        List<Dialog> dialogList = new ArrayList<>();
        dialogList.addAll(loadDialogs(doc, TABLE_ROWS_CN, "Chinese"));
        dialogList.addAll(loadDialogs(doc, TABLE_ROWS_JAP, "Japanese"));
        dialogList.addAll(loadDialogs(doc, TABLE_ROWS_EN, "English"));
        return dialogList;
    }

    private List<Dialog> loadDialogs(Document doc, String selector, String lang) {
        List<Dialog> dialogList = new ArrayList<>();

        System.out.println("Loading dialogs of language " + lang);

        Elements rows = Selector.select(selector, doc);
        // TODO: Find a better way
        // Some quote pages (for example Bremerton and Enterprise) have
        // an additional p tag and the table is shifted
        if (rows.size() == 0) {
            selector = selector.replace("(3)", "(4)");
            rows = Selector.select(selector, doc);
        }
        rows.remove(0);

        for (Element row : rows) {
            if (row.childrenSize() < 3) {
                continue;
            }
            Element audioElem = row.select(AUDIO_COL).first();
            String audioUrl = audioElem != null ? audioElem.attr("href") : "";
            String eventText = row.selectFirst(EVENT_COL).text().trim();
            if (eventText.contains("Idle")) {
                eventText = "Idle";
            }

            // Replace event with our own
            eventText = eventText.replace(WIKI_ON_IDLE_EVENT_KEY, onIdleEventKey())
                    .replace(WIKI_ON_CLICK_EVENT_KEY, onTouchEventKey())
                    .replace(WIKI_ON_LOGIN_EVENT_KEY, onLoginEventKey());

            String dialogText = row.selectFirst(DIALOG_COL).text();

            if (!"".equals(dialogText)) {
                dialogList.add(new Dialog(lang, dialogText, eventText, audioUrl));
            }
        }

        System.out.println("Found " + dialogList.size() + " dialogs");
        return dialogList;
    }

    @Override
    public String getSkin(int skinIndex) {
        return this.data.getSkins().stream()
                .filter(e -> Settings.isAzurChibi() == e.toLowerCase(Locale.ENGLISH).contains("chibi"))
                .collect(Collectors.toList()).get(skinIndex);
    }

    @Override
    public int getSkinCount() {
        return (int) this.data.getSkins().stream()
                .filter(e -> Settings.isAzurChibi() == e.toLowerCase(Locale.ENGLISH).contains("chibi"))
                .count();
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return this.data.getDialogs().stream()
                .filter(d -> d.getEvent().equals(event))
                .filter(d -> d.getLanguage().equalsIgnoreCase(lang))
                .collect(Collectors.toList());
    }

}
