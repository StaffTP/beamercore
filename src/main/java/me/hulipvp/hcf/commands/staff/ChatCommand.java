package me.hulipvp.hcf.commands.staff;

import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.StringUtils;
import org.bukkit.Bukkit;

public class ChatCommand {

    public static int slow = 0;
    public static boolean muted = false;

    @Command(label = "clearchat", aliases = { "cc", "chatclear" }, permission = "command.clearchat")
    public void onClear(CommandData args) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.hasPermission("hcf.staff"))
                .forEach(player -> {
                    for(int i = 0; i < 256; i++)
                        player.sendMessage(" ");
                });

        Bukkit.broadcastMessage(Locale.COMMAND_CHAT_CLEARED.toString());
    }

    @Command(label = "mutechat", aliases = { "mc", "umc", "chatmute", "unmutechat" }, permission = "command.mutechat")
    public void onMute(CommandData args) {
        muted = !muted;

        if(muted)
            Bukkit.broadcastMessage(Locale.COMMAND_CHAT_MUTED.toString());
        else
            Bukkit.broadcastMessage(Locale.COMMAND_CHAT_UNMUTED.toString());
    }

    @Command(label = "slowchat", aliases = { "slow", "unslow", "unslowchat" }, permission = "command.slowchat")
    public void onSlow(CommandData args) {
        if(args.getLabel().contains("unslow")) {
            slow = 0;
            Bukkit.broadcastMessage(Locale.COMMAND_CHAT_UNSLOWED.toString());
            return;
        }

        if(args.length() < 1) {
            args.getSender().sendMessage(C.color("&cUsage: /" + args.getLabel() + " <time>"));
            return;
        }

        if(!StringUtils.isInt(args.getArg(0))) {
            args.getSender().sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
            return;
        }

        slow = Integer.parseInt(args.getArg(0));

        if(slow <= 0)
            Bukkit.broadcastMessage(Locale.COMMAND_CHAT_UNSLOWED.toString());
        else
            Bukkit.broadcastMessage(Locale.COMMAND_CHAT_SLOWED.toString().replace("%time%", slow + ""));
    }
}
