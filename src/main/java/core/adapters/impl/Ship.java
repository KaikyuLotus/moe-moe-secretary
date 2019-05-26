package core.adapters.impl;

import core.adapters.IWaifuAdapter;
import core.entities.Dialog;
import core.entities.WaifuImage;
import core.entities.exceptions.StartFailedException;
import core.settings.Settings;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.util.*;
import java.util.stream.Collectors;

public class Ship implements IWaifuAdapter {

    private static final String BASE_URL = "https://azurlane.koumakan.jp";

    private static final String LANGUAGE    = "waifu.language";
    private static final String NATIVE_LANG = "waifu.language.useNative";

    private static final String IMAGES_SELECTOR = ".adaptiveratioimg > a > img";
    private static final String TABLE_ROWS_JAP  = "div[title='Japanese Server'] > table:nth-child(3) > * tr";
    private static final String TABLE_ROWS_CN   = "div[title='Chinese Server'] > table:nth-child(3) > * tr";
    private static final String SKIN_NAMES      = ".azl_box_body > .tabber > div[title]";

    private static final String AUDIO_COL         = "td:nth-child(2) > a";
    private static final String EVENT_COL         = "td:nth-child(1)";
    private static final String DIALOG_NATIVE_COL = "td:nth-child(3)";
    private static final String DIALOG_TRANSL_COL = "td:nth-child(4)";

    private String                    name;
    private List<String>              skinNames;
    private Map<String, List<Dialog>> dialogs;
    private List<WaifuImage[]>        imageSizeSets;


    public Ship(String name) throws Exception {

        try {
            System.out.println("Getting ship home page");
            Document mainDoc = Jsoup.connect(BASE_URL + "/" + name).get();
            System.out.println("Getting ship quotes");
            Document quotesDoc = Jsoup.connect(BASE_URL + "/" + name + "/Quotes").get();
            System.out.println("Parsing data...");

            this.name = name;
            this.skinNames = loadSkinNames(mainDoc);
            this.dialogs = loadDialogs(quotesDoc);
            this.imageSizeSets = loadImageSizeSet(mainDoc);
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

    private List<String> loadSkinNames(Document doc) {
        return Selector.select(SKIN_NAMES, doc)
                .stream()
                .map(e -> e.attr("title"))
                .collect(Collectors.toList());
    }

    private List<WaifuImage[]> loadImageSizeSet(Document doc) {
        return Selector.select(IMAGES_SELECTOR, doc).stream()
                .map(e -> e.attr("srcset"))
                .map(set -> Arrays.stream(set.split(","))
                        .map(s -> BASE_URL + s.trim().split(" ")[0])
                        .map(WaifuImage::new)
                        .toArray(WaifuImage[]::new))
                .collect(Collectors.toList());
    }

    private Map<String, List<Dialog>> loadDialogs(Document doc) {

        Map<String, List<Dialog>> phrasesMap = new HashMap<>();

        String lang = Settings.get(LANGUAGE, "Chinese");
        boolean useNative = Settings.get(NATIVE_LANG, false);

        // If language is set to Chinese use chinese otherwise Japanese
        String rowSelector = lang.equalsIgnoreCase("chinese") ? TABLE_ROWS_CN : TABLE_ROWS_JAP;

        Elements rows = Selector.select(rowSelector, doc);
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getSkinNames() {
        return this.skinNames;
    }

    @Override
    public int getSkinCount() {
        return skinNames.size();
    }

    @Override
    public WaifuImage[] getImageSizeSet(int skinIndex) {
        if (skinIndex >= getSkinCount() || skinIndex < 0) {
            return null;
        }
        return this.imageSizeSets.get(skinIndex);
    }

    @Override
    public Map<String, List<Dialog>> getDialogs() {
        return dialogs;
    }

    @Override
    public String onTouchEventKey() {
        return "Secretary (Touch)";
    }

    @Override
    public String onIdleEventKey() {
        return "Secretary (Idle)";
    }

    @Override
    public String onLoginEventKey() {
        return "Login";
    }
}
