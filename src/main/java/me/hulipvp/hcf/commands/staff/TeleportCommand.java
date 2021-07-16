package me.hulipvp.hcf.commands.staff;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommand {

    @Command(label = "tp", aliases = { "teleport", "tppos" }, permission = "command.tp", playerOnly = true)
    public void onTp(CommandData args) {
        Player player = (Player) args.getSender();

        if(args.length() == 1) {
            Player target = Bukkit.getPlayerExact(args.getArg(0));
            if(target != null) {
                player.teleport(target.getLocation());
                player.sendMessage(Locale.TELEPORT_PLAYER_ONLINE.toString().replace("%name%", target.getName()));
            } else {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            }
        } else if(args.length() == 3) {
            Integer x = null;
            Integer y = null;
            Integer z = null;

            try {
                x = Integer.parseInt(args.getArg(0));
                y = Integer.parseInt(args.getArg(1));
                z = Integer.parseInt(args.getArg(2));
            } catch(NumberFormatException e) {
                String number = ((x == null) ? args.getArg(0) : ((y == null) ? args.getArg(1) : args.getArg(2)));
                player.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", number));
                return;
            }

            player.teleport(new Location(player.getWorld(), x, y, z));
            player.sendMessage(Locale.TELEPORT_LOCATION.toString().replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z)));
        } else {
            player.sendMessage(C.color("&cUsage: /tp <player> OR /tp <x> <y> <z>"));
        }
    }

    @Command(label = "tphere", aliases = { "teleporthere", "s" }, permission = "command.tphere", playerOnly = true)
    public void onTpHere(CommandData args) {
        Player player = (Player) args.getSender();

        if(args.length() == 1) {
            Player target = Bukkit.getPlayerExact(args.getArg(0));
            if(target != null) {
                target.teleport(player.getLocation());
                player.sendMessage(Locale.TELEPORT_PLAYER_HERE.toString().replace("%name%", target.getName()).replace("%you%", player.getName()));
            } else {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            }
        } else {
            player.sendMessage(C.color("&cUsage: /tphere <player>"));
        }
    }

    @Command(label = "tpall", aliases = { "teleportall" }, permission = "command.tpall", playerOnly = true)
    public void onTpAll(CommandData args) {
        Player player = args.getPlayer();
        player.sendMessage(Locale.TELEPORT_ALL.toString());

        for(Player other : Bukkit.getOnlinePlayers()) {
            if(other == player)
                continue;

            other.teleport(player);
        }
    }
}
