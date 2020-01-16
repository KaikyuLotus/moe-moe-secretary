package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arknights extends IWaifuAdapter {

    private static final String BASE_URL = "http://en.rhinelab.org";

    private static final String SD_IMG_SELECTOR = ".gallerybox a";
    private static final String HD_IMG_SELECTOR = ".fullImageLink a";

    public Arknights(String name) {
        super(name);
    }

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

}
