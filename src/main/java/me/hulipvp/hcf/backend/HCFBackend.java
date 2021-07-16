package me.hulipvp.hcf.backend;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.HCF;

public abstract class HCFBackend implements IBackend {

    @Getter private final BackendType type;

    @Getter @Setter private boolean loaded;

    public HCFBackend(BackendType type) {
        this.type = type;
    }

    protected void logInfoMessage(String message) {
        HCF.getInstance().getLogger().info("(Backend) {" + this.getType().getVerboseName() + "} - " + message);
    }

    protected void logException(String message, Exception e) {
        HCF.getInstance().getLogger().severe("(Backend) {" + this.getType().getVerboseName() + "} - " + message);
        HCF.getInstance().getLogger().severe("-------------------------------------------");
        e.printStackTrace();
        HCF.getInstance().getLogger().severe("-------------------------------------------");
    }
}
