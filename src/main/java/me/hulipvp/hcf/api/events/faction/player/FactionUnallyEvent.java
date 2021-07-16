package me.hulipvp.hcf.api.events.faction.player;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;

public class FactionUnallyEvent extends PlayerFactionEvent {

    @Getter private final PlayerFaction allied;

    public FactionUnallyEvent(PlayerFaction faction, PlayerFaction allied) {
        super(faction);

        this.allied = allied;
    }
}
