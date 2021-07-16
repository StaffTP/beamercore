package me.hulipvp.hcf.backend.files;

import lombok.Getter;
import me.hulipvp.hcf.utils.LocUtils;
import org.bukkit.Location;

public class LocationsFile extends ConfigFile {
    
    @Getter private Location endSpawn, endExit, netherSpawn, netherExit;

    public LocationsFile() {
        super("locations.yml");
    }

    public void init() {
        String endSpawn = this.getConfig().getString("locations.end.spawn");
        String endExit = this.getConfig().getString("locations.end.exit");
        String netherSpawn = this.getConfig().getString("locations.nether.exit");
        String netherExit = this.getConfig().getString("locations.nether.exit");

        if(endSpawn != null)
            this.endSpawn = (endSpawn.length() > 0) ? LocUtils.stringToLocation(endSpawn) : null;

        if(endExit != null)
            this.endExit = (endExit.length() > 0) ? LocUtils.stringToLocation(endExit) : null;

        if(netherSpawn != null)
            this.netherSpawn = (netherSpawn.length() > 0) ? LocUtils.stringToLocation(netherSpawn) : null;

        if(netherExit != null)
            this.netherExit = (netherExit.length() > 0) ? LocUtils.stringToLocation(netherExit) : null;
    }

    public void setEndSpawn( Location location) {
        this.endSpawn = location;

        this.getConfig().set("locations.end.spawn", LocUtils.locationToString(location));
    }


    public void setEndExit( Location location) {
        this.endExit = location;

        this.getConfig().set("locations.end.exit", LocUtils.locationToString(location));
    }

    public void setNetherSpawn( Location location) {
        this.netherSpawn = location;

        this.getConfig().set("locations.nether.spawn", LocUtils.locationToString(location));
    }

    public void setNetherExit( Location location) {
        this.netherExit = location;

        this.getConfig().set("locations.nether.exit", LocUtils.locationToString(location));
    }
}
