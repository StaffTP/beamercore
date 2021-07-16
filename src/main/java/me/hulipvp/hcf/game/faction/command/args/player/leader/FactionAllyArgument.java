package me.hulipvp.hcf.game.faction.command.args.player.leader;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionAllyArgument {

    @Command(label = "f.ally", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = args.getPlayer();

        if (ConfigValues.FACTIONS_ALLY_MAX > 0) {
            if(args.length() < 2) {
                p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "ally <faction>"));
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

                TaskUtils.runAsync(() -> {
                    List<Faction> factions = Faction.findFactions(args.getArg(1));
                    Faction faction;
                    if(factions.isEmpty() || (!((faction = factions.get(0)) instanceof PlayerFaction))) {
                        p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
                        return;
                    }

                    PlayerFaction playerFaction = (PlayerFaction) faction;
                    if(pf.getAllies().size() >= ConfigValues.FACTIONS_ALLY_MAX || playerFaction.getAllies().size() >= ConfigValues.FACTIONS_ALLY_MAX) {
                        p.sendMessage(Locale.COMMAND_FACTION_ALLY_MAX.toString().replace("%faction%", pf.getAllies().size() > ConfigValues.FACTIONS_ALLY_MAX ? pf.getName() : playerFaction.getName()));
                        return;
                    }

                    if(pf.getUuid().equals(playerFaction.getUuid()) || pf.isAllied(playerFaction.getUuid()) || pf.isRequestedAlly(playerFaction.getUuid())) {
                        p.sendMessage(Locale.COMMAND_FACTION_ALLY_ALREADY.toString());
                        return;
                    }

                    if(playerFaction.isRequestedAlly(pf.getUuid())) {
                        playerFaction.removeRequestedAlly(pf.getUuid());
                        playerFaction.addAlly(pf.getUuid());
                        pf.addAlly(playerFaction.getUuid());
                        playerFaction.save();
                        pf.save();

                        pf.sendMessage(Locale.COMMAND_FACTION_ALLY_ACCEPTED.toString().replace("%faction%", playerFaction.getName()));
                        playerFaction.sendMessage(Locale.COMMAND_FACTION_ALLY_ACCEPTED.toString().replace("%faction%", pf.getName()));
                        return;
                    }

                    pf.addRequestedAlly(playerFaction.getUuid());
                    pf.save();
                    pf.sendMessage(Locale.COMMAND_FACTION_ALLY_SENT.toString().replace("%faction%", playerFaction.getName()));
                    playerFaction.sendMessage(Locale.COMMAND_FACTION_ALLY_PENDING.toString().replace("%faction%", pf.getName()));
                });
            }
        } else {
            p.sendMessage(ChatColor.RED + "Allies are disabled");
        }
    }
}
