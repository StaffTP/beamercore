package me.hulipvp.hcf.backend.files;

import me.hulipvp.hcf.HCF;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class FeatureFile {

    private File file;
    public YamlConfiguration config;

    public FeatureFile(String name, HCF plugin) {
        name += (name.endsWith(".yml") ? "" : ".yml");
        name = "features" + File.separator + name;

        file = new File(plugin.getDataFolder() + File.separator + name);
        if(!file.exists()) {
            try {
                plugin.getLogger().info("[HCF] " + name + " doesn't exist, now creating...");

                file.getParentFile().mkdirs();
                if(plugin.getResource(name) != null) {
                    plugin.saveResource(name, false);
                    HCF.getInstance().getLogger().info("[HCF] Successfully created " + file + ". (Resource)");
                } else {
                    if(file.createNewFile())
                        HCF.getInstance().getLogger().info("[HCF] Successfully created " + file + ". (File)");
                }

                if(file.exists())
                    plugin.getLogger().info("[HCF] " +name + " has successfully been created. (Exists)");
                else
                    throw new IOException("[HCF] Unable to create " + name);
            } catch(IOException ex) {
                plugin.getLogger().severe("[HCF] " + name + " wasn't able to be created: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public FeatureFile(String name) {
        this(name, HCF.getInstance());
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public boolean save() {
        try {
            this.getConfig().save(this.getFile());
            return true;
        } catch(IOException ignored) { }

        return false;
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }
}