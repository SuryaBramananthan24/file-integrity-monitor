package com.devops.file_integrity_monitor.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FileSystemMonitor {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemMonitor.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private WatchService watchService;

    public void start(Path directory) throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE);
        executor.submit(() -> watch(directory));
        logger.info("Filesystem monitoring started for: {}",directory.toAbsolutePath());
    }

    private void watch(Path directory) {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watchService.take();
            }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (ClosedWatchServiceException exception) {
                break;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                processEvent(directory, event);
            }
            boolean valid = key.reset();
            if (!valid) {
                logger.warn("Watch key is no longer valid for: {}",directory);
                break;
            }
        }
    }

    private void processEvent(Path directory,WatchEvent<?> event) {
        WatchEvent.Kind<?> kind = event.kind();
        if (kind == StandardWatchEventKinds.OVERFLOW) {
            logger.warn("Filesystem event overflow detected for: {}",directory);
            return;
        }

        Path relativePath = (Path) event.context();
        Path affectedPath = directory.resolve(relativePath);
        FileSystemEventType eventType = mapEventType(kind);
        FileSystemEvent fileSystemEvent = new FileSystemEvent("filesystem-agent-local",affectedPath.toAbsolutePath().normalize().toString(),eventType,Instant.now());
        logger.info("Filesystem event detected: {}", fileSystemEvent);
    }

    private FileSystemEventType mapEventType(WatchEvent.Kind<?> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            return FileSystemEventType.CREATED;
        }
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            return FileSystemEventType.DELETED;
        }
        return FileSystemEventType.MODIFIED;
    }

    public void stop() {
        executor.shutdownNow();
        if (watchService != null) {
            try {
                watchService.close();
            }
            catch (IOException exception) {
                logger.warn("Failed to close filesystem watch service",exception);
            }
        }
        logger.info("Filesystem monitoring stopped");
    }
}