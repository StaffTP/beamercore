package me.hulipvp.hcf.game.kits.type.item;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.kits.type.BardKit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BardItem {

    @Getter private final String key;
    @Getter private final String displayName;
    @Getter private final int itemId;
    @Getter private final int energy;
    @Getter private boolean applyOnBard = false;
    @Getter private boolean otherFaction = false;
    @Getter private PotionEffect finalEffect;

    public BardItem(String key) {
        ConfigurationSection section = HCF.getInstance().getConfig().getConfigurationSection("kits.bard.items." + key);

        this.key = key;
        this.displayName = section.getString("displayName");
        this.itemId = section.getInt("itemId");
        this.energy = section.getInt("energy");
        this.applyOnBard = section.getBoolean("applyOnBard");
        this.otherFaction = section.getBoolean("otherFaction");
        this.finalEffect = new PotionEffect(PotionEffectType.getByName(section.getString("potionEffect")), section.getInt("potionDuration"), section.getInt("potionAmplifier"));

        if (section.getBoolean("hold")) {
            BardKit.getHeldItems().put(this.getKey(), this);
        } else {
            BardKit.getClickItems().put(this.getKey(), this);
        }
    }
}
