package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class RestartCommand {

    @Command(label = "corerestart", permission = "command.restart")
    public void onRestart(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 1) {
            sendUsage(sender);
        } else {
            ServerTimer timer = ServerTimer.getRestart();
            if(timer != null) {
                sender.sendMessage(Locale.COMMAND_RESTART_RUNNING.toString());
                return;
            }

            long expiry = -1;
            try {
                expiry = TimeUtils.timeFromString(args.getArg(0), true);
            } catch(Exception ex) { }

            if(expiry <= 0) {
                sender.sendMessage(C.color("&cInvalid time. Ex: 10m"));
                return;
            }

            long length = expiry - System.currentTimeMillis();

            timer = new ServerTimer(ServerTimerType.RESTART);
            timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
            timer.add();
        }
    }

    @Command(label = "corerestart.pause", permission = "command.restart")
    public void onPause(CommandData args) {
        CommandSender sender = args.getSender();
        ServerTimer timer = ServerTimer.getRestart();
        if(timer == null) {
            sender.sendMessage(Locale.COMMAND_RESTART_NOT_RUNNING.toString());
            return;
        }

        timer.setPaused(!timer.isPaused());
        sender.sendMessage(Locale.COMMAND_RESTART_PAUSED.toString().replace("%status%", timer.isPaused() ? "&apaused" : "&aunpaused"));
    }

    @Command(label = "corerestart.cancel", permission = "command.restart")
    public void onCancel(CommandData args) {
        CommandSender sender = args.getSender();
        ServerTimer timer = ServerTimer.getRestart();
        if(timer == null) {
            sender.sendMessage(Locale.COMMAND_RESTART_NOT_RUNNING.toString());
            return;
        }

        timer.cancel();
        sender.sendMessage(Locale.COMMAND_RESTART_CANCELLED.toString());
    }

    @Command(label = "corerestart.force", permission = "command.restart")
    public void onForce(CommandData args) {
        restartServer();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Locale.COMMAND_RESTART_USAGE.toString());
    }

    public static void restartServer() {
        TaskUtils.runSync(() -> {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer(Locale.COMMAND_RESTART_KICK_MESSAGE.toString());
                }
            }
        );

        TaskUtils.runSync(Bukkit::shutdown);
    }
}
