package me.hulipvp.hcf.game.faction.command.args.player.captain;

import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionUninviteArgument {

    @Command(label = "f.uninvite", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 2) {
            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();
                if(!(pf.getMembers().get(p.getUniqueId()).isAtLeast(FactionRank.CAPTAIN))) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_CAPTAIN.toString());
                } else {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                    if(target.isOnline() || target.hasPlayedBefore()) {
                        if(!pf.isInvited(target.getUniqueId())) {
                            p.sendMessage(Locale.COMMAND_FACTION_UNINVITE_NOT_INVITED.toString().replace("%name%", target.getName()));
                            return;
                        }

                        pf.removeInvite(target.getUniqueId());
                        pf.save();
                        p.sendMessage(Locale.COMMAND_FACTION_UNINVITE_MESSAGE.toString().replace("%name%", target.getName()));
                    } else {
                        p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    }
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "uninvite <name>"));
        }
    }
}
