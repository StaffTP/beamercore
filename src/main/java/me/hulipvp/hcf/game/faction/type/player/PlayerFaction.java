package me.hulipvp.hcf.game.faction.type.player;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerFaction extends Faction {
    
    @Getter @Setter private static List<UUID> sorted = new ArrayList<>();
    @Getter @Setter private static List<Faction> sortedFactionTop = new ArrayList<>();

    @Getter private final Map<UUID, FactionMember> members;
    @Getter private final List<UUID> allies, requestedAllies, invited;

    @Getter @Setter private FactionMember leader;
    @Getter @Setter private double dtr;
    @Getter @Setter private Timestamp startRegen, lastRegen;
    @Getter @Setter private boolean regening, open;
    @Getter @Setter private int lives, balance, kothCaptures, points;
    @Getter @Setter private UUID focused;
    @Getter @Setter private boolean powerFaction;
    @Getter @Setter private PlayerFaction factionFocus;
    @Getter @Setter private Location rallyPoint;
    @Getter @Setter private String announcement;
    @Getter @Setter private BukkitTask regenTask;

    public PlayerFaction(UUID uuid, String name, UUID leader) {
        super(uuid, name, true, FactionType.PLAYER);

        this.leader = new FactionMember(leader, FactionRank.LEADER);
        this.members = new HashMap<>();

        if(leader != null)
            this.members.put(this.getLeader().getUuid(), this.getLeader());

        this.allies = new ArrayList<>();
        this.requestedAllies = new ArrayList<>();
        this.invited = new ArrayList<>();
        this.dtr = 1.1;
        this.startRegen = null;
        this.lastRegen = null;
        this.regening = false;
        this.lives = 0;
        this.balance = 0;
        this.points = 0;
        this.powerFaction = false;
        this.announcement = "";
        this.regenTask = null;

        this.setupRegenTask();
    }

    public void addMember(FactionMember member) {
        this.getMembers().put(member.getUuid(), member);
    }

    public void removeMember(UUID uuid) {
        this.getMembers().remove(uuid);
    }

    public List<Player> getOnlinePlayers() {
        return members.values()
                .stream()
                .sorted(Comparator.comparingInt(factionMember -> ((FactionMember) factionMember).getRank().getRank()).reversed())
                .map(factionMember -> Bukkit.getPlayer(factionMember.getUuid()))
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .collect(Collectors.toList());
    }

    public int getOnlineCount() {
        return this.getOnlinePlayers().size();
    }

    public void sendMessage(String message) {
        this.getOnlinePlayers().forEach(ply -> ply.sendMessage(message));
    }

    public void sendExcludingMessage(Player player, String message) {
        this.getOnlinePlayers().stream()
                .filter(ply -> !player.getName().equalsIgnoreCase(ply.getName()))
                .forEach(ply -> ply.sendMessage(message));
    }

    public void sendAllyMessage(String message) {
        this.sendMessage(message);

        for(UUID uuid : allies) {
            PlayerFaction faction = getPlayerFaction(uuid);
            if(faction != null)
                faction.sendMessage(message);
        }
    }

    public void sendCaptainMessage(String message) {
        for(FactionMember member : this.getMembers().values()) {
            if(member.isAtLeast(FactionRank.CAPTAIN)) {
                Player player = Bukkit.getPlayer(member.getUuid());
                if(player != null && player.isOnline())
                    player.sendMessage(message);
            }
        }
    }

    public boolean isAllied(UUID uuid) {
        return this.getAllies().contains(uuid);
    }

    public void addAlly(UUID uuid) {
        this.getAllies().add(uuid);
    }

    public void removeAlly(UUID uuid) {
        this.getAllies().remove(uuid);
    }

    public boolean isRequestedAlly(UUID uuid) {
        return this.getRequestedAllies().contains(uuid);
    }

    public void addRequestedAlly(UUID uuid) {
        this.getRequestedAllies().add(uuid);
    }

    public void removeRequestedAlly(UUID uuid) {
        this.getRequestedAllies().remove(uuid);
    }

    public boolean isInvited(UUID uuid) {
        return this.getInvited().contains(uuid);
    }

    public void addInvite(UUID uuid) {
        this.getInvited().add(uuid);
    }

    public void removeInvite(UUID uuid) {
        this.getInvited().remove(uuid);
    }

    public double getMaxDtr() {
        switch(members.size()) {
            case 0:
            case 1:
                return 1.01;
            case 2:
                return 2.01;
            case 3:
                return 3.25;
            case 4:
                return 3.75;
            case 5:
                return 4.50;
            default:
                return 5.50;
        }
    }

    public String getDtrSymbol() {
        if(this.getStartRegen() != null)
            return ChatColor.RED.toString() + '\u25a0';

        if(this.isRegening() || this.getDtr() != this.getMaxDtr())
            return ChatColor.GREEN.toString() + '\u25B2';

        return ChatColor.GREEN.toString() + '\u25a0';
    }

    public String getDtrDisplay() {
        return (getDtr() <= 0.00 ? ChatColor.DARK_RED : ChatColor.GREEN) + String.valueOf(getDtr()) + getDtrSymbol();
    }

    public String getDisplayString(UUID uuid) {
        return this.getRelationColor(uuid) + this.getName() + " " + ((this.isDeathban()) ? Locale.FACTION_DEATHBAN.toString() : Locale.FACTION_NONDEATHBAN.toString());
    }

    public String getDisplayString(Player player) {
        return getDisplayString(player.getUniqueId());
    }

    public String getRelationColor(Faction faction) {
        return allies.contains(faction.getUuid()) ? ChatColor.LIGHT_PURPLE.toString() : ChatColor.RED.toString();
    }

    public String getRelationColor(UUID uuid) {
        HCFProfile profile = HCFProfile.getByUuid(uuid);
        if(!profile.hasFaction())
            return ChatColor.RED.toString();

        if(isAllied(uuid))
            return ChatColor.LIGHT_PURPLE.toString();

        return this.getMembers().containsKey(uuid) ? ChatColor.GREEN.toString() : ChatColor.RED.toString();
    }

    public String getRelationColor(Player player) {
        return getRelationColor(player.getUniqueId());
    }

    public boolean isRaidable() {
        return this.getDtr() <= 0;
    }

    public void setupRegenTask() {
        if (this.regenTask != null) {
            this.regenTask.cancel();
            this.regenTask = null;
        }
        this.regenTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(getOnlineCount() == 0)
                    return;
                if(ServerTimer.isEotw()) {
                    cancel();
                    return;
                }

                if(getStartRegen() == null) {
                    if(getDtr() < getMaxDtr()) {
                        if (getLastRegen() == null || new Timestamp(System.currentTimeMillis()).getTime() > getLastRegen().getTime()) {
                            DecimalFormat df = new DecimalFormat("#.##");
                            String newDtr = df.format(getDtr() + ConfigValues.FACTIONS_DTR_REGEN_INCREMENT);
                            setDtr(Double.valueOf(newDtr));
                            setLastRegen(new Timestamp(System.currentTimeMillis() + (ConfigValues.FACTIONS_DTR_REGEN_DELAY * 1000)));

                        }
                    } else {
                        setDtr(getMaxDtr());
                        setRegening(false);
                        setStartRegen(null);
                        regenTask.cancel();
                        regenTask = null;
                    }
                } else {
                    if(getStartRegen().getTime() > new Timestamp(System.currentTimeMillis()).getTime())
                        return;

                    setStartRegen(null);
                    setRegening(true);
                    sendMessage(Locale.COMMAND_FACTION_NOW_REGENERATING.toString());
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 20, 20);
    }

    @Override
    public Document toDocument() {
        Document doc = super.toDocument().append("leader", this.getLeader().getUuid().toString() + ";" + this.getLeader().getRank().toString() + ";");

        BasicDBObject members = new BasicDBObject();
        BasicDBList allies = new BasicDBList();
        BasicDBList requestedAllies = new BasicDBList();
        BasicDBList invited = new BasicDBList();

        for(FactionMember member : this.getMembers().values())
            members.append(member.getUuid().toString(), member.getRank().toString());

        for(UUID ally : this.getAllies())
            allies.add(ally.toString());

        for(UUID ally : this.getRequestedAllies())
            requestedAllies.add(ally.toString());

        for(UUID invitee : this.getInvited())
            invited.add(invitee.toString());

        doc.append("members", members);
        doc.append("allies", allies);
        doc.append("requestedAllies", requestedAllies);
        doc.append("invited", invited);
        doc.append("dtr", this.getDtr());
        doc.append("startRegen", ((this.getStartRegen() == null) ? null : TimeUtils.serializeTimestamp(this.getStartRegen())));
        doc.append("regening", this.isRegening());
        doc.append("open", this.isOpen());
        doc.append("lives", this.getLives());
        doc.append("kothCaptures", this.getKothCaptures());
        doc.append("balance", this.getBalance());
        doc.append("points", this.getPoints());
        doc.append("powerfaction", this.isPowerFaction());
        doc.append("announcement", this.getAnnouncement());

        return doc;
    }
}
