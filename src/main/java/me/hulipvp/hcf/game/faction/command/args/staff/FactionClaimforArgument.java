package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.LocUtils;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FactionClaimforArgument {

    @Command(label = "f.claimfor", aliases = {"f.cf"}, permission = "command.claimfor", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 2) {
            Faction fac = Faction.getByName(args.getArg(1));

            if (args.getArg(1).equalsIgnoreCase("warzone")) {
                p.sendMessage(C.color("&cYou can't claim warzone anymore, put the warzone radius in the config"));
                return;
            }

            if(fac != null) {
                if(profile.getClaimData().isClaiming()) {
                    if(p.getInventory().contains(LocUtils.getClaimingWand()))
                        p.getInventory().remove(LocUtils.getClaimingWand());

                    p.sendMessage(Locale.CLAIMING_ENDED.toString());
                    profile.getClaimData().setClaiming(false);
                    profile.getClaimData().setClaimingFaction(null);
                    profile.getClaimData().setPos1(null);
                    profile.getClaimData().setPos2(null);
                    profile.getClaimData().removePillars(p);
                } else {
                    for(String str : HCF.getInstance().getMessagesFile().getFactionClaimingStart())
                        p.sendMessage(C.color(str));

                    if(!p.getInventory().contains(LocUtils.getClaimingWand())) {
                        p.getInventory().addItem(LocUtils.getClaimingWand());
                        p.sendMessage(Locale.CLAIMING_CLAIM_WAND.toString());
                    }

                    profile.getClaimData().setClaiming(true);
                    profile.getClaimData().setClaimingFaction(fac.getUuid());
                    profile.getClaimData().setPillars(new ArrayList<>());
                }
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "claimfor <name>"));
        }
    }

}
