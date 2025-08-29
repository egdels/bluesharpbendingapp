package de.schliweb.bluesharpbendingapp.favorites;
/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic unit tests for the platform-neutral FavoriteManager in base module.
 */
public class FavoriteManagerBaseTest {

    static class InlineExecutorService extends AbstractExecutorService {
        private volatile boolean shutdown = false;

        @Override
        public void shutdown() {
            shutdown = true;
        }

        @Override
        public List<Runnable> shutdownNow() {
            shutdown = true;
            return Collections.emptyList();
        }

        @Override
        public boolean isShutdown() {
            return shutdown;
        }

        @Override
        public boolean isTerminated() {
            return shutdown;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return true;
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    static class TempFileStorage implements FavoritesStorage {
        private final Path file;

        TempFileStorage(Path file) {
            this.file = file;
        }

        @Override
        public String read() {
            try {
                return Files.exists(file) ? Files.readString(file) : "";
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        public void write(String json) {
            try {
                Files.writeString(file, json == null ? "" : json);
            } catch (IOException ignored) {
            }
        }
    }

    private Path tempFile;
    private FavoriteManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("favorites", ".json");
        ExecutorService executor = new InlineExecutorService();
        manager = new FavoriteManager(new TempFileStorage(tempFile), executor);
        // ensure clean state
        for (Favorite f : manager.load()) {
            manager.remove(f.id);
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testToggleCreateAndRemove() {
        assertTrue(manager.load().isEmpty());
        Favorite f = manager.toggle("RICHTER", null, "C", 10, null);
        assertNotNull(f);
        List<Favorite> list = manager.load();
        assertEquals(1, list.size());
        assertTrue(manager.isCurrentFavorite("RICHTER", null, "C", 10));

        Favorite removed = manager.toggle("RICHTER", null, "C", 10, null);
        assertNull(removed);
        assertFalse(manager.isCurrentFavorite("RICHTER", null, "C", 10));
        assertTrue(manager.load().isEmpty());
    }

    @Test
    public void testRenameAndReorder() {
        Favorite a = manager.toggle("RICHTER", null, "C", 10, null);
        Favorite b = manager.toggle("RICHTER", null, "G", 10, null);
        assertNotNull(a);
        assertNotNull(b);
        manager.rename(a.id, "Richter • C");
        Favorite aLoaded = manager.load().stream().filter(x -> x.id.equals(a.id)).findFirst().orElse(null);
        assertNotNull(aLoaded);
        assertEquals("Richter • C", aLoaded.label);
        manager.reorder(List.of(b, a));
        List<Favorite> ordered = manager.load();
        assertEquals(b.id, ordered.get(0).id);
        assertEquals(a.id, ordered.get(1).id);
    }
}
