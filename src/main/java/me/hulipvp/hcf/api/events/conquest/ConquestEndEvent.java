package me.hulipvp.hcf.api.events.conquest;

import lombok.Getter;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.conquest.data.ConquestEndReason;
import org.bukkit.entity.Player;

public class ConquestEndEvent extends ConquestEvent {

    @Getter private final Player capper;
    @Getter private final ConquestEndReason reason;

    public ConquestEndEvent(Conquest conquest, Player capper, ConquestEndReason reason) {
        super(conquest);
        this.capper = capper;
        this.reason = reason;
    }
}
