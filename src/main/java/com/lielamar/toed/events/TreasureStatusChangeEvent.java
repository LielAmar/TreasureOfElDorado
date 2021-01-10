package com.lielamar.toed.events;

import com.lielamar.toed.modules.Treasure;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TreasureStatusChangeEvent extends Event implements Cancellable {

    /**
     * This event is fired whenever a player's treasure changes status
     */

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Treasure treasure;
    private final Treasure.TreasureStatus oldStatus;
    private Treasure.TreasureStatus newStatus;
    private boolean cancelled;

    public TreasureStatusChangeEvent(Treasure treasure, Treasure.TreasureStatus oldStatus, Treasure.TreasureStatus newStatus) {
        this.treasure = treasure;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.cancelled = false;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public Treasure getTreasure() {
        return this.treasure;
    }

    public Treasure.TreasureStatus getOldStatus() {
        return this.oldStatus;
    }

    public Treasure.TreasureStatus getNewStatus() {
        return this.newStatus;
    }

    public void setNewStatus(Treasure.TreasureStatus newStatus) {
        this.newStatus = newStatus;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
