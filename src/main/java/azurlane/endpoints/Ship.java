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

    private static final String IMAGES_ALT_SELECTOR = "#file > a";
    private static final String IMAGES_SELECTOR = ".adaptiveratioimg > a > img";
    private static final String PHRASES_SELECTOR = "div[title='Chinese Ship Lines '] > table > * tr > td:nth-child(5)";
    private static final String EVENTS_SELECTOR = "div[title='Chinese Ship Lines '] > table > * tr > td:nth-child(3)";
    private static final String AUDIO_CELLS_SELECTOR = "div[title='Chinese Ship Lines '] > table > * tr > td:nth-child(2)";
    private static final String TABLE_ROWS = "div[title='Chinese Ship Lines '] > table > * tr";
    private static final String AUDIO_FINAL = "a";

    private static Document doc;

    private String name;

    private Ship(String name) {
        // Private access, this element can be obtained only with .get(name)
        this.name = name;
    }

    public static Ship get(String shipName) throws Exception {
        System.out.println("Getting ship");
        System.out.println(BASEURL + "/" + shipName);
        doc = Jsoup.connect(BASEURL + "/" + shipName).get();
        return new Ship(shipName);
    }

    public String getName() {
        return name;
    }

    public Image getSampleImage(int skinNumber) {
        Elements images = Selector.select(IMAGES_SELECTOR, doc);
        return new Image(BASEURL + images.get(skinNumber).attr("src"));
    }

    public List<Image> getImageSizeSet(int skinNumber) throws IOException {
        Elements images = Selector.select(IMAGES_SELECTOR, doc);
        if (images.isEmpty()) {
            Document imgDoc = Jsoup.connect(BASEURL + "/File:" + getName() + ".png").get();
            images = Selector.select(IMAGES_SELECTOR, imgDoc);
            List<Image> l = new ArrayList<>();
            l.add(new Image(BASEURL + images.get(0).attr("href")));
            return l;
        }
        System.out.println(images.size());
        System.out.println(images.get(skinNumber).attr("srcset"));
        return Arrays.stream(images.get(skinNumber).attr("srcset")
                .split(","))
                .map(s -> BASEURL + s.trim().split(" ")[0])
                .map(Image::new)
                .collect(Collectors.toList());
    }

    public Map<String, List<Dialog>> getPhrases() {
        Map<String, List<Dialog>> phrasesMap = new HashMap<>();

        Elements rows = Selector.select(TABLE_ROWS, doc);
        rows.remove(0);
        rows.remove(0);
        rows.remove(0);

        for (Element row : rows) {

            List<Dialog> dials = new ArrayList<>();
            List<String> stringDials = new ArrayList<>();

            List<String> audios = row.select("td:nth-child(2) > a")
                    .stream()
                    .map(a -> a.attr("href"))
                    .collect(Collectors.toList());

            String event = row.selectFirst("td:nth-child(3)").text().trim();

            Element dialogCell = row.selectFirst("td:nth-child(5)");
            String allDialogs = dialogCell.text();
            Elements moreDialogs = dialogCell.select("p");
            stringDials.add("tmp");
            for (Element dial : moreDialogs) {
                String dialText = dial.text();
                stringDials.add(dialText);
                allDialogs = allDialogs.replace(dialText, ""); // Ugh...
            }
            stringDials.set(0, allDialogs);

            for (int i = 0; i < audios.size(); i++) {
                try {
                    dials.add(new Dialog(stringDials.get(i), event, audios.get(i)));
                } catch (IndexOutOfBoundsException e) {
                    // Ignore
                }
            }

            phrasesMap.put(event, dials);
        }

        return phrasesMap;
    }
}
