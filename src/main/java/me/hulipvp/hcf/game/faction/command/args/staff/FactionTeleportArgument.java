package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionTeleportArgument {

    @Command(label = "f.teleport", aliases = {"f.tp"}, permission = "command.teleport", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 2) {
            Faction faction = Faction.getByName(args.getArg(1));
            if(faction != null) {
                if(faction.getHome() == null) {
                    p.sendMessage(Locale.COMMAND_FACTION_HOME_NOT_SET.toString());
                } else {
                    p.teleport(faction.getHome());
                    p.sendMessage(Locale.FACTION_WARPING.toString().replace("%faction%", faction.getName()));
                }
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "teleport <name>"));
        }
    }
}
