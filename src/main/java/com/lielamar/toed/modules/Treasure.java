package com.lielamar.toed.modules;

import com.lielamar.toed.events.TreasureStatusChangeEvent;
import com.lielamar.toed.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class Treasure {

    private final Plugin plugin;
    private final Location destinationLocation;

    private TreasureStatus status;

    public Treasure(Plugin plugin, Location destinationLocation) {
        this(plugin, destinationLocation, TreasureStatus.WAITING_FOR_ARRIVAL);
    }

    public Treasure(Plugin plugin, Location destinationLocation, TreasureStatus status) {
        this.plugin = plugin;
        this.destinationLocation = destinationLocation;

        this.status = status;
    }

    public void setStatus(TreasureStatus status) {
        TreasureStatusChangeEvent event = new TreasureStatusChangeEvent(this, this.status, status);
        Bukkit.getPluginManager().callEvent(event);

        if(!event.isCancelled())
            this.status = event.getNewStatus();
    }

    public TreasureStatus getStatus() {
        return this.status;
    }

    public Location getDestinationLocation() {
        return this.destinationLocation;
    }

    public void descend() {
        if(this.status != TreasureStatus.WAITING_FOR_ARRIVAL)
            return;

        setStatus(TreasureStatus.DESCENDING);

        if(this.status == TreasureStatus.DESCENDING) {
            Location surface = destinationLocation.getWorld().getHighestBlockAt(destinationLocation).getLocation();
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

                        ItemStack health3 = instantHealth3();
                        chest.getInventory().setItem(11, health3);
                        chest.getInventory().setItem(12, new ItemStack(Material.DIAMOND_BLOCK));
                        chest.getInventory().setItem(13, new ItemStack(Material.EXP_BOTTLE, 16));
                        chest.getInventory().setItem(14, new ItemStack(Material.GOLD_BLOCK));
                        chest.getInventory().setItem(15, health3);

                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 5L, 5L);
        }
    }

    private ItemStack instantHealth3() {
        ItemStack item = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Splash Potion of Healing");
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2), true);
        item.setItemMeta(meta);

        Potion pot = new Potion(1);
//        pot.setType(PotionType.INSTANT_HEAL);
//        pot.setLevel(2);
        pot.setSplash(true);
        pot.apply(item);

        return item;
    }

    public enum TreasureStatus {
        WAITING_FOR_ARRIVAL,
        DESCENDING,
        RECEIVED
    }
}
