package me.hulipvp.hcf.hooks.plugins.aqua;


import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import me.hulipvp.hcf.utils.CC;
import org.bukkit.entity.Player;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/14/2021 / 12:58 AM
 * GlacialHCF / me.hulipvp.hcf.hooks.plugins.hexa
 */
public class AquaCore implements PlayerHook {
    @Override
    public boolean canChat(Player player) {
        return true;
    }

    @Override
    public String getRankName(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getName();
    }

    @Override
    public String getRankPrefix(Player player) {
        return CC.translate(AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getPrefix());
    }

    @Override
    public String getRankSuffix(Player player) {
        return CC.translate(AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getSuffix());
    }

    @Override
    public String getTag(Player player) {
        return CC.translate(AquaCoreAPI.INSTANCE.getTagFormat(player));
    }
}
