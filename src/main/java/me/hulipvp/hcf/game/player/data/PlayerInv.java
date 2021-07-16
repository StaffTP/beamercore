package me.hulipvp.hcf.game.player.data;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.utils.InvUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInv {
    
    @Getter @Setter private ItemStack[] items;
    @Getter @Setter private ItemStack[] armor;
    @Getter @Setter private GameMode gameMode;

    public PlayerInv() { }

    public PlayerInv(ItemStack[] items, ItemStack[] armor) {
        this.items = items;
        this.armor = armor;
    }

    public static PlayerInv fromPlayer(PlayerInventory inv) {
        return new PlayerInv(inv.getContents(), inv.getArmorContents());
    }

    public ItemStack getHelmet() {
        return this.armor[0];
    }

    public ItemStack getChestPlate() {
        return this.armor[1];
    }

    public ItemStack getLeggings() {
        return this.armor[2];
    }

    public ItemStack getBoots() {
        return this.armor[3];
    }

    public ItemStack getSword() {
        return this.items[0];
    }

    public void load(Player player) {
        player.getInventory().clear();

        player.getInventory().setContents(items);
        player.getInventory().setArmorContents(armor);

        player.updateInventory();
    }

    @Override
    public String toString() {
        return InvUtils.invToString(this);
    }
}
