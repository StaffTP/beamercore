package me.hulipvp.hcf.game.event.conquest.data;

import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum ConquestZoneType {
    
    RED(ChatColor.RED),
    YELLOW(ChatColor.YELLOW),
    GREEN(ChatColor.DARK_GREEN),
    BLUE(ChatColor.BLUE);

    @Getter private final ChatColor color;

    ConquestZoneType(ChatColor color) {
        this.color = color;
    }

    public String getDisplay() {
        return this.getColor() + WordUtils.capitalizeFully(this.name());
    }

    @Override
    public String toString() {
        return getDisplay();
    }
}
