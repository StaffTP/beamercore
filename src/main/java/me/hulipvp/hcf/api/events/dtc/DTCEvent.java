package me.hulipvp.hcf.api.events.dtc;

import lombok.Getter;
import me.hulipvp.hcf.game.event.dtc.DTC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class DTCEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final DTC dtc;

    DTCEvent(DTC dtc) {
        this.dtc = dtc;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
