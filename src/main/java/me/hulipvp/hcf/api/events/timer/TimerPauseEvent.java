package me.hulipvp.hcf.api.events.timer;

import me.hulipvp.hcf.game.timer.Timer;

public class TimerPauseEvent extends TimerEvent {

    public TimerPauseEvent(Timer timer) {
        super(timer);
    }
}
