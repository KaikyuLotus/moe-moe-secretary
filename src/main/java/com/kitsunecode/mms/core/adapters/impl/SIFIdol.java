package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Adapter
public class SIFIdol extends IWaifuAdapter {

    private static final String QUOTES_WIKI_URL = "https://decaf.kouhi.me/lovelive/index.php?title=%s";

    private static final String BASE_URL = "https://schoolido.lu";
    private static final String CARD_URL = BASE_URL + "/cards/%s/";

    private static final String NAME_SELECTOR = "tr:nth-child(2) > td > strong";
    private static final String SKIN_LINKS = "td > a";
    private static final String QUOTES_SEL = "#mw-content-text > *";

    private static final String WIKI_ON_CLICK_EVENT_KEY = "Tapping the Character";
    private static final String WIKI_ON_LOGIN_EVENT_KEY = "Home Screen";

    private String idolName;

    public SIFIdol(String code) throws IOException {
        super(code);
    }

    @Override
    protected WaifuData loadFromCustomSource() throws IOException {
        Document mainDoc = Jsoup.connect(String.format(CARD_URL, this.name)).get();
        this.idolName = getIdolName(mainDoc);

        List<String> urls = getIdolSkinUrls(mainDoc);

        List<Dialog> dialogs = new ArrayList<>();
        try {
            Document quotesDoc = Jsoup.connect(String.format(QUOTES_WIKI_URL, this.idolName)).get();
            dialogs.addAll(getDialogsFromWiki(quotesDoc, WIKI_ON_LOGIN_EVENT_KEY, onLoginEventKey()));
            dialogs.addAll(getDialogsFromWiki(quotesDoc, WIKI_ON_LOGIN_EVENT_KEY, onIdleEventKey()));
            dialogs.addAll(getDialogsFromWiki(quotesDoc, WIKI_ON_CLICK_EVENT_KEY, onTouchEventKey()));
        } catch (Exception e) {
            System.out.println("Cannot load waifu dialogs...");
        }
        return new WaifuData(dialogs, urls);
    }

    private String elaborateDialog(String dialog, String info, boolean hasJap) {
        if (hasJap) {
            if (!dialog.contains(" ")) {
                return null;
            }
            dialog = dialog.split(" ", 2)[1];
        }

        if (info != null && !info.equals("")) {
            if (info.contains("#") && !info.contains(this.name)) {
                return null;
            }
        }
        return dialog;
    }

    private List<Dialog> getDialogsFromWiki(Document doc, String section, String mmsEventKey) {
        List<Dialog> dialogs = new ArrayList<>();
        boolean isDialog = false;
        for (Element elem : Selector.select(QUOTES_SEL, doc)) {
            if (elem.text().trim().equalsIgnoreCase(section)) {
                isDialog = true;
                continue;
            }

            if (elem.tag().getName().equals("h3")) {
                isDialog = false;
                continue;
            }

            if (isDialog) {
                Elements i = elem.select("i");
                String infoText = null;
                if (i != null) {
                    infoText = i.text();
                    i.remove();
                }

                boolean hasJap = !elem.select("br").isEmpty();
                String elaborated = elaborateDialog(elem.text().trim(), infoText, hasJap);
                if (elaborated != null) {
                    dialogs.add(new Dialog("english", elaborated, mmsEventKey, null));
                }
            }
        }
        return dialogs;
    }

    private List<String> getIdolSkinUrls(Document doc) {
        return Selector.select(SKIN_LINKS, doc).stream()
                .filter(e -> e.text().toLowerCase().contains("transparent: "))
                .map(e -> "https:" + e.attr("href").split("\\?")[0])
                .collect(Collectors.toList());
    }

    private String getIdolName(Document doc) {
        Elements nameElem = Selector.select(NAME_SELECTOR, doc);
        return nameElem.isEmpty() ? null : nameElem.text();
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return this.data.getDialogs().stream()
                .filter(d -> d.getEvent().equalsIgnoreCase(event))
                .collect(Collectors.toList());
    }

}
