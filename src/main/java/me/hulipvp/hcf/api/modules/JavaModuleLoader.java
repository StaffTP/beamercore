package me.hulipvp.hcf.api.modules;

import me.hulipvp.hcf.api.modules.ex.InvalidModuleException;
import me.hulipvp.hcf.api.modules.ex.InvalidModuleInfoException;
import me.hulipvp.hcf.HCF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class JavaModuleLoader {
    
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final Map<String, ModuleClassLoader> loaders = new LinkedHashMap<>();

    public Module loadModule(File file)
            throws InvalidModuleException {
        if(!file.exists())
            throw new InvalidModuleException(new FileNotFoundException(file.getPath() + " does not exist"));

        ModuleInfoFile information;

        try {
            information = this.getModuleInfoFile(file);
        } catch(InvalidModuleInfoException ex) {
            throw new InvalidModuleException(ex);
        }

        ModuleClassLoader loader;

        try {
            loader = new ModuleClassLoader(this.getClass().getClassLoader(), this, information, file);
        } catch(InvalidModuleException ex) {
            throw ex;
        } catch(Throwable ex) {
            throw new InvalidModuleException(ex);
        }

        this.loaders.put(information.getName(), loader);

        return loader.getModule();
    }


    public void enableModule(Module module) {
        if(!module.isEnabled()) {
            if(!this.loaders.containsKey(module.getInfo().getName()))
                this.loaders.put(module.getInfo().getName(), (ModuleClassLoader) module.getClassLoader());

            try {
                module.setEnabled(true);
            } catch(Throwable e) {
                HCF.getInstance().getLogger().log(Level.SEVERE, "Error occurred while enabling " + module.getInfo().getName(), e);
            }
        }
    }


    public void disableModule(Module module) {
        if(module.isEnabled()) {
            ClassLoader classLoader = module.getClassLoader();
            try {
                module.setEnabled(false);
                module.kill();
            } catch(Throwable e) {
                HCF.getInstance().getLogger().log(Level.SEVERE, "Error occurred while disabling " + module.getInfo().getName(), e);
            }

            this.loaders.remove(module.getInfo().getName());
            if(!(classLoader instanceof ModuleClassLoader))
                return;

            ModuleClassLoader loader = (ModuleClassLoader) classLoader;
            Set<String> names = loader.getClasses().keySet();

            names.forEach(this::removeClass);
        }
    }

    public ModuleInfoFile getModuleInfoFile(File file)
            throws InvalidModuleInfoException {
        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("info.yml");
            if(entry == null)
                throw new InvalidModuleInfoException(new FileNotFoundException("Module file does not contain info.yml"));

            stream = jar.getInputStream(entry);

            return new ModuleInfoFile(stream);
        } catch(IOException e) {
            throw new InvalidModuleInfoException(e);
        } finally {
            if(jar != null) {
                try {
                    jar.close();
                } catch(IOException ignored) { }
            }

            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ignored) { }
            }
        }
    }

    public Class<?> getClassByName(String name) {
        Class<?> cachedClass = this.classes.get(name);
        if(cachedClass != null)
            return cachedClass;

        for(String current : this.loaders.keySet()) {
            ModuleClassLoader loader = this.loaders.get(current);

            try {
                cachedClass = loader.findClass(name, false);
            } catch(ClassNotFoundException ignored) { }

            if(cachedClass != null)
                return cachedClass;
        }

        return null;
    }

    public void setClass(String name, Class<?> clazz) {
        if(!this.classes.containsKey(name))
            this.classes.put(name, clazz);
    }

    private void removeClass(String name) {
        this.classes.remove(name);
    }
}
