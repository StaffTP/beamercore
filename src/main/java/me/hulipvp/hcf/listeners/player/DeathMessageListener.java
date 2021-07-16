package me.hulipvp.hcf.listeners.player;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        EntityDamageEvent.DamageCause damageCause = (player.getLastDamageCause() == null) ? EntityDamageEvent.DamageCause.CUSTOM : player.getLastDamageCause().getCause();

        String message = null;
        if(damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();
                if(damageEvent.getDamager() instanceof Player) {
                    Player damager = (Player) damageEvent.getDamager();

                    if(damager.getItemInHand() != null && damager.getItemInHand().getType() != Material.AIR) {
                        if (damager.getItemInHand().getItemMeta() != null
                            && damager.getItemInHand().getItemMeta().hasDisplayName()) {
                            message = Locale.DEATHMESSAGE_ENTITY_PLAYER.toString().replace("%item%", WordUtils.capitalizeFully
                                    (damager.getItemInHand().getItemMeta().getDisplayName()));
                        } else {
                            message = Locale.DEATHMESSAGE_ENTITY_PLAYER.toString().replace("%item%", WordUtils.capitalizeFully
                                    (damager.getItemInHand().getType().name().replace("_", " ").toLowerCase()));
                        }
                    } else {
                        message = Locale.DEATHMESSAGE_ENTITY_PLAYER_NOITEM.toString();
                    }

                    message = message.replace("%killer%", damager.getName()).replace("%killer_kills%", String.valueOf(HCFProfile.getByPlayer(damager).getKills().size()));
                } else {
                    message = Locale.DEATHMESSAGE_ENTITY_ENTITY.toString().replace("%killer%", WordUtils.capitalizeFully
                            (damageEvent.getDamager().getType().name().replace("_", " ").toLowerCase()));
                }
            } else {
                message = Locale.DEATHMESSAGE_ENTITY_ENTITY.toString().replace("%killer%", "Unknown");
            }
        } else if(damageCause == EntityDamageEvent.DamageCause.PROJECTILE) {
            if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();

                if(damageEvent.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) damageEvent.getDamager();

                    if(projectile.getShooter() instanceof Player) {
                        Player damager = (Player) projectile.getShooter();
                        double distance = damager.getLocation().distance(player.getLocation());

                        if(damager.getItemInHand() != null && damager.getItemInHand().getType() != Material.AIR) {
                            message = Locale.DEATHMESSAGE_PROJECTILE_PLAYER.toString().replace("%item%", WordUtils.capitalizeFully(damager.getItemInHand().getType().name().replace("_", " ").toLowerCase())).replace("%distance%", String.valueOf((int) distance));
                        } else {
                            message = Locale.DEATHMESSAGE_PROJECTILE_PLAYER_NOITEM.toString();
                        }

                        message = message.replace("%killer%", damager.getName()).replace("%killer_kills%", String.valueOf(HCFProfile.getByPlayer(damager).getKills().size()));
                    } else if(projectile.getShooter() instanceof Entity) {
                        message = Locale.DEATHMESSAGE_PROJECTILE_ENTITY.toString().replace("%killer%", WordUtils.capitalizeFully(((Entity) projectile.getShooter()).getType().name().replace("_", " ").toLowerCase()));
                    }
                } else {
                    message = Locale.DEATHMESSAGE_PROJECTILE_ENTITY.toString().replace("%killer%", "Unknown");
                }
            } else {
                message = Locale.DEATHMESSAGE_ENTITY_ENTITY.toString().replace("%killer%", "Unknown");
            }
        } else if(damageCause != null) {
            message = Locale.valueOf("DEATHMESSAGE_" + damageCause.name()).toString();
        }

        if(message != null) {
            message = message.replace("%dead%", player.getName()).replace("%dead_kills%", String.valueOf(HCFProfile.getByPlayer(player).getKills().size()));
            e.setDeathMessage(message);
        }
    }
}
