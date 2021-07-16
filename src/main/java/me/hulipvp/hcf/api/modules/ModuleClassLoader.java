package me.hulipvp.hcf.api.modules;

import lombok.Getter;
import me.hulipvp.hcf.api.modules.ex.InvalidModuleException;
import me.hulipvp.hcf.api.modules.ex.ModuleLoadException;
import me.hulipvp.hcf.HCF;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ModuleClassLoader extends URLClassLoader {

    @Getter private JavaModuleLoader loader;

    @Getter private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    @Getter private final Module module;
    @Getter private final ModuleInfoFile info;

    private Module moduleInit;
    private IllegalStateException state;

    static {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable");
            if(method != null) {
                boolean oldAccessible = method.isAccessible();

                method.setAccessible(true);
                method.invoke(null);
                method.setAccessible(oldAccessible);
                HCF.getInstance().getLogger().info("[HCF] Successfully set ModuleClassLoader as parallel capable");
            }
        } catch(Exception e) {
            HCF.getInstance().getLogger().log(Level.WARNING, "Error setting ModuleClassLoader as parallel capable", e);
        }
    }

    public ModuleClassLoader(ClassLoader parent, JavaModuleLoader loader, ModuleInfoFile info, File file) throws InvalidModuleException, MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.loader = loader;
        this.info = info;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(info.getMain(), true, this);
            } catch(ClassNotFoundException e) {
                throw new InvalidModuleException("Module " + info.getName() + "'s main class does not exist. (" + info.getMain() + ")", e);
            }

            Class<? extends Module> mainClass;
            try {
                mainClass = jarClass.asSubclass(Module.class);
            } catch(ClassCastException e) {
                throw new InvalidModuleException("Module " + info.getName() + "'s main class does not extend Module. (" + info.getMain() + ")", e);
            }

            this.module = mainClass.newInstance();
        } catch(IllegalAccessException e) {
            throw new InvalidModuleException("Module " + info.getName() + " doesn't have a public constructor", e);
        } catch(InstantiationException e) {
            throw new InvalidModuleException("Module " + info.getName() + " is not a proper type.", e);
        }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return this.findClass(name, true);
    }

    public Class<?> findClass(String name, boolean global) throws ClassNotFoundException {
        Class<?> result = this.classes.get(name);
        if(result == null) {
            if(global)
                result = this.loader.getClassByName(name);

            if(result == null) {
                result = super.findClass(name);
                if(result != null)
                    this.loader.setClass(name, result);
            }

            this.classes.put(name, result);
        }

        return result;
    }

    public void instate(Module module) {
        if(module.getClass().getClassLoader() != this)
            throw new ModuleLoadException(module.getInfo().getName(), "Cannot initialize module outside of ModuleClassLoader.");

        if(this.module != null || this.moduleInit != null)
            throw new IllegalArgumentException("Module already instated.", this.state);

        this.state = new IllegalStateException("Already instated");
        this.moduleInit = module;

        module.instate(this.loader, this.info, this);
    }
}
