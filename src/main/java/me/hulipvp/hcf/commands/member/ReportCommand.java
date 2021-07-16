package me.hulipvp.hcf.commands.member;

import com.google.common.base.Joiner;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReportCommand {

    @Command(label = "report", playerOnly = true)
    public void onReport(CommandData args) {
        Player player = (Player) args.getSender();
        if(args.length() < 2) {
            player.sendMessage(Locale.COMMAND_REPORT_USAGE.toString());
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if(target == null) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }

        if(target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Locale.COMMAND_REPORT_SELF.toString());
            return;
        }

        HCFProfile profile = HCFProfile.getByPlayer(player);
        if(System.currentTimeMillis() - profile.getLastReportTime() < 8000) {
            player.sendMessage(Locale.COMMAND_REPORT_COOLDOWN.toString());
            return;
        }

        String reason = Joiner.on(' ').join(args.getArgs()).replaceFirst(args.getArg(0), "").replaceFirst(" ", "");
        String message = Locale.COMMAND_REPORT_BROADCAST.toString().replace("%reported%", target.getName()).replace("%reporter%", player.getName()).replace("%reason%", reason);
        Bukkit.getOnlinePlayers().stream()
                .filter(staff -> staff.hasPermission("hcf.staff"))
                .forEach(staff -> staff.sendMessage(message));

        player.sendMessage(Locale.COMMAND_REPORT_SUCCESS.toString());
        profile.setLastReportTime(System.currentTimeMillis());
    }

}
