package me.hulipvp.hcf.api.events.koth;

import lombok.Getter;
import me.hulipvp.hcf.game.event.koth.Koth;
import org.bukkit.entity.Player;

public class KothControlEvent extends KothEvent {

    @Getter private final Player player;

    public KothControlEvent(Koth koth, Player player) {
        super(koth);

        this.player = player;
    }
}
