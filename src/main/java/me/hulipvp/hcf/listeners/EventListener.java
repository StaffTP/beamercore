package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.api.events.conquest.ConquestControlEvent;
import me.hulipvp.hcf.api.events.conquest.ConquestEndEvent;
import me.hulipvp.hcf.api.events.conquest.ConquestKnockEvent;
import me.hulipvp.hcf.api.events.conquest.ConquestStartEvent;
import me.hulipvp.hcf.api.events.dtc.DTCEndEvent;
import me.hulipvp.hcf.api.events.dtc.DTCStartEvent;
import me.hulipvp.hcf.api.events.faction.normal.FactionDisbandEvent;
import me.hulipvp.hcf.api.events.koth.KothControlEvent;
import me.hulipvp.hcf.api.events.koth.KothEndEvent;
import me.hulipvp.hcf.api.events.koth.KothKnockEvent;
import me.hulipvp.hcf.api.events.koth.KothStartEvent;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.conquest.data.ConquestEndReason;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZone;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.dtc.data.DTCEndReason;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.koth.data.KothEndReason;
import me.hulipvp.hcf.game.event.koth.data.KothPoint;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.event.ConquestFaction;
import me.hulipvp.hcf.game.faction.type.event.DTCFaction;
import me.hulipvp.hcf.game.faction.type.event.KothFaction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

    /*=============================*/
    // Conquest

    @EventHandler
    public void onConquestDisband(FactionDisbandEvent e) {
        if (Conquest.getActiveConquest() != null) {
            Conquest conquest = Conquest.getActiveConquest();
            conquest.getPoints().remove(e.getFaction().getUuid().toString());
        }
    }

    @EventHandler
    public void onConquestStart(ConquestStartEvent e) {
        Bukkit.broadcastMessage(Locale.EVENT_CONQUEST_STARTED.toString().replace("%name%", e.getConquest().getName()));
    }

    @EventHandler
    public void onConquestControl(ConquestControlEvent e) {
        /*Bukkit.broadcastMessage(Locale.EVENT_CONQUEST_CONTROL.toString()
                .replace("%zone%", e.getZone().getType().getDisplay())
                .replace("%faction%", HCFProfile.getByPlayer(e.getPlayer()).getFactionObj().getName())
        );*/

        PlayerFaction faction = HCFProfile.getByPlayer(e.getPlayer()).getFactionObj();
        faction.sendMessage(Locale.EVENT_CONQUEST_CONTROL_TEAM.toString().replace("%zone%", e.getZone().getType().getDisplay()));
    }

    @EventHandler
    public void onConquestKnock(ConquestKnockEvent e) {
        if (e.getKnockTime() < 10) {
            Bukkit.broadcastMessage(Locale.EVENT_CONQUEST_KNOCK.toString()
                    .replace("%zone%", e.getZone().getType().getDisplay())
                    .replace("%faction%", HCFProfile.getByPlayer(e.getPlayer()).getFactionObj().getName())
            );
        }
    }

    @EventHandler
    public void onConquestMove(PlayerMoveEvent e) {

        if (Conquest.getActiveConquest() == null)
            return;

        Faction fac = Faction.getByLocation(e.getPlayer().getLocation());
        HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());
        if (profile.hasTimer(PlayerTimerType.PVPTIMER) || profile.hasTimer(PlayerTimerType.STARTING))
            return;
        if (profile.getFactionObj() == null)
            return;

        if (fac != null) {
            if (!(fac instanceof ConquestFaction))
                return;

            ConquestFaction cf = (ConquestFaction) fac;
            ConquestZone zone = cf.getConquest().getZoneAt(e.getPlayer().getLocation());

            if (zone != null) {
                if (!zone.getCappers().contains(e.getPlayer().getUniqueId().toString())) {
                    zone.getCappers().add(e.getPlayer().getUniqueId().toString());
                    e.getPlayer().sendMessage(Locale.CAPZONE_ENTRY.toString().replace("%zone%", zone.getType().getDisplay()));
                }

                if (zone.getCapper() == null) {
                    zone.setCapper(e.getPlayer().getUniqueId());

                    ConquestControlEvent event = new ConquestControlEvent(cf.getConquest(), zone, e.getPlayer());
                    Bukkit.getServer().getPluginManager().callEvent(event);
                }
            } else {
                for (ConquestZone zones : cf.getConquest().getZones().values()) {
                    if (zones.getCappers().contains(e.getPlayer().getUniqueId().toString())) {
                        zones.getCappers().remove(e.getPlayer().getUniqueId().toString());
                        e.getPlayer().sendMessage(Locale.CAPZONE_LEFT.toString().replace("%zone%", zones.getType().getDisplay()));
                    }

                    if (zones.getCapper() == null)
                        continue;

                    if (zones.getCapper().toString().equalsIgnoreCase(e.getPlayer().getUniqueId().toString())) {
                        int time = (int) TimeUnit.MILLISECONDS.toSeconds(zones.getTimer());

                        zones.setCapper(null);
                        zones.resetTimer();

                        ConquestKnockEvent event = new ConquestKnockEvent(cf.getConquest(), zones, time, e.getPlayer());
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onConquestEnd(ConquestEndEvent e) {
        if (e.getReason() == ConquestEndReason.CAPTURED) {

            Bukkit.broadcastMessage(Locale.valueOf("EVENT_CONQUEST_" + e.getReason().name()).toString()
                    .replace("%name%", e.getConquest().getName())
            );
            if (e.getCapper() != null) {
                Player capper = e.getCapper();
                PlayerFaction capperFaction = HCFProfile.getByUuid(capper.getUniqueId()).getFactionObj();

                if (ConfigValues.ELO_CONQUEST_ENABLED) {
                    if (ConfigValues.ELO_CONQUEST_CAPPER_ONLY) {
                        HCFProfile.getByUuid(capper.getUniqueId()).addToElo(ConfigValues.ELO_CONQUEST_POINTS);
                    } else {
                        for (Player player : capperFaction.getOnlinePlayers()) {
                            HCFProfile.getByPlayer(player).addToElo(ConfigValues.ELO_CONQUEST_POINTS);
                        }
                    }
                }

                capperFaction.setPoints(capperFaction.getPoints() + ConfigValues.FACTIONS_POINTS_WORLD_CONQUEST_POINTS);


                TaskUtils.runSync(() -> {
                    for (String command : ConfigValues.CONQUEST_ON_CAPTURE) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command
                                .replace("%name%", capper.getName())
                                .replace("%faction%", capperFaction.getName())
                                .replace("%conquest%", e.getConquest().getName())
                        );
                    }
                });
            }
        }
    }

    @EventHandler
    public void onConquestDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        HCFProfile profile = HCFProfile.getByPlayer(player);
        Faction fac = Faction.getByLocation(player.getLocation());
        if (profile.getFactionObj() == null)
            return;

        if (fac != null) {
            if (!(fac instanceof ConquestFaction))
                return;

            ConquestFaction cf = (ConquestFaction) fac;
            ConquestZone zone = cf.getConquest().getZoneAt(player.getLocation());

            if (zone != null) {
                zone.getCappers().remove(player.getUniqueId().toString());

                if (zone.getCapper().toString().equals(player.getUniqueId().toString())) {
                    zone.setCapper(null);
                    zone.resetTimer();

                    if (cf.getConquest().getPoints().containsKey(profile.getFactionObj().toString()))
                        if (profile.getFactionObj().getOnlinePlayers().size() == 0) {
                            cf.getConquest().getPoints().remove(profile.getFactionObj().toString());
                        } else {
                            int points = (cf.getConquest().getPoints().getOrDefault(profile.getFactionObj().toString(), 0)) - ConfigValues.CONQUEST_POINTS_LOSS_PER_DEATH;
                            cf.getConquest().getPoints().put(profile.getFactionObj().getUuid().toString(), points);
                        }
                }
            }
        }
    }
    /*=============================*/

    /*=============================*/
    // Koth

    @EventHandler
    public void onKothControl(KothControlEvent e) {
        /*Bukkit.broadcastMessage(Locale.EVENT_KOTH_CONTROL.toString()
                .replace("%name%", e.getKoth().getName())
                .replace("%faction%", HCFProfile.getByPlayer(e.getPlayer()).getFactionObj().getName())
        );*/

        PlayerFaction faction = HCFProfile.getByPlayer(e.getPlayer()).getFactionObj();
        faction.sendExcludingMessage(e.getPlayer(), Locale.EVENT_KOTH_CONTROL_TEAM.toString().replace("%name%", e.getKoth().getName()));

        e.getPlayer().sendMessage(Locale.EVENT_KOTH_CONTROL_PLAYER.toString().replace("%name%", e.getKoth().getName()));
    }

    @EventHandler
    public void onKothKnock(KothKnockEvent e) {
        if (TimeUnit.MILLISECONDS.toMinutes(e.getKoth().getTimer()) <= 5)
            Bukkit.broadcastMessage(Locale.EVENT_KOTH_KNOCK.toString()
                    .replace("%name%", e.getKoth().getName())
                    .replace("%faction%", HCFProfile.getByPlayer(e.getPlayer()).getFactionObj().getName())
            );
    }

    @EventHandler
    public void onKothMove(PlayerMoveEvent e) {

        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {

            if (Koth.getActiveKoth() == null)
                return;

            Faction fac = Faction.getByLocation(e.getPlayer().getLocation());
            HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());

            if (profile.hasTimer(PlayerTimerType.PVPTIMER) || profile.hasTimer(PlayerTimerType.STARTING))
                return;

            if (profile.getFactionObj() == null)
                return;

            if (fac != null) {
                if (!(fac instanceof KothFaction))
                    return;


                KothFaction kf = (KothFaction) fac;
                if (kf.getKoth().getPoint().toCuboid() == null)
                    return;

                if (!Koth.getActiveKoth().equals(kf.getKoth())) {
                    return;
                }

                if (kf.getKoth().getPoint().toCuboid().isInCuboid(e.getPlayer())) {
                    if (kf.getKoth().getCappers().contains(e.getPlayer().getUniqueId().toString())) {
                        kf.getKoth().getCappers().add(e.getPlayer().getUniqueId().toString());
                        e.getPlayer().sendMessage(Locale.CAPZONE_ENTRY.toString().replace("%zone%", kf.getColoredName()));
                    }

                    if (kf.getKoth().getCapper() == null) {
                        kf.getKoth().setCapper(e.getPlayer().getUniqueId());

                        KothControlEvent event = new KothControlEvent(kf.getKoth(), e.getPlayer());
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    }
                } else {
                    if (kf.getKoth().getCappers().contains(e.getPlayer().getUniqueId().toString())) {
                        kf.getKoth().getCappers().remove(e.getPlayer().getUniqueId().toString());
                        e.getPlayer().sendMessage(Locale.CAPZONE_LEFT.toString().replace("%zone%", kf.getColoredName()));
                    }

                    if (kf.getKoth().getCapper() != null) {
                        if (kf.getKoth().getCapper().toString().equals(e.getPlayer().getUniqueId().toString())) {
                            KothKnockEvent event = new KothKnockEvent(kf.getKoth(), e.getPlayer());
                            Bukkit.getServer().getPluginManager().callEvent(event);

                            kf.getKoth().setCapper(null);
                            kf.getKoth().resetTimer();
                        }
                    }

                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onKothTeleport(PlayerTeleportEvent e) {

        if (Koth.getActiveKoth() == null)
            return;

        Faction fac = Faction.getByLocation(e.getFrom());
        HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());

        if (profile.hasTimer(PlayerTimerType.PVPTIMER) || profile.hasTimer(PlayerTimerType.STARTING))
            return;

        if (profile.getFactionObj() == null)
            return;

        if (fac != null) {
            if (!(fac instanceof KothFaction))
                return;


            KothFaction kf = (KothFaction) fac;
            if (kf.getKoth().getPoint().toCuboid() == null)
                return;

            if (!Koth.getActiveKoth().equals(kf.getKoth())) {
                return;
            }

            if (!kf.getKoth().getPoint().toCuboid().isInCuboid(e.getTo())) {

                if (kf.getKoth().getCapper() != null) {
                    if (kf.getKoth().getCapper().toString().equals(e.getPlayer().getUniqueId().toString())) {
                        KothKnockEvent event = new KothKnockEvent(kf.getKoth(), e.getPlayer());
                        Bukkit.getServer().getPluginManager().callEvent(event);

                        kf.getKoth().setCapper(null);
                        kf.getKoth().resetTimer();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKothDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        HCFProfile profile = HCFProfile.getByPlayer(player);
        Faction fac = Faction.getByLocation(player.getLocation());
        if (profile.getFactionObj() == null)
            return;

        if (fac != null) {
            if (!(fac instanceof KothFaction))
                return;

            KothFaction kf = (KothFaction) fac;
            Koth koth = kf.getKoth();
            KothPoint point = koth.getPoint();

            if (point != null && point.toCuboid() != null && point.toCuboid().isInCuboid(player)) {
                koth.getCappers().remove(player.getUniqueId().toString());

                if (koth.getCapper().equals(player.getUniqueId())) {
                    KothKnockEvent event = new KothKnockEvent(kf.getKoth(), player);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    koth.setCapper(null);
                    koth.resetTimer();
                }
            }
        }
    }

    @EventHandler
    public void onFactionDisband(FactionDisbandEvent e) {
        PlayerFaction playerFaction = PlayerFaction.getPlayerFaction(e.getFaction().getUuid());
        for (FactionMember factionMember : playerFaction.getMembers().values()) {
            Player player = Bukkit.getPlayer(factionMember.getUuid());
            if (player == null || !player.isOnline()) break;
            Faction fac = Faction.getByLocation(player.getLocation());

            if (fac != null) {
                if (fac instanceof KothFaction) {
                    KothFaction kf = (KothFaction) fac;
                    Koth koth = kf.getKoth();
                    KothPoint point = koth.getPoint();

                    if (point != null && point.toCuboid() != null && point.toCuboid().isInCuboid(player)) {
                        koth.getCappers().remove(player.getUniqueId().toString());

                        if (koth.getCapper().equals(player.getUniqueId())) {
                            KothKnockEvent event = new KothKnockEvent(kf.getKoth(), player);
                            Bukkit.getServer().getPluginManager().callEvent(event);

                            koth.setCapper(null);
                            koth.resetTimer();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKothEnd(KothEndEvent e) {
        if (e.getReason() == KothEndReason.CAPTURED) {
            if (e.getKoth().getCapper() != null) {
                if (Bukkit.getPlayer(e.getKoth().getCapper()) != null) {
                    Player capper = Bukkit.getPlayer(e.getKoth().getCapper());
                    PlayerFaction capperFaction = HCFProfile.getByUuid(capper.getUniqueId()).getFactionObj();

                    capperFaction.setKothCaptures(capperFaction.getKothCaptures() + 1);

                    if (ConfigValues.ELO_KOTH_ENABLED) {
                        if (ConfigValues.ELO_KOTH_CAPPER_ONLY) {
                            HCFProfile.getByUuid(capper.getUniqueId()).addToElo(ConfigValues.ELO_KOTH_POINTS);
                        } else {
                            for (Player player : capperFaction.getOnlinePlayers()) {
                                HCFProfile.getByPlayer(player).addToElo(ConfigValues.ELO_KOTH_POINTS);
                            }
                        }
                    }

                    if (e.getKoth().getName().equalsIgnoreCase("citadel")) {
                        capperFaction.setPoints(capperFaction.getPoints() + ConfigValues.FACTIONS_POINTS_WORLD_CITADEL_POINTS);
                    } else {
                        if (capper.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                            capperFaction.setPoints(capperFaction.getPoints() + ConfigValues.FACTIONS_POINTS_WORLD_KOTH_POINTS);
                        } else if (capper.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                            capperFaction.setPoints(capperFaction.getPoints() + ConfigValues.FACTIONS_POINTS_NETHER_KOTH_POINTS);
                        } else {
                            capperFaction.setPoints(capperFaction.getPoints() + ConfigValues.FACTIONS_POINTS_END_KOTH_POINTS);
                        }
                    }

                    TaskUtils.runSync(() -> {
                        for (String command : ConfigValues.KOTH_ON_CAPTURE) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command
                                    .replace("%name%", capper.getName())
                                    .replace("%faction%", capperFaction.getName())
                                    .replace("%koth%", e.getKoth().getName())
                            );
                        }
                    });
                }
            }
        }

        Bukkit.broadcastMessage(Locale.valueOf("EVENT_KOTH_" + e.getReason().name()).toString()
                .replace("%name%", e.getKoth().getName())
        );
    }

    @EventHandler
    public void onKothStart(KothStartEvent e) {
        String kothStartString = Locale.EVENT_KOTH_STARTED.toString();
        if (!kothStartString.contains(";")) {
            Bukkit.broadcastMessage(kothStartString.replace("%name%", e.getKoth().getName()));
        } else {
            String[] lines = kothStartString.split(";");
            Arrays.stream(lines).forEach(line -> Bukkit.broadcastMessage(line.replace("%name%", e.getKoth().getName())));
        }
    }
    /*=============================*/


    /*=============================*/
    // DTC
    @EventHandler
    public void onDTCStart(DTCStartEvent e) {
        Bukkit.broadcastMessage(Locale.EVENT_DTC_STARTED.toString().replace("%name%", e.getDtc().getName()));
    }

    @EventHandler
    public void onDTCBreak(BlockBreakEvent e) {
        if (e.getPlayer() == null)
            return;

        if (DTC.getActiveDTC() == null)
            return;

        Faction fac = Faction.getByLocation(e.getBlock().getLocation());
        HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());

        if (profile.hasTimer(PlayerTimerType.PVPTIMER) || profile.hasTimer(PlayerTimerType.STARTING))
            return;

        if (profile.getFactionObj() == null)
            return;

        if (fac != null) {
            if (!(fac instanceof DTCFaction))
                return;


            DTCFaction df = (DTCFaction) fac;

            if (!DTC.getActiveDTC().equals(df.getDtc())) {
                return;
            }

            DTC dtc = DTC.getActiveDTC();

            if (e.getBlock().getType().name().equals(ConfigValues.DTC_MATERIAL)) {
                if (profile.getFactionObj() != null) {

                    int points = (dtc.getPoints().getOrDefault(profile.getFactionObj().getUuid().toString(), 0)) + ConfigValues.DTC_POINTS_PER_CORE_BREAK;
                    dtc.getPoints().put(profile.getFactionObj().getUuid().toString(), points);

                    if ((points % 5) == 0) {
                        Bukkit.broadcastMessage(Locale.EVENT_DTC_CAPTURED.toString()
                                .replace("%points%", String.valueOf(points))
                                .replace("%name%", profile.getFactionObj().getName()));
                    }

                    if (points >= ConfigValues.DTC_POINTS_NEEDED_TO_WIN) {
                        dtc.setFinalCapper(profile.getPlayer().getUniqueId());
                        dtc.win();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDTCEnd(DTCEndEvent e) {
        if (e.getReason() == DTCEndReason.WON) {
            OfflinePlayer capper = Bukkit.getOfflinePlayer(e.getDtc().getFinalCapper());
            PlayerFaction capperFaction = HCFProfile.getByUuid(capper.getUniqueId()).getFactionObj();

            if (ConfigValues.ELO_DTC_ENABLED) {
                if (ConfigValues.ELO_DTC_CAPPER_ONLY) {
                    HCFProfile.getByUuid(capper.getUniqueId()).addToElo(ConfigValues.ELO_DTC_POINTS);
                } else {
                    for (Player player : capperFaction.getOnlinePlayers()) {
                        HCFProfile.getByPlayer(player).addToElo(ConfigValues.ELO_DTC_POINTS);
                    }
                }
            }

            TaskUtils.runSync(() -> {
                for (String command : ConfigValues.DTC_ON_WIN) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command
                            .replace("%name%", capper.getName())
                            .replace("%faction%", capperFaction.getName())
                            .replace("%dtc%", e.getDtc().getName())
                    );
                }
            });
        }

        Bukkit.broadcastMessage(Locale.valueOf("EVENT_DTC_" + e.getReason().name()).toString()
                .replace("%name%", e.getDtc().getName())
        );
    }

    @EventHandler
    public void onDTRDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        HCFProfile profile = HCFProfile.getByPlayer(player);
        Faction fac = Faction.getByLocation(player.getLocation());
        if (profile.getFactionObj() == null)
            return;

        if (fac != null) {
            if (!(fac instanceof DTCFaction))
                return;

            DTCFaction df = (DTCFaction) fac;

            if (df.getDtc().getPoints().containsKey(profile.getFactionObj().toString())) {
                if (profile.getFactionObj().getOnlinePlayers().size() == 0) {
                    df.getDtc().getPoints().remove(profile.getFactionObj().toString());
                } else {
                    int points = (df.getDtc().getPoints().getOrDefault(profile.getFactionObj().getUuid().toString(), 0)) - ConfigValues.CONQUEST_POINTS_LOSS_PER_DEATH;
                    df.getDtc().getPoints().put(profile.getFactionObj().getUuid().toString(), points);
                }
            }
        }
    }
    /*=============================*/

    public static void removeFromConquestCap(Player p) {
        if (Conquest.getActiveConquest() != null) {
            for (ConquestZone zone : Conquest.getActiveConquest().getZones().values()) {
                if (zone.getCapper() != null)
                    if (zone.getCapper().toString().equals(p.getUniqueId().toString()))
                        zone.setCapper(null);

                for (String uuid : zone.getCappers()) {
                    if (uuid.equals(p.getUniqueId().toString()))
                        zone.getCappers().remove(uuid);
                }
            }
        }
    }

    public static void removeFromKothCap(Player p) {
        if (Koth.getActiveKoth() != null) {
            Koth koth = Koth.getActiveKoth();

            if (koth.getCapper() != null)
                if (koth.getCapper().toString().equals(p.getUniqueId().toString()))
                    koth.setCapper(null);

            for (String uuid : koth.getCappers()) {
                if (uuid.equals(p.getUniqueId().toString()))
                    koth.getCappers().remove(uuid);
            }
        }
    }
}
