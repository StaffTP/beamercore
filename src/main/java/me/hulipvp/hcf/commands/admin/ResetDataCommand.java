package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ResetDataCommand {

    @Command(label = "resetdata", permission = "command.resetdata")
    public void onCommand(CommandData args) {
        sendUsage(args.getSender());
    }

    @Command(label = "resetdata.factions", permission = "command.resetdata")
    public void onFactions(CommandData args) {
        CommandSender sender = args.getSender();

        sender.sendMessage(C.color("&aAttempting to reset all faction data..."));

        TaskUtils.runAsync(() -> {
            HCF.getInstance().getBackend().deleteFactions();
            HCFProfile.getProfiles().values().forEach(profile -> {
                profile.setFaction(null);
                profile.save();
            });

            Faction.getFactions().clear();
            Faction.getClaimPositions().clear();

            HCF.getInstance().getBackend().deleteFactions();

            Mountain.getMountains().values().forEach(HCF.getInstance().getBackend()::deleteMountain);
            Conquest.getConquests().values().forEach(HCF.getInstance().getBackend()::deleteConquest);
            Koth.getKoths().values().forEach(HCF.getInstance().getBackend()::deleteKoth);

            Mountain.getMountains().clear();
            Conquest.getConquests().clear();
            Koth.getKoths().clear();

            sender.sendMessage(C.color("&aFaction data has been cleared!"));
        });
    }

    @Command(label = "resetdata.profiles", permission = "command.resetdata")
    public void onProfiles(CommandData args) {
        CommandSender sender = args.getSender();

        sender.sendMessage(C.color("&aAttempting to reset all profile data..."));
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer(C.color("&cResetting all profile data!"));
        });

        TaskUtils.runAsync(() -> {
            HCF.getInstance().getBackend().loadProfiles();
            HCFProfile.getProfiles().values().forEach(profile -> {
                profile.reset();
                profile.save();
            });

            sender.sendMessage(C.color("&aProfile data has been cleared!"));
        });
    }

    @Command(label = "resetdata.profile", permission = "command.resetdata")
    public void onProfile(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 2) {
            sender.sendMessage(C.color("&cUsage: /resetdata profile <user>"));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
        String targetName;
        UUID targetUuid;
        HCFProfile profile;
        if(target == null) {
            sender.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
            return;
        } else {
            targetName = target.getName();
            targetUuid = target.getUniqueId();
        }

        profile = HCFProfile.getByUuid(targetUuid);

        sender.sendMessage(C.color("&aAttempting to reset data of '" + targetName + "'."));
        /*if(profile.hasFaction()) {
            PlayerFaction faction = profile.getFactionObj();
            faction.getMembers().remove(profile.getUuid());

            faction.getMembers().keySet().stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .filter(Player::isOnline)
                    .forEach(TabAdapter::update);
        }*/

        profile.reset();
        profile.save();

        sender.sendMessage(C.color("&aProfile data of '" + targetName + "' has been cleared!"));
    }

    @Command(label = "resetdata.revives", permission = "command.resetdata")
    public void onRevives(CommandData args) {
        CommandSender sender = args.getSender();
        sender.sendMessage(C.color("&aAttempting to reset everyone's revive cooldown..."));

        TaskUtils.runAsync(() -> {
            HCF.getInstance().getBackend().loadProfiles();
            HCFProfile.getProfiles().values().forEach(profile -> {
                profile.setLastRevive(0L);
                profile.save();
            });

            sender.sendMessage(C.color("&aRevives have been reset!"));
        });
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(C.color("&cUsage: /resetdata <factions;profiles;profile;revives> <user>"));
    }
}
