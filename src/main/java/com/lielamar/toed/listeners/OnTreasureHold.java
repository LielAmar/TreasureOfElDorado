package com.lielamar.toed.listeners;

import com.lielamar.toed.TreasureOfElDorado;
import com.lielamar.toed.utils.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class OnTreasureHold implements Listener {

    private final TreasureOfElDorado plugin;

    public OnTreasureHold(TreasureOfElDorado plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMapHold(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());

        if(previousItem != null) {
            if(Utilities.isSimilar(previousItem, Utilities.treasureOfElDoradoItem()))
                plugin.getTreasureManager().stopTask(player);
        }

        if(newItem != null) {
            if(Utilities.isSimilar(newItem, Utilities.treasureOfElDoradoItem()))
                plugin.getTreasureManager().startTask(player, plugin.getTreasureManager().getTreasure(newItem));
        }
    }
}
