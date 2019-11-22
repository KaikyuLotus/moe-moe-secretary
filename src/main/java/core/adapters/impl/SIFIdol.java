package core.adapters.impl;

import core.adapters.IWaifuAdapter;
import core.entities.Dialog;
import core.entities.waifudata.WaifuData;
import core.settings.Settings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SIFIdol implements IWaifuAdapter {

    private static final String QUOTES_WIKI_URL = "https://decaf.kouhi.me/lovelive/index.php?title=%s";

    private static final String BASE_URL = "https://schoolido.lu";
    private static final String CARD_URL = BASE_URL + "/cards/%s/";

    private static final String NAME_SELECTOR = "tr:nth-child(2) > td > strong";
    private static final String SKIN_LINKS    = "td > a";
    private static final String QUOTES_SEL    = "#mw-content-text > *";

    private WaifuData data;

    private String code;
    private String name;
    private String idolName;

    public SIFIdol(String code) throws IOException {
        this.code = code;
        this.name = code;
        if (IWaifuAdapter.hasSavedFile(this)) {
            data = IWaifuAdapter.getDataFromFile(this);
        } else {
            data = loadFromWiki();
            IWaifuAdapter.saveDataToFile(this);
        }
    }

    private WaifuData loadFromWiki() throws IOException {
        Document mainDoc = Jsoup.connect(String.format(CARD_URL, this.code)).get();
        this.idolName = getIdolName(mainDoc);

        List<String> urls = getIdolSkinUrls(mainDoc);

        List<Dialog> dialogs = new ArrayList<>();
        try {
            Document quotesDoc = Jsoup.connect(String.format(QUOTES_WIKI_URL, this.idolName)).get();
            dialogs = getDialogsFromWiki(quotesDoc, onIdleEventKey());
            dialogs.addAll(getDialogsFromWiki(quotesDoc, onTouchEventKey()));
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
            // Filter here
            if (info.contains("#") && !info.contains(this.code)) {
                return null;
            }
        }
        return dialog;
    }

    private List<Dialog> getDialogsFromWiki(Document doc, String section) {
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
                    dialogs.add(new Dialog("english", elaborated, section, null));
                }
            }
        }
        return dialogs;
    }

    private List<String> getIdolSkinUrls(Document doc) {
        return Selector.select(SKIN_LINKS, doc).stream()
                .filter(e -> e.text().toLowerCase().contains("transparent: "))
                .map(e -> "https:" + e.attr("href"))
                .collect(Collectors.toList());
    }

    private String getIdolName(Document doc) {
        Elements nameElem = Selector.select(NAME_SELECTOR, doc);
        return nameElem.isEmpty() ? null : nameElem.text();
    }


    @Override
    public int getSkinCount() {
        return this.data.getSkins().size();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getShowableName() {
        return this.idolName;
    }

    @Override
    public String getSkin(int skinNumber) {
        return this.data.getSkins().get(skinNumber);
    }

    @Override
    public String onTouchEventKey() {
        return "Tapping the Character";
    }

    @Override
    public String onIdleEventKey() {
        return "Home Screen";
    }

    @Override
    public String onLoginEventKey() {
        return "Home Screen";
    }

    @Override
    public List<Dialog> getDialogs() {
        return this.data.getDialogs();
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return this.data.getDialogs().stream()
                .filter(d -> d.getEvent().equalsIgnoreCase(event))
                .collect(Collectors.toList());
    }

    @Override
    public WaifuData getWaifuData() {
        return this.data;
    }

    @Override
    public long getUptime() {
        return 0;
    }
}
