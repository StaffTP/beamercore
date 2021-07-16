package me.hulipvp.hcf.api;

import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.kits.type.BardKit;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.listeners.FactionListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HCFAPI {

    public static String getFactionName(Player player) {
        Faction faction = Faction.getByPlayer(player.getName());
        return (faction != null) ? faction.getName() : "";
    }

    public static boolean hasFaction(Player player) {
        return Faction.getByPlayer(player.getName()) != null;
    }

    public static List<UUID> getTeamMembers(Player player) {
        Faction faction = Faction.getByPlayer(player.getName());

        if (faction == null) return new ArrayList<>();

        PlayerFaction playerFaction = (PlayerFaction) faction;
        return playerFaction.getMembers().values().stream().map(FactionMember::getUuid).collect(Collectors.toList());
    }

    public static List<Player> getOnlineTeamMembers(Player player) {
        Faction faction = Faction.getByPlayer(player.getName());

        if (faction == null) return new ArrayList<>();

        PlayerFaction playerFaction = (PlayerFaction) faction;
        return playerFaction.getOnlinePlayers();
    }

    public static boolean isInSafezone(Player player) {
        return isInSafezone(player.getLocation());
    }

    public static boolean isInSafezone(Location location) {
        return Faction.getByLocation(location) instanceof SafezoneFaction;
    }

    public static boolean isVanished(Player player) {
        return HCFProfile.getByUuid(player.getUniqueId()).getVanish() != null
                && HCFProfile.getByUuid(player.getUniqueId()).getVanish().isVanished();
    }

    public static boolean isLeader(Player player) {
        Faction playerFaction = Faction.getByPlayer(player.getName());

        if (!(playerFaction instanceof PlayerFaction)) return false;

        if (((PlayerFaction) playerFaction).getLeader().getUuid().equals(player.getUniqueId())) return true;
        else return false;
    }

    public static boolean isOwnFactionClaim(Player player, Location location) {
        Faction faction = Faction.getByLocation(location);
        Faction playerFaction = Faction.getByPlayer(player.getName());

        if ((playerFaction != null && faction != null) && playerFaction.getUuid() == faction.getUuid()) return true;
        else return false;
    }

    public static Integer getLives(UUID uuid) {
        return HCFProfile.getByUuid(uuid).getLives();
    }

    public static Long getDeathban(UUID uuid) {
        HCFProfile profile = HCFProfile.getByUuid(uuid);
        if (profile.getBannedTill() != 0 && profile.getBannedTill() > System.currentTimeMillis()) {
            return profile.getBannedTill();
        } else {
            return 0L;
        }
    }

    public static String getArmorClass(Player player) {
        HCFProfile profile = HCFProfile.getByUuid(player.getUniqueId());
        return profile.getCurrentKit() != null ? profile.getCurrentKit().getName() : "";
    }

    public static boolean isArcherTagged(Player player) {
        HCFProfile profile = HCFProfile.getByUuid(player.getUniqueId());
        return profile.hasTimer(PlayerTimerType.ARCHERMARK);
    }

    public static boolean canBuildAt(Player player, Location location) {
        return FactionListener.handleBlockEvent(player, location) == null;
    }

    public static void setRestoreEffect(Player target, PotionEffect effect) {
        BardKit.applyEffect(target, effect);
    }

    public static boolean canHurtOtherPlayer(Player damager, Player damaged) {
        if (isInSafezone(damager) || isInSafezone(damaged)) return false;
        if (getOnlineTeamMembers(damager).contains(damaged)) return false;

        for (Player player : Arrays.asList(damager, damaged)) {
            HCFProfile profile = HCFProfile.getByUuid(player.getUniqueId());

            if (profile.hasTimer(PlayerTimerType.PVPTIMER)) {
                return false;
            } else if (profile.hasTimer(PlayerTimerType.STARTING)) {
                return false;
            } else if (ServerTimer.isSotw()) {
                return false;
            }

        }

        return true;
    }

}
