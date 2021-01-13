package com.lielamar.toed.modules;

import com.lielamar.toed.events.TreasureStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Treasure {

    private final Location destinationLocation;

    private TreasureStatus status;

    public Treasure(Location destinationLocation) {
        this(destinationLocation, TreasureStatus.WAITING_FOR_ARRIVAL);
    }

    public Treasure(Location destinationLocation, TreasureStatus status) {
        this.destinationLocation = destinationLocation;

        this.status = status;
    }


    public Location getDestinationLocation() {
        return this.destinationLocation;
    }

    public TreasureStatus getStatus() {
        return this.status;
    }

    public void setStatus(TreasureStatus status) {
        TreasureStatusChangeEvent event = new TreasureStatusChangeEvent(this, this.status, status);
        Bukkit.getPluginManager().callEvent(event);

        if(!event.isCancelled())
            this.status = event.getNewStatus();
    }


    public enum TreasureStatus {
        WAITING_FOR_ARRIVAL,
        DESCENDING,
        RECEIVED
    }
}
