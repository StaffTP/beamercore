package me.hulipvp.hcf.api.events.faction.normal;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.Faction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();

    @Getter private final Faction faction;

    FactionEvent(Faction faction) {
        this.faction = faction;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
