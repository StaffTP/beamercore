package me.hulipvp.hcf.backend.files;

import lombok.Getter;

import java.util.*;

public class ReclaimFile extends ConfigFile {

    @Getter private List<Map.Entry<String, List<String>>> groups;
    @Getter private List<String> reclaimed;

    public ReclaimFile() {
        super("reclaim.yml");

        groups = new ArrayList<>();
        reclaimed = new ArrayList<>();
    }

    public void loadGroups() {
        if(config.contains("reclaim")) {
            for(String key : config.getConfigurationSection("reclaim").getKeys(false))
                groups.add(new AbstractMap.SimpleEntry<>(key, config.getStringList("reclaim." + key + ".commands")));
        } else {
            config.createSection("reclaim");
            save();
        }
    }

    public void loadReclaimed() {
        if(config.contains("reclaimed")) {
            reclaimed = config.getStringList("reclaimed");
        } else {
            config.createSection("reclaimed");
            save();
        }
    }

    public void saveReclaimed() {
        config.set("reclaimed", reclaimed);
        save();
        reload();
    }
}
