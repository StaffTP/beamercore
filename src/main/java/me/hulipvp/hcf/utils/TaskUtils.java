package me.hulipvp.hcf.utils;

import me.hulipvp.hcf.HCF;
import org.bukkit.Bukkit;

public class TaskUtils {

    public static void runSync(Runnable runnable) {
        if(Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(HCF.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if(Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), runnable);
        else
            runnable.run();
    }
}
