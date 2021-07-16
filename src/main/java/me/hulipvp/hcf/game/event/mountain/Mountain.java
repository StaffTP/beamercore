package me.hulipvp.hcf.game.event.mountain;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.event.mountain.type.MountainType;
import me.hulipvp.hcf.game.faction.Cuboid;
import me.hulipvp.hcf.game.faction.type.event.MountainFaction;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.mountain.MountainResetEvent;
import me.hulipvp.hcf.utils.*;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Mountain {
    
    @Getter private static final Map<String, Mountain> mountains = new HashMap<>();

    @Getter private final String name;
    @Getter private final MountainType type;

    @Getter @Setter private MountainFaction faction;
    @Getter @Setter private Long lastReset;
    @Getter @Setter private Integer resetTime;
    @Getter @Setter private Location point1, point2;

    public Mountain(String name, MountainType type) {
        this.name = name;
        this.type = type;
        this.lastReset = System.currentTimeMillis();
        this.resetTime = ConfigValues.MOUNTIAN_RESET_TIME;
    }

    public static Mountain getMountain(MountainType type) {
        return mountains.values().stream()
                .filter(mountain -> mountain.getType() == type)
                .findFirst()
                .orElse(null);
    }

    public static Mountain getMountain(String name) {
        return mountains.get(name);
    }

    public void save() {
        HCF.getInstance().getBackend().saveMountain(this);
    }

    public Document toDocument() {
        Document doc = new Document();

        doc.append("name", this.getName());
        doc.append("type", this.getType().name());
        doc.append("resetTime", this.getResetTime());

        if(this.getPoint1() != null)
            doc.append("point1", LocUtils.locationToString(this.getPoint1()));

        if(this.getPoint2() != null)
            doc.append("point2", LocUtils.locationToString(this.getPoint2()));

        return doc;
    }

    public void reset() {
        if(this.getPoint1() == null || this.getPoint2() == null)
            return;

        Cuboid cuboid = new Cuboid(this.getPoint1(), this.getPoint2());
        MountainType type = this.getType();

        TaskUtils.runSync(() -> {
            for(Block block : cuboid) {
                switch(this.getType()) {
                    case ORE:
                        block.setType(this.getType().getMaterials()[TimeUtils.random.nextInt(type.getMaterials().length)]);
                        break;
                    case GLOWSTONE:
                        block.setType(Material.GLOWSTONE);
                        break;
                }
            }
        });

        this.setLastReset(System.currentTimeMillis());

        MountainResetEvent event = new MountainResetEvent(this, this.lastReset);
        Bukkit.getPluginManager().callEvent(event);

        Bukkit.broadcastMessage(Locale.MOUNTAIN_RESET.toString().replace("%type%", WordUtils.capitalizeFully(type.name())));
    }

    public static void instate() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
            for(Mountain mountain : Mountain.getMountains().values()) {
                if(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - mountain.getLastReset()) >= mountain.getResetTime())
                    mountain.reset();
            }
        }, 100L, 20L);

        Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getInstance(), () -> {
            Mountain.getMountains().values().forEach(Mountain::reset);
        }, 1000L);
    }
}
