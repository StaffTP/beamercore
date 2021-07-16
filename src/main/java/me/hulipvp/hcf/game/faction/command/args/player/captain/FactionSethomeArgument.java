package me.hulipvp.hcf.game.faction.command.args.player.captain;

import me.activated.core.plugin.AquaCoreAPI;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public class FactionSethomeArgument {

    @Command(label = "f.sethome", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
        } else {
            PlayerFaction pf = profile.getFactionObj();
            LunarClientAPI api = LunarClientAPI.getInstance();

            if(!(pf.getMembers().get(p.getUniqueId()).getRank().getRank() >= 2)) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_CAPTAIN.toString());
            } else {
                if(Faction.isInsideFactionTerritory(pf, p.getLocation())) {

                    if (pf.getHome() != null) {
                        Location loc = pf.getHome();
                        for (UUID uuid : pf.getMembers().keySet()) {
                            Player member = Bukkit.getPlayer(uuid);

                            if (member == null) {
                                continue;
                            }
                                api.removeWaypoint(p, new LCWaypoint("Home",
                                        loc.getBlockX(),
                                        loc.getBlockY(),
                                        loc.getBlockZ(),
                                        loc.getWorld().getUID().toString(),
                                        -16776961,
                                        true,
                                        true));

                        }
                    }
                    pf.setHome(p.getLocation());

                    Location loc = pf.getHome();
                    for (UUID uuid : pf.getMembers().keySet()) {
                        Player member = Bukkit.getPlayer(uuid);
                            if (member == null) {
                                continue;
                            }
                            api.sendWaypoint(p, new LCWaypoint("Home",
                                    loc.getBlockX(),
                                    loc.getBlockY(),
                                    loc.getBlockZ(),
                                    loc.getWorld().getUID().toString(),
                                    -16776961,
                                    true,
                                    true));

                    }
                    pf.save();
                    pf.sendMessage(Locale.COMMAND_FACTION_HOME_UPDATED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_CANNOT_SET_HOME.toString());
                }
            }
        }
    }
}
