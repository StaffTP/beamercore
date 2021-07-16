package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.TimeUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FactionShowArgument {

    @Command(label = "f.show", aliases = { "f.f", "f.who", "f.i", "f.info" }, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        if(args.length() >= 2) {
            TaskUtils.runAsync(() -> {
                List<Faction> factions = Faction.findFactions(args.getArg(1));
                if(factions != null && !factions.isEmpty()) {
                    factions.forEach(faction -> {
                        sendInfo(p, faction, args.getArg(1));
                    });
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
                }
            });
        } else {
            if(args.length() == 1) {
                if(profile.getFactionObj() != null) {
                    sendInfo(p, profile.getFactionObj(), null);
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "show <name>"));
                }
            } else {
                p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "show <name>"));
            }
        }
    }

    private void sendInfo(Player player, Faction fac, String name) {
        if(fac == null) {
            player.sendMessage(C.color(name == null ? Locale.COMMAND_FACTION_PROFILE_INVALID.toString() : Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%name%", name)));
            return;
        }

        TaskUtils.runAsync(() -> {
            switch(fac.getType()) {
                case PLAYER:
                    PlayerFaction pf = (PlayerFaction) fac;

                    String leader = ((Bukkit.getPlayer(pf.getLeader().getUuid()) != null) ? "&a" + Bukkit.getPlayer(pf.getLeader().getUuid()).getName() + "&e[&a" + HCFProfile.getByUuid(pf.getLeader().getUuid()).getKills().size() + "&e]" : "&7" + Bukkit.getOfflinePlayer(pf.getLeader().getUuid()).getName() + "&e[&a" + HCFProfile.getByUuid(pf.getLeader().getUuid()).getKills().size() + "&e]");

                    StringBuilder coleaders = new StringBuilder();
                    StringBuilder captains = new StringBuilder();
                    StringBuilder members = new StringBuilder();
                    StringBuilder allies = new StringBuilder();



                    for(UUID uuid : pf.getMembers().keySet()) {
                        OfflinePlayer of = Bukkit.getOfflinePlayer(uuid);

                        StringBuilder builder = null;
                        switch(pf.getMembers().get(uuid).getRank()) {
                            case COLEADER:
                                builder = coleaders;
                                break;
                            case CAPTAIN:
                                builder = captains;
                                break;
                            case MEMBER:
                                builder = members;
                                break;
                        }

                        if(builder != null) {
                            if(builder.length() == 0) builder.append((of.isOnline()) ? "&a" : "&7").append(of.getName()).append("&e[&a").append(HCFProfile.getByUuid(uuid).getKills().size()).append("&e]");
                            else builder.append("&7, ").append((of.isOnline()) ? "&a" : "&7").append(of.getName()).append("&e[&a").append(HCFProfile.getByUuid(uuid).getKills().size()).append("&e]");
                        }
                    }

                    if (pf.getAllies().size() > 0) {
                        for (UUID uuid : pf.getAllies()) {
                            PlayerFaction playerFaction = PlayerFaction.getPlayerFaction(uuid);
                            if (playerFaction != null) {
                                if (allies.length() == 0) allies.append(playerFaction.getName());
                                else allies.append(", ").append(playerFaction.getName());
                            }
                        }
                    }


                    for(String str : HCF.getInstance().getMessagesFile().getPlayerFactionShow()) {
                        if(str.contains("%coleaders%")) {
                            if(coleaders.length() == 0)
                                continue;
                        }
                        if(str.contains("%captains%")) {
                            if(captains.length() == 0)
                                continue;
                        }
                        if(str.contains("%members%")) {
                            if(members.length() == 0)
                                continue;
                        }
                        if(str.contains("%allies%")) {
                            if(allies.length() == 0)
                                continue;
                        }
                        if(str.contains("%points%")) {
                            if(pf.getPoints() == 0)
                                continue;
                        }
                        if(str.contains("%regen%")) {
                            if(pf.getStartRegen() == null)
                                continue;
                        }
                        if(str.contains("%lives%")) {
                            if(!pf.getMembers().containsKey(player.getUniqueId()) && !player.hasPermission("hcf.staff"))
                                continue;
                        }
                        if(str.contains("%announcement%")) {
                            if(!pf.getMembers().containsKey(player.getUniqueId()))
                                continue;
                        }
                        if(str.contains("%announcement%")) {
                            if(pf.getAnnouncement().length() == 0)
                                continue;
                        }
                        if(str.contains("%powerfac%")) {
                            if(!player.hasPermission("hcf.staff"))
                                continue;
                        }
                        if(str.contains("%open%")) {
                            if(!pf.isOpen())
                                continue;
                        }

                        String regen = "None";
                        if (pf.getStartRegen() != null) {
                            if (TimeUtils.getTimeTill(pf.getStartRegen()) != null) {
                                regen = TimeUtils.getTimeTill(pf.getStartRegen());
                            }
                        }

                        try {
                            player.sendMessage(C.color(str.replace("%primary%", ConfigValues.SERVER_PRIMARY)
                                    .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                                    .replace("%servername%", ConfigValues.SERVER_NAME)
                                    .replace("%servernamelower%", ConfigValues.SERVER_NAME.toLowerCase())
                                    .replace("%website%", ConfigValues.SERVER_WEBSITE)
                                    .replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK)
                                    .replace("%store%", ConfigValues.SERVER_STORE)
                                    .replace("%name%", pf.getName())
                                    .replace("%points%", String.valueOf(pf.getPoints()))
                                    .replace("%online%", String.valueOf(pf.getOnlineCount()))
                                    .replace("%players%", String.valueOf(pf.getMembers().size()))
                                    .replace("%home%", (pf.getHome() == null) ? "Not Set" : pf.getHomeString())
                                    .replace("%leader%", leader)
                                    .replace("%coleaders%", ((coleaders.length() > 0) ? coleaders.toString() : "null"))
                                    .replace("%captains%", ((captains.length() > 0) ? captains.toString() : "null"))
                                    .replace("%members%", ((members.length() > 0) ? members.toString() : "null"))
                                    .replace("%balance%", String.valueOf(pf.getBalance()))
                                    .replace("%dtr%", (pf.getDtr() <= 0.00 ? ChatColor.DARK_RED : ChatColor.GREEN) + String.valueOf(pf.getDtr()))
                                    .replace("%dtrsymbol%", pf.getDtrSymbol())
                                    .replace("%maxdtr%", String.valueOf(pf.getMaxDtr()))
                                    .replace("%regen%", regen)
                                    .replace("%lives%", String.valueOf(pf.getLives()))
                                    .replace("%kothcaptures%", String.valueOf(pf.getKothCaptures()))
                                    .replace("%open%", ((pf.isOpen()) ? "&aTrue" : "&cFalse"))
                                    .replace("%announcement%", pf.getAnnouncement())
                                    .replace("%powerfac%", ((pf.isPowerFaction()) ? "&aTrue" : "&cFalse"))
                                    .replace("%allies%", ((allies.length() > 0) ? allies.toString() : "null"))));
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    return;
                case SAFEZONE:
                case WARZONE:
                case KOTH:
                case CONQUEST:
                case ROAD:
                case SYSTEM:
                    SystemFaction sf = (SystemFaction) fac;
                    String displayName = sf.getColoredName();

                    for(String str : HCF.getInstance().getMessagesFile().getSystemFactionShow())
                        player.sendMessage(C.color(str.replace("%primary%", ConfigValues.SERVER_PRIMARY).replace("%secondary%", ConfigValues.SERVER_SECONDARY).replace("%servername%", ConfigValues.SERVER_NAME).replace("%servernamelower%", ConfigValues.SERVER_NAME.toLowerCase()).replace("%website%", ConfigValues.SERVER_WEBSITE).replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK).replace("%store%", ConfigValues.SERVER_STORE).replace("%name%", displayName).replace("%home%", (sf.getHome() == null && sf.getType() != FactionType.WARZONE) ? "Not Set" : sf.getHomeString())));
            }
        });
    }
}
