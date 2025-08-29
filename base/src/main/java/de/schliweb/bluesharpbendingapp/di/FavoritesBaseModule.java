package de.schliweb.bluesharpbendingapp.di;
/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 */

import dagger.Module;
import dagger.Provides;
import de.schliweb.bluesharpbendingapp.favorites.FavoriteManager;
import de.schliweb.bluesharpbendingapp.favorites.FavoritesStorage;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

/**
 * Base Dagger module used by the base AppComponent to provide a FavoriteManager
 * without introducing platform-specific file dependencies. This uses an in-memory
 * storage that resets on process restart. Desktop and Android override this with
 * platform-specific modules (DesktopFavoritesModule, FavoritesModule) that persist data.
 */
@Module
public class FavoritesBaseModule {

    @Provides
    @Singleton
    public FavoriteManager provideFavoriteManager(ExecutorService executorService) {
        FavoritesStorage inMemory = new FavoritesStorage() {
            private String json = "";
            @Override public String read() { return json; }
            @Override public void write(String s) { json = s == null ? "" : s; }
        };
        return new FavoriteManager(inMemory, executorService);
    }
}
