package me.hulipvp.hcf.blockshop;




import me.hulipvp.hcf.blockshop.shops.ClayShop;
import me.hulipvp.hcf.blockshop.shops.GlassShop;
import me.hulipvp.hcf.blockshop.shops.WoodShop;
import me.hulipvp.hcf.blockshop.shops.WoolShop;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.ItemBuilder;
import me.hulipvp.hcf.utils.menu.Button;
import me.hulipvp.hcf.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 6/4/2021 / 12:38 AM
 * vhcf / com.cavepvp.hcf.blockshop
 */
public class BlockShop extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&d&lBlock Shop";
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


        ItemStack woolshop = new ItemBuilder(Material.WOOL)
                .name(CC.translate("&d&lWool Shop"))
                .lore(CC.translate(Arrays.asList("&7Click to open the woolshop!"))).build();

        ItemStack glassshop = new ItemBuilder(Material.GLASS)
                .name(CC.translate("&d&lGlass Shop"))
                .lore(CC.translate(Arrays.asList("&7Click to open the glassshop!"))).build();

        ItemStack woodshop = new ItemBuilder(Material.LOG)
                .name(CC.translate("&d&lWood Shop"))
                .lore(CC.translate(Arrays.asList("&7Click to open the woodshop!"))).build();

        ItemStack clayshop = new ItemBuilder(Material.CLAY)
                .name(CC.translate("&d&lClay Shop"))
                .lore(CC.translate(Arrays.asList("&7Click to open the clayshop!"))).build();


        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return woolshop;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.closeInventory();
                new WoolShop().openMenu(player);
            }

        });

        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return glassshop;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.closeInventory();
                new GlassShop().openMenu(player);
            }

        });

        buttons.put(14, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return woodshop;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.closeInventory();
                new WoodShop().openMenu(player);
            }

        });

        buttons.put(16, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return clayshop;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.closeInventory();
                new ClayShop().openMenu(player);
            }

        });

        return buttons;
    }
}
