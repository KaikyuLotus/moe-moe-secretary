package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.utils.Util;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

@Adapter
public class Github extends IWaifuAdapter {

    private String folderUrl;

    public Github(String name) throws IOException, URISyntaxException {
        super(name);
        folderUrl = createWaifuFolderUrl();
    }

    private String createWaifuFolderUrl() throws URISyntaxException {
        String repo = Settings.getGithubRepo();
        String branch = Settings.getGithubBranch();
        if (repo == null || branch == null) {
            throw new StartFailedException("github.url or github.branch are not been set in the configuration file");
        }
        return new URIBuilder("https://raw.githubusercontent.com/")
                .setPath(repo + "/" + branch + "/" + getName())
                .build()
                .normalize()
                .toString();
    }

    @Override
    public String getSkin(int skinNumber) {
        return folderUrl + "/" + data.getSkins().get(skinNumber);
    }

    @Override
    protected WaifuData loadFromCustomSource() throws Exception {
        folderUrl = createWaifuFolderUrl();
        WaifuData data = Util.deserializeWaifu(Util.downloadString(folderUrl + "/data.yaml"), "YAML");
        data.getDialogs()
                .stream()
                .filter((d) -> d.getAudio() != null)
                .forEach((d) -> d.setAudio(folderUrl + "/" + d.getAudio()));
        return data;
    }
}
