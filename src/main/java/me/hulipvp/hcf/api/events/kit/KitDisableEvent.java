package me.hulipvp.hcf.api.events.kit;

import me.hulipvp.hcf.game.kits.Kit;
import org.bukkit.entity.Player;

public class KitDisableEvent extends KitEvent {

    public KitDisableEvent(Player player, Kit kit) {
        super(player, kit);
    }
}