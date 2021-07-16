package me.hulipvp.hcf.listeners;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.InvUtils;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import me.hulipvp.hcf.utils.item.ItemNames;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopSignListener implements Listener {

    private static final Map<UUID, SignRestorer> pending = new HashMap<>();

    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        Player player = e.getPlayer();
        if(!player.hasPermission("hcf.shop.create"))
            return;

        if(e.getLine(0).equalsIgnoreCase("sell") || e.getLine(0).equalsIgnoreCase("buy")) {
            ItemStack item = getItem(e.getLine(1));
            if(item == null && !e.getLine(1).contains("Crowbar")) {
                player.sendMessage(C.color("&cError whilst creating Shop sign: &7" + e.getLine(1) + "&c is not a material."));
                e.setCancelled(true);
                return;
            }

            if(!StringUtils.isInt(e.getLine(2))) {
                player.sendMessage(C.color("&cError whilst creating Shop sign: &7" + e.getLine(2) + "&c is not a number."));
                e.setCancelled(true);
                return;
            }

            int amount = Integer.parseInt(e.getLine(2));
            if(!StringUtils.isInt(e.getLine(3))) {
                player.sendMessage(C.color("&cError whilst creating Shop sign: &7" + e.getLine(3) + "&c is not a number."));
                e.setCancelled(true);
                return;
            }

            double cost = Double.parseDouble(e.getLine(3));
            e.setLine(0, C.color(e.getLine(0).equalsIgnoreCase("sell") ? "&c[Sell]" : "&a[Buy]"));
            e.setLine(1, C.color(e.getLine(1).replace("_", " ")));
            e.setLine(2, String.valueOf(amount));
            e.setLine(3, "$" + cost);
            player.sendMessage(Locale.CREATED_SHOP_SIGN.toString());
        } else if (e.getLine(0).equalsIgnoreCase("shopguiplus")) {
            e.setLine(0, C.color("&a[Shop Menu]"));
            player.sendMessage(Locale.CREATED_SHOP_SIGN.toString());
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = e.getClickedBlock();
        if(!block.getType().name().contains("SIGN"))
            return;

        Sign sign = (Sign) block.getState();
        String firstLine = sign.getLine(0);
        HCFProfile profile = HCFProfile.getByPlayer(player);
        if(firstLine.contains("[Buy]") || firstLine.contains("[Sell]")) {
            e.setCancelled(true);

            Faction faction = Faction.getByLocation(block.getLocation());

            if (faction == null) {
                player.sendMessage(C.color("You can only use this in spawn"));
                return;
            }

            if (!(faction instanceof SafezoneFaction)) {
                player.sendMessage(C.color("You can only use this in spawn"));
                return;
            }

            TaskUtils.runAsync(() -> {
                ItemStack item = null;
                if(!sign.getLine(1).contains("Crowbar"))
                    item = getItem(sign.getLine(1));

                int amount = Integer.parseInt(sign.getLine(2));
                double price = Double.valueOf(sign.getLine(3).split("\\$")[1]);
                if(firstLine.contains("[Buy]")) {
                    int balance = profile.getBalance();
                    if(balance < price) {
                        String materialName = "Crowbar";
                        if (item != null) materialName = getMaterialName(item);
                        player.sendMessage(Locale.SHOP_SIGN_NOT_ENOUGH.toString());
                        String[] newLines = new String[4];
                        for(int i = 0; i < newLines.length; i++)
                            newLines[i] = HCF.getInstance().getMessagesFile().getShopInsufficientLines().get(i).replace("%item%", materialName);

                        e.getPlayer().sendSignChange(sign.getLocation(), newLines);
                        new SignRestorer(e.getPlayer(), sign, sign.getLines(), 3);
                        return;
                    }

                    ItemStack bought;
                    if(item == null)
                        bought = CrowbarListener.getCrowbar();
                    else
                        bought = new ItemBuilder(item).amount(amount).get();

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            profile.removeFromBalance((int) price);
                            profile.save();

                            player.getInventory().addItem(bought);
                            player.updateInventory();
                        }
                    }.runTaskLater(HCF.getInstance(), 1L);

                    String materialName = getMaterialName(bought);
                    String[] newLines = new String[4];
                    for(int i = 0; i < newLines.length; i++)
                        newLines[i] = HCF.getInstance().getMessagesFile().getShopBuyLines().get(i).replace("%price%", (int) price + "").replace("%amount%", amount + "").replace("%item%", materialName);

                    e.getPlayer().sendSignChange(sign.getLocation(), newLines);
                    new SignRestorer(e.getPlayer(), sign, sign.getLines(), 3);
                } else if(firstLine.contains("[Sell]")) {
                    int inventoryAmount;
                    if (item == null)
                        inventoryAmount = InvUtils.getAmount(player, CrowbarListener.getCrowbar());
                    else
                        inventoryAmount = InvUtils.getAmount(player, item.getType());

                    if (inventoryAmount == 0) {
                        String materialName = getMaterialName(item == null ? CrowbarListener.getCrowbar() : item);
                        player.sendMessage(Locale.SHOP_SIGN_NO_ITEMS.toString());
                        String[] newLines = new String[4];
                        for (int i = 0; i < newLines.length; i++)
                            newLines[i] = HCF.getInstance().getMessagesFile().getShopNoItemLines().get(i).replace("%price%", (int) price + "").replace("%amount%", amount + "").replace("%item%", materialName);

                        e.getPlayer().sendSignChange(sign.getLocation(), newLines);
                        new SignRestorer(e.getPlayer(), sign, sign.getLines(), 3);
                        return;
                    }

                    if (inventoryAmount < Integer.parseInt(sign.getLine(2))) {
                        double perItem = price / (double) amount;
                        int finalPrice = (int) (perItem * (double) inventoryAmount);

                        List<ItemStack> all;
                        if (item == null)
                            all = InvUtils.getAllFromStack(player, CrowbarListener.getCrowbar());
                        else
                            all = InvUtils.getAllFromMaterial(player, item.getType(), inventoryAmount);

                        int amountSold = all.stream().mapToInt(ItemStack::getAmount).sum();

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                all.forEach(player.getInventory()::remove);
                                player.updateInventory();

                                profile.addToBalance(finalPrice);
                                profile.save();
                            }
                        }.runTaskLater(HCF.getInstance(), 1L);

                        String materialName = getMaterialName(all.get(0));

                        String[] newLines = new String[4];
                        for (int i = 0; i < newLines.length; i++)
                            newLines[i] = HCF.getInstance().getMessagesFile().getShopSellLines().get(i).replace("%price%", (int) finalPrice + "").replace("%amount%", amountSold + "").replace("%item%", materialName);

                        e.getPlayer().sendSignChange(sign.getLocation(), newLines);
                        new SignRestorer(e.getPlayer(), sign, sign.getLines(), 3);
                    } else {
                        double perItem = price / (double) amount;
                        int finalPrice = (int) (perItem * (double) amount);
                        int amountSold;
                        String materialName;

                        List<ItemStack> all;
                        if (item == null) {
                            all = InvUtils.getAllFromStack(player, CrowbarListener.getCrowbar());
                            amountSold = all.stream().mapToInt(ItemStack::getAmount).sum();
                            materialName = getMaterialName(all.get(0));

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    all.forEach(player.getInventory()::remove);
                                    player.updateInventory();

                                    profile.addToBalance(finalPrice);
                                    profile.save();
                                }
                            }.runTaskLater(HCF.getInstance(), 1L);
                        } else {
                            amountSold = amount;
                            materialName = getMaterialName(item);
                            InvUtils.removeItem(player.getInventory(), item.getType(), item.getDurability(), amount);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.updateInventory();

                                    profile.addToBalance(finalPrice);
                                    profile.save();
                                }
                            }.runTaskLater(HCF.getInstance(), 1L);
                        }


                        String[] newLines = new String[4];
                        for (int i = 0; i < newLines.length; i++)
                            newLines[i] = HCF.getInstance().getMessagesFile().getShopSellLines().get(i).replace("%price%", (int) finalPrice + "").replace("%amount%", amountSold + "").replace("%item%", materialName);

                        e.getPlayer().sendSignChange(sign.getLocation(), newLines);
                        new SignRestorer(e.getPlayer(), sign, sign.getLines(), 3);
                    }
                }
            });
        } else if(firstLine.contains("[Shop Menu]")) {
            e.setCancelled(true);

            Faction faction = Faction.getByLocation(block.getLocation());

            if (faction == null) {
                player.sendMessage(C.color("You can only use this in spawn"));
                return;
            }

            if (!(faction instanceof SafezoneFaction)) {
                player.sendMessage(C.color("You can only use this in spawn"));
                return;
            }

            try {
                if (ShopGuiPlusApi.getShop(sign.getLine(1)) == null) {
                    player.sendMessage(C.color("&cInvalid shop from shopguiplus"));
                    return;
                } else {
                    ShopGuiPlusApi.getPlugin().getShopManager().openShopMenu(player, sign.getLine(1), true);
                }

            } catch (Exception ex) {
                System.out.println("ShopGuiplus error:");
                ex.printStackTrace();
            }
        }
    }

    private class SignRestorer extends BukkitRunnable {

        @Getter private Player player;
        @Getter private Sign sign;
        @Getter private String[] lines;

        SignRestorer(Player player, Sign sign, String[] lines, int delay) {
            this.player = player;
            this.sign = sign;
            this.lines = lines;

            SignRestorer restorer = pending.put(player.getUniqueId(), this);
            if(restorer != null && sign.getX() == restorer.getSign().getX() && sign.getZ() == restorer.getSign().getZ())
                restorer.cancel();

            runTaskLater(HCF.getInstance(), 20L * delay);
        }

        public void run() {
            pending.remove(player.getUniqueId());
            if(player == null || !player.isOnline())
                return;

            player.sendSignChange(sign.getLocation(), lines);
        }
    }

    private ItemStack getItem(String line) {
        ItemStack item;
        if(StringUtils.isInt(line)) {
            item = new ItemStack(Material.matchMaterial(line));
        } else {
            if(line.equalsIgnoreCase("End Portal")) {
                item = new ItemStack(Material.ENDER_PORTAL_FRAME);
            } else {
                if(line.equalsIgnoreCase("Crowbar"))
                    return CrowbarListener.getCrowbar();

                if(line.contains(":")) {
                    String[] split = line.split(":");
                    if(split.length > 2)
                        item = new ItemStack(Material.matchMaterial(split[0]));
                    else {
                        try {
                            item = new ItemStack(Material.matchMaterial(split[0]), 1, (byte) Integer.parseInt(split[1]));
                        } catch(NumberFormatException ex) {
                            return null;
                        }
                    }
                } else {
                    Material material = Material.matchMaterial(line);
                    if (material != null) item = new ItemStack(Material.matchMaterial(line));
                    else item = null;
                }
            }
        }

        if (item != null) {

            switch (item.getType()) {
                case NETHER_WARTS:
                    return new ItemStack(Material.NETHER_STALK);
                case CARROT:
                    return new ItemStack(Material.CARROT_ITEM);
                default:
                    return (item.getType() == null || item.getType() == Material.AIR) ? null : item;
            }
        } else {
            return null;
        }
    }

    private String getMaterialName(ItemStack stack) {
        return ItemNames.lookup(stack);
    }
}
