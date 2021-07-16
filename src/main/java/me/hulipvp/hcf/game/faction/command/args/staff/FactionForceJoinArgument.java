package me.hulipvp.hcf.game.faction.command.args.staff;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.entity.Player;

public class FactionForceJoinArgument {

    @Command(label = "f.forcejoin", aliases = { "f.fj" }, permission = "command.forcejoin", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 2) {
            HCFProfile profile = HCFProfile.getByPlayer(p);
            if(profile.getFactionObj() != null) {
                p.sendMessage(Locale.COMMAND_FACTION_ATTEMPT_JOIN.toString());
                return;
            }

            Faction fac = Faction.getByName(args.getArg(1));
            if(fac != null) {
                if(fac instanceof PlayerFaction) {
                    PlayerFaction pf = (PlayerFaction) fac;
                    if(!pf.getMembers().containsKey(p.getUniqueId()))
                        pf.addMember(new FactionMember(p.getUniqueId(), FactionRank.MEMBER));

                    profile.setFaction(pf.getUuid());
                    if(pf.getDtr() < pf.getMaxDtr()) {
                        pf.setRegening(true);
                        pf.setupRegenTask();
                    }

                    pf.sendMessage(Locale.COMMAND_FACTION_JOINED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_PLAYER.toString());
                }
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "forcejoin <name>"));
        }
    }
}
