package me.hulipvp.hcf.game.timer.type.server;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.utils.TimeUtils;

public class ServerTimer extends Timer {
    
    @Getter private ServerTimerType type;
    @Getter @Setter
    private String command;
    @Getter @Setter private String text = "Default";

    private ServerTimer(long length) {
        super(length);
    }

    public ServerTimer(ServerTimerType type) {
        this(type.getDefaultTime());

        this.type = type;
    }

    public void add() {
        Timer.getTimers().put(this.getUuid(), this);
    }

    public String getDisplay() {
        return TimeUtils.getFormatted(this.getTimeRemaining(), true, true);
    }

    public static boolean isEotw() {
        return Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .anyMatch(timer -> timer.getType() == ServerTimerType.EOTW);
    }

    public static boolean isSotw() {
        return Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .anyMatch(timer -> timer.getType() == ServerTimerType.SOTW);
    }

    public static ServerTimer getRestart() {
        return getTimer(ServerTimerType.RESTART);
    }

    public static ServerTimer getTimer(ServerTimerType type) {
        return Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .filter(timer -> timer.getType() == type)
                .findFirst()
                .orElse(null);
    }
}
