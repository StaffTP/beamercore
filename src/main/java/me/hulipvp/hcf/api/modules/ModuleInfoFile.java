package me.hulipvp.hcf.api.modules;

import lombok.Getter;
import me.hulipvp.hcf.api.modules.ex.InvalidModuleInfoException;
import me.hulipvp.hcf.HCF;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ModuleInfoFile {

    private static final Yaml YAML = new Yaml();

    @Getter private String name;
    @Getter private String rawName;
    @Getter private String version;
    @Getter private String main;

    public ModuleInfoFile(InputStream stream)
            throws InvalidModuleInfoException {
        this.loadMap(this.asMap(YAML.load(stream)));
    }

    private void loadMap(Map<?, ?> map)
            throws InvalidModuleInfoException {
        try {
            this.name = this.rawName = map.get("name").toString();

            if(!this.name.matches("^[A-Za-z0-9 _.-]+$"))
                throw new InvalidModuleInfoException("Module Name '" + this.name + "' contains invalid characters.");

            this.name = this.name.replace(' ', '_');
        } catch(NullPointerException e) {
            throw new InvalidModuleInfoException("Module name is null", e);
        } catch(ClassCastException e) {
            throw new InvalidModuleInfoException("Module name is not a string", e);
        }

        try {
            this.version = map.get("version").toString();
        } catch(NullPointerException e) {
            throw new InvalidModuleInfoException("Module version is null", e);
        } catch(ClassCastException e) {
            throw new InvalidModuleInfoException("Module version is not a string", e);
        }

        try {
            this.main = map.get("main").toString();

            if(this.main.startsWith(HCF.class.getPackage().getName()))
                throw new InvalidModuleInfoException("A module cannot be within the namespace of " + HCF.class.getPackage().getName());
        } catch(NullPointerException e) {
            throw new InvalidModuleInfoException("Main Class is null", e);
        } catch(ClassCastException e) {
            throw new InvalidModuleInfoException("Main Class is not a string", e);
        }
    }

    private Map<?, ?> asMap(Object object)
            throws InvalidModuleInfoException {
        if(object instanceof Map)
            return (Map<?, ?>) object;

        throw new InvalidModuleInfoException(object + " is not properly structured.");
    }
}
