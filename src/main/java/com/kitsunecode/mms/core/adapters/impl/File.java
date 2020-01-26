package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.annotations.Adapter;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.entities.WaifuData;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Adapter
public class File extends IWaifuAdapter {

    public File(String shipName) throws IOException, StartFailedException {
        super(shipName);
    }

    @Override
    protected WaifuData loadFromCustomSource() {
        throw new StartFailedException("File adapter needs a configuration folder, please click here to open the guide!",
                "https://telegra.ph/Moe-Moe-Secretary-File-Adapter-Configuration-01-12");
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return data.getDialogs().stream()
                .filter(d -> d.getEvent().equalsIgnoreCase(event))
                .collect(Collectors.toList());
    }

}
