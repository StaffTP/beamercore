package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class FactionSetRegenArgument {

    @Command(label = "f.setregen", permission = "command.setregen", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 3) {
            Faction fac = Faction.getByName(args.getArg(1));

            if(fac != null) {
                if(fac instanceof PlayerFaction) {
                    PlayerFaction pf = (PlayerFaction) fac;

                    try {
                        int seconds = Integer.parseInt(args.getArg(2));
                        if(seconds > 3600)
                            seconds = 3600;

                        pf.setStartRegen(new Timestamp(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds)));
                        pf.setupRegenTask();
                        pf.save();
                        p.sendMessage(Locale.COMMAND_FACTION_SETREGEN_SET.toString().replace("%name%", pf.getName()).replace("%minutes%", String.valueOf(TimeUnit.SECONDS.toMinutes(seconds))));
                        pf.sendMessage(Locale.COMMAND_FACTION_SETREGEN_SET_BC.toString().replace("%name%", pf.getName()).replace("%minutes%", String.valueOf(TimeUnit.SECONDS.toMinutes(seconds))));
                    } catch(NumberFormatException e) {
                        p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(2)));
                    }
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_PLAYER.toString());
                }
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "setregen <name> <seconds>"));
        }
    }
}
