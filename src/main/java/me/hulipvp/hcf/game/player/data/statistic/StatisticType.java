package me.hulipvp.hcf.game.player.data.statistic;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;

public enum StatisticType {
    
    DIAMONDS_MINED("diamondsMined", Material.DIAMOND_ORE),
    EMERALDS_MINED("emeraldsMined", Material.EMERALD_ORE),
    GOLD_MINED("goldMined", Material.GOLD_ORE),
    LAPIS_MINED("lapisMined", Material.LAPIS_ORE),
    REDSTONE_MINED("redstoneMined", Material.REDSTONE_ORE),
    IRON_MINED("ironMined", Material.IRON_ORE),
    COAL_MINED("coalMined", Material.COAL_ORE);

    @Getter private final String key;
    @Getter private final Material material;

    StatisticType(String key, Material material) {
        this.key = key;
        this.material = material;
    }

    public static StatisticType getByKey(String key) {
        return Arrays.stream(values())
                .filter((type) -> type.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public static StatisticType getByMaterial(Material mat) {
        return Arrays.stream(values())
                .filter((type) -> type.getMaterial() == mat)
                .findFirst()
                .orElse(null);
    }
}
