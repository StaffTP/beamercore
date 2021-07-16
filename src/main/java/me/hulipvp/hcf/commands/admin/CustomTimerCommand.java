package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.TimeUtils;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class CustomTimerCommand {

    @Command(label = "customtimer", permission = "command.customtimer")
    public void onCustomTimer(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 3) {
            sender.sendMessage(Locale.COMMAND_CUSTOMTIMER_USAGE.toString());
        }
    }

    @Command(label = "customtimer.add", permission = "command.customtimer")
    public void onCustomTimerSet(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 4) {
            sender.sendMessage(Locale.COMMAND_CUSTOMTIMER_USAGE.toString());
            return;
        }

        if (Bukkit.getPlayer(args.getArg(1)) == null) {
            sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(2)));
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(1));

        String text = args.getArg(2).replace("_", " ");

        long expiry = -1;
        try {
            expiry = TimeUtils.timeFromString(args.getArg(3), true);
        } catch(Exception ex) { }

        if(expiry <= 0) {
            sender.sendMessage(C.color("&cInvalid time. Ex: 10m or 2h"));
            return;
        }

        long length = expiry - System.currentTimeMillis();

        PlayerTimer timer = new PlayerTimer(target, PlayerTimerType.CUSTOM);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.setText(text);
        timer.add();

        sender.sendMessage(Locale.COMMAND_TIMER_STARTED.toString().replace("%name%", target.getName()).replace("%timer%", CC.translate(text)));
    }

    @Command(label = "customtimer.reset", permission = "command.customtimer")
    public void onCustomTimerRemove(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 2) {
            sender.sendMessage(Locale.COMMAND_CUSTOMTIMER_USAGE.toString());
            return;
        }

        if (Bukkit.getPlayer(args.getArg(1)) == null) {
            sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(1));

        HCFProfile targetProfile = HCFProfile.getByPlayer(target);
        if (targetProfile != null) {
            targetProfile.removeTimersByType(PlayerTimerType.CUSTOM);
            sender.sendMessage(Locale.COMMAND_CUSTOMTIMER_REMOVED.toString().replace("%name%", args.getArg(1)));
        }

    }
}
