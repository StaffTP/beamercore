package me.hulipvp.hcf.game.faction.command.args.player.member;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


import java.util.UUID;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/5/2021 / 3:57 PM
 * vhcf / me.hulipvp.hcf.game.faction.command.args.player.member
 */
public class FactionRallyCommand {

     private int id;

    @Command(label = "f.rally", playerOnly = true)
    public void onCommand(CommandData args) {

        Player player = (Player) args.getSender();

        HCFProfile profile = HCFProfile.getByPlayer(player);
        PlayerFaction playerFaction = profile.getFactionObj();
        LunarClientAPI api = LunarClientAPI.getInstance();
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You are not in a faction!");
            return;
        }

        if (playerFaction.getRallyPoint() != null) {

            Location loc = playerFaction.getRallyPoint();
            Bukkit.getScheduler().cancelTask(id);

            for (UUID uuid : playerFaction.getMembers().keySet()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member == null) {
                    continue;
                }
                api.removeWaypoint(member, new LCWaypoint("Rally Point",
                        loc.getBlockX(),
                        loc.getBlockY(),
                        loc.getBlockZ(),
                        loc.getWorld().getUID().toString(),
                        15073510,
                        true,
                        true));

            }

        }

        playerFaction.setRallyPoint(player.getLocation());
        playerFaction.sendMessage(CC.translate("&eThe Rally Point has been updated!"));
        player.sendMessage(CC.translate("&eYou have updated the rally point!"));
        Location loc = playerFaction.getRallyPoint();
        for (UUID uuid : playerFaction.getMembers().keySet()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member == null) {
                continue;
            }
            api.sendWaypoint(member, new LCWaypoint("Rally Point",
                    loc.getBlockX(),
                    loc.getBlockY(), loc.getBlockZ(),
                    loc.getWorld().getUID().toString(),
                    15073510,
                    true,
                    true));
        }
        id = Bukkit.getScheduler().scheduleSyncDelayedTask(HCF.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (playerFaction.getRallyPoint() != null) {
                    Location loc = playerFaction.getRallyPoint();
                    for (UUID uuid : playerFaction.getMembers().keySet()) {
                        Player member = Bukkit.getPlayer(uuid);
                        api.removeWaypoint(member, new LCWaypoint("Rally Point",
                                loc.getBlockX(),
                                loc.getBlockY(),
                                loc.getBlockZ(),
                                loc.getWorld().getUID().toString(),
                                13369344,
                                true,
                                true));

                    }
                }
            }
        }, 600 * 20);

    }


}
