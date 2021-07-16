package me.hulipvp.hcf.listeners.player;

import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ItemStatisticListener implements Listener {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @EventHandler
    public void onItemTrack(PlayerDeathEvent e) {
        if(e.getEntity().getKiller() == null)
            return;
        if(!ConfigValues.FEATURES_ITEM_KILLS_AND_DEATHS) return;

        Player player = e.getEntity();
        Player killer = player.getKiller();
        String date = LocalDateTime.now().format(formatter);
        if(player.getInventory().getArmorContents() != null) {
            for(ItemStack item : player.getInventory().getArmorContents()) {
                if(item != null && item.getType() != Material.AIR) {
                    List<String> lore = new ArrayList<>();
                    ItemMeta itemMeta = item.getItemMeta();
                    if(!itemMeta.hasLore() || !itemMeta.getLore().contains(Locale.ITEM_STATISTIC_HEADER_DEATHS.toString())) {
                        lore.add(" ");
                        lore.add(Locale.ITEM_STATISTIC_HEADER_DEATHS.toString());
                    } else {
                        lore.addAll(itemMeta.getLore());
                    }

                    lore.add(Locale.ITEM_STATISTIC_LORE_DEATH.toString().replace("%killer%", killer.getDisplayName()).replace("%dead%", player.getDisplayName()).replace("%time%", date));
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
        }

        ItemStack weapon = killer.getItemInHand();
        if(weapon != null && weapon.getType() != Material.AIR) {
            List<String> lore = new ArrayList<>();
            ItemMeta itemMeta = weapon.getItemMeta();
            if(!itemMeta.hasLore() || !itemMeta.getLore().contains(Locale.ITEM_STATISTIC_HEADER_KILLS.toString())) {
                lore.add(" ");
                lore.add(Locale.ITEM_STATISTIC_HEADER_KILLS.toString());
            } else {
                lore.addAll(itemMeta.getLore());
            }

            lore.add(Locale.ITEM_STATISTIC_LORE_KILL.toString().replace("%killer%", killer.getDisplayName()).replace("%dead%", player.getDisplayName()).replace("%time%", date));
            itemMeta.setLore(lore);
            weapon.setItemMeta(itemMeta);
        }
    }
}
