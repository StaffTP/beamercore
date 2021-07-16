package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class SotwCommand {

    @Command(label = "sotw", permission = "command.sotw")
    public void onSotw(CommandData args) {
        CommandSender sender = args.getSender();
        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        sender.sendMessage(Locale.COMMAND_SOTW_USAGE.toString());
    }

    @Command(label = "sotw.start", permission = "command.sotw.start")
    public void onStart(CommandData args) {
        CommandSender sender = args.getSender();
        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        ServerTimer sotw = getSotw();
        if(sotw != null) {
            sender.sendMessage(Locale.COMMAND_SOTW_RUNNING.toString());
            return;
        }

        if(args.length() < 2) {
            sender.sendMessage(Locale.COMMAND_SOTW_USAGE.toString());
            return;
        }


        long expiry = -1;
        try {
            expiry = TimeUtils.timeFromString(args.getArg(1), true);
        } catch(Exception ex) { }

        if(expiry <= 0) {
            sender.sendMessage(C.color("&cInvalid time. Ex: 10m"));
            return;
        }

        long length = expiry - System.currentTimeMillis();

        ServerTimer timer = new ServerTimer(ServerTimerType.SOTW);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_SOTW_STARTED.toString());
    }

    @Command(label = "sotw.stop", permission = "command.sotw.stop")
    public void onStop(CommandData args) {
        CommandSender sender = args.getSender();

        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        ServerTimer sotw = getSotw();
        if(sotw == null) {
            sender.sendMessage(Locale.COMMAND_SOTW_NOT_RUNNING.toString());
            return;
        }

        Timer.getTimers().remove(sotw.getUuid());

        Bukkit.broadcastMessage(Locale.COMMAND_SOTW_STOPPED.toString());
    }

    @Command(label = "sotw.add", permission = "command.sotw.add")
    public void onAdd(CommandData args) {
        CommandSender sender = args.getSender();

        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        ServerTimer sotw = getSotw();
        if(sotw == null) {
            sender.sendMessage(Locale.COMMAND_SOTW_NOT_RUNNING.toString());
            return;
        }

        long expiry = -1;
        try {
            expiry = TimeUtils.timeFromString(args.getArg(1), true);
        } catch(Exception ex) { }

        if(expiry <= 0) {
            sender.sendMessage(C.color("&cInvalid time. Ex: 10m"));
            return;
        }

        long length = expiry - System.currentTimeMillis();

        Timer.getTimers().remove(sotw.getUuid());
        ServerTimer timer = new ServerTimer(ServerTimerType.SOTW);
        timer.setNewLength(sotw.getTimeRemaining() / 1000 + length / 1000);
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_SOTW_ADDED.toString());
    }

    @Command(label = "sotw.enable", permission = "command.sotw.enable")
    public void onEnable(CommandData args) {
        CommandSender sender = args.getSender();
        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        ServerTimer sotw = getSotw();
        if(sotw == null) {
            sender.sendMessage(Locale.COMMAND_SOTW_NOT_RUNNING.toString());
            return;
        }

        HCFProfile profile = HCFProfile.getByPlayer((Player) sender);

        profile.setSotwPvp(true);

        sender.sendMessage(Locale.COMMAND_SOTW_PLAYER_PVP_ENABLED.toString());
    }

    @Command(label = "sotw.disable", permission = "command.sotw.disable")
    public void onDisable(CommandData args) {
        CommandSender sender = args.getSender();
        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        ServerTimer sotw = getSotw();
        if(sotw == null) {
            sender.sendMessage(Locale.COMMAND_SOTW_NOT_RUNNING.toString());
            return;
        }

        HCFProfile profile = HCFProfile.getByPlayer((Player) sender);

        boolean hasCombat = profile.hasTimer(PlayerTimerType.COMBAT);
        if (hasCombat) {
            sender.sendMessage(Locale.IN_COMBAT.toString());
            return;
        }

        profile.setSotwPvp(false);

        sender.sendMessage(Locale.COMMAND_SOTW_PLAYER_PVP_DISABLED.toString());
    }

    @Command(label = "sotw.pvp", permission = "command.sotw.pvp")
    public void onPvp(CommandData args) {
        CommandSender sender = args.getSender();
        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        ServerTimer sotw = getSotw();
        if(sotw == null) {
            sender.sendMessage(Locale.COMMAND_SOTW_NOT_RUNNING.toString());
            return;
        }

        sotw.setPvp(!sotw.isPvp());

        if (sotw.isPvp()) Bukkit.broadcastMessage(Locale.COMMAND_SOTW_PVP_ENABLED.toString());
        else Bukkit.broadcastMessage(Locale.COMMAND_SOTW_PVP_DISABLED.toString());
    }

    @Command(label = "sotw.pause", permission = "command.sotw.pause")
    public void onPause(CommandData args) {
        CommandSender sender = args.getSender();
        if(ConfigValues.LISTENERS_STARTING_TIMER) {
            sender.sendMessage(C.color("&cPlease disable the 'starting-timer' option in the config.yml in order to use /sotw"));
            return;
        }

        ServerTimer sotw = getSotw();
        if(sotw == null) {
            sender.sendMessage(Locale.COMMAND_SOTW_NOT_RUNNING.toString());
            return;
        }

        sotw.setPaused(!sotw.isPaused());

        if(sotw.isPaused())
            Bukkit.broadcastMessage(Locale.COMMAND_SOTW_PAUSED.toString());
        else
            Bukkit.broadcastMessage(Locale.COMMAND_SOTW_UNPAUSED.toString());
    }

    private ServerTimer getSotw() {
        return ServerTimer.getTimer(ServerTimerType.SOTW);
    }
}
