package me.hulipvp.hcf.game.faction.command.args.player.coleader;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import com.google.common.base.Joiner;
import org.bukkit.entity.Player;

public class FactionAnnouncementArgument {

    @Command(label = "f.announcement", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() >= 2) {
            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();

                if(!(pf.getMembers().get(p.getUniqueId()).isAtLeast(FactionRank.COLEADER))) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_COLEADER.toString());
                } else {
                    String announcement = Joiner.on(' ').join(args.getArgs()).replaceFirst("announcement", "").replaceFirst(" ", "");
                    if(announcement.equalsIgnoreCase("null")) {
                        pf.setAnnouncement("");
                        pf.sendMessage(Locale.COMMAND_FACTION_ANNOUNCEMENT_REMOVED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
                        pf.save();
                    } else {
                        pf.setAnnouncement(announcement);
                        pf.sendMessage(Locale.COMMAND_FACTION_ANNOUNCEMENT_CHANGED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName()).replace("%announcement%", pf.getAnnouncement())));
                        pf.save();
                    }
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "announcement <new announcement;null>"));
        }
    }

}
