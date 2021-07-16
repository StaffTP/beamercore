package me.hulipvp.hcf.game.faction.command.args.player.member;

import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/5/2021 / 1:57 AM
 * vhcf / me.hulipvp.hcf.game.faction.command.args.player.member
 */
public class FactionPlayerFocusCommand {

    @Command(label = "focus", playerOnly = true)
    public void onCommand(CommandData args) {
            Player p = (Player) args.getSender();

            if (args.length() > 1) {
                HCFProfile profile = HCFProfile.getByPlayer(p);

                if (profile.getFactionObj() == null) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
                } else {
                    Player target = Bukkit.getPlayer(args.getArg(1));
                    if (target == null || !target.isOnline()) {
                        p.sendMessage(CC.translate("&cNo Faction or Player by the name of '%name%' is found!").replace("%name%", args.getArg(1)));
                        return;
                    }

                    PlayerFaction pf = profile.getFactionObj();
                    if (pf.getMembers().containsKey(target.getUniqueId())) {
                        p.sendMessage(ChatColor.RED + "You cannot focus your own team.");
                        return;
                    }

                    if (pf.getFocused() != null) {
                        if (pf.getFocused().equals(target.getUniqueId())) {
                            pf.setFocused(null);
                            p.sendMessage(C.color("&d" + target.getName() + " &eis no longer focused."));
                            return;
                        }
                    }
                    pf.setFocused(target.getUniqueId());
                    pf.sendMessage(C.color("&d" + target.getName() + " &eis now focused."));
                }
            } else {
                p.sendMessage(CC.translate("&cUsage: /focus <player>"));
            }
        }
}
