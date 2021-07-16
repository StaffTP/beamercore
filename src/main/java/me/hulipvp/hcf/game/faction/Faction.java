package me.hulipvp.hcf.game.faction;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.LocUtils;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Faction {
    
    @Getter private static Map<String, Faction> factions = new HashMap<>();

    // Idea of storing claims inspired by iHCF
    @Getter private static final Table<String, Long, Claim> claimPositions = HashBasedTable.create();

    @Getter private final UUID uuid;
    @Getter private final FactionType type;

    @Getter private Set<Claim> claims;

    @Getter @Setter public String name;
    @Getter @Setter private boolean deathban;
    @Getter @Setter private Location home;
    @Getter @Setter private long lastRename;

    public Faction(UUID uuid, String name, boolean deathban, FactionType type) {
        UUID finalUuid = UUID.randomUUID();
        if(uuid == null)
            while(getFaction(finalUuid) != null)
                finalUuid = UUID.randomUUID();
        else
            finalUuid = uuid;

        this.uuid = finalUuid;
        this.name = name;
        this.deathban = deathban;
        this.type = type;
        this.claims = new HashSet<>();

        factions.put(this.uuid.toString(), this);
    }

    public String getHomeString() {
        return (this.getHome() == null) ? null : LocUtils.locationToFomattedString(this.getHome());
    }

    public String getName() {
        return name.replace("_", " ");
    }

    public String getDisplayString() {
        return C.color("&c" + this.getName() + " " + ((this.isDeathban()) ? Locale.FACTION_DEATHBAN.toString() : Locale.FACTION_NONDEATHBAN.toString()));
    }

    public String getDisplayFor(Player player) {
        if(this instanceof PlayerFaction) {
            PlayerFaction pf = (PlayerFaction) this;
            if(pf.getMembers().containsKey(player.getUniqueId()))
                return ChatColor.GREEN + getName();

            return ChatColor.RED + getName();
        } else if(this instanceof SystemFaction) {
            SystemFaction sf = (SystemFaction) this;

            return sf.getColoredName();
        } else {
            return ChatColor.RED + getName();
        }
    }

    public void addClaim(Claim claim) {
        claim.setFaction(this);
        claims.add(claim);

        saveClaim(claim);
    }

    public void removeClaim(Claim claim) {
        claims.remove(claim);
        claim.setFaction(null);

        deleteClaim(claim);
    }

    public void removeClaims() {
        claims.forEach(Faction::deleteClaim);
        claims.forEach(claim -> claim.setFaction(null));
        claims.clear();
    }

    public void save() {
        HCF.getInstance().getBackend().saveFaction(this);
    }

    public Document toDocument() {
        BasicDBList claims = new BasicDBList();
        this.claims.forEach(claim -> {
            claims.add(new BasicDBObject().append("claim", claim.toString()));
        });
        Document document = new Document()
                .append("uuid", this.getUuid().toString())
                .append("name", this.name)
                .append("deathban", this.isDeathban())
                .append("type", this.getType().toString())
                .append("claims", claims);
        if (this.home != null) {
            document.append("home", LocUtils.locationToString(this.getHome()));
        }

        return document;
    }

    /*=============================*/
    // Static Methods

    public static boolean isFaction(UUID uuid) {
        return factions.containsKey(uuid.toString());
    }

    public static boolean isNotAFaction(UUID uuid) {
        return !isFaction(uuid);
    }

    public static Faction getFaction(UUID uuid) {
        return factions.get(uuid.toString());
    }

    public static PlayerFaction getPlayerFaction(UUID uuid) {
        return (PlayerFaction) factions.get(uuid.toString());
    }

    public static boolean isInsideFactionTerritory(Faction faction, Location location) {
        return !faction.getClaims().isEmpty() && faction.isInsideClaim(location);
    }

    public boolean isInsideClaim(Location location) {
        return claims.stream()
                .anyMatch(claim -> claim.toCuboid().isInCuboid(location));
    }

    public static void saveClaim(Claim claim) {
        if(claimPositions.containsValue(claim))
            return;

        Cuboid cuboid = claim.toCuboid();
        for(int x = cuboid.getLowerX(); x <= cuboid.getUpperX(); x++) {
            for(int z = cuboid.getLowerZ(); z <= cuboid.getUpperZ(); z++)
                claimPositions.put(cuboid.getWorld().getName(), LongHash.toLong(x, z), claim);
        }
    }

    public static void deleteClaim(Claim claim) {
        Cuboid cuboid = claim.toCuboid();
        for(int x = cuboid.getLowerX(); x <= cuboid.getUpperX(); x++) {
            for(int z = cuboid.getLowerZ(); z <= cuboid.getUpperZ(); z++)
                claimPositions.remove(cuboid.getWorld().getName(), LongHash.toLong(x, z));
        }
    }

    public static Claim getClaimAt(Location location) {
        return getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    public static Claim getClaimAt(World world, int x, int z) {
        return claimPositions.get(world.getName(), LongHash.toLong(x, z));
    }

    public static Faction getByLocation(Location location) {
        Claim claim = getClaimAt(location);
        if(claim != null) {
            Faction faction = claim.getFaction();
            if(faction != null)
                return faction;
        } else if (LocUtils.checkWarzone(location)) {
            return Faction.getByName("Warzone");
        }

        return null;
    }

    public static Faction getByName(String name) {
        return factions.values().stream()
                .filter(fac -> fac.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static Faction getByPlayer(String name) {
        HCFProfile profile;
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if(player != null) {
            profile = HCFProfile.getByUuid(player.getUniqueId());
        } else {
            return null;
        }

        PlayerFaction faction = profile.getFactionObj();
        if(faction != null)
            return faction;

        return null;
    }

    public static List<Faction> findFactions(String string) {
        List<Faction> factions = new ArrayList<>();
        if(Bukkit.isPrimaryThread())
            return factions;

        Faction byName = getByName(string);
        if(byName != null)
            factions.add(byName);

        if (factions.size() > 0) return factions;

        Faction byPlayer = getByPlayer(string);
        if(byPlayer != null)
            factions.add(byPlayer);

        return factions;
    }

    public static List<Faction> getAllByName(String name) {
        return factions.values().stream()
                .filter(fac -> fac.name.equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public static void refreshSorted() {
        List<UUID> playerFactions = getFactions().values().stream()
                .filter(PlayerFaction.class::isInstance)
                .map(Faction::getUuid)
                .sorted(Comparator.comparingInt(id -> {
                    UUID uuid = (UUID) id;
                    PlayerFaction faction = (PlayerFaction) getFaction(uuid);

                    return faction.getOnlineCount();
                }).reversed())
                .collect(Collectors.toList());

        PlayerFaction.setSorted(playerFactions);
    }

    public static void refreshSortedFactionTop() {
        List<Faction> playerFactions = getFactions().values().stream()
                .filter(PlayerFaction.class::isInstance)
                .sorted(Comparator.comparingInt(faction -> {
                    PlayerFaction playerFaction = (PlayerFaction) faction;

                    return playerFaction.getPoints();
                }).reversed()).limit(10)
                .collect(Collectors.toList());

        PlayerFaction.setSortedFactionTop(playerFactions);
    }

    public static void instate() {
        // Give some time for Factions to load
        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), Faction::refreshSorted, 200L, 200L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), Faction::refreshSortedFactionTop, 200L, 200L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
            Bukkit.getLogger().info("Attempting to save all factions...");
            long start = System.currentTimeMillis();
            Faction.getFactions().values().forEach(HCF.getInstance().getBackend()::saveFactionSync);

            long end = System.currentTimeMillis();
            Bukkit.getLogger().info("Saved " + Faction.getFactions().size() + " factions. Took " + (end - start) + "ms.");
        }, 200L, (20L * 60L) * 7L); // Save 10 seconds after boot, then every 7 minutes.

        if (ConfigValues.FEATURES_TIME) {
            Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), () -> {
                Bukkit.getWorlds().forEach(world -> {
                    world.setTime(0L);
                });
            }, 1000L, 1000L);
        }
    }
    /*=============================*/
}
