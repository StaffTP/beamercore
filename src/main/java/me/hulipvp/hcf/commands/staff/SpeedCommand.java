package me.hulipvp.hcf.commands.staff;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpeedCommand {

    @Command(label = "flyspeed", permission = "command.flyspeed", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        if (args.getArgs().length == 1) {

            if (!isStringInteger(args.getArg(0))) {
                player.sendMessage(ChatColor.RED + "You must enter a number");
                return;
            }

            int amount = Integer.parseInt(args.getArg(0));

            if (amount < 1 || amount > 10) {
                player.sendMessage(ChatColor.RED + "You must enter a number between 10");
                return;
            }

            player.setFlySpeed(amount * 0.1F);
            player.sendMessage(ChatColor.GREEN + "Success");
        } else {
            player.sendMessage(ChatColor.RED + "/flyspeed 1-10");
        }
    }

    public static boolean isStringInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
