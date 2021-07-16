package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FactionSetColorArgument {

    @Command(label = "f.setcolor", permission = "command.setcolor", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 3) {
            Faction fac = Faction.getByName(args.getArg(1));
            if(fac != null) {
                SystemFaction sf = (SystemFaction) fac;

                ChatColor color;
                try {
                    color = ChatColor.valueOf(args.getArg(2).toUpperCase());
                } catch(IllegalArgumentException ex) {
                    p.sendMessage(Locale.COMMAND_FACTION_SETCOLOR_INVALID.toString().replace("%invalid%", args.getArg(2)));
                    return;
                }

                sf.setColor(color);
                sf.save();

                p.sendMessage(Locale.COMMAND_FACTION_SETCOLOR_SET.toString().replace("%name%", sf.getName()).replace("%color%", color + WordUtils.capitalizeFully(color.name())));
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "setcolor <color>"));
        }
    }
}
