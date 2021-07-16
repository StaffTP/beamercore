package me.hulipvp.hcf.hooks.plugins.utils;

import cc.invictusgames.iutils.api.IUtilsAPI;
import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UtilsHook implements PlayerHook {

    public static boolean canHook() {
        return Bukkit.getPluginManager().getPlugin("Utils") != null
                && Bukkit.getPluginManager().getPlugin("Utils").getDescription() != null
                && Bukkit.getPluginManager().getPlugin("Utils").getDescription().getAuthors() != null
                && Bukkit.getPluginManager().getPlugin("Utils").getDescription().getAuthors().stream().anyMatch(item -> item.contains("Value Network"));
    }

    @Override
    public boolean canChat(Player player) {
        return true;
    }

    @Override
    public String getRankName(Player player) {
        return IUtilsAPI.getRank(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(Player player) {
        return IUtilsAPI.getPrefix(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(Player player) {
        return IUtilsAPI.getSuffix(player.getUniqueId());
    }

    @Override
    public String getTag(Player player) {
        String tag = IUtilsAPI.getTag(player.getUniqueId());
        if(tag == null || tag.equals(""))
            return "";

        return tag;
    }
}
