package me.hulipvp.hcf.utils;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.game.kits.type.BardKit;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class Placeholders {

    public static String replacePlaceholders(String line, Player player, HCFProfile profile) {
        line = line
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                .replace("%server_name%", ConfigValues.SERVER_NAME)
                .replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK)
                .replace("%store%", ConfigValues.SERVER_STORE)
                .replace("%player%", player.getName())
                .replace("%prefix%", HCF.getInstance().getPlayerHook().getRankPrefix(player))
                .replace("%suffix%", HCF.getInstance().getPlayerHook().getRankSuffix(player))
                .replace("%tag%", HCF.getInstance().getPlayerHook().getTag(player))
                .replace("%kills%", String.valueOf(profile.getKills().size()))
                .replace("%deaths%", String.valueOf(profile.getDeaths().size()))
                .replace("%streak%", String.valueOf(profile.getStreak()))
                .replace("%lives%", String.valueOf(profile.getLives()))
                .replace("%elo%", String.valueOf(profile.getElo()))
                .replace("%faction_balance%", (profile.getFactionObj() != null ? String.valueOf(profile.getFactionObj().getBalance()) : ""))
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%tps%", String.valueOf(new DecimalFormat("##.##").format(20.00)))
                .replace("%balance%", String.valueOf(profile.getBalance()))
                .replace("%location%", "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") " + LocUtils.getCardinalDirection(player))
                .replace("%is_kitmap%", Boolean.toString(ConfigValues.FEATURES_KITMAP))
                .replace("%active_class%", profile.getCurrentKit() != null ? profile.getCurrentKit().getName() : "")
                .replace("%has_active_class%", Boolean.toString(profile.getCurrentKit() != null))
                .replace("%is_bard%", Boolean.toString(BardKit.getPlayerEnergy().containsKey(player.getUniqueId().toString())))
                .replace("%bard_energy%", BardKit.getPlayerEnergy().containsKey(player.getUniqueId().toString()) ? BardKit.getPlayerEnergy().get(player.getUniqueId().toString()).toString() : "")
                .replace("%player_has_combat_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.COMBAT)))
                .replace("%player_combat_timer%", profile.getTimerByType(PlayerTimerType.COMBAT) != null ? profile.getTimerByType(PlayerTimerType.COMBAT).getDisplay() : "")
                .replace("%player_has_pearl_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.ENDERPEARL)))
                .replace("%player_pearl_timer%", profile.getTimerByType(PlayerTimerType.ENDERPEARL) != null ? profile.getTimerByType(PlayerTimerType.ENDERPEARL).getDisplay() : "")
                .replace("%player_has_apple_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.APPLE)))
                .replace("%player_apple_timer%", profile.getTimerByType(PlayerTimerType.APPLE) != null ? profile.getTimerByType(PlayerTimerType.APPLE).getDisplay() : "")
                .replace("%player_has_gapple_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.GAPPLE)))
                .replace("%player_gapple_timer%", profile.getTimerByType(PlayerTimerType.GAPPLE) != null ? profile.getTimerByType(PlayerTimerType.GAPPLE).getDisplay() : "")
                .replace("%player_has_spawn_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.SPAWN)))
                .replace("%player_spawn_timer%", profile.getTimerByType(PlayerTimerType.SPAWN) != null ? profile.getTimerByType(PlayerTimerType.SPAWN).getDisplay() : "")
                .replace("%player_has_logout_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.LOGOUT)))
                .replace("%player_logout_timer%", profile.getTimerByType(PlayerTimerType.LOGOUT) != null ? profile.getTimerByType(PlayerTimerType.LOGOUT).getDisplay() : "")
                .replace("%player_has_home_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.HOME)))
                .replace("%player_home_timer%", profile.getTimerByType(PlayerTimerType.HOME) != null ? profile.getTimerByType(PlayerTimerType.HOME).getDisplay() : "")
                .replace("%player_has_stuck_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.STUCK)))
                .replace("%player_stuck_timer%", profile.getTimerByType(PlayerTimerType.STUCK) != null ? profile.getTimerByType(PlayerTimerType.STUCK).getDisplay() : "")
                .replace("%player_has_archer-mark_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.ARCHERMARK)))
                .replace("%player_archer-mark_timer%", profile.getTimerByType(PlayerTimerType.ARCHERMARK) != null ? profile.getTimerByType(PlayerTimerType.ARCHERMARK).getDisplay() : "")
                .replace("%player_has_starting_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.STARTING)))
                .replace("%player_starting_timer%", profile.getTimerByType(PlayerTimerType.STARTING) != null ? profile.getTimerByType(PlayerTimerType.STARTING).getDisplay() : "")
                .replace("%player_has_pvptimer_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.PVPTIMER)))
                .replace("%player_pvptimer_timer%", profile.getTimerByType(PlayerTimerType.PVPTIMER) != null ? profile.getTimerByType(PlayerTimerType.PVPTIMER).getDisplay() : "")
                .replace("%player_has_bard-effect_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.BARD_EFFECT)))
                .replace("%player_bard-effect_timer%", profile.getTimerByType(PlayerTimerType.BARD_EFFECT) != null ? profile.getTimerByType(PlayerTimerType.BARD_EFFECT).getDisplay() : "")
                .replace("%player_has_speed-effect_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.SPEED_EFFECT)))
                .replace("%player_speed-effect_timer%", profile.getTimerByType(PlayerTimerType.SPEED_EFFECT) != null ? profile.getTimerByType(PlayerTimerType.SPEED_EFFECT).getDisplay() : "")
                .replace("%player_has_jump-effect_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.JUMP_EFFECT)))
                .replace("%player_jump-effect_timer%", profile.getTimerByType(PlayerTimerType.JUMP_EFFECT) != null ? profile.getTimerByType(PlayerTimerType.JUMP_EFFECT).getDisplay() : "")
                .replace("%player_has_backstab_timer%", Boolean.toString(profile.hasTimer(PlayerTimerType.BACKSTAB)))
                .replace("%player_backstab_timer%", profile.getTimerByType(PlayerTimerType.BACKSTAB) != null ? profile.getTimerByType(PlayerTimerType.BACKSTAB).getDisplay() : "")
                .replace("%server_has_sotw_timer%", Boolean.toString(ServerTimer.getTimer(ServerTimerType.SOTW) != null && ServerTimer.getTimer(ServerTimerType.SOTW).getTimeRemaining() > 2))
                .replace("%server_sotw_timer%", ServerTimer.getTimer(ServerTimerType.SOTW) != null && ServerTimer.getTimer(ServerTimerType.SOTW).getTimeRemaining() > 2 ? ServerTimer.getTimer(ServerTimerType.SOTW).getDisplay() : "")
                .replace("%server_has_eotw_timer%", Boolean.toString(ServerTimer.getTimer(ServerTimerType.EOTW) != null && ServerTimer.getTimer(ServerTimerType.EOTW).getTimeRemaining() > 2))
                .replace("%server_eotw_timer%", ServerTimer.getTimer(ServerTimerType.EOTW) != null && ServerTimer.getTimer(ServerTimerType.EOTW).getTimeRemaining() > 2 ? ServerTimer.getTimer(ServerTimerType.EOTW).getDisplay() : "")
                .replace("%server_has_sale_timer%", Boolean.toString(ServerTimer.getTimer(ServerTimerType.SALE) != null && ServerTimer.getTimer(ServerTimerType.SALE).getTimeRemaining() > 2))
                .replace("%server_sale_timer%", ServerTimer.getTimer(ServerTimerType.SALE) != null && ServerTimer.getTimer(ServerTimerType.SALE).getTimeRemaining() > 2 ? ServerTimer.getTimer(ServerTimerType.SALE).getDisplay() : "")
                .replace("%server_has_key-sale_timer%", Boolean.toString(ServerTimer.getTimer(ServerTimerType.KEY_SALE) != null && ServerTimer.getTimer(ServerTimerType.KEY_SALE).getTimeRemaining() > 2))
                .replace("%server_key-sale_timer%", ServerTimer.getTimer(ServerTimerType.KEY_SALE) != null && ServerTimer.getTimer(ServerTimerType.KEY_SALE).getTimeRemaining() > 2 ? ServerTimer.getTimer(ServerTimerType.KEY_SALE).getDisplay() : "")
                .replace("%server_has_key-all_timer%", Boolean.toString(ServerTimer.getTimer(ServerTimerType.KEY_ALL) != null && ServerTimer.getTimer(ServerTimerType.KEY_ALL).getTimeRemaining() > 2))
                .replace("%server_key-all_timer%", ServerTimer.getTimer(ServerTimerType.KEY_ALL) != null && ServerTimer.getTimer(ServerTimerType.KEY_ALL).getTimeRemaining() > 2 ? ServerTimer.getTimer(ServerTimerType.KEY_ALL).getDisplay() : "")
                .replace("%server_has_restart_timer%", Boolean.toString(ServerTimer.getTimer(ServerTimerType.RESTART) != null && ServerTimer.getTimer(ServerTimerType.RESTART).getTimeRemaining() > 2))
                .replace("%server_restart_timer%", ServerTimer.getTimer(ServerTimerType.RESTART) != null && ServerTimer.getTimer(ServerTimerType.RESTART).getTimeRemaining() > 2 ? ServerTimer.getTimer(ServerTimerType.RESTART).getDisplay() : "")
                .replace("%has_faction%", Boolean.toString(profile.getFactionObj() != null))
                .replace("%has_faction_home%", Boolean.toString(profile.getFactionObj() != null && profile.getFactionObj().getHome() != null))
                .replace("%faction_name%", profile.getFactionObj() != null ? profile.getFactionObj().getName() : "")
                .replace("%faction_dtr%", profile.getFactionObj() != null ? Double.toString(profile.getFactionObj().getDtr()) : "")
                .replace("%faction_online_count%", profile.getFactionObj() != null ? Integer.toString(profile.getFactionObj().getOnlineCount()) : "")
                .replace("%faction_total_count%", profile.getFactionObj() != null ? Integer.toString(profile.getFactionObj().getMembers().size()) : "")
                .replace("%faction_balance%", profile.getFactionObj() != null ? Integer.toString(profile.getFactionObj().getBalance()) : "")
                .replace("%faction_home%", (profile.getFactionObj() != null && profile.getFactionObj().getHome() != null) ? profile.getFactionObj().getHomeString() : "")
                .replace("%faction_at%", getLocation(player))
                .replace("%is_koth%", Boolean.toString(Koth.getActiveKoth() != null))
                .replace("%active_koth%", Koth.getActiveKoth() != null ? Koth.getActiveKoth().getName() : "")
                .replace("%active_koth_time%", Koth.getActiveKoth() != null ? TimeUtils.getFormatted(Koth.getActiveKoth().getTimer()) : "")
                .replace("%active_koth_home%", (Koth.getActiveKoth() != null && Koth.getActiveKoth().getFaction() != null && Koth.getActiveKoth().getFaction().getHome() != null) ? Koth.getActiveKoth().getFaction().getHomeString() : "");
        if (line.contains("%faction_online_member_")) {
            if (profile.getFactionObj() != null) {
                line = replaceFactionMemberPlaceholder(line, profile);
            } else {
                line = "";
            }
        } else if (line.contains("%faction_list_")) {
            line = replaceFactionListPlaceholder(line, player);
        } else if (line.contains("%has_permission_")) {
            line = replaceHasPermission(line, player);
        }
        return shouldDisplay(line);
    }

    public static String getLocation(Player player) {
        Faction currentTerritory = Faction.getByLocation(player.getLocation());
        if(currentTerritory != null) {
            if(currentTerritory instanceof PlayerFaction) {
                PlayerFaction pf = (PlayerFaction) currentTerritory;
                return pf.getRelationColor(player.getUniqueId()) + pf.getName();
            } else {
                if(currentTerritory instanceof SystemFaction) {
                    SystemFaction sf = (SystemFaction) currentTerritory;
                    return sf.getColoredName();
                } else {
                    return currentTerritory.getName();
                }
            }
        } else {
            return Locale.FACTION_WILDERNESS.toString();
        }
    }

    public static String replaceFactionMemberPlaceholder(String line, HCFProfile profile) {
        String newLine = line.split("%")[1].replace("faction_online_member_", "").replace("%", "");
        int number = Integer.parseInt(newLine) - 1;
        Player player;
        try {
            player = profile.getFactionObj().getOnlinePlayers().get(number);
        } catch (Exception e) {
            return "";
        }
        if (player != null) {
            String stars = profile.getFactionObj().getMembers().get(player.getUniqueId()).getStars();
            return line.replace("%faction_online_member_" + newLine + "%", player.getName() + "&7" + stars);
        } else {
            return "";
        }
    }

    public static String replaceFactionListPlaceholder(String line, Player player) {
        String newLine = line.split("%")[1].replace("faction_list_", "").replace("%", "");
        int number = Integer.parseInt(newLine) - 1;
        PlayerFaction pf;
        try {
            pf = PlayerFaction.getPlayerFaction(PlayerFaction.getSorted().get(number));
        } catch (Exception e) {
            return "";
        }
        if(pf != null) {
            if (pf.getOnlineCount() > 0) {
                return line.replace("%faction_list_" + newLine + "%", pf.getRelationColor(player) + pf.getName() + " &7(" + pf.getOnlineCount() + ")");
            }
        }
        return "";
    }

    public static String replaceHasPermission(String line, Player player) {
        String newLine = line.split("%")[1].replace("has_permission_", "").replace("%", "");
        return line.replace("%has_permission_" + newLine + "%", String.valueOf(player.hasPermission(newLine)));
    }

    public static String shouldDisplay(String line) {
        if (line.contains("<display=")) {
            String clearLine = line.split("<display=")[1];
            if (clearLine.equalsIgnoreCase("!true") || clearLine.equalsIgnoreCase("false")) return null;
            else return line.split("<display=")[0];
        } else {
            return line;
        }
    }

}
