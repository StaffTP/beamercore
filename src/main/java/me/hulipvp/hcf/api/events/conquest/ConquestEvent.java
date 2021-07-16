package me.hulipvp.hcf.api.events.conquest;

import lombok.Getter;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ConquestEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final Conquest conquest;

    ConquestEvent(Conquest conquest) {
        this.conquest = conquest;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
