package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class File implements IWaifuAdapter {

    private WaifuData data;
    private String name;
    private long startTimeMillis;

    public File(String shipName) throws StartFailedException {

        startTimeMillis = System.currentTimeMillis();
        name = shipName;

        try {
            if (IWaifuAdapter.hasSavedFile(this)) {
                data = IWaifuAdapter.getDataFromFile(this);
            }
        } catch (IOException e) {
            throw new StartFailedException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @Override
    public int getSkinCount() {
        return data.getSkins().size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShowableName() {
        return this.name;
    }

    @Override
    public String getSkin(int skinNumber) {
        return data.getSkins().get(skinNumber);
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
    public List<Dialog> getDialogs() {
        return data.getDialogs();
    }

    @Override
    public List<Dialog> getDialogs(String event) {
        return data.getDialogs().stream()
                .filter(d -> d.getEvent().equalsIgnoreCase(event))
                .collect(Collectors.toList());
    }

    @Override
    public WaifuData getWaifuData() {
        return data;
    }

    @Override
    public long getUptime() {
        return System.currentTimeMillis() - startTimeMillis;
    }
}
