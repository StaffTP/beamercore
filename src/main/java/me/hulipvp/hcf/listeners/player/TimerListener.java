package me.hulipvp.hcf.listeners.player;

import com.google.common.collect.Sets;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.api.events.timer.TimerExpireEvent;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Cuboid;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.*;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.commands.admin.RestartCommand;
import me.hulipvp.hcf.game.timer.type.server.type.RampageTimer;
import me.hulipvp.hcf.listeners.GlassListener;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimerListener implements Listener {

    private final Set<Material> blockedPearlTypes;

    public TimerListener() {
        blockedPearlTypes = Sets.immutableEnumSet(Material.THIN_GLASS,
                        Material.IRON_FENCE, Material.FENCE, Material.NETHER_FENCE, Material.FENCE_GATE,
                        Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS,
                        Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS,
                        Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS, Material.SANDSTONE_STAIRS,
                        Material.SMOOTH_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(e.getDamager() == null)
            return;

        if(e.getEntity() instanceof Player) {

            if(e.getDamager() instanceof Player || e.getDamager() instanceof Projectile) {
                Projectile projectile = null;
                if(e.getDamager() instanceof Projectile) {
                    projectile = (Projectile) e.getDamager();
                    if(!(projectile.getShooter() instanceof Player))
                        return;
                }

                Player p = (Player) e.getEntity();
                Player damager = (projectile == null) ? (Player) e.getDamager() : (Player) projectile.getShooter();
                if(p.getGameMode() == GameMode.CREATIVE && damager.getGameMode() == GameMode.CREATIVE)
                    return;

                HCFProfile pProfile = HCFProfile.getByPlayer(p);
                HCFProfile dProfile = HCFProfile.getByPlayer(damager);
                if(pProfile.hasTimer(PlayerTimerType.PVPTIMER) || pProfile.hasTimer(PlayerTimerType.STARTING)) {
                    e.setCancelled(true);
                    damager.sendMessage(Locale.TIMER_ATTACK_PROTECTED.toString());
                    return;
                }

                if(dProfile.hasTimer(PlayerTimerType.PVPTIMER) || dProfile.hasTimer(PlayerTimerType.STARTING)) {
                    e.setCancelled(true);
                    damager.sendMessage(Locale.TIMER_PROTECTED.toString());
                    return;
                }

                if(pProfile.hasFaction() && dProfile.hasFaction() && pProfile.getFactionObj().equals(dProfile.getFactionObj())) {
                    if(projectile != null && pProfile.getUuid().equals(dProfile.getUuid()))
                        return;
                    e.setCancelled(true);
                    return;
                }


                if (ServerTimer.isSotw()) {
                    if (!e.getDamager().getType().equals(EntityType.PLAYER)) {
                        e.setCancelled(true);
                        return;
                    }
                    if (ServerTimer.getTimer(ServerTimerType.SOTW).isPvp() || (pProfile.isSotwPvp() && dProfile.isSotwPvp())) {
                        e.setCancelled(false);
                        return;
                    } else {
                        e.setCancelled(true);
                        return;
                    }
                }


                if (!e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    if(damager.getInventory().getItemInHand().isSimilar(LocUtils.getClaimingWand())) {
                        damager.getInventory().remove(LocUtils.getClaimingWand());
                    }
                    if (pProfile.hasTimer(PlayerTimerType.COMBAT)) {
                        pProfile.updateTimer(PlayerTimerType.COMBAT, PlayerTimerType.COMBAT.getTime(), true);
                    } else {
                        PlayerTimer timer = new PlayerTimer(p, PlayerTimerType.COMBAT);
                        timer.add();
                        p.sendMessage(Locale.TIMER_SPAWNTAG.toString());
                    }
                    if (dProfile.hasTimer(PlayerTimerType.COMBAT)) {
                        dProfile.updateTimer(PlayerTimerType.COMBAT, PlayerTimerType.COMBAT.getTime(), true);
                    } else {
                        PlayerTimer timer = new PlayerTimer(damager, PlayerTimerType.COMBAT);
                        timer.add();
                        damager.sendMessage(Locale.TIMER_SPAWNTAG.toString());
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile projectile = e.getEntity();
        if (projectile instanceof EnderPearl) {
            EnderPearl enderPearl = (EnderPearl) projectile;
            ProjectileSource source = enderPearl.getShooter();
            if (source instanceof Player) {
                Player player = ((Player) source).getPlayer();
                HCFProfile profile = HCFProfile.getByPlayer(player);
                if(profile.hasTimer(PlayerTimerType.ENDERPEARL)) {
                    PlayerTimer timer = profile.getTimerByType(PlayerTimerType.ENDERPEARL);

                    player.sendMessage(Locale.TIMER_CANNOT_USE.toString().replace("%time%", timer.getFormattedTime().replace("s", "")));
                    e.setCancelled(true);
                    player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                    player.updateInventory();
                } else {
                    PlayerTimer timer = new PlayerTimer(player, PlayerTimerType.ENDERPEARL);
                    timer.add();
                }

            }
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());
        if(e.getItem().getType() == Material.GOLDEN_APPLE) {
            if(profile.hasTimer(PlayerTimerType.APPLE) && e.getItem().getData().getData() == 0) {
                PlayerTimer timer = profile.getTimerByType(PlayerTimerType.APPLE);

                e.getPlayer().sendMessage(Locale.TIMER_CANNOT_USE.toString().replace("%time%", timer.getFormattedTime().replace("s", "")));
                e.getPlayer().updateInventory();
                e.setCancelled(true);
                return;
            }

            if(profile.hasTimer(PlayerTimerType.GAPPLE) && e.getItem().getData().getData() == 1) {
                PlayerTimer timer = profile.getTimerByType(PlayerTimerType.GAPPLE);

                e.getPlayer().sendMessage(Locale.TIMER_CANNOT_USE.toString()
                        .replace("%time%", timer.getFormattedTime()
                                .replace("s", "")));
                e.getPlayer().updateInventory();
                e.setCancelled(true);
                return;
            }

            e.setCancelled(false);
            e.getPlayer().updateInventory();

            PlayerTimer timer = null;
            if(e.getItem().getDurability() == 0)
                timer = new PlayerTimer(e.getPlayer(), PlayerTimerType.APPLE);
            else if(e.getItem().getDurability() == 1)
                timer = new PlayerTimer(e.getPlayer(), PlayerTimerType.GAPPLE);

            if(timer != null)
                timer.add();
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent e) {
        if(e.getTimer() instanceof PlayerTimer) {
            PlayerTimer timer = (PlayerTimer) e.getTimer();
            Player p = timer.getPlayer();
            HCFProfile profile = HCFProfile.getByPlayer(p);

            switch(timer.getType()) {
                case HOME:
                    TaskUtils.runSync(() -> {
                        PlayerFaction pf = profile.getFactionObj();
                        p.teleport(pf.getHome());
                        p.sendMessage(Locale.FACTION_WARPING.toString().replace("%faction%", pf.getName()));
                    });
                    break;
                case SPAWN:
                    TaskUtils.runSync(() -> {
                        Faction faction = Faction.getByName("Spawn");
                        if(faction != null && faction.getHome() != null)
                            p.teleport(faction.getHome());
                    });
                    break;
                case LOGOUT:
                    TaskUtils.runSync(() -> {
                        p.setMetadata("LOGOUT", new FixedMetadataValue(HCF.getInstance(), ""));
                        p.kickPlayer(Locale.TIMER_LOGOUT_SUCCESS.toString());
                    });
                    break;
                case STUCK:
                    TaskUtils.runAsync(() -> {
                        p.sendMessage(Locale.TIMER_STUCK_STARTING.toString());

                        if (Faction.getByLocation(profile.getStuckLocation()) instanceof SafezoneFaction) {
                            Faction faction = Faction.getByName("Spawn");
                            if (faction != null && faction.getHome() != null) {
                                Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                                    p.teleport(faction.getHome());
                                    p.sendMessage(Locale.TIMER_STUCK_SUCCESS.toString());
                                }, 1L);
                                return;
                            }
                        }

                        Claim claim = Faction.getClaimAt(profile.getStuckLocation());
                        if(claim == null && (claim = profile.getFactionObj().getClaims().stream().findFirst().orElseGet(null)) == null) {
                            return;
                        }

                        Cuboid cuboid = claim.toCuboid();
                        Location location = cuboid.getWorld().getHighestBlockAt(new Location(cuboid.getWorld(), cuboid.getLowerX() - 1, 0, cuboid.getLowerZ() - 1)).getLocation();

                        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                            p.teleport(location.add(0, 1, 0));
                            p.sendMessage(Locale.TIMER_STUCK_SUCCESS.toString());
                        }, 1L);
                    });
                    break;
                case COMBAT:
                case PVPTIMER:
                case STARTING:
                    TaskUtils.runSync(() -> {
                        GlassListener.removeGlass(p);
                    });
                    break;
                case BACKSTAB:
                    p.sendMessage(ChatColor.GREEN + "You can now backstab again");
            }
        } else {
            ServerTimer timer = (ServerTimer) e.getTimer();
            switch(timer.getType()) {
                case EOTW:
                    Bukkit.getScheduler().runTask(HCF.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eotw commence"));
                    LunarClientAPI api = LunarClientAPI.getInstance();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (api.isRunningLunarClient(player)) {
                            api.sendTitle(player, TitleType.TITLE, CC.translate("&cEOTW has commenced!"), Duration.ofSeconds(3));
                        }
                    }
                    break;
                case RESTART:
                    RestartCommand.restartServer();
                    break;
                case KEY_SALE:
                    Bukkit.broadcastMessage(Locale.COMMAND_KEY_SALE_EXPIRED.toString());
                    break;
                case KEY_ALL:
                    TaskUtils.runSync(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), timer.getCommand()));
                    Bukkit.broadcastMessage(Locale.COMMAND_KEY_ALL_DONE.toString());
                    break;
                case SOTW:
                    Bukkit.broadcastMessage(Locale.COMMAND_SOTW_ENDED.toString());
                    LunarClientAPI apid = LunarClientAPI.getInstance();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (apid.isRunningLunarClient(player)) {
                            apid.sendTitle(player, TitleType.TITLE, CC.translate("&cSOTW period has ended!"), Duration.ofSeconds(3));
                        }
                    }
                    break;
                case RAMPAGE:
                    RampageTimer rampageTimer = (RampageTimer) timer;
                    String message = Locale.COMMAND_RAMPAGE_ENDED.toString();
                    List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(rampageTimer.getSortedMap().entrySet());
                    for (int i = 1; i < 6; i++) {
                        if (i > sortedList.size()) {
                            message = message.replace("%rampage-top-name-" + i + "%", "Unknown")
                                    .replace("%rampage-top-kills-" + i + "%", "Unknown");
                        } else {
                            message = message.replace("%rampage-top-name-" + i + "%", sortedList.get(i - 1).getKey())
                                    .replace("%rampage-top-kills-" + i + "%", sortedList.get(i - 1).getValue() + "");
                        }
                    }
                    Bukkit.broadcastMessage(message);
                    if (rampageTimer.getTopKiller() != null) {
                        Player winner = Bukkit.getPlayer(rampageTimer.getTopKiller());
                        if (winner != null && winner.isOnline()) {
                            for (String command : ConfigValues.RAMPAGE_ON_WIN) {
                                TaskUtils.runSync(() -> {
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", winner.getName()));
                                });
                            }
                        }
                    }
                    break;
            }
        }
    }

    /*public void teleportOut(Player player, Claim claim) {
        Cuboid cuboid = claim.toCuboid();
        Location location = new Location(cuboid.getWorld(), cuboid.getLowerX() - 1, 0, cuboid.getLowerZ() - 1);
        Location location = cuboid.getWorld().getHighestBlockAt(new Location(cuboid.getWorld(), cuboid.getLowerX() - 1, 0, cuboid.getLowerZ() - 1)).getLocation();

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            player.teleport(location.add(0, 1, 0));
        }, 1L);
    }*/

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo(), from = e.getFrom();
        if(to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ())
            return;
        if(Math.abs(from.getX() - to.getX()) == 0.5 && Math.abs(from.getZ() - to.getZ()) == 0.5)
            return; // zSpigot's weird movement
        if((from.getX() - 0.5 == to.getX() && from.getZ() - 0.5 == to.getZ()) || (from.getX() + 0.5 == to.getX() && from.getZ() + 0.5 == to.getZ()))
            return;

        TaskUtils.runAsync(() -> {
            HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());
            for(PlayerTimer timer : profile.getTimers()) {
                switch(timer.getType()) {
                    case HOME:
                    case LOGOUT:
                    case SPAWN:
                        if(!timer.isCancelled()) {
                            if(timer.getTimeRemaining() > 0) {
                                if(timer.getType() == PlayerTimerType.LOGOUT)
                                    e.getPlayer().sendMessage(Locale.TIMER_LOGOUT_CANCELLED.toString());

                                if(timer.getType() == PlayerTimerType.HOME)
                                    e.getPlayer().sendMessage(Locale.FACTION_TELEPORT_CANCELLED.toString());

                                timer.cancel();
                            }
                        }
                        return;
                    case PVPTIMER:
                        Faction fac = Faction.getByLocation(to);
                        if(fac instanceof SafezoneFaction) {
                            if(!timer.isPaused())
                                timer.setPaused(true);
                        } else {
                            if(timer.isPaused())
                                timer.setPaused(false);
                        }
                        return;
                    case STARTING:
                        Faction fac2 = Faction.getByLocation(to);
                        if(fac2 instanceof SafezoneFaction) {
                            if(!timer.isPaused())
                                timer.setPaused(true);
                        } else {
                            if(timer.isPaused())
                                timer.setPaused(false);
                        }
                        return;
                    case STUCK:
                        if(to.distance(profile.getStuckLocation()) > 5) {
                            timer.cancel();

                            profile.setStuckLocation(null);
                            e.getPlayer().sendMessage(Locale.TIMER_STUCK_CANCELLED.toString());
                        }
                        return;
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        onMove(event);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(e.getPlayer().hasPlayedBefore())
            return;
        if(ConfigValues.FEATURES_KITMAP)
            return;

        HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());
        if(!ConfigValues.LISTENERS_STARTING_TIMER) {
            if(!ServerTimer.isSotw()) {
                PlayerTimer timer = new PlayerTimer(e.getPlayer(), PlayerTimerType.PVPTIMER);
                timer.setPaused(true);
                timer.add();
            }
        } else {
            TaskUtils.runAsync(() -> {
                if(!profile.hasTimer(PlayerTimerType.STARTING)) {
                    PlayerTimer timer = new PlayerTimer(e.getPlayer(), PlayerTimerType.STARTING);
                    timer.setPaused(true);
                    timer.add();
                }
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;

        Player p = (Player) e.getEntity();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        if((ServerTimer.isSotw() && !e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) || (ServerTimer.isSotw() && !profile.isSotwPvp() && !ServerTimer.getTimer(ServerTimerType.SOTW).isPvp()) || profile.hasTimer(PlayerTimerType.STARTING)) {
            e.setDamage(0);
            e.setCancelled(true);
            return;
        }

        if(profile.hasTimer(PlayerTimerType.PVPTIMER)) {
            if(e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION
                    || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        }

        PlayerTimer homeTimer = profile.getTimerByType(PlayerTimerType.HOME);
        if(homeTimer != null && !homeTimer.isCancelled()) {
//            e.setCancelled(true);
            homeTimer.setCancelled(true);
            profile.removeTimersByType(PlayerTimerType.HOME);

            p.sendMessage(Locale.FACTION_TELEPORT_CANCELLED.toString());
        }

        PlayerTimer stuckTimer = profile.getTimerByType(PlayerTimerType.STUCK);
        if(stuckTimer != null && !stuckTimer.isCancelled()) {
//            e.setCancelled(true);
            stuckTimer.setCancelled(true);
            profile.removeTimersByType(PlayerTimerType.STUCK);

            p.sendMessage(Locale.FACTION_TELEPORT_CANCELLED.toString());
        }

        PlayerTimer logoutTimer = profile.getTimerByType(PlayerTimerType.LOGOUT);
        if(logoutTimer != null && !logoutTimer.isCancelled()) {
//            e.setCancelled(true);
            logoutTimer.setCancelled(true);
            profile.removeTimersByType(PlayerTimerType.LOGOUT);

            p.sendMessage(Locale.TIMER_LOGOUT_CANCELLED.toString());
        }

        PlayerTimer spawnTimer = profile.getTimerByType(PlayerTimerType.SPAWN);
        if(spawnTimer != null && !spawnTimer.isCancelled()) {
//            e.setCancelled(true);
            spawnTimer.setCancelled(true);
            profile.removeTimersByType(PlayerTimerType.SPAWN);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        if(!profile.hasTimer(PlayerTimerType.COMBAT))
            return;

        if(!ConfigValues.LISTENERS_PLACE_IN_COMBAT) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Locale.TIMER_CANNOT_PLACE.toString());
        }
    }
}
