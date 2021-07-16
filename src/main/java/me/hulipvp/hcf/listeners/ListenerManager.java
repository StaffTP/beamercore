package me.hulipvp.hcf.listeners;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.listeners.refillstation.RefillListener;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.listeners.player.*;
import me.hulipvp.hcf.utils.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class ListenerManager {

    @Getter private PluginManager pluginManager;

    public ListenerManager() {
        this.pluginManager = Bukkit.getServer().getPluginManager();
    }

    public void registerListeners() {
        registerListener(new BlockListener());
        registerListener(new MenuListener());
        registerListener(new BorderListener());
        registerListener(new BrewingListener());
        registerListener(new CrowbarListener());
        registerListener(new ClaimingListener());
        registerListener(new DeathMessageListener());
        registerListener(new ElevatorListener());
        registerListener(new EventListener());
        registerListener(new RefillListener());
        registerListener(new FactionListener());
        if (ConfigValues.LISTENERS_COOK_SPEED_MULTIPLIER != 1) {
            registerListener(new FurnaceListener());
        }
        registerListener(new GlassListener());
        registerListener(new GlitchListener());
        registerListener(new ItemStatisticListener());
        registerListener(new ExpMultiplierListener());
        registerListener(new KitmapListener());
        registerListener(new LimiterListener());
        registerListener(new LoggerListener());
        registerListener(new PortalListener());
        registerListener(new ModListener());
        registerListener(new ModItemListener());
        registerListener(new PlayerListener());
        registerListener(new ServerListener());
        registerListener(new ShopSignListener());
        registerListener(new SubclaimListener());
        registerListener(new TimerListener());
        registerListener(new LunarClientListener());
    }

    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, HCF.getInstance());
    }
}
