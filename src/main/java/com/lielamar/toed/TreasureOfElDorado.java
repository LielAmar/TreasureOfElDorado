package com.lielamar.toed;

import com.lielamar.lielsutils.files.FileManager;
import com.lielamar.toed.listeners.*;
import com.lielamar.toed.managers.ConfigManager;
import com.lielamar.toed.managers.TreasureManager;
import com.lielamar.toed.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TreasureOfElDorado extends JavaPlugin {

    private FileManager fileManager;
    private ConfigManager configManager;
    private TreasureManager treasureManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setupCustomRecipes();
        setupToED();
        registerListeners();
    }

    private void setupToED() {
        this.fileManager = new FileManager(this);
        this.configManager = new ConfigManager(this);
        this.treasureManager = new TreasureManager(this);
    }

    private void setupCustomRecipes() {
        Bukkit.addRecipe(Utilities.treasureOfElDoradoRecipe());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new OnTreasureCraft(this), this);
        pm.registerEvents(new OnTreasureHold(this), this);
    }


    public FileManager getFileManager() { return this.fileManager; }
    public ConfigManager getConfigManager() { return this.configManager; }
    public TreasureManager getTreasureManager() { return this.treasureManager; }
}