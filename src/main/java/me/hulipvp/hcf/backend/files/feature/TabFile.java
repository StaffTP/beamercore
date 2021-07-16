package me.hulipvp.hcf.backend.files.feature;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.backend.files.FeatureFile;
import me.hulipvp.hcf.utils.ui.Tab;

public class TabFile extends FeatureFile {

    @Getter private static Tab tab;

    public TabFile() {
        super("tab.yml", HCF.getInstance());

        tab = new Tab(config.getBoolean("tab.enabled"),
                config.getString("tab.title"),
                config.getString("tab.header"),
                config.getString("tab.footer"),
                config.getStringList("tab.left-rows"),
                config.getStringList("tab.center-rows"),
                config.getStringList("tab.right-rows"));
    }
}
