package me.hulipvp.hcf.hooks.server.holograms;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;

public class FTOPNameHologramReplacer implements PlaceholderReplacer {

    private final int position;

    public FTOPNameHologramReplacer(int position) {
        this.position = position;
    }

    @Override
    public String update() {
        if (PlayerFaction.getSortedFactionTop().size() < position) return "Unknown";
        PlayerFaction entry = (PlayerFaction) PlayerFaction.getSortedFactionTop().get(position - 1);
        return entry.getName();
    }
}
