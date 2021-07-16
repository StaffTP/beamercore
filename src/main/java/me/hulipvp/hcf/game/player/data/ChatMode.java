package me.hulipvp.hcf.game.player.data;

import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public enum ChatMode {

    PUBLIC(ChatColor.GREEN, "p", "public", "g", "global"),
    FACTION(ChatColor.DARK_GREEN, "f", "faction", "fac", "t", "team"),
    ALLY(ChatColor.LIGHT_PURPLE, "a", "ally", "allies"),
    CAPTAIN(ChatColor.AQUA, "c", "cap", "captain", "o", "officer");

    @Getter private final ChatColor color;
    @Getter private final List<String> aliases;

    ChatMode(ChatColor color, String... aliases) {
        this.color = color;
        this.aliases = Arrays.asList(aliases);
    }

    public String getDisplay() {
        return this.name().toLowerCase();
    }

    public String getPrefix() {
        return this.getColor() + "(" + (this == FACTION ? "Team" : WordUtils.capitalizeFully(this.name().toLowerCase())) + ")";
    }

    public static ChatMode getNext(ChatMode mode) {
        ChatMode[] values = values();
        int length = values.length - 1;
        int ordinal = mode.ordinal();

        return values[((ordinal + 1 > length) ? 0 : ordinal + 1)];
    }

    public static ChatMode getByString(String string) {
        return Arrays.stream(values())
                .filter(mode -> mode.getAliases().stream().anyMatch(str -> str.equalsIgnoreCase(string)))
                .findFirst()
                .orElse(null);
    }
}
