package me.hulipvp.hcf.game.faction.command.args.player.leader;

import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionReviveArgument {

    @Command(label = "f.revive", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = args.getPlayer();

        if(args.length() != 2) {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "revive <player>"));
        } else {
            HCFProfile profile = HCFProfile.getByPlayer(p);

            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();
                if(pf.getMembers().get(p.getUniqueId()).getRank() != FactionRank.LEADER) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_LEADER.toString());
                } else {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                    String targetName;
                    UUID targetUuid;
                    HCFProfile targetProfile;
                    if(target == null) {
                        p.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                        return;
                    } else {
                        targetName = target.getName();
                        targetUuid = target.getUniqueId();
                    }

                    targetProfile = HCFProfile.getByUuid(targetUuid);
                    if(!pf.getMembers().containsKey(targetUuid)) {
                        p.sendMessage(Locale.COMMAND_FACTION_PLAYER_NOT_IN.toString().replace("%name%", targetName));
                        return;
                    }

                    int lives = pf.getLives();
                    if(lives <= 0) {
                        p.sendMessage(Locale.COMMAND_FACTION_REVIVE_NO_LIVES.toString());
                        return;
                    }

                    if(targetProfile.getBannedTill() != 0) {
                        if(System.currentTimeMillis() < targetProfile.getBannedTill()) {
                            targetProfile.setBannedTill(0);
                            targetProfile.save();

                            pf.setLives(lives - 1);
                            pf.save();

                            p.sendMessage(Locale.COMMAND_REVIVE_SUCCESS.toString().replace("%player%", targetName));
                            return;
                        }
                    }
                    p.sendMessage(Locale.COMMAND_REVIVE_NO_DEATHBAN.toString().replace("%player%", targetName));
                }
            }
        }
    }
}
