package me.hulipvp.hcf.game.faction.type.event;

import lombok.Getter;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.UUID;

public class ConquestFaction extends SystemFaction {

    @Getter private final Conquest conquest;

    public ConquestFaction(UUID uuid, String name, Conquest conquest) {
        super(uuid, name, true, FactionType.CONQUEST, ChatColor.YELLOW);

        this.conquest = conquest;
    }

    @Override
    public String getColoredName() {
        return this.getColor() + "Conquest";
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("conquest", this.getConquest().getName());
    }
}
