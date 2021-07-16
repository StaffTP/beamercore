package me.hulipvp.hcf.game.kits.type;

import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class ArcherKit extends Kit {

    public ArcherKit() {
        super("Archer", ArmorType.LEATHER, Arrays.asList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false)));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;


        Player target = (Player) e.getEntity();
        HCFProfile profile = HCFProfile.getByPlayer(target);
        if(profile.hasTimer(PlayerTimerType.STARTING) || profile.hasTimer(PlayerTimerType.PVPTIMER)) {
            e.setCancelled(true);

            if(e.getDamager() instanceof Player)
                ((Player) e.getDamager()).sendMessage(Locale.TIMER_ATTACK_PROTECTED.toString());
            return;
        }

        if(Faction.getByLocation(target.getLocation()) instanceof SafezoneFaction) {
            e.setDamage(0.0);
            e.setCancelled(true);
            return;
        }

        if(e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile proj = (Projectile) e.getDamager();
            if(!(proj instanceof Arrow))
                return;

            Arrow arrow = (Arrow) proj;
            Player damager = (Player) arrow.getShooter();
            if(Faction.getByLocation(damager.getLocation()) instanceof SafezoneFaction){
                e.setCancelled(true);
                return;
            }

            if(profile.getFactionObj() != null) {
                if(profile.getFactionObj().getMembers().containsKey(damager.getUniqueId())) {
                    e.setCancelled(true);
                    return;
                }
            }

            HCFProfile damagerProfile = HCFProfile.getByPlayer(damager);
            if(damagerProfile.hasTimer(PlayerTimerType.STARTING) || damagerProfile.hasTimer(PlayerTimerType.PVPTIMER)) {
                e.setCancelled(true);
                damager.sendMessage(Locale.TIMER_PROTECTED.toString());
                return;
            }

            if(!Kit.isWearingKit(damager, this.getType())) {
                if(profile.hasTimer(PlayerTimerType.ARCHERMARK)) {
                    double dmg = e.getDamage() + (2.5 * TimeUtils.random.nextInt(100) / 100.0);
                    e.setDamage(dmg);
                }
                return;
            }

            double range = Math.round(arrow.getLocation().distance(damager.getLocation()));
            if(!Kit.isWearingKit(target, this.getType())) {
                double dmg = e.getDamage(EntityDamageEvent.DamageModifier.BASE) + (3.5 * (TimeUtils.random.nextInt(100) / 100.0));
                e.setDamage(dmg);

                damager.sendMessage(Locale.ARCHER_TAG.toString().replace("%range%", String.valueOf(range)).replace("%time%", String.valueOf(PlayerTimerType.ARCHERMARK.getDefaultTime())).replace("%hearts%", String.valueOf(Math.ceil((e.getFinalDamage() / 2.0) * 2) / 2.0)));

                PlayerTimer timer = profile.getTimerByType(PlayerTimerType.ARCHERMARK);
                if(timer == null) {
                    timer = new PlayerTimer(target, PlayerTimerType.ARCHERMARK);
                    timer.add();
                } else {
                    timer.setCurrentTime(timer.getLength(), false);
                }
            } else {
                damager.sendMessage(Locale.ARCHER_CANNOT_TAG.toString().replace("%range%", String.valueOf(range)).replace("%time%", String.valueOf(PlayerTimerType.ARCHERMARK.getDefaultTime())).replace("%hearts%", String.valueOf(Math.ceil((e.getFinalDamage() / 2.0) * 2) / 2.0)));
            }
        } else {
            if(profile.hasTimer(PlayerTimerType.ARCHERMARK)) {
                double dmg = e.getDamage() + (3.0 * (TimeUtils.random.nextInt(100) / 100.0));

                e.setDamage(dmg);
//                target.damage(dmg);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(!e.getAction().name().contains("RIGHT"))
            return;
        if(!Kit.isWearingKit(e.getPlayer(), this.getType()))
            return;
        if(!e.hasItem())
            return;
        if(e.getItem().getType() != Material.SUGAR && e.getItem().getType() != Material.FEATHER)
            return;

        PlayerTimerType timerType = e.getItem().getType() == Material.FEATHER ? PlayerTimerType.JUMP_EFFECT : PlayerTimerType.SPEED_EFFECT;
        PotionEffectType potionType = e.getItem().getType() == Material.FEATHER ? PotionEffectType.JUMP : PotionEffectType.SPEED;

        HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());
        PlayerTimer timer = profile.getTimerByType(timerType);
        if(timer == null) {
            timer = new PlayerTimer(e.getPlayer(), timerType);
            timer.add();

            e.getPlayer().removePotionEffect(potionType);
            e.getPlayer().addPotionEffect(new PotionEffect(potionType, 10 * 20, 3, false));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!Kit.isWearingKit(e.getPlayer(), getType()))
                        return;

                    getEffects().stream()
                            .filter(effect -> effect.getType() == potionType)
                            .forEach(effect -> {
                                e.getPlayer().addPotionEffect(effect, true);
                            });
                }
            }.runTaskLater(HCF.getInstance(), 190L);

            int newAmount = e.getItem().getAmount() - 1;
            if(newAmount == 0)
                e.getPlayer().setItemInHand(new ItemStack(Material.AIR, 1));
            else
                e.getItem().setAmount(newAmount);
            e.getPlayer().updateInventory();
        } else {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Locale.TIMER_CANNOT_USE.toString().replace("%time%", timer.getFormattedTime().replace("s", "")));
        }
    }
}
