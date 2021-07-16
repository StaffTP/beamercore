package me.hulipvp.hcf.game.faction.type.event;

import lombok.Getter;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.UUID;

public class KothFaction extends SystemFaction {

    @Getter private final Koth koth;

    public KothFaction(UUID uuid, String name, Koth koth) {
        super(uuid, name, true, FactionType.KOTH, ChatColor.BLUE);

        this.koth = koth;
    }

    @Override
    public String getColoredName() {
        return this.getColor() + this.getName();
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("koth", getName());
    }
}
