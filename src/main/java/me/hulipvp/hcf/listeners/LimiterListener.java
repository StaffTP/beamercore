package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Map;

public class LimiterListener implements Listener {

    @EventHandler
    public void onEnchantItem(EnchantItemEvent e) {
        Map<Enchantment, Integer> enchantsToAdd = e.getEnchantsToAdd();
        for(Enchantment enchantment : ConfigValues.LIMITERS_ENCHANTS.keySet()) {
            if(!enchantsToAdd.containsKey(enchantment))
                continue;

            int level = ConfigValues.LIMITERS_ENCHANTS.get(enchantment);
            if(enchantsToAdd.get(enchantment) > level) {
                enchantsToAdd.remove(enchantment);

                if(level > 0)
                    enchantsToAdd.put(enchantment, level);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().getType() != InventoryType.ANVIL)
            return;
        if(e.getSlotType() != InventoryType.SlotType.RESULT)
            return;

        ItemStack item = e.getCurrentItem();
        for(Enchantment enchantment : ConfigValues.LIMITERS_ENCHANTS.keySet()) {
            if(item.getType() == Material.ENCHANTED_BOOK) {

                EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item.getItemMeta();
                if (bookMeta.getStoredEnchants().containsKey(enchantment)) {
                    int level = ConfigValues.LIMITERS_ENCHANTS.get(enchantment);
                    if (bookMeta.getStoredEnchants().get(enchantment) > level) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }

            if(item.getEnchantments().containsKey(enchantment)) {
                int level = ConfigValues.LIMITERS_ENCHANTS.get(enchantment);
                if(item.getEnchantments().get(enchantment) > level) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public final void onVillagerTrade(InventoryClickEvent e)
    {
        Inventory inventory = e.getInventory();
        InventoryType.SlotType slotType = e.getSlotType();
        if ((inventory.getType().equals(InventoryType.MERCHANT)) && (slotType == InventoryType.SlotType.RESULT)) {
            ItemStack item = e.getCurrentItem();
            for(Enchantment enchantment : ConfigValues.LIMITERS_ENCHANTS.keySet()) {
                if(item.getType() != Material.ENCHANTED_BOOK) continue;

                EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item.getItemMeta();
                if(bookMeta.getStoredEnchants().containsKey(enchantment)) {
                    int level = ConfigValues.LIMITERS_ENCHANTS.get(enchantment);
                    if(bookMeta.getStoredEnchants().get(enchantment) > level) {
                        e.setCancelled(true);
                        return;
                    }
                }

                if(item.getEnchantments().containsKey(enchantment)) {
                    int level = ConfigValues.LIMITERS_ENCHANTS.get(enchantment);
                    if(item.getEnchantments().get(enchantment) > level) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent e) {
        int potionId = e.getPotion().getItem().getDurability();
        if(!ConfigValues.LIMITERS_BLOCKED_POTIONS.contains(potionId))
            return;

        e.setCancelled(true);

        for(LivingEntity entity : e.getAffectedEntities())
            e.setIntensity(entity, 0);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() != Material.POTION)
            return;
        int potionId = e.getItem().getDurability();
        if(!ConfigValues.LIMITERS_BLOCKED_POTIONS.contains(potionId))
            return;

        e.setCancelled(true);
        e.getPlayer().setItemInHand(null);
        e.getPlayer().updateInventory();
        e.getPlayer().sendMessage(Locale.BLOCKED_POTION.toString());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        if(!e.hasItem())
            return;
        if(e.getItem().getType() != Material.ENCHANTED_BOOK)
            return;
        if(e.getClickedBlock().getType() != Material.ENCHANTMENT_TABLE)
            return;

        e.setCancelled(true);
        e.getPlayer().setItemInHand(new ItemStack(Material.BOOK));
        e.getPlayer().sendMessage(Locale.BOOK_DISENCHANTED.toString());
    }

    @EventHandler
    public void onBrew(BrewEvent e) {
        BrewingStand brewingStand = (BrewingStand) e.getBlock().getState();
        for(ItemStack item : brewingStand.getInventory().getContents()) {
            if(item == null || item.getType() != Material.POTION)
                continue;

            try {
                Potion potion = Potion.fromItemStack(item);
                PotionType type = potion.getType();
                if(!ConfigValues.LIMITERS_POTIONS.containsKey(type))
                    return;

                String[] limitSplit = ConfigValues.LIMITERS_POTIONS.get(type).split(";");
                int level = Integer.valueOf(limitSplit[0]);
                int duration = Integer.valueOf(limitSplit[1]);

                boolean invalid = false;

                for(PotionEffect effect : potion.getEffects()) {
                    if(type.getEffectType() == effect.getType()) { // Should be true -- if it isn't, then that's some voodoo magic
                        if(effect.getAmplifier() > level && effect.getDuration() > duration)
                            invalid = true;
                    }
                    break; // A potion will only have one effect
                }

                if(invalid) {
                    potion.setLevel(level);

                    potion.getEffects().clear();
                    potion.getEffects().add(new PotionEffect(PotionEffectType.getByName(type.name()), duration * 20, level));
                }
            } catch(IllegalArgumentException ex) {
                // We surround everything in a try-catch as a water bottle cannot be turned into a potion from an itemstack
            }
        }
    }
}
