package me.hulipvp.hcf.api.events.faction.player;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;

public class FactionRenameEvent extends PlayerFactionEvent {

    @Getter private final String oldName;
    @Getter private final String newName;

    public FactionRenameEvent(PlayerFaction faction, String oldName, String newName) {
        super(faction);

        this.oldName = oldName;
        this.newName = newName;
    }
}
