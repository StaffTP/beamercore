package me.hulipvp.hcf.utils;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.item.Enchantments;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigValues {
    /*=============================*/
    // Server
    public static String LICENSE, SERVER_NAME, SERVER_PRIMARY, SERVER_SECONDARY, SERVER_WEBSITE, SERVER_TEAMSPEAK, SERVER_STORE;
    /*=============================*/

    /*=============================*/
    // Map
    public static Integer MAP_NUMBER;
    public static String MAP_START_DATE;
    /*=============================*/

    /*=============================*/
    // Features
    public static Boolean FEATURES_KITMAP, FEATURES_TABLIST, FEATURES_AUTOSMELT, FEATURES_SCOREBOARD,
            FEATURES_DEATHBANS, FEATURES_DEATH_LIGHTNING, FEATURES_ITEM_KILLS_AND_DEATHS, FEATURES_HIDDEN_SPAWN_PLAYERS,
            FEATURES_STAFF_CHAT, FEATURES_MANAGE_CHAT, FEATURES_REPORT, FEATURES_NOTES, FEATURES_REQUEST, FEATURES_FREEZE,
            FEATURES_AUTO_RESPAWN, FEATURES_MSG, FEATURES_WEATHER, FEATURES_TIME;
    /*=============================*/

    /*=============================*/
    // Welcome
    public static Integer WELCOME_STARTING_BALANCE;
    public static Boolean WELCOME_STARTING_BALANCE_MESSAGE;
    /*=============================*/

    /*=============================*/
    // Factions
    public static Integer FACTIONS_NAME_MIN, FACTIONS_NAME_MAX, FACTIONS_MEMBER_MAX, FACTIONS_INSTANT_HOME_RADIUS, FACTIONS_ALLY_MAX, FACTIONS_DTR_REGEN_DELAY, FACTIONS_DTR_REGEN_START_DELAY,
            FACTIONS_CLAIM_BUFFER, FACTIONS_CLAIM_GLASS_BUFFER, FACTIONS_CLAIM_PRICE_MULTIPLIER, FACTIONS_CLAIM_PRICE_VOLUME_DIVISOR,
            FACTIONS_SIZE_SPAWN, FACTIONS_SIZE_WARZONE, FACTIONS_SIZE_NETHER_WARZONE, FACTIONS_SIZE_ROAD_WLEFT, FACTIONS_SIZE_ROAD_WRIGHT, FACTIONS_SIZE_ROAD_OFFSET, FACTIONS_SIZE_ROAD_LENGTH,
            FACTIONS_SIZE_NETHER_SPAWN, FACTIONS_SIZE_NETHER_ROAD_WLEFT, FACTIONS_SIZE_NETHER_ROAD_WRIGHT, FACTIONS_SIZE_NETHER_ROAD_OFFSET, FACTIONS_SIZE_NETHER_ROAD_LENGTH, FACTIONS_SIZE_WARZONE_BUILD_LIMIT,
            FACTIONS_SIZE_NETHER_WARZONE_BUILD_LIMIT, FACTIONS_WALL_COLOR, FACTIONS_POINTS_STEAL_PERCENTAGE,
            FACTIONS_POINTS_WORLD_KILL_POINTS, FACTIONS_POINTS_WORLD_KOTH_POINTS, FACTIONS_POINTS_WORLD_CONQUEST_POINTS, FACTIONS_POINTS_WORLD_CITADEL_POINTS,
            FACTIONS_POINTS_NETHER_KILL_POINTS, FACTIONS_POINTS_NETHER_KOTH_POINTS,
            FACTIONS_POINTS_END_KILL_POINTS, FACTIONS_POINTS_END_KOTH_POINTS;
    public static Boolean FACTIONS_ALLY_ATTACKING_PREVENT, FACTIONS_HOME_DISABLE_IN_END;
    public static Double FACTIONS_DTR_DEATH, FACTIONS_DTR_MAX, FACTIONS_DTR_MULTIPLE, FACTIONS_DTR_REGEN_INCREMENT, FACTIONS_ALLY_ATTACKING_DAMAGE_MULTIPLIER, FACTIONS_CLAIM_PRICE_SELLING_MULTIPLIER, FACTIONS_CLAIM_PRICE_PER_BLOCK;
    public static List<String> FACTIONS_NAME_BLOCKED, FACTIONS_SAFEZONE_BLOCKED_CMDS;
    public static String FACTIONS_COLORS_MEMBERS, FACTIONS_COLORS_ALLIES, FACTIONS_COLORS_TAGGED, FACTIONS_COLORS_ENEMIES, FACTIONS_COLORS_FOCUSED;
    /*=============================*/

    /*=============================*/
    // Koth
    public static Integer KOTH_TIME;
    public static List<String> KOTH_ON_CAPTURE;
    public static Map<String, Integer> KOTH_SPECIAL_TIMES;
    /*=============================*/

    /*=============================*/
    // Conquest
    public static Integer CONQUEST_TIME, CONQUEST_POINTS_PER_CAP, CONQUEST_POINTS_LOSS_PER_DEATH, CONQUEST_MAX_POINTS;
    public static List<String> CONQUEST_ON_CAPTURE;
    /*=============================*/

    /*=============================*/
    // DTC
    public static Integer DTC_POINTS_PER_CORE_BREAK, DTC_POINTS_LOSS_PER_DEATH, DTC_POINTS_NEEDED_TO_WIN;
    public static String DTC_MATERIAL;
    public static List<String> DTC_ON_WIN;
    /*=============================*/

    /*=============================*/
    // DTC
    public static List<String> RAMPAGE_ON_WIN;
    /*=============================*/

    /*=============================*/
    // Scoreboard
    public static String SCOREBOARD_TITLE, SCOREBOARD_KOTH_NORMAL, SCOREBOARD_KOTH_SPECIAL,
    SCOREBOARD_CONQUEST_HEADER, SCOREBOARD_CONQUEST_NO_SCORES, SCOREBOARD_CONQUEST_SCORE,
    SCOREBOARD_DTC_HEADER, SCOREBOARD_DTC_NO_SCORES, SCOREBOARD_DTC_SCORE, SCOREBOARD_CUSTOMTIMER_LINE, SCOREBOARD_CUSTOMSERVERTIMER_LINE;
    public static List<String> SCOREBOARD_LINES, SCOREBOARD_MOD_LINES, SCOREBOARD_RAMPAGE_LINES, SCOREBOARD_EXTRACTION_LINES;

    /*=============================*/

    /*=============================*/
    // Listeners
    public static Integer LISTENERS_COOK_SPEED_MULTIPLIER;
    public static Integer LISTENERS_BREWING_SPEED_MULTIPLIER;
    public static Boolean LISTENERS_STARTING_TIMER, LISTENERS_PLACE_IN_COMBAT, LISTENERS_DISABLE_ENDERCHEST;
    public static Double LISTENERS_EXPERIENCE_GLOBAL_MULTIPLIER, LISTENERS_EXPERIENCE_LOOTING_MULTIPLIER,
            LISTENERS_EXPERIENCE_FORTUNE_MULTIPLIER, LISTENERS_EXPERIENCE_FISHING_GLOBAL_MULTIPLIER, LISTENERS_EXPERIENCE_FISHING_LUCK_MULTIPLIER, LISTENERS_EXPERIENCE_FURNACE_MULTIPLIER;
    /*=============================*/

    /*=============================*/
    // Combat
    public static boolean COMBAT_ALLOW_END_PORTAL_ENTER, COMBAT_VILLAGER_KNOCKBACK;
    public static String COMBAT_COMMAND_LIST_TYPE;
    public static List<String> COMBAT_COMMAND_LIST;
    /*=============================*/

    /*=============================*/
    // ELO
    public static boolean ELO_ENABLED, ELO_KOTH_ENABLED, ELO_KOTH_CAPPER_ONLY, ELO_CONQUEST_ENABLED, ELO_CONQUEST_CAPPER_ONLY, ELO_DTC_ENABLED, ELO_DTC_CAPPER_ONLY;
    public static int ELO_POINTS_ON_KILL, ELO_POINTS_ON_DEATH, ELO_KOTH_POINTS, ELO_CONQUEST_POINTS, ELO_DTC_POINTS;
    /*=============================*/

    /*=============================*/
    // Elevators
    public static Boolean ELEVATORS_SIGN, ELEVATORS_MINECART;
    /*=============================*/

    /*=============================*/
    // Kits
    public static Boolean KITS_ARCHER_ENABLE, KITS_BARD_ENABLE, KITS_MINER_ENABLE, KITS_ROGUE_ENABLE, KITS_HOLD_EFFECTS_IN_SPAWN, KITS_CLICK_EFFECTS_IN_SPAWN, KITS_BARD_EFFECT_CLICK_COMBAT;
    public static Integer KITS_BARD_EFFECT_RANGE, KITS_BARD_MAX_ENERGY, KITS_MINER_INVIS_LEVEL, KITS_ROGUE_COOLDOWN, KITS_ROGUE_BACKSTAB;
    /*=============================*/

    /*=============================*/
    // Mountains
    public static Integer MOUNTIAN_RESET_TIME;
    /*=============================*/

    /*=============================*/
    // Limiters
    public static Integer LIMITERS_WORLD_BORDER, LIMITERS_NETHER_BORDER, LIMITERS_END_BORDER, LIMITERS_BEACON_STRENGTH, LIMITERS_ENTITY_PER_CHUNK, LIMITERS_LFF_COOLDOWN;
    public static HashMap<Enchantment, Integer> LIMITERS_ENCHANTS = new HashMap<>();
    public static HashMap<PotionType, String> LIMITERS_POTIONS = new HashMap<>();
    public static ArrayList<Integer> LIMITERS_BLOCKED_POTIONS = new ArrayList<>();
    /*=============================*/

    /*=============================*/
    // Pearls
    public static Boolean PEARLS_CLOSE_PEARL_CHECK, PEARL_SHIFT_FENCE_GATE_PEARL_CHECK;
    /*=============================*/


    /*=============================*/
    // Kitmap
    public static String KITMAP_MONEY_PER_KILL;
    public static HashMap<Integer, String> KITMAP_STREAK_NAMES = new HashMap<>();
    public static HashMap<Integer, List<String>> KITMAP_STREAK_REWARDS = new HashMap<>();
    /*=============================*/

    /*=============================*/
    // Settings GUI
    public static String SETTINGS_GUI_TITLE, SETTINGS_GUI_ITEM_TITLE, SETTINGS_GUI_PREFIX_ACTIVE, SETTINGS_GUI_PREFIX_NOT_ACTIVE;
    public static List<String> SETTINGS_GUI_LORE;
    /*=============================*/

    /*=============================*/
    // Freeze
    public static Integer FREEZE_MESSAGE_REPEAT;
    public static Boolean FREEZE_GUI_ENABLED;
    public static String FREEZE_GUI_ITEM_NAME;
    public static List<String> FREEZE_ALLOWED_COMMANDS, FREEZE_GUI_ITEM_LINES;
    /*=============================*/

    /*=============================*/
    // Rank Revive
    public static Map<String, String> REVIVE_PERMISSIONS, REVIVE_BROADCASTS;
    public static Map<String, Integer> REVIVE_COOLDOWNS;
    /*=============================*/

    /*=============================*/
    // Rank Broadcast
    public static boolean RANK_BROADCAST_ENABLED;
    public static Integer RANK_BROADCAST_TIME;
    public static String RANK_BROADCAST_NAME, RANK_BROADCAST_BROADCAST, RANK_BROADCAST_NO_PLAYERS,
            RANK_BROADCAST_JOINER_COLOR, RANK_BROADCAST_JOINER_CHARACTER;
    /*=============================*/

    public static void init() {
        HCF.getInstance().reloadConfig();

        FileConfiguration config = HCF.getInstance().getConfig();
        config.options().copyDefaults(true);

        if (!config.contains("rampage.on-win")) {
            config.set("rampage.on-win", Arrays.asList("say %player% has just won rampage!"));
        }
        if (!config.contains("board.rampage")) {
            config.set("board.rampage", Arrays.asList("&4&lRampage &7(%rampage-remaining%)", " &fKills: &c%rampage-kills%", " &fTop Kills: &c%rampage-top-kills%"));
        }
        if (!config.contains("board.extraction")) {
            config.set("board.extraction", Arrays.asList("&3&lExtraction", " &fTime: &b%extraction-remaining%"));
        }
        if (!config.contains("dtc.material")) {
            config.set("dtc.material", "ENDER_STONE");
        }

        try {
            config.save(new File(HCF.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        LICENSE = config.getString("license");
        SERVER_NAME = config.getString("server.name");
        SERVER_PRIMARY = config.getString("server.color.primary");
        SERVER_SECONDARY = config.getString("server.color.secondary");
        SERVER_WEBSITE = config.getString("server.website").replace("%servername%", SERVER_NAME.toLowerCase());
        SERVER_TEAMSPEAK = config.getString("server.teamspeak").replace("%servername%", SERVER_NAME.toLowerCase());
        SERVER_STORE = config.getString("server.store").replace("%servername%", SERVER_NAME.toLowerCase());

        MAP_NUMBER = config.getInt("map.number");
        MAP_START_DATE = config.getString("map.start-date");

        FEATURES_KITMAP = config.getBoolean("features.kitmap");
        FEATURES_TABLIST = config.getBoolean("features.tablist");
        FEATURES_SCOREBOARD = config.getBoolean("features.scoreboard");
        FEATURES_DEATHBANS = config.getBoolean("features.deathbans");
        FEATURES_AUTOSMELT = config.getBoolean("features.autosmelt");
        FEATURES_DEATH_LIGHTNING = config.getBoolean("features.death-lightning");
        FEATURES_ITEM_KILLS_AND_DEATHS = config.getBoolean("features.item-kills-and-deaths");
        FEATURES_HIDDEN_SPAWN_PLAYERS = config.getBoolean("features.hidden-spawn-players");
        FEATURES_STAFF_CHAT = config.getBoolean("features.staff-chat", true);
        FEATURES_MANAGE_CHAT = config.getBoolean("features.manage-chat", true);
        FEATURES_REPORT = config.getBoolean("features.report", true);
        FEATURES_NOTES = config.getBoolean("features.notes", true);
        FEATURES_REQUEST = config.getBoolean("features.request", true);
        FEATURES_FREEZE = config.getBoolean("features.freeze", true);
        FEATURES_AUTO_RESPAWN = config.getBoolean("features.auto-respawn", true);
        FEATURES_MSG = config.getBoolean("features.msg", true);
        FEATURES_TIME = config.getBoolean("features.time", false);
        FEATURES_WEATHER = config.getBoolean("features.weather", false);

        WELCOME_STARTING_BALANCE = config.getInt("welcome.starting-balance", 250);
        WELCOME_STARTING_BALANCE_MESSAGE = config.getBoolean("welcome.starting-balance-message");

        FACTIONS_NAME_MIN = config.getInt("factions.name.min");
        FACTIONS_NAME_MAX = config.getInt("factions.name.max");
        FACTIONS_MEMBER_MAX = config.getInt("factions.members.max", 30);
        FACTIONS_INSTANT_HOME_RADIUS = config.getInt("factions.members.instant-home-radius", 25);
        FACTIONS_ALLY_MAX = config.getInt("factions.allies.max");
        FACTIONS_CLAIM_BUFFER = config.getInt("factions.claim.buffer");
        FACTIONS_CLAIM_GLASS_BUFFER = config.getInt("factions.claim.glass-buffer");
        FACTIONS_CLAIM_PRICE_MULTIPLIER = config.getInt("factions.claim.price.multiplier");
        FACTIONS_CLAIM_PRICE_VOLUME_DIVISOR = config.getInt("factions.claim.price.volume-divisor");

        FACTIONS_COLORS_MEMBERS = config.getString("factions.colors.members", "GREEN");
        FACTIONS_COLORS_ALLIES = config.getString("factions.colors.allies", "DARK_PURPLE");
        FACTIONS_COLORS_TAGGED = config.getString("factions.colors.tagged", "RED");
        FACTIONS_COLORS_ENEMIES = config.getString("factions.colors.enemies", "YELLOW");
        FACTIONS_COLORS_FOCUSED = config.getString("factions.colors.focused", "LIGHT_PURPLE");

        FACTIONS_WALL_COLOR = config.getInt("factions.claim.glass-color", 14);

        FACTIONS_SIZE_SPAWN = config.getInt("factions.sizes.worlds.default.spawn");
        FACTIONS_SIZE_WARZONE = config.getInt("factions.sizes.worlds.default.warzone");
        FACTIONS_SIZE_ROAD_WLEFT = config.getInt("factions.sizes.worlds.default.roads.width-left");
        FACTIONS_SIZE_ROAD_WRIGHT = config.getInt("factions.sizes.worlds.default.roads.width-right");
        FACTIONS_SIZE_ROAD_OFFSET = FACTIONS_SIZE_SPAWN + 1;
        FACTIONS_SIZE_ROAD_LENGTH = config.getInt("factions.sizes.worlds.default.roads.length");
        FACTIONS_SIZE_WARZONE_BUILD_LIMIT = config.getInt("factions.sizes.worlds.default.warzone-build-limit");

        FACTIONS_SIZE_NETHER_SPAWN = config.getInt("factions.sizes.worlds.nether.spawn");
        FACTIONS_SIZE_NETHER_WARZONE = config.getInt("factions.sizes.worlds.nether.warzone");
        FACTIONS_SIZE_NETHER_ROAD_WLEFT = config.getInt("factions.sizes.worlds.nether.roads.width-left");
        FACTIONS_SIZE_NETHER_ROAD_WRIGHT = config.getInt("factions.sizes.worlds.nether.roads.width-right");
        FACTIONS_SIZE_NETHER_ROAD_OFFSET = FACTIONS_SIZE_NETHER_SPAWN + 1;
        FACTIONS_SIZE_NETHER_ROAD_LENGTH = config.getInt("factions.sizes.worlds.nether.roads.length");
        FACTIONS_SIZE_NETHER_WARZONE_BUILD_LIMIT = config.getInt("factions.sizes.worlds.nether.warzone-build-limit");

        FACTIONS_POINTS_STEAL_PERCENTAGE = config.getInt("factions.points.steal-on-faction-raidable");
        FACTIONS_POINTS_WORLD_KILL_POINTS = config.getInt("factions.points.worlds.world.kill-points");
        FACTIONS_POINTS_WORLD_KOTH_POINTS = config.getInt("factions.points.worlds.world.koth-points");
        FACTIONS_POINTS_WORLD_CONQUEST_POINTS = config.getInt("factions.points.worlds.world.conquest-points");
        FACTIONS_POINTS_WORLD_CITADEL_POINTS = config.getInt("factions.points.worlds.world.citadel-points");
        FACTIONS_POINTS_NETHER_KILL_POINTS = config.getInt("factions.points.worlds.nether.kill-points");
        FACTIONS_POINTS_NETHER_KOTH_POINTS = config.getInt("factions.points.worlds.nether.koth-points");
        FACTIONS_POINTS_END_KILL_POINTS = config.getInt("factions.points.worlds.end.kill-points");
        FACTIONS_POINTS_END_KOTH_POINTS = config.getInt("factions.points.worlds.end.koth-points");

        FACTIONS_HOME_DISABLE_IN_END = config.getBoolean("factions.home.disable-in-end", false);

        FACTIONS_ALLY_ATTACKING_PREVENT = config.getBoolean("factions.allies.attacking.prevent");
        FACTIONS_DTR_DEATH = config.getDouble("factions.dtr.death");
        FACTIONS_DTR_MAX = config.getDouble("factions.dtr.max");
        FACTIONS_DTR_MULTIPLE = config.getDouble("factions.dtr.multiple");
        FACTIONS_DTR_REGEN_DELAY = config.getInt("factions.dtr.regen.delay");
        FACTIONS_DTR_REGEN_START_DELAY = config.getInt("factions.dtr.regen.start-delay", 60);
        FACTIONS_DTR_REGEN_INCREMENT = config.getDouble("factions.dtr.regen.increment");
        FACTIONS_ALLY_ATTACKING_DAMAGE_MULTIPLIER = config.getDouble("factions.allies.attacking.damage-multiplier");
        FACTIONS_CLAIM_PRICE_SELLING_MULTIPLIER = config.getDouble("factions.claim.price.selling-multiplier");
        FACTIONS_CLAIM_PRICE_PER_BLOCK = config.getDouble("factions.claim.price.per-block");
        FACTIONS_NAME_BLOCKED = config.getStringList("factions.name.blocked");
        FACTIONS_SAFEZONE_BLOCKED_CMDS = config.getStringList("factions.safezone.outside-safezone-blocked-cmds");

        KOTH_TIME = config.getInt("koth.time");
        KOTH_ON_CAPTURE = config.getStringList("koth.on-capture");
        KOTH_SPECIAL_TIMES = new HashMap<>();
        if(config.contains("koth.special-times")) {
            for(String key : config.getConfigurationSection("koth.special-times").getKeys(false)) {
                int time = config.getInt("koth.special-times." + key, 30);

                KOTH_SPECIAL_TIMES.put(key.toLowerCase(), time);
            }
        }

        CONQUEST_TIME = config.getInt("conquest.time");
        CONQUEST_POINTS_PER_CAP = config.getInt("conquest.points-per-cap");
        CONQUEST_POINTS_LOSS_PER_DEATH = config.getInt("conquest.points-loss-per-death");
        CONQUEST_MAX_POINTS = config.getInt("conquest.max-points");
        CONQUEST_ON_CAPTURE = config.getStringList("conquest.on-capture");

        DTC_POINTS_PER_CORE_BREAK = config.getInt("dtc.points-per-core-break");
        DTC_POINTS_LOSS_PER_DEATH = config.getInt("dtc.points-loss-per-death");
        DTC_POINTS_NEEDED_TO_WIN = config.getInt("dtc.points-needed-to-win");
        DTC_MATERIAL = config.getString("dtc.material", "ENDER_STONE");
        DTC_ON_WIN = config.getStringList("dtc.on-win");

        RAMPAGE_ON_WIN = config.getStringList("rampage.on-win");

        SCOREBOARD_TITLE = config.getString("board.title")
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                .replace("%servername%", ConfigValues.SERVER_NAME)
                .replace("%servernamelower%", ConfigValues.SERVER_NAME.toLowerCase())
                .replace("%website%", ConfigValues.SERVER_WEBSITE)
                .replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK)
                .replace("%store%", ConfigValues.SERVER_STORE);
        SCOREBOARD_LINES = config.getStringList("board.lines");
        SCOREBOARD_MOD_LINES = config.getStringList("board.mod-mode");
        SCOREBOARD_RAMPAGE_LINES = config.getStringList("board.rampage");
        SCOREBOARD_EXTRACTION_LINES = config.getStringList("board.extraction");
        SCOREBOARD_KOTH_NORMAL = config.getString("board.koth.normal");
        SCOREBOARD_KOTH_SPECIAL = config.getString("board.koth.special");
        SCOREBOARD_CONQUEST_HEADER = config.getString("board.conquest.header");
        SCOREBOARD_CONQUEST_NO_SCORES = config.getString("board.conquest.no-scores");
        SCOREBOARD_CONQUEST_SCORE = config.getString("board.conquest.score");
        SCOREBOARD_DTC_HEADER = config.getString("board.dtc.header");
        SCOREBOARD_DTC_NO_SCORES = config.getString("board.dtc.no-scores");
        SCOREBOARD_DTC_SCORE = config.getString("board.dtc.score");
        SCOREBOARD_CUSTOMTIMER_LINE = config.getString("board.custom-timer.line", "Config needed");
        SCOREBOARD_CUSTOMSERVERTIMER_LINE = config.getString("board.custom-server-timer.line", "Config needed");

        LISTENERS_COOK_SPEED_MULTIPLIER = config.getInt("listeners.cook-speed-multiplier", 1);
        LISTENERS_BREWING_SPEED_MULTIPLIER = config.getInt("listeners.brewing-speed-multiplier");
        LISTENERS_STARTING_TIMER = config.getBoolean("listeners.starting-timer", true);
        LISTENERS_PLACE_IN_COMBAT = config.getBoolean("listeners.place-in-combat", true);
        LISTENERS_DISABLE_ENDERCHEST = config.getBoolean("listeners.disable-enderchest", true);
        LISTENERS_EXPERIENCE_GLOBAL_MULTIPLIER = config.getDouble("listeners.experience.global-multiplier");
        LISTENERS_EXPERIENCE_LOOTING_MULTIPLIER = config.getDouble("listeners.experience.looting-multiplier");
        LISTENERS_EXPERIENCE_FORTUNE_MULTIPLIER = config.getDouble("listeners.experience.fortune-multiplier");
        LISTENERS_EXPERIENCE_FISHING_GLOBAL_MULTIPLIER = config.getDouble("listeners.experience.luck-multiplier");
        LISTENERS_EXPERIENCE_FISHING_LUCK_MULTIPLIER = config.getDouble("listeners.experience.luck-multiplier");
        LISTENERS_EXPERIENCE_FURNACE_MULTIPLIER = config.getDouble("listeners.experience.furnace-multiplier");

        COMBAT_ALLOW_END_PORTAL_ENTER = config.getBoolean("combat.allow-end-portal-enter", false);
        COMBAT_COMMAND_LIST_TYPE = config.getString("combat.command-list-type", "NONE");
        COMBAT_COMMAND_LIST = config.getStringList("combat.command-list");
        COMBAT_VILLAGER_KNOCKBACK = config.getBoolean("combat.villager.knockback", false);

        ELO_ENABLED = config.getBoolean("elo.enable");
        ELO_POINTS_ON_KILL = config.getInt("elo.points-on-kill");
        ELO_POINTS_ON_DEATH = config.getInt("elo.points-on-death");
        ELO_KOTH_ENABLED = config.getBoolean("elo.koth.enable");
        ELO_KOTH_POINTS = config.getInt("elo.koth.points-on-win");
        ELO_KOTH_CAPPER_ONLY = config.getBoolean("elo.koth.points-for-capper-only");
        ELO_CONQUEST_ENABLED = config.getBoolean("elo.conquest.enable");
        ELO_CONQUEST_POINTS = config.getInt("elo.conquest.points-on-win");
        ELO_CONQUEST_CAPPER_ONLY = config.getBoolean("elo.conquest.points-for-capper-only");
        ELO_DTC_ENABLED = config.getBoolean("elo.dtc.enable");
        ELO_DTC_POINTS = config.getInt("elo.dtc.points-on-win");
        ELO_DTC_CAPPER_ONLY = config.getBoolean("elo.dtc.points-for-capper-only");

        ELEVATORS_SIGN = config.getBoolean("elevators.sign");
        ELEVATORS_MINECART = config.getBoolean("elevators.minecart");

        KITS_ARCHER_ENABLE = config.getBoolean("kits.archer.enable");
        KITS_BARD_ENABLE = config.getBoolean("kits.bard.enable");
        KITS_MINER_ENABLE = config.getBoolean("kits.miner.enable");
        KITS_ROGUE_ENABLE = config.getBoolean("kits.rogue.enable");
        KITS_BARD_EFFECT_CLICK_COMBAT = config.getBoolean("kits.bard.bard-effects-click-combat");
        KITS_HOLD_EFFECTS_IN_SPAWN = config.getBoolean("kits.bard.hold-effects-in-spawn");
        KITS_CLICK_EFFECTS_IN_SPAWN = config.getBoolean("kits.bard.click-effects-in-spawn");
        KITS_BARD_EFFECT_RANGE = config.getInt("kits.bard.effect-range");
        KITS_BARD_MAX_ENERGY = config.getInt("kits.bard.max-energy");
        KITS_MINER_INVIS_LEVEL = config.getInt("kits.miner.invis-level");
        KITS_ROGUE_BACKSTAB = config.getInt("kits.rogue.backstab", 100);
        KITS_ROGUE_COOLDOWN = config.getInt("kits.rogue.cooldown", 0);

        MOUNTIAN_RESET_TIME = config.getInt("mountain.reset-time");

        LIMITERS_WORLD_BORDER = config.getInt("limiters.world-border");
        LIMITERS_NETHER_BORDER = config.getInt("limiters.nether-border", 2000);
        LIMITERS_END_BORDER = config.getInt("limiters.end-border", 0);
        LIMITERS_BEACON_STRENGTH = config.getInt("limiters.beacon-strength");
        LIMITERS_ENTITY_PER_CHUNK = config.getInt("limiters.entity.per-chunk");
        LIMITERS_LFF_COOLDOWN = config.getInt("timers.lff", 300);

        for(String enchant : config.getStringList("limiters.enchants")) {
            String[] enchantArr = enchant.split(";");
            Enchantment enchantment = Enchantments.getByName(enchantArr[0]);
            int level = Integer.parseInt(enchantArr[1]);
            if(enchantment == null) { // Just in case someone makes a typo -- like I did for testing :P
                HCF.getInstance().getLogger().config("Invalid Enchantment: " + enchantArr[0]);
                continue;
            }

            LIMITERS_ENCHANTS.put(enchantment, level);
        }

        for(String potion : config.getStringList("limiters.potions")) {
            String[] potionArr = potion.split(";");
            PotionType type = PotionType.getByEffect(PotionEffectType.getByName(potionArr[0]));
            if(type == null) {
                HCF.getInstance().getLogger().config("Invalid PotionType: " + potionArr[0]);
                continue;
            }

            LIMITERS_POTIONS.put(type, potionArr[1] + ";" + potionArr[2]);
        }

        PEARLS_CLOSE_PEARL_CHECK = config.getBoolean("pearls.close-pearl-check");
        PEARL_SHIFT_FENCE_GATE_PEARL_CHECK = config.getBoolean("pearls.shift-fence-gate-pearl-check");

        KITMAP_MONEY_PER_KILL = config.getString("kitmap.money-per-kill");

        for(String key : config.getConfigurationSection("kitmap.rewards").getKeys(false)) {
            int kills = config.getInt("kitmap.rewards." + key + ".kills");
            String name = config.getString("kitmap.rewards." + key + ".name");
            List<String> command = config.getStringList("kitmap.rewards." + key + ".commands");

            KITMAP_STREAK_NAMES.put(kills, name);
            KITMAP_STREAK_REWARDS.put(kills, command);
        }

        LIMITERS_BLOCKED_POTIONS.addAll(config.getIntegerList("limiters.blocked-potions"));

        SETTINGS_GUI_TITLE = config.getString("settings-gui.title");
        SETTINGS_GUI_ITEM_TITLE = config.getString("settings-gui.item-title");
        SETTINGS_GUI_PREFIX_ACTIVE = config.getString("settings-gui.prefix.active")
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY);
        SETTINGS_GUI_PREFIX_NOT_ACTIVE = config.getString("settings-gui.prefix.not-active")
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY);
        SETTINGS_GUI_LORE = config.getStringList("settings-gui.lore");

        FREEZE_GUI_ENABLED = false;
        FREEZE_GUI_ITEM_NAME = config.getString("freeze.gui.item.name", "%teamspeak%");
        if(config.contains("freeze.gui.item.lines"))
            FREEZE_GUI_ITEM_LINES = config.getStringList("freeze.gui.item.lines");
        else
            FREEZE_GUI_ITEM_LINES = Arrays.asList("DO NOT LOG OUT YOU HAVE 5 MINUTES", "https://www.teamspeak.com/downloads");

        FREEZE_MESSAGE_REPEAT = config.getInt("freeze.message-repeat", 10);

        if(config.contains("freeze.allowed-commands"))
            FREEZE_ALLOWED_COMMANDS = config.getStringList("freeze.allowed-commands");
        else
            FREEZE_ALLOWED_COMMANDS = Arrays.asList("/m", "/r", "/msg", "/reply", "/ts");

        REVIVE_COOLDOWNS = new HashMap<>();
        REVIVE_PERMISSIONS = new HashMap<>();
        REVIVE_BROADCASTS = new HashMap<>();
        if(config.contains("revive")) {
            for(String rank : config.getConfigurationSection("revive.ranks").getKeys(false)) {
                if(config.contains("revive.ranks." + rank + ".permission"))
                    REVIVE_PERMISSIONS.put(rank.toLowerCase(), config.getString("revive.ranks." + rank + ".permission"));

                if(config.contains("revive.ranks." + rank + ".cooldown"))
                    REVIVE_COOLDOWNS.put(rank.toLowerCase(), config.getInt("revive.ranks." + rank + ".cooldown"));

                if(config.contains("revive.ranks." + rank + ".broadcast"))
                    REVIVE_BROADCASTS.put(rank.toLowerCase(), config.getString("revive.ranks." + rank + ".broadcast"));
            }
        }

        if(config.contains("rank-broadcast")) {
            RANK_BROADCAST_ENABLED = config.getBoolean("rank-broadcast.enabled", false);
            RANK_BROADCAST_BROADCAST = config.getString("rank-broadcast.messages.broadcast", "");
            RANK_BROADCAST_NO_PLAYERS = config.getString("rank-broadcast.messages.no-players", "");
            RANK_BROADCAST_TIME = config.getInt("rank-broadcast.time", 0);
            if(config.contains("rank-broadcast.rank"))
                RANK_BROADCAST_NAME = config.getString("rank-broadcast.rank", "");
            else
                RANK_BROADCAST_NAME = config.getString("rank-broadcast.name", "");
            RANK_BROADCAST_JOINER_COLOR = config.getString("rank-broadcast.messages.joiner.color", "");
            RANK_BROADCAST_JOINER_CHARACTER = config.getString("rank-broadcast.messages.joiner.character", "");
        } else {
            RANK_BROADCAST_ENABLED = false;
            RANK_BROADCAST_BROADCAST = "";
            RANK_BROADCAST_TIME = 0;
            RANK_BROADCAST_NAME = "";
            RANK_BROADCAST_NO_PLAYERS = "";
            RANK_BROADCAST_JOINER_COLOR = "";
            RANK_BROADCAST_JOINER_CHARACTER = "";
        }
    }
}
