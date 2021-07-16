package me.hulipvp.hcf.game.faction.command.args.player.captain;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.FancyMessage;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FactionInviteArgument {

    @Command(label = "f.invite", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        if(ServerTimer.isEotw()) {
            p.sendMessage(Locale.COMMAND_EOTW_CANNOT.toString());
            return;
        }

        if(args.length() == 2) {
            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();
                if(pf.isRaidable()) {
                    p.sendMessage(Locale.COMMAND_FACTION_RAIDABLE.toString());
                    return;
                }

                if(!(pf.getMembers().get(p.getUniqueId()).getRank().getRank() >= 2)) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_CAPTAIN.toString());
                } else {
                    Player target = Bukkit.getPlayer(args.getArg(1));
                    if(target != null && target.isOnline()) {
                        if(pf.isInvited(target.getUniqueId())) {
                            p.sendMessage(Locale.COMMAND_FACTION_INVITE_ALREADY_IN.toString().replace("%name%", target.getName()));
                            return;
                        }

                        if(pf.getMembers().containsKey(target.getUniqueId())) {
                            p.sendMessage(Locale.COMMAND_FACTION_INVITE_ALREADY_IN_TEAM.toString().replace("%name%", target.getName()));
                            return;
                        }

                        if(pf.getMembers().size() >= ConfigValues.FACTIONS_MEMBER_MAX) {
                            p.sendMessage(Locale.COMMAND_FACTION_INVITE_MEMBER_MAX.toString());
                            return;
                        }

                        pf.addInvite(target.getUniqueId());
                        pf.save();
                        pf.sendMessage(Locale.COMMAND_FACTION_INVITE_BC.toString().replace("%name%", target.getName()));

                        if(target.isOnline()) {
                            new FancyMessage(Locale.COMMAND_FACTION_INVITE_PM.toString()
                                    .replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName()))
                                    .replace("%faction%", pf.getName()))
                                    .tooltip("Click to join " + pf.getName() + ".")
                                    .command("/f join " + pf.getName())
                                    .send(target.getPlayer());
//                            ((Player) target).sendMessage(Locale.COMMAND_FACTION_INVITE_PM.toString()
//                                    .replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())
//                                    .replace("%faction%", pf.getName())
//                            );
                        }
                    } else {
                        p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    }
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "invite <name>"));
        }
    }
}
