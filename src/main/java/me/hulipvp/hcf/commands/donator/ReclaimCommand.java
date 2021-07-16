package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.backend.files.ReclaimFile;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReclaimCommand {

    private ReclaimFile file;

    public ReclaimCommand() {
        file = HCF.getInstance().getReclaimFile();

        file.loadGroups();
        file.loadReclaimed();
    }

    @Command(label = "reclaim", aliases = { "claim", "claimkeys" }, permission = "command.reclaim", playerOnly = true)
    public void onReclaim(CommandData args) {
        Player player = args.getPlayer();
        if(file.getGroups() == null || file.getGroups().isEmpty()) {
            player.sendMessage(C.color("&cNo rewards found for reclaim."));
            return;
        }

        if(file.getReclaimed().contains(player.getUniqueId().toString())) {
            player.sendMessage(Locale.RECLAIM_CLAIMED.toString());
            return;
        }


        boolean foundGroup = false;
        for(Map.Entry<String, List<String>> group : file.getGroups()) {
            String permission = group.getKey();
            if(player.hasPermission("reclaim." + permission.toLowerCase())) {
                foundGroup = true;
                for(String command : group.getValue())
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));

                player.sendMessage(Locale.RECLAIM_CLAIMED_ITEMS.toString().replace("%rank%", WordUtils.capitalizeFully(permission)));
                if(file.config.contains("reclaim." + group.getKey() + ".broadcast"))
                    Bukkit.broadcastMessage(C.color(file.getConfig().getString("reclaim." + group.getKey() + ".broadcast")).replace("%player%", player.getName()));

                file.getReclaimed().add(player.getUniqueId().toString());
                file.saveReclaimed();
                break;
            }
        }

        if(!foundGroup)
            player.sendMessage(Locale.RECLAIM_NO_REWARDS.toString());
    }

    @Command(label = "resetreclaim", permission = "command.resetreclaim", playerOnly = true)
    public void onResetReclaim(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 1) {
            sender.sendMessage(C.color("&cUsage: /resetreclaim <player;all>"));
            return;
        }

        if(args.getArg(0).equalsIgnoreCase("all")) {
            file.getReclaimed().clear();
            sender.sendMessage(C.color("&aSuccessfully reset everyone's reclaim."));
        } else {
            Player target = Bukkit.getPlayerExact(args.getArg(0));
            if(target == null || !target.isOnline()) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }

            if(file.getReclaimed().remove(target.getUniqueId().toString()))
                sender.sendMessage(C.color("&aSuccessfully reset " + target.getName() + "'s reclaim."));
            else
                sender.sendMessage(C.color("&cThe player '" + target.getName() + "' has not reclaimed anything yet."));
        }
    }
}
