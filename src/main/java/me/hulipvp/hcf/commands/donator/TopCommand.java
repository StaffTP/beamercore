package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TopCommand {

    @Command(label = "top", permission = "command.top", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        if (args.getArgs().length == 0) {

            Faction faction = HCFProfile.getByPlayer(player).getFactionObj();
            if (faction == null && !player.hasPermission("hcf.staff")) {
                player.sendMessage(ChatColor.RED + "You need a faction for this, you can only do it in your own claim");
                return;
            }

            Faction locFaction = Faction.getByLocation(player.getLocation());
            if (locFaction == null && !player.hasPermission("hcf.staff")) {
                player.sendMessage(ChatColor.RED + "You need a faction for this, you can only do it in your own claim");
                return;
            }

            if (!locFaction.equals(faction) && !player.hasPermission("hcf.staff")) {
                player.sendMessage(ChatColor.RED + "You can only do this in your own claim");
                return;
            }
            player.teleport(teleportToTop(player.getLocation()));
        }
    }

    public Location teleportToTop(Location location) {
        return new Location(location.getWorld(), location.getX(), location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()), location.getZ(), location.getYaw(), location.getPitch());
    }
}
