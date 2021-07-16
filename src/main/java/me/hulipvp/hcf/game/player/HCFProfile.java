package me.hulipvp.hcf.game.player;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.ClaimPillar;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.game.player.data.ChatMode;
import me.hulipvp.hcf.game.player.data.ClaimData;
import me.hulipvp.hcf.game.player.data.Death;
import me.hulipvp.hcf.game.player.data.Kill;
import me.hulipvp.hcf.game.player.data.mod.Vanish;
import me.hulipvp.hcf.game.player.data.setting.HCFSetting;
import me.hulipvp.hcf.game.player.data.setting.SettingType;
import me.hulipvp.hcf.game.player.data.statistic.HCFStatistic;
import me.hulipvp.hcf.game.player.data.statistic.StatisticType;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.LocUtils;
import me.hulipvp.hcf.utils.InvUtils;
import me.hulipvp.hcf.utils.player.PlayerUtils;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HCFProfile {
    
    @Getter private static final Map<String, HCFProfile> profiles = new HashMap<>();
    @Getter private static Map<UUID, BukkitTask> wallBorderTask = new HashMap<>();

    @Getter private final UUID uuid;

    @Getter @Setter private String name;
    @Getter @Setter private String rankName;
    @Getter @Setter private int lives, balance;
    @Getter @Setter private int elo = 1000;
    @Getter @Setter private long bannedTill, lastRevive, lastSeen;
    @Getter @Setter private UUID faction;

    @Getter private List<Kill> kills;
    @Getter private List<Death> deaths;
    @Getter private List<HCFSetting> settings;
    @Getter private List<HCFStatistic> statistics;
    @Getter private List<PlayerTimer> timers;
    @Getter private List<String> notes, ignored;

    @Getter private ClaimData claimData;

    @Getter @Setter private boolean loaded, deathBanned = false, sotwPvp = false, hideStaff = false;

    @Getter @Setter private Kit currentKit;
    @Getter @Setter private Vanish vanish;
    @Getter @Setter private boolean staffModule;
    @Getter @Setter private ChatMode chatMode;
    @Getter @Setter private int streak;
    @Getter @Setter private boolean bypass, filterEnabled, messagingEnabled, messagingSounds, staffChat;
    @Getter @Setter private Location mapLocation, stuckLocation;
    @Getter @Setter private long lastReportTime, lastRequestTime, lastFactionEdit;
    @Getter @Setter private Player lastMessaged;
    @Getter @Setter private List<Material> filtered;
    @Getter @Setter private Map<Faction, List<ClaimPillar>> mapPillars;

    public HCFProfile(UUID uuid, boolean cache) {
        this.uuid = uuid;

        this.kills = new ArrayList<>();
        this.deaths = new ArrayList<>();
        this.settings = new ArrayList<>();
        this.statistics = new ArrayList<>();
        this.filtered = new ArrayList<>();
        this.timers = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.ignored = new ArrayList<>();

        this.claimData = new ClaimData();
        this.chatMode = ChatMode.PUBLIC;

        this.messagingEnabled = true;
        this.messagingSounds = true;

        HCF.getInstance().getBackend().loadProfile(this);

        if(cache)
            profiles.put(uuid.toString(), this);
    }

    public HCFProfile(UUID uuid) {
        this(uuid, true);
    }

    public HCFProfile(Map<String, Object> map) { // This should actually never be called xd
        this(UUID.fromString((String) map.get("uuid")));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void addToBalance(int amount) {
        balance += amount;
    }

    public void removeFromBalance(int amount) {
        balance -= amount;
    }

    public void addToElo(int amount) {
        elo += amount;
    }

    public void removeFromElo(int amount) {
        elo -= amount;
        if (elo < 0) elo = 0;
    }

    public PlayerFaction getFactionObj() {
        return faction == null ? null : Faction.getPlayerFaction(faction);
    }

    public boolean hasFaction() {
        return getFactionObj() != null;
    }

    public void addKill(Kill kill) {
        kills.add(kill);
    }

    public void addDeath(Death death) {
        deaths.add(death);
    }

    public void addTimer(PlayerTimer timer) {
        timers.add(timer);
    }

    public void removeTimersByType(PlayerTimerType type) {
        Iterator<PlayerTimer> it = timers.iterator();
        while(it.hasNext()) {
            PlayerTimer timer = it.next();
            if(timer == null) {
                it.remove();
                continue;
            }
            if(timer.getType() != type)
                continue;

            timer.setCancelled(true);
            timer.setCurrentTime(0L, false);

            it.remove();
        }
    }

    public PlayerTimer getTimerByType(PlayerTimerType type) {
        return timers.stream()
                .filter(timer -> timer.getType() == type)
                .findFirst()
                .orElse(null);
    }

    public void updateTimer(PlayerTimerType type, long time, boolean convert) {
        timers.stream()
                .filter(timer -> timer.getType() == type)
                .forEach(timer -> timer.setCurrentTime(time, convert));
    }

    public boolean hasTimer(PlayerTimerType type) {
        return timers.stream()
                .anyMatch(timer -> timer.getType() == type);
    }

    public HCFSetting getSetting(SettingType type) {
        HCFSetting toReturn = settings.stream()
                .filter(setting -> setting.getType() == type)
                .findFirst()
                .orElse(null);
        if(toReturn == null)
            settings.add(toReturn = new HCFSetting(type, type.getDefaultValue()));

        return toReturn;
    }

    public HCFStatistic getStatistic(StatisticType type) {
        return statistics.stream()
                .filter(statistic -> statistic.getType() == type)
                .findFirst()
                .orElse(null);
    }

    public void updateStatistic(HCFStatistic statistic) {
        HCFStatistic found = getStatistic(statistic.getType());
        if(found == null)
            return;

        found.setValue(statistic.getValue());
    }

    public String getChatPrefix() {
        Player player = getPlayer();
        String chatDisplay;
        if(PlayerUtils.hasHook())
            chatDisplay = PlayerUtils.getRankPrefix(player) + player.getName() + PlayerUtils.getRankSuffix(player);
        else
            chatDisplay = player.getName();

        return C.color(chatDisplay);
    }

    public void updateMap(Location location) {
        mapLocation = location;

        List<Faction> nearbyFactions = Claim.getNearbyFactions(location, 25);
        if(nearbyFactions.isEmpty())
            return;

        mapPillars = new HashMap<>();

        Player player = getPlayer();
        for(Faction faction : nearbyFactions) {
            if(faction.getType() == FactionType.WARZONE || faction.getType() == FactionType.ROAD)
                continue;

            if(!mapPillars.containsKey(faction)) {
                int count = 0;
                Material material;

                do {
                    material = ClaimPillar.getRandomMaterial();
                } while(++count < ClaimPillar.getMaterials().size() && !isValidMapMaterial(material));

                for(Claim claim : faction.getClaims()) {
                    List<ClaimPillar> pillars = mapPillars.getOrDefault(faction, new ArrayList<>());

                    for(Location corner : claim.getCorners())
                        pillars.add(new ClaimPillar(corner, material));

                    mapPillars.put(faction, pillars);

                    pillars.forEach(pillar -> pillar.display(player));
                }

                player.sendMessage(Locale.COMMAND_FACTION_MAP_FACTION.toString().replace("%faction%", faction.getDisplayFor(player)).replace("%material%", WordUtils.capitalizeFully(material.name().replace("_", " "))));
            }
        }
    }

    public void handleVisibility() {
        Player player = getPlayer();

        if (player != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                        handleVisibility(player, otherPlayer);
                    }
                }
            }.runTaskAsynchronously(HCF.getInstance());
        }
    }

    public void handleVisibility(Player player, Player otherPlayer) {
        if (!ConfigValues.FEATURES_HIDDEN_SPAWN_PLAYERS) return;
        if (player == null || otherPlayer == null) return;

        Faction byLocation = Faction.getByLocation(otherPlayer.getLocation());
        HCFProfile otherProfile = HCFProfile.getByPlayer(otherPlayer);
        if ((byLocation != null && byLocation.getName().equals("Spawn")) || (otherProfile.getVanish() != null && otherProfile.getVanish().isVanished())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.hidePlayer(otherPlayer);
                }
            }.runTask(HCF.getInstance());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.showPlayer(otherPlayer);
                }
            }.runTask(HCF.getInstance());
        }
    }

    public void hideMap() {
        if(!mapPillars.isEmpty())
            mapPillars.values().forEach(pillarList -> pillarList.forEach(pillar -> pillar.remove(this.getPlayer())));

        mapPillars.clear();
        mapPillars = null;
        mapLocation = null;
    }

    private boolean isValidMapMaterial(Material material) {
        if(mapPillars.isEmpty())
            return true;

        return mapPillars.values().stream()
                .anyMatch(claimPillars -> claimPillars.stream().anyMatch(claimPillar -> claimPillar.getMaterial() != material));
    }

    public Long getDeathbanTime(String rankname) {
        Player player = getPlayer();
        if(player != null && (player.isOp() || player.hasPermission("hcf.deathban.bypass")))
            return 0L;

        String group;
        if (rankname != null) {
            group = rankname;
        } else {
            if (PlayerUtils.hasHook())
                group = PlayerUtils.getRankName(player);
            else
                group = "default-time";
        }

        // TODO: Pre-load all death-ban times into a List using ConfigValues.
        if(HCF.getInstance().getConfig().contains("deathban.times." + group)) {
            return HCF.getInstance().getConfig().getLong("deathban.times." + group);
        } else {
            return HCF.getInstance().getConfig().getLong("deathban.default-time");
        }
    }

    public void reset() {
        this.kills = new ArrayList<>();
        this.deaths = new ArrayList<>();
        this.settings = new ArrayList<>();
        this.statistics = new ArrayList<>();
        this.filtered = new ArrayList<>();
        this.timers = new ArrayList<>();
        this.notes = new ArrayList<>();

        this.claimData = new ClaimData();
        this.chatMode = ChatMode.PUBLIC;

        this.lastRevive = 0L;
        this.bannedTill = 0L;
        this.deathBanned = false;
        this.balance = 0;
        this.elo = 1000;
        this.streak = 0;
        this.lives = 0;

        for(StatisticType type : StatisticType.values()) {
            if(this.getStatistic(type) != null)
                continue;

            HCFStatistic statistic = new HCFStatistic(type, 0);
            this.getStatistics().add(statistic);
        }

        for(SettingType type : SettingType.values()) {
            if(this.getSetting(type) != null)
                continue;

            this.getSettings().add(new HCFSetting(type, type.getDefaultValue()));
        }
    }

    public void save() {
        HCF.getInstance().getBackend().saveProfileSync(this);
    }

    public void saveSync() {
        HCF.getInstance().getBackend().saveProfileSync(this);
    }

    public void load(Document doc) {
        if(doc.get("name") != null)
            this.setName(doc.getString("name"));
        if(doc.get("rankName") != null)
            this.setRankName(doc.getString("rankName"));
        if(doc.get("lives") != null)
            this.setLives(doc.getInteger("lives"));
        if(doc.get("balance") != null)
            this.setBalance(doc.getInteger("balance"));
        if(doc.get("elo") != null)
            this.setElo(doc.getInteger("elo"));
        if(doc.get("bannedTill") != null)
            this.setBannedTill(((Number) doc.get("bannedTill")).longValue());
        if(doc.get("deathBanned") != null)
            this.setDeathBanned(doc.getBoolean("deathBanned"));

        if(doc.get("lastRevive") != null)
            this.setLastRevive(((Number) doc.get("lastRevive")).longValue());

        if(doc.get("faction") != null) {
            if (doc.getString("faction").equalsIgnoreCase("")) {
                this.setFaction(null);
            } else {
                UUID faction = UUID.fromString(doc.getString("faction"));
                if (Faction.getPlayerFaction(faction) != null) {
                    this.setFaction(faction);
                } else {
                    this.setFaction(null);
                }
            }
        } else {
            this.setFaction(null);
        }

        if(doc.get("notes") != null) {
            List<String> notes = (ArrayList<String>) doc.get("notes", ArrayList.class);
            if(notes != null && !notes.isEmpty())
                this.getNotes().addAll(notes);
        }

        if(doc.get("ignored") != null) {
            List<String> ignored = (ArrayList<String>) doc.get("ignored", ArrayList.class);
            if(ignored != null && !ignored.isEmpty())
                this.getIgnored().addAll(ignored);
        }

        if(doc.get("kills") != null) {
            List<Document> killList = (ArrayList<Document>) doc.get("kills", ArrayList.class);

            for(Document killDoc : killList) {
                if(killDoc != null) {
                    Kill kill = new Kill(
                            this.getUuid(),
                            UUID.fromString(killDoc.getString("killed")),
                            killDoc.getString("type")
                    );

                    this.addKill(kill);
                }
            }
        }

        if(doc.get("deaths") != null) {
            List<Document> deathList = (List<Document>) doc.get("deaths", List.class);

            for(Document deathDoc : deathList) {
                if(deathDoc != null) {
                    Death death = new Death(
                            uuid,
                            (deathDoc.getString("killer") == null || deathDoc.getString("killer").equals("") ?
                                    null : UUID.fromString(deathDoc.getString("killer"))),
                            LocUtils.stringToLocation(deathDoc.getString("location")),
                            InvUtils.invFromString(deathDoc.getString("inv"))
                    );

                    this.deaths.add(death);
                }
            }
        }

        if(doc.get("filtered") != null) {
            List<Document> filterList = (List<Document>) doc.get("filtered", List.class);

            for(Document typeDocument : filterList) {
                if(typeDocument != null) {
                    Material material = Material.valueOf(typeDocument.getString("type").toUpperCase());

                    this.getFiltered().add(material);
                }
            }
        }

        if(doc.get("statistics") != null) {
            Document statistics = (Document) doc.get("statistics");

            for(String statistic : statistics.keySet()) {
                Integer value = (Integer) statistics.get(statistic);
                HCFStatistic statisticObj = new HCFStatistic(StatisticType.getByKey(statistic), value);
                this.getStatistics().add(statisticObj);
            }
        }

        if(doc.get("settings") != null) {
            Document settings = (Document) doc.get("settings");

            for(String setting : settings.keySet()) {
                boolean value = settings.getBoolean(setting);
                HCFSetting settingObj = new HCFSetting(SettingType.getByKey(setting), value);
                this.getSettings().add(settingObj);
            }
        }

        for(StatisticType type : StatisticType.values()) {
            if(this.getStatistic(type) != null)
                continue;

            HCFStatistic statistic = new HCFStatistic(type, 0);
            this.getStatistics().add(statistic);
        }

        for(SettingType type : SettingType.values()) {
            if(this.getSetting(type) != null)
                continue;

            this.getSettings().add(new HCFSetting(type, type.getDefaultValue()));
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getInstance(), () -> {
            Player player = this.getPlayer();
            if(player != null) {
                if(doc.get("timers") != null) {
                    ArrayList<Document> timerList = (ArrayList<Document>) doc.get("timers", ArrayList.class);

                    for(Document timerDoc : timerList) {
                        if(timerDoc != null) {
                            if (System.currentTimeMillis() < ((Number) timerDoc.get("time")).longValue()) {
                                PlayerTimer timerObj = new PlayerTimer(
                                        player,
                                        PlayerTimerType.valueOf(timerDoc.getString("type")),
                                        ((Number) timerDoc.get("time")).longValue() - System.currentTimeMillis()
                                );
                                timerObj.setNewLength(TimeUnit.MILLISECONDS.toSeconds(((Number) timerDoc.get("time")).longValue() - System.currentTimeMillis()));
                                if (timerDoc.containsKey("text")) {
                                    timerObj.setText(timerDoc.getString("text"));
                                }
                                timerObj.setPaused(timerDoc.getBoolean("paused"));
                                timerObj.add();
                            }
                        }
                    }
                }
            }
        }, 1L);

        setLoaded(true);
    }

    public Document toDocument() {
        Document doc = new Document();
        BasicDBList notes = new BasicDBList();
        BasicDBList ignored = new BasicDBList();
        BasicDBList kills = new BasicDBList();
        BasicDBList deaths = new BasicDBList();
        BasicDBObject statistics = new BasicDBObject();
        BasicDBObject settings = new BasicDBObject();
        BasicDBList filtered = new BasicDBList();
        BasicDBList timers = new BasicDBList();

        notes.addAll(this.getNotes());
        ignored.addAll(this.getIgnored());

        for(Kill kill : this.getKills())
            kills.add(
                    new BasicDBObject()
                        .append("killed", ((kill.getKilled() != null) ? kill.getKilled().toString() : null))
                        .append("type", kill.getType())
            );

        for(Death death : this.getDeaths())
            deaths.add(
                    new BasicDBObject()
                        .append("killer", (death.getKiller() == null ? "" : death.getKiller().toString()))
                        .append("location", LocUtils.locationToString(death.getLocation()))
                        .append("inv", InvUtils.invToString(death.getInv()))
            );

        for(Material material : this.getFiltered())
            filtered.add(new BasicDBObject().append("type", material.name().toLowerCase()));

        this.getStatistics().forEach(statistic -> statistics.append(statistic.getType().getKey(), statistic.getValue()));

        this.getSettings().forEach(setting -> settings.append(setting.getType().getKey(), setting.isValue()));

        for(PlayerTimer timer : this.getTimers()) {
            if(!timer.getType().isSave())
                continue;
            BasicDBObject timerObj = new BasicDBObject();

            timerObj.append("time", timer.getCurrentTime());
            timerObj.append("paused", timer.isPaused());
            timerObj.append("text", timer.getText());
            timerObj.append("type", timer.getType().toString());

            timers.add(timerObj);
        }

        doc.append("uuid", this.getUuid().toString());
        doc.append("name", this.getName());
        doc.append("rankName", this.getRankName());
        doc.append("lives", this.getLives());
        doc.append("balance", this.getBalance());
        doc.append("elo", this.getElo());
        doc.append("bannedTill", this.getBannedTill());
        doc.append("deathBanned", this.isDeathBanned());
        doc.append("lastRevive", this.getLastRevive());
        if (this.getFaction() != null) {
            doc.append("faction", this.getFaction().toString());
        } else {
            doc.append("faction", null);
        }
        doc.append("notes", notes);
        doc.append("ignored", ignored);
        doc.append("kills", kills);
        doc.append("deaths", deaths);
        doc.append("statistics", statistics);
        doc.append("settings", settings);
        doc.append("filtered", filtered);
        doc.append("timers", timers);

        return doc;
    }

    public static HCFProfile getByUuid(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        return player != null ? getByPlayer(player) : new HCFProfile(uuid);
    }

    public static HCFProfile getByPlayer(Player player) {
        if(profiles.containsKey(player.getUniqueId().toString()))
            return profiles.get(player.getUniqueId().toString());

        return new HCFProfile(player.getUniqueId(), true);
    }

    public static void instate() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
            Bukkit.getLogger().info("Attempting to save all online profiles...");
            long start = System.currentTimeMillis();
            HCFProfile.getProfiles().values().forEach(HCF.getInstance().getBackend()::saveProfile);

            long end = System.currentTimeMillis();
            Bukkit.getLogger().info("Saved " + HCFProfile.getProfiles().size() + " profile(s). Took " + (end - start) + "ms.");
        }, 200L, (20L * 60L) * 5L); // Save 10 seconds after boot, then every 5 minutes.
        if (ConfigValues.FEATURES_HIDDEN_SPAWN_PLAYERS) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    HCFProfile.getByPlayer(player).handleVisibility();
                }
            }, 50L, 50L);
        }

    }
}
