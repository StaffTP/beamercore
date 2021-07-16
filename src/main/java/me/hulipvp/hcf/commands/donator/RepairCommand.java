package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RepairCommand {

    public static Map<UUID, Long> repairCooldowns = new HashMap<>();
    public static Map<UUID, Long> repairAllCooldowns = new HashMap<>();

    @Command(label = "repair", permission = "command.repair", aliases = {"fix"}, playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        UUID uuid = player.getUniqueId();

        if (args.getArgs().length == 0) {
            ItemStack item = player.getItemInHand();
            Material material = Material.getMaterial(item.getTypeId());

            if (material == null || material == Material.AIR || material == Material.POTION || material == Material.GOLDEN_APPLE || material.isBlock() || material.getMaxDurability() < 1) {
                player.sendMessage(C.color("&cYou cannot repair this"));
                return;
            }

            if (item.getDurability() == 0) {
                player.sendMessage(C.color("&cYou cannot repair this"));
                return;
            }

            if (!ConfigValues.FEATURES_KITMAP) {
                long timestamp1 = repairCooldowns.getOrDefault(uuid, 0L);
                long millis1 = System.currentTimeMillis();
                long remaining1 = timestamp1 - millis1;

                if (remaining1 > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot do this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining1, true, true) + ".");
                    return;
                }
                repairCooldowns.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(6L));
            }

            item.setDurability((short) 0);
            player.sendMessage(C.color("&aSuccessfully repaired your item"));
        } else if (args.getArg(0).equalsIgnoreCase("all")) {
            if (!(player.hasPermission("hcf.repair.all"))) {
                player.sendMessage(Locale.NO_PERMISSION.toString());
                return;
            }

            List<ItemStack> items = new ArrayList<ItemStack>();

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != Material.POTION && item.getType() != Material.GOLDEN_APPLE && !item.getType().isBlock() && item.getType().getMaxDurability() > 1 && item.getDurability() != 0) {
                    items.add(item);
                }
            }

            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null && armor.getType() != Material.AIR) {
                    items.add(armor);
                }
            }

            if (items.isEmpty()) {
                player.sendMessage(C.color("&cNo items worth repairing"));
                return;
            }

            long timestamp1 = repairAllCooldowns.getOrDefault(uuid, 0L);
            long millis1 = System.currentTimeMillis();
            long remaining1 = timestamp1 - millis1;

            if (remaining1 > 0L) {
                player.sendMessage(ChatColor.RED + "You cannot do this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining1, true, true) + ".");
                return;
            }
            repairAllCooldowns.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(6L));

            for (ItemStack item : items) {
                item.setDurability((short) 0);
            }
            player.sendMessage(C.color("&aSuccessfully repaired your items"));

        }
        return;
    }
}
