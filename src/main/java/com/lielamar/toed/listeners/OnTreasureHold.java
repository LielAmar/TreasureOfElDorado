package com.lielamar.toed.listeners;

import com.google.inject.Inject;
import com.lielamar.toed.managers.TreasureManager;
import com.lielamar.toed.utils.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class OnTreasureHold implements Listener {

    private final TreasureManager treasureManager;

    @Inject
    public OnTreasureHold(TreasureManager treasureManager) {
        this.treasureManager = treasureManager;
    }

    @EventHandler
    public void onMapHold(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());

        if(previousItem != null) {
            if(Utilities.isSimilar(previousItem, Utilities.treasureOfElDoradoItem()))
                treasureManager.stopTask(player);
        }

        if(newItem != null) {
            if(Utilities.isSimilar(newItem, Utilities.treasureOfElDoradoItem()))
                treasureManager.startTask(player, treasureManager.getTreasure(newItem));
        }
    }
}
