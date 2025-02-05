package online.andrew2007.mythic.config;

import online.andrew2007.mythic.MythicWorldTweaks;

import java.io.IOException;
import java.nio.file.*;

public class ConfigFileListener {
    private static ListenerThread listenerThread = null;
    @SuppressWarnings("unchecked")
    private static class ListenerThread extends Thread {
        boolean shouldRun = true;
        long lastTriggeredTime = 0L;
        public ListenerThread() {
            super("Config File Listener");
        }
        @Override
        public void run() {
            Path configDir = Paths.get(System.getProperty("user.dir") + "/config/" + MythicWorldTweaks.MOD_ID);
            try(WatchService watchService = FileSystems.getDefault().newWatchService()) {
                configDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (shouldRun) {
                    try {
                        WatchKey watchKey = watchService.take();
                        for (WatchEvent<?> event : watchKey.pollEvents()) {
                            WatchEvent.Kind<?> watchEventKind = event.kind();
                            if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                WatchEvent<Path> modifyEvent = (WatchEvent<Path>) event;
                                Path fileName = modifyEvent.context();
                                if (!(lastTriggeredTime + 600L >= System.currentTimeMillis())) {
                                    lastTriggeredTime = System.currentTimeMillis();
                                    onEventTriggered(fileName);
                                }
                            }
                        }
                        if (!watchKey.reset()) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        if (!shouldRun) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void initiateListener() {
        if (listenerThread == null) {
            listenerThread = new ListenerThread();
            listenerThread.start();
            MythicWorldTweaks.LOGGER.info("Config listener started!");
        } else {
            throw new IllegalStateException("A listener thread has already been running.");
        }
    }
    public static void stopListener() {
        if (listenerThread != null) {
            listenerThread.shouldRun = false;
            listenerThread.interrupt();
            listenerThread = null;
            MythicWorldTweaks.LOGGER.info("Config listener stopped!");
        } else {
            throw new IllegalStateException("No listener thread is running.");
        }
    }
    private static void onEventTriggered(Path fileName) {
        String stringFileName = fileName.toString();
        if (stringFileName.startsWith("config.json")) {
            ConfigLoader.onConfigHotUpdate();
        }
    }
}
