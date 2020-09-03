package com.kitsunecode.mms.core.entities.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Audio {

    private boolean isPlaying = false;

    private File file;

    private int volume;

    private List<Runnable> closeActions = new ArrayList<>();

    public Audio(File file, int volume) {
        this.file = file;
        this.volume = volume;
    }

    public File getFile() {
        return file;
    }

    public int getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    protected void start() {
        isPlaying = true;
        onStart();
    }

    protected void finish() {
        isPlaying = false;
        onFinish();
        closeActions.forEach(Runnable::run);
    }

    public void addCloseAction(Runnable runnable) {
        closeActions.add(runnable);
    }

    public void stop() {
        isPlaying = false;
    }

    public void onStart() {
        // Overrideable
    }

    public void onFinish() {
        // Overrideable
    }

}
