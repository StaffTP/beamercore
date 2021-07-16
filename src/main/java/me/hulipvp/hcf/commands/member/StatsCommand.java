package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.Death;
import me.hulipvp.hcf.game.player.data.Kill;
import me.hulipvp.hcf.game.player.data.statistic.HCFStatistic;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.InvUtils;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class StatsCommand {

    @Command(label = "stats", aliases = { "ores" }, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        Player target;
        HCFProfile profile;

        if(args.length() != 1) {
            target = args.getPlayer();
        } else {
            target = Bukkit.getPlayer(args.getArg(0));
            if(target == null) {
                args.getPlayer().sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }
        }

        profile = HCFProfile.getByPlayer(target);
        p.openInventory(this.getStatistics(target, profile));
    }

    private Inventory getStatistics(Player target, HCFProfile profile) {
        Inventory inventory = Bukkit.createInventory(null, 36, C.color(ConfigValues.SERVER_SECONDARY + target.getName() + ConfigValues.SERVER_PRIMARY + "'s Statistics"));
        InvUtils.fillSidesWithItem(inventory, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14));

        for(HCFStatistic statistic : profile.getStatistics()) {
            ItemStack itemStack = new ItemBuilder(statistic.getType().getMaterial())
                    .amount(1)
                    .name("&b&l" + WordUtils.capitalizeFully(statistic.getType().name().replace("_", " ").toLowerCase()))
                    .lore("&eAmount: &a" + statistic.getValue()).get();

            inventory.addItem(itemStack);
        }

        inventory.setItem(19, new ItemBuilder(Material.DIAMOND_SWORD)
                .amount(1)
                .name("&a&lKills")
                .lore("&eAmount: &a" + profile.getKills().size(), " ", "&7Click to show recent kills.").get());

        inventory.setItem(25, new ItemBuilder(Material.SKULL_ITEM)
                .amount(1)
                .name("&c&lDeaths")
                .lore("&eAmount: &a" + profile.getDeaths().size(), " ", "&7Click to show recent deaths.").get());

        return inventory;
    }

    public static void openDeathsInventory(Player target, Player viewer) {
        Inventory inventory = Bukkit.createInventory(null, 45, C.color(ConfigValues.SERVER_SECONDARY + target.getName() + ConfigValues.SERVER_PRIMARY + "'s Recent Deaths"));
        HCFProfile profile = HCFProfile.getByPlayer(target);

        int index = 0;
        for(Death death : profile.getDeaths()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            if(death.getKiller() != null) {
                HCFProfile deadProfile = HCFProfile.getByUuid(death.getKiller());
                if(deadProfile.getName() == null) {
                    skullMeta.setOwner(target.getName());
                    skullMeta.setDisplayName(ChatColor.AQUA + "Unknown Killer");
                } else {
                    skullMeta.setOwner(deadProfile.getName());
                    skullMeta.setDisplayName(ChatColor.AQUA + deadProfile.getName());
                }
            } else {
                skullMeta.setOwner(target.getName());
                skullMeta.setDisplayName(ChatColor.AQUA + "Unknown Killer");
            }

            skull.setItemMeta(skullMeta);

            inventory.addItem(skull);
            index++;
            if(index >= inventory.getSize())
                break;
        }

        viewer.openInventory(inventory);
    }

    public static void openKillsInventory(Player target, Player viewer) {
        Inventory inventory = Bukkit.createInventory(null, 45, C.color(ConfigValues.SERVER_SECONDARY + target.getName() + ConfigValues.SERVER_PRIMARY + "'s Recent Kills"));
        HCFProfile profile = HCFProfile.getByPlayer(target);

        int index = 0;
        for(Kill kill : profile.getKills()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            if(kill.getKilled() != null) {
                HCFProfile killedProfile = HCFProfile.getByUuid(kill.getKilled());
                if(killedProfile.getName() == null) {
                    skullMeta.setOwner(target.getName());
                    skullMeta.setDisplayName(ChatColor.AQUA + "Unknown Killed");
                } else {
                    skullMeta.setOwner(killedProfile.getName());
                    skullMeta.setDisplayName(ChatColor.AQUA + killedProfile.getName());
                }
            } else {
                skullMeta.setOwner(target.getName());
                skullMeta.setDisplayName(ChatColor.AQUA + "Unknown Killed");
            }

            skull.setItemMeta(skullMeta);

            inventory.addItem(skull);
            index++;
            if(index >= inventory.getSize())
                break;
        }

        viewer.openInventory(inventory);
    }

    @Command(label = "playtime", aliases = { "pt" }, playerOnly = true)
    public void onPlaytime(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() > 1) {
            player.sendMessage(Locale.COMMAND_PLAYTIME_USAGE.toString());
            return;
        }

        Player target = player;
        if(args.length() == 1) {
            target = Bukkit.getPlayer(args.getArg(0));
            if(target == null) {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }
            int statistic = target.getStatistic(Statistic.PLAY_ONE_TICK);
            int days = statistic / 1728000;
            int hours = statistic % 1728000 / 20 / 3600;
            int minutes = statistic % 72000 / 20 / 60;
            int seconds = statistic % 1200 / 20;
            player.sendMessage(Locale.COMMAND_PLAYTIME_OTHER.toString().replace("%player%", target.getName()).replace("%days%", days + "").replace("%hours%", hours + "").replace("%minutes%", minutes + "").replace("%seconds%", seconds + ""));

        } else {
            int statistic = target.getStatistic(Statistic.PLAY_ONE_TICK);
            int days = statistic / 1728000;
            int hours = statistic % 1728000 / 20 / 3600;
            int minutes = statistic % 72000 / 20 / 60;
            int seconds = statistic % 1200 / 20;
            player.sendMessage(Locale.COMMAND_PLAYTIME_SELF.toString().replace("%days%", days + "").replace("%hours%", hours + "").replace("%minutes%", minutes + "").replace("%seconds%", seconds + ""));
        }
    }
}
