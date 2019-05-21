package azurlane.endpoints;

import azurlane.entities.Dialog;
import azurlane.entities.Image;
import core.settings.Settings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Ship implements IEndpoint {

    private static final String LANGUAGE    = "ship.language";
    private static final String NATIVE_LANG = "ship.language.useNative";

    private static final String IMAGES_SELECTOR = ".adaptiveratioimg > a > img";
    private static final String TABLE_ROWS_JAP  = "div[title='Japanese Server'] > table:nth-child(3) > * tr";
    private static final String TABLE_ROWS_CN   = "div[title='Chinese Server'] > table:nth-child(3) > * tr";
    private static final String SKIN_NAMES      = ".azl_box_body > .tabber > div[title]";

    private static final String AUDIO_COL         = "td:nth-child(2) > a";
    private static final String EVENT_COL         = "td:nth-child(1)";
    private static final String DIALOG_NATIVE_COL = "td:nth-child(3)";
    private static final String DIALOG_TRANSL_COL = "td:nth-child(4)";

    private List<String> skinNames;
    private Map<String, List<Dialog>> dialogs;

    private Document mainDoc;
    private Document quotesDoc;

    private String name;

    public Ship(String name) throws Exception {

        System.out.println("Getting ship...");

        mainDoc = Jsoup.connect(BASEURL + "/" + name).get();
        quotesDoc = Jsoup.connect(BASEURL + "/" + name + "/Quotes").get();

        this.name = name;
        this.skinNames = getSkinNames();
        this.dialogs = loadDialogs();
    }


    public String getName() {
        return name;
    }

    private List<String> getSkinNames() {
        return Selector.select(SKIN_NAMES, mainDoc)
                .stream()
                .map(e -> e.attr("title"))
                .collect(Collectors.toList());
    }

    public int getSkinCount() {
        return skinNames.size();
    }

    public Image getSampleImage(int skinNumber) {
        Elements images = Selector.select(IMAGES_SELECTOR, mainDoc);
        return new Image(BASEURL + images.get(skinNumber).attr("src"));
    }

    public List<Image> getImageSizeSet(int skinNumber) throws IOException {
        Elements images = Selector.select(IMAGES_SELECTOR, mainDoc);
        if (images.isEmpty()) {
            Document imgDoc = Jsoup.connect(BASEURL + "/File:" + getName() + ".png").get();
            images = Selector.select(IMAGES_SELECTOR, imgDoc);
            List<Image> l = new ArrayList<>();
            l.add(new Image(BASEURL + images.get(0).attr("href")));
            return l;
        }

        return Arrays.stream(images.get(skinNumber).attr("srcset")
                .split(","))
                .map(s -> BASEURL + s.trim().split(" ")[0])
                .map(Image::new)
                .collect(Collectors.toList());
    }

    public Map<String, List<Dialog>> getDialogs() {
        return dialogs;
    }

    private Map<String, List<Dialog>> loadDialogs() {

        Map<String, List<Dialog>> phrasesMap = new HashMap<>();

        String lang = Settings.get(LANGUAGE, "Chinese");
        boolean useNative = Settings.get(NATIVE_LANG, false);

        // If language is set to Chinese use chinese otherwise Japanese
        String rowSelector = lang.equalsIgnoreCase("chinese") ? TABLE_ROWS_CN : TABLE_ROWS_JAP;

        Elements rows = Selector.select(rowSelector, quotesDoc);
        // Remove header row
        rows.remove(0);

        for (Element row : rows) {
            Element audioElem = row.select(AUDIO_COL).first();
            String audioUrl = audioElem != null ? audioElem.attr("href") : "";
            String eventText = row.selectFirst(EVENT_COL).text().trim();
            String dialogText = row.selectFirst(useNative ? DIALOG_NATIVE_COL : DIALOG_TRANSL_COL).text();

            Dialog dialog = new Dialog(dialogText, eventText, audioUrl);

            // Initialize if not already present
            if (!phrasesMap.containsKey(eventText)) {
                phrasesMap.put(eventText, new ArrayList<>());
            }

            phrasesMap.get(eventText).add(dialog);
        }

        return phrasesMap;

    }
}
