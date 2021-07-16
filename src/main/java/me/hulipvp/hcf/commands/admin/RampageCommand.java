package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.game.timer.type.server.type.RampageTimer;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RampageCommand {

    @Command(label = "rampage", permission = "command.rampage")
    public void onCommand(CommandData args) {
        args.getSender().sendMessage(Locale.COMMAND_RAMPAGE_USAGE.toString());
    }

    @Command(label = "rampage.start", permission = "command.rampage")
    public void onStart(CommandData args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(Locale.COMMAND_RAMPAGE_USAGE.toString());
            return;
        }

        boolean foundTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .anyMatch(timer -> timer.getType() == ServerTimerType.RAMPAGE);

        if(foundTimer) {
            args.getSender().sendMessage(Locale.COMMAND_RAMPAGE_ALREADY_STARTING.toString());
            return;
        }

        long expiry = -1;
        try {
            expiry = TimeUtils.timeFromString(args.getArg(1), true);
        } catch(Exception ex) { }

        if(expiry <= 0) {
            args.getSender().sendMessage(C.color("&cInvalid time. Ex: 10m"));
            return;
        }

        long length = expiry - System.currentTimeMillis();

        RampageTimer timer = new RampageTimer();
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_RAMPAGE_STARTED.toString());
    }

    @Command(label = "rampage.stop", permission = "command.rampage")
    public void onStop(CommandData args) {
        ServerTimer rampageTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .filter(timer -> timer.getType() == ServerTimerType.RAMPAGE)
                .findFirst()
                .orElse(null);

        if(rampageTimer == null) {
            args.getSender().sendMessage(Locale.COMMAND_RAMPAGE_NOT_RUNNING.toString());
            return;
        }

        Timer.getTimers().remove(rampageTimer.getUuid());
        Bukkit.broadcastMessage(Locale.COMMAND_RAMPAGE_STOPPED.toString());
    }

    @Command(label = "rampage.pause", permission = "command.rampage")
    public void onPause(CommandData args) {
        ServerTimer rampageTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .filter(timer -> timer.getType() == ServerTimerType.RAMPAGE)
                .findFirst()
                .orElse(null);

        if(rampageTimer == null) {
            args.getSender().sendMessage(Locale.COMMAND_RAMPAGE_NOT_RUNNING.toString());
            return;
        }

        if (rampageTimer.isPaused()) {
            rampageTimer.setPaused(false);
            Bukkit.broadcastMessage(Locale.COMMAND_RAMPAGE_UNPAUSED.toString());
        } else {
            rampageTimer.setPaused(true);
            Bukkit.broadcastMessage(Locale.COMMAND_RAMPAGE_PAUSED.toString());
        }
    }

    @Command(label = "rampage.info", aliases = {"rampage.list"}, permission = "command.rampage")
    public void onInfo(CommandData args) {
        ServerTimer serverTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .filter(timer -> timer.getType() == ServerTimerType.RAMPAGE)
                .findFirst()
                .orElse(null);

        if(serverTimer == null) {
            args.getSender().sendMessage(Locale.COMMAND_RAMPAGE_NOT_RUNNING.toString());
            return;
        }

        RampageTimer rampageTimer = (RampageTimer) serverTimer;

        String message = Locale.COMMAND_RAMPAGE_INFO.toString();
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(rampageTimer.getSortedMap().entrySet());
        for (int i = 1; i < 6; i++) {
            if (i > sortedList.size()) {
                message = message.replace("%rampage-top-name-" + i + "%", "Unknown")
                                 .replace("%rampage-top-kills-" + i + "%", "Unknown");
            } else {
                message = message.replace("%rampage-top-name-" + i + "%", sortedList.get(i - 1).getKey())
                                 .replace("%rampage-top-kills-" + i + "%", sortedList.get(i - 1).getValue() + "");
            }
        }

        args.getSender().sendMessage(message);
    }
}
