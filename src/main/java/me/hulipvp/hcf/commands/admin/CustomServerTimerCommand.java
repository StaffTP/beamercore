package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.TimeUtils;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class CustomServerTimerCommand {

    @Command(label = "customservertimer", permission = "command.customservertimer")
    public void onCustomTimer(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 3) {
            sender.sendMessage(Locale.COMMAND_CUSTOMSERVERTIMER_USAGE.toString());
        }
    }

    @Command(label = "customservertimer.add", permission = "command.customservertimer")
    public void onCustomTimerSet(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 3) {
            sender.sendMessage(Locale.COMMAND_CUSTOMSERVERTIMER_USAGE.toString());
            return;
        }

        String text = args.getArg(1).replace("_", " ");

        long expiry = -1;
        try {
            expiry = TimeUtils.timeFromString(args.getArg(2), true);
        } catch(Exception ex) { }

        if(expiry <= 0) {
            sender.sendMessage(C.color("&cInvalid time. Ex: 10m or 2h"));
            return;
        }

        long length = expiry - System.currentTimeMillis();

        ServerTimer timer = new ServerTimer(ServerTimerType.CUSTOM);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.setText(text);
        timer.add();

        sender.sendMessage(Locale.COMMAND_CUSTOMSERVERTIMER_STARTED.toString().replace("%timer%", CC.translate(text)));
    }

    @Command(label = "customservertimer.reset", permission = "command.customservertimer")
    public void onCustomTimerRemove(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 2) {
            sender.sendMessage(Locale.COMMAND_CUSTOMSERVERTIMER_USAGE.toString());
            return;
        }

        Timer timer = Timer.getTimers().values().stream().filter(timer1 -> timer1 instanceof ServerTimer && StringUtils.containsIgnoreCase(((ServerTimer) timer1).getText(), args.getArg(0))).findFirst().orElse(null);

        if (timer != null && Timer.getTimers().containsKey(timer.getUuid())) {
            Timer.getTimers().remove(timer.getUuid());
            sender.sendMessage(Locale.COMMAND_CUSTOMSERVERTIMER_REMOVED.toString().replace("%name%", args.getArg(1)));
        } else {
            sender.sendMessage(Locale.COMMAND_CUSTOMSERVERTIMER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
        }

    }
}
