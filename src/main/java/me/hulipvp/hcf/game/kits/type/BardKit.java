package me.hulipvp.hcf.game.kits.type;

import com.sun.javafx.iio.jpeg.JPEGImageLoader;
import lombok.Getter;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.kits.EffectRestoreTask;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.game.kits.type.item.BardItem;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BardKit extends Kit {
    
    @Getter private static final Map<String, Integer> playerEnergy = new HashMap<>();
    @Getter private static final Map<String, BardItem> heldItems = new HashMap<>();
    @Getter private static final Map<String, BardItem> clickItems = new HashMap<>();

    public BardKit() {
        super("Bard", ArmorType.GOLD, Arrays.asList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false), new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false)));
        HCF.getInstance().getConfig().getConfigurationSection("kits.bard.items").getKeys(false).forEach(BardItem::new);
    }

    @EventHandler
    public void onHeldItem(PlayerItemHeldEvent event) {
        if(!Kit.isWearingKit(event.getPlayer(), this.getType())) return;
        if (HCFProfile.getByPlayer(event.getPlayer()).hasTimer(PlayerTimerType.PVPTIMER)) return;

        Player player = event.getPlayer();

        Faction otherFaction = Faction.getByLocation(player.getLocation());
        if(otherFaction != null && !otherFaction.isDeathban() && !ConfigValues.KITS_HOLD_EFFECTS_IN_SPAWN) {
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
            if (newItem != null) {
                if (newItem.getType() != null && newItem.getType() != Material.AIR) {
                    applyHeldBard(player, getItemByType(newItem.getType(), true));
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!Kit.isWearingKit(event.getPlayer(), this.getType()))
            return;
        if(!event.getAction().name().contains("RIGHT"))
            return;
        if(!event.hasItem())
            return;
        if(getItemByType(event.getItem().getType(), false) == null)
            return;

        Player player = event.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);
        Faction otherFaction = Faction.getByLocation(player.getLocation());
        if(otherFaction != null && !otherFaction.isDeathban() && !ConfigValues.KITS_CLICK_EFFECTS_IN_SPAWN) {
            player.sendMessage(Locale.BARD_SAFEZONE.toString());
            return;
        }
        if (profile.hasTimer(PlayerTimerType.PVPTIMER)) return;

        PlayerTimer timer = profile.getTimerByType(PlayerTimerType.BARD_EFFECT);
        if(timer == null) {
            if(applyInstantBard(player, event.getItem())) {
                timer = new PlayerTimer(player, PlayerTimerType.BARD_EFFECT);
                timer.add();

                if (ConfigValues.KITS_BARD_EFFECT_CLICK_COMBAT) {
                    if (profile.hasTimer(PlayerTimerType.COMBAT)) {
                        profile.updateTimer(PlayerTimerType.COMBAT, PlayerTimerType.COMBAT.getTime(), true);
                    } else {
                        PlayerTimer timer2 = new PlayerTimer(player, PlayerTimerType.COMBAT);
                        timer2.add();
                    }
                }

                int newAmount = event.getItem().getAmount() - 1;
                if(newAmount == 0)
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR, 1));
                else
                    event.getItem().setAmount(newAmount);
                event.getPlayer().updateInventory();
            }
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Locale.TIMER_CANNOT_USE.toString().replace("%time%", timer.getFormattedTime().replace("s", "")));
        }
    }

    public static BardItem getItemByType(Material toCompare, boolean hold) {
        if (hold) {
            for (BardItem item : heldItems.values()) {
                if (new ItemStack(item.getItemId()).getType().equals(toCompare)) {
                    return item;
                }
            }
        } else {
            for (BardItem item : clickItems.values()) {
                if (new ItemStack(item.getItemId()).getType().equals(toCompare)) {
                    return item;
                }
            }
        }

        return null;
    }

    private boolean applyInstantBard(Player player, ItemStack item) {
        if(item != null) {
            Material type = item.getType();

            BardItem bardItem = getItemByType(type, false);

            if (bardItem == null) return false;

            int energyToConsume = bardItem.getEnergy();
            int energy = playerEnergy.get(player.getUniqueId().toString());
            if(energy < energyToConsume) {
                player.sendMessage(Locale.BARD_NOT_ENOUGH.toString());
                return false;
            }

            applyEffects(player, bardItem, true);
            playerEnergy.put(player.getUniqueId().toString(), energy - energyToConsume);
            player.sendMessage(Locale.BARD_USED_EFFECT.toString().replace("%effect%", bardItem.getDisplayName())
                    .replace("%energy%", String.valueOf(energyToConsume)));
            return true;
        }

        return false;
    }

    public void applyHeldBard(Player player, BardItem item) {
        if(item != null) applyEffects(player, item, false);
    }

    public void applyHeldBard(Player player, ItemStack item) {
        if(item != null) {
            if (getItemByType(item.getType(), true) != null) applyEffects(player, getItemByType(item.getType(), true), false);
        }
    }

    public static void applyEffect(Player player, PotionEffect effect) {
        Faction otherFaction = Faction.getByLocation(player.getLocation());
        if(otherFaction != null && !otherFaction.isDeathban() && !ConfigValues.KITS_CLICK_EFFECTS_IN_SPAWN) {
            return;
        }

        if(canOverrideLevel(player, effect) && player.hasPotionEffect(effect.getType())) {
            PotionEffect temp = player.getActivePotionEffects().stream()
                    .filter(potionEffect -> potionEffect.getType().getName().equals(effect.getType().getName()))
                    .findFirst()
                    .orElse(null);
            if(temp == null)
                return;

            if (temp.getDuration() > 100) {
                PotionEffect pre = new PotionEffect(temp.getType(), temp.getDuration(), temp.getAmplifier(), temp.isAmbient());
                new EffectRestoreTask(player, pre).runTaskLater(HCF.getInstance(), effect.getDuration() - 5);
            }
        }

        if(canOverrideLevel(player, effect)) {
            player.addPotionEffect(effect, true);
        }
    }

    private void applyEffects(Player player, BardItem bardItem, boolean isClick) {
        HCFProfile profile = HCFProfile.getByPlayer(player);
        PlayerFaction faction = profile.getFactionObj();
        int range = ConfigValues.KITS_BARD_EFFECT_RANGE;

        if(faction != null) {
            for(Entity entity : player.getNearbyEntities(range, range, range)) {
                if(!(entity instanceof Player))
                    continue;

                Player p = (Player) entity;
                HCFProfile other = HCFProfile.getByPlayer(p);
                if(bardItem.isOtherFaction()) {
                    if(other.hasFaction() && (other.getFaction().equals(faction.getUuid()) || faction.isAllied(other.getFaction())))
                        continue;

                    applyEffect(p, bardItem.getFinalEffect());
                } else {
                    if(!other.hasFaction())
                        continue;
                    if(!other.getFaction().equals(faction.getUuid()))
                        continue;

                    applyEffect(p, bardItem.getFinalEffect());
                    if (isClick) {
                        p.sendMessage(CC.translate("&eYou have received&a " + bardItem.getDisplayName() + " &efrom &a" + player.getName() + "&e!"));
                    }
                }
            }
        }

        if(!bardItem.isApplyOnBard())
            return;

        applyEffect(player, bardItem.getFinalEffect());
        if (isClick) {
            player.sendMessage(CC.translate("&eYou have received&a " + bardItem.getDisplayName() + " &efrom &a" + player.getName() + "&e!"));
        }
    }

    private void removeEffects(Player player, PotionEffect effect) {
        HCFProfile profile = HCFProfile.getByPlayer(player);
        PlayerFaction faction = profile.getFactionObj();
        int range = ConfigValues.KITS_BARD_EFFECT_RANGE;

        if(faction != null) {
            for(Entity entity : player.getNearbyEntities(range, range, range)) {
                if(!(entity instanceof Player))
                    continue;

                Player p = (Player) entity;
                HCFProfile other = HCFProfile.getByPlayer(p);
                if(!other.hasFaction())
                    continue;
                if(!other.getFaction().equals(faction.getUuid()) && !faction.isAllied(other.getFaction()))
                        continue;

                removeEffect(p, effect);
            }
        }

        removeEffect(player, effect);
    }

    private void removeEffect(Player player, PotionEffect effect) {
        if(!player.hasPotionEffect(effect.getType()))
            return;

        PotionEffect toRemove = player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effect.getType().getId()).findFirst().orElse(null);
        if(toRemove == null)
            return;
        if(toRemove.getAmplifier() != effect.getAmplifier())
            return;
        if(toRemove.getDuration() > effect.getDuration())
            return;

        player.removePotionEffect(effect.getType());
    }

    private static boolean canOverrideLevel(Player player, PotionEffect effect) {
        if(player.hasPotionEffect(effect.getType())) {
            PotionEffect before = player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effect.getType().getId()).findFirst().orElse(null);
            if(before == null)
                return true;

            return before.getAmplifier() < effect.getAmplifier() || (before.getAmplifier() == effect.getAmplifier() && before.getDuration() < effect.getDuration() && !effect.getType().equals(PotionEffectType.REGENERATION));
        }

        return true;
    }
}