package me.hulipvp.hcf.commands.member;

import com.google.common.base.Joiner;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RequestCommand {

    @Command(label = "request", aliases = { "helpop" }, playerOnly = true)
    public void onRequest(CommandData args) {
        Player player = (Player) args.getSender();
        if(args.length() == 0) {
            player.sendMessage(Locale.COMMAND_REQUEST_USAGE.toString());
            return;
        }

        HCFProfile profile = HCFProfile.getByPlayer(player);
        if(System.currentTimeMillis() - profile.getLastRequestTime() < 8000) {
            player.sendMessage(Locale.COMAMND_REQUEST_COOLDOWN.toString());
            return;
        }

        String message = Locale.COMMAND_REQUEST_BROADCAST.toString().replace("%sender%", player.getName()).replace("%message%", Joiner.on(' ').join(args.getArgs()));
        Bukkit.getOnlinePlayers().stream()
                .filter(staff -> staff.hasPermission("hcf.staff"))
                .forEach(staff -> staff.sendMessage(message));

        player.sendMessage(Locale.COMMAND_REQUEST_SUCCESS.toString());
        profile.setLastRequestTime(System.currentTimeMillis());
    }

}
