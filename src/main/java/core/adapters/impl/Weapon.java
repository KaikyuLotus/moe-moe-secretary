package core.adapters.impl;

import core.adapters.IWaifuAdapter;
import core.entities.Dialog;
import core.entities.WaifuImage;
import core.entities.exceptions.StartFailedException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Weapon implements IWaifuAdapter {

    private static final String BASE_URL = "https://en.gfwiki.com";

    private static final String IMAGES_URL_SELECTOR     = "ul.gallery.mw-gallery-traditional > li > * a";
    private static final String FULL_IMAGE_URL_SELECTOR = ".fullMedia > a";
    private static final String SKIN_NAMES              = "ul.gallery.mw-gallery-traditional > li > * p";
    private static final String QUOTE_ROWS              = ".tabbertab > * tr";
    private static final String ALL_TDS                 = "td";
    private static final String SOUNDS                  = "span.audio-button";

    private String                    name;
    private List<String>              skinNames;
    private Map<String, List<Dialog>> dialogs;
    private List<String>              imageSources;


    public Weapon(String name) throws StartFailedException {
        try {
            System.out.println("Getting weapon home page");
            Document mainDoc = Jsoup.connect(BASE_URL + "/wiki/" + name).get();
            System.out.println("Getting weapon quotes");
            Document quotesDoc = Jsoup.connect(BASE_URL + "/wiki/" + name + "/Quotes").get();
            System.out.println("Parsing data...");

            this.name = name;
            this.skinNames = loadSkinNames(mainDoc);
            this.dialogs = loadDialogs(quotesDoc);
            this.imageSources = loadImageSources(mainDoc);
        } catch (HttpStatusException e) {
            String message = "Wiki status code: " + e.getStatusCode();
            if (e.getStatusCode() == 404) {
                message += ", probably this weapon does not exist";
            }
            throw new StartFailedException(message);
        } catch (Exception e) {
            throw new StartFailedException(e.getMessage());
        }
    }

    // TODO better error handling
    private String getFullImageLink(String path) {
        try {
            Document imgDoc = Jsoup.connect(BASE_URL + path).get();
            return BASE_URL + Selector.select(FULL_IMAGE_URL_SELECTOR, imgDoc).attr("href");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> loadSkinNames(Document doc) {
        return Selector.select(SKIN_NAMES, doc)
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }

    private Map<String, List<Dialog>> loadDialogs(Document doc) {

        Elements rows = Selector.select(QUOTE_ROWS, doc);

        Map<String, List<Dialog>> dialogs = new HashMap<>();

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

            Dialog dial = new Dialog(dialogString, lastEvent, audioURl);

            if (!dialogs.keySet().contains(lastEvent)) {
                dialogs.put(lastEvent, new ArrayList<>());
            }

            dialogs.get(lastEvent).add(dial);
        }

        return dialogs;
    }

    private List<String> loadImageSources(Document doc) {
        return Selector.select(IMAGES_URL_SELECTOR, doc)
                .stream()
                .map(e -> e.attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getSkinNames() {
        return this.skinNames;
    }

    @Override
    public int getSkinCount() {
        return this.imageSources.size();
    }

    @Override
    public WaifuImage[] getImageSizeSet(int skinNumber) {
        // Load only the page of the needed skin, not for every skin
        if (skinNumber >= getSkinCount() || skinNumber < 0) {
            return null;
        }
        return new WaifuImage[]{new WaifuImage(getFullImageLink(this.imageSources.get(skinNumber)))};
    }

    @Override
    public Map<String, List<Dialog>> getDialogs() {
        return this.dialogs;
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
