package me.hulipvp.hcf.api.events.faction.player;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import org.bukkit.entity.Player;

public class FactionPromoteEvent extends PlayerFactionEvent {
    
    @Getter private final Player player;
    @Getter private final FactionMember promoted;

    public FactionPromoteEvent(PlayerFaction faction, Player player, FactionMember promoted) {
        super(faction);

        this.player = player;
        this.promoted = promoted;
    }
}
