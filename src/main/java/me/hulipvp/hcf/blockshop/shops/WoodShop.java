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
public class WoodShop extends Menu {

   private HashMap<Integer, Button> buttons = new HashMap<>();
   private FileConfiguration config = HCF.getInstance().getConfig();

    @Override
    public String getTitle(Player player) {
        return CC.translate("&d&lWood Shop");
    }


    public int getSize() {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HCFProfile profile = HCFProfile.getByPlayer(player);
        for (String key : HCF.getInstance().getConfig().getConfigurationSection("BlockShop.WoodShop.Items").getKeys(false)) {
            ItemStack woolshop;
            if (config.getBoolean("BlockShop.WoodShop.Items." + key + ".Data.Enabled")) {
                woolshop = new ItemStack(Material.valueOf(config.getString("BlockShop.WoodShop.Items." + key + ".Material")),
                        64,
                        (byte) config.getInt("BlockShop.WoodShop.Items." + key + ".Data.Data"));
            } else {
                woolshop = new ItemStack(Material.valueOf(config.getString("BlockShop.WoodShop.Items." + key + ".Material")),
                        64);
            }
            ItemMeta woolshopmeta = woolshop.getItemMeta();
            List<String> lore = CC.translate(config.getStringList("BlockShop.WoodShop.Items." + key + ".Lore"));
            woolshopmeta.setLore(lore);
            woolshopmeta.setDisplayName(CC.translate(config.getString("BlockShop.WoodShop.Items." + key + ".Name")));
            woolshop.setItemMeta(woolshopmeta);

            buttons.put(config.getInt("BlockShop.WoodShop.Items." + key + ".Slot"), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return woolshop;
                }
                public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                    int bal = profile.getBalance();

                    int price = config.getInt("BlockShop.WoodShop.Items." + key + ".Price");


                    if (bal < price) {
                        player.closeInventory();
                        player.sendMessage(CC.translate("&cYou have insufficient funds for this purchase!"));
                        return;
                    }
                    if (config.getBoolean("BlockShop.WoodShop.Items." + key + ".Data.Enabled")) {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(CC.translate("&cYour inventory is full!"));
                            player.closeInventory();
                            return;
                        }
                        player.getInventory().addItem(new ItemStack(Material.valueOf(config.getString("BlockShop.WoodShop.Items." + key + ".Material")),
                                64,
                                (byte) config.getInt("BlockShop.WoodShop.Items." + key + ".Data.Data")));
                    } else {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(CC.translate("&cYour inventory is full!"));
                            player.closeInventory();
                            return;
                        }
                        player.getInventory().addItem(new ItemStack(Material.valueOf(config.getString("BlockShop.WoodShop.Items." + key + ".Material")),
                                64));
                    }
                    profile.removeFromBalance(price);
                    profile.save();
                    player.sendMessage(CC.translate("&aYou have purchased this for &2$" + price));


                }


            });

        }
        buttons.put(16, new Button() {





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
