package me.hulipvp.hcf.game.kits;

import lombok.Getter;
import me.hulipvp.hcf.api.events.kit.KitDisableEvent;
import me.hulipvp.hcf.api.events.kit.KitEnableEvent;
import me.hulipvp.hcf.game.kits.type.*;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Kit implements Listener {

    @Getter private static final Map<String, Kit> kits = new HashMap<>();

    @Getter private final String name;
    @Getter private final ArmorType type;
    @Getter private final List<PotionEffect> effects;

    public Kit(String name, ArmorType type, List<PotionEffect> effects) {
        this.name = name;
        this.type = type;
        this.effects = effects;
    }

    public static Kit getByType(ArmorType type) {
        return kits.values().stream()
                .filter(kit -> kit.getType() == type)
                .findFirst()
                .orElse(null);
    }

    public static boolean isWearingKit(Player p, ArmorType type) {
        if(p.getInventory().getHelmet() == null || p.getInventory().getChestplate() == null || p.getInventory().getLeggings() == null || p.getInventory().getBoots() == null)
            return false;

        switch(type) {
            case DIAMOND:
                return p.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET && p.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE && p.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS && p.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS;
            case GOLD:
                return p.getInventory().getHelmet().getType() == Material.GOLD_HELMET && p.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE && p.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS && p.getInventory().getBoots().getType() == Material.GOLD_BOOTS;
            case IRON:
                return p.getInventory().getHelmet().getType() == Material.IRON_HELMET && p.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE && p.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS && p.getInventory().getBoots().getType() == Material.IRON_BOOTS;
            case CHAIN:
                return p.getInventory().getHelmet().getType() == Material.CHAINMAIL_HELMET && p.getInventory().getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE && p.getInventory().getLeggings().getType() == Material.CHAINMAIL_LEGGINGS && p.getInventory().getBoots().getType() == Material.CHAINMAIL_BOOTS;
            case LEATHER:
                return p.getInventory().getHelmet().getType() == Material.LEATHER_HELMET && p.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE && p.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS && p.getInventory().getBoots().getType() == Material.LEATHER_BOOTS;
        }
        return false;
    }

    public static void instate() {
        if(ConfigValues.KITS_ARCHER_ENABLE) {
            Kit archer = new ArcherKit();
            kits.put(archer.getName(), archer);
        }

        if(ConfigValues.KITS_BARD_ENABLE) {
            Kit bard = new BardKit();
            kits.put(bard.getName(), bard);
        }

        if(ConfigValues.KITS_MINER_ENABLE) {
            Kit miner = new MinerKit();
            kits.put(miner.getName(), miner);
        }

        if(ConfigValues.KITS_ROGUE_ENABLE) {
            Kit rogue = new RogueKit();
            kits.put(rogue.getName(), rogue);
        }

        for(Kit kit : kits.values())
            Bukkit.getServer().getPluginManager().registerEvents(kit, HCF.getInstance());

        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                HCFProfile profile = HCFProfile.getByPlayer(player);

                if(profile.getCurrentKit() != null) {
                    if(!isWearingKit(player, profile.getCurrentKit().getType())) {
                        for(PotionEffect effect : profile.getCurrentKit().getEffects())
                            player.removePotionEffect(effect.getType());

                        KitDisableEvent kitDisableEvent = new KitDisableEvent(player, profile.getCurrentKit());
                        Bukkit.getPluginManager().callEvent(kitDisableEvent);

                        profile.setCurrentKit(null);
                        break;
                    } else {
                        TaskUtils.runSync(() -> {
                            profile.getCurrentKit().getEffects().forEach(player::addPotionEffect);

                            if(profile.getCurrentKit().getType() == ArmorType.GOLD) {
                                if (player.getItemInHand() != null && !(player.getItemInHand().getType().equals(Material.AIR)))
                                    ((BardKit) profile.getCurrentKit()).applyHeldBard(player, player.getItemInHand());

                                if(BardKit.getPlayerEnergy().get(profile.getUuid().toString()) < ConfigValues.KITS_BARD_MAX_ENERGY)
                                    BardKit.getPlayerEnergy().put(profile.getUuid().toString(), BardKit.getPlayerEnergy().get(profile.getUuid().toString()) + 1);
                            }
                        });
                    }
                } else {
                    for(ArmorType type : ArmorType.values()) {
                        if(isWearingKit(player, type)) {
                            Kit kit = getByType(type);
                            if(kit != null) {
                                TaskUtils.runSync(() -> {
                                    kit.getEffects().forEach(player::addPotionEffect);

                                    KitEnableEvent kitEnableEvent = new KitEnableEvent(player, kit);
                                    Bukkit.getPluginManager().callEvent(kitEnableEvent);

                                    profile.setCurrentKit(kit);
                                });
                            }
                            break;
                        }
                    }
                }
            }
        }, 20L, 20L);

        ShapedRecipe easyMelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
        easyMelon.shape("   ", "AB ", "   ")
                .setIngredient('A', Material.MELON)
                .setIngredient('B', Material.GOLD_NUGGET);

        Bukkit.addRecipe(easyMelon);
    }
}
