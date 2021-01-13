package com.lielamar.toed;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lielamar.lielsutils.files.FileManager;

public class BinderModule extends AbstractModule {

    private final TreasureOfElDorado plugin;
    private final FileManager fileManager;

    public BinderModule(TreasureOfElDorado plugin) {
        this.plugin = plugin;
        this.fileManager = new FileManager(this.plugin);
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(TreasureOfElDorado.class).toInstance(this.plugin);
        this.bind(FileManager.class).toInstance(this.fileManager);
    }
}
