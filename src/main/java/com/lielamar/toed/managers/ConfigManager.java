package com.lielamar.toed.managers;

import com.lielamar.lielsutils.files.FileManager;
import com.lielamar.toed.TreasureOfElDorado;
import com.lielamar.toed.modules.Direction;
import com.lielamar.toed.utils.ImageRenderer;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final TreasureOfElDorado plugin;
    private final Map<Direction, MapView> directionImages;

    private final int requiredHP;
    private final double middleX, middleZ;
    private final int radius;

    public ConfigManager(TreasureOfElDorado plugin) {
        this.plugin = plugin;
        this.directionImages = new HashMap<>();

        this.requiredHP = plugin.getConfig().getInt("Required HP");
        this.middleX = plugin.getConfig().getDouble("Middle Location.x");
        this.middleZ = plugin.getConfig().getDouble("Middle Location.z");
        this.radius = plugin.getConfig().getInt("Treasure Radius");

        loadDirections();
    }

    /**
     * Loads all of the direction images and puts them in a hash map for repetitive use
     */
    @SuppressWarnings("deprecation")
    private void loadDirections() {
        FileManager.Config config = plugin.getFileManager().getConfig("maps.yml");

        final MapView[] maps = new MapView[9];

        // Loading all maps (set ids to prevent map overflow)
        if(config.getConfig().contains("ids")) {
            int i = 0;
            for(int id : config.getConfig().getIntegerList("ids"))
                maps[i++] = Bukkit.getMap((short)id);
        } else {
            for(short i = 0; i < 9; i++)
                maps[i] = Bukkit.createMap(Bukkit.getWorlds().get(0));

            int[] ids = new int[9];
            for(int i = 0; i < ids.length; i++)
                ids[i] = maps[i].getId();

            config.getConfig().set("ids", ids);
            config.save();
        }

        try {
            int i = 0;
            for(Direction direction : Direction.values()) {
                MapView mapView = maps[i++];
                ImageRenderer imageRenderer = new ImageRenderer(plugin.getResource( "directions/" + direction.name() + ".png"));
                mapView.addRenderer(imageRenderer);

                directionImages.put(direction, mapView);
            }
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public MapView getImage(Direction direction) {
        return directionImages.get(direction);
    }

    public int getRequiredHP() {
        return requiredHP;
    }

    public double getMiddleX() {
        return middleX;
    }

    public double getMiddleZ() {
        return middleZ;
    }

    public int getRadius() {
        return radius;
    }
}
