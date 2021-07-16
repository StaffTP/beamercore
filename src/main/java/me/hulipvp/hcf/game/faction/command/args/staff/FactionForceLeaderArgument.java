package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionForceLeaderArgument {

    @Command(label = "f.forceleader", permission = "command.forceleader", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = args.getPlayer();

        if (args.length() < 2) {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "leader <player>"));
        } else {
            HCFProfile profile = HCFProfile.getByPlayer(p);
            PlayerFaction pf = profile.getFactionObj();
            if (pf == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
            String targetName;
            UUID targetUuid;
            if (target == null || !target.hasPlayedBefore()) {
                p.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            } else {
                targetName = target.getName();
                targetUuid = target.getUniqueId();
            }

            if (!pf.getMembers().containsKey(targetUuid)) {
                p.sendMessage(Locale.COMMAND_FACTION_PLAYER_NOT_IN.toString().replace("%name%", targetName));
                return;
            }

            FactionMember targetMember = pf.getMembers().get(targetUuid);
            if (targetMember.getRank() == FactionRank.LEADER) {
                p.sendMessage(Locale.COMMAND_FACTION_LEADER_SELF.toString());
                return;
            }

            FactionMember member = pf.getLeader();
            member.setRank(FactionRank.MEMBER);

            targetMember.setRank(FactionRank.LEADER);
            pf.setLeader(targetMember);
            pf.save();

            pf.sendMessage(Locale.COMMAND_FACTION_LEADER_CHANGED.toString().replace("%name%", targetName));
        }
    }
}
