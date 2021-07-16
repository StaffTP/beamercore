package me.hulipvp.hcf.listeners.player;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.ConfigValues;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.github.paperspigot.event.block.BeaconEffectEvent;

import java.util.*;

public class GlitchListener implements Listener {

    public Map<UUID, EnderPearl> pearlMap = new HashMap<>();

    public GlitchListener() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while(iterator.hasNext()) {
            Recipe recipe = iterator.next();
            switch(recipe.getResult().getType()) {
                case GOLDEN_APPLE: {
                    if(recipe.getResult().getData().getData() == 1)
                        iterator.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final EnderPearl pearl;
        if ((pearl = (EnderPearl) this.pearlMap.remove(player.getUniqueId())) != null) {
            pearl.remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final EnderPearl pearl;
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN
                && (pearl = (EnderPearl) this.pearlMap.remove(player.getUniqueId())) != null) {
            pearl.remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPearl(final ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        final ProjectileSource shooter = projectile.getShooter();
        if (shooter instanceof Player && projectile instanceof EnderPearl) {
            final Player player = (Player) shooter;
            final EnderPearl enderPearl = (EnderPearl) projectile;
            pearlMap.put(player.getUniqueId(), enderPearl);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        Location location = e.getLocation();
        CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();
        if(reason != CreatureSpawnEvent.SpawnReason.SLIME_SPLIT && reason != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            if(location.getChunk().getEntities().length >= ConfigValues.LIMITERS_ENTITY_PER_CHUNK)
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBoatPlace(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();
        if(player.getItemInHand().getType() == Material.BOAT)
            e.setCancelled(true);
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;
        if(!(e.getProjectile() instanceof Arrow))
            return;
        
        Arrow arrow = (Arrow) e.getProjectile();
        if(e.getBow().getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE))
            arrow.setMetadata("infinite", new FixedMetadataValue(HCF.getInstance(), true));
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent e) {
        if(!(e.getEntity() instanceof Arrow))
            return;

        Arrow arrow = (Arrow) e.getEntity();
        if(arrow.hasMetadata("infinite"))
            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), arrow::remove, 25L);

        if(!(arrow.getShooter() instanceof Player))
            arrow.remove();
    }


    @EventHandler
    public void onBeaconStrength(BeaconEffectEvent e) {
        PotionEffect effect = e.getEffect();
        if(e.getEffect().getType() != PotionEffectType.INCREASE_DAMAGE)
            return;
        if(ConfigValues.LIMITERS_BEACON_STRENGTH <= 0) {
            e.setCancelled(true);
            return;
        }

        if(effect.getAmplifier() > ConfigValues.LIMITERS_BEACON_STRENGTH - 1)
            e.setEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effect.getDuration(),ConfigValues.LIMITERS_BEACON_STRENGTH - 1, effect.isAmbient()));
    }

    @EventHandler
    public void onInteractEnderChest(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(e.getClickedBlock().getType() != Material.ENDER_CHEST)
            return;

        if (ConfigValues.LISTENERS_DISABLE_ENDERCHEST) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(event.getInventory().getType() != InventoryType.ENDER_CHEST)
            return;

        if (ConfigValues.LISTENERS_DISABLE_ENDERCHEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedPlace(BlockPlaceEvent e) {
        if(e.getPlayer().getWorld().getEnvironment() != World.Environment.NETHER)
            return;

        if(e.getBlockPlaced().getType() == Material.BED || e.getBlockPlaced().getType() == Material.BED_BLOCK)
            e.setCancelled(true);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if(result == null || result.getType() == Material.AIR)
            return;

        if(result.getType() == Material.GOLDEN_APPLE && result.getData().getData() == 1)
            event.getInventory().setResult(new ItemStack(Material.AIR));
    }

    /*@EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        if(!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();
        if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            PotionEffect potionEffect = player.getActivePotionEffects().stream().filter(effect -> effect.getType() == PotionEffectType.INCREASE_DAMAGE).findFirst().orElse(null);
            if(potionEffect == null)
                return;

            event.setDamage(event.getDamage() - (0.5 * potionEffect.getAmplifier()));
        }
    }*/
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                for(PotionEffect effect : player.getActivePotionEffects()) {
                    if(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        double damagePercentage = (effect.getAmplifier() + 1) * 1.3 + 1.0;
                        int newDamage;
                        if(event.getDamage() / damagePercentage <= 1.0) {
                            newDamage = (effect.getAmplifier() + 1) * (new Random().nextInt(2) + 1) + 1;
                        } else {
                            newDamage = (int) (event.getDamage() / damagePercentage) + (effect.getAmplifier() + 1) * 3;
                        }
                        event.setDamage(newDamage);
                        break;
                    }
                }
            }
        }
    }

    /*@EventHandler
    public void onSplash(PotionSplashEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player))
            return;

        event.setCancelled(false);

        Player shooter = (Player) event.getEntity().getShooter();
        List<MobEffect> list = (List<MobEffect>) Items.POTION.g(CraftItemStack.asNMSCopy(event.getEntity().getItem()));
        if(list == null || list.isEmpty())
            return;

        for(LivingEntity entity : event.getAffectedEntities()) {
            if(!(entity instanceof CraftLivingEntity))
                continue;

            EntityLiving entityLiving = ((CraftLivingEntity) entity).getHandle();
            for(MobEffect effect : list) {
                int id = effect.getEffectId();
                if(MobEffectList.byId[id].isInstant())
                    MobEffectList.byId[id].applyInstantEffect(((CraftPlayer) shooter).getHandle(), entityLiving, effect.getAmplifier(), event.getIntensity(entity));
                else {
                    int time = (int) (event.getIntensity(entity) * (double) effect.getDuration() + 0.5D);
                    if(time > 20)
                        entityLiving.addEffect(new MobEffect(id, time, effect.getAmplifier()));
                }
            }
        }
    }*/

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockGlitchJump(BlockPlaceEvent event) {
        if(!event.isCancelled())
            return;

        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight())
            return;

        Block block = event.getBlockPlaced();
        if(!block.getType().isSolid() || block.getState() instanceof Sign)
            return;

        int playerY = player.getLocation().getBlockY();
        int blockY = block.getLocation().getBlockY();
        if(playerY > blockY) {
            Vector vector = player.getVelocity();
            vector.setX(-0.1);
            vector.setZ(-0.1);
            player.setVelocity(vector.setY(vector.getY() - 0.41999998688697815D));
        }
    }
}
