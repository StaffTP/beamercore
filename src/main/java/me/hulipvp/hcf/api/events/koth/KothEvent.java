package me.hulipvp.hcf.api.events.koth;

import lombok.Getter;
import me.hulipvp.hcf.game.event.koth.Koth;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class KothEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter private final Koth koth;

    KothEvent(Koth koth) {
        this.koth = koth;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
