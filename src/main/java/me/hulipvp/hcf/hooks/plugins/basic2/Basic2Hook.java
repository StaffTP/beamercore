package me.hulipvp.hcf.hooks.plugins.basic2;

import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import net.evilblock.stark.Stark;
import net.evilblock.stark.profile.BukkitProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Basic2Hook implements PlayerHook {

    public static boolean canHook() {
        return Bukkit.getPluginManager().getPlugin("Basic2") != null;
    }

    @Override
    public boolean canChat(Player player) {
        return true;
    }


    @Override
    public String getRankName(Player player) {
        BukkitProfile bukkitProfile = Stark.getInstance().getCore().getProfileHandler().getByUUID(player.getUniqueId());
        if (bukkitProfile == null) return "Member";
        return bukkitProfile.getRank().getId();
    }

    @Override
    public String getRankPrefix(Player player) {
        BukkitProfile bukkitProfile = Stark.getInstance().getCore().getProfileHandler().getByUUID(player.getUniqueId());
        if (bukkitProfile == null) return "";
        return bukkitProfile.getRank().getPrefix();
    }

    @Override
    public String getRankSuffix(Player player) {
        return "";
    }

    @Override
    public String getTag(Player player) {
        return "";
    }
}
