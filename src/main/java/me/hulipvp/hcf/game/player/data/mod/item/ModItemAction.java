package me.hulipvp.hcf.game.player.data.mod.item;

public enum ModItemAction {

    /**
     * Unhandled: Other plugin handles it's usage or that it is just an item. (Ex. WorldEdit Wand, Better View Item)
     * Inspector: Inspect a player's inventory.
     * Freeze: Freeze a player.
     * Random Tp: Randomly teleport to a player.
     * Online Staff: Show a GUI with online staff members.
     * Vanish On: Set vanish mode to on. (Hidden)
     * Vanish Off: Set vanish mode to off. (Visible)
     * Vanish Toggle: Toggle vanish mode.
     */
    UNHANDLED,
    INSPECTOR,
    FREEZE,
    RANDOM_TP,
    ONLINE_STAFF,
    VANISH_ON,
    VANISH_OFF,
    VANISH_TOGGLE
}
