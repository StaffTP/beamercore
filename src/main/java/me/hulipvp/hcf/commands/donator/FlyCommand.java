package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlyCommand {

    @Command(label = "fly", permission = "command.fly", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = args.getPlayer();

        if (args.getArgs().length == 0) {
            Faction locFaction = Faction.getByLocation(player.getLocation());
            Faction ownFaction = Faction.getByPlayer(player.getName());
            if (player.hasPermission("hcf.staff")) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.sendMessage(Locale.COMMAND_FLY_DISABLED.toString());
                } else {
                    player.setAllowFlight(true);
                    player.sendMessage(Locale.COMMAND_FLY_ENABLED.toString());
                }
                return;
            }

            if (locFaction != null) {
                if (locFaction instanceof SafezoneFaction) {
                    if (player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        player.sendMessage(Locale.COMMAND_FLY_DISABLED.toString());
                    } else {
                        player.setAllowFlight(true);
                        player.sendMessage(Locale.COMMAND_FLY_ENABLED.toString());
                    }
                } else if (locFaction.equals(ownFaction)) {
                    if (player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        player.sendMessage(Locale.COMMAND_FLY_DISABLED.toString());
                    } else {
                        player.setAllowFlight(true);
                        player.sendMessage(Locale.COMMAND_FLY_ENABLED.toString());
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You can only do this in spawn or in your own claim");
                }
            }
        } else {
            if (!player.hasPermission("command.fly.other")) {
                player.sendMessage(Locale.NO_PERMISSION.toString());
                return;
            }

            Player target = Bukkit.getPlayer(args.getArg(0));

            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }

            if (target.getAllowFlight()) {
                target.setAllowFlight(false);
                target.sendMessage(Locale.COMMAND_FLY_DISABLED_OTHER_TARGET.toString().replace("%name%", player.getName()));
                player.sendMessage(Locale.COMMAND_FLY_DISABLED_OTHER.toString().replace("%name%", target.getName()));
            } else {
                target.setAllowFlight(true);
                target.sendMessage(Locale.COMMAND_FLY_ENABLED_OTHER_TARGET.toString().replace("%name%", player.getName()));
                player.sendMessage(Locale.COMMAND_FLY_ENABLED_OTHER.toString().replace("%name%", target.getName()));
            }

        }
    }
}
