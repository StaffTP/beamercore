package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.kits.MapKit;
import me.hulipvp.hcf.game.player.data.PlayerInv;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.backend.files.KitsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitmapListener implements Listener {

    private ItemStack HEALTH_SPLASH = new ItemStack(Material.POTION, 1, (short) 16421);
    private KitsFile kits;

    public KitmapListener() {
        kits = HCF.getInstance().getKitsFile();
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        if(!ConfigValues.FEATURES_KITMAP)
            return;

        Player player = e.getPlayer();
        if(!player.isOp())
            return;

        e.setLine(0, ChatColor.translateAlternateColorCodes('&', e.getLine(0)));
        e.setLine(1, ChatColor.translateAlternateColorCodes('&', e.getLine(1)));
        e.setLine(2, ChatColor.translateAlternateColorCodes('&', e.getLine(2)));
        e.setLine(3, ChatColor.translateAlternateColorCodes('&', e.getLine(3)));

        if(e.getLine(1).contains("Kit") || e.getLine(1).contains("Class")) {
            if(e.getLine(2) == null || e.getLine(2).isEmpty())
                return;

            String kit = C.strip(e.getLine(2));
            if(kit != null && kits.isValidKit(kit) && kits.isEnabled(kit)) {
                MapKit mapKit = kits.getKit(kit);
                player.sendMessage(Locale.KITMAP_CREATED_SIGN.toString().replace("%mapkit%", mapKit.getDisplay()));
                return;
            }

            player.sendMessage(Locale.KITMAP_INVALID_KIT.toString().replace("%invalid%", e.getLine(2)));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if(!block.getType().name().contains("SIGN"))
            return;

        Sign sign = (Sign) block.getState();
        if(!sign.getLine(1).contains("Kit") && !sign.getLine(1).contains("Class"))
            return;

        String kit = C.strip(sign.getLine(2));
        if(kit == null || kit.isEmpty())
            return;

        MapKit mapKit = kits.getKit(kit);
        if(mapKit == null || !mapKit.isEnabled())
            return;
        if(mapKit.getPlayerInv() == null)
            return;

        if(!(Faction.getByLocation(player.getLocation()) instanceof SafezoneFaction)) {
            player.sendMessage(ChatColor.RED + "You must be in Spawn to claim a map kit.");
            return;
        }

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            PlayerInventory inventory = player.getInventory();
            PlayerInv playerInv = mapKit.getPlayerInv();
            inventory.setContents(playerInv.getItems());
            inventory.setArmorContents(playerInv.getArmor());

            player.updateInventory();
        }, 1L);
    }
}
