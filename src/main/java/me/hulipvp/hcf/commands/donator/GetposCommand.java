package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GetposCommand {

    @Command(label = "getpos", permission = "command.getpos", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour position is: &fX: " + player.getLocation().getX() + ", Z: " + player.getLocation().getZ()));
        return;
    }
}
