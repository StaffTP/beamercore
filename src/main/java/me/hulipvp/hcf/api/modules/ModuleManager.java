package me.hulipvp.hcf.api.modules;

import lombok.Getter;
import me.hulipvp.hcf.api.modules.ex.InvalidModuleException;
import me.hulipvp.hcf.api.modules.ex.InvalidModuleInfoException;
import me.hulipvp.hcf.HCF;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleManager {
    
    @Getter private final Map<String, Module> modules = new HashMap<>();
    @Getter private final Pattern pattern = Pattern.compile("\\.jar$");
    @Getter private final JavaModuleLoader loader;

    public ModuleManager() {
        this.loader = new JavaModuleLoader();
    }
    
    public Module loadModule(File file)
            throws InvalidModuleException {
        Module result = null;

        String name = file.getName();
        Matcher matcher = this.pattern.matcher(name);
        if(matcher.find())
            result = this.loader.loadModule(file);

        if(result != null) {
            this.modules.put(result.getInfo().getName(), result);

            result.onLoad();
            result.setLoaded(true);

            return result;
        }

        return null;
    }

    public boolean enableModule(Module module) {
        if(!module.isEnabled()) {
            try {
                module.getLoader().enableModule(module);

                return true;
            } catch(Throwable e) {
                HCF.getInstance().getLogger().log(Level.SEVERE, "Error occurred (in the module loader) while enabling " + module.getInfo().getName(), e);
            }
        }

        return false;
    }

    public void disableModule(Module module) {
        if(module.isEnabled()) {
            try {
                module.getLoader().disableModule(module);
            } catch(Throwable e) {
                HCF.getInstance().getLogger().log(Level.SEVERE, "Error occurred (in the module loader) while disabling " + module.getInfo().getName(), e);
            }
        }

        modules.remove(module.getInfo().getName());
    }

    public void loadModules() {
        File directory = new File(HCF.getInstance().getDataFolder() + File.separator + "modules");
        if(!directory.exists()) {
            if(directory.mkdir())
                Bukkit.getLogger().info("Created modules directory.");
            else
                Bukkit.getLogger().info("Failed to create modules directory.");
        }

        if(!directory.isDirectory())
            throw new IllegalStateException("Invalid directory.");

        Map<String, File> tempModules = new HashMap<>();
        for(File file : Objects.requireNonNull(directory.listFiles())) {
            ModuleInfoFile info;

            try {
                info = this.loader.getModuleInfoFile(file);
                String name = info.getName();

                if(name.equalsIgnoreCase("HCF")) {
                    HCF.getInstance().getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + ", Restricted Name.");
                    continue;
                }
            } catch(InvalidModuleInfoException e) {
                HCF.getInstance().getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "'", e);
                continue;
            }

            tempModules.put(info.getName(), file);
        }

        while(!tempModules.isEmpty()) {
            Iterator<String> ite = tempModules.keySet().iterator();

            while(ite.hasNext()) {
                String plugin = ite.next();
                File file = tempModules.get(plugin);
                ite.remove();

                try {
                    this.loadModule(file);
                } catch(InvalidModuleException e) {
                    HCF.getInstance().getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "'", e);
                }
            }
        }

        modules.values().forEach(this::enableModule);
    }

    public void disableModules() {
        modules.values().forEach(this::disableModule);
    }

    public Module getModule(String name) {
        return getModules().get(name);
    }
}
