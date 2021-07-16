package me.hulipvp.hcf.commands.staff;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaffReviveCommand {

    @Command(label = "staffrevive", permission = "command.revive", aliases = {"revive"}, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() != 1) {
            p.sendMessage(Locale.COMMAND_REVIVE_USAGE.toString());
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(0));
            String targetName;
            UUID targetUuid;
            HCFProfile profile;

            if(target == null) {
                p.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            } else {
                targetName = target.getName();
                targetUuid = target.getUniqueId();
            }

            profile = HCFProfile.getByUuid(targetUuid);

            if(profile.getBannedTill() != 0) {
                if(System.currentTimeMillis() < profile.getBannedTill()) {
                    profile.setBannedTill(0);
                    profile.save();
                    p.sendMessage(Locale.COMMAND_REVIVE_SUCCESS.toString().replace("%player%", targetName));
                    return;
                }
            }
            p.sendMessage(Locale.COMMAND_REVIVE_NO_DEATHBAN.toString().replace("%player%", targetName));
        }
    }
}
