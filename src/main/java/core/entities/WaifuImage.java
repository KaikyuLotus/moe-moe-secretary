package core.entities;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WaifuImage {
    private String url;

    public WaifuImage(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public byte[] download() throws IOException {
        System.out.println("Downloading: " + url);
        try(InputStream in = new URL(url).openStream()) {
            return IOUtils.toByteArray(in);
        }
    }
}
