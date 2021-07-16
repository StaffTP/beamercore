package me.hulipvp.hcf.game.player.data.mod.item;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ModItem {

    @Getter private final String key, name, permission;
    @Getter private final int slot, data, itemId;
    @Getter private final String fallbackItem;
    @Getter private final ModItemAction action;
    @Getter private final List<String> lore;

    ModItem(String key) {
        ConfigurationSection section = HCF.getInstance().getModModeFile().getConfig().getConfigurationSection("mod-mode.items." + key);

        this.key = key;
        this.name = section.getString("name");
        this.permission = section.getString("permission");
        this.slot = section.getInt("slot");
        this.data = section.getInt("data");
        this.itemId = section.getInt("itemId");
        this.fallbackItem = section.getString("fallbackItem");
        this.action = ModItemAction.valueOf(section.getString("action"));
        this.lore = section.getStringList("lore");

        ModItems.getItems().put(this.getKey(), this);
    }

    public boolean isApplicable(Player player) {
        return this.getPermission().length() <= 0 || player.hasPermission(this.getPermission());
    }

    public ItemStack toItem() {
        return new ItemBuilder(
                new ItemStack(
                        Material.getMaterial(this.getItemId()),
                        1,
                        (short) this.getData()
                )
        ).name(
                this.getName()
                        .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                        .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
        ).lore(
                this.getLore()
        ).get();
    }
}
