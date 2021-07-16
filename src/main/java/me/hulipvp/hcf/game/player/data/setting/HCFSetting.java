package me.hulipvp.hcf.game.player.data.setting;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HCFSetting {
    
    @Getter private final SettingType type;
    @Getter @Setter private boolean value;

    public HCFSetting(SettingType type, boolean value) {
        this.type = type;
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }

    public ItemStack getSettingItem() {
        List<String> lore = new ArrayList<>();

        ConfigValues.SETTINGS_GUI_LORE.forEach(str -> lore.add(str
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                .replace("%name%", this.getType().getVerboseName())
                .replace("%show_prefix%", (value) ? ConfigValues.SETTINGS_GUI_PREFIX_ACTIVE : ConfigValues.SETTINGS_GUI_PREFIX_NOT_ACTIVE)
                .replace("%hide_prefix%", (!value) ? ConfigValues.SETTINGS_GUI_PREFIX_ACTIVE : ConfigValues.SETTINGS_GUI_PREFIX_NOT_ACTIVE)
        ));

        return new ItemBuilder(
                this.getType().getMaterial()
        ).name(
                C.color(ConfigValues.SETTINGS_GUI_ITEM_TITLE
                            .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                            .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                            .replace("%name%", this.getType().getVerboseName())
                )
        ).lore(
                lore.stream().map(C::color).collect(Collectors.toList())
        ).get();
    }
}
