package me.hulipvp.hcf.game.faction.type.system;

import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.utils.ConfigValues;
import org.bukkit.ChatColor;

import java.util.UUID;

public class WarzoneFaction extends SystemFaction {

    public WarzoneFaction(UUID uuid) {
        super(uuid, "Warzone", FactionType.WARZONE, ChatColor.RED);
    }

    @Override
    public String getHomeString() {
        if(ConfigValues.FACTIONS_SIZE_WARZONE > 0)
            return ConfigValues.FACTIONS_SIZE_WARZONE + " | " + ConfigValues.FACTIONS_SIZE_WARZONE;

        return super.getHomeString();
    }
}
