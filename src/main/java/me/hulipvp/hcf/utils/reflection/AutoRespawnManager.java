package me.hulipvp.hcf.utils.reflection;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.HCF;
import org.bukkit.Bukkit;

public class AutoRespawnManager {

    @Getter @Setter
    private NMS nmsAccess;
    @Getter
    private boolean isEnabled = false;

    public void enable() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            final Class<?> nmsClass = Class.forName("me.hulipvp.hcf.utils.reflection.version." + version);
            if (NMS.class.isAssignableFrom(nmsClass)) {
                this.nmsAccess = (NMS)nmsClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                this.isEnabled = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.isEnabled = false;
        }
        if (this.isEnabled) {
            this.nmsAccess.registerDeathListener(HCF.getInstance());
        }
    }

}
