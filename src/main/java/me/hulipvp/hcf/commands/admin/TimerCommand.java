package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TimerCommand {

    @Command(label = "timer", permission = "command.timer")
    public void onTimer(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 3) {
            sender.sendMessage(Locale.COMMAND_TIMER_USAGE.toString());
        }
    }

    @Command(label = "timer.set", permission = "command.timer")
    public void onTimerSet(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 4) {
            sender.sendMessage(Locale.COMMAND_TIMER_USAGE.toString());
            return;
        }

        if (Bukkit.getPlayer(args.getArg(1)) == null) {
            sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(2)));
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(1));



        PlayerTimerType playerTimerType = null;

        for (PlayerTimerType pTimerType : PlayerTimerType.values()) {
            if (pTimerType.name().toLowerCase().replace("_", "").equals(args.getArg(2).toLowerCase())) playerTimerType = pTimerType;
        }

        if (playerTimerType == null) {
            sender.sendMessage(Locale.COMMAND_TIMER_WRONG.toString());
            return;
        }

        long expiry = -1;
        try {
            expiry = TimeUtils.timeFromString(args.getArg(3), true);
        } catch(Exception ex) { }

        if(expiry <= 0) {
            sender.sendMessage(C.color("&cInvalid time. Ex: 10m or 2h"));
            return;
        }

        long length = expiry - System.currentTimeMillis();

        HCFProfile targetProfile = HCFProfile.getByPlayer(target);
        if (targetProfile != null && targetProfile.hasTimer(playerTimerType)) {
            targetProfile.removeTimersByType(playerTimerType);
        }

        PlayerTimer timer = new PlayerTimer(target, playerTimerType);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.add();

        sender.sendMessage(Locale.COMMAND_TIMER_STARTED.toString().replace("%name%", target.getName()).replace("%timer%", args.getArg(2)));
    }

    @Command(label = "timer.remove", permission = "command.timer")
    public void onTimerRemove(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 3) {
            sender.sendMessage(Locale.COMMAND_TIMER_USAGE.toString());
            return;
        }

        if (Bukkit.getPlayer(args.getArg(1)) == null) {
            sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(1));



        PlayerTimerType playerTimerType = null;

        for (PlayerTimerType pTimerType : PlayerTimerType.values()) {
            if (pTimerType.name().toLowerCase().replace("_", "").equals(args.getArg(2).toLowerCase())) playerTimerType = pTimerType;
        }

        if (playerTimerType == null) {
            sender.sendMessage(Locale.COMMAND_TIMER_WRONG.toString());
            return;
        }

        HCFProfile targetProfile = HCFProfile.getByPlayer(target);
        if (targetProfile != null && targetProfile.hasTimer(playerTimerType)) {
            targetProfile.removeTimersByType(playerTimerType);
            sender.sendMessage(Locale.COMMAND_TIMER_STOPPED.toString().replace("%name%", args.getArg(1)).replace("%timer%", args.getArg(2)));
        } else {
            sender.sendMessage(Locale.COMMAND_TIMER_NOT_RUNNING.toString().replace("%name%", args.getArg(1)));
        }

    }

    @Command(label = "sale", permission = "command.sale")
    public void onSale(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 1) {
            sender.sendMessage(Locale.COMMAND_SALE_USAGE.toString());
            return;
        }

        ServerTimer keySale = getSale();
        if(keySale != null) {
            sender.sendMessage(Locale.COMMAND_SALE_RUNNING.toString());
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

        ServerTimer timer = new ServerTimer(ServerTimerType.SALE);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_SALE_STARTED.toString());
    }

    @Command(label = "sale.stop", permission = "command.sale")
    public void onSaleStop(CommandData args) {
        CommandSender sender = args.getSender();
        ServerTimer keySale = getSale();
        if(keySale == null) {
            sender.sendMessage(Locale.COMMAND_SALE_NOT_RUNNING.toString());
            return;
        }

        Timer.getTimers().remove(keySale.getUuid());

        Bukkit.broadcastMessage(Locale.COMMAND_SALE_STOPPED.toString());
    }

    private ServerTimer getSale() {
        return ServerTimer.getTimer(ServerTimerType.SALE);
    }

    @Command(label = "keysale", permission = "command.keysale")
    public void onKeySale(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 1) {
            sender.sendMessage(Locale.COMMAND_KEY_SALE_USAGE.toString());
            return;
        }

        ServerTimer keySale = getKeySale();
        if(keySale != null) {
            sender.sendMessage(Locale.COMMAND_KEY_SALE_RUNNING.toString());
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

        ServerTimer timer = new ServerTimer(ServerTimerType.KEY_SALE);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_KEY_SALE_STARTED.toString());
    }

    @Command(label = "keysale.stop", permission = "command.keysale")
    public void onKeySaleStop(CommandData args) {
        CommandSender sender = args.getSender();
        ServerTimer keySale = getKeySale();
        if(keySale == null) {
            sender.sendMessage(Locale.COMMAND_KEY_SALE_NOT_RUNNING.toString());
            return;
        }

        Timer.getTimers().remove(keySale.getUuid());

        Bukkit.broadcastMessage(Locale.COMMAND_KEY_SALE_STOPPED.toString());
    }

    private ServerTimer getKeySale() {
        return ServerTimer.getTimer(ServerTimerType.KEY_SALE);
    }

    @Command(label = "keyall", permission = "command.keyall")
    public void onKeyAll(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 1) {
            sender.sendMessage(Locale.COMMAND_KEY_ALL_USAGE.toString());
            return;
        }

        ServerTimer keySale = getKeyAll();
        if(keySale != null) {
            sender.sendMessage(Locale.COMMAND_KEY_ALL_RUNNING.toString());
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

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.getArgs().length; i++) {
            message.append(args.getArgs()[i]).append(" ");
        }

        ServerTimer timer = new ServerTimer(ServerTimerType.KEY_ALL);
        timer.setNewLength(TimeUnit.MILLISECONDS.toSeconds(length));
        timer.setCommand(message.toString());
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_KEY_ALL_STARTED.toString());
    }

    @Command(label = "keyall.stop", permission = "command.keyall")
    public void onKeyAllStop(CommandData args) {
        CommandSender sender = args.getSender();
        ServerTimer keySale = getKeyAll();
        if(keySale == null) {
            sender.sendMessage(Locale.COMMAND_KEY_ALL_NOT_RUNNING.toString());
            return;
        }

        Timer.getTimers().remove(keySale.getUuid());

        Bukkit.broadcastMessage(Locale.COMMAND_KEY_ALL_STOPPED.toString());
    }

    private ServerTimer getKeyAll() {
        return ServerTimer.getTimer(ServerTimerType.KEY_ALL);
    }
}
