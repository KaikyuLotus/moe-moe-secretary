package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;

import java.util.List;
import java.util.stream.Collectors;

public class File extends IWaifuAdapter {

    public File(String shipName) throws StartFailedException {
        super(shipName);
    }

    @Override
    protected WaifuData loadFromCustomSource() {
        throw new StartFailedException("File adapter needs a configuration folder, please click here to open the guide!",
                "https://telegra.ph/Moe-Moe-Secretary-File-Adapter-Configuration-01-12");
    }

    @Override
    public String onTouchEventKey() {
        return "OnTouch";
    }

    @Override
    public String onIdleEventKey() {
        return "OnIdle";
    }

    @Override
    public String onLoginEventKey() {
        return "OnLogin";
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return data.getDialogs().stream()
                .filter(d -> d.getEvent().equalsIgnoreCase(event))
                .collect(Collectors.toList());
    }

}
