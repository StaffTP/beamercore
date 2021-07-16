package me.hulipvp.hcf.api.events.koth;

import lombok.Getter;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.koth.data.KothEndReason;

public class KothEndEvent extends KothEvent {

    @Getter private final KothEndReason reason;

    public KothEndEvent(Koth koth, KothEndReason reason) {
        super(koth);

        this.reason = reason;
    }
}
