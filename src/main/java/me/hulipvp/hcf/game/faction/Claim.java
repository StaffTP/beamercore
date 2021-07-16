package me.hulipvp.hcf.game.faction;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.LocUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Claim {

    @Getter private Location corner1, corner2;

    @Getter @Setter private Cuboid cuboid;
    @Getter @Setter private Faction faction;

    public Claim(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public Location getCorner3() {
        return new Location(corner1.getWorld(), corner1.getBlockX(), 0, corner2.getBlockZ());
    }

    public Location getCorner4() {
        return new Location(corner1.getWorld(), corner2.getBlockX(), 0, corner1.getBlockZ());
    }

    public List<Location> getCorners() {
        return Arrays.asList(corner1, corner2, getCorner3(), getCorner4());
    }

    public Cuboid toCuboid() {
        if(cuboid == null)
            cuboid = new Cuboid(this.getCorner1(), this.getCorner2());

        return cuboid;
    }

    public Cuboid getCuboid() {
        return toCuboid();
    }

    @Override
    public String toString() {
        if(corner1 == null || corner2 == null)
            return null;

        return LocUtils.locationToString(corner1) + ":" + LocUtils.locationToString(corner2);
    }

    public static Claim fromString(String input) {
        String[] info = input.split(":");
        Location loc1 = LocUtils.stringToLocation(info[0]);
        Location loc2 = LocUtils.stringToLocation(info[1]);
        return new Claim(loc1, loc2);
    }

    public static boolean isNearby(Location loc, int buffer) {
        for(int x = -buffer; x <= buffer; ++x) {
            for(int z = -buffer; z <= buffer; ++z) {
                if(x != 0 || z != 0) {
                    Faction faction = Faction.getByLocation(new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z));
                    if(faction == null)
                        continue;

                    return true;
                }
            }
        }

        return false;
    }

    public static List<Faction> getNearbyFactions(Location loc, int buffer) {
        List<Faction> nearbyFactions = new ArrayList<>();
        for(int x = -buffer; x <= buffer; ++x) {
            for(int z = -buffer; z <= buffer; ++z) {
                if(x != 0 || z != 0) {
                    Faction faction = Faction.getByLocation(new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z));
                    if(faction == null)
                        continue;
                    if(nearbyFactions.contains(faction))
                        continue;

                    nearbyFactions.add(faction);
                }
            }
        }

        return nearbyFactions;
    }

    public static List<Location> getPossibleStuckLocation(Location center, int buffer) {
        List<Location> locations = new ArrayList<>();
        for(int x = -buffer; x <= buffer; ++x) {
            for(int z = -buffer; z <= buffer; ++z) {
                if(x != 0 || z != 0) {
                    Location location = new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
                    Faction faction = Faction.getByLocation(location);
                    if(faction != null)
                        continue;

                    location.add(location);
                }
            }
        }

        return locations;
    }

    public int getPrice() {
        return getPrice(true);
    }

    public int getPrice(boolean selling) {
        if(corner1 == null || corner2 == null)
            return 0;
        if(!corner1.getWorld().getUID().equals(corner2.getWorld().getUID()))
            return 0;

        int multiplier = 1;
        int remaining = this.toCuboid().getVolume() / ConfigValues.FACTIONS_CLAIM_PRICE_VOLUME_DIVISOR;
        double price = 0;

        while(remaining > 0) {
            if(--remaining % ConfigValues.FACTIONS_CLAIM_PRICE_MULTIPLIER == 0)
                multiplier++;

            price += (ConfigValues.FACTIONS_CLAIM_PRICE_PER_BLOCK * multiplier);
        }

        if(selling)
            price *= ConfigValues.FACTIONS_CLAIM_PRICE_SELLING_MULTIPLIER;

        return (int) price;
    }
}
