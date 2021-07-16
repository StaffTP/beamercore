package me.hulipvp.hcf.game.event.koth;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.event.koth.data.KothEndReason;
import me.hulipvp.hcf.game.event.koth.data.KothPoint;
import me.hulipvp.hcf.game.faction.type.event.KothFaction;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.koth.KothEndEvent;
import me.hulipvp.hcf.api.events.koth.KothStartEvent;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Koth {
    
    @Getter private static final Map<String, Koth> koths = new HashMap<>();
    @Getter @Setter private static Koth activeKoth;

    @Getter private final String name;
    @Getter @Setter private KothFaction faction;
    @Getter @Setter private KothPoint point;
    @Getter @Setter private Integer time;
    @Getter @Setter private long timer;
    @Getter @Setter private UUID capper;
    @Getter @Setter private boolean special;
    @Getter @Setter private boolean pearlable;
    @Getter private final List<String> cappers;

    public Koth(String name) {
        this.name = name;
        this.faction = null;
        this.point = new KothPoint(null, null);
        this.time = ConfigValues.KOTH_SPECIAL_TIMES.getOrDefault(name.toLowerCase(), ConfigValues.KOTH_TIME);
        this.timer = TimeUnit.MINUTES.toMillis(this.time);
        this.capper = null;
        this.special = false;
        this.pearlable = true;
        this.cappers = new ArrayList<>();
    }

    public void start() {
        this.timer = TimeUnit.MINUTES.toMillis(this.time);
        setActiveKoth(this);

        KothStartEvent event = new KothStartEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void stop() {
        if(getActiveKoth() == this) {
            this.capper = null;
            setActiveKoth(null);

            KothEndEvent event = new KothEndEvent(this, KothEndReason.CANCELLED);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    public boolean isCapperOnline() {
        Player player = Bukkit.getPlayer(capper);

        return player != null && player.isOnline();
    }

    public void save() {
        HCF.getInstance().getBackend().saveKoth(this);
    }

    public void resetTimer() {
        this.timer = TimeUnit.MINUTES.toMillis(this.time);
    }

    public void lowerTimer() {
        this.timer -= TimeUnit.SECONDS.toMillis(1);
    }

    public Document toDocument() {
        return new Document().append("name", this.getName()).append("faction", (this.getFaction() == null) ? null : this.getFaction().getUuid().toString()).append("point", this.getPoint().toString()).append("special", this.isSpecial()).append("pearlable", this.isPearlable()).append("time", this.getTime());
    }

    public static Koth getKoth(String name) {
        return getKoths().get(name);
    }

    public static void instate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getActiveKoth() == null)
                    return;

                if(getActiveKoth().getCapper() == null || !getActiveKoth().isCapperOnline()) {
                    getActiveKoth().setCapper(null);
                    getActiveKoth().resetTimer();
                    return;
                }

                getActiveKoth().lowerTimer();

                if(getActiveKoth().getTimer() + TimeUnit.SECONDS.toMillis(1) != TimeUnit.MINUTES.toMillis(getActiveKoth().getTime()) && TimeUnit.MILLISECONDS.toSeconds(getActiveKoth().getTimer()) % 30 == 0) {
                    Bukkit.broadcastMessage(Locale.EVENT_KOTH_CONTROLLING.toString().replace("%name%", getActiveKoth().getName())
                            .replace("%time%", TimeUtils.getFormatted(getActiveKoth().getTimer())));
                }

                if(getActiveKoth().getTimer() <= 20L) {
                    KothEndEvent event = new KothEndEvent(getActiveKoth(), KothEndReason.CAPTURED);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    getActiveKoth().stop();
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 2L, 20L);
    }
}
