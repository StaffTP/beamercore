package me.hulipvp.hcf.api.events.mountain;

import lombok.Getter;
import me.hulipvp.hcf.game.event.mountain.Mountain;

public class MountainResetEvent extends MountainEvent {

    @Getter private final Long resetTime;

    public MountainResetEvent(Mountain mountain, Long resetTime) {
        super(mountain);

        this.resetTime = resetTime;
    }
}
