package me.hulipvp.hcf.game.timer.type.player;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.entity.Player;

public class PlayerTimer extends Timer {

    @Getter private final Player player;
    @Getter private final PlayerTimerType type;
    @Getter @Setter private String text = "Default";

    public PlayerTimer(Player player, PlayerTimerType type, long length) {
        super(length);

        this.player = player;
        this.type = type;
    }

    public PlayerTimer(Player player, PlayerTimerType type) {
        this(player, type, type.getDefaultTime());
    }

    public void add() {
        HCFProfile profile = HCFProfile.getByPlayer(this.getPlayer());
        profile.addTimer(this);
    }

    public String getDisplay() {
        return TimeUtils.getFormatted(this.getTimeRemaining(), true, true);
    }

    @Override
    public boolean cancel() {
        HCFProfile profile = HCFProfile.getByPlayer(player);
        profile.getTimers().remove(this);

        return super.cancel();
    }
}
