package me.hulipvp.hcf.listeners.player;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.mod.item.ModItemAction;
import me.hulipvp.hcf.game.player.data.mod.item.ModItems;
import me.hulipvp.hcf.utils.*;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModItemListener implements Listener {

    private List<Material> armor = Arrays.asList(
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS
    );

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(!e.getPlayer().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getPlayer().getUniqueId());
        if(e.getItem() == null)
            return;
        if(profile.getVanish() == null)
            return;
        if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(e.getAction().name().contains("RIGHT")) {
            if(armor.contains(e.getItem().getType())) {
                e.setCancelled(true);
                e.getPlayer().updateInventory();
            }
        }

        ModItemAction action = ModItems.getActionByItem(e.getPlayer().getItemInHand());
        LunarClientAPI lunarClientAPI = LunarClientAPI.getInstance();
        if(action == null)
            return;

        switch(action) {
            case VANISH_ON:
                profile.getVanish().setVanished(true);
                profile.getVanish().refreshItems(); // TODO: Make it only refresh the vanished item.
                lunarClientAPI.sendTitle(e.getPlayer(), TitleType.TITLE, CC.translate("&aVanish Enabled!"), Duration.ofSeconds(1));
                return;
            case VANISH_OFF:
                profile.getVanish().setVanished(false);
                profile.getVanish().refreshItems(); // TODO: Make it only refresh the vanished item.
                lunarClientAPI.sendTitle(e.getPlayer(), TitleType.TITLE, CC.translate("&cVanish Disabled!"), Duration.ofSeconds(1));
                return;
            case VANISH_TOGGLE:
                profile.getVanish().setVanished(!profile.getVanish().isVanished());
                profile.getVanish().refreshItems(); // TODO: Make it only refresh the vanished item.
                return;
            case RANDOM_TP:
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                if (Bukkit.getOnlinePlayers().size() == 1) {
                    e.getPlayer().sendMessage(C.color("&cNo other players online"));
                    return;
                }
                boolean shouldContinue = true;
                while(shouldContinue) {
                    int randomNumber = TimeUtils.random.nextInt(players.size());

                    if (e.getPlayer() == players.get(randomNumber)) {
                        shouldContinue = true;
                    } else {
                        e.getPlayer().teleport(players.get(randomNumber));
                        e.getPlayer().sendMessage(C.color(Locale.MODMODE_TELEPORT_PLAYER.toString().replace("%primary%", ConfigValues.SERVER_PRIMARY).replace("%secondary%", ConfigValues.SERVER_SECONDARY).replace("%name%", players.get(randomNumber).getName())));
                        return;
                    }
                }
            case ONLINE_STAFF:
                Inventory staffInv = Bukkit.createInventory(null, 27, C.color("&dOnline Staff"));
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!player.hasPermission("hcf.staff"))
                        continue;

                    HCFProfile staffProfile = HCFProfile.getByUuid(player.getUniqueId());
                    int statistic = player.getStatistic(Statistic.PLAY_ONE_TICK);
                    int hours = statistic % 1728000 / 20 / 3600;
                    int minutes = statistic % 72000 / 20 / 60;
                    String modMode = ((staffProfile.getVanish() != null) ? "Yes" : "No");
                    String vanished = ((staffProfile.getVanish() != null) ? ((staffProfile.getVanish().isVanished()) ? "Yes" : "No") : "No");
                    String location = player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ();
                    ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                    meta.setOwner(player.getName());
                    meta.setDisplayName(CC.translate(HCF.getInstance().getPlayerHook().getRankPrefix(player) + player.getName()));
                    meta.setLore(CC.translate(Arrays.asList("",
                            "&dMod Mode: &f" + modMode,
                            "&dVanished: &f" + vanished,
                            "&dPlaytime: &f" + hours + " hours, " + minutes + " minutes",
                            "",
                            "&7Left-Click to teleport to them.",
                            "&7Right-Click to view their inventory.")));
                    itemStack.setItemMeta(meta);



                    staffInv.addItem(itemStack);
                }

                e.getPlayer().openInventory(staffInv);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if(!e.getPlayer().hasPermission("hcf.staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getPlayer().getUniqueId());
        if(profile.getVanish() == null)
            return;
        if(!(e.getRightClicked() instanceof Player))
            return;

        Player interacted = (Player) e.getRightClicked();
        if(e.getPlayer().getItemInHand() != null) {
            ModItemAction action = ModItems.getActionByItem(e.getPlayer().getItemInHand());
            if(action == null)
                return;

            switch(action) {
                case INSPECTOR:
                    e.getPlayer().openInventory(InvUtils.createInventory(e.getPlayer(), interacted));
                    return;
                case FREEZE:
                    e.getPlayer().chat("/freeze " + interacted.getName());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getWhoClicked().hasPermission("hcf.staff"))
            return;
        if(e.getCurrentItem() == null)
            return;
        if(e.getCurrentItem().getItemMeta() == null)
            return;
        if(e.getClick() == null)
            return;
        if(!C.strip(e.getInventory().getName()).equals("Online Staff"))
            return;

        HCFProfile profile = HCFProfile.getByUuid(e.getWhoClicked().getUniqueId());
        if(profile.getVanish() == null)
            return;

        e.setCancelled(true);
        if(e.getClick() == ClickType.LEFT) {
            ItemStack skull = e.getCurrentItem();
            SkullMeta skullMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
            Player player = (Player) e.getWhoClicked();
            Player staff = Bukkit.getPlayerExact(skullMeta.getOwner());
            if(staff != null && staff.isOnline())
                e.getWhoClicked().teleport(staff);
                player.sendMessage(CC.translate("&eYou have been teleported to &d" + staff.getName()));
        }

        if (e.getClick() == ClickType.RIGHT) {
            ItemStack skull = e.getCurrentItem();
            SkullMeta skullMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
            Player staff = Bukkit.getPlayerExact(skullMeta.getOwner());
            Player player = (Player) e.getWhoClicked();
            player.openInventory(InvUtils.createInventory(player, staff));
        }
    }
}
