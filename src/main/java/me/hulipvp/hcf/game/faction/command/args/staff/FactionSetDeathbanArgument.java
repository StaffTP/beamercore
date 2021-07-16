package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionSetDeathbanArgument {

    @Command(label = "f.setdeathban", permission = "command.setdeathban", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 3) {
            Faction fac = Faction.getByName(args.getArg(1));
            if(fac != null) {
                boolean deathban = Boolean.valueOf(args.getArg(2));

                fac.setDeathban(deathban);
                fac.save();

                p.sendMessage(Locale.COMMAND_FACTION_SETDEATHBAN_SET.toString().replace("%name%", fac.getName()).replace("%status%", deathban ? Locale.FACTION_DEATHBAN.toString() : Locale.FACTION_NONDEATHBAN.toString()));
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "setdeath <value>"));
        }
    }
}
