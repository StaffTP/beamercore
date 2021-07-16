package me.hulipvp.hcf.game.faction.command.args.player.leader;

import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class FactionRenameArgument {

    @Command(label = "f.rename", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 2) {
            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();
                if(!pf.getMembers().get(p.getUniqueId()).isAtLeast(FactionRank.LEADER)) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_LEADER.toString());
                    return;
                }

                if(pf.getLastRename() != 0 && System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5) < pf.getLastRename()) {
                    p.sendMessage(Locale.COMMAND_FACTION_RENAME_COOLDOWN.toString());
                    return;
                }

                if(StringUtils.checkFactionName(args.getArg(1)) != null) {
                    p.sendMessage(StringUtils.checkFactionName(args.getArg(1)));
                } else {
                    String oldName = pf.getName();

                    pf.setName(args.getArg(1));
                    pf.save();

                    pf.setLastRename(System.currentTimeMillis());

                    p.sendMessage(Locale.COMMAND_FACTION_RENAME.toString().replace("%name%", pf.getName()));
                    Bukkit.getServer().broadcastMessage(Locale.COMMAND_FACTION_RENAME_BROADCAST.toString().replace("%oldname%", oldName).replace("%name%", pf.getName()));
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "rename <name>"));
        }
    }
}
