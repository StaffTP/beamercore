package me.hulipvp.hcf.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCNotification;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.lunarclient.bukkitapi.object.TitleType;
import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.faction.normal.FactionCreateEvent;
import me.hulipvp.hcf.api.events.faction.normal.FactionDisbandEvent;
import me.hulipvp.hcf.api.events.faction.normal.FactionJoinEvent;
import me.hulipvp.hcf.api.events.faction.normal.FactionLeaveEvent;
import me.hulipvp.hcf.api.events.faction.player.FactionKickEvent;
import me.hulipvp.hcf.api.events.faction.player.FactionPromoteEvent;
import me.hulipvp.hcf.api.events.koth.KothEndEvent;
import me.hulipvp.hcf.api.events.koth.KothStartEvent;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/5/2021 / 3:52 PM
 * vhcf / me.hulipvp.hcf.listeners
 */
public class LunarClientListener implements Listener {

    @Getter
    public LunarClientListener instance = this;

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);
        PlayerFaction playerFaction = profile.getFactionObj();
        LunarClientAPI api = LunarClientAPI.getInstance();
        updateWaypoints(player);
        api.sendWaypoint(player, new LCWaypoint("Spawn",
                0,
                70, 0,
                player.getWorld().getUID().toString(),
                5635840,
                true,
                true));
        LunarClientAPI.getInstance().sendTitle(player, TitleType.TITLE, CC.translate(HCF.getInstance().getConfig().getString("LunarJoinTitle")), Duration.ofSeconds(3));
        api.setBossbar(player, CC.translate("&7Welcome to &d&lGlacial &fMap 2&7!"), Integer.MAX_VALUE);
        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), new Runnable() {
            @Override
            public void run() {
                api.unsetBossbar(player);
            }
        }, 5 * 20);
    }

    @EventHandler
    public void on(FactionJoinEvent event) {
        Player player = event.getPlayer();
        LunarClientAPI api = LunarClientAPI.getInstance();
        api.sendTitle(player, TitleType.TITLE, CC.translate("&eYou have joined &d" + event.getFaction().getName() + "&e."), Duration.ofSeconds(3));
    }

    @EventHandler
    public void on(FactionCreateEvent event) {
        Player player = event.getPlayer();
        LunarClientAPI api = LunarClientAPI.getInstance();
        api.sendTitle(player, TitleType.TITLE, CC.translate("&aYou created a faction!"), Duration.ofSeconds(3));

    }

    @EventHandler
    public void on(FactionKickEvent event) {
        Player player = event.getPlayer();
        LunarClientAPI api = LunarClientAPI.getInstance();
        api.sendTitle(event.getPlayer(), TitleType.TITLE, CC.translate("&cYou have been kicked from &d" + event.getFaction().getName() + "&c!"), Duration.ofSeconds(3));

    }

    @EventHandler
    public void on(KothStartEvent event) {
        LunarClientAPI api = LunarClientAPI.getInstance();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (api.isRunningLunarClient(player)) {
                Faction sf = Faction.getByName(event.getKoth().getName());
                api.sendTitle(player, TitleType.TITLE, CC.translate("&9" + event.getKoth().getName() + " &eKOTH has started!"), Duration.ofSeconds(3));
                api.sendWaypoint(player, new LCWaypoint(sf.getName() + " KOTH",
                        sf.getHome().getBlockX(),
                        sf.getHome().getBlockY(), sf.getHome().getBlockZ(),
                       sf.getHome().getWorld().getUID().toString(),
                        16776960,
                        true,
                        true));
            }

        }
    }
    @EventHandler
    public void on(KothEndEvent event) {
        LunarClientAPI api = LunarClientAPI.getInstance();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (api.isRunningLunarClient(player)) {
                Faction sf = Faction.getByName(event.getKoth().getName());
                api.removeWaypoint(player, new LCWaypoint(sf.getName() + " KOTH",
                        sf.getHome().getBlockX(),
                        sf.getHome().getBlockY(), sf.getHome().getBlockZ(),
                        sf.getHome().getWorld().getUID().toString(),
                        16776960,
                        true,
                        true));
            }

        }
    }

    @EventHandler
    public void on(FactionPromoteEvent event) {
        Player player = event.getPlayer();
        LunarClientAPI api = LunarClientAPI.getInstance();
        api.sendTitle(player, TitleType.TITLE, CC.translate("&eYou have been promoted to &9" + event.getPromoted().getRank().name()), Duration.ofSeconds(3));

    }



    @EventHandler
    public void on(FactionDisbandEvent event) {
        Player player = event.getPlayer();
        LunarClientAPI api = LunarClientAPI.getInstance();
        api.sendTitle(player, TitleType.TITLE, CC.translate("&cFaction Disbanded!"), Duration.ofSeconds(3));

    }

    public void updateWaypoints(Player player) {
        HCFProfile profile = HCFProfile.getByPlayer(player);
        PlayerFaction playerFaction = profile.getFactionObj();
        LunarClientAPI api = LunarClientAPI.getInstance();

        if (playerFaction != null) {

            if (playerFaction.getRallyPoint() != null) {
                Location loc = playerFaction.getRallyPoint();
                api.sendWaypoint(player, new LCWaypoint("Rally Point",
                        loc.getBlockX(),
                        loc.getBlockY(), loc.getBlockZ(),
                        loc.getWorld().getUID().toString(),
                        15073510,
                        true,
                        true));


            }

            if (playerFaction.getFactionFocus() != null) {
                Location loc = playerFaction.getFactionFocus().getHome();
                api.sendWaypoint(player, new LCWaypoint(playerFaction.getFactionFocus().getName() + "'s HQ",
                        loc.getBlockX(),
                        loc.getBlockY(),
                        loc.getBlockZ(),
                        loc.getWorld().getUID().toString(),
                        13369344,
                        true,
                        true));

            }

            if (playerFaction.getHome() != null) {
                Location loc = playerFaction.getHome();
                api.sendWaypoint(player, new LCWaypoint("Home",
                        loc.getBlockX(),
                        loc.getBlockY(),
                        loc.getBlockZ(),
                        loc.getWorld().getUID().toString(),
                        -16776961,
                        true,
                        true));


            }


        }



    }



}
