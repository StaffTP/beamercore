package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearCommand {

    @Command(label = "clear", aliases = {"ci", "clearinventory"}, permission = "command.clear", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();

        if (args.getArgs().length == 0) {
            clear(player);
            player.sendMessage(Locale.COMMAND_CLEAR_CLEARED.toString());
        } else {
            if (!player.hasPermission("hcf.command.clear.others")) {
                player.sendMessage(Locale.NO_PERMISSION.toString());
                return;
            }

            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }

            clear(target);
            player.sendMessage(Locale.COMMAND_CLEAR_CLEARED_OTHER.toString().replace("%name%", args.getArg(0)));
            player.sendMessage(Locale.COMMAND_CLEAR_CLEARED_OTHER_TARGET.toString().replace("%name%", player.getName()));
        }
        return;
    }

        public void clear(Player player) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

        }
}
