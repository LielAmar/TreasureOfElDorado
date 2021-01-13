package com.lielamar.toed.listeners;

import com.google.inject.Inject;
import com.lielamar.toed.events.TreasureCraftEvent;
import com.lielamar.toed.managers.ConfigManager;
import com.lielamar.toed.managers.TreasureManager;
import com.lielamar.toed.modules.Treasure;
import com.lielamar.toed.utils.Utilities;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class OnTreasureCraft implements Listener {

    private final ConfigManager configManager;
    private final TreasureManager treasureManager;

    @Inject
    public OnTreasureCraft(ConfigManager configManager, TreasureManager treasureManager) {
        this.configManager = configManager;
        this.treasureManager = treasureManager;
    }

    @EventHandler
    public void onCraft(InventoryClickEvent event) {
        if(event.getClickedInventory() == null || event.getCurrentItem() == null)
            return;

        if(event.getClickedInventory().getType() != InventoryType.WORKBENCH)
            return;

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        /*
         * Whenever a player is trying to craft the Treasure of El Dorado, we check if
         * their health is lower than 10hp (5 hearts). If so, we generate a new treasure,
         * call the craft event and then register the treasure, giving the player a map to it
         */
        if(event.getSlotType() == InventoryType.SlotType.RESULT) {
            if(Utilities.isSimilar(event.getCurrentItem(), Utilities.treasureOfElDoradoCraftItem())) {
                if(event.getWhoClicked().getHealth() > configManager.getRequiredHP()) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "This item can only be crafted below 5 hearts!");
                } else {
                    Treasure treasure = treasureManager.generateTreasure((Player)event.getWhoClicked());
                    TreasureCraftEvent craftEvent = new TreasureCraftEvent(treasure);
                    Bukkit.getPluginManager().callEvent(craftEvent);

                    if(craftEvent.isCancelled()) return;

                    treasure = craftEvent.getTreasure();
                    treasureManager.registerTreasure(treasure, (Player)event.getWhoClicked());

                    ItemStack item = Utilities.treasureOfElDoradoItem();

                    net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
                    NBTTagCompound tag = nmsItem.getTag() != null ? nmsItem.getTag() : new NBTTagCompound();
                    tag.setString("uuid", event.getWhoClicked().getUniqueId().toString());
                    tag.setDouble("destinationx", treasure.getDestinationLocation().getX());
                    tag.setDouble("destinationz", treasure.getDestinationLocation().getZ());
                    nmsItem.setTag(tag);

                    item = CraftItemStack.asCraftMirror(nmsItem);

                    event.setCurrentItem(item);
                }
            }
        }
    }
}
