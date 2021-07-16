package me.hulipvp.hcf.hooks.plugins;

import org.bukkit.entity.Player;

public interface PlayerHook {

    boolean canChat(Player player);

    String getRankName(Player player);

    String getRankPrefix(Player player);

    String getRankSuffix(Player player);

    String getTag(Player player);
}
