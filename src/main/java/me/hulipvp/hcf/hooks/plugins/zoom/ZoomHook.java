package me.hulipvp.hcf.hooks.plugins.zoom;

import club.frozed.core.ZoomAPI;
import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ZoomHook implements PlayerHook {

    public static boolean canHook() {
        return Bukkit.getPluginManager().getPlugin("Zoom") != null;
    }

    @Override
    public boolean canChat(Player player) {
        return true;
    }

    @Override
    public String getRankName(Player player) {
        return ZoomAPI.getRankName(player);
    }

    @Override
    public String getRankPrefix(Player player) {
        return ZoomAPI.getRankPrefix(player);
    }

    @Override
    public String getRankSuffix(Player player) {
        return ZoomAPI.getRankSuffix(player);
    }

    @Override
    public String getTag(Player player) {
        String tag = ZoomAPI.getTag(player);
        return tag == null ? "" : tag;
    }

}
