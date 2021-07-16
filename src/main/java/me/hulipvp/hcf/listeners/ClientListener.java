package me.hulipvp.hcf.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/14/2021 / 8:24 AM
 * GlacialHCF / me.hulipvp.hcf.listeners
 */
public class ClientListener implements Listener {


        public ClientListener() {
            Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    HCFProfile profile = HCFProfile.getByPlayer(player);
                    Faction team = profile.getFactionObj();
                    int radiusX = 100;
                    int radiusY = 100;
                    int radiusZ = 100;
                    List<Entity> entityList = player.getNearbyEntities(radiusX, radiusY, radiusZ);
                    for (Entity en : entityList) {
                        if (en instanceof Player) {
                            Player other = (Player) en;
                            LunarClientAPI.getInstance().overrideNametag(other, Arrays.asList(fetchNametag(other, player).<String>toArray(new String[0])), player);
                        }
                    }
                }
            }, 0L, 20L);
        }

        public List<String> fetchNametag(Player target, Player viewer) {
            String nameTag = (target.hasMetadata("invisible") ? ChatColor.GRAY + "*" : "") + target.getName();
            List<String> tag = new ArrayList<>();

            PlayerFaction viewerTeam = HCFProfile.getByPlayer(viewer).getFactionObj();
            PlayerFaction targetTeam = HCFProfile.getByPlayer(target).getFactionObj();
            if (HCFProfile.getByPlayer(target).getVanish() != null) {
                tag.add((ChatColor.GRAY + "[Mod Mode]"));
            }
            if (targetTeam != null) {
                tag.add(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + targetTeam.getName() + ChatColor.GRAY + " \u2758 " + getDTRColor(targetTeam) + targetTeam.getDtr() + getDTRSuffix(targetTeam) + ChatColor.GOLD + "]");
            }
            tag.add(nameTag);
            return tag;
        }

        public static ChatColor getDTRColor(PlayerFaction team) {
            ChatColor dtrColor = ChatColor.GREEN;

            if (team.getDtr() / team.getMaxDtr() <= 0.25) {
                if (team.isRaidable()) {
                    dtrColor = ChatColor.DARK_RED;
                } else {
                    dtrColor = ChatColor.YELLOW;
                }
            }

            return (dtrColor);
        }


        public static String getDTRSuffix(PlayerFaction team) {
            if (team.isRegening()) {
                if (team.getOnlineCount() == 0) {
                    return (ChatColor.GRAY + "◀");
                } else {
                    return (ChatColor.GREEN + "▲");
                }
            } else if (team.getDtr() != team.getMaxDtr()) {
                return (ChatColor.RED + "■");
            } else {
                return (ChatColor.GREEN + "◀");
            }


        }
    }


