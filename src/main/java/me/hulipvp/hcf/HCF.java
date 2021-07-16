package me.hulipvp.hcf;

import lombok.Getter;
import me.allen.ziggurat.Ziggurat;
import me.hulipvp.hcf.api.modules.ModuleManager;
import me.hulipvp.hcf.backend.BackendType;
import me.hulipvp.hcf.backend.HCFBackend;
import me.hulipvp.hcf.backend.backends.FlatfileBackend;
import me.hulipvp.hcf.backend.backends.MongoBackend;
import me.hulipvp.hcf.backend.backends.RedisBackend;
import me.hulipvp.hcf.backend.creds.MongoCredentials;
import me.hulipvp.hcf.backend.creds.RedisCredentials;
import me.hulipvp.hcf.backend.files.*;
import me.hulipvp.hcf.backend.files.feature.ModModeFile;
import me.hulipvp.hcf.backend.files.feature.TabFile;
import me.hulipvp.hcf.commands.CommandManager;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.RoadFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.faction.type.system.WarzoneFaction;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.PlayerLogger;
import me.hulipvp.hcf.game.player.data.mod.item.ModItems;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import me.hulipvp.hcf.hooks.plugins.aqua.AquaCore;
import me.hulipvp.hcf.hooks.plugins.vault.VaultEconomy;
import me.hulipvp.hcf.hooks.server.HoloHook;
import me.hulipvp.hcf.hooks.server.LunarHook;
import me.hulipvp.hcf.hooks.server.ProtocolHook;
import me.hulipvp.hcf.listeners.ListenerManager;
import me.hulipvp.hcf.ui.RelationHandler;
import me.hulipvp.hcf.ui.ScoreboardHandler;
import me.hulipvp.hcf.ui.TabHandler;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.reflection.AutoRespawnManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class HCF extends JavaPlugin {

    @Getter
    private static HCF instance;

    @Getter
    private PlayerHook playerHook;
    @Getter
    private LunarHook lunarHook = null;

    @Getter
    private AquaCore hexaHook;

    @Getter
    private HoloHook holoHook = null;

    @Getter
    private String bukkitVersion;

    @Getter
    private KitsFile kitsFile;
    @Getter
    private LocationsFile locationsFile;
    @Getter
    private MessagesFile messagesFile;
    @Getter
    private ReclaimFile reclaimFile;
    @Getter
    private TabFile tabFile;
    @Getter
    private ModModeFile modModeFile;
    @Getter
    private LunarFile lunarFile;


    @Getter
    private HCFBackend backend;

    @Getter
    private CommandManager commandManager;
    @Getter
    private ListenerManager listenerManager;
    @Getter
    private ModuleManager moduleManager;
    @Getter
    private AutoRespawnManager autoRespawnManager;

    public void onEnable() {
        System.out.println("------------------------------------------");
        System.out.println("[HCF] Now initializing");
        instance = this;


                bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);

        /*=============================*/
        // Saving config.
        /*=============================*/
        HCF.getInstance().saveDefaultConfig();

        ConfigValues.init();

        /*=============================*/
        // Load licensing.

        /*=============================*/

        if (!this.isEnabled()) return;

        Locale.load(this, false);

        File dataFolder = new File(HCF.getInstance().getDataFolder(), "features");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        /*=============================*/
        // Loading plugin hooks

        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServer().getServicesManager().register(Economy.class, new VaultEconomy(), this, ServicePriority.Highest);
        }

        hexaHook = new AquaCore();
        playerHook = new AquaCore();
        lunarHook = new LunarHook();


        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            new ProtocolHook();

        /*=============================*/

        /*=============================*/
        // Loading Backend
        BackendType type = BackendType.getOrDefault(getConfig().getString("backend.driver"));
        switch (type) {
            case REDIS: {
                backend = new RedisBackend(
                        new RedisCredentials(
                                getConfig().getString("backend.redis.host"),
                                getConfig().getInt("backend.redis.port"),
                                getConfig().getString("backend.redis.pass")
                        )
                );
                break;
            }
            case MONGO: {
                backend = new MongoBackend(
                        new MongoCredentials(
                                getConfig().getString("backend.mongo.host"),
                                getConfig().getInt("backend.mongo.port"),
                                getConfig().getString("backend.mongo.auth.username"),
                                getConfig().getString("backend.mongo.auth.password"),
                                getConfig().getString("backend.mongo.database"),
                                getConfig().getString("backend.mongo.auth.authDb")
                        )
                );
                break;
            }
            case FLAT_FILE:
                backend = new FlatfileBackend();
                break;
        }

        if (!backend.isLoaded()) {
            getLogger().severe("[HCF] Unable to connect to backend. Shutting down.");
            getLogger().severe("[HCF] Unable to connect to backend. Shutting down.");
            getLogger().severe("[HCF] Unable to connect to backend. Shutting down.");
            Bukkit.getServer().shutdown();
        }
        /*=============================*/

        /*=============================*/
        // Files
        this.loadConfigs();
        /*=============================*/

        /*=============================*/
        // Scoreboard & Tablist
        if (ConfigValues.FEATURES_SCOREBOARD) {
            new ScoreboardHandler();
        }
        if (TabFile.getTab().isEnabled()) {
            new Ziggurat(this, new TabHandler());
        }
        RelationHandler relationHandler = new RelationHandler();
        relationHandler.start();
        /*=============================*/

        /*=============================*/
        // AutoRespawnNMS
        if (ConfigValues.FEATURES_AUTO_RESPAWN) {
            autoRespawnManager = new AutoRespawnManager();
            autoRespawnManager.enable();
        }
        /*=============================*/

        /*=============================*/
        // Events
        listenerManager = new ListenerManager();
        listenerManager.registerListeners();
        /*=============================*/

        /*=============================*/
        // Commands
        commandManager = new CommandManager();
        commandManager.registerCommands();
        /*=============================*/

        /*=============================*/
        // Instantiating object tasks
        Conquest.instate();
        Faction.instate();
        Koth.instate();
        HCFProfile.instate();
        Mountain.instate();
        Timer.instate();
        Kit.instate();
        ModItems.instate();
        /*=============================*/

        /*=============================*/
        // Loading Data
        backend.loadConquests();
        backend.loadDTCs();
        backend.loadKoths();
        backend.loadMountains();
        backend.loadFactions();

        kitsFile.init();
        locationsFile.init();
        /*=============================*/

        /*=============================*/
        // Modules
        moduleManager = new ModuleManager();
        moduleManager.loadModules();
        /*=============================*/

        /*=============================*/
        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            try {
                holoHook = new HoloHook(this);
            } catch (Exception e) {
                //TODO
            }
        }

        /*=============================*/

        /*=============================*/
        // Check for default factions
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, this::checkFactions, 60L);
        /*=============================*/
        System.out.println("[HCF] Startup done");
        System.out.println("------------------------------------------");
    }

    public void loadConfigs() {
        kitsFile = new KitsFile();
        locationsFile = new LocationsFile();
        messagesFile = new MessagesFile();
        reclaimFile = new ReclaimFile();
        tabFile = new TabFile();
        modModeFile = new ModModeFile();
        lunarFile = new LunarFile();
    }



    public void checkFactions() {
        System.out.println("[HCF] Checking if default factions exist:");
        try {
            if (Faction.getByName("Warzone") == null) {
                System.out.println("[HCF] Creating Warzone faction....");
                WarzoneFaction warzone = new WarzoneFaction(null);
                HCF.getInstance().getBackend().createFaction(warzone);
                System.out.println("[HCF] Warzone faction created!");
            }
            if (Faction.getByName("Spawn") == null) {
                System.out.println("[HCF] Creating Spawn faction....");
                Faction spawn = new SafezoneFaction(null, "Spawn");
                HCF.getInstance().getBackend().createFaction(spawn);
                System.out.println("[HCF] Spawn faction created!");
            }
            if (Faction.getByName("Nether_Spawn") == null) {
                System.out.println("[HCF] Creating Nether_Spawn faction....");
                Faction netherSpawn = new SafezoneFaction(null, "Nether_Spawn");
                HCF.getInstance().getBackend().createFaction(netherSpawn);
                System.out.println("[HCF] Nether_Spawn faction created!");
            }
            if (Faction.getByName("North") == null) {
                System.out.println("[HCF] Creating North faction....");
                Faction northRoad = new RoadFaction(null, "North");
                HCF.getInstance().getBackend().createFaction(northRoad);
                System.out.println("[HCF] North faction created!");
            }
            if (Faction.getByName("East") == null) {
                System.out.println("[HCF] Creating East faction....");
                Faction eastRoad = new RoadFaction(null, "East");
                HCF.getInstance().getBackend().createFaction(eastRoad);
                System.out.println("[HCF] East faction created!");
            }
            if (Faction.getByName("South") == null) {
                System.out.println("[HCF] Creating South faction....");
                Faction southRoad = new RoadFaction(null, "South");
                HCF.getInstance().getBackend().createFaction(southRoad);
                System.out.println("[HCF] South faction created!");
            }
            if (Faction.getByName("West") == null) {
                System.out.println("[HCF] Creating West faction....");
                Faction westRoad = new RoadFaction(null, "West");
                HCF.getInstance().getBackend().createFaction(westRoad);
                System.out.println("[HCF] West faction created!");
            }
            System.out.println("[HCF] Successfully checked default factions.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDisable() {
        PlayerLogger.despawnVillagers();

        if (moduleManager != null)
            moduleManager.disableModules();

        Bukkit.getOnlinePlayers().forEach(player -> {
            HCFProfile profile = HCFProfile.getByPlayer(player);
            if (profile.getVanish() != null) {
                profile.getVanish().disable();
                profile.setVanish(null);
            }
        });

        if (backend != null && backend.isLoaded()) {
            HCFProfile.getProfiles().values().forEach(backend::saveProfileSync);
            Faction.getFactions().values().forEach(backend::saveFactionSync);
            Koth.getKoths().values().forEach(backend::saveKothSync);
            Conquest.getConquests().values().forEach(backend::saveConquestSync);
            DTC.getDTCs().values().forEach(backend::saveDTCSync);
            Mountain.getMountains().values().forEach(backend::saveMountainSync);
            System.out.println("[HCF] Saved all factions and profiles!");
        }

        reclaimFile.saveReclaimed();
    }




}
