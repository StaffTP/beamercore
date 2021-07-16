package me.hulipvp.hcf.game.faction.command.args.player.leader;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.faction.normal.FactionDisbandEvent;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.Placeholders;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FactionDisbandArgument {

    @Command(label = "f.disband", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
        } else {
            if(profile.getLastFactionEdit() != 0 && System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1) < profile.getLastFactionEdit()) {
                p.sendMessage(Locale.COMMAND_FACTION_ACTION_COOLDOWN.toString());
                return;
            }

            PlayerFaction pf = profile.getFactionObj();
            if(!(pf.getLeader().getUuid().toString().equals(p.getUniqueId().toString()))) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_LEADER.toString());
            } else {
                if(ServerTimer.isEotw()) {
                    p.sendMessage(Locale.COMMAND_EOTW_CANNOT.toString());
                    return;
                }
                if(pf.isRaidable()) {
                    p.sendMessage(Locale.COMMAND_FACTION_RAIDABLE.toString());
                    return;
                }
                pf.sendMessage(Placeholders.replacePlaceholders(Locale.COMMAND_FACTION_DISBAND_FBC.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())), p, profile));

                FactionDisbandEvent event = new FactionDisbandEvent(pf, p);
                Bukkit.getServer().getPluginManager().callEvent(event);

                Bukkit.getServer().broadcastMessage(CC.translate("&eFaction &9" + pf.getName() + " &ehas been &cdisbanded &eby " + HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName()));

                PlayerFaction.getSorted().remove(pf.getUuid());

                for(FactionMember members : pf.getMembers().values()) {
                    HCFProfile mProfile = HCFProfile.getByUuid(members.getUuid());
                    mProfile.setFaction(null);
                    mProfile.save();
                }

                List<Claim> claims = new ArrayList<>(pf.getClaims());
                claims.forEach(pf::removeClaim);

                pf.getClaims().clear();

                HCF.getInstance().getBackend().deleteFaction(pf);
                Faction.getFactions().remove(pf.getUuid().toString());

                profile.setLastFactionEdit(System.currentTimeMillis());
            }
        }
    }
}
