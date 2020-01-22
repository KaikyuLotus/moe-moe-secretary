package com.kitsunecode.mms.core.adapters.impl.adapterentities.arknights.skin;

public class Skin {

    private static final String SKIN_URL = "https://raw.githubusercontent.com/Aceship/AN-EN-Tags/master/img/characters/%s.png";

    private String skinId;
    private String charId;

    public String getSkinId() {
        return skinId;
    }

    public String getCharId() {
        return charId;
    }

    public String composeUrl() {
        String url = String.format(SKIN_URL, skinId);
        if (url.contains("@")) {
            url = url.replace("@", "_").replace("#", "%23");
        } else {
            url = url.replace("#", "_");
        }
        return url;
    }
}
