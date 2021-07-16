package me.hulipvp.hcf.api.events.kit;

import me.hulipvp.hcf.game.kits.Kit;
import org.bukkit.entity.Player;

public class KitEnableEvent extends KitEvent {

    public KitEnableEvent(Player player, Kit kit) {
        super(player, kit);
    }
}
