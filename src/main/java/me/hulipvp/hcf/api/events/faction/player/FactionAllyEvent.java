package me.hulipvp.hcf.api.events.faction.player;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;

public class FactionAllyEvent extends PlayerFactionEvent {
    
    @Getter private final PlayerFaction allied;

    public FactionAllyEvent(PlayerFaction faction, PlayerFaction allied) {
        super(faction);

        this.allied = allied;
    }
}
