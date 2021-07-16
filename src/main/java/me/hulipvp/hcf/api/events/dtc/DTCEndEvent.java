package me.hulipvp.hcf.api.events.dtc;

import lombok.Getter;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.dtc.data.DTCEndReason;

public class DTCEndEvent extends DTCEvent {
    
    @Getter private final DTCEndReason reason;

    public DTCEndEvent(DTC dtc, DTCEndReason reason) {
        super(dtc);

        this.reason = reason;
    }
}
