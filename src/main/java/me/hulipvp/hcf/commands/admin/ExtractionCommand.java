package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

public class ExtractionCommand {

    @Command(label = "extraction", permission = "command.extraction")
    public void onCommand(CommandData args) {
        args.getSender().sendMessage(Locale.COMMAND_EXTRACTION_USAGE.toString());
    }

    @Command(label = "extraction.start", permission = "command.extraction")
    public void onStart(CommandData args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(Locale.COMMAND_EXTRACTION_USAGE.toString());
            return;
        }

        boolean foundTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .anyMatch(timer -> timer.getType() == ServerTimerType.EXTRACTION);

        if(foundTimer) {
            args.getSender().sendMessage(Locale.COMMAND_EXTRACTION_ALREADY_STARTED.toString());
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

        ServerTimer timer = new ServerTimer(ServerTimerType.EXTRACTION);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_EXTRACTION_STARTED.toString());
    }

    @Command(label = "extraction.stop", permission = "command.extraction")
    public void onStop(CommandData args) {
        ServerTimer extractionTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .filter(timer -> timer.getType() == ServerTimerType.EXTRACTION)
                .findFirst()
                .orElse(null);

        if(extractionTimer == null) {
            args.getSender().sendMessage(Locale.COMMAND_EXTRACTION_NOT_RUNNING.toString());
            return;
        }

        Timer.getTimers().remove(extractionTimer.getUuid());
        Bukkit.broadcastMessage(Locale.COMMAND_EXTRACTION_STOPPED.toString());
    }

    @Command(label = "extraction.pause", permission = "command.extraction")
    public void onPause(CommandData args) {
        ServerTimer extractionTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .filter(timer -> timer.getType() == ServerTimerType.EXTRACTION)
                .findFirst()
                .orElse(null);

        if(extractionTimer == null) {
            args.getSender().sendMessage(Locale.COMMAND_EXTRACTION_NOT_RUNNING.toString());
            return;
        }

        if (extractionTimer.isPaused()) {
            extractionTimer.setPaused(false);
            Bukkit.broadcastMessage(Locale.COMMAND_EXTRACTION_UNPAUSED.toString());
        } else {
            extractionTimer.setPaused(true);
            Bukkit.broadcastMessage(Locale.COMMAND_EXTRACTION_PAUSED.toString());
        }
    }
}
