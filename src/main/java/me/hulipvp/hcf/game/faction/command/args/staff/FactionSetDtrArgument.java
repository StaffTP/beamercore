package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionSetDtrArgument {

    @Command(label = "f.setdtr", permission = "command.setdtr", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 3) {
            Faction fac = Faction.getByName(args.getArg(1));

            if(fac != null) {
                if(fac instanceof PlayerFaction) {
                    PlayerFaction pf = (PlayerFaction) fac;

                    try {
                        double newDtr = Double.parseDouble(args.getArg(2));

                        if(newDtr > pf.getMaxDtr()) {
                            newDtr = pf.getMaxDtr();
                        }

                        pf.setDtr(newDtr);
                        pf.save();
                        p.sendMessage(Locale.COMMAND_FACTION_SETDTR_SET.toString().replace("%name%", pf.getName()).replace("%dtr%", String.valueOf(newDtr)));
                        pf.sendMessage(Locale.COMMAND_FACTION_SETDTR_SET_BC.toString().replace("%name%", pf.getName()).replace("%dtr%", String.valueOf(newDtr)));
                    } catch(NumberFormatException e) {
                        p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
                    }
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_PLAYER.toString());
                }
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "setdtr <name> <dtr>"));
        }
    }
}
