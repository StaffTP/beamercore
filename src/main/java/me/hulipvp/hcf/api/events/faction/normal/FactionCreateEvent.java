package me.hulipvp.hcf.api.events.faction.normal;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.Faction;
import org.bukkit.entity.Player;

public class FactionCreateEvent extends FactionEvent {

    @Getter private final Player player;

    public FactionCreateEvent(Faction faction, Player player) {
        super(faction);

        this.player = player;
    }
}
