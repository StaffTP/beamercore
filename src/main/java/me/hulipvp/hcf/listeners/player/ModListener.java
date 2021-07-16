package me.hulipvp.hcf.listeners.player;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.mod.Vanish;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ModListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            HCFProfile profile = HCFProfile.getByPlayer(player);
            if(profile.getVanish() == null || !profile.getVanish().isVanished())
                continue;

            if(!e.getPlayer().hasPermission("hcf.staff")) {
                e.getPlayer().hidePlayer(player);
                continue;
            }

            Scoreboard scoreboard = e.getPlayer().getScoreboard();
            if(scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                e.getPlayer().setScoreboard(scoreboard);
            }

            Team team = scoreboard.getTeam(Vanish.teamName);
            if(team == null) {
                team = scoreboard.registerNewTeam(Vanish.teamName);
                team.setPrefix(ChatColor.GRAY.toString());
                team.setCanSeeFriendlyInvisibles(true);
            }

            team.addEntry(player.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if(!e.getPlayer().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getPlayer().getUniqueId());
        if(profile.getVanish() == null)
            return;

        if (profile.getVanish().canBypass()) return;

        e.setCancelled(true);
        e.getPlayer().sendMessage(Locale.MODMODE_CANNOT_DO.toString());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if(!e.getPlayer().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getPlayer().getUniqueId());
        if(profile.getVanish() == null)
            return;

        if (profile.getVanish().canBypass()) return;

        e.setCancelled(true);
        e.getPlayer().sendMessage(Locale.MODMODE_CANNOT_DO.toString());
    }

    @EventHandler
    public void onInteractChest(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(!e.getPlayer().hasPermission("hcf.staff"))
            return;

        Player p = e.getPlayer();
        HCFProfile profile = HCFProfile.getByUuid(p.getUniqueId());
        if(profile.getVanish() == null)
            return;

        Block block = e.getClickedBlock();
        if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            if(block.getState() instanceof ContainerBlock) {
                ContainerBlock container = (ContainerBlock) block.getState();
                Inventory copy = Bukkit.createInventory(null, container.getInventory().getSize(), container.getInventory().getTitle());

                for(int i = 0; i < container.getInventory().getSize(); ++i)
                    copy.setItem(i, container.getInventory().getItem(i));

                p.openInventory(copy);
                p.sendMessage(C.color("&cOpening " + block.getType().name().replace("_", "").toLowerCase() + " silently."));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractPhysical(PlayerInteractEvent e) {
        if(e.getAction() != Action.PHYSICAL && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player p = e.getPlayer();
        HCFProfile profile = HCFProfile.getByUuid(p.getUniqueId());
        if(profile.getVanish() == null)
            return;

        if (profile.getVanish().canBypass()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if(!e.getWhoClicked().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getWhoClicked().getUniqueId());
        if(profile.getVanish() == null)
            return;

        if (profile.getVanish().canBypass()) return;

        e.setCursor(null);
        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
        ((Player) e.getWhoClicked()).updateInventory();
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        InventoryAction action = event.getAction();

        if (event.getInventory().getName().equalsIgnoreCase("Inventory preview")) {
            event.setCancelled(true);
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasDisplayName()) {
                    if (item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Close Preview")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.closeInventory();
                            }
                        }.runTaskLater(HCF.getInstance(), 1L);
                    }
                }
            }
        }

        HCFProfile profile = HCFProfile.getByPlayer(player);
        if (profile.getVanish() != null) {

            if (profile.getVanish().canBypass()) return;

            if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
                event.setCancelled(true);
                return;
            }
            if (item == null || item.getType().equals(Material.AIR)) {
                if (action.equals(InventoryAction.HOTBAR_SWAP) || action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
                    event.setCancelled(true);
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player))
            return;

        Player damager = (Player) e.getDamager();
        if(!damager.hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(damager.getUniqueId());

        if(profile.getVanish() == null)
            return;

        if (!profile.getVanish().canBypass()) {
            e.setCancelled(true);
            damager.sendMessage(Locale.MODMODE_CANNOT_DO.toString());
            return;
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(!e.getEntity().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getEntity().getUniqueId());
        if(profile.getVanish() != null)
            e.getDrops().clear();
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if(!e.getPlayer().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getPlayer().getUniqueId());


        e.setCancelled(profile.getVanish() != null && !profile.getVanish().canBypass());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(!e.getPlayer().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getPlayer().getUniqueId());

        if(profile.getVanish() != null) {
            if (profile.getVanish().canBypass()) return;

            e.getItemDrop().remove();
            if(e.getPlayer().getItemInHand().getAmount() == 0) {
                e.getPlayer().setItemInHand(e.getItemDrop().getItemStack());
            } else {
                e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() + 1);
            }

            e.getPlayer().updateInventory();
            e.getPlayer().sendMessage(Locale.MODMODE_CANNOT_DO.toString());
        }
    }
}
