package me.hulipvp.hcf.game.faction.type.system;

import me.hulipvp.hcf.game.faction.type.FactionType;
import org.bukkit.ChatColor;

import java.util.UUID;

public class SafezoneFaction extends SystemFaction {

    public SafezoneFaction(UUID uuid, String name) {
        super(uuid, name, false, FactionType.SAFEZONE, ChatColor.GREEN);
    }
}
