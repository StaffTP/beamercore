package me.hulipvp.hcf.game.faction.command.args.player.member;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionFocusArgument {

    @Command(label = "f.focus", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if (args.length() == 2) {
            HCFProfile profile = HCFProfile.getByPlayer(p);

            if (profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                Player target = Bukkit.getPlayer(args.getArg(1));
                HCFProfile targetprofile;
                PlayerFaction targetfaction;
                LCWaypoint lcWaypoint = null;
                LunarClientAPI api = LunarClientAPI.getInstance();
                if (target != null) {
                    targetprofile = HCFProfile.getByPlayer(target);
                    targetfaction = targetprofile.getFactionObj();
                    if (targetfaction == null) {
                        p.sendMessage(CC.translate("&c%name% does not have a faction!").replace("%name%", args.getArg(1)));
                        return;
                    }
                } else {
                    targetfaction = (PlayerFaction) PlayerFaction.getByName(args.getArg(1));
                    if (targetfaction == null) {
                        p.sendMessage(CC.translate("&cNo Faction or Player by the name of '%name%' is found!").replace("%name%", args.getArg(1)));
                        return;
                    }
                }


                PlayerFaction pf = profile.getFactionObj();
                if (targetfaction.getName().equals(pf.getName())) {
                    p.sendMessage(ChatColor.RED + "You cannot focus your own team.");
                    return;
                }

                if (targetfaction.getHome() == null) {
                    p.sendMessage(CC.translate("&cThat faction doesn't have a home!"));
                    return;
                }

                if (pf.getFactionFocus() != null) {
                    if (pf.getFactionFocus().equals(targetfaction)) {
                        p.sendMessage(C.color("&d" + targetfaction.getName() + " &eis no longer focused."));
                        Location loc = pf.getFactionFocus().getHome();
                        for (UUID uuid : pf.getMembers().keySet()) {
                            Player member = Bukkit.getPlayer(uuid);
                            if (member == null) {
                                continue;
                            }
                            api.removeWaypoint(member, new LCWaypoint(targetfaction.getName() + "'s HQ",
                                    loc.getBlockX(),
                                    loc.getBlockY(),
                                    loc.getBlockZ(),
                                    loc.getWorld().getUID().toString(),
                                    13369344,
                                    true,
                                    true));

                        }
                        pf.setFactionFocus(null);
                        return;
                    }
                }
                pf.setFactionFocus(targetfaction);
                pf.sendMessage(C.color("&d" + targetfaction.getName() + " &eis now focused."));
                Location loc = pf.getFactionFocus().getHome();
                for (UUID uuid : pf.getMembers().keySet()) {
                    Player member = Bukkit.getPlayer(uuid);
                    if (member == null) {
                        continue;
                    }
                    lcWaypoint = new LCWaypoint(targetfaction.getName() + "'s HQ",
                            loc.getBlockX(),
                            loc.getBlockY(),
                            loc.getBlockZ(),
                            loc.getWorld().getUID().toString(),
                            13369344,
                            true,
                            true);
                    api.sendWaypoint(member, lcWaypoint);
                }
            }
            } else{
                p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "focus <faction/player>"));
            }
        }
    }
