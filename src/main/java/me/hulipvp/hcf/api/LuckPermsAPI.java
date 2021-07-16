package me.hulipvp.hcf.api;


import me.hulipvp.hcf.HCF;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;

/**
 * This class is made more secure then the actual API.
 * No exception will be thrown because of null statements or the plugin not being found.
 * This class was not made by the resource holder (Luck).
 *
 * Portal: https://www.spigotmc.org/resources/luckperms.28140/
 *
 * @author Interface
 */
public class LuckPermsAPI {

    private LuckPerms luckPerms;

    /**
     * Register The luckPerms field by creating a provider.
     */
    public LuckPermsAPI() {
        if(isLuckPermsFound()) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
            registerRankColors();
        }
    }

    /**
     * Register all the ranks with the default color in the config.
     */
    private void registerRankColors() {
        for (Group group : luckPerms.getGroupManager().getLoadedGroups()) {
            if(!HCF.getInstance().getConfig().contains(group.getName()))
                HCF.getInstance().getConfig().set(group.getName(), "WHITE");
        }
        HCF.getInstance().saveConfig();
    }

    /**
     * @return The status of LuckPerms being found.
     */
    private boolean isLuckPermsFound(){
        return Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
    }

    /**
     * Will return 'Default' if their is no LuckPerms found.
     * @param player The player where to get the rank from.
     * @return The name of the rank of the player.
     */
    public String getRank(Player player) {
        if (isLuckPermsFound()) {
            if (Objects.requireNonNull(luckPerms.getGroupManager().getGroup(Objects.requireNonNull(luckPerms.getUserManager().getUser(player.getUniqueId())).getPrimaryGroup())).getDisplayName() == null) {
                return "Default";
            }
           return Objects.requireNonNull(luckPerms.getGroupManager().getGroup(Objects.requireNonNull(luckPerms.getUserManager().getUser(player.getUniqueId())).getPrimaryGroup())).getDisplayName();
        }
        return "Default";
    }
}
