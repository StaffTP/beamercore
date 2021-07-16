package me.hulipvp.hcf.api.events.timer;

import lombok.Getter;
import me.hulipvp.hcf.game.timer.Timer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class TimerEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();

    @Getter private Timer timer;

    TimerEvent(Timer timer) {
        this.timer = timer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
