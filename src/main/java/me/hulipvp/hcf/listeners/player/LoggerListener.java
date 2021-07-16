package me.hulipvp.hcf.listeners.player;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.Death;
import me.hulipvp.hcf.game.player.data.Kill;
import me.hulipvp.hcf.game.player.data.PlayerInv;
import me.hulipvp.hcf.game.player.data.PlayerLogger;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LoggerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
            PlayerLogger villager = PlayerLogger.getLoggers().remove(event.getPlayer().getUniqueId().toString());
            if(villager == null)
                return;
            if(villager.getVillager().isDead() || villager.isDead()) {
                TaskUtils.runSync(() -> {
                    Player player = event.getPlayer();
                    Faction spawn = Faction.getByName("Spawn");
                    if(spawn != null && spawn.getHome() != null)
                        player.teleport(spawn.getHome());

                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setExp(0);

                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.updateInventory();

                    player.getActivePotionEffects().forEach(potionEffect -> {
                        player.removePotionEffect(potionEffect.getType());
                    });
                });
                return;
            }

            villager.getVillager().remove();
            PlayerLogger.getLoggers().remove(event.getPlayer().getUniqueId().toString());
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.SURVIVAL);

        if (event.getPlayer().hasMetadata("LOGOUT")) {
            event.getPlayer().removeMetadata("LOGOUT", HCF.getInstance());
            return;
        }

        if(profile.getVanish() != null) {
            profile.getVanish().disable();
            profile.setVanish(null);
            return;
        }


        if (ServerTimer.isSotw() && !profile.isSotwPvp())
            return;

        if (profile.hasTimer(PlayerTimerType.PVPTIMER)) {
            profile.getTimerByType(PlayerTimerType.PVPTIMER).setPaused(true);
            profile.save();
            return;
        }



        if (profile.getVanish() != null) {
            return;
        }

        if (profile.hasTimer(PlayerTimerType.LOGOUT)) {
            return;
        }
        Faction faction = Faction.getByLocation(event.getPlayer().getLocation());
        if (faction != null) {
            if (faction instanceof SafezoneFaction) {
                return;
            }
        }

        List<Player> players = getNearbyPlayers(event.getPlayer(), 10);

        boolean enemyNearby = false;
        for(Player other : players) {
            if(!profile.hasFaction()) {
                enemyNearby = true;
                break;
            }

            HCFProfile otherProfile = HCFProfile.getByPlayer(other);
            if(!otherProfile.hasFaction()) {
                enemyNearby = true;
                break;
            }

            PlayerFaction pf = profile.getFactionObj();
            PlayerFaction otherPf = otherProfile.getFactionObj();
            if(pf.getUuid().equals(otherPf.getUuid()) || pf.isAllied(otherPf.getUuid()))
                continue;

            enemyNearby = true;
            break;
        }

        if(enemyNearby || profile.hasTimer(PlayerTimerType.COMBAT)) {
            Player player = event.getPlayer();
            PlayerLogger villager = new PlayerLogger(player);

            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                if((!villager.getVillager().isDead() || villager.getVillager().isValid())) {
                    villager.getVillager().remove();
                    PlayerLogger.getLoggers().remove(player.getUniqueId().toString());
                }
            }, 20L * 30L);
        }
    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onKick(PlayerKickEvent event) {
//        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
//        if(!profile.hasTimer(PlayerTimerType.COMBAT))
//            return;
//
//        Player player = event.getPlayer();
//        PlayerLogger villager = new PlayerLogger(player);
//
//        player.getInventory().clear();
//        player.getInventory().setArmorContents(null);
//        player.updateInventory();
//        player.getActivePotionEffects().forEach(potionEffect -> {
//            player.removePotionEffect(potionEffect.getType());
//        });
//        player.setExp(0);
//
//        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
//            if((!villager.getVillager().isDead() || villager.getVillager().isValid())) {
//                villager.getVillager().remove();
//                PlayerLogger.getLoggers().remove(player.getUniqueId().toString());
//            }
//        }, 20L * 30L);
//    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Villager))
            return;
        if(!event.getEntity().hasMetadata("COMBAT"))
            return;

        Villager villager = (Villager) event.getEntity();
        PlayerLogger logger = PlayerLogger.get(villager);
        if(logger == null)
            return;

        HCFProfile profile = HCFProfile.getByUuid(logger.getUuid());
        villager.remove();
        PlayerLogger.getLoggers().get(logger.getUuid().toString()).setDead(true);

        event.getDrops().clear();
        event.getDrops().addAll(Arrays.asList(logger.getContents()));
        event.getDrops().addAll(Arrays.asList(logger.getArmor()));
        event.setDroppedExp((int) logger.getExp());
        profile.setDeathBanned(true);

        profile.setBannedTill(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(profile.getDeathbanTime(event.getEntity().getMetadata("RANK").get(0).asString())));
        if (profile.getFactionObj() != null) {
            PlayerFaction pf = profile.getFactionObj();
            DecimalFormat df = new DecimalFormat("#.##");

            String newDtr = df.format(pf.getDtr() - ConfigValues.FACTIONS_DTR_DEATH);

            pf.setDtr(Double.valueOf(newDtr));
            pf.setStartRegen(new Timestamp(System.currentTimeMillis() + (TimeUnit.MINUTES.toMillis(ConfigValues.FACTIONS_DTR_REGEN_START_DELAY))));
            pf.sendMessage(Locale.FACTION_MEMBER_DEATH.toString()
                    .replace("%name%", logger.getName())
                    .replace("%dtr%", newDtr)
            );
            pf.setupRegenTask();

            pf.save();
        }

        Player player = villager.getKiller();
        if(player == null) {
            Bukkit.broadcastMessage(Locale.DEATHMESSAGE_LOGGER_UNKNOWN.toString()
                    .replace("%dead%", profile.getName())
                    .replace("%dead_kills%", String.valueOf(profile.getKills().size()))
            );
        } else {
            HCFProfile killer = HCFProfile.getByUuid(player.getUniqueId());
            killer.addKill(new Kill(killer.getUuid(), logger.getUuid(), "Combat Logger"));
            killer.save();

            Bukkit.broadcastMessage(Locale.DEATHMESSAGE_LOGGER_PLAYER.toString()
                    .replace("%dead%", profile.getName())
                    .replace("%dead_kills%", String.valueOf(profile.getKills().size()))
                    .replace("%killer%", killer.getName())
                    .replace("%killer_kills%", String.valueOf(killer.getKills().size()))
            );
        }

        profile.addDeath(new Death(logger.getUuid(), player == null ? null : player.getUniqueId(), villager.getLocation(), new PlayerInv(logger.getContents(), logger.getArmor())));
        profile.save();
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if(!(e.getRightClicked() instanceof Villager))
            return;

        Villager villager = (Villager) e.getRightClicked();
        if(PlayerLogger.get(villager) != null)
            e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Villager))
            return;
        if(!(event.getDamager() instanceof Player))
            return;

        Villager villager = (Villager) event.getEntity();
        PlayerLogger logger = PlayerLogger.get(villager);
        if(logger == null)
            return;

        HCFProfile profile = HCFProfile.getByUuid(event.getDamager().getUniqueId());

        if (profile.hasTimer(PlayerTimerType.PVPTIMER)) {
            event.setCancelled(true);
            return;
        } else if (profile.hasTimer(PlayerTimerType.STARTING)) {
            event.setCancelled(true);
            return;
        } else if (ServerTimer.isSotw()) {
            event.setCancelled(true);
            return;
        }
        if (villager.hasMetadata("FACTION")) {
            Player attacker = (Player) event.getDamager();
            PlayerFaction playerFaction = (PlayerFaction) PlayerFaction.getPlayerFaction(UUID.fromString(villager.getMetadata("FACTION").get(0).asString()));
            PlayerFaction attackerFaction = (PlayerFaction) Faction.getByPlayer(attacker.getName());
            if (playerFaction != null) {
                if (attackerFaction != null) {
                    if (playerFaction.equals(attackerFaction)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (ConfigValues.FACTIONS_ALLY_ATTACKING_PREVENT) {
                        playerFaction.getAllies().forEach(uuid -> {
                            if (PlayerFaction.getPlayerFaction(uuid).equals(attackerFaction)) event.setCancelled(true);
                        });
                    }
                }
            }
        }
        Faction faction = Faction.getByLocation(event.getDamager().getLocation());
        if (faction instanceof SafezoneFaction) {
            event.setCancelled(true);
            return;
        }

        if (!ConfigValues.COMBAT_VILLAGER_KNOCKBACK) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    villager.setVelocity(new Vector(0, 0, 0));
                }
            }.runTaskLater(HCF.getInstance(), 1L);
        }
    }

    private List<Player> getNearbyPlayers(Player player, int radius) {
        return player.getNearbyEntities(radius, radius, radius)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());
    }
}
