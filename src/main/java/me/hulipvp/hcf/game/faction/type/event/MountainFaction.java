package me.hulipvp.hcf.game.faction.type.event;

import lombok.Getter;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.UUID;

public class MountainFaction extends SystemFaction {
    
    @Getter private final Mountain mountain;

    public MountainFaction(UUID uuid, String name, Mountain mountain) {
        super(uuid, name, true, FactionType.MOUNTAIN, ChatColor.YELLOW);

        this.mountain = mountain;
    }

    @Override
    public String getColoredName() {
        return this.getColor() + this.getName() + ChatColor.GOLD + " Mountain";
    }

    @Override
    public Document toDocument() {
        if(mountain == null)
            return super.toDocument();

        return super.toDocument().append("mountain", this.getMountain().getName());
    }
}
