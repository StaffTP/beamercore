package me.hulipvp.hcf.api.events.conquest;

import lombok.Getter;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZone;
import org.bukkit.entity.Player;

public class ConquestControlEvent extends ConquestEvent {
    
    @Getter private final ConquestZone zone;
    @Getter private final Player player;

    public ConquestControlEvent(Conquest conquest, ConquestZone zone, Player player) {
        super(conquest);

        this.zone = zone;
        this.player = player;
    }
}
