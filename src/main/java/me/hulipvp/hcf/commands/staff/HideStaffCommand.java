package me.hulipvp.hcf.commands.staff;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HideStaffCommand {

    @Command(label = "hidestaff", aliases = {"hs"}, permission = "command.hidestaff", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 0) {
            if (!profile.isHideStaff()) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                            HCFProfile hcfProfile = HCFProfile.getByPlayer(player);
                            if (hcfProfile.getVanish() != null) {
                                if (hcfProfile.getVanish().isVanished()) {
                                    p.hidePlayer(player);
                                }
                            }
                        }
                );
            } else {
                Bukkit.getOnlinePlayers().forEach(player -> {
                        HCFProfile hcfProfile = HCFProfile.getByPlayer(player);
                        if (hcfProfile.getVanish() != null) {
                            if (hcfProfile.getVanish().isVanished()) {
                                p.showPlayer(player);
                            }
                        }
                    }
            );
            }
            profile.setHideStaff(!profile.isHideStaff());
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Locale.MODMODE_HIDESTAFF.toString().replace("%status%", profile.isHideStaff() ? "&chiding" : "&ashowing")));
        }
    }
}
