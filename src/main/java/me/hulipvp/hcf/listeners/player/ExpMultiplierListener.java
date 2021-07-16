package me.hulipvp.hcf.listeners.player;

import me.hulipvp.hcf.utils.ConfigValues;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class ExpMultiplierListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        double amount = event.getDroppedExp();
        Player killer = event.getEntity().getKiller();
        if (killer != null && amount > 0) {
            ItemStack stack = killer.getItemInHand();
            if (stack != null && stack.getType() != Material.AIR) {
                int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                if (enchantmentLevel > 0L) {
                    double multiplier = enchantmentLevel * ConfigValues.LISTENERS_EXPERIENCE_LOOTING_MULTIPLIER;
                    int result = (int) Math.ceil(amount * multiplier);
                    event.setDroppedExp(result);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        double amount = event.getExpToDrop();
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand();
        if (stack != null && stack.getType() != Material.AIR && amount > 0) {
            int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            if (enchantmentLevel > 0) {
                double multiplier = enchantmentLevel * ConfigValues.LISTENERS_EXPERIENCE_FORTUNE_MULTIPLIER;
                int result = (int) Math.ceil(amount * multiplier);
                event.setExpToDrop(result);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupExp(PlayerExpChangeEvent event) {
        double amount = event.getAmount();
        if (amount > 0) {
            int result = (int) Math.ceil(amount * ConfigValues.LISTENERS_EXPERIENCE_GLOBAL_MULTIPLIER);
            event.setAmount(result);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFish(PlayerFishEvent event) {
        double amount = event.getExpToDrop();
        if (amount > 0) {
            amount = Math.ceil(amount * ConfigValues.LISTENERS_EXPERIENCE_FISHING_GLOBAL_MULTIPLIER);
            ProjectileSource projectileSource = event.getHook().getShooter();
            if (projectileSource instanceof Player) {
                ItemStack stack = ((Player) projectileSource).getItemInHand();
                int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LUCK);
                if (enchantmentLevel > 0L) {
                    amount = Math.ceil(amount * (enchantmentLevel * ConfigValues.LISTENERS_EXPERIENCE_FISHING_LUCK_MULTIPLIER));
                }
            }

            event.setExpToDrop((int) amount);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        double amount = event.getExpToDrop();
        if (amount > 0) {
            double multiplier = ConfigValues.LISTENERS_EXPERIENCE_FURNACE_MULTIPLIER;
            int result = (int) Math.ceil(amount * multiplier);
            event.setExpToDrop(result);
        }
    }
}
