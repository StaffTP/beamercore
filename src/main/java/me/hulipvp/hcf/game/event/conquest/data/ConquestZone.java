package me.hulipvp.hcf.game.event.conquest.data;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.faction.Cuboid;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.LocUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ConquestZone {
    
    @Getter @Setter private Location corner1, corner2;

    @Getter private final ConquestZoneType type;

    @Getter @Setter private long timer;
    @Getter @Setter private UUID capper;

    @Getter private Cuboid cuboid;
    @Getter private List<String> cappers;

    public ConquestZone(Location corner1, Location corner2, ConquestZoneType type) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.type = type;
        this.timer = TimeUnit.SECONDS.toMillis(ConfigValues.CONQUEST_TIME);
        this.capper = null;
        this.cappers = new ArrayList<>();
    }

    public void lowerTimer() {
        this.timer -= TimeUnit.SECONDS.toMillis(1);
    }

    public void resetTimer() {
        this.timer = TimeUnit.SECONDS.toMillis(ConfigValues.CONQUEST_TIME);
    }

    public Cuboid toCuboid() {
        if(corner1 == null || corner2 == null)
            return null;
        if(cuboid == null)
            cuboid = new Cuboid(corner1, corner2);

        return cuboid;
    }

    public void reformCuboid() {
        if(corner1 == null || corner2 == null)
            return;

        cuboid = new Cuboid(corner1, corner2);
    }

    @Override
    public String toString() {
        if(corner1 != null && corner2 != null)
            return LocUtils.locationToString(corner1) + ":" + LocUtils.locationToString(corner2) + ":" + type.name() + ":";
        
        return null;
    }

    public static ConquestZone fromString(String input) {
        String[] info = input.split(":");
        Location loc1 = LocUtils.stringToLocation(info[0]);
        Location loc2 = LocUtils.stringToLocation(info[1]);
        ConquestZoneType type = ConquestZoneType.valueOf(info[2]);
        return new ConquestZone(loc1, loc2, type);
    }
}
