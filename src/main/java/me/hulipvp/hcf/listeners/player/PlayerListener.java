package me.hulipvp.hcf.listeners.player;

import me.activated.core.plugin.AquaCoreAPI;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.api.events.faction.player.FactionKickEvent;
import me.hulipvp.hcf.api.events.kit.KitDisableEvent;
import me.hulipvp.hcf.api.events.kit.KitEnableEvent;
import me.hulipvp.hcf.commands.staff.ChatCommand;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.kits.type.MinerKit;
import me.hulipvp.hcf.game.player.data.ChatMode;
import me.hulipvp.hcf.game.player.data.Death;
import me.hulipvp.hcf.game.player.data.Kill;
import me.hulipvp.hcf.game.player.data.mod.Vanish;
import me.hulipvp.hcf.game.player.data.setting.HCFSetting;
import me.hulipvp.hcf.game.player.data.setting.SettingType;
import me.hulipvp.hcf.game.player.data.statistic.HCFStatistic;
import me.hulipvp.hcf.game.player.data.statistic.StatisticType;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.timer.type.server.type.RampageTimer;
import me.hulipvp.hcf.utils.*;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.commands.member.BottleCommand;
import me.hulipvp.hcf.commands.staff.ModCommand;
import me.hulipvp.hcf.commands.member.StatsCommand;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.game.kits.type.BardKit;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.listeners.EventListener;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import me.hulipvp.hcf.utils.player.PlayerUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    public static Inventory frozenInventory;

    public PlayerListener() {
        if(ConfigValues.FREEZE_GUI_ENABLED) {
            frozenInventory = Bukkit.createInventory(null, 9, "You are frozen!");
            frozenInventory.setItem(4, new ItemBuilder(Material.PAPER).lore(ConfigValues.FREEZE_GUI_ITEM_LINES).name(ConfigValues.FREEZE_GUI_ITEM_NAME.replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK)).get());
        }

        if(ConfigValues.RANK_BROADCAST_ENABLED) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
                String broadcast = ConfigValues.RANK_BROADCAST_BROADCAST;
                if(broadcast.isEmpty())
                    return;

                List<String> rankPlayers = new ArrayList<>();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String rank = PlayerUtils.getRankName(player);
                    if(!ConfigValues.RANK_BROADCAST_NAME.equalsIgnoreCase(rank))
                        continue;

                    rankPlayers.add(player.getName());
                }

                switch(rankPlayers.size()) {
                    case 0: {
                        if(ConfigValues.RANK_BROADCAST_NO_PLAYERS.isEmpty())
                            return;
                        else
                            broadcast = broadcast.replace("%players%", ConfigValues.RANK_BROADCAST_NO_PLAYERS);
                        break;
                    }
                    case 2: {
                        broadcast = broadcast.replace("%players%", rankPlayers.get(0) + " and " + rankPlayers.get(1));
                        break;
                    }
                    default: {
                        StringBuilder sb = new StringBuilder();
                        for(int i = 0; i < rankPlayers.size(); i++) {
                            if(i == rankPlayers.size() - 1)
                                sb.append(ConfigValues.RANK_BROADCAST_JOINER_COLOR).append(rankPlayers.get(i));
                            else
                                sb.append(ConfigValues.RANK_BROADCAST_JOINER_COLOR).append(rankPlayers.get(i)).append(ConfigValues.RANK_BROADCAST_JOINER_CHARACTER);
                        }
                        broadcast = broadcast.replace("%players%", sb.toString());
                    }
                }

                Bukkit.broadcastMessage(C.color(broadcast));
            }, 300L, (20L * 60L) * ConfigValues.RANK_BROADCAST_TIME);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();

        HCFProfile profile = HCFProfile.getByPlayer(player);
        profile.removeTimersByType(PlayerTimerType.COMBAT);

        if (profile.getFactionObj() != null) {
            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.chat("/f who");

                }
            }, 60);
        }

        if(profile.isDeathBanned() && !ConfigValues.FEATURES_KITMAP) {
            if(System.currentTimeMillis() > profile.getBannedTill() || profile.getLives() > 0) {
                if(System.currentTimeMillis() < profile.getBannedTill()) {
                    profile.setLives(profile.getLives() - 1);
                    player.sendMessage(Locale.DEATHBAN_USED_LIFE.toString());
                }

                profile.setBannedTill(0L);
                profile.setDeathBanned(false);

                player.setHealth(20);
                player.setFoodLevel(20);
                player.setExp(0);
                player.setLevel(0);

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.updateInventory();
                player.getActivePotionEffects().forEach(potionEffect -> {
                    player.removePotionEffect(potionEffect.getType());
                });

                Faction spawn = Faction.getByName("Spawn");
                if(spawn != null && spawn.getHome() != null)
                    player.teleport(spawn.getHome());

                PlayerTimer timer = new PlayerTimer(player, PlayerTimerType.PVPTIMER);
                timer.setPaused(true);
                timer.add();
            } else {
                String remaining = TimeUtils.getTimeTill(new Timestamp(profile.getBannedTill()));

                player.kickPlayer(Locale.DEATHBAN_KICK_MESSAGE.toString().replace("%remaining%", remaining).replace("%store%", ConfigValues.SERVER_STORE));
                HCFProfile.getProfiles().remove(event.getPlayer().getUniqueId().toString());
                return;
            }
            Faction spawn = Faction.getByName("Spawn");
            if(spawn != null && spawn.getHome() != null)
                player.teleport(spawn.getHome());
        } else if (profile.isDeathBanned()) {
            profile.setDeathBanned(false);

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setExp(0);
            player.setLevel(0);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            player.getActivePotionEffects().forEach(potionEffect -> {
                player.removePotionEffect(potionEffect.getType());
            });

            Faction spawn = Faction.getByName("Spawn");
            if(spawn != null && spawn.getHome() != null)
                player.teleport(spawn.getHome());
        }

        Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
            if (profile.getFactionObj() == null) {
                for (Faction faction : Faction.getFactions().values()) {
                    if (faction instanceof PlayerFaction) {
                        PlayerFaction playerFaction = (PlayerFaction) faction;
                        if (playerFaction.getMembers().containsKey(event.getPlayer().getUniqueId())) {
                            System.out.println("------------------------------");
                            System.out.println("User had a possible bugged faction, but he was found in another faction and got restored: Faction = " + playerFaction.getName());
                            System.out.println("------------------------------");
                            profile.setFaction(playerFaction.getUuid());
                        }
                    }
                }
            }

            if(profile.getFactionObj() != null)
                profile.getFactionObj().sendMessage(Locale.FACTION_MEMBER_ONLNE.toString().replace("%name%", event.getPlayer().getName()));
        });

        if(!player.hasPlayedBefore()) {
            int startingBalance = ConfigValues.WELCOME_STARTING_BALANCE;
            profile.setBalance(startingBalance);

            if (ConfigValues.WELCOME_STARTING_BALANCE_MESSAGE) {
                player.sendMessage(Locale.WELCOME_STARTING_BALANCE_MESSAGE.toString());
            }

            if (!ConfigValues.FEATURES_KITMAP) {
                PlayerTimer timer = new PlayerTimer(player, PlayerTimerType.PVPTIMER);
                timer.setPaused(true);
                timer.add();
            }

            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                Faction faction = Faction.getByName("Spawn");
                if(faction != null && faction.getHome() != null)
                    player.teleport(faction.getHome());
            }, 2L);
        }

        if (Faction.getByLocation(player.getLocation()) != null) {
            if (Faction.getByLocation(player.getLocation()).equals(Faction.getByName("Spawn")) && ConfigValues.FEATURES_HIDDEN_SPAWN_PLAYERS) {
                profile.handleVisibility();
            }
        }

        if (profile.hasTimer(PlayerTimerType.PVPTIMER)) {
            Faction faction = Faction.getByLocation(player.getLocation());
            if (faction != null && !(faction instanceof SafezoneFaction)) {
                profile.getTimerByType(PlayerTimerType.PVPTIMER).setPaused(false);
            }
        }

        if (player.hasPermission("hcf.staff")) {
            if (HCF.getInstance().getLunarHook() != null) {
                Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                    if (HCF.getInstance().getLunarHook().isLunarPlayer(player)) {
                        HCF.getInstance().getLunarHook().toggleStaffModules(player);
                    }
                }, 20L);
            }
        }

        if (player.hasPermission("hcf.mod.onjoin")
                && HCF.getInstance().getModModeFile().getConfig().getBoolean("mod-mode.enabled", true)
                && HCF.getInstance().getModModeFile().getConfig().getBoolean("mod-mode.on-join-enabled", true)) {
            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                profile.setVanish(new Vanish(player));
                profile.getVanish().enable();
                player.sendMessage(Locale.MODMODE_ENABLED.toString());
            }, 4L);
        }

        profile.setName(event.getPlayer().getName());
        profile.setRankName(HCF.getInstance().getPlayerHook().getRankName(event.getPlayer()));
        profile.save();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        HCFProfile profile = HCFProfile.getByUuid(event.getPlayer().getUniqueId());
        profile.removeTimersByType(PlayerTimerType.COMBAT);
        Faction spawn = Faction.getByName("Spawn");
        if (spawn != null && spawn.getHome() != null) {
            event.setRespawnLocation(spawn.getHome());

            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                profile.removeTimersByType(PlayerTimerType.COMBAT);
                event.getPlayer().teleport(spawn.getHome());
            }, 4L);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());

        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onKick(PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());

        event.setLeaveMessage(null);
    }

    private void handleDisconnect(Player p) {
        HCFProfile profile = HCFProfile.getByPlayer(p);
        profile.removeTimersByType(PlayerTimerType.COMBAT);

        if(profile.hasFaction() && !profile.isDeathBanned()) {
            PlayerFaction pf = profile.getFactionObj();
            pf.sendMessage(Locale.FACTION_MEMBER_OFFLINE.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
        }

        if (profile.getCurrentKit() != null) {
            for (PotionEffect effect : profile.getCurrentKit().getEffects())
                p.removePotionEffect(effect.getType());

            KitDisableEvent kitDisableEvent = new KitDisableEvent(p, profile.getCurrentKit());
            Bukkit.getPluginManager().callEvent(kitDisableEvent);

            profile.setCurrentKit(null);
        }

        EventListener.removeFromConquestCap(p);
        EventListener.removeFromKothCap(p);

        if(p.getInventory().contains(LocUtils.getClaimingWand()))
            p.getInventory().remove(LocUtils.getClaimingWand());

        profile.save();
        BardKit.getPlayerEnergy().remove(profile.getUuid().toString());
        HCFProfile.getProfiles().remove(profile.getUuid().toString());

//        if(ConfigValues.RANK_BROADCAST_ENABLED)
//            rankPlayers.remove(p.getName());
        /*new BukkitRunnable() {
            @Override
            public void run() {
                if(profile.getPlayer() == null || !profile.getPlayer().isOnline())
                    HCFProfile.getProfiles().remove(profile.getUuid().toString());
            }
        }.runTaskLaterAsynchronously(HCF.getInstance(), 20L * 120L);*/
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();

        Player killer = dead.getKiller();

        EventListener.removeFromKothCap(dead);
        EventListener.removeFromConquestCap(dead);

        Death death = new Death(dead.getUniqueId(), ((killer == null) ? null : killer.getUniqueId()));
        HCFProfile deadProfile = HCFProfile.getByPlayer(dead);
        deadProfile.addDeath(death);
        deadProfile.removeTimersByType(PlayerTimerType.COMBAT);
        deadProfile.removeTimersByType(PlayerTimerType.ENDERPEARL);
        deadProfile.removeTimersByType(PlayerTimerType.SPEED_EFFECT);
        deadProfile.removeTimersByType(PlayerTimerType.JUMP_EFFECT);
        deadProfile.removeTimersByType(PlayerTimerType.LOGOUT);
        if (ConfigValues.ELO_ENABLED) deadProfile.removeFromElo(ConfigValues.ELO_POINTS_ON_DEATH);

        if(killer != null) {
            HCFProfile killerProfile = HCFProfile.getByPlayer(killer);
            Kill kill = new Kill(killer.getUniqueId(), dead.getUniqueId());
            killerProfile.addKill(kill);

            if (ConfigValues.ELO_ENABLED) killerProfile.addToElo(ConfigValues.ELO_POINTS_ON_KILL);

            if (killerProfile.getFactionObj() != null) {
                PlayerFaction pf = PlayerFaction.getPlayerFaction(killerProfile.getFaction());
                if (killer.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                    pf.setPoints(pf.getPoints() + ConfigValues.FACTIONS_POINTS_WORLD_KILL_POINTS);
                } else if (killer.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                    pf.setPoints(pf.getPoints() + ConfigValues.FACTIONS_POINTS_NETHER_KILL_POINTS);
                } else {
                    pf.setPoints(pf.getPoints() + ConfigValues.FACTIONS_POINTS_END_KILL_POINTS);
                }
            }

            if (HCF.getInstance().getConfig().contains("board.rampage")) {
                ServerTimer serverTimer = Timer.getTimers().values().stream()
                        .filter(ServerTimer.class::isInstance)
                        .map(ServerTimer.class::cast)
                        .filter(timer -> timer.getType() == ServerTimerType.RAMPAGE)
                        .findFirst()
                        .orElse(null);

                if (serverTimer != null) {
                    RampageTimer rampageTimer = (RampageTimer) serverTimer;
                    if (rampageTimer.getKills().containsKey(killer.getName())) {
                        rampageTimer.getKills().put(killer.getName(), rampageTimer.getKills().get(killer.getName()) + 1);
                    } else {
                        rampageTimer.getKills().put(killer.getName(), 1);
                    }
                }
            }


            if(ConfigValues.FEATURES_KITMAP) {
                killerProfile.setStreak(killerProfile.getStreak() + 1);

                int streak = killerProfile.getStreak();
                if(ConfigValues.KITMAP_STREAK_REWARDS.containsKey(streak)) {
                    Bukkit.broadcastMessage(Locale.KITMAP_REWARDED_STREAK.toString().replace("%player%", killer.getName())
                            .replace("%name%", ConfigValues.KITMAP_STREAK_NAMES.get(streak)));
                    for(String command : ConfigValues.KITMAP_STREAK_REWARDS.get(streak))
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", killer.getName()));
                }

                String unparsedAmount = ConfigValues.KITMAP_MONEY_PER_KILL;
                int amount;
                if(!StringUtils.isInt(unparsedAmount)) {
                    amount = deadProfile.getBalance();
                } else {
                    amount = Integer.parseInt(unparsedAmount);
                    if(deadProfile.getBalance() < amount)
                        amount = deadProfile.getBalance();
                }

                if(amount > 0) {
                    deadProfile.removeFromBalance(amount);

                    if (killer.hasPermission("hcf.doublekillreward")) amount = (amount * 2);

                    killerProfile.addToBalance(amount);

                    killer.sendMessage(Locale.KITMAP_REWARDED_MONEY.toString().replace("%amount%", String.valueOf(amount)).replace("%player%", dead.getName()));

                    dead.sendMessage(Locale.KITMAP_MONEY_TAKEN.toString().replace("%amount%", String.valueOf(amount)).replace("%name%", killer.getName()));
                }
            }

            killerProfile.save();

            if (killer.hasPermission("hcf.headdrop")) {
                ItemStack skull = new ItemBuilder(new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()))
                        .name("&b" + dead.getName() + "'s Head")
                        .owner(dead.getName())
                        .get();

                event.getDrops().add(skull);
            }
        }

        deadProfile.setDeathBanned(true);

        if(!ConfigValues.FEATURES_KITMAP) {
            if(!dead.hasPermission("hcf.deathban.bypass") || !dead.isOp()) {
                deadProfile.setBannedTill(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(deadProfile.getDeathbanTime(null)));

                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                long currentMillis = System.currentTimeMillis();

                from.setTime(new Date(currentMillis));
                to.setTime(new Date(deadProfile.getBannedTill()));

                Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                    dead.kickPlayer(Locale.DEATHBAN_KICK_MESSAGE.toString()
                            .replace("%remaining%", TimeUtils.formatDateDiff(from, to))
                    );
                }, 10L);
            }
        }

        deadProfile.saveSync();

        if (deadProfile.getFactionObj() != null) {
            PlayerFaction pf = deadProfile.getFactionObj();
            DecimalFormat df = new DecimalFormat("#.##");

            String newDtr = df.format(pf.getDtr() - ConfigValues.FACTIONS_DTR_DEATH);

            pf.setDtr(Double.valueOf(newDtr));
            pf.setStartRegen(new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(ConfigValues.FACTIONS_DTR_REGEN_START_DELAY)));
            pf.sendMessage(Locale.FACTION_MEMBER_DEATH.toString()
                    .replace("%name%", dead.getName())
                    .replace("%dtr%", newDtr)
            );
            if (newDtr.contains("-")) {
                LunarClientAPI api = LunarClientAPI.getInstance();
                for (UUID uuid : pf.getMembers().keySet()) {
                    Player member = Bukkit.getPlayer(uuid);
                    if (member == null) {
                        continue;
                    }
                    if (api.isRunningLunarClient(member)) {
                        member.playSound(member.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
                        api.sendTitle(member, TitleType.TITLE, CC.translate("&cYou are RAIDABLE!"), Duration.ofSeconds(3));
                    }
                }
            }
            pf.setupRegenTask();

            pf.save();
        }

        if(ConfigValues.FEATURES_KITMAP)
            deadProfile.setStreak(0);

        if(!ConfigValues.FEATURES_DEATH_LIGHTNING)
            return;

        // Prevents lag on servers if the server already has low TPS
        if(Bukkit.spigot().getTPS()[0] <= 19.9)
            return;

        Location location = dead.getLocation();
        for(HCFProfile profile : HCFProfile.getProfiles().values()) {
            Player other = profile.getPlayer();
            if(other == null)
                return;
            if (profile.getSetting(SettingType.DEATH_LIGHTNING) == null)
                return;
            if(!profile.getSetting(SettingType.DEATH_LIGHTNING).isValue())
                return;

            location.getWorld().strikeLightningEffect(location);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public final void onCtCommand(final PlayerCommandPreprocessEvent event) {
        boolean hasCombat = HCFProfile.getByPlayer(event.getPlayer()).hasTimer(PlayerTimerType.COMBAT);
        Faction faction = Faction.getByName("Citadel");
        if (faction.isInsideClaim(event.getPlayer().getLocation())) {
            final String[] givenCommand = event.getMessage().substring(1).split(" ", 3);
            if (!ConfigValues.COMBAT_COMMAND_LIST_TYPE.equals("NONE")) {
                final boolean contains = recursiveContainsCommand(givenCommand, ConfigValues.COMBAT_COMMAND_LIST);
                if (contains && ConfigValues.COMBAT_COMMAND_LIST_TYPE.equals("BLACKLIST")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Locale.IN_COMBAT.toString());
                } else if (!contains && ConfigValues.COMBAT_COMMAND_LIST_TYPE.equals("WHITELIST")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Locale.IN_COMBAT.toString());
                }
            }
        }
        if (hasCombat) {
            final String[] givenCommand = event.getMessage().substring(1).split(" ", 3);

            if (!ConfigValues.COMBAT_COMMAND_LIST_TYPE.equals("NONE")) {
                final boolean contains = recursiveContainsCommand(givenCommand, ConfigValues.COMBAT_COMMAND_LIST);
                if (contains && ConfigValues.COMBAT_COMMAND_LIST_TYPE.equals("BLACKLIST")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Locale.IN_COMBAT.toString());
                } else if (!contains && ConfigValues.COMBAT_COMMAND_LIST_TYPE.equals("WHITELIST")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Locale.IN_COMBAT.toString());
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public final void outsideSpawnCommandBlocker(final PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("hcf.staff")) return;

        Faction faction = Faction.getByLocation(event.getPlayer().getLocation());
        if (faction != null && !(faction instanceof SafezoneFaction)) {
            final String[] givenCommand = event.getMessage().substring(1).split(" ", 3);

            final boolean contains = recursiveContainsCommand(givenCommand, ConfigValues.FACTIONS_SAFEZONE_BLOCKED_CMDS);
            if (contains) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Locale.OUTSIDE_SAFEZONE.toString());
            }
        }

    }

    public static boolean recursiveContainsCommand(final String[] givenCommand, final List<String> list) {
        boolean contains = false;
        for (int i = 0; i < givenCommand.length; i++) {
            String args = givenCommand[0];
            for (int j = 1; j <= i; j++) {
                args += " " + givenCommand[j];
            }
            if (list.contains(args.toLowerCase())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().isSimilar(LocUtils.getClaimingWand()))
            return;

        Inventory top = event.getView().getTopInventory();
        if(top.getType() == InventoryType.ENDER_CHEST || top.getType() == InventoryType.CHEST ||
                top.getType() == InventoryType.WORKBENCH || top.getType() == InventoryType.ENCHANTING ||
                top.getType() == InventoryType.ANVIL || top.getType() == InventoryType.HOPPER ||
                top.getType() == InventoryType.DISPENSER || top.getType() == InventoryType.DROPPER ||
                top.getType() == InventoryType.MERCHANT || top.getType() == InventoryType.FURNACE ||
                top.getType() == InventoryType.BEACON || top.getType() == InventoryType.BREWING) {
            event.setCancelled(true);
        }
    }

    private static Map<UUID, Long> slowCooldown = new HashMap<>();

    public static void onChatEvent(AsyncPlayerChatEvent event) {
        if(PlayerUtils.hasHook() && !HCF.getInstance().getPlayerHook().canChat(event.getPlayer()))
            return;

        if(ChatCommand.muted || ChatCommand.slow > 0) {
            Player player = event.getPlayer();
            if(!player.hasPermission("hcf.staff")) {
                if(ChatCommand.muted) {
                    event.setCancelled(true);
                    player.sendMessage(Locale.COMMAND_CHAT_CHAT_MUTED.toString());
                    return;
                } else if(ChatCommand.slow > 0) {
                    long now = System.currentTimeMillis();

                    long lastChat = 0;
                    if(slowCooldown.containsKey(player.getUniqueId()))
                        lastChat = slowCooldown.get(player.getUniqueId());

                    if(lastChat > 0) {
                        long earliestNext = lastChat + ChatCommand.slow * 1000;
                        if(now < earliestNext) {
                            event.setCancelled(true);
                            player.sendMessage(Locale.COMMAND_CHAT_CHAT_SLOWED.toString().replace("%time%", TimeUtils.getFormatted(earliestNext - now, true, true) + ""));
                            return;
                        }
                    }

                    slowCooldown.put(player.getUniqueId(), now);
                }
            }
        }

        event.setCancelled(true);

        String message = event.getMessage();
        if(event.getPlayer().hasPermission("hcf.chatwithcolors"))
            message = C.color(message);

        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        if(profile.isStaffChat()) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(ply -> ply.hasPermission("hcf.staff"))
                    .forEach(ply -> {
                        ply.sendMessage(Locale.COMMAND_STAFF_CHAT_MESSAGE.toString().replace("%name%", profile.getName()).replace("%message%", event.getMessage()));
                    });
            return;
        }

        String chatDisplay = profile.getChatPrefix();
        Set<Player> recipients = event.getRecipients();
        recipients.removeIf(player -> {
            if(player.getName().equals(profile.getName()))
                return false;

            HCFProfile other = HCFProfile.getByPlayer(player);
            HCFSetting setting = other.getSetting(SettingType.PUBLIC_CHAT);
            return setting != null && !setting.isValue();
        });

        if(message.length() >= 2) {
            if(message.startsWith("!")) {
                sendGlobalMessage(profile, profile.getName(), chatDisplay, message.substring(1), recipients);
                return;
            } else if(message.startsWith("@")) {
                sendFactionMessage(profile, profile.getName(), message.substring(1));
                return;
            } else if(message.startsWith("^")) {
                sendOfficerMessage(profile, profile.getName(), message.substring(1));
                return;
            }
        }

        switch(profile.getChatMode()) {
            case PUBLIC: {
                sendGlobalMessage(profile, profile.getName(), chatDisplay, message, recipients);
                return;
            }
            case FACTION: {
                sendFactionMessage(profile, profile.getName(), message);
                return;
            }
            case ALLY: {
                sendAllyMessage(profile, profile.getName(), message);
                return;
            }
            case CAPTAIN: {
                sendOfficerMessage(profile, profile.getName(), message);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        onChatEvent(event);
    }

    private static void sendGlobalMessage(HCFProfile profile, String name, String chatDisplay, String message, Set<Player> recipients) {
        if(profile.getFactionObj() != null) {
            PlayerFaction pf = profile.getFactionObj();

            for(Player player : recipients) {
                if(pf.getMembers().containsKey(player.getUniqueId())) {
                    player.sendMessage(Placeholders.replacePlaceholders(Locale.CHAT_FORMAT_PUBLIC_FACTION.toString().replace("%name%", C.color("&2" + pf.getName())).replace("%display%", chatDisplay).replace("%suffix%", HCF.getInstance().getPlayerHook().getRankSuffix(profile.getPlayer())).replace("%tag%", HCF.getInstance().getPlayerHook().getTag(profile.getPlayer())).replace("%elo%", Integer.toString(profile.getElo())).replace("%message%", message), profile.getPlayer(), profile));
                } else {
                    player.sendMessage(Placeholders.replacePlaceholders(Locale.CHAT_FORMAT_PUBLIC_FACTION.toString().replace("%name%", C.color("&e" + pf.getName())).replace("%display%", chatDisplay).replace("%suffix%", HCF.getInstance().getPlayerHook().getRankSuffix(profile.getPlayer())).replace("%tag%", HCF.getInstance().getPlayerHook().getTag(profile.getPlayer())).replace("%elo%", Integer.toString(profile.getElo())).replace("%message%", message), profile.getPlayer(), profile));
                }
            }

            Bukkit.getLogger().info("[" + pf.getName() + "] " + name  + ": " + message);
        } else {
            for(Player player : recipients) {
                player.sendMessage(Placeholders.replacePlaceholders(Locale.CHAT_FORMAT_PUBLIC_NO_FACTION.toString().replace("%display%", chatDisplay).replace("%tag%", HCF.getInstance().getPlayerHook().getTag(profile.getPlayer())).replace("%suffix%", HCF.getInstance().getPlayerHook().getRankSuffix(profile.getPlayer())).replace("%elo%", Integer.toString(profile.getElo())).replace("%message%", message), profile.getPlayer(), profile));
            }
            Bukkit.getLogger().info(name + ": " + message);
        }
    }

    private static void sendFactionMessage(HCFProfile profile, String name, String message) {
        if(profile.getFactionObj() != null) {
            PlayerFaction pf = profile.getFactionObj();
            String prefix = ChatMode.FACTION.getPrefix();

            pf.sendMessage(Placeholders.replacePlaceholders(Locale.CHAT_FORMAT_FACTION.toString().replace("%prefix%", prefix).replace("%tag%", HCF.getInstance().getPlayerHook().getTag(profile.getPlayer())).replace("%name%", name).replace("%suffix%", HCF.getInstance().getPlayerHook().getRankSuffix(profile.getPlayer())).replace("%elo%", Integer.toString(profile.getElo())).replace("%message%", ChatColor.YELLOW + message), profile.getPlayer(), profile));
            Bukkit.getLogger().info(C.strip(prefix + " [" + pf.getName() + "] " + name + ": " + message));
        } else {
            profile.setChatMode(ChatMode.PUBLIC);
        }
    }

    private static void sendAllyMessage(HCFProfile profile, String name, String message) {
        if(profile.getFactionObj() != null) {
            PlayerFaction pf = profile.getFactionObj();
            String prefix = ChatMode.ALLY.getPrefix();

            pf.sendAllyMessage(Placeholders.replacePlaceholders(Locale.CHAT_FORMAT_ALLY.toString().replace("%prefix%", prefix).replace("%name%", name).replace("%tag%", HCF.getInstance().getPlayerHook().getTag(profile.getPlayer())).replace("%suffix%", HCF.getInstance().getPlayerHook().getRankSuffix(profile.getPlayer())).replace("%elo%", Integer.toString(profile.getElo())).replace("%message%", ChatColor.YELLOW + message), profile.getPlayer(), profile));
            Bukkit.getLogger().info(C.strip(prefix + " [" + pf.getName() + "] " + name + ": " + message));
        } else {
            profile.setChatMode(ChatMode.PUBLIC);
        }
    }

    private static void sendOfficerMessage(HCFProfile profile, String name, String message) {
        if(profile.getFactionObj() != null) {
            PlayerFaction pf = profile.getFactionObj();
            if(!pf.getMembers().get(profile.getUuid()).isAtLeast(FactionRank.CAPTAIN)) {
                profile.setChatMode(ChatMode.PUBLIC);
                return;
            }

            String prefix = ChatMode.CAPTAIN.getPrefix();

            pf.sendCaptainMessage(Locale.CHAT_FORMAT_CAPTAIN.toString().replace("%prefix%", prefix).replace("%name%", name).replace("%tag%", HCF.getInstance().getPlayerHook().getTag(profile.getPlayer())).replace("%suffix%", HCF.getInstance().getPlayerHook().getRankSuffix(profile.getPlayer())).replace("%elo%", Integer.toString(profile.getElo())).replace("%message%", ChatColor.YELLOW + message));
            Bukkit.getLogger().info(C.strip(prefix + " [" + pf.getName() + "] " + name + ": " + message));
        } else {
            profile.setChatMode(ChatMode.PUBLIC);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        if(to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ())
            return;

        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        if(profile.getMapLocation() != null && to.getWorld() == profile.getMapLocation().getWorld() && to.distance(profile.getMapLocation()) >= 25)
            profile.updateMap(to);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        StatisticType type = StatisticType.getByMaterial(event.getBlock().getType());
        if(type == null)
            return;

        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        HCFStatistic statisticObj = profile.getStatistic(type);
        if(statisticObj != null) {
            profile.updateStatistic(statisticObj.increment());
            profile.save();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onStatInvClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null || event.getClickedInventory().getTitle() == null)
            return;

        Inventory inventory = event.getClickedInventory();
        if(!(inventory.getTitle().equals(CC.translate(ConfigValues.SETTINGS_GUI_TITLE.replace("%primary%", ConfigValues.SERVER_PRIMARY).replace("%secondary%", ConfigValues.SERVER_SECONDARY))) || inventory.getTitle().contains("'s Statistics") || inventory.getTitle().equals(CC.translate(ConfigValues.SERVER_PRIMARY + "Mapkit")) || inventory.getTitle().contains("'s Recent Deaths") || inventory.getTitle().contains("'s Recent Kills")))
            return;

        event.setCancelled(true);

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        if(CC.translate(inventory.getTitle()).equals(CC.translate(ConfigValues.SETTINGS_GUI_TITLE.replace("%primary%", ConfigValues.SERVER_PRIMARY).replace("%secondary%", ConfigValues.SERVER_SECONDARY)))) {
            HCFProfile profile = HCFProfile.getByPlayer((Player) event.getWhoClicked());
            HCFSetting setting = profile.getSetting(SettingType.getByMaterial(event.getCurrentItem().getType()));
            if(setting == null)
                return;

            setting.toggle();
            profile.save();
            event.getClickedInventory().setItem(event.getSlot(), setting.getSettingItem());
        } else if(inventory.getTitle().contains("'s Statistics")) {
            ItemStack item = event.getCurrentItem();
            if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
                return;

            String displayName = item.getItemMeta().getDisplayName();
            if(displayName.contains("Kills")) {
                Player player = Bukkit.getPlayerExact(ChatColor.stripColor(inventory.getTitle().split("'")[0]));
                if(player != null && player.isOnline())
                    StatsCommand.openKillsInventory(player, (Player) event.getWhoClicked());
                else
                    event.getWhoClicked().closeInventory();
            } else if(displayName.contains("Deaths")) {
                Player player = Bukkit.getPlayerExact(ChatColor.stripColor(inventory.getTitle().split("'")[0]));
                if(player != null && player.isOnline())
                    StatsCommand.openDeathsInventory(player, (Player) event.getWhoClicked());
                else
                    event.getWhoClicked().closeInventory();
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        if(!profile.isFilterEnabled())
            return;

        Material material = event.getItem().getItemStack().getType();
        if(profile.getFiltered().contains(material))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        BlockState state = event.getClickedBlock().getState();
        if(!(state instanceof Skull))
            return;

        Skull skull = (Skull) state;
        player.sendMessage(Locale.PLAYER_HEAD_INFO.toString()
                .replace("%player%", skull.getSkullType() == SkullType.PLAYER && skull.hasOwner() ? skull.getOwner() : "a " + WordUtils.capitalizeFully(skull.getSkullType().name())));
    }

    @EventHandler
    public void onKitEnable(KitEnableEvent event) {
        Player player = event.getPlayer();
        Kit kit = event.getKit();

        if(kit instanceof BardKit)
            BardKit.getPlayerEnergy().put(player.getUniqueId().toString(), 1);
        else if(kit instanceof MinerKit) {
            if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false));
                player.sendMessage(Locale.MINER_INVIS_STATUS.toString()
                        .replace("%status%", ChatColor.GREEN + "Enabled"));
            }
        }


        player.sendMessage(Locale.KIT_ENABLED.toString().replace("%name%", kit.getName()));
    }

    @EventHandler
    public void onKitDisable(KitDisableEvent event) {
        Player player = event.getPlayer();
        Kit kit = event.getKit();

        if(kit instanceof BardKit)
            BardKit.getPlayerEnergy().remove(player.getUniqueId().toString());
        else if(kit instanceof MinerKit)
            player.removePotionEffect(PotionEffectType.INVISIBILITY);

        player.sendMessage(Locale.KIT_DISABLED.toString().replace("%name%", kit.getName()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        if(event.getClickedBlock().getType() != Material.ENCHANTMENT_TABLE)
            return;
        if(!event.hasItem())
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if(item.getType() != Material.ENCHANTED_BOOK)
            return;

        event.setCancelled(true);

        player.setItemInHand(new ItemStack(Material.BOOK, 1));
        player.updateInventory();

        player.sendMessage(ChatColor.YELLOW + "You have disenchanted your book.");
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Faction faction = Faction.getByLocation(player.getLocation());
        if(player.getFoodLevel() > event.getFoodLevel() && faction instanceof SafezoneFaction)
            event.setCancelled(true);

        if(player.getWorld().getEnvironment() != World.Environment.THE_END)
            return;

        if(player.getFoodLevel() > event.getFoodLevel() && player.getFoodLevel() - event.getFoodLevel() > 0.75)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;

        if (((Player) event.getEntity()).getAllowFlight() || ((Player) event.getEntity()).isFlying()) {
            ((Player) event.getEntity()).setAllowFlight(false);
            ((Player) event.getEntity()).setFlying(false);
        }

        Player player = (Player) event.getEntity();
        Faction faction = Faction.getByLocation(player.getLocation());
        if(faction instanceof SafezoneFaction)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (((Player) event.getDamager()).getPlayer().hasMetadata("hcf-frozen")) event.setCancelled(true);
        }
        if(event.getDamager() instanceof FishHook) {
            FishHook hook = (FishHook) event.getDamager();
            if(hook.getShooter() instanceof Player) {
                Player player = (Player) hook.getShooter();
                Faction faction = Faction.getByLocation(player.getLocation());
                if(faction instanceof SafezoneFaction)
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().trim().toLowerCase();


        if(message.startsWith("/focus"))
            event.setMessage(message.replace("/focus", "/f focus"));
        else if(message.startsWith("/restart"))
            event.setMessage(message.replace("/restart", "/corerestart"));

        if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass")) {
            boolean cancel = true;
            for(String string : ConfigValues.FREEZE_ALLOWED_COMMANDS) {
                if(message.startsWith(string))
                    cancel = false;
            }

            event.setCancelled(cancel);
            if(cancel)
                player.sendMessage(Locale.FREEZE_CANNOT.toString());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageFrozen(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass")) {
                event.setCancelled(true);
                if(event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    damager.sendMessage(Locale.FREEZE_CANNOT_ATTACK.toString());
                }
            }
        }

        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass")) {
                event.setCancelled(true);
                player.sendMessage(Locale.FREEZE_CANNOT.toString());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!ConfigValues.FREEZE_GUI_ENABLED)
            return;

        Player player = (Player) event.getPlayer();
        if(player.hasMetadata("hcf-frozen") && !ModCommand.paniced.contains(player.getUniqueId()) || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.openInventory(frozenInventory);
                }
            }.runTaskLater(HCF.getInstance(), 1L);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMoveFrozen(PlayerMoveEvent event) {
        if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        Player player = event.getPlayer();
        if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass"))
            event.setTo(event.getFrom());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractFrozen(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass")) {
            e.setCancelled(true);
            player.sendMessage(Locale.FREEZE_CANNOT.toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropFrozen(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass")) {
            e.setCancelled(true);
            player.sendMessage(Locale.FREEZE_CANNOT.toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickupFrozen(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        if(player.hasMetadata("hcf-frozen") || ModCommand.serverFrozen && !player.hasPermission("hcf.freeze.bypass")) {
            e.setCancelled(true);
            player.sendMessage(Locale.FREEZE_CANNOT.toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onReviveCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if(!message.contains(" revive"))
            return;

        String rank = message.trim().replace("/", "").split(" ")[0].toLowerCase();
        if(!ConfigValues.REVIVE_PERMISSIONS.containsKey(rank) || !ConfigValues.REVIVE_COOLDOWNS.containsKey(rank))
            return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        if(!player.hasPermission(ConfigValues.REVIVE_PERMISSIONS.get(rank))) {
            player.sendMessage(Locale.NO_PERMISSION.toString());
            return;
        }

        String[] split = message.split(" ");
        if(split.length < 3) {
            player.sendMessage(C.color("Usage: /" + rank + " revive <player>"));
            return;
        }

        String targetName = split[2];
        try {
            HCFProfile profile = HCFProfile.getByPlayer(player);
            if(profile.getLastRevive() != 0 && profile.getLastRevive() > System.currentTimeMillis()) {
                String time = TimeUtils.getTimeTill(new Timestamp(profile.getLastRevive()));

                if(time != null)
                    player.sendMessage(Locale.COMMAND_RANK_REVIVE_COOLDOWN.toString().replace("%time%", time));
                return;
            }


            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if(target == null) {
                player.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", targetName));
                return;
            }
            HCFProfile targetProfile = HCFProfile.getByUuid(target.getUniqueId());
            if(targetProfile.getBannedTill() != 0 && System.currentTimeMillis() < targetProfile.getBannedTill()) {
                targetProfile.setBannedTill(0);
                targetProfile.save();

                profile.setLastRevive(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(ConfigValues.REVIVE_COOLDOWNS.get(rank)));

                player.sendMessage(Locale.COMMAND_REVIVE_SUCCESS.toString().replace("%player%", targetName));
                if(ConfigValues.REVIVE_BROADCASTS.containsKey(rank))
                    Bukkit.broadcastMessage(C.color(ConfigValues.REVIVE_BROADCASTS.get(rank)).replace("%reviver%", player.getName()).replace("%revived%", targetName).replace("%rank%", WordUtils.capitalizeFully(rank)));
            } else {
                player.sendMessage(Locale.COMMAND_REVIVE_NO_DEATHBAN.toString().replace("%player%", targetName));
            }
        } catch(Exception ex) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", targetName));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBottleThrow(PlayerInteractEvent event) {
        if(!event.hasItem())
            return;
        if(!BottleCommand.isXpBottle(event.getItem()))
            return;
        if(!event.getAction().name().contains("RIGHT"))
            return;

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);

        Player player = event.getPlayer();
        int xpValue = BottleCommand.getXpAmount(event.getItem()) * event.getItem().getAmount();

        ExpUtils.setTotalExperience(player, ExpUtils.getTotalExperience(player) + xpValue);

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            player.setItemInHand(null);
            player.updateInventory();

            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 0.8F);
            player.sendMessage(Locale.COMMAND_BOTTLE_REDEEMED.toString().replace("%amount%", xpValue + ""));
        }, 1L);
    }
}
