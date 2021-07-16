package me.hulipvp.hcf.commands.staff;

import me.hulipvp.hcf.listeners.CrowbarListener;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrowbarCommand {

    @Command(label = "crowbar", permission = "command.crowbar")
    public void onCommand(CommandData args) {
        CommandSender sender = args.getSender();

        if(args.length() == 0) {
            if(sender instanceof Player)
                giveCrowbar((Player) sender);
            else
                sender.sendMessage(C.color("&cUsage: /crowbar <player>"));
        } else {
            Player player = Bukkit.getPlayer(args.getArg(0));
            if(player == null || !player.isOnline()) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }

            giveCrowbar(player);
            sender.sendMessage(C.color("&aGave '" + player.getName() + "' a crowbar."));
        }
    }

    private void giveCrowbar(Player player) {
        player.getInventory().addItem(CrowbarListener.getCrowbar());
        player.updateInventory();
    }
}
