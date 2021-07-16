package me.hulipvp.hcf.backend;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZone;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZoneType;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.koth.data.KothPoint;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.event.mountain.type.MountainType;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.event.ConquestFaction;
import me.hulipvp.hcf.game.faction.type.event.DTCFaction;
import me.hulipvp.hcf.game.faction.type.event.KothFaction;
import me.hulipvp.hcf.game.faction.type.event.MountainFaction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.RoadFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.game.faction.type.system.WarzoneFaction;
import me.hulipvp.hcf.utils.LocUtils;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.TimeUtils;
import me.hulipvp.hcf.utils.chat.C;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.*;

public class BackendUtils {

    public static String VALID = "VALID";

    public static Faction factionFromDocument(Document doc) {
        FactionType type = FactionType.valueOf(doc.getString("type"));

        Faction faction;
        switch(type) {
            case PLAYER:
                PlayerFaction pf = new PlayerFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"), null);

                pf.setDeathban(doc.getBoolean("deathban"));
                pf.setDtr(doc.getDouble("dtr"));

                if(doc.getString("startRegen") != null)
                    pf.setStartRegen(TimeUtils.deserializeTimestamp(doc.getString("startRegen")));

                if(doc.get("open") != null)
                    pf.setOpen(doc.getBoolean("open"));

                pf.setRegening(doc.getBoolean("regening"));
                pf.setLives(doc.getInteger("lives"));
                pf.setBalance(doc.getInteger("balance"));
                pf.setPoints(doc.getInteger("points"));
                pf.setPowerFaction(doc.getBoolean("powerfaction"));
                pf.setAnnouncement(doc.getString("announcement"));

                if(doc.containsKey("home"))
                    pf.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.get("kothCaptures") != null)
                    pf.setKothCaptures(doc.getInteger("kothCaptures"));

                if(doc.get("members") != null) {
                    Document members = doc.get("members", Document.class);
                    for(String member : members.keySet()) {
                        FactionMember facMember = new FactionMember(UUID.fromString(member), FactionRank.valueOf(members.getString(member)));
                        if(facMember.getRank() == FactionRank.LEADER)
                            pf.setLeader(facMember);

                        pf.addMember(facMember);
                    }
                }

                if(doc.containsKey("allies")) {
                    ArrayList<String> allies = doc.get("allies", ArrayList.class);
                    for(String ally : allies)
                        pf.addAlly(UUID.fromString(ally));
                }

                if(doc.containsKey("requestedAllies")) {
                    ArrayList<String> requestedAllies = doc.get("requestedAllies", ArrayList.class);
                    for(String requestedAlly : requestedAllies)
                        pf.addRequestedAlly(UUID.fromString(requestedAlly));
                }

                if(doc.containsKey("invited")) {
                    ArrayList<String> invited = doc.get("invited", ArrayList.class);
                    for(String invitee : invited)
                        pf.addInvite(UUID.fromString(invitee));
                }

                if(doc.containsKey("claim"))
                    pf.addClaim(Claim.fromString(doc.getString("claim")));
                faction = pf;
                break;
            case SAFEZONE:
                SafezoneFaction safezone = new SafezoneFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"));
                safezone.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color")) {
                    safezone.setColor(ChatColor.valueOf(doc.getString("color")));
                }

                if(doc.containsKey("home"))
                    safezone.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.getString("claim") != null)
                    safezone.addClaim(Claim.fromString(doc.getString("claim")));
                faction = safezone;
                break;
            case WARZONE:
                WarzoneFaction warzone = new WarzoneFaction(UUID.fromString(doc.getString("uuid")));
                warzone.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color")) {
                    warzone.setColor(ChatColor.valueOf(doc.getString("color")));
                }

                if(doc.containsKey("home"))
                    warzone.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.getString("claim") != null)
                    warzone.addClaim(Claim.fromString(doc.getString("claim")));
                faction = warzone;
                break;
            case KOTH:
                KothFaction koth = new KothFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"), Koth.getKoth(doc.getString("koth")));
                koth.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color") && ChatColor.valueOf(doc.getString("color")) != null)
                    koth.setColor(ChatColor.valueOf(doc.getString("color")));

                if(doc.containsKey("home"))
                    koth.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.getString("claim") != null)
                    koth.addClaim(Claim.fromString(doc.getString("claim")));
                faction = koth;
                break;
            case CONQUEST:
                ConquestFaction conquest = new ConquestFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"), Conquest.getConquest(doc.getString("conquest")));
                conquest.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color"))
                    conquest.setColor(ChatColor.valueOf(doc.getString("color")));

                if(doc.containsKey("home"))
                    conquest.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.containsKey("home"))
                    conquest.addClaim(Claim.fromString(doc.getString("claim")));
                faction = conquest;
                break;
            case DTC:
                DTCFaction dtc = new DTCFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"), DTC.getDTC(doc.getString("dtc")));
                dtc.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color"))
                    dtc.setColor(ChatColor.valueOf(doc.getString("color")));

                if(doc.get("home") != null) dtc.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.containsKey("home"))
                    dtc.addClaim(Claim.fromString(doc.getString("claim")));
                faction = dtc;
                break;
            case ROAD:
                RoadFaction road = new RoadFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"));
                road.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color"))
                    road.setColor(ChatColor.valueOf(doc.getString("color")));

                if(doc.containsKey("home"))
                    road.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.getString("claim") != null)
                    road.addClaim(Claim.fromString(doc.getString("claim")));
                faction = road;
                break;
            case SYSTEM:
                SystemFaction system = new SystemFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"));
                system.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color"))
                    system.setColor(ChatColor.valueOf(doc.getString("color")));

                if(doc.containsKey("home"))
                    system.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.getString("claim") != null)
                    system.addClaim(Claim.fromString(doc.getString("claim")));
                faction = system;
                break;
            case MOUNTAIN:
                MountainFaction mountain = new MountainFaction(UUID.fromString(doc.getString("uuid")), doc.getString("name"), Mountain.getMountain(doc.getString("name")));
                mountain.setDeathban(doc.getBoolean("deathban"));
                if (doc.containsKey("color"))
                    mountain.setColor(ChatColor.valueOf(doc.getString("color")));

                if(doc.containsKey("home"))
                    mountain.setHome(LocUtils.stringToLocation(doc.getString("home")));

                if(doc.getString("claim") != null)
                    mountain.addClaim(Claim.fromString(doc.getString("claim")));
                faction = mountain;
                break;
            default:
                faction = null;
                return null;
        }

        Set<Claim> claims = null;
        if(doc.containsKey("claims")) {
            claims = new HashSet<>();
            List<Document> claimList = (ArrayList<Document>) doc.get("claims", ArrayList.class);
            for(Document document : claimList) {
                if(document == null || !document.containsKey("claim"))
                    continue;

                claims.add(Claim.fromString(document.getString("claim")));
            }
        }

        if(faction != null && claims != null && !claims.isEmpty())
            claims.forEach(faction::addClaim);

        return null;
    }

    public static Koth kothFromDocument(Document doc) {
        Koth koth = new Koth(doc.getString("name"));
        if(doc.getString("point") != null)
            koth.setPoint(KothPoint.fromString(doc.getString("point")));

        if(HCF.getInstance().getConfig().getString("koth.special-times." + koth.getName().toLowerCase()) != null)
            koth.setTime(HCF.getInstance().getConfig().getInt("koth.special-times." + koth.getName()));

        koth.setSpecial(doc.getBoolean("special"));
        if (doc.get("pearlable") != null) {
            koth.setPearlable(doc.getBoolean("pearlable"));
        }
        if (doc.get("time") != null) {
            koth.setTime(doc.getInteger("time"));
        }
        return koth;
    }

    public static Conquest conquestFromDocument(Document doc) {
        Conquest conquest = new Conquest(doc.getString("name"));

        if(doc.getString("red") != null)
            conquest.getZones().put(ConquestZoneType.RED, ConquestZone.fromString(C.strip(doc.getString("red")).toUpperCase()));
        if(doc.getString("yellow") != null)
            conquest.getZones().put(ConquestZoneType.YELLOW, ConquestZone.fromString(C.strip(doc.getString("yellow")).toUpperCase()));
        if(doc.getString("green") != null)
            conquest.getZones().put(ConquestZoneType.GREEN, ConquestZone.fromString(C.strip(doc.getString("green")).toUpperCase()));
        if(doc.getString("blue") != null)
            conquest.getZones().put(ConquestZoneType.BLUE, ConquestZone.fromString(C.strip(doc.getString("blue")).toUpperCase()));

        return conquest;
    }

    public static DTC dtcFromDocument(Document doc) {
        DTC dtc = new DTC(doc.getString("name"));

        return dtc;
    }

    public static Mountain mountainFromDocument(Document doc) {
        Mountain mountain = new Mountain(doc.getString("name"), MountainType.valueOf(doc.getString("type")));
        mountain.setResetTime(doc.getInteger("resetTime"));

        if(doc.containsKey("point1"))
            mountain.setPoint1(LocUtils.stringToLocation(doc.getString("point1")));

        if(doc.containsKey("point2"))
            mountain.setPoint2(LocUtils.stringToLocation(doc.getString("point2")));

        return mountain;
    }

    public static void invalidFactionCheck() {
        TaskUtils.runAsync(() -> {
            Faction.getFactions().values().stream()
                    .filter(PlayerFaction.class::isInstance)
                    .map(PlayerFaction.class::cast)
                    .forEach(playerFaction -> {
                        playerFaction.getAllies().removeIf(Faction::isNotAFaction);
                    });
        });
    }
}
