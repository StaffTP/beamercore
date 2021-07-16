package me.hulipvp.hcf.game.faction.type.event;

import lombok.Getter;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.UUID;

public class DTCFaction extends SystemFaction {

    @Getter private final DTC dtc;

    public DTCFaction(UUID uuid, String name, DTC dtc) {
        super(uuid, name, true, FactionType.DTC, ChatColor.DARK_GREEN);

        this.dtc = dtc;
    }

    @Override
    public String getColoredName() {
        return this.getColor() + "DTC";
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("dtc", this.getDtc().getName());
    }
}
