package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FactionTphereArgument {

    @Command(label = "f.tphere", aliases = {"f.tphere"}, permission = "command.factiontphere", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 2) {
            Faction faction = Faction.getByName(args.getArg(1));
            if(faction != null) {
                PlayerFaction playerFaction = PlayerFaction.getPlayerFaction(faction.getUuid());
                if (playerFaction != null) {
                    for (FactionMember factionMember : playerFaction.getMembers().values()) {
                        Player player = Bukkit.getPlayer(factionMember.getUuid());
                        if (player != null) {
                            player.teleport(p);
                        }
                    }
                }
            } else {
                Player player = Bukkit.getPlayer(args.getArg(1));
                if (player != null) {
                    Faction f = Faction.getByPlayer(player.getName());
                    if(f != null) {
                        PlayerFaction playerFaction = PlayerFaction.getPlayerFaction(f.getUuid());
                        if (playerFaction != null) {
                            for (FactionMember factionMember : playerFaction.getMembers().values()) {
                                Player fplayer = Bukkit.getPlayer(factionMember.getUuid());
                                if (fplayer != null) {
                                    fplayer.teleport(p);
                                }
                            }
                        }
                    } else {
                        p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
                    }
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "tphere <name>"));
        }
    }
}
