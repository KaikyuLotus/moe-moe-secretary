package azurlane.entities;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Image {
    private String url;

    public Image(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public byte[] download() throws IOException {
        try(InputStream in = new URL(url).openStream()) {
            return IOUtils.toByteArray(in);
        }
    }
}
