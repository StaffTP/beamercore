package me.hulipvp.hcf.api.events.conquest;

import lombok.Getter;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZone;
import org.bukkit.entity.Player;

public class ConquestKnockEvent extends ConquestEvent {
    
    @Getter private final ConquestZone zone;
    @Getter private final int knockTime;
    @Getter private final Player player;

    public ConquestKnockEvent(Conquest conquest, ConquestZone zone, int knockTime, Player player) {
        super(conquest);

        this.zone = zone;
        this.knockTime = knockTime;
        this.player = player;
    }
}
