package me.hulipvp.hcf.game.kits.type;

import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;

public class RogueKit extends Kit {

    public RogueKit() {
        super("Rogue", ArmorType.CHAIN, Arrays.asList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false)));
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(!Kit.isWearingKit(damager, this.getType()))
                return;
            if(direction(damaged) != direction(damager))
                return;

            ItemStack hand = damager.getItemInHand();
            if(hand != null && hand.getType() == Material.GOLD_SWORD) {
                HCFProfile damagerProfile = HCFProfile.getByPlayer(damager);
                if (!damagerProfile.hasTimer(PlayerTimerType.BACKSTAB)) {
                    Random r = new Random();
                    int randomInt = r.nextInt(100) + 1;
                    if (randomInt > ConfigValues.KITS_ROGUE_BACKSTAB) {
                        if (damaged.getHealth() <= 0.0D) {
                            return;
                        }
                        if (damaged.getHealth() <= 6.0D) {
                            damaged.damage(20.0D);
                        } else {
                            damaged.setHealth(damaged.getHealth() - 6.0D);
                        }

                        damager.sendMessage(Locale.ROGUE_BACKSTAB.toString().replace("%backstabbed%", damaged.getName()));
                        damaged.sendMessage(Locale.ROGUE_BACKSTABBED.toString().replace("%backstabber%", damager.getName()));

                        damager.setItemInHand(null);
                        damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                        damaged.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);

                        PlayerTimer timer = new PlayerTimer(damager, PlayerTimerType.BACKSTAB);
                        timer.add();
                    }
                }
            }
        }
    }

    public Byte direction(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return 0xC; // S > E
        } else if (22.5 <= rotation && rotation < 67.5) {
            return 0xE; // SW > SE
        } else if (67.5 <= rotation && rotation < 112.5) {
            return 0x0; // W > E
        } else if (112.5 <= rotation && rotation < 157.5) {
            return 0x2; // NW > SW
        } else if (157.5 <= rotation && rotation < 202.5) {
            return 0x4; // N > W
        } else if (202.5 <= rotation && rotation < 247.5) {
            return 0x6; // NE > NW
        } else if (247.5 <= rotation && rotation < 292.5) {
            return 0x8; // E > N
        } else if (292.5 <= rotation && rotation < 337.5) {
            return 0xA; // SE > NE
        } else if (337.5 <= rotation && rotation < 360.0) {
            return 0xC; // S > E
        } else {
            return null;
        }
    }
}
