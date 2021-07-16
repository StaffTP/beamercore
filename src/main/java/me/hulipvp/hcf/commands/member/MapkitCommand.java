package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.kits.MapKit;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.game.player.data.PlayerInv;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import me.hulipvp.hcf.utils.TimeUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.concurrent.TimeUnit;

public class MapkitCommand {

    private Inventory mapkitInv;

    public MapkitCommand() {
        buildMapKitInventory();
    }

    @Command(label = "mapkit", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        p.openInventory(mapkitInv);
    }

    @Command(label = "mapkit.help", permission = "mapkit.admin", playerOnly = true)
    public void onMapKitHelp(CommandData args) {
        Player p = args.getPlayer();
        p.sendMessage(Locale.COMMAND_MAPKIT_USAGE.toString());
    }

    @Command(label = "mapkit.create", permission = "mapkit.admin", playerOnly = true)
    public void onMapkitCreate(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() < 2) {
            p.sendMessage(Locale.COMMAND_MAPKIT_USAGE.toString());
        } else {
            String kit = args.getArg(1);
            if(HCF.getInstance().getKitsFile().isValidKit(kit)) {
                p.sendMessage(Locale.COMMAND_MAPKIT_ALREADY_EXISTS.toString());
                return;
            }

            MapKit mapKit = new MapKit(kit);
            mapKit.setPlayerInv(PlayerInv.fromPlayer(p.getInventory()));
            mapKit.setEnabled(true);
            mapKit.setColor(ChatColor.GREEN);

            HCF.getInstance().getKitsFile().getKits().put(kit, mapKit);
            HCF.getInstance().getKitsFile().saveKit(mapKit);
            HCF.getInstance().getKitsFile().save();
            HCF.getInstance().getKitsFile().reload();
            p.sendMessage(Locale.COMMAND_MAPKIT_CREATED.toString().replace("%type%", kit));
        }
    }

    @Command(label = "mapkit.delete", permission = "mapkit.admin", playerOnly = true)
    public void onMapkitDelete(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() < 2) {
            p.sendMessage(Locale.COMMAND_MAPKIT_USAGE.toString());
        } else {
            String kit = args.getArg(1);
            if(!HCF.getInstance().getKitsFile().isValidKit(kit)) {
                p.sendMessage(Locale.COMMAND_MAPKIT_ALREADY_EXISTS.toString());
                return;
            }

            HCF.getInstance().getKitsFile().getKits().remove(kit);
            HCF.getInstance().getKitsFile().saveKits();
            HCF.getInstance().getKitsFile().reload();
            p.sendMessage(Locale.COMMAND_MAPKIT_DELETED.toString().replace("%type%", kit));
        }
    }

    @Command(label = "mapkit.toggle", permission = "mapkit.admin", playerOnly = true)
    public void onMapkitToggle(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() < 2) {
            p.sendMessage(Locale.COMMAND_MAPKIT_USAGE.toString());
        } else {
            String kit = args.getArg(1);
            if(!HCF.getInstance().getKitsFile().isValidKit(kit)) {
                p.sendMessage(Locale.COMMAND_MAPKIT_INVALID_KIT.toString());
                return;
            }

            HCF.getInstance().getKitsFile().setEnabled(kit, !HCF.getInstance().getKitsFile().isEnabled(kit));
            p.sendMessage(Locale.COMMAND_MAPKIT_TOGGLED.toString().replace("%type%", kit)
                    .replace("%status%", HCF.getInstance().getKitsFile().isEnabled(kit) ? C.color("&aEnabled") : C.color("&cDisabled")));
        }
    }

    @Command(label = "mapkit.update", permission = "mapkit.admin", playerOnly = true)
    public void onMapkitSetinv(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() < 2) {
            p.sendMessage(Locale.COMMAND_MAPKIT_USAGE.toString());
        } else {
            String kit = args.getArg(1);
            if(!HCF.getInstance().getKitsFile().isValidKit(kit)) {
                p.sendMessage(Locale.COMMAND_MAPKIT_INVALID_KIT.toString());
                return;
            }

            HCF.getInstance().getKitsFile().setMapkitInv(kit, PlayerInv.fromPlayer(p.getInventory()));
            p.sendMessage(Locale.COMMAND_MAPKIT_UPDATED.toString().replace("%type%", kit));
        }
    }

    @Command(label = "mapkit.load", permission = "mapkit.admin", playerOnly = true)
    public void onMapkitLoadInv(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() < 2) {
            p.sendMessage(Locale.COMMAND_MAPKIT_USAGE.toString());
        } else {
            String kit = args.getArg(1);
            if(!HCF.getInstance().getKitsFile().isValidKit(kit)) {
                p.sendMessage(Locale.COMMAND_MAPKIT_INVALID_KIT.toString());
                return;
            }

            HCF.getInstance().getKitsFile().getMapkitInv(kit).load(p);
            p.sendMessage(Locale.COMMAND_MAPKIT_LOADED.toString().replace("%type%", kit));
        }
    }

    @Command(label = "mapkit.setcolor", permission = "mapkit.admin", playerOnly = true)
    public void onMapkitSetcolor(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() < 3) {
            p.sendMessage(Locale.COMMAND_MAPKIT_USAGE.toString());
        } else {
            String kit = args.getArg(1);
            if(!HCF.getInstance().getKitsFile().isValidKit(kit)) {
                p.sendMessage(Locale.COMMAND_MAPKIT_INVALID_KIT.toString());
                return;
            }

            ChatColor color;
            try {
                color = ChatColor.valueOf(args.getArg(2));
            } catch(Exception ex) {
                p.sendMessage(C.color("&cThat color is not a valid color."));
                return;
            }

            HCF.getInstance().getKitsFile().setColor(kit, color);
            p.sendMessage(Locale.COMMAND_MAPKIT_SET_COLOR.toString().replace("%type%", kit)
                .replace("%color%", color.toString() + WordUtils.capitalizeFully(color.name())));
        }
    }

    private void buildMapKitInventory() {
        mapkitInv = Bukkit.createInventory(null, 54, C.color(ConfigValues.SERVER_PRIMARY + "Mapkit"));

        ItemStack outline = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7))
                .name("")
                .get();

        Integer protLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        Integer unbreakLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.DURABILITY);
        Integer fallLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.PROTECTION_FALL);
        Integer sharpLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.DAMAGE_ALL);
        Integer fireLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.FIRE_ASPECT);
        Integer powerLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.ARROW_DAMAGE);
        Integer punchLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.ARROW_KNOCKBACK);
        Integer flameLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.ARROW_FIRE);
        Integer infitityLimit = ConfigValues.LIMITERS_ENCHANTS.get(Enchantment.ARROW_INFINITE);

        PotionType poisonType = PotionType.getByEffect(PotionEffectType.getByName("POISON"));
        String[] poisonInfo = ((ConfigValues.LIMITERS_POTIONS.get(poisonType) == null) ? new String[]{ "1", "0:33" } : ConfigValues.LIMITERS_POTIONS.get(poisonType).split(";"));

        PotionType slownessType = PotionType.getByEffect(PotionEffectType.getByName("SLOWNESS"));
        String[] slownessInfo = ((ConfigValues.LIMITERS_POTIONS.get(slownessType) == null) ? new String[]{ "1", "1:07" } : ConfigValues.LIMITERS_POTIONS.get(slownessType).split(";"));

        ItemStack helmet = new ItemBuilder(Material.DIAMOND_HELMET)
                .name("&bDiamond Helmet")
                .lore(
                        "&7Protection " + ((protLimit == null) ? 4 : protLimit),
                        "&7Unbreaking " + ((unbreakLimit == null) ? 3 : unbreakLimit)
                )
                .get();

        ItemStack chestplate = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .name("&bDiamond Chestplate")
                .lore(
                        "&7Protection " + ((protLimit == null) ? 4 : protLimit),
                        "&7Unbreaking " + ((unbreakLimit == null) ? 3 : unbreakLimit)
                )
                .get();

        ItemStack leggings = new ItemBuilder(Material.DIAMOND_LEGGINGS)
                .name("&bDiamond Leggings")
                .lore(
                        "&7Protection " + ((protLimit == null) ? 4 : protLimit),
                        "&7Unbreaking " + ((unbreakLimit == null) ? 3 : unbreakLimit)
                )
                .get();

        ItemStack boots = new ItemBuilder(Material.DIAMOND_BOOTS)
                .name("&bDiamond Boots")
                .lore(
                        "&7Protection " + ((protLimit == null) ? 4 : protLimit),
                        "&7Unbreaking " + ((unbreakLimit == null) ? 3 : unbreakLimit),
                        "&7Feather Falling " + ((fallLimit == null) ? 4 : fallLimit)
                )
                .get();

        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD)
                .name("&bDiamond Sword")
                .lore(
                        "&7Sharpness " + ((sharpLimit == null) ? 5 : sharpLimit),
                        "&7Fire Aspect " + ((fireLimit == null) ? 2 : fireLimit),
                        "&7Unbreaking " + ((unbreakLimit == null) ? 3 : unbreakLimit)
                )
                .get();

        ItemStack bow = new ItemBuilder(Material.BOW)
                .name("&bBow")
                .lore(
                        "&7Power " + ((powerLimit == null) ? 5 : powerLimit),
                        "&7Punch " + ((punchLimit == null) ? 2 : punchLimit),
                        "&7Flame " + ((flameLimit == null) ? 1 : flameLimit),
                        "&7Infinity " + ((infitityLimit == null) ? 1 : infitityLimit),
                        "&7Unbreaking " + ((unbreakLimit == null) ? 3 : unbreakLimit)
                )
                .get();

        ItemStack poison = new ItemBuilder(Material.POTION)
                .name("&bPoison")
                .durability(16388)
                .lore(
                        "",
                        "&7Max Level: " + poisonInfo[0],
                        "&7Max Duration: " + (poisonInfo[1].contains(":") ? poisonInfo[1] : TimeUtils.getFormatted(TimeUnit.SECONDS.toMillis(Long.valueOf(poisonInfo[1]))))
                )
                .get();

        ItemStack slowness = new ItemBuilder(Material.POTION)
                .name("&bSlowness")
                .durability(16394)
                .lore(
                        "",
                        "&7Max Level: " + slownessInfo[0],
                        "&7Max Duration: " + (slownessInfo[1].contains(":") ? slownessInfo[1] : TimeUtils.getFormatted(TimeUnit.SECONDS.toMillis(Long.valueOf(slownessInfo[1]))))
                )
                .get();

        mapkitInv.setItem(13, helmet);
        mapkitInv.setItem(22, chestplate);
        mapkitInv.setItem(31, leggings);
        mapkitInv.setItem(40, boots);

        mapkitInv.setItem(21, sword);
        mapkitInv.setItem(23, bow);

        mapkitInv.setItem(30, poison);
        mapkitInv.setItem(32, slowness);

        for(int i = 0; i < 54; i++) {
            if(mapkitInv.getItem(i) != null)
                continue;

            mapkitInv.setItem(i, outline);
        }
    }
}
