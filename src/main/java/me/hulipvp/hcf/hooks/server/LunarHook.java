package me.hulipvp.hcf.hooks.server;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class LunarHook {

    @Getter private boolean hooked;
    @Getter private LunarClientAPI lunarClientAPI;

    public LunarHook() {

        this.lunarClientAPI = LunarClientAPI.getInstance();

        this.hooked = true;
        Bukkit.getLogger().info("[HCF] Successfully hooked into the LunarClient-API.");
    }

    public static boolean canHook() {
        return Bukkit.getPluginManager().getPlugin("LunarClient-API") != null;
    }

    public boolean isLunarPlayer(Player player) {
        return this.lunarClientAPI.isRunningLunarClient(player);
    }

    public void toggleStaffModules(Player player) {
        this.lunarClientAPI.giveAllStaffModules(player);
    }

}
