package me.hulipvp.hcf.api.events.timer;

import me.hulipvp.hcf.game.timer.Timer;

public class TimerUnpauseEvent extends TimerEvent {

    public TimerUnpauseEvent(Timer timer) {
        super(timer);
    }
}
