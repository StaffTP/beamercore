package me.hulipvp.hcf.commands.staff;

import com.google.common.base.Joiner;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffChatCommand {

    @Command(label = "sc", aliases = { "staffchat" }, permission = "command.staffchat", playerOnly = true)
    public void onStaffChat(CommandData args) {
        Player player = (Player) args.getSender();
        if(args.length() == 0) {
            HCFProfile profile = HCFProfile.getByPlayer(player);
            profile.setStaffChat(!profile.isStaffChat());
            player.sendMessage(Locale.COMMAND_STAFF_CHAT_TOGGLED.toString().replace("%status%", C.color(profile.isStaffChat() ? "&aEnabled" : "&cDisabled")));
            return;
        }

        String message = Joiner.on(' ').join(args.getArgs());
        Bukkit.getOnlinePlayers().stream()
                .filter(ply -> ply.hasPermission("hcf.staff"))
                .forEach(ply -> {
                    ply.sendMessage(Locale.COMMAND_STAFF_CHAT_MESSAGE.toString().replace("%name%", player.getName()).replace("%message%", message));
                });
    }

}
