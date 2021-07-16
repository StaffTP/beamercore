package me.hulipvp.hcf.listeners;

import lombok.Getter;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.InvUtils;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CrowbarListener implements Listener {

    @Getter private static ItemStack crowbar;

    public CrowbarListener() {
        crowbar = new ItemBuilder(Material.GOLD_HOE)
                .name("&c&lCrowbar")
                .lore("&eSpawners: &d1&e/&d1", "&eEnd Frames: &d6&e/&d6")
                .get();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(!event.hasItem())
            return;
        if(!isCrowbar(event.getItem()))
            return;

        event.setCancelled(true);

        Block block = event.getClickedBlock();
        if(block.getType() != Material.MOB_SPAWNER && block.getType() != Material.ENDER_PORTAL_FRAME)
            return;

        Player player = event.getPlayer();
        String handle = FactionListener.handleBlockEvent(player, block);
        if(handle != null) {
            player.sendMessage(Locale.FACTION_CANNOT_USE.toString().replace("%faction%", handle));
            return;
        }

        ItemStack item = event.getItem();
        List<String> lore = item.getItemMeta().getLore();

        int loreNumber = (block.getType() != Material.MOB_SPAWNER) ? 1 : 0;
        String[] splitLore = lore.get(loreNumber).split(":");
        int usesLeft = Integer.parseInt(C.strip(splitLore[1]).subSequence(1, 2).toString()) - 1;
        if(useCheck(player, item, block))
            return;

        switch(block.getType()) {
            case MOB_SPAWNER: {
                CreatureSpawner mobSpawn = (CreatureSpawner) block.getState();
                if(player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    event.getPlayer().sendMessage(Locale.CROWBAR_INVALID_WORLD.toString()
                            .replace("%world%", player.getWorld().getEnvironment() == World.Environment.NETHER ? "Nether" : "The End")
                    );
                    return;
                }

                ItemStack droppedSpawner = new ItemBuilder(new ItemStack(Material.MOB_SPAWNER, 1, mobSpawn.getCreatureType().getTypeId()))
                        .name("&d&l" + WordUtils.capitalizeFully(mobSpawn.getSpawnedType().name()) + " Spawner")
                        .get();
                InvUtils.addItems(player, droppedSpawner);

                splitLore[1] = ChatColor.LIGHT_PURPLE + "" + usesLeft + ChatColor.GRAY + "/" + ChatColor.LIGHT_PURPLE + "1";
                break;
            }
            case ENDER_PORTAL_FRAME: {
                ItemStack itemStack = new ItemStack(block.getType());
                InvUtils.addItems(player, itemStack);

                splitLore[1] = C.color("&d" + usesLeft + ChatColor.GRAY + "/&d" + "6");
                break;
            }
        }

        block.setType(Material.AIR);
        block.getState().update(true, true);

        lore.set(loreNumber, splitLore[0] + ": " + splitLore[1]);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        if(useCheck(player, item, block))
            return;

        player.updateInventory();
    }

    private boolean useCheck(Player player, ItemStack item, Block block) {
        List<String> lore = item.getItemMeta().getLore();

        int loreNumber = (block.getType() != Material.MOB_SPAWNER) ? 1 : 0;
        String[] splitLore = lore.get(loreNumber).split(":");
        int usesLeft = Integer.parseInt(C.strip(splitLore[1]).subSequence(1, 2).toString()) - 1;
        if(usesLeft < 0) {
            int checkOther = (block.getType() == Material.MOB_SPAWNER) ? 1 : 0;
            String[] checkLore = lore.get(checkOther).split(":");
            int otherUsesLeft = Integer.parseInt(C.strip(checkLore[1]).subSequence(1, 2).toString()) - 1;
            if(otherUsesLeft < 0) {
                player.setItemInHand(null);
                player.updateInventory();
            }

            return true;
        }

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType() != Material.MOB_SPAWNER)
            return;

        if(event.getBlock().getLocation().getWorld().getEnvironment() != World.Environment.NORMAL) {
            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.getPlayer().sendMessage(Locale.CROWBAR_INVALID_WORLD.toString()
                        .replace("%world%", event.getBlock().getLocation().getWorld().getEnvironment() == World.Environment.NETHER ? "Nether" : "The End")

                );
                event.setCancelled(true);
                return;
            }
        }

        Player player = event.getPlayer();
        String handle = FactionListener.handleBlockEvent(player, event.getBlock());
        if(handle != null) {
            player.sendMessage(Locale.FACTION_CANNOT_USE.toString().replace("%faction%", handle));
            return;
        }

        ItemStack stack = event.getPlayer().getItemInHand();
        if(stack == null)
            return;
        if(!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName() || !stack.getItemMeta().hasLore())
            return;

        String display = C.strip(stack.getItemMeta().getDisplayName());
        EntityType type;
        try {
            type = EntityType.valueOf(display.split(" ")[0].toUpperCase());
        } catch(Exception ex) {
            event.setCancelled(true);
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
        spawner.setSpawnedType(type);
        spawner.update(true, true);
    }

    private boolean isCrowbar(ItemStack item) {
        if(item == null || item.getType() != crowbar.getType())
            return false;
        if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore())
            return false;

        return C.strip(item.getItemMeta().getDisplayName()).equals(C.strip(crowbar.getItemMeta().getDisplayName()));
    }
}
