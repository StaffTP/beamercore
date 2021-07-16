package me.hulipvp.hcf.game.faction.command.args.staff;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.faction.normal.FactionDisbandEvent;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import me.hulipvp.hcf.hooks.plugins.vault.VaultHook;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionForceDisbandArgument {

    @Command(label = "f.forcedisband", aliases = {"f.fd"}, permission = "command.forcedisband", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 2) {
            Faction fac = Faction.getByName(args.getArg(1));

            if(fac != null) {
                FactionDisbandEvent event = new FactionDisbandEvent(fac, p);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if(fac instanceof PlayerFaction) {
                    PlayerFaction pf = (PlayerFaction) fac;
                    pf.sendMessage(Locale.COMMAND_FACTION_DISBAND_FBC.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));

                    PlayerFaction.getSorted().remove(pf.getUuid());

                    for(FactionMember members : pf.getMembers().values()) {
                        HCFProfile mProfile = HCFProfile.getByUuid(members.getUuid());
                        mProfile.setFaction(null);
                        mProfile.save();
                    }
                }

                /*Bukkit.getServer().broadcastMessage(Locale.COMMAND_FACTION_DISBAND_BC.toString()
                        .replace("%faction%", fac.getName())
                        .replace("%player%", Colors.getColorFor(p))
                );*/

                CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName());

                Bukkit.getServer().broadcastMessage(Locale.COMMAND_FACTION_DISBAND_BC.toString()
                        .replace("%faction%", fac.getName())
                        .replace("%player%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())
                ));

                List<Claim> claims = new ArrayList<>(fac.getClaims());
                claims.forEach(fac::removeClaim);

                fac.getClaims().clear();

                HCF.getInstance().getBackend().deleteFaction(fac);
                Faction.getFactions().remove(fac.getUuid().toString());
                Faction.refreshSorted();
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "forcedisband <name>"));
        }
    }

}
