package me.hulipvp.hcf.api.modules;

public interface IModule {

    /**
     * Called when the module has started loading
     */
    void onLoad();

    /**
     * Called when the module is done loading
     */
    void onEnable();

    /**
     * Called when the module gets disabled... duh
     */
    void onDisable();
}
