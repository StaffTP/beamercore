package me.hulipvp.hcf.backend.backends;

import lombok.Getter;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.event.ConquestFaction;
import me.hulipvp.hcf.game.faction.type.event.KothFaction;
import me.hulipvp.hcf.game.faction.type.event.MountainFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.backend.BackendType;
import me.hulipvp.hcf.backend.BackendUtils;
import me.hulipvp.hcf.backend.HCFBackend;
import me.hulipvp.hcf.backend.creds.RedisCredentials;
import me.hulipvp.hcf.utils.TaskUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bson.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;
import java.util.UUID;

public class RedisBackend extends HCFBackend {

    @Getter private JedisPool pool;

    public RedisBackend(RedisCredentials credentials) {
        super(BackendType.REDIS);

        if(!credentials.password()) {
            this.pool = new JedisPool(new GenericObjectPoolConfig(), credentials.getHost(), credentials.getPort(), 3000);
        } else {
            this.pool = new JedisPool(new GenericObjectPoolConfig(), credentials.getHost(), credentials.getPort(), 3000, credentials.getPassword());
        }

        try(Jedis jedis = pool.getResource()) {
            setLoaded(jedis.isConnected());
            if(isLoaded())
                logInfoMessage("Redis Driver successfully loaded.");
            else
                throw new Exception("Unable to establish Jedis connection.");
        } catch(Exception ex) {
            logException("Redis Driver failed to load.", ex);
        }
    }

    @Override
    public void close() {
        if(this.pool != null)
            if(!this.pool.isClosed())
                this.pool.close();
    }

    /*=============================*/
    // Profiles

    @Override
    public void createProfile(HCFProfile profile) {
        this.saveProfile(profile);
    }

    @Override
    public void deleteProfile(HCFProfile profile) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.del(this.getKey(KeyType.PROFILE, profile.getUuid().toString()));
        }
    }

    @Override
    public void deleteProfiles() {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.PROFILE) + "*");
            }
        });
    }

    @Override
    public void saveProfile(HCFProfile profile) {
        TaskUtils.runAsync(() -> {
            this.saveProfileSync(profile);
        });
    }

    @Override
    public void saveProfileSync(HCFProfile profile) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.set(this.getKey(KeyType.PROFILE, profile.getUuid().toString()), profile.toDocument().toJson());
        }
    }

    @Override
    public void loadProfile(HCFProfile profile) {
        try(Jedis jedis = this.getPool().getResource()) {
            String json = jedis.get(this.getKey(KeyType.PROFILE, profile.getUuid().toString()));

            if(json != null) {
                Document doc = Document.parse(json);
                profile.load(doc);
            } else {
                this.createProfile(profile);
            }
        }
    }

    @Override
    public void loadProfiles() {
        try(Jedis jedis = this.getPool().getResource()) {
            Set<String> profiles = jedis.keys(this.getKey(KeyType.PROFILE) + "*");

            profiles.forEach(profile -> {
                Document doc = Document.parse(jedis.get(profile));
                if(doc == null || !doc.containsKey("uuid"))
                    return;

                UUID uuid = UUID.fromString(doc.getString("uuid"));
                HCFProfile.getByUuid(uuid);
            });
        }
    }

    /*=============================*/

    /*=============================*/
    // Factions

    @Override
    public void createFaction(Faction faction) {
        this.saveFaction(faction);
    }

    @Override
    public void deleteFaction(Faction faction) {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.FACTION, faction.getUuid().toString()));
            }
        });
    }

    @Override
    public void deleteFactions() {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.FACTION) + "*");
            }
        });
    }

    @Override
    public void saveFaction(Faction faction) {
        TaskUtils.runAsync(() -> {
            this.saveFactionSync(faction);
        });
    }

    @Override
    public void saveFactionSync(Faction faction) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.set(this.getKey(KeyType.FACTION, faction.getUuid().toString()), faction.toDocument().toJson());
        }
    }

    private synchronized void loadFaction(Document doc) {
        if(doc != null) {
            Faction faction = BackendUtils.factionFromDocument(doc);
            if(faction == null)
                return;

            if(faction.getType() == FactionType.KOTH)
                Koth.getKoth(faction.getName()).setFaction((KothFaction) faction);

            if(faction.getType() == FactionType.CONQUEST)
                Conquest.getConquest(faction.getName()).setFaction((ConquestFaction) faction);

            if(faction.getType() == FactionType.MOUNTAIN)
                Mountain.getMountain(faction.getName()).setFaction((MountainFaction) faction);
        }
    }

    @Override
    public void loadFactions() {
        try(Jedis jedis = this.getPool().getResource()) {
            Set<String> factions = jedis.keys(this.getKey(KeyType.FACTION) + "*");

            factions.forEach(faction -> {
                Document doc = Document.parse(jedis.get(faction));
                if(doc != null)
                    this.loadFaction(doc);
            });
        }

        BackendUtils.invalidFactionCheck();
    }
    /*=============================*/

    /*=============================*/
    // Koths

    @Override
    public void createKoth(Koth koth) {
        this.saveKoth(koth);
    }

    @Override
    public void deleteKoth(Koth koth) {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.KOTH, koth.getName()));
            }
        });
    }

    @Override
    public void saveKoth(Koth koth) {
        TaskUtils.runAsync(() -> {
            this.saveKothSync(koth);
        });
    }

    @Override
    public void saveKothSync(Koth koth) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.set(this.getKey(KeyType.KOTH, koth.getName()), koth.toDocument().toJson());
        }
    }

    private synchronized void loadKoth(Document doc) {
        if(doc != null) {
            Koth koth = BackendUtils.kothFromDocument(doc);
            Koth.getKoths().put(koth.getName(), koth);
        }
    }

    @Override
    public void loadKoths() {
        try(Jedis jedis = this.getPool().getResource()) {
            Set<String> koths = jedis.keys(this.getKey(KeyType.KOTH) + "*");

            koths.forEach(koth -> {
                Document doc = Document.parse(jedis.get(koth));
                if(doc != null)
                    this.loadKoth(doc);
            });
        }
    }
    /*=============================*/

    /*=============================*/
    // Conquests

    @Override
    public void createConquest(Conquest conquest) {
        this.saveConquest(conquest);
    }

    @Override
    public void deleteConquest(Conquest conquest) {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.CONQUEST, conquest.getName()));
            }
        });
    }

    @Override
    public void saveConquest(Conquest conquest) {
        TaskUtils.runAsync(() -> {
            this.saveConquestSync(conquest);
        });
    }

    @Override
    public void saveConquestSync(Conquest conquest) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.set(this.getKey(KeyType.CONQUEST, conquest.getName()), conquest.toDocument().toJson());
        }
    }

    private synchronized void loadConquest(Document doc) {
        if(doc != null) {
            Conquest conquest = BackendUtils.conquestFromDocument(doc);
            Conquest.getConquests().put(conquest.getName(), conquest);
        }
    }

    @Override
    public void loadConquests() {
        try(Jedis jedis = this.getPool().getResource()) {
            Set<String> conquests = jedis.keys(this.getKey(KeyType.CONQUEST) + "*");

            conquests.forEach(conquest -> {
                Document doc = Document.parse(jedis.get(conquest));
                if(doc != null)
                    this.loadConquest(doc);
            });
        }
    }
    /*=============================*/

    /*=============================*/
    // Conquests

    @Override
    public void createDTC(DTC dtc) {
        this.saveDTC(dtc);
    }

    @Override
    public void deleteDTC(DTC dtc) {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.DTC, dtc.getName()));
            }
        });
    }

    @Override
    public void saveDTC(DTC dtc) {
        TaskUtils.runAsync(() -> {
            this.saveDTCSync(dtc);
        });
    }

    @Override
    public void saveDTCSync(DTC dtc) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.set(this.getKey(KeyType.DTC, dtc.getName()), dtc.toDocument().toJson());
        }
    }

    private synchronized void loadDTC(Document doc) {
        if(doc != null) {
            DTC dtc = BackendUtils.dtcFromDocument(doc);
            DTC.getDTCs().put(dtc.getName(), dtc);
        }
    }

    @Override
    public void loadDTCs() {
        try(Jedis jedis = this.getPool().getResource()) {
            Set<String> dtcs = jedis.keys(this.getKey(KeyType.DTC) + "*");

            dtcs.forEach(dtc -> {
                Document doc = Document.parse(jedis.get(dtc));
                if(doc != null)
                    this.loadDTC(doc);
            });
        }
    }
    /*=============================*/

    /*=============================*/
    // Mountains

    @Override
    public void createMountain(Mountain mountain) {
        this.saveMountain(mountain);
    }

    @Override
    public void deleteMountain(Mountain mountain) {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.MOUNTAIN, mountain.getName()));
            }
        });
    }

    @Override
    public void saveMountain(Mountain mountain) {
        TaskUtils.runAsync(() -> {
            this.saveMountainSync(mountain);
        });
    }

    @Override
    public void saveMountainSync(Mountain mountain) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.set(this.getKey(KeyType.MOUNTAIN, mountain.getName()), mountain.toDocument().toJson());
        }
    }

    private synchronized void loadMountain(Document doc) {
        if(doc != null) {
            Mountain mountain = BackendUtils.mountainFromDocument(doc);
            Mountain.getMountains().put(mountain.getName(), mountain);
        }
    }

    @Override
    public void loadMountains() {
        try(Jedis jedis = this.getPool().getResource()) {
            Set<String> mountains = jedis.keys(this.getKey(KeyType.MOUNTAIN) + "*");

            mountains.forEach(mountain -> {
                Document doc = Document.parse(jedis.get(mountain));
                if(doc != null)
                    this.loadMountain(doc);
            });
        }
    }
    /*=============================*/

    private String getKey(KeyType type) {
        return "hcf:map" + type.getPrefix() + ":";
    }

    private String getKey(KeyType type, String identifier) {
        return getKey(type) + identifier;
    }

    private enum KeyType {

        PROFILE("profile"),
        FACTION("faction"),
        KOTH("koth"),
        CONQUEST("conquest"),
        DTC("dtc"),
        MOUNTAIN("mountain");

        @Getter private String prefix;

        KeyType(String prefix) {
            this.prefix = prefix;
        }
    }
}
