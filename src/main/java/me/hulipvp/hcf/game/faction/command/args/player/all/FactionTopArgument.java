package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FactionTopArgument {

    @Command(label = "f.top", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        int index = 1;
        p.sendMessage(C.color("&7&m----------------------------------------------------"));
        p.sendMessage(C.color("&9Faction Top"));
        if (PlayerFaction.getSortedFactionTop().size() == 0) {
            p.sendMessage(C.color("&cNo factions"));
        } else {
            for(Faction faction : PlayerFaction.getSortedFactionTop()) {
                PlayerFaction pf = (PlayerFaction) faction;

                p.sendMessage(C.color("&7" + index + ". " + ChatColor.YELLOW + pf.getRelationColor(p) + pf.getName() + ": &f" + pf.getPoints() + " Points"));
                index++;
            }
        }
        p.sendMessage(C.color("&7"));
        p.sendMessage(C.color("&7&o(Refreshes & resorts every 10 seconds)"));
        p.sendMessage(C.color("&7&m----------------------------------------------------"));
    }
}
