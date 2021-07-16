package me.hulipvp.hcf.backend;

import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;

public interface IBackend {

    /**
     * Called when the plugin is being disabled.
     */
    void close();

    // Profile //
    void createProfile(HCFProfile profile);

    void deleteProfile(HCFProfile profile);

    void deleteProfiles();

    void saveProfile(HCFProfile profile);

    void saveProfileSync(HCFProfile profile);

    void loadProfile(HCFProfile profile);

    void loadProfiles();
    // End Profile //

    // Factions //
    void createFaction(Faction faction);

    void deleteFaction(Faction faction);

    void deleteFactions();

    void saveFaction(Faction faction);

    void saveFactionSync(Faction faction);

    void loadFactions();
    // End Factions //

    // Koths //
    void createKoth(Koth koth);

    void deleteKoth(Koth koth);

    void saveKoth(Koth koth);

    void saveKothSync(Koth koth);

    void loadKoths();
    // End Koths //

    // Conquests //
    void createConquest(Conquest conquest);

    void deleteConquest(Conquest conquest);

    void saveConquest(Conquest conquest);

    void saveConquestSync(Conquest conquest);

    void loadConquests();
    // End Conquests //

    // DTCs //
    void createDTC(DTC dtc);

    void deleteDTC(DTC dtc);

    void saveDTC(DTC dtc);

    void saveDTCSync(DTC dtc);

    void loadDTCs();
    // End DTCs //

    // Mountains //
    void createMountain(Mountain mountain);

    void deleteMountain(Mountain mountain);

    void saveMountain(Mountain mountain);

    void saveMountainSync(Mountain mountain);

    void loadMountains();
    // End Mountains //
}
