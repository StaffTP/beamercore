package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionResetClaimsArgument {

    @Command(label = "f.resetclaims", aliases = {"f.clearclaims"}, permission = "command.resetclaims", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() >= 2) {
            List<Faction> facs = Faction.getAllByName(args.getArg(1));
            if(facs != null && !facs.isEmpty()) {
                facs.forEach(Faction::removeClaims);
                facs.forEach(Faction::save);

                p.sendMessage(Locale.COMMAND_FACTION_RESET_CLAIMS.toString().replace("%name%", facs.get(0).getDisplayString()));
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "resetclaims <faction>"));
        }
    }
}
