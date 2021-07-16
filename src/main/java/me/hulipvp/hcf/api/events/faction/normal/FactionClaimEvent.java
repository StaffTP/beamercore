package me.hulipvp.hcf.api.events.faction.normal;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class FactionClaimEvent extends FactionEvent implements Cancellable {

    @Getter private final Player player;

    @Getter private final Claim claim;

    @Getter @Setter private boolean cancelled;

    public FactionClaimEvent(Faction faction, Player player, Claim claim) {
        super(faction);

        this.player = player;
        this.claim = claim;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
