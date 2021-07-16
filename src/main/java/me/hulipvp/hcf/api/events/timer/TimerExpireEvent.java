package me.hulipvp.hcf.api.events.timer;

import me.hulipvp.hcf.game.timer.Timer;

public class TimerExpireEvent extends TimerEvent {

    public TimerExpireEvent(Timer timer) {
        super(timer);
    }
}
