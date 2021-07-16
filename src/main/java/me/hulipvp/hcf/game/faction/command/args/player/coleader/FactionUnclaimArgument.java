package me.hulipvp.hcf.game.faction.command.args.player.coleader;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionUnclaimArgument {

    @Command(label = "f.unclaim", playerOnly = true)
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

        if (pf.isRaidable()) {
            p.sendMessage(Locale.COMMAND_FACTION_CANNOT_UNCLAIM_DTR.toString());
            return;
        }

        for(Claim claim : pf.getClaims()) {
            if(pf.getHome() != null && claim.toCuboid().isInCuboid(pf.getHome()))
                pf.setHome(null);
        }

        int totalBack = pf.getClaims().stream().mapToInt(Claim::getPrice).sum();
        profile.addToBalance(totalBack);
        profile.save();

        List<Claim> claims = new ArrayList<>(pf.getClaims());
        claims.forEach(pf::removeClaim);

        pf.getClaims().clear();
        pf.save();

        pf.sendMessage(Locale.COMMAND_FACTION_UNCLAIM_BROADCAST.toString().replace("%player%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
        p.sendMessage(Locale.COMMAND_FACTION_UNCLAIM_PLAYER.toString());
    }
}
