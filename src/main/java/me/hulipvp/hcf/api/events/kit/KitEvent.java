package me.hulipvp.hcf.api.events.kit;

import lombok.Getter;
import me.hulipvp.hcf.game.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter private final Player player;
    @Getter private final Kit kit;

    KitEvent(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
