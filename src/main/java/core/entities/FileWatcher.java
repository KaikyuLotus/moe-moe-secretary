package core.entities;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class FileWatcher {

    private final Runnable runnable;
    private final Path dir;

    private boolean running = true;

    public FileWatcher(Path dir, Runnable runnable) {
        this.dir = dir;
        this.runnable = runnable;
    }

    public void stop() {
        running = false;
    }

    public void watch() {
        new Thread(() -> {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                if (dir.toFile().isFile()) {
                    dir.getParent().register(watcher, ENTRY_MODIFY);
                } else {
                    dir.register(watcher, ENTRY_MODIFY);
                }

                while (running) {
                    WatchKey key;

                    try {
                        key = watcher.poll(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        return;
                    }

                    if (key == null) {
                        Thread.yield();
                        continue;
                    }

                    for (WatchEvent<?> evt : key.pollEvents()) {

                        Path filename = (Path) evt.context();

                        if (evt.kind() == OVERFLOW || !filename.toString().equals(dir.toFile().getName())) {
                            Thread.yield();
                            continue;
                        }

                        runnable.run();
                    }

                    if (!key.reset()) {
                        break;
                    }

                    Thread.yield();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }).start();
    }

}
