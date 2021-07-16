package me.hulipvp.hcf.game.faction.command.args.player.coleader;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.LocUtils;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FactionClaimArgument {

    @Command(label = "f.claim", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        if(ServerTimer.isEotw()) {
            p.sendMessage(Locale.COMMAND_EOTW_CANNOT_CLAIM.toString());
            return;
        }

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
        } else {
            /*if(profile.hasTimer(PlayerTimerType.PVPTIMER)) {
                p.sendMessage(Locale.TIMER_CANNOT_CLAIM.toString());
                return;
            }*/

            PlayerFaction pf = profile.getFactionObj();
            if(pf.isRaidable()) {
                p.sendMessage(Locale.CLAIMING_RAIDABLE.toString());
                return;
            }

            if(!(pf.getMembers().get(p.getUniqueId()).getRank().getRank() >= 2)) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_COLEADER.toString());
            } else {
                if(pf.getClaims().size() >= 1) {
                    p.sendMessage(Locale.CLAIMING_ALREADY_CLAIMED.toString());
                    return;
                }

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
                    profile.getClaimData().setClaimingFaction(profile.getFactionObj().getUuid());
                    profile.getClaimData().setPillars(new ArrayList<>());
                }
            }
        }
    }

}
