package me.hulipvp.hcf.game.timer.type.player;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import org.bukkit.configuration.file.FileConfiguration;

public enum PlayerTimerType {

    APPLE(10, false),
    ARCHERMARK(10, false),
    BARD_EFFECT(5, false),
    BACKSTAB(15, false),
    COMBAT(30, true),
    ENDERPEARL(16, true),
    GAPPLE(360 * 60, true),
    JUMP_EFFECT(15, true),
    HOME(10, false),
    LOGOUT(30, false),
    PVPTIMER(30 * 60, true),
    SPAWN(15, false),
    SPEED_EFFECT(15, false),
    STARTING(60 * 60, true),
    STUCK((2 * 60) + 30, false),
    CUSTOM(0, true);

    @Getter private final long defaultTime;
    @Getter private final boolean save;

    PlayerTimerType(long defaultTime, boolean save) {
        this.defaultTime = getTime() != -1 ? getTime() : defaultTime;
        this.save = save;
    }

    public int getTime() {
        FileConfiguration config = HCF.getInstance().getConfig();

        return config.contains("timers." + name().toLowerCase().replace("_", "-")) ? config.getInt("timers." + name().toLowerCase().replace("_", "-")) : -1;
    }
}
