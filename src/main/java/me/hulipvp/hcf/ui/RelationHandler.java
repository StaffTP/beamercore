package me.hulipvp.hcf.ui;

import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.spigotmc.AsyncCatcher;

import java.util.*;

public class RelationHandler {

    public static void updateTab(Player player) {
        AsyncCatcher.enabled = false;
        Scoreboard scoreboard = player.getScoreboard();
        if(scoreboard == null)
            return;

        Team members = getOrCreateTeam(scoreboard, "members", ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_MEMBERS), true, false);
        Team allies = getOrCreateTeam(scoreboard, "allies", ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_ALLIES), false, false);
        Team tagged = getOrCreateTeam(scoreboard, "tagged", ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_TAGGED), false, false);
        Team enemies = getOrCreateTeam(scoreboard, "enemies", ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_ENEMIES), false, false);
        Team focused = getOrCreateTeam(scoreboard, "focused", ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_FOCUSED), false, false);

        HCFProfile profile = HCFProfile.getByPlayer(player);
        PlayerFaction faction = profile.getFactionObj();

        List<Player> team = new ArrayList<>();
        for(Player other : Bukkit.getOnlinePlayers()) {
            if (player.equals(other)) {
                if (!members.hasEntry(other.getName())) members.addEntry(other.getName());
                continue;
            }

            if (other.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                if (allies.hasEntry(other.getName())) allies.removeEntry(other.getName());
                if (tagged.hasEntry(other.getName())) tagged.removeEntry(other.getName());
                if (enemies.hasEntry(other.getName())) enemies.removeEntry(other.getName());
                if (focused.hasEntry(other.getName())) focused.removeEntry(other.getName());
                continue;
            }
            if (!player.canSee(other)) {
                if (allies.hasEntry(other.getName())) allies.removeEntry(other.getName());
                if (tagged.hasEntry(other.getName())) tagged.removeEntry(other.getName());
                if (enemies.hasEntry(other.getName())) enemies.removeEntry(other.getName());
                if (focused.hasEntry(other.getName())) focused.removeEntry(other.getName());
                continue;
            }


            boolean skip = false, isAlly = false, fullSkip = false;
            if (faction != null) {
                if (faction.getFocused() != null) {
                    if (Bukkit.getPlayer(faction.getFocused()) == null) {
                        faction.setFocused(null);
                    } else {
                        if (faction.getFocused().equals(other.getUniqueId())) {
                            if (!focused.hasEntry(other.getName()))
                                focused.addEntry(other.getName());

                            if (tagged.hasEntry(other.getName()))
                                tagged.removeEntry(other.getName());
                            if (members.hasEntry(other.getName()))
                                members.removeEntry(other.getName());
                            fullSkip = true;
                        } else {
                            if (focused.hasEntry(other.getName()))
                                focused.removeEntry(other.getName());
                        }
                    }
                } else {
                    if (focused.hasEntry(other.getName()))
                        focused.removeEntry(other.getName());
                }
                if (!fullSkip) {

                    if (faction.getMembers().containsKey(other.getUniqueId())) {
                        members.addEntry(other.getName());
                        team.add(other);
                        fullSkip = true;
                    } else if (members.hasEntry(other.getName())) {
                        members.removeEntry(other.getName());
                    }
                }

                if (!fullSkip) {
                    if (HCFProfile.getByPlayer(other).getFactionObj() != null) {
                        if (profile.getFactionObj().isAllied(HCFProfile.getByPlayer(other).getFactionObj().getUuid())) {
                            allies.addEntry(other.getName());
                            isAlly = true;
                            skip = true;
                        }
                    }
                }
                if (!isAlly && allies.hasEntry(other.getName()))
                    allies.removeEntry(other.getName());
            } else {
                if (members.hasEntry(other.getName()))
                    members.removeEntry(other.getName());

                if (allies.hasEntry(other.getName()))
                    allies.removeEntry(other.getName());
            }

            if (!fullSkip) {
                if (!HCFProfile.getProfiles().containsKey(other.getUniqueId().toString()))
                    continue;

                HCFProfile otherProfile = HCFProfile.getByPlayer(other);
                if (otherProfile.hasTimer(PlayerTimerType.ARCHERMARK)) {
                    tagged.addEntry(other.getName());
                    skip = true;
                } else if (tagged.hasEntry(other.getName())) {
                    tagged.removeEntry(other.getName());
                }

                if (!skip)
                    enemies.addEntry(other.getName());
                else if (enemies.hasEntry(other.getName()))
                    enemies.removeEntry(other.getName());

            }

           if (HCF.getInstance().getLunarHook() != null) {
                if (HCF.getInstance().getLunarHook().isLunarPlayer(player)) {
                    String relation = "";
                    if (members.hasEntry(other.getName())) {
                        relation = ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_MEMBERS).toString();
                    } else if (allies.hasEntry(other.getName())) {
                        relation = ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_ALLIES).toString();
                    } else if (tagged.hasEntry(other.getName())) {
                        relation = ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_TAGGED).toString();
                    } else if (enemies.hasEntry(other.getName())) {
                        relation = ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_ENEMIES).toString();
                    } else if (focused.hasEntry(other.getName())) {
                        relation = ChatColor.valueOf(ConfigValues.FACTIONS_COLORS_FOCUSED).toString();
                    }

                    if (relation != null) {
                        List<String> nameTagLines = new ArrayList<>();
                        if (player.hasPermission("hcf.lunar.nametags")) {
                            if (HCFProfile.getByPlayer(other).getVanish() != null && player.hasPermission("hcf.lunar.nametags.modmode")) {
                                nameTagLines.addAll(HCF.getInstance().getLunarFile().getConfig().getStringList("lunar.nametags.modmode.lines"));

                            } else if (Faction.getByPlayer(other.getName()) == null) {
                                nameTagLines.addAll(HCF.getInstance().getLunarFile().getConfig().getStringList("lunar.nametags.factionless.lines"));
                            } else {
                                PlayerFaction playerFaction = (PlayerFaction) Faction.getByPlayer(other.getName());
                                nameTagLines.addAll(HCF.getInstance().getLunarFile().getConfig().getStringList("lunar.nametags.faction.lines"));
                                replace(nameTagLines, "%other-faction-name%", playerFaction.getName());
                                replace(nameTagLines, "%other-faction-dtr-display%", playerFaction.getDtrDisplay());
                            }
                        } else {
                            nameTagLines.addAll(HCF.getInstance().getLunarFile().getConfig().getStringList("lunar.nametags.noperm.lines"));
                        }

                        replace(nameTagLines, "%other-relation%", relation);
                        replace(nameTagLines, "%other%", other.getName());
                        HCF.getInstance().getLunarHook().getLunarClientAPI().resetNametag(other, player);
                        HCF.getInstance().getLunarHook().getLunarClientAPI().overrideNametag(other, nameTagLines, player);
                    }
                }
            }
//            if (HCF.getInstance().getLunarHook() != null) {
//                if (HCF.getInstance().getLunarHook().isLunarPlayer(player)) {
//                    try {
//                        HCF.getInstance().getLunarHook().getLunarClientAPI().sendTeamMate(player, team.toArray(new Player[team.size()]));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        }
    }

    private static Team getOrCreateTeam(Scoreboard scoreboard, String name, ChatColor prefix, boolean allowInvis, boolean isInvis) {
        Team team = scoreboard.getTeam(name);
        if(team == null)
            team = scoreboard.registerNewTeam(name);

        team.setPrefix(prefix.toString());
        team.setCanSeeFriendlyInvisibles(allowInvis);

        return team;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(RelationHandler::updateTab);
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 40L, 40L);
    }

    public static void replace(List<String> list, String oldText, String newText) {
        ListIterator<String> it = list.listIterator();
        while(it.hasNext()) {
            it.set(CC.translate(it.next().replace(oldText,newText)));
        }
    }
}
