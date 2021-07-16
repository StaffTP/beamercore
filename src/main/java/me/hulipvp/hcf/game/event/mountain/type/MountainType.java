package me.hulipvp.hcf.game.event.mountain.type;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;

public enum MountainType {
    
    ORE(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.EMERALD_ORE, Material.DIAMOND_ORE),
    GLOWSTONE(Material.GLOWSTONE);

    @Getter private final Material[] materials;

    MountainType(Material... materials) {
        this.materials = materials;
    }

    public boolean hasMaterial(Material material) {
        return Arrays.asList(materials).contains(material);
    }
}
