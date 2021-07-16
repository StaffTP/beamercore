package me.hulipvp.hcf.api.events.faction.player;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import org.bukkit.entity.Player;

public class FactionKickEvent extends PlayerFactionEvent {

    @Getter private final Player player;
    @Getter private final FactionMember kicked;

    public FactionKickEvent(PlayerFaction faction, Player player, FactionMember kicked) {
        super(faction);

        this.player = player;
        this.kicked = kicked;
    }
}
