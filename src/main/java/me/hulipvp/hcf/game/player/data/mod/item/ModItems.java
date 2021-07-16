package me.hulipvp.hcf.game.player.data.mod.item;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ModItems {

    @Getter private static final Map<String, ModItem> items = new HashMap<>();

    public static Map<Integer, ItemStack> getItemsFor(Player player) {
        HCFProfile profile = HCFProfile.getByPlayer(player);
        Map<Integer, ItemStack> returnItems = new HashMap<>();

        for(ModItem item : items.values()) {
            if(item.isApplicable(player)) {
                if(item.getAction() == ModItemAction.VANISH_ON || item.getAction() == ModItemAction.VANISH_OFF || item.getAction() == ModItemAction.VANISH_TOGGLE) {
                    if (profile.getVanish() != null) {
                        if (item.getAction() == ModItemAction.VANISH_ON && profile.getVanish().isVanished())
                            continue;
                        if (item.getAction() == ModItemAction.VANISH_OFF && !profile.getVanish().isVanished())
                            continue;
                    }
                }

                returnItems.put(item.getSlot() - 1, item.toItem());
            }
        }

        return returnItems;
    }

    public static ModItemAction getActionByItem(ItemStack toCompare) {
        for(ModItem item : items.values()) {
            if(item.toItem().isSimilar(toCompare)) {
                return item.getAction();
            }
        }

        return null;
    }

    public static void instate() {
        if (HCF.getInstance().getModModeFile().getConfig().contains("mod-mode") && HCF.getInstance().getModModeFile().getConfig().contains("mod-mode.items")) {
            HCF.getInstance().getModModeFile().getConfig().getConfigurationSection("mod-mode.items").getKeys(false)
                    .forEach(ModItem::new);
        }
    }
}