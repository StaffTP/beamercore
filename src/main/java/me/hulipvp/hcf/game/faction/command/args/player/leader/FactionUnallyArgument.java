package me.hulipvp.hcf.game.faction.command.args.player.leader;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionUnallyArgument {

    @Command(label = "f.unally", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = args.getPlayer();

        if(args.length() < 2) {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "unally <faction>"));
        } else {
            HCFProfile profile = HCFProfile.getByPlayer(p);
            PlayerFaction pf = profile.getFactionObj();
            if(pf == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
                return;
            }

            if(!pf.getMembers().get(p.getUniqueId()).isAtLeast(FactionRank.LEADER)) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_LEADER.toString());
                return;
            }

            Faction faction = Faction.getByName(args.getArg(1));
            if(!(faction instanceof PlayerFaction)) {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
                return;
            }

            PlayerFaction playerFaction = (PlayerFaction) faction;
            if(pf.isRequestedAlly(playerFaction.getUuid())) {
                pf.removeRequestedAlly(playerFaction.getUuid());
                pf.save();

                p.sendMessage(Locale.COMMAND_FACTION_UNALLY_DENIED.toString().replace("%faction%", playerFaction.getName()));
                return;
            }

            if(playerFaction.isRequestedAlly(pf.getUuid())) {
                playerFaction.removeRequestedAlly(pf.getUuid());
                playerFaction.save();

                p.sendMessage(Locale.COMMAND_FACTION_UNALLY_REMOVED_REQUEST.toString().replace("%faction%", playerFaction.getName()));
                return;
            }

            if(pf.isAllied(playerFaction.getUuid()) || playerFaction.isAllied(pf.getUuid())) {
                pf.removeAlly(playerFaction.getUuid());
                pf.save();
                playerFaction.removeAlly(pf.getUuid());
                playerFaction.save();
                pf.sendMessage(Locale.COMMAND_FACTION_UNALLY_REMOVED.toString().replace("%faction%", playerFaction.getName()));
                playerFaction.sendMessage(Locale.COMMAND_FACTION_UNALLY_REMOVED.toString().replace("%faction%", pf.getName()));
                return;
            }

            p.sendMessage(Locale.COMMAND_FACTION_UNALLY_NOT_ALLIED.toString());
        }
    }
}
