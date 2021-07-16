package me.hulipvp.hcf.utils;

import me.hulipvp.hcf.game.player.data.PlayerInv;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InvUtils {

    public static String invToString(PlayerInv inv) {
        if(inv == null)
            return "null";

        StringBuilder builder = new StringBuilder();
        ItemStack[] armor = inv.getArmor();

        for(int i = 0; i < armor.length; i++) {
            if(i == 3) {
                if(armor[i] == null)
                    builder.append(itemStackToString(new ItemStack(Material.AIR)));
                else
                    builder.append(itemStackToString(armor[3]));
            } else {
                if(armor[i] == null)
                    builder.append(itemStackToString(new ItemStack(Material.AIR))).append(";");
                else
                    builder.append(itemStackToString(armor[i])).append(";");
            }
        }

        builder.append("|");

        for(int i = 0; i < inv.getItems().length; ++i)
            builder.append(i).append("#").append(itemStackToString(inv.getItems()[i])).append((i == inv.getItems().length - 1) ? "" : ";");

        return builder.toString();
    }

    public static PlayerInv invFromString(String in) {
        if(in == null || in.equals("unset") || in.equals("null") || in.equals("'null'"))
            return null;

        PlayerInv inv = new PlayerInv();
        String[] data = in.split("\\|");
        ItemStack[] armor = new ItemStack[data[0].split(";").length];

        for(int i = 0; i < data[0].split(";").length; ++i)
            armor[i] = itemStackFromString(data[0].split(";")[i]);

        inv.setArmor(armor);

        ItemStack[] contents = new ItemStack[data[1].split(";").length];

        for(String s : data[1].split(";")) {
            int slot = Integer.parseInt(s.split("#")[0]);

            if(s.split("#").length == 1)
                contents[slot] = null;
            else
                contents[slot] = itemStackFromString(s.split("#")[1]);
        }

        inv.setItems(contents);

        return inv;
    }

    public static String itemStackToString(ItemStack item) {
        StringBuilder builder = new StringBuilder();

        if(item != null) {
            String isType = String.valueOf(item.getType().getId());
            builder.append("t@").append(isType);

            if(item.getDurability() != 0) {
                String isDurability = String.valueOf(item.getDurability());
                builder.append(":d@").append(isDurability);
            }

            if(item.getAmount() != 1) {
                String isAmount = String.valueOf(item.getAmount());
                builder.append(":a@").append(isAmount);
            }

            Map<Enchantment, Integer> isEnch = item.getEnchantments();

            if(isEnch.size() > 0) {
                for(Map.Entry<Enchantment, Integer> ench : isEnch.entrySet())
                    builder.append(":e@").append(ench.getKey().getId()).append("@").append(ench.getValue());
            }

            if(item.hasItemMeta()) {
                ItemMeta imeta = item.getItemMeta();
                if(imeta.hasDisplayName())
                    builder.append(":dn@").append(imeta.getDisplayName());
                if(imeta.hasLore())
                    builder.append(":l@").append(imeta.getLore());
            }
        }

        return builder.toString();
    }

    public static ItemStack itemStackFromString(String in) {
        ItemStack item = null;
        ItemMeta meta = null;
        String[] split;

        if(in.equals("null"))
            return new ItemStack(Material.AIR);

        split = in.split(":");

        for(String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String s2 = itemAttribute[0];

            switch(s2) {
                case "t": {
                    item = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    meta = item.getItemMeta();
                    break;
                }
                case "d": {
                    if(item != null)
                        item.setDurability(Short.valueOf(itemAttribute[1]));
                    break;
                }
                case "a": {
                    if(item != null)
                        item.setAmount(Integer.valueOf(itemAttribute[1]));
                    break;
                }
                case "e": {
                    if(meta == null && item != null)
                        meta = item.getItemMeta();

                    if(meta != null)
                        meta.addEnchant(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]), true);
                    break;
                }
                case "dn": {
                    if(meta != null)
                        meta.setDisplayName(itemAttribute[1]);
                    break;
                }
                case "l": {
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");

                    List<String> lore = Arrays.asList(itemAttribute[1].split(","));

                    for(int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);

                        if(s != null) {
                            if(s.toCharArray().length != 0) {
                                if(s.charAt(0) == ' ')
                                    s = s.replaceFirst(" ", "");

                                lore.set(x, s);
                            }
                        }
                    }

                    if(meta != null) {
                        meta.setLore(lore);
                        break;
                    }

                    break;
                }
            }
        }

        if(meta != null)
            item.setItemMeta(meta);

        return item;
    }

    public static void fillSidesWithItem(Inventory inv, ItemStack item) {
        int size = inv.getSize();
        int rows = size / 9;

        if(rows >= 3) {
            for(int i = 0; i <= 8; i++)
                inv.setItem(i, item);

            for(int s = 8; s < (inv.getSize() - 9); s += 9) {
                int lastSlot = s + 1;
                inv.setItem(s, item);
                inv.setItem(lastSlot, item);
            }

            for(int lr = (inv.getSize() - 9); lr < inv.getSize(); lr++)
                inv.setItem(lr, item);
        }
    }

    public static void addItems(Player player, ItemStack... itemStacks) {
        Map<Integer, ItemStack> itemMap = player.getInventory().addItem(itemStacks);
        if(!itemMap.isEmpty()) {
            itemMap.values().forEach(itemStack -> {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            });
        }
    }

    public static Integer getAmount(Player player, Material material) {
        int amount = 0;
        ItemStack[] itemStacks = player.getInventory().getContents();

        for(ItemStack itemStack : itemStacks) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            if(itemStack.getType() == material)
                amount += itemStack.getAmount();
        }

        return amount;
    }

    public static List<ItemStack> getAllFromMaterial(Player player, Material material, int amount) {
        List<ItemStack> items = new ArrayList<>();
        ItemStack[] itemStacks = player.getInventory().getContents();

        for(ItemStack itemStack : itemStacks) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            if(itemStack.getType() == material)
                items.add(itemStack);
        }

        return items;
    }

    public static void removeItem(Inventory inventory, Material type, short data, int quantity) {
        ItemStack[] contents = inventory.getContents();
        boolean compareDamage = type.getMaxDurability() == 0;
        for (int i = quantity; i > 0; --i) {
            for (ItemStack content : contents) {
                if (content != null) {
                    if (content.getType() == type) {
                        if (!compareDamage || content.getData().getData() == data) {
                            if (content.getAmount() <= 1) {
                                inventory.removeItem(new ItemStack[]{content});
                                break;
                            }
                            content.setAmount(content.getAmount() - 1);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static Integer getAmount(Player player, ItemStack item) {
        int amount = 0;
        ItemStack[] itemStacks = player.getInventory().getContents();

        for(ItemStack itemStack : itemStacks) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            if(itemStack.isSimilar(item))
                amount += itemStack.getAmount();
        }

        return amount;
    }

    public static List<ItemStack> getAllFromStack(Player player, ItemStack item) {
        List<ItemStack> items = new ArrayList<>();
        ItemStack[] itemStacks = player.getInventory().getContents();

        for(ItemStack itemStack : itemStacks) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            if(itemStack.isSimilar(item))
                items.add(itemStack);
        }

        return items;
    }

    public static Inventory createInventory(Player player, Player target) {
        Inventory inv = Bukkit.createInventory(null, 54, "Inventory preview");

        ItemStack[] contents = target.getInventory().getContents();
        ItemStack[] armor = target.getInventory().getArmorContents();

        inv.setContents(contents);

        inv.setItem(45, armor[0]);
        inv.setItem(46, armor[1]);
        inv.setItem(47, armor[2]);
        inv.setItem(48, armor[3]);

        inv.setItem(36, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(37, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(38, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(39, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(40, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(41, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(42, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(43, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(44, createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(49, createGlass(ChatColor.RED + "Inventory Preview"));

        inv.setItem(50, createItem(Material.SPECKLED_MELON, ChatColor.RED + "Health", (int) ((Damageable) target).getHealth()));
        inv.setItem(51, createItem(Material.GRILLED_PORK, ChatColor.RED + "Hunger", target.getFoodLevel()));
        inv.setItem(52, createSkull(target, ChatColor.GREEN + target.getName()));
        inv.setItem(53, createWool(ChatColor.RED + "Close Preview", 14));

        return inv;
    }

    public static ItemStack createItem(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);

        return item;
    }

    public static ItemStack createGlass(String name) {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);

        return item;
    }

    public static ItemStack createWool(String name, int value) {
        ItemStack item = new ItemStack(Material.WOOL, 1, (short) value);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);
        return item;
    }

    public static ItemStack createSkull(Player player, String name) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
        skullmeta.setDisplayName(name);
        skullmeta.setOwner(player.getName());
        item.setItemMeta(skullmeta);

        return item;
    }
}
