package me.hulipvp.hcf.blockshop.shops;


import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.blockshop.BlockShop;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.menu.Button;
import me.hulipvp.hcf.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 6/4/2021 / 12:54 AM
 * vhcf / com.cavepvp.hcf.blockshop.shops
 */
public class ClayShop extends Menu {

    FileConfiguration config = HCF.getInstance().getConfig();
    private HashMap<Integer, Button> buttons = new HashMap<>();

    @Override
    public String getTitle(Player player) {
        return CC.translate("&d&lClay Shop");
    }

    public int getSize() {
        return 45;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HCFProfile profile = HCFProfile.getByPlayer(player);
        for (String key : HCF.getInstance().getConfig().getConfigurationSection("BlockShop.ClayShop.Items").getKeys(false)) {
            ItemStack clayshop;
            if (config.getBoolean("BlockShop.ClayShop.Items." + key + ".Data.Enabled")) {
                clayshop = new ItemStack(Material.valueOf(config.getString("BlockShop.ClayShop.Items." + key + ".Material")),
                        64,
                        (byte) config.getInt("BlockShop.ClayShop.Items." + key + ".Data.Data"));
            } else {
                clayshop = new ItemStack(Material.valueOf(config.getString("BlockShop.ClayShop.Items." + key + ".Material")),
                        64);
            }

            ItemMeta clayshopmeta = clayshop.getItemMeta();
            List<String> lore = CC.translate(config.getStringList("BlockShop.ClayShop.Items." + key + ".Lore"));
            clayshopmeta.setLore(lore);
            clayshopmeta.setDisplayName(CC.translate(config.getString("BlockShop.ClayShop.Items." + key + ".Name")));
            clayshop.setItemMeta(clayshopmeta);

            buttons.put(config.getInt("BlockShop.ClayShop.Items." + key + ".Slot"), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return clayshop;
                }

                public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                    int bal = profile.getBalance();

                    int price = config.getInt("BlockShop.ClayShop.Items." + key + ".Price");


                    if (bal < price) {
                        player.closeInventory();
                        player.sendMessage(CC.translate("&cYou have insufficient funds for this purchase!"));
                        return;
                    }
                    if (config.getBoolean("BlockShop.ClayShop.Items." + key + ".Data.Enabled")) {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(CC.translate("&cYour inventory is full!"));
                            player.closeInventory();
                            return;
                        }
                        player.getInventory().addItem(new ItemStack(Material.valueOf(config.getString("BlockShop.ClayShop.Items." + key + ".Material")),
                                64,
                                (byte) config.getInt("BlockShop.ClayShop.Items." + key + ".Data.Data")));
                    } else {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(CC.translate("&cYour inventory is full!"));
                            player.closeInventory();
                            return;
                        }
                        player.getInventory().addItem(new ItemStack(Material.valueOf(config.getString("BlockShop.ClayShop.Items." + key + ".Material")),
                                64));
                    }
                    profile.removeFromBalance(price);
                    profile.save();
                    player.sendMessage(CC.translate("&aYou have purchased this for &2$" + price));


                }


            });

        }

        buttons.put(31, new Button() {


            @Override
            public ItemStack getButtonItem(Player player) {

                ItemStack backButton = new ItemStack(Material.ARROW);
                ItemMeta backButtonMeta = backButton.getItemMeta();
                backButtonMeta.setDisplayName(CC.translate("&5» &dBack &5«"));
                backButton.setItemMeta(backButtonMeta);
                return backButton;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.closeInventory();
                new BlockShop().openMenu(player);

            }
        });


        return buttons;
    }
}
