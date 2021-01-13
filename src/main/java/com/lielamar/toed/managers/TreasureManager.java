package com.lielamar.toed.managers;

import com.google.inject.Inject;
import com.lielamar.lielsutils.MathUtils;
import com.lielamar.lielsutils.SpigotUtils;
import com.lielamar.toed.TreasureOfElDorado;
import com.lielamar.toed.modules.Direction;
import com.lielamar.toed.modules.Treasure;
import com.lielamar.toed.utils.Utilities;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TreasureManager {

    private final TreasureOfElDorado plugin;
    private final ConfigManager configManager;

    private final Map<UUID, Treasure> treasures;
    private final Map<UUID, BukkitTask> tasks;

    @Inject
    public TreasureManager(TreasureOfElDorado plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;

        this.treasures = new HashMap<>();
        this.tasks = new HashMap<>();
    }

    /**
     * Generates a new treasure
     *
     * @param player   Player to generate treasure for
     * @return         Generated treasure
     */
    public Treasure generateTreasure(Player player) {
        return new Treasure(getRandomOppositeLocation(player.getLocation()));
    }

    /**
     * Registers a treasure (saves it in the hashmap)
     *
     * @param treasure   Treasure to register
     * @param player     Owner of the treasure
     */
    public void registerTreasure(Treasure treasure, Player player) {
        this.treasures.put(player.getUniqueId(), treasure);
    }

    /**
     * Returns (or creates, if not found) a treasure object by a map
     *
     * @param item   Map to get the treasure of
     * @return       Matching treasure
     */
    public Treasure getTreasure(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if(nmsItem.hasTag()) {
            String uuidRaw = nmsItem.getTag().getString("uuid");
            UUID uuid = UUID.fromString(uuidRaw);

            if(this.treasures.containsKey(uuid)) {
                return this.treasures.get(uuid);
            } else {
                if(!nmsItem.getTag().hasKey("used")) {
                    Location start = Bukkit.getWorlds().get(0).getSpawnLocation();
                    double x = nmsItem.getTag().getDouble("destinationx");
                    double z = nmsItem.getTag().getDouble("destinationz");
                    Treasure treasure = new Treasure(new Location(start.getWorld(), x, 256, z));
                    this.treasures.put(uuid, treasure);
                    return treasure;
                }
            }
        }
        return null;
    }

    /**
     * Updates the treasure map
     *
     * @param holder     Player who holds the map to the treasure
     * @param treasure   Treasure to get the destination of
     */
    public void updateTreasureMap(Player holder, Treasure treasure) {
        if(treasure == null)
            return;

        MapView mapView;
        if(MathUtils.XZDistance(treasure.getDestinationLocation().getX(), holder.getLocation().getX(), treasure.getDestinationLocation().getZ(), holder.getLocation().getZ()) < 10) {
            descendTreasure(treasure);
            mapView = configManager.getImage(Direction.X);

            // Setting the map to "unusable" (only the one in their hand/the first instance)
            if(treasure.getStatus() == Treasure.TreasureStatus.DESCENDING) {
                ItemStack toedItem = Utilities.treasureOfElDoradoItem();

                if(Utilities.isSimilar(holder.getItemInHand(), toedItem)) {
                    holder.getInventory().setItemInHand(setUsedTag(holder.getItemInHand()));
                } else {
                    for(int i = 0; i < holder.getInventory().getContents().length; i++) {
                        ItemStack item = holder.getInventory().getContents()[i];

                        if(Utilities.isSimilar(item, toedItem)) {
                            holder.getInventory().setItem(i, setUsedTag(item));
                            break;
                        }
                    }
                }
            }
        } else {
            mapView = configManager.getImage(getDirectionFromLocations(holder, treasure.getDestinationLocation()));
        }

        if(Utilities.isSimilar(holder.getItemInHand(), Utilities.treasureOfElDoradoItem()))
            holder.getItemInHand().setDurability(SpigotUtils.getMapID(mapView));
    }

    private ItemStack setUsedTag(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItem.getTag() != null ? nmsItem.getTag() : new NBTTagCompound();

        tag.setBoolean("used", true);
        nmsItem.setTag(tag);

        return CraftItemStack.asCraftMirror(nmsItem);
    }

    /**
     * Starts the task that updates the player's map
     *
     * @param player     Player to update the map of
     * @param treasure   Treasure matched to the player's map
     */
    public void startTask(Player player, final Treasure treasure) {
        stopTask(player);

        tasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if(!Utilities.isSimilar(player.getItemInHand(), Utilities.treasureOfElDoradoItem())) {
                    this.cancel();
                    return;
                }

                updateTreasureMap(player, treasure);
            }
        }.runTaskTimer(plugin, 20L, 20L));
    }

    /**
     * Stops the task that updates the player's map
     *
     * @param player   Player to stop the task of
     */
    public void stopTask(Player player) {
        if(tasks.get(player.getUniqueId()) != null) {
            tasks.get(player.getUniqueId()).cancel();
            tasks.remove(player.getUniqueId());
        }
    }

    /**
     * Descending a treasure (giving it to the player)
     *
     * @param treasure   Treasure to descend
     */
    public void descendTreasure(Treasure treasure) {
        if(treasure.getStatus() != Treasure.TreasureStatus.WAITING_FOR_ARRIVAL)
            return;

        treasure.setStatus(Treasure.TreasureStatus.DESCENDING);

        if(treasure.getStatus() == Treasure.TreasureStatus.DESCENDING) {
            Location surface = treasure.getDestinationLocation().getWorld().getHighestBlockAt(treasure.getDestinationLocation()).getLocation();
            Location location = surface.clone().add(0, 40, 0);

            Chicken chicken = (Chicken)location.getWorld().spawnEntity(location, EntityType.CHICKEN);
            ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            as.setVisible(false);
            as.setHelmet(new ItemStack(Material.CHEST));
            chicken.setPassenger(as);
            chicken.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 255));

            Chicken[] chickens = new Chicken[7];
            for(int i = 0; i < 7; i++) {
                chickens[i] = (Chicken)location.getWorld().spawnEntity(location.clone().add(Utilities.random.nextInt(6)-3, 3, Utilities.random.nextInt(6)-3), EntityType.CHICKEN);
                chickens[i].setLeashHolder(chicken);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(Math.abs(chicken.getLocation().getY()-surface.getY()) < 5) {
                        for(Chicken c : chickens)
                            c.setLeashHolder(null);

                        chicken.getPassenger().remove();
                        chicken.remove();

                        surface.add(0, 1, 0).getBlock().setType(Material.CHEST);
                        Chest chest = (Chest) surface.getBlock().getState();

                        ItemStack health3 = Utilities.instantHealth3Item();
                        chest.getInventory().setItem(11, health3);
                        chest.getInventory().setItem(12, new ItemStack(Material.DIAMOND_BLOCK));
                        chest.getInventory().setItem(13, new ItemStack(Material.EXP_BOTTLE, 16));
                        chest.getInventory().setItem(14, new ItemStack(Material.GOLD_BLOCK));
                        chest.getInventory().setItem(15, health3);

                        treasure.setStatus(Treasure.TreasureStatus.RECEIVED);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 5L, 5L);
        }
    }


    /**
     * Returns a random location on map
     *
     * @param location   Location to start from
     * @return           Generated random location
     */
    private Location getRandomOppositeLocation(Location location) {
        double x, y = 256, z;
        x = (configManager.getMiddleX() + (Utilities.random.nextInt(configManager.getRadius())-(double)(configManager.getRadius()/2))) * (location.getX() >= 0 ? -1 : 1);
        z = (configManager.getMiddleZ() + (Utilities.random.nextInt(configManager.getRadius())-(double)(configManager.getRadius()/2))) * (location.getZ() >= 0 ? -1 : 1);

        return new Location(location.getWorld(), x, y, z);
    }

    /**
     * Get the direction from a player to a location
     *
     * @param player   Player to get the start location of
     * @param to       Targeted location
     * @return         Direction in which the player needs to go to
     */
    private Direction getDirectionFromLocations(Player player, Location to) {
        if(player.getWorld() != to.getWorld())
            return Direction.X;

        // A vector representing the direction to go to get to the destination from the current player's location
        Vector direction = to.clone().subtract(player.getLocation()).toVector();
        // A vector representing the location at which the player is looking
        Vector playerDirection = player.getLocation().getDirection();


        // Using the atan method to get the numeric value of the angle between the given point (x;y) [y is given first] and the origin (0;0)
        // Using this, we get the angle as radians, and we convert it to degrees.

        // Math.toDegrees is the same as (((radians / 2) / Math.PI) * 360 + 360) % 360
        double directionAngle = Math.toDegrees(Math.atan2(direction.getZ(), direction.getX()));
        double playerAngle = Math.toDegrees(Math.atan2(playerDirection.getZ(), playerDirection.getX()));

        // Calculating the final angle, in which the player needs to walk to get to the destination
        double angle = (directionAngle - playerAngle + 360) % 360;

        if(angle <= 22.5)
            return Direction.N;
        else if(angle <= 67.5)
            return Direction.NE;
        else if(angle <= 112.5)
            return Direction.E;
        else if(angle <= 157.5)
            return Direction.SE;
        else if(angle <= 202.5)
            return Direction.S;
        else if(angle <= 247.5)
            return Direction.SW;
        else if(angle <= 292.5)
            return Direction.W;
        else if(angle <= 337.5)
            return Direction.NW;
        return Direction.N;
    }
}
