package me.hulipvp.hcf.api.events.faction.player;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerFactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final PlayerFaction faction;

    PlayerFactionEvent(PlayerFaction faction) {
        this.faction = faction;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
