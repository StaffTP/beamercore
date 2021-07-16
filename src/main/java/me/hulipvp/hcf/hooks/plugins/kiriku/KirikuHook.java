package me.hulipvp.hcf.hooks.plugins.kiriku;

import com.broustudio.KrikuAPI.KrikuAPI;
import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KirikuHook implements PlayerHook {

    public static boolean canHook() {
        return Bukkit.getPluginManager().getPlugin("Kiriku") != null;
    }

    @Override
    public boolean canChat(Player player) {
        return true;
    }


    @Override
    public String getRankName(Player player) {
        String rank = KrikuAPI.getRankManager().getRank(player.getUniqueId());
        if(rank == null)
            return "";
        if(rank.contains("null"))
            return "";

        return rank;
    }

    @Override
    public String getRankPrefix(Player player) {
        String rank = KrikuAPI.getRankManager().getRankPrefix(player.getUniqueId());
        if(rank == null)
            return "";
        if(rank.contains("null"))
            return "";

        return rank + " ";
    }

    @Override
    public String getRankSuffix(Player player) {
        String rank = KrikuAPI.getTagManager().getTagDisplay(player.getUniqueId());
        if(rank == null)
            return "";
        if(rank.contains("null"))
            return "";

        return rank;
    }

    @Override
    public String getTag(Player player) {
        return KrikuAPI.getTagManager().getTag(player.getUniqueId()) == null ? "" : KrikuAPI.getTagManager().getTag(player.getUniqueId());
    }
}
