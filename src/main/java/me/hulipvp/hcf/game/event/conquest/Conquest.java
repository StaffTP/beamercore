package me.hulipvp.hcf.game.event.conquest;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.event.conquest.data.ConquestEndReason;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZone;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZoneType;
import me.hulipvp.hcf.game.faction.type.event.ConquestFaction;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.conquest.ConquestEndEvent;
import me.hulipvp.hcf.api.events.conquest.ConquestStartEvent;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Conquest {

    @Getter private static final Map<String, Conquest> conquests = new HashMap<>();
    @Getter @Setter private static Conquest activeConquest;

    @Getter private final String name;
    @Getter @Setter private ConquestFaction faction;
    @Getter private final Map<ConquestZoneType, ConquestZone> zones;
    @Getter private final Map<String, Integer> points;
    @Getter @Setter private UUID finalCapper;

    public Conquest(String name) {
        this.name = name;
        this.faction = null;
        this.zones = new HashMap<>();
        this.zones.put(ConquestZoneType.RED, new ConquestZone(null, null, ConquestZoneType.RED));
        this.zones.put(ConquestZoneType.YELLOW, new ConquestZone(null, null, ConquestZoneType.YELLOW));
        this.zones.put(ConquestZoneType.GREEN, new ConquestZone(null, null, ConquestZoneType.GREEN));
        this.zones.put(ConquestZoneType.BLUE, new ConquestZone(null, null, ConquestZoneType.BLUE));
        this.points = new HashMap<>();
        this.finalCapper = null;
    }

    public void start() {
        setActiveConquest(this);

        ConquestStartEvent event = new ConquestStartEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void stop() {
        if(getActiveConquest() == this) {
            setActiveConquest(null);

            ConquestEndEvent event = new ConquestEndEvent(this, null, ConquestEndReason.CANCELLED);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    public void save() {
        HCF.getInstance().getBackend().saveConquest(this);
    }

    public ConquestZone getZoneAt(Location location) {
        for(ConquestZone zone : this.getZones().values()) {
            if(zone.toCuboid() == null)
                continue;

            if(zone.toCuboid().isInCuboid(location))
                return zone;
        }

        return null;
    }

    public static Conquest getConquest(String name) {
        return getConquests().get(name);
    }

    public Document toDocument() {
        return new Document()
                .append("name", this.getName())
                .append("faction", (this.getFaction() == null) ? null : this.getFaction().getUuid().toString())
                .append("red", this.getZones().get(ConquestZoneType.RED).toString())
                .append("yellow", this.getZones().get(ConquestZoneType.YELLOW).toString())
                .append("green", this.getZones().get(ConquestZoneType.GREEN).toString())
                .append("blue", this.getZones().get(ConquestZoneType.BLUE).toString());
    }

    public static void instate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getActiveConquest() == null)
                    return;

                Conquest conquest = getActiveConquest();
                for(ConquestZone zone : conquest.getZones().values()) {
                    if(zone.getCapper() != null) {
                        zone.lowerTimer();

                        if(zone.getTimer() <= 20L) {
                            zone.resetTimer();

                            HCFProfile capper = HCFProfile.getByUuid(zone.getCapper());
                            if (capper.getFactionObj() != null) {
                                int points = (conquest.getPoints().getOrDefault(capper.getFaction().toString(), 0)) + ConfigValues.CONQUEST_POINTS_PER_CAP;
                                conquest.getPoints().put(capper.getFaction().toString(), points);
                                Bukkit.broadcastMessage(Locale.EVENT_CONQUEST_CAPPED.toString()
                                        .replace("%zone%", zone.getType().getDisplay())
                                        .replace("%name%", capper.getFactionObj().getName()));
                                if (points >= ConfigValues.CONQUEST_MAX_POINTS) {
                                    ConquestEndEvent event = new ConquestEndEvent(getActiveConquest(), capper.getPlayer(), ConquestEndReason.CAPTURED);
                                    Bukkit.getServer().getPluginManager().callEvent(event);
                                    getActiveConquest().stop();
                                }
                            }
                        } else {
                            if (zone.getTimer() % 5000 == 0) {
                                Player capper = Bukkit.getPlayer(zone.getCapper());
                                if (capper != null) {
                                    capper.sendMessage(Locale.EVENT_CONQUEST_CAPPING.toString()
                                    .replace("%zone%", zone.getType().getDisplay())
                                    .replace("%time%", (zone.getTimer() / 1000) + "s"));
                                }
                            }
                        }
                    } else {
                        zone.resetTimer();
                    }
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 2L, 20L);
    }
}
