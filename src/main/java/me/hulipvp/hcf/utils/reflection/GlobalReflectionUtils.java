package me.hulipvp.hcf.utils.reflection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GlobalReflectionUtils {

    // https://www.spigotmc.org/threads/fake-lightning-strike.97700/#post-1069886
    public static void sendLightning(Player player, Location location) {
        Class<?> entityLightning = getNMSClass("EntityLightning");
        try {
            Constructor<?> constructor = entityLightning.getConstructor(getNMSClass("World"),
                                    double.class, double.class,
                                    double.class, boolean.class, boolean.class);
            Object worldHandle  = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());
            Object lightningObject = constructor.newInstance(worldHandle, location.getX(), location.getY(), location.getZ(), false, false);

            Object weatherPacker = getNMSClass("PacketPlayOutSpawnEntityWeather").getConstructor(getNMSClass("Entity")).newInstance(lightningObject);

            sendPacket(player, weatherPacker);
            player.playSound(player.getLocation(), Sound.EXPLODE, 100, 1);
        } catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
