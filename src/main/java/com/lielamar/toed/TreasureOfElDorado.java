package com.lielamar.toed;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.lielamar.lielsutils.files.FileManager;
import com.lielamar.toed.listeners.*;
import com.lielamar.toed.managers.ConfigManager;
import com.lielamar.toed.managers.TreasureManager;
import com.lielamar.toed.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TreasureOfElDorado extends JavaPlugin {

    @Inject private OnTreasureCraft onTreasureCraft;
    @Inject private OnTreasureHold onTreasureHold;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupBinderModule();

        registerRecipes();
        registerListeners();
    }

    private void setupBinderModule() {
        BinderModule module = new BinderModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        ConfigManager configManager = new ConfigManager(this, injector.getInstance(FileManager.class));
        TreasureManager treasureManager = new TreasureManager(this, configManager);

        injector.injectMembers(configManager);
        injector.injectMembers(treasureManager);
    }

    private void registerRecipes() {
        Bukkit.addRecipe(Utilities.treasureOfElDoradoRecipe());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this.onTreasureCraft, this);
        pm.registerEvents(this.onTreasureHold, this);
    }
}