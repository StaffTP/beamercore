package me.hulipvp.hcf.game.player.data.setting;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;

public enum SettingType {

    DEATH_LIGHTNING("Death Lightning", "deathLightning", true, 0, Material.BLAZE_POWDER),
    DEATH_MESSAGES("Death Messages", "deathMessages", true, 2, Material.SKULL_ITEM),
    FOUND_DIAMONDS("Found Diamond Alerts", "foundDiamonds", true, 4, Material.DIAMOND),
    PUBLIC_CHAT("Public Chat", "publicChat", true, 6, Material.SIGN),
    SCOREBOARD("Scoreboard", "showScoreboard", true, 8, Material.ENCHANTED_BOOK);

    @Getter private final String key, verboseName;
    @Getter private final Boolean defaultValue;
    @Getter private final int slot;
    @Getter private final Material material;

    SettingType(String verboseName, String key, Boolean defaultValue, int slot, Material material) {
        this.verboseName = verboseName;
        this.key = key;
        this.defaultValue = defaultValue;
        this.slot = slot;
        this.material = material;
    }

    public static SettingType getByKey(String key) {
        return Arrays.stream(values())
                .filter((type) -> type.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public static SettingType getByMaterial(Material material) {
        return Arrays.stream(values())
                .filter((type) -> type.getMaterial() == material)
                .findFirst()
                .orElse(null);
    }
}
