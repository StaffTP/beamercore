package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.event.MountainFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.setting.SettingType;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class BlockListener implements Listener {

    private static final Set<String> foundLocations = new HashSet<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onAutoSmelt(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("hcf.autosmelt") && ConfigValues.FEATURES_AUTOSMELT) {
            Block block = event.getBlock();
            if (!block.getType().name().contains("ORE"))
                return;

            ItemStack stack = player.getItemInHand();
            if (stack == null || !stack.getType().name().contains("PICKAXE"))
                return;
            if (stack.containsEnchantment(Enchantment.SILK_TOUCH))
                return;

            Material toDrop;
            try {
                toDrop = Material.valueOf(block.getType().name().split("_")[0] + "_INGOT");
            } catch (Exception ex) {
                return;
            }

            event.setCancelled(true);

            int dropAmount = 1, fortuneLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            if (fortuneLevel != 0)
                dropAmount *= TimeUtils.random.nextInt(25) > 50 ? fortuneLevel : 1;

            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(toDrop, dropAmount));
            block.setType(Material.AIR);
            block.getState().update(true, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for(Block block : event.getBlocks()) {
            if(block.getType() != Material.DIAMOND_ORE)
                continue;

            foundLocations.add(block.getLocation().toString());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if(block.getType() != Material.DIAMOND_ORE)
            return;

        foundLocations.add(block.getLocation().toString());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE)
            return;
        if(player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
            return;

        Block block = event.getBlock();
        if(Faction.getByLocation(block.getLocation()) instanceof MountainFaction)
            return;
        if(block.getType() != Material.DIAMOND_ORE)
            return;

        Location location = block.getLocation();
        if(foundLocations.add(location.toString())) {
            int amount = 1;
            for(int x = -5; x < 5; x++) {
                for(int y = -5; y < 5; y++) {
                    for(int z = -5; z < 5; z++) {
                        Block otherBlock = location.clone().add(x, y, z).getBlock();
                        if(otherBlock != block && otherBlock.getType() == Material.DIAMOND_ORE && foundLocations.add(otherBlock.getLocation().toString()))
                            amount++;
                    }
                }
            }

            for(Player other : Bukkit.getOnlinePlayers()) {
                HCFProfile profile = HCFProfile.getByPlayer(other);
                if(!profile.getSetting(SettingType.FOUND_DIAMONDS).isValue())
                    continue;

                String message = Locale.FOUND_DIAMONDS.toString().replace("%name%", player.getName()).replace("%amount%", amount + "");
                other.sendMessage(message);
            }
        }
    }
}
