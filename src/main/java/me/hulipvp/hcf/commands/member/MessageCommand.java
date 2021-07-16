package me.hulipvp.hcf.commands.member;

import com.google.common.base.Joiner;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MessageCommand {

    @Command(label = "m", aliases = { "msg", "message", "t", "tell", "w", "whisper" }, playerOnly = true)
    public void onMessage(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() < 2) {
            player.sendMessage(Locale.COMMAND_MESSAGE_USAGE.toString().replace("%label%", args.getLabel()));
            return;
        }

        if(args.getArg(0).equalsIgnoreCase(player.getName())) {
            player.sendMessage(Locale.COMMAND_MESSAGE_SELF.toString());
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if(target == null) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }

        HCFProfile messagee = HCFProfile.getByPlayer(player);
        HCFProfile messager = HCFProfile.getByPlayer(target);
        if(!player.hasPermission("hcf.staff") && messager.getVanish() != null && messager.getVanish().isVanished()) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }

        if(messager.getIgnored().contains(messagee.getUuid().toString()) || messagee.getIgnored().contains(messager.getUuid().toString())) {
            player.sendMessage(Locale.COMMAND_MESSAGE_CANNOT.toString());
            return;
        }

        if(messagee.isMessagingEnabled()) {
            if(messager.isMessagingEnabled()) {
                messagee.setLastMessaged(target);
                messager.setLastMessaged(player);

                String message = Joiner.on(' ').join(args.getArgs()).replaceFirst(args.getArg(0), "").replaceFirst(" ", "");

                player.sendMessage(Locale.COMMAND_MESSAGE_FORMAT_TO.toString().replace("%recipient%", messager.getChatPrefix()).replace("%message%", message));
                target.sendMessage(Locale.COMMAND_MESSAGE_FORMAT_FROM.toString().replace("%sender%", messagee.getChatPrefix()).replace("%message%", message));

                if(messager.isMessagingSounds())
                    target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
            } else {
                player.sendMessage(Locale.COMMAND_MESSAGE_DISABLED.toString());
            }
        } else {
            player.sendMessage(Locale.COMMAND_MESSAGE_PLAYER_DISABLED.toString());
        }
    }

    @Command(label = "r", aliases = { "reply", "respond", "answer" }, playerOnly = true)
    public void onReply(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() < 1) {
            player.sendMessage(Locale.COMMAND_REPLY_USAGE.toString().replace("%label%", args.getLabel()));
            return;
        }

        HCFProfile replier = HCFProfile.getByPlayer(player);
        Player target = replier.getLastMessaged();
        if(target == null || !target.isOnline()) {
            player.sendMessage(target == null ? Locale.COMMAND_REPLY_NO_PLAYER.toString() : Locale.COMMAND_REPLY_PLAYER_OFFLINE.toString());
            return;
        }

        HCFProfile lastMessaged = HCFProfile.getByPlayer(target);
        if(!player.isOp() && lastMessaged.getVanish() != null && lastMessaged.getVanish().isVanished()) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }

        if(replier.getIgnored().contains(lastMessaged.getUuid().toString()) || lastMessaged.getIgnored().contains(replier.getUuid().toString())) {
            player.sendMessage(Locale.COMMAND_MESSAGE_CANNOT.toString());
            return;
        }

        lastMessaged.setLastMessaged(player);
        String message = Joiner.on(' ').join(args.getArgs());

        player.sendMessage(Locale.COMMAND_MESSAGE_FORMAT_TO.toString().replace("%recipient%", lastMessaged.getChatPrefix()).replace("%message%", message));
        target.sendMessage(Locale.COMMAND_MESSAGE_FORMAT_FROM.toString().replace("%sender%", replier.getChatPrefix()).replace("%message%", message));

        if(lastMessaged.isMessagingSounds())
            target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
    }

    @Command(label = "ignore", permission = "command.ignore",playerOnly = true)
    public void onIgnore(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() != 1) {
            player.sendMessage(Locale.COMMAND_IGNORE_USAGE.toString());
            return;
        }

        if(args.getArg(0).equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You cannot ignore yourself.");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if(target == null) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }
        if(target.hasPermission("hcf.staff")) {
            player.sendMessage(Locale.COMMAND_IGNORE_CANNOT.toString());
            return;
        }

        HCFProfile profile = HCFProfile.getByPlayer(player);
        if(profile.getIgnored().contains(target.getUniqueId().toString()))
            profile.getIgnored().remove(target.getUniqueId().toString());
        else
            profile.getIgnored().add(target.getUniqueId().toString());
        profile.save();

        player.sendMessage(Locale.COMMAND_IGNORE_PLAYER.toString().replace("%status%", C.color(profile.getIgnored().contains(target.getUniqueId().toString()) ? "&anow" : "&cno longer")).replace("%player%", target.getName()));
    }

    @Command(label = "tpm", aliases = { "togglepm", "togglemessages" }, playerOnly = true)
    public void onToggleMessages(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        profile.setMessagingEnabled(!profile.isMessagingEnabled());
        player.sendMessage(Locale.COMMAND_MESSAGE_TOGGLED.toString().replace("%status%", C.color(profile.isMessagingEnabled() ? "&aenabled" : "&cdisabled")));
    }

    @Command(label = "sounds", aliases = { "togglesounds", "togglemessagesounds" }, playerOnly = true)
    public void onToggleSounds(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        profile.setMessagingSounds(!profile.isMessagingSounds());
        player.sendMessage(Locale.COMMAND_MESSAGE_TOGGLED_SOUNDS.toString().replace("%status%", C.color(profile.isMessagingSounds() ? "&aenabled" : "&cdisabled")));
    }

}
