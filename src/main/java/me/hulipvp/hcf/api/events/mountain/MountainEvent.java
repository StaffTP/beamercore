package me.hulipvp.hcf.api.events.mountain;

import lombok.Getter;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MountainEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final Mountain mountain;

    MountainEvent(Mountain mountain) {
        this.mountain = mountain;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
