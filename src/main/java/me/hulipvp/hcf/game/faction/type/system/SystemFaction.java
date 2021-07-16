package me.hulipvp.hcf.game.faction.type.system;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.UUID;

public class SystemFaction extends Faction {

    @Getter @Setter private ChatColor color;

    public SystemFaction(UUID uuid, String name, boolean deathban, FactionType type, ChatColor color) {
        super(uuid, name, deathban, type);

        this.color = color;
    }

    public SystemFaction(UUID uuid, String name, FactionType type, ChatColor color) {
        this(uuid, name, true, type, color);
    }

    public SystemFaction(UUID uuid, String name) {
        this(uuid, name, true, FactionType.SYSTEM, ChatColor.WHITE);
    }

    public String getColoredName() {
        return this.getColor() + this.getName();
    }

    @Override
    public String getDisplayString() {
        return C.color(this.getColoredName() + " " + ((this.isDeathban()) ? Locale.FACTION_DEATHBAN.toString() : Locale.FACTION_NONDEATHBAN.toString()));
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("color", StringUtils.chatColorToString(this.getColor()));
    }
}
