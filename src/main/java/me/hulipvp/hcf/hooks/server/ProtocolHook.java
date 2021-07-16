package me.hulipvp.hcf.hooks.server;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.hooks.server.protocollib.PLib1_7;
import me.hulipvp.hcf.hooks.server.protocollib.PLib1_8;

public class ProtocolHook {

    public ProtocolHook() {
        String bukkitVersion = HCF.getInstance().getBukkitVersion();
        if(bukkitVersion.contains("1_7"))
            new PLib1_7();
        else
            new PLib1_8();
    }
}
