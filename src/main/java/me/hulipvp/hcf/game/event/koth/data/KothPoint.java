package me.hulipvp.hcf.game.event.koth.data;

import me.hulipvp.hcf.game.faction.Cuboid;
import me.hulipvp.hcf.utils.LocUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class KothPoint {

    @Getter @Setter private Location corner1, corner2;

    @Getter private Cuboid cuboid;

    public KothPoint(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
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
            return LocUtils.locationToString(corner1) + ":" + LocUtils.locationToString(corner2) + ":";

        return null;
    }

    public static KothPoint fromString(String input) {
        String[] info = input.split(":");
        Location loc1 = LocUtils.stringToLocation(info[0]);
        Location loc2 = LocUtils.stringToLocation(info[1]);
        return new KothPoint(loc1, loc2);
    }
}
