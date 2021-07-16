package me.hulipvp.hcf.game.player.data;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLogger {

    @Getter private static final ConcurrentHashMap<String, PlayerLogger> loggers = new ConcurrentHashMap<>();

    @Getter private final UUID uuid;
    @Getter private final Player player;
    @Getter private final String name;

    @Getter @Setter private ItemStack[] contents, armor;
    @Getter @Setter private double exp;
    @Getter @Setter private boolean dead;
    @Getter @Setter private Villager villager;

    public PlayerLogger(Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
        this.name = player.getName();
        this.contents = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.exp = player.getExp();
        this.villager = this.getVillager(player);

        loggers.put(this.getUuid().toString(), this);
    }

    private Villager getVillager(Player player) {
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);

        villager.setMetadata("COMBAT", new FixedMetadataValue(HCF.getInstance(), player.getUniqueId().toString()));
        if (Faction.getByPlayer(player.getName()) != null) {
            villager.setMetadata("FACTION", new FixedMetadataValue(HCF.getInstance(), Faction.getByPlayer(player.getName()).getUuid()));
        }
        villager.setMetadata("RANK", new FixedMetadataValue(HCF.getInstance(), HCF.getInstance().getPlayerHook().getRankName(player) != null ? HCF.getInstance().getPlayerHook().getRankName(player) : ""));
        villager.setCustomName(ChatColor.RED + player.getName());
        villager.setCustomNameVisible(true);
        villager.setAgeLock(true);
        villager.setAdult();
        villager.setProfession(Villager.Profession.PRIEST);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10));
        villager.setMaxHealth(villager.getMaxHealth() * 2.0);
        villager.setHealth(player.getHealth() * 1.75); // Add more health as compensation for the villagers not being able to wear armor

        return villager;
    }

    public static PlayerLogger get(Villager villager) {
        return getLoggers().values()
                .stream()
                .filter(logger -> logger.getVillager().getEntityId() == villager.getEntityId())
                .findFirst()
                .orElse(null);
    }

    public static PlayerLogger get(UUID uuid) {
        return getLoggers().get(uuid.toString());
    }

    public static void despawnVillagers() {
        for (PlayerLogger logger : PlayerLogger.getLoggers().values()) {
            if (logger.getVillager() != null && !logger.getVillager().isDead()) {
                logger.getVillager().remove();
                PlayerLogger.getLoggers().remove(logger.getUuid().toString());
            }
        }
    }
}
