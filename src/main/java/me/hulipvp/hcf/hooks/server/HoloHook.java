package me.hulipvp.hcf.hooks.server;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.hooks.server.holograms.FTOPNameHologramReplacer;
import me.hulipvp.hcf.hooks.server.holograms.FTOPValueHologramReplacer;
import org.bukkit.Bukkit;

public class HoloHook {

    public HoloHook(HCF plugin) {
        for (int i = 1; i <= 10; i++) {
            HologramsAPI.registerPlaceholder(plugin, ("%hcf_top_name_" + i + "%"), 30, new FTOPNameHologramReplacer(i));
        }
        for (int i = 1; i <= 10; i++) {
            HologramsAPI.registerPlaceholder(plugin, ("%hcf_top_value_" + i + "%"), 30, new FTOPValueHologramReplacer(i));
        }
        Bukkit.getLogger().info("[HCF] Successfully hooked into HolographicDisplays.");
    }

}
