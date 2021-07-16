package me.hulipvp.hcf.api.modules;

import lombok.Getter;
import me.hulipvp.hcf.api.modules.ex.InvalidModuleLoaderException;
import me.hulipvp.hcf.api.modules.ex.ModuleLoadException;
import me.hulipvp.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public abstract class Module implements IModule {

    @Getter private static HCF plugin = HCF.getInstance();

    @Getter private JavaModuleLoader loader;

    @Getter private boolean loaded, enabled;

    @Getter private ClassLoader classLoader;
    @Getter private ModuleInfoFile info;

    private Set<Object> commands;
    private Set<Listener> listeners;
    private Set<Integer> tasks;

    public Module() {
        ClassLoader loader = this.getClass().getClassLoader();
        if(!(loader instanceof ModuleClassLoader))
            throw new InvalidModuleLoaderException(this.getClass().getName());

        ((ModuleClassLoader) loader).instate(this);

        commands = new HashSet<>();
        listeners = new HashSet<>();
        tasks = new HashSet<>();
    }

    public void onLoad() { }

    public void onEnable() { }

    public void onDisable() { }

    public void kill() {
        unregisterCommands();
        unregisterListeners();
        unregisterTasks();
    }

    public void setEnabled(boolean enabled)
            throws ModuleLoadException {
        if(this.enabled == enabled)
            throw new ModuleLoadException(getInfo().getName(), "Attempted to enable when initialized.");

        this.enabled = enabled;

        if(enabled)
            this.onEnable();
        else
            this.onDisable();
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void registerCommand(Object object) {
        if(commands.add(object))
            HCF.getInstance().getCommandManager().registerCommands(object);
    }

    public void unregisterCommand(Object object) {
        if(commands.remove(object))
            HCF.getInstance().getCommandManager().unregisterCommands(object);
    }

    public void unregisterCommands() {
        commands.forEach(this::unregisterCommand);
        commands.clear();
    }

    public <T extends Listener> void registerListener(T t) {
        if(listeners.add(t))
            HCF.getInstance().getListenerManager().registerListener(t);
    }

    public <T extends Listener> void unregisterListener(T t) {
        if(listeners.remove(t))
            HandlerList.unregisterAll(t);
    }

    public void unregisterListeners() {
        listeners.forEach(this::unregisterListener);
        listeners.clear();
    }

    public void registerSyncRepeatingTask(Runnable runnable, long delay, long period) {
        registerTask(Bukkit.getScheduler().runTaskTimer(getPlugin(), runnable, delay, period));
    }

    public void registerAsyncRepeatingTask(Runnable runnable, long delay, long period) {
        registerTask(Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, delay, period));
    }

    public void registerTask(BukkitTask task) {
        tasks.add(task.getTaskId());
    }

    public void unregisterTask(BukkitTask task) {
        if(tasks.remove(task.getTaskId()))
            Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

    public void unregisterTasks() {
        tasks.forEach(Bukkit.getScheduler()::cancelTask);
        tasks.clear();
    }

    protected void instate(JavaModuleLoader loader, ModuleInfoFile info, ClassLoader classLoader) {
        this.loader = loader;
        this.info = info;
        this.classLoader = classLoader;
    }
}
