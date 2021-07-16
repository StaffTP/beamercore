package me.hulipvp.hcf.game.faction.command.args.player.member;

import me.activated.core.plugin.AquaCoreAPI;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

import java.time.Duration;

public class FactionLeaveArgument {

    @Command(label = "f.leave", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            return;
        }

        PlayerFaction faction = profile.getFactionObj();
        Faction currentTerritory = Faction.getByLocation(p.getLocation());

        if(faction.getLeader().getUuid().toString().equals(p.getUniqueId().toString())) {
            p.sendMessage(C.color("&cYou cannot leave a faction you lead."));
            return;
        }

        if(currentTerritory != null) {
            if(currentTerritory.getName().equalsIgnoreCase(faction.getName())) {
                p.sendMessage(C.color("&cYou must leave your faction's territory before leaving."));
                return;
            }
        }

        profile.setFaction(null);
        profile.save();
        faction.getMembers().remove(p.getUniqueId());
        faction.save();
        faction.sendMessage(Locale.COMMAND_FACTION_LEFT.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
        p.sendMessage(Locale.COMMAND_FACTION_LEFT_SELF.toString());
        LunarClientAPI api = LunarClientAPI.getInstance();
        api.sendTitle(p, TitleType.TITLE, CC.translate("&cYou left &d" + faction.getName() + "&c!"), Duration.ofSeconds(3));
    }
}
