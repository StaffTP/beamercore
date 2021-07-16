package me.hulipvp.hcf.game.faction;

import lombok.Getter;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClaimPillar {
    
    @Getter private static List<Material> materials = Arrays.asList(Material.WOOD, Material.GLASS, Material.STONE,
            Material.STAINED_CLAY, Material.STAINED_GLASS, Material.ICE, Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK,
            Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.SAND, Material.SANDSTONE, Material.SOUL_SAND,
            Material.COAL_BLOCK, Material.LAPIS_BLOCK, Material.MELON_BLOCK, Material.QUARTZ_BLOCK, Material.SNOW_BLOCK,
            Material.HAY_BLOCK, Material.NOTE_BLOCK, Material.WORKBENCH, Material.BEDROCK, Material.COBBLESTONE,
            Material.GRAVEL, Material.DISPENSER, Material.FURNACE, Material.GRASS, Material.LEAVES, Material.LEAVES_2,
            Material.OBSIDIAN, Material.NETHER_BRICK, Material.NETHERRACK, Material.TNT, Material.SMOOTH_BRICK, Material.PUMPKIN);

    @Getter private final Location location;
    @Getter private final Material material;

    public ClaimPillar(Location location, Material material) {
        this.location = location;
        this.material = material;
    }

    public void display(Player claimer) {
        TaskUtils.runSync(() -> {
            List<Integer> values = new ArrayList<>();
            for(int i = location.getWorld().getHighestBlockYAt(location); i <= 256; i++) {
                Location location = new Location(getLocation().getWorld(), getLocation().getBlockX(), i, getLocation().getBlockZ());

                if(location.getBlock().getType() == Material.AIR) {
                    if(values.contains(location.getBlockY())) {
                        claimer.sendBlockChange(location, material, (byte) 0);
                        claimer.sendBlockChange(location.add(0.0, 2.0, 0.0), Material.GLASS, (byte) 0);
                    } else {
                        claimer.sendBlockChange(location, Material.GLASS, (byte) 0);
                        values.add(location.getBlockY() + 2);
                    }
                }
            }
        });
    }

    public void remove(Player claimer) {
        TaskUtils.runSync(() -> {
            Location loc = new Location(location.getWorld(), location.getBlockX(), 0, location.getBlockZ());
            for(int i = 1; i <= 256; i++) {
                claimer.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
                loc.add(0, 1, 0);
            }
        });
    }

    public static Material getRandomMaterial() {
        return ClaimPillar.getMaterials().get(TimeUtils.random.nextInt(getMaterials().size()));
    }
}
