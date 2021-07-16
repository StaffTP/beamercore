package me.hulipvp.hcf.game.timer;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.timer.TimerExpireEvent;
import me.hulipvp.hcf.api.events.timer.TimerPauseEvent;
import me.hulipvp.hcf.api.events.timer.TimerUnpauseEvent;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class Timer {
    @Getter private static final HashMap<UUID, Timer> timers = new HashMap<>();

    @Getter private final UUID uuid;
    @Getter @Setter private long length;
    @Getter private long currentTime;
    @Getter private boolean paused;
    @Getter @Setter private boolean cancelled;
    @Getter @Setter private boolean isPvp = false;

    public Timer(long length) {
        this.uuid = UUID.randomUUID();
        this.length = TimeUnit.SECONDS.toMillis(length);
        this.currentTime = System.currentTimeMillis() + this.length;
        this.paused = false;
        this.cancelled = false;
    }

    public long getTimeRemaining() {
        return (this.isPaused()) ? this.getLength() : this.getCurrentTime() - System.currentTimeMillis();
    }

    public void setNewLength(long length) {
        this.length = TimeUnit.SECONDS.toMillis(length);
        this.currentTime = System.currentTimeMillis() + this.length;
    }

    public String getFormattedTime() {
        return TimeUtils.getFormatted(this.getTimeRemaining(), true, true);
    }

    public void setCurrentTime(long newTime, boolean convert) {
        if(convert)
            this.currentTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(newTime);
        else
            this.currentTime = System.currentTimeMillis() + newTime;
    }

    public void setPaused(boolean paused) {
        if(paused) {
            Bukkit.getPluginManager().callEvent(new TimerPauseEvent(this));
            this.setLength(this.getTimeRemaining());
        }

        if(!paused) {
            Bukkit.getPluginManager().callEvent(new TimerUnpauseEvent(this));
            this.setCurrentTime(this.getLength(), false);
        }

        this.paused = paused;
    }

    public boolean cancel() {
        setCancelled(true);
        setCurrentTime(0L, false);

        return cancelled;
    }

    public static void instate() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
            Iterator<Timer> timerIterator = timers.values().iterator();
            while(timerIterator.hasNext()) {
                Timer timer = timerIterator.next();

                if(timer.getTimeRemaining() <= 2L) {
                    if(!timer.isCancelled())
                        Bukkit.getPluginManager().callEvent(new TimerExpireEvent(timer));

                    timerIterator.remove();
                }
            }

            for(HCFProfile profile : HCFProfile.getProfiles().values()) {
                Iterator<PlayerTimer> ite = profile.getTimers().iterator();
                while(ite.hasNext()) {
                    try {
                        PlayerTimer timer = ite.next();

                        if (timer.getTimeRemaining() <= 2L) {
                            ite.remove();

                            if (!timer.isCancelled())
                                Bukkit.getPluginManager().callEvent(new TimerExpireEvent(timer));
                        }
                    } catch (NoSuchElementException e) {
                        continue;
                    }
                }
            }
        }, 2L, 2L);
    }
}
