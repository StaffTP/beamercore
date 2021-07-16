package me.hulipvp.hcf.game.timer.type.server;

import lombok.Getter;

public enum ServerTimerType {
    
    EOTW(10 * 60),
    SOTW(10 * 60),
    RESTART(10 * 60),
    SALE(60 * 10),
    KEY_SALE(60 * 10),
    KEY_ALL(60 * 10),
    RAMPAGE(60 * 60),
    EXTRACTION(60 * 60),
    CUSTOM(0);

    @Getter private final long defaultTime;

    ServerTimerType(long defaultTime) {
        this.defaultTime = defaultTime;
    }
}
