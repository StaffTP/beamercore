package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.listeners.LunarClientListener;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionJoinArgument {

    @Command(label = "f.join", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 2) {
            if(profile.getFactionObj() != null) {
                p.sendMessage(Locale.COMMAND_FACTION_ATTEMPT_JOIN.toString());
            } else {
                TaskUtils.runAsync(() -> {
                    List<Faction> factions = Faction.findFactions(args.getArg(1));
                    Faction faction;
                    if(factions.isEmpty() || ((faction = factions.get(0)) == null)) {
                        p.sendMessage(Locale.COMMAND_FACTION_NOT_INVITED.toString());
                    } else {
                        if(!(faction instanceof PlayerFaction)) {
                            p.sendMessage(Locale.COMMAND_FACTION_NOT_INVITED.toString());
                        } else {
                            PlayerFaction pf = (PlayerFaction) faction;

                            if(!pf.isOpen() && pf.isInvited(p.getUniqueId())) {
                                if(pf.getStartRegen() != null) {
                                    p.sendMessage(Locale.COMMAND_FACTION_ON_FREEZE.toString());
                                } else {
                                    pf.removeInvite(p.getUniqueId());
                                    if(pf.getMembers().size() < ConfigValues.FACTIONS_MEMBER_MAX) {
                                        pf.addMember(new FactionMember(p.getUniqueId(), FactionRank.MEMBER));
                                        pf.save();
                                        profile.setFaction(pf.getUuid());
                                        profile.save();
                                        pf.sendMessage(Locale.COMMAND_FACTION_JOINED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));

                                        if(pf.getDtr() < pf.getMaxDtr()) {
                                            pf.setRegening(true);
                                            pf.setupRegenTask();
                                        }
                                    } else {
                                        p.sendMessage(Locale.COMMAND_FACTION_MEMBER_MAX.toString());
                                    }
                                }
                            } else if(pf.isOpen()) {
                                if(pf.getMembers().size() < ConfigValues.FACTIONS_MEMBER_MAX) {
                                    pf.addMember(new FactionMember(p.getUniqueId(), FactionRank.MEMBER));
                                    pf.save();
                                    profile.setFaction(pf.getUuid());
                                    profile.save();
                                    pf.sendMessage(Locale.COMMAND_FACTION_JOINED.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
                                    LunarClientListener lunarClientListener = new LunarClientListener();
                                    lunarClientListener.updateWaypoints(p);
                                    if(pf.getDtr() < pf.getMaxDtr()) {
                                        pf.setRegening(true);
                                        pf.setupRegenTask();
                                    }
                                } else {
                                    p.sendMessage(Locale.COMMAND_FACTION_MEMBER_MAX.toString());
                                }
                            } else {
                                p.sendMessage(Locale.COMMAND_FACTION_NOT_INVITED.toString());
                            }
                        }
                    }
                });
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "join <name>"));
        }
    }
}
