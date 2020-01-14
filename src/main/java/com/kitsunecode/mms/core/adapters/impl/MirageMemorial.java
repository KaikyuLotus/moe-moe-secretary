package com.kitsunecode.mms.core.adapters.impl;


import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class MirageMemorial extends IWaifuAdapter {

    private static final String BASE_URL = "https://miragememorialglobal.fandom.com/wiki/";
    private static final String PAGES = ".wikia-paginator .paginator-page:not(.active)";

    private static final String IMAGE_SELECTOR = "#%s-png img";

    public MirageMemorial(String name) {
        super(name);
    }

    /**
     * Loads data from Wiki, we MUST use it only once in a while
     */
    @Override
    protected WaifuData loadFromCustomSource() throws IOException {
        System.out.println("Getting servant from wiki");
        Document skinsDoc = Jsoup.connect(BASE_URL + "Special:Images?file=" + name + ".png").get();
        return new WaifuData(Collections.emptyList(), loadSkinUrls(skinsDoc));
    }

    private List<String> loadSkinUrls(Document doc) throws IOException {

        String url = getImageUrlFromDocument(doc);
        if (url != null) return Collections.singletonList(url);

        // Iterate all pages but this one
        Elements pages = Selector.select(PAGES, doc);
        for (Element pageBtn : pages) {
            String pageUrl = pageBtn.attr("href");
            String image = getImageUrlFromDocument(Jsoup.connect(pageUrl).get());
            if (image != null) return Collections.singletonList(image);
        }

        throw new StartFailedException("Cannot find servant named " + name);
    }

    private String getImageUrlFromDocument(Document doc) {
        Element image = Selector.select(String.format(IMAGE_SELECTOR, name), doc).first();
        if (image == null) {
            return null;
        }
        return image.attr("src").split("\\.png")[0] + ".png";
    }

    @Override
    public String onTouchEventKey() {
        return "onClick";
    }

    @Override
    public String onIdleEventKey() {
        return "onIdle";
    }

    @Override
    public String onLoginEventKey() {
        return "onLogin";
    }

}
