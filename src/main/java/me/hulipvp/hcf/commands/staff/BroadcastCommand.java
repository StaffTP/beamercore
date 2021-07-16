package me.hulipvp.hcf.commands.staff;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.CC;
import org.bukkit.Bukkit;

public class BroadcastCommand {

    @Command(label = "broadcast", aliases = {"bc"}, permission = "command.broadcast")
    public void onCommand(CommandData args) {
        if (args.getArgs().length == 0) {
            args.getSender().sendMessage(CC.translate("&cUsage: /broadcast <text>"));
        } else {

            StringBuilder message = new StringBuilder();
            for (int i = 0; i < args.getArgs().length; i++) {
                message.append(args.getArgs()[i]).append(" ");
            }

            Bukkit.broadcastMessage(CC.translate(Locale.COMMAND_BROADCAST_PREFIX.toString() + message.toString()));
        }

        return;
    }
}
