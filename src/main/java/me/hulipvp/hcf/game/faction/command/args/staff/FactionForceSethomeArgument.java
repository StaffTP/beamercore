package me.hulipvp.hcf.game.faction.command.args.staff;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionForceSethomeArgument {

    @Command(label = "f.forcesethome", aliases = {"f.fsh"}, permission = "command.forcesethome", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 2) {
            Faction faction = Faction.getByName(args.getArg(1));

            if(faction != null) {
                if(Faction.isInsideFactionTerritory(faction, p.getLocation())) {
                    faction.setHome(p.getLocation());
                    if(faction.getName().equalsIgnoreCase("Spawn"))
                        p.getWorld().setSpawnLocation(p.getLocation().getBlockX(), p.getLocation().getBlockY() + 1, p.getLocation().getBlockZ());

                    if(faction instanceof PlayerFaction) {
                        PlayerFaction pf = (PlayerFaction) faction;
                        pf.sendMessage(Locale.COMMAND_FACTION_HOME_UPDATED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));

                        if(pf.getMembers().containsKey(p.getUniqueId()))
                            p.sendMessage(Locale.COMMAND_FACTION_HOME_UPDATED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
                    } else {
                        p.sendMessage(Locale.COMMAND_FACTION_HOME_UPDATED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
                    }
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_CANNOT_SET_HOME.toString());
                }
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "forcesethome <name>"));
        }
    }

}
