package me.hulipvp.hcf.game.faction.type.system;

import me.hulipvp.hcf.game.faction.type.FactionType;
import org.bukkit.ChatColor;

import java.util.UUID;

public class RoadFaction extends SystemFaction {

    public RoadFaction(UUID uuid, String name) {
        super(uuid, name, FactionType.ROAD, ChatColor.GOLD);
    }

    @Override
    public String getColoredName() {
        return this.getColor() + this.getName() + " Road";
    }
}
