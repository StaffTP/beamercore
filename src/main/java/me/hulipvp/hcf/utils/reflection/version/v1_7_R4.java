package me.hulipvp.hcf.utils.reflection.version;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.reflection.NMS;
import org.bukkit.plugin.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.event.*;

public class v1_7_R4 implements NMS, Listener
{
    private HCF plugin;
    
    @Override
    public void registerDeathListener(final HCF plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (player.isDead()) {
                    ((CraftPlayer)player).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
                }
            }
        });
    }
}
