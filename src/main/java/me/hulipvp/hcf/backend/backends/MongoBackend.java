package me.hulipvp.hcf.backend.backends;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.backend.IBackend;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.event.ConquestFaction;
import me.hulipvp.hcf.game.faction.type.event.DTCFaction;
import me.hulipvp.hcf.game.faction.type.event.KothFaction;
import me.hulipvp.hcf.game.faction.type.event.MountainFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.backend.BackendType;
import me.hulipvp.hcf.backend.BackendUtils;
import me.hulipvp.hcf.backend.HCFBackend;
import me.hulipvp.hcf.backend.creds.MongoCredentials;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bson.Document;

import java.util.Collections;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoBackend extends HCFBackend implements IBackend {
    
    private MongoClient mongo;
    private MongoDatabase db;
    private MongoCollection<Document> profiles, factions, koths, conquests, dtcs, mountains;

    public MongoBackend(MongoCredentials credentials) {
        super(BackendType.MONGO);

        try {
            ServerAddress address = new ServerAddress(credentials.getHostname(), credentials.getPort());

            if(HCF.getInstance().getConfig().getBoolean("backend.mongo.auth.enable")) {
                MongoCredential credential = MongoCredential.createCredential(credentials.getUsername(), credentials.getAuthDb(), credentials.getPassword().toCharArray());
                this.mongo = new MongoClient(address, Collections.singletonList(credential));
            } else {
                this.mongo = new MongoClient(address);
            }

            this.db = this.mongo.getDatabase(credentials.getDatabase());
            this.profiles = this.db.getCollection("profiles");
            this.factions = this.db.getCollection("factions");
            this.koths = this.db.getCollection("koths");
            this.conquests = this.db.getCollection("conquests");
            this.dtcs = this.db.getCollection("dtcs");
            this.mountains = this.db.getCollection("mountains");

            setLoaded(db != null);
            if(isLoaded())
                this.logInfoMessage("Mongo Driver successfully loaded.");
            else
                throw new Exception("A connection was opened but was unable to read the database '" + credentials.getDatabase() + "'.");
        } catch(Exception e) {
            this.logException("Mongo Driver failed to load.", e);
        }
    }

    @Override
    public void close() {
        if(this.mongo != null)
            this.mongo.close();
    }

    /*=============================*/
    // Profiles

    @Override
    public void createProfile(HCFProfile profile) {
        TaskUtils.runAsync(() -> {
            this.profiles.insertOne(profile.toDocument());
        });
    }

    @Override
    public void deleteProfile(HCFProfile profile) {
        TaskUtils.runAsync(() -> {
            this.profiles.deleteOne(eq("uuid", profile.getUuid().toString()));
        });
    }

    @Override
    public void deleteProfiles() {
        TaskUtils.runAsync(() -> {
            this.profiles.drop();
            this.profiles = this.db.getCollection("profiles");
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
        Document doc = profile.toDocument();
        this.profiles.findOneAndReplace(eq("uuid", profile.getUuid().toString()), doc);
    }

    @Override
    public void loadProfile(HCFProfile profile) {
        Document doc = this.profiles.find(eq("uuid", profile.getUuid().toString())).first();

        if(doc != null) {
            profile.load(doc);
        } else {
            this.createProfile(profile);
        }
    }

    @Override
    public void loadProfiles() {
        for(Document doc : this.profiles.find()) {
            if(!doc.containsKey("uuid"))
                continue;

            UUID uuid = UUID.fromString(doc.getString("uuid"));
            HCFProfile.getByUuid(uuid);
        }
    }

    /*=============================*/

    /*=============================*/
    // Factions

    @Override
    public void createFaction(Faction faction) {
        TaskUtils.runAsync(() -> {
            this.factions.insertOne(faction.toDocument());
        });
    }

    @Override
    public void deleteFaction(Faction faction) {
        TaskUtils.runAsync(() -> {
            this.factions.deleteOne(eq("uuid", faction.getUuid().toString()));
        });
    }

    @Override
    public void deleteFactions() {
        TaskUtils.runAsync(() -> {
            this.factions.drop();
            this.factions = this.db.getCollection("factions");
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
        Document doc = faction.toDocument();
        if(doc != null)
            this.factions.findOneAndReplace(eq("uuid", faction.getUuid().toString()), doc);
    }

    private synchronized void loadFaction(Document doc) {
        if(doc != null) {
            Faction faction = BackendUtils.factionFromDocument(doc);
            if(faction == null)
                return;

            if(faction.getType() == FactionType.KOTH)
                Koth.getKoth(faction.getName()).setFaction((KothFaction) faction);

            if(faction.getType() == FactionType.DTC)
                DTC.getDTC(faction.getName()).setFaction((DTCFaction) faction);

            if(faction.getType() == FactionType.CONQUEST)
                Conquest.getConquest(faction.getName()).setFaction((ConquestFaction) faction);

            if(faction.getType() == FactionType.MOUNTAIN)
                Mountain.getMountain(faction.getName()).setFaction((MountainFaction) faction);
        }
    }

    @Override
    public void loadFactions() {
        TaskUtils.runAsync(() -> {
            for(Document doc : this.factions.find()) {
                this.loadFaction(doc);
            }
        });

        BackendUtils.invalidFactionCheck();
    }
    /*=============================*/

    /*=============================*/
    // Koths

    @Override
    public void createKoth(Koth koth) {
        TaskUtils.runAsync(() -> {
            this.koths.insertOne(koth.toDocument());
        });
    }

    @Override
    public void deleteKoth(Koth koth) {
        TaskUtils.runAsync(() -> {
            this.koths.deleteOne(eq("name", koth.getName()));
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
        this.koths.findOneAndReplace(eq("name", koth.getName()), koth.toDocument());
    }

    private synchronized void loadKoth(Document doc) {
        if(doc != null) {
            Koth koth = BackendUtils.kothFromDocument(doc);
            Koth.getKoths().put(koth.getName(), koth);
        }
    }

    @Override
    public void loadKoths() {
        for(Document doc : this.koths.find())
            this.loadKoth(doc);
    }
    /*=============================*/

    /*=============================*/
    // Conquests

    @Override
    public void createConquest(Conquest conquest) {
        TaskUtils.runAsync(() -> {
            this.conquests.insertOne(conquest.toDocument());
        });
    }

    @Override
    public void deleteConquest(Conquest conquest) {
        TaskUtils.runAsync(() -> {
            this.conquests.deleteOne(eq("name", conquest.getName()));
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
        this.conquests.findOneAndReplace(eq("name", conquest.getName()), conquest.toDocument());
    }

    private synchronized void loadConquest(Document doc) {
        if(doc != null) {
            Conquest conquest = BackendUtils.conquestFromDocument(doc);
            Conquest.getConquests().put(conquest.getName(), conquest);
        }
    }

    @Override
    public void loadConquests() {
        for(Document doc : this.conquests.find())
            this.loadConquest(doc);
    }
    /*=============================*/

    /*=============================*/
    // DTC

    @Override
    public void createDTC(DTC dtc) {
        TaskUtils.runAsync(() -> {
            this.dtcs.insertOne(dtc.toDocument());
        });
    }

    @Override
    public void deleteDTC(DTC dtc) {
        TaskUtils.runAsync(() -> {
            this.dtcs.deleteOne(eq("name", dtc.getName()));
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
        this.dtcs.findOneAndReplace(eq("name", dtc.getName()), dtc.toDocument());
    }

    private synchronized void loadDTC(Document doc) {
        if(doc != null) {
            DTC dtc = BackendUtils.dtcFromDocument(doc);
            DTC.getDTCs().put(dtc.getName(), dtc);
        }
    }

    @Override
    public void loadDTCs() {
        for(Document doc : this.dtcs.find())
            this.loadDTC(doc);
    }
    /*=============================*/

    /*=============================*/
    // Mountains

    @Override
    public void createMountain(Mountain mountain) {
        TaskUtils.runAsync(() -> {
            this.mountains.insertOne(mountain.toDocument());
        });
    }

    @Override
    public void deleteMountain(Mountain mountain) {
        TaskUtils.runAsync(() -> {
            this.mountains.deleteOne(eq("name", mountain.getName()));
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
        try {
            this.mountains.findOneAndReplace(eq("name", mountain.getName()), mountain.toDocument());
        } catch(Exception ex) {
            deleteMountain(mountain);
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
        for(Document doc : this.mountains.find())
            this.loadMountain(doc);
    }
    /*=============================*/
}
