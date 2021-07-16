package me.hulipvp.hcf.backend.backends;

import me.hulipvp.hcf.HCF;
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
import me.hulipvp.hcf.utils.TaskUtils;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class FlatfileBackend extends HCFBackend {

    public FlatfileBackend() {
        super(BackendType.FLAT_FILE);
        getFolder(HCFProfile.class, true);
        getFolder(Faction.class, true);
        getFolder(Koth.class, true);
        getFolder(DTC.class, true);
        getFolder(Conquest.class, true);
        getFolder(Mountain.class, true);
        setLoaded(true);
    }

    public File getDatafile(Object object, boolean create) {
        File file = new File(HCF.getInstance().getDataFolder(), filename(object) + ".json");
        if (!file.exists() && create) {
            File datFile = new File(HCF.getInstance().getDataFolder(), filename(object) + ".dat");
            if (datFile.exists()) {
                try {
                    FileUtils.copyFile(datFile, file, true);
                    datFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                file = new File(HCF.getInstance().getDataFolder(), filename(object) + ".json");
            } else {
                try {
                    file.createNewFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return file;
    }

    public static void renameFile(File toBeRenamed, String newName) throws IOException {
        boolean success = toBeRenamed.renameTo(new File(newName));
        if (!success) {
            System.out.println(newName + " couldn't be renamed");
        }
    }

    public File getFolder(Class<?> type, boolean create) {
        File dataFolder = new File(HCF.getInstance().getDataFolder(),  "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File folder;
        if (type == HCFProfile.class) {
            folder = new File(HCF.getInstance().getDataFolder(),"data/profiles");
        } else if (type == Faction.class) {
            folder = new File(HCF.getInstance().getDataFolder(),"data/factions");
        } else if (type == Koth.class) {
            folder = new File(HCF.getInstance().getDataFolder(),"data/koths");
        } else if (type == Conquest.class) {
            folder = new File(HCF.getInstance().getDataFolder(),"data/conquests");
        } else if (type == DTC.class) {
            folder = new File(HCF.getInstance().getDataFolder(),"data/dtcs");
        } else if (type == Mountain.class) {
            folder = new File(HCF.getInstance().getDataFolder(),"data/mountains");
        } else {
            return null;
        }
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    public String filename(Object object) {
        if (object instanceof HCFProfile) {
            return "data/profiles/" + ((HCFProfile) object).getUuid().toString();
        } else if (object instanceof Faction) {
            return "data/factions/" + ((Faction) object).getUuid().toString();
        } else if (object instanceof Koth) {
            return "data/koths/" + ((Koth)object).getName();
        } else if (object instanceof Conquest) {
            return "data/conquests/" + ((Conquest) object).getName();
        } else if (object instanceof DTC) {
            return "data/dtcs/" + ((DTC) object).getName();
        } else if (object instanceof Mountain) {
            return "data/mountains/" + ((Mountain) object).getName();
        }
        return "unknown";
    }

    @Override
    public void close() {
    }

    @Override
    public void createProfile(HCFProfile profile) {
        this.saveProfile(profile);
    }

    private void deleteObject(Object object) {
        TaskUtils.runAsync(() -> {
            File file = getDatafile(object, false);
            if (file.exists()) file.delete();
        });
    }

    @Override
    public void deleteProfile(HCFProfile profile) {
        this.deleteObject(profile);
    }

    private void deleteObjects(Class<?> type) {
        TaskUtils.runAsync(() -> {
            for (File file : getFolder(type, true).listFiles()) {
                file.delete();
            }
        });
    }

    @Override
    public void deleteProfiles() {
        this.deleteObject(HCFProfile.class);
    }

    @Override
    public void saveProfile(HCFProfile profile) {
        TaskUtils.runAsync(() -> saveProfileSync(profile));
    }

    private void saveObject(Object object, String json) {
        File file = this.getDatafile(object, true);
        try {
            Files.write(Paths.get(file.toURI()), json.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void saveProfileSync(HCFProfile profile) {
        JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
        this.saveObject(profile, profile.toDocument().toJson(settingsBuilder.build()));
    }

    @Override
    public void loadProfile(HCFProfile profile) {
        File file = this.getDatafile(profile, true);
        try {
            String json = new String(Files.readAllBytes(Paths.get(file.toURI())));

            if(!json.isEmpty()) {
                Document doc = Document.parse(json);
                profile.load(doc);
            } else {
                this.createProfile(profile);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadProfiles() {
        for (File file : getFolder(HCFProfile.class, true).listFiles()) {
            String uuid = file.getName().substring(0, file.getName().length() - (file.getName().contains(".dat") ? 4 : 5));
            HCFProfile.getByUuid(UUID.fromString(uuid));
        }
    }

    @Override
    public void createFaction(Faction faction) {
        this.saveFaction(faction);
    }

    @Override
    public void deleteFaction(Faction faction) {
        this.deleteObject(faction);
    }

    @Override
    public void deleteFactions() {
        this.deleteObjects(Faction.class);
    }

    @Override
    public void saveFaction(Faction faction) {
        TaskUtils.runAsync(() -> saveFactionSync(faction));
    }

    @Override
    public void saveFactionSync(Faction faction) {
        JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
        this.saveObject(faction, faction.toDocument().toJson(settingsBuilder.build()));
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

            if(faction.getType() == FactionType.DTC)
                DTC.getDTC(faction.getName()).setFaction((DTCFaction) faction);

            if(faction.getType() == FactionType.MOUNTAIN)
                Mountain.getMountain(faction.getName()).setFaction((MountainFaction) faction);
        }
    }

    @Override
    public void loadFactions() {
        for (File file : getFolder(Faction.class, true).listFiles()) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(file.toURI())));

                if(!json.isEmpty()) {
                    Document doc = Document.parse(json);
                    this.loadFaction(doc);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        BackendUtils.invalidFactionCheck();
    }

    @Override
    public void createKoth(Koth koth) {
        this.saveKoth(koth);
    }

    @Override
    public void deleteKoth(Koth koth) {
        this.deleteObject(koth);
    }

    @Override
    public void saveKoth(Koth koth) {
        TaskUtils.runAsync(() -> saveKothSync(koth));
    }

    @Override
    public void saveKothSync(Koth koth) {
        JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
        this.saveObject(koth, koth.toDocument().toJson(settingsBuilder.build()));
    }

    private synchronized void loadKoth(Document doc) {
        if(doc != null) {
            Koth koth = BackendUtils.kothFromDocument(doc);
            Koth.getKoths().put(koth.getName(), koth);
        }
    }

    @Override
    public void loadKoths() {
        for (File file : getFolder(Koth.class, true).listFiles()) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(file.toURI())));

                if(!json.isEmpty()) {
                    Document doc = Document.parse(json);
                    this.loadKoth(doc);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void createConquest(Conquest conquest) {
        this.saveConquest(conquest);
    }

    @Override
    public void deleteConquest(Conquest conquest) {
        this.deleteObject(conquest);
    }

    @Override
    public void saveConquest(Conquest conquest) {
        TaskUtils.runAsync(() -> saveConquestSync(conquest));
    }

    @Override
    public void saveConquestSync(Conquest conquest) {
        JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
        this.saveObject(conquest, conquest.toDocument().toJson(settingsBuilder.build()));
    }

    private synchronized void loadConquest(Document doc) {
        if(doc != null) {
            Conquest conquest = BackendUtils.conquestFromDocument(doc);
            Conquest.getConquests().put(conquest.getName(), conquest);
        }
    }

    @Override
    public void loadConquests() {
        for (File file : getFolder(Conquest.class, true).listFiles()) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(file.toURI())));

                if(!json.isEmpty()) {
                    Document doc = Document.parse(json);
                    this.loadConquest(doc);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void createDTC(DTC dtc) {
        this.saveDTC(dtc);
    }

    @Override
    public void deleteDTC(DTC dtc) {
        this.deleteObject(dtc);
    }

    @Override
    public void saveDTC(DTC dtc) {
        TaskUtils.runAsync(() -> saveDTCSync(dtc));
    }

    @Override
    public void saveDTCSync(DTC dtc) {
        JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
        this.saveObject(dtc, dtc.toDocument().toJson(settingsBuilder.build()));
    }

    private synchronized void loadDTC(Document doc) {
        if(doc != null) {
            DTC dtc = BackendUtils.dtcFromDocument(doc);
            DTC.getDTCs().put(dtc.getName(), dtc);
        }
    }

    @Override
    public void loadDTCs() {
        for (File file : getFolder(DTC.class, true).listFiles()) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(file.toURI())));

                if(!json.isEmpty()) {
                    Document doc = Document.parse(json);
                    this.loadDTC(doc);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void createMountain(Mountain mountain) {
        this.saveMountain(mountain);
    }

    @Override
    public void saveMountain(Mountain mountain) {
        TaskUtils.runAsync(() -> this.saveMountainSync(mountain));
    }

    @Override
    public void saveMountainSync(Mountain mountain) {
        JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
        this.saveObject(mountain, mountain.toDocument().toJson(settingsBuilder.build()));
    }

    @Override
    public void deleteMountain(Mountain mountain) {
        this.deleteObject(mountain);
    }

    private synchronized void loadMountain(Document doc) {
        if(doc != null) {
            Mountain mountain = BackendUtils.mountainFromDocument(doc);
            Mountain.getMountains().put(mountain.getName(), mountain);
        }
    }

    @Override
    public void loadMountains() {
        for (File file : getFolder(Mountain.class, true).listFiles()) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(file.toURI())));

                if(!json.isEmpty()) {
                    Document doc = Document.parse(json);
                    this.loadMountain(doc);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}