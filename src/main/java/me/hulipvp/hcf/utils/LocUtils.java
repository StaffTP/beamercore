package me.hulipvp.hcf.utils;

import me.hulipvp.hcf.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LocUtils {
    
    public static String locationToString(Location location) {
        if(location == null)
            return "";
        if(location.getWorld() == null)
            return "";
        if(location.getWorld().getName() == null)
            return "";

        return location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getWorld().getName() + ";" + location.getYaw() + ";" + location.getPitch() + ";";
    }

    public static String locationToFomattedString(Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    public static Location stringToLocation(String string) {
        if (string == null || string.equals("")) return null;
        String[] str = string.split(";");
        double x = new Double(str[0]);
        double y = new Double(str[1]);
        double z = new Double(str[2]);
        World world = Bukkit.getWorld(str[3]);
        float yaw = new Float(str[4]);
        float pitch = new Float(str[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String getCardinalDirection(Location loc) {
        double rotation = (loc.getYaw() - 90) % 360;
        if(rotation < 0)
            rotation += 360.0;

        if(0 <= rotation && rotation < 22.5) {
            return "[W]";
        } else if(22.5 <= rotation && rotation < 67.5) {
            return "[NW]";
        } else if(67.5 <= rotation && rotation < 112.5) {
            return "[N]";
        } else if(112.5 <= rotation && rotation < 157.5) {
            return "[NE]";
        } else if(157.5 <= rotation && rotation < 202.5) {
            return "[E]";
        } else if(202.5 <= rotation && rotation < 247.5) {
            return "[SE]";
        } else if(247.5 <= rotation && rotation < 292.5) {
            return "[S]";
        } else if(292.5 <= rotation && rotation < 337.5) {
            return "[SW]";
        } else if(337.5 <= rotation && rotation < 360.0) {
            return "[W]";
        } else {
            return "[?]";
        }
    }

    public static String getCardinalDirection(Player player) {
        return getCardinalDirection(player.getLocation());
    }

    public static boolean checkBorder(Location location) {
        int border = 0;

        if (location.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            border = ConfigValues.LIMITERS_WORLD_BORDER;
        } else if (location.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            border = ConfigValues.LIMITERS_NETHER_BORDER;
        } else if (location.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            border = ConfigValues.LIMITERS_END_BORDER;
        }

        if(border <= 0)
            return false;

        return Math.abs(location.getBlockX()) > border || Math.abs(location.getBlockZ()) > border;
    }

    public static boolean checkWarzone(Location location) {
        if(location.getWorld().getEnvironment() == World.Environment.THE_END)
            return true;
        int buildLimit = 0;
        if (location.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            buildLimit = ConfigValues.FACTIONS_SIZE_NETHER_WARZONE;
        } else {
            buildLimit = ConfigValues.FACTIONS_SIZE_WARZONE;
        }
        if(buildLimit <= 0)
            return true;

        return Math.abs(location.getBlockX()) <= buildLimit && Math.abs(location.getBlockZ()) <= buildLimit;
    }

    public static boolean checkWarzoneBuild(Location location) {
        if(location.getWorld().getEnvironment() == World.Environment.THE_END)
            return true;
        int buildLimit = 0;
        if (location.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            buildLimit = ConfigValues.FACTIONS_SIZE_NETHER_WARZONE_BUILD_LIMIT;
        } else {
            buildLimit = ConfigValues.FACTIONS_SIZE_WARZONE_BUILD_LIMIT;
        }
        if(buildLimit <= 0)
            return true;

        return Math.abs(location.getBlockX()) <= buildLimit && Math.abs(location.getBlockZ()) <= buildLimit;
    }

    public static ItemStack getClaimingWand() {
        return new ItemBuilder(Material.GOLD_HOE)
                .amount(1)
                .name("&aClaiming Wand")
                .lore(
                        "&eRight/Left Click &6Block",
                        "&b- &fSelect claim's corners",
                        "",
                        "&eRight Click &6Air",
                        "&b- &fCancel current claim",
                        "",
                        "&9Crouch &eLeft Click &6Block/Air",
                        "&b- &fPurchase current claim"
                )
                .get();
    }
}
