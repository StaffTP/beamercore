package me.hulipvp.hcf.listeners;

import com.google.common.collect.Sets;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.faction.normal.FactionClaimEvent;
import me.hulipvp.hcf.api.events.faction.normal.FactionDisbandEvent;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Cuboid;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.event.KothFaction;
import me.hulipvp.hcf.game.faction.type.event.MountainFaction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.game.faction.type.system.WarzoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.LocUtils;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FactionListener implements Listener {

    private static final Set<Material> DENIED_BLOCKS = Sets.immutableEnumSet(
            Material.BED,
            Material.BED_BLOCK,
            Material.BEACON,
            Material.FENCE_GATE,
            Material.IRON_DOOR,
            Material.TRAP_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.CHEST,
            Material.ENDER_CHEST,
            Material.TRAPPED_CHEST,
            Material.SOIL,
            Material.CROPS,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.BREWING_STAND,
            Material.HOPPER,
            Material.DROPPER,
            Material.DISPENSER,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.ENCHANTMENT_TABLE,
            Material.ANVIL,
            Material.LEVER,
            Material.FIRE,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.STONE_PLATE,
            Material.WOOD_PLATE
    );

    private Map<UUID, Long> cooldowns;

    public FactionListener() {
        cooldowns = new HashMap<>();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if(e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
            return;

        HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());

        Faction faction = Faction.getByLocation(e.getTo());
        if (faction == null) return;

        if(faction instanceof SafezoneFaction || (faction instanceof KothFaction && !((KothFaction) faction).getKoth().isPearlable())) {
            e.setCancelled(true);
            e.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            e.getPlayer().updateInventory();
            profile.removeTimersByType(PlayerTimerType.ENDERPEARL);

            if(faction instanceof SafezoneFaction)
                e.getPlayer().sendMessage(Locale.TIMER_ENDERPEARL_REFUNDED_SAFEZONE.toString());
            else
                e.getPlayer().sendMessage(Locale.TIMER_ENDERPEARL_REFUNDED_SPECIAL.toString());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerTeleport(ProjectileLaunchEvent e) {
        if(!(e.getEntity() instanceof EnderPearl))
            return;

        if (!(e.getEntity().getShooter() instanceof Player))
            return;

        Player player = (Player) e.getEntity().getShooter();

        HCFProfile profile = HCFProfile.getByPlayer(player);

        Faction faction = Faction.getByLocation(player.getLocation());
        if (faction == null) return;

        if(faction instanceof SafezoneFaction || (faction instanceof KothFaction && !((KothFaction) faction).getKoth().isPearlable())) {
            e.setCancelled(true);
            profile.removeTimersByType(PlayerTimerType.ENDERPEARL);

            if(faction instanceof SafezoneFaction)
                player.sendMessage(Locale.TIMER_ENDERPEARL_REFUNDED_SAFEZONE.toString());
            else
                player.sendMessage(Locale.TIMER_ENDERPEARL_REFUNDED_SPECIAL.toString());

            player.getInventory().addItem(new ItemBuilder(Material.ENDER_PEARL).get());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFactionMemberDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;
        if(!(e.getDamager() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        HCFProfile pProfile = HCFProfile.getByPlayer(player);
        HCFProfile dProfile = HCFProfile.getByPlayer(damager);
        if(pProfile.getFactionObj() == null)
            return;
        if(dProfile.getFactionObj() == null)
            return;

        if(dProfile.getFactionObj().getMembers().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            damager.sendMessage(Locale.FACTION_MEMBER_DAMAGE.toString().replace("%name%", player.getName()));
            return;
        }

        if(dProfile.getFactionObj().isAllied(pProfile.getFactionObj().getUuid()) && ConfigValues.FACTIONS_ALLY_ATTACKING_PREVENT) {
            e.setCancelled(true);
            damager.sendMessage(Locale.FACTION_ALLY_DAMAGE.toString().replace("%name%", player.getName()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSafezoneDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player player = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();

            Faction damagerFaction = Faction.getByLocation(damager.getLocation());
            if(damagerFaction != null) {
                if(damagerFaction instanceof SafezoneFaction || !damagerFaction.isDeathban()) {
                    damager.sendMessage(Locale.FACTION_CANNOT_ATTACK_INSIDE_SAFEZONE.toString().replace("%name%", player.getName()));

                    e.setCancelled(true);
                    return;
                }
            }

            Faction faction = Faction.getByLocation(player.getLocation());
            if(faction != null) {
                if(faction instanceof SafezoneFaction || !faction.isDeathban()) {
                    damager.sendMessage(Locale.FACTION_CANNOT_ATTACK_HERE.toString().replace("%name%", player.getName()));
                    e.setCancelled(true);
                    return;
                }
            }

            return;
        }

        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Faction playerFaction = Faction.getByLocation(player.getLocation());
            if(playerFaction == null)
                return;

            if(playerFaction.getType() == FactionType.SAFEZONE || !playerFaction.isDeathban())
                e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onClaim(FactionClaimEvent event) {
        Claim claim = event.getClaim();
        Cuboid cuboid = claim.toCuboid();
        Location location = cuboid.getWorld().getHighestBlockAt(new Location(cuboid.getWorld(), cuboid.getLowerX() - 1, 0, cuboid.getLowerZ() - 1)).getLocation();
        TaskUtils.runAsync(() -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!cuboid.isInCuboid(player))
                    continue;

                HCFProfile profile = HCFProfile.getByPlayer(player);
                if(profile.hasTimer(PlayerTimerType.PVPTIMER) || profile.hasTimer(PlayerTimerType.STARTING)) {
                    Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                        player.teleport(location.add(0, 1, 0));
                        player.sendMessage(Locale.TIMER_AREA_CLAIMED.toString());
                    }, 1L);
                }
            }
        });
    }

    @EventHandler
    public void onDisband(FactionDisbandEvent event) {
        Faction faction = event.getFaction();
        if(!(faction instanceof PlayerFaction))
            return;

        TaskUtils.runAsync(() -> {
            PlayerFaction playerFaction = (PlayerFaction) faction;
            playerFaction.getAllies().stream()
                    .map(Faction::getFaction)
                    .filter(Objects::nonNull)
                    .filter(PlayerFaction.class::isInstance)
                    .map(PlayerFaction.class::cast)
                    .forEach(pf -> {
                        pf.getAllies().remove(playerFaction.getUuid());
                    });
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent e) {
        Faction faction = Faction.getByLocation(e.getBlock().getLocation());

        e.getBlocks().forEach(block -> {
            Faction factionOne = Faction.getByLocation(block.getLocation());
            Faction factionTwo = Faction.getByLocation(block.getRelative(e.getDirection()).getLocation());

            if((factionOne != null && !factionOne.equals(faction)) || (factionTwo != null && !factionTwo.equals(faction)))
                e.setCancelled(true);
        });
    }

    @EventHandler
    public void onBlock(BlockBreakEvent e) {
        String cancel = handleBlockEvent(e.getPlayer(), e.getBlock(), e);
        if(cancel != null) {
            if (!e.getBlock().getType().name().equals(ConfigValues.DTC_MATERIAL) || !cancel.contains("DTC")) {
                e.getPlayer().sendMessage(Locale.FACTION_CANNOT_BUILD.toString().replace("%faction%", cancel));
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        String cancel = handleBlockEvent(e.getPlayer(), e.getBlock(), e);
        if(cancel != null) {
            e.getPlayer().sendMessage(Locale.FACTION_CANNOT_BUILD.toString().replace("%faction%", cancel));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemFrame(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            if (e.getEntity() instanceof ItemFrame) {
                String cancel = handleBlockEvent(player, e.getEntity().getLocation());
                if(cancel != null) {
                    player.sendMessage(Locale.FACTION_CANNOT_BUILD.toString().replace("%faction%", cancel));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        String cancel = handleBlockEvent(e.getPlayer(), e.getBlockClicked());
        if(cancel != null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Locale.FACTION_CANNOT_BUILD.toString().replace("%faction%", cancel));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        if (profile == null)
            return;
        if (profile.getFactionObj() == null)
            return;
        if (profile.getFactionObj() instanceof PlayerFaction) {
            profile.getFactionObj().setupRegenTask();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        HCFProfile profile = HCFProfile.getByPlayer(event.getEntity());
        if (profile == null)
            return;
        if (profile.getFactionObj() == null)
            return;
        if (profile.getFactionObj() instanceof PlayerFaction) {
            profile.getFactionObj().setupRegenTask();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent e) {
        if (!e.hasBlock()) {
            return;
        }
        if (ConfigValues.PEARL_SHIFT_FENCE_GATE_PEARL_CHECK) {
            if (e.getItem() != null) {
                if (e.getItem().getType().equals(Material.ENDER_PEARL) && e.getPlayer().isSneaking()) {
                    return;
                }
            }
        }

        Material material = e.getClickedBlock().getType();

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (e.getPlayer().getItemInHand() != null) {
                if (e.getPlayer().getItemInHand().getType().name().contains("_HOE")) {
                    if (material.name().contains("GRASS")) {
                        String cancel = handleBlockEvent(e.getPlayer(), e.getClickedBlock());
                        if (cancel != null) {
                            e.getPlayer().sendMessage(Locale.FACTION_CANNOT_USE.toString().replace("%faction%", cancel));
                            e.setCancelled(true);
                            return;
                        }

                        return;
                    }
                }
            }
        }

        if (isElevatorSign(e.getClickedBlock())) return;

        Location loc = e.getClickedBlock().getLocation().clone();
        Block blockAbove = e.getPlayer().getWorld().getBlockAt(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));

        if(DENIED_BLOCKS.contains(material)
                || e.getAction() == Action.PHYSICAL
                || material.name().contains("GATE")
                || material.name().contains("DOOR")
                || (blockAbove != null && blockAbove.getType().equals(Material.FIRE))) {
            ServerTimer serverTimer = ServerTimer.getTimer(ServerTimerType.EXTRACTION);

            if (serverTimer == null) {
                String cancel = handleBlockEvent(e.getPlayer(), e.getClickedBlock(), e);
                if (cancel != null) {
                    if (e.getAction() == Action.PHYSICAL)
                        sendMessage(e.getPlayer(), Locale.FACTION_CANNOT_USE.toString().replace("%faction%", cancel));
                    else
                        e.getPlayer().sendMessage(Locale.FACTION_CANNOT_USE.toString().replace("%faction%", cancel));

                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        String cancel = handleBlockEvent(e.getPlayer(), e.getBlockClicked());
        if(cancel != null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Locale.FACTION_CANNOT_BUILD.toString().replace("%faction%", cancel));
        }
    }

    private boolean isElevatorSign(Block block) {
        if(!block.getType().name().contains("SIGN"))
            return false;

        Sign sign = (Sign) block.getState();
        return org.bukkit.ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Elevator]");
    }

    private void sendMessage(Player player, String message) {
        if(cooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() - cooldowns.get(player.getUniqueId()) < 1000)
            return;

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(message);
    }

    static String handleBlockEvent(Player player, Block block) {
        return handleBlockEvent(player, block.getLocation(), null);
    }

    public static String handleBlockEvent(Player player, Location block) {
        return handleBlockEvent(player, block, null);
    }

    static String handleBlockEvent(Player player, Block block, Event event) {
        return handleBlockEvent(player, block.getLocation(), event);
    }

    static String handleBlockEvent(Player player, Location block, Event event) {
        Faction faction = Faction.getByLocation(block);
        HCFProfile profile = HCFProfile.getByPlayer(player);
        if(profile.isBypass() || player.getGameMode().equals(GameMode.CREATIVE))
            return null;

        if(faction != null) {
            if(faction instanceof PlayerFaction) {
                PlayerFaction pf = (PlayerFaction) faction;
                if(pf.isRaidable())
                    return null;
                if(pf.getMembers().containsKey(player.getUniqueId()))
                    return null;

                return ChatColor.RED + faction.getName();
            } else if(faction instanceof MountainFaction) {
                MountainFaction mf = (MountainFaction) faction;
                if(!(event instanceof BlockBreakEvent)) {
                    if(event instanceof BlockPlaceEvent && mf.getMountain() != null) {
                        if(mf.getMountain().getPoint1() != null && mf.getMountain().getPoint2() != null) {
                            BlockPlaceEvent placeEvent = (BlockPlaceEvent) event;
                            Material type = placeEvent.getBlockPlaced().getType();
                            Cuboid cuboid = new Cuboid(mf.getMountain().getPoint1(), mf.getMountain().getPoint2());

                            return cuboid.isInCuboid(placeEvent.getBlockPlaced().getLocation()) && mf.getMountain().getType().hasMaterial(type) ? null : mf.getColoredName();
                        }
                    }

                    return mf.getColoredName();
                }

                if(mf.getMountain() != null) {
                    if(mf.getMountain().getPoint1() != null && mf.getMountain().getPoint2() != null) {
                        Cuboid cuboid = new Cuboid(mf.getMountain().getPoint1(), mf.getMountain().getPoint2());

                        return (cuboid.isInCuboid(block)) ? null : mf.getColoredName();
                    }
                }
            } else if(faction instanceof WarzoneFaction) {
                WarzoneFaction wf = (WarzoneFaction) faction;
                if (LocUtils.checkWarzoneBuild(block)) {
                    if (event instanceof PlayerInteractEvent
                            && ((PlayerInteractEvent) event).getAction().equals(Action.PHYSICAL)
                            && (((PlayerInteractEvent) event).getClickedBlock().getType().equals(Material.SOIL))) {
                        return wf.getColoredName();
                    }
                    if (event instanceof PlayerInteractEvent
                            && ((PlayerInteractEvent) event).getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        return wf.getColoredName();
                    }
                    return (event instanceof PlayerInteractEvent) ? null : wf.getColoredName();
                }
            } else if(faction instanceof SystemFaction) {
                SystemFaction sf = (SystemFaction) faction;
                return (event instanceof PlayerInteractEvent) ? null : sf.getColoredName();
            } else {
                return ChatColor.RED + faction.getName();
            }
        }

        return null;
    }

    @EventHandler
    public void onPistonPush(BlockPistonExtendEvent event){
        if (Faction.getByLocation(event.getBlock().getLocation()) == null) {
            event.getBlocks().forEach(block -> {
                Faction faction = Faction.getByLocation(block.getLocation());
                if (faction instanceof PlayerFaction) {
                    event.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    public void onPistonPull(BlockPistonRetractEvent event){
        if (Faction.getByLocation(event.getBlock().getLocation()) == null) {
            event.getBlocks().forEach(block -> {
                Faction faction = Faction.getByLocation(block.getLocation());
                if (faction instanceof PlayerFaction) {
                    event.setCancelled(true);
                }
            });
        }
    }
}
