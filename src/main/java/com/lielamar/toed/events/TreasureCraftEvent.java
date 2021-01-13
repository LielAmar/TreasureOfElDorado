package com.lielamar.toed.events;

import com.lielamar.toed.modules.Treasure;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TreasureCraftEvent extends Event implements Cancellable {

    /**
     * This event is fired whenever a player crafts the Treasure of El Dorado item
     */

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Treasure treasure;
    private boolean cancelled;

    public TreasureCraftEvent(Treasure treasure) {
        this.treasure = treasure;
        this.cancelled = false;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setTreasure(Treasure treasure) {
        this.treasure = treasure;
    }

    public Treasure getTreasure() {
        return this.treasure;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
