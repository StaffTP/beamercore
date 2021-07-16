package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionAddPointsArgument {

    @Command(label = "f.addpoints", permission = "command.addpoints", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 3) {
            TaskUtils.runAsync(() -> {
                List<Faction> factions = Faction.findFactions(args.getArg(1));
                if(factions != null && !factions.isEmpty()) {
                    Faction fac = factions.get(0);
                    if(fac instanceof PlayerFaction) {
                        PlayerFaction pf = (PlayerFaction) fac;
                        try {
                            pf.setPoints(pf.getPoints() + Integer.parseInt(args.getArg(2)));
                            pf.save();
                            p.sendMessage(Locale.COMMAND_FACTION_ADDPOINTS_ADDED.toString().replace("%name%", pf.getName()).replace("%points%", String.valueOf(Integer.parseInt(args.getArg(2)))));
                        } catch(NumberFormatException e) {
                            p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
                        }
                    } else {
                        p.sendMessage(Locale.COMMAND_FACTION_NOT_PLAYER.toString());
                    }
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
                }
            });
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "addpoints <name> <points>"));
        }
    }
}
