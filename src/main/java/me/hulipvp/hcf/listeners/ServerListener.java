package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class ServerListener implements Listener {

    /*@EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Arrays.stream(e.getChunk().getEntities()).forEach(entity -> {
            if(entity.getType() == EntityType.VILLAGER) {
                entity.remove();
            } else {
                switch(entity.getType()) {
                    case WOLF:
                    case HORSE:
                    case ENDERMAN:
                    case PIG_ZOMBlIE:
                    case GHAST:
                    case PLAYER:
                        return;
                    default:
                        LocUtils.removeAi(entity);
                        break;
                }
            }
        });
    }*/

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER)
            return;
        if(event.getLocation().getWorld().getEnvironment() != World.Environment.NETHER)
            return;

        EntityType type = event.getEntityType();
        switch(type) {
            case BLAZE:
            case PIG_ZOMBIE:
            case ENDERMAN:
            case GHAST:
            case WITHER_SKULL:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBlockChange(EntityChangeBlockEvent e) {
        if(e.getEntity().getType() != EntityType.ENDERMAN)
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBlockChange(PortalCreateEvent e) {
        Block block = e.getBlocks().stream().filter(b -> b.getType().equals(Material.OBSIDIAN)).findFirst().orElse(null);
        if(block != null) {
            if (block.getLocation().getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                if (Faction.getByLocation(block.getLocation()) == null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getExpToDrop() == 0)
            return; // Doesn't really matter since when it's 0, it will always multiply to 0 anyways; but who cares.

        int finalXp = event.getExpToDrop() * (TimeUtils.random.nextInt(2) + (int) TimeUtils.random.nextDouble());
        event.setExpToDrop(finalXp);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getDroppedExp() == 0)
            return;

        int finalXp = event.getDroppedExp() * (TimeUtils.random.nextInt(2) + (int) TimeUtils.random.nextDouble());
        event.setDroppedExp(finalXp);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if(!(e.getTarget() instanceof Player))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.setCancelled(true);
        e.blockList().clear();
    }

    @EventHandler
    public void onTnt(ExplosionPrimeEvent e) {
        e.setCancelled(true);
        e.setRadius(0);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (ConfigValues.FEATURES_WEATHER) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if(message.equals("/whatversionofhcf")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("HCF v" + HCF.getInstance().getDescription().getVersion() + " by NickMC & HuliPvP.");
        }
    }
}
