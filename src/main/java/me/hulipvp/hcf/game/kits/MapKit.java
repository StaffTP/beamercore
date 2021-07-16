package me.hulipvp.hcf.game.kits;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.player.data.PlayerInv;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class MapKit {

    @Getter private final String name;

    @Getter @Setter private boolean enabled;
    @Getter @Setter private ChatColor color;
    @Getter @Setter private PlayerInv playerInv;

    public MapKit(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return this.getColor() + WordUtils.capitalizeFully(this.getName());
    }
}
