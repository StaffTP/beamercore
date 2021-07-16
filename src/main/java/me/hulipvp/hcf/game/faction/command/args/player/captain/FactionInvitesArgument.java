package me.hulipvp.hcf.game.faction.command.args.player.captain;

import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionInvitesArgument {

    @Command(label = "f.invites", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            return;
        }

        PlayerFaction pf = profile.getFactionObj();
        if(!(pf.getMembers().get(p.getUniqueId()).isAtLeast(FactionRank.CAPTAIN))) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_CAPTAIN.toString());
            return;
        }

        p.sendMessage(C.color("&9Invites: " + (pf.getInvited().size() == 0 ? "&cNone" : "")));
        pf.getInvited().forEach(invitee -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(invitee);
            p.sendMessage(C.color("  &7- &a" + player.getName()));
        });
    }
}
