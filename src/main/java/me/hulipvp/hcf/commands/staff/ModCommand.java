package me.hulipvp.hcf.commands.staff;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.mod.Vanish;
import me.hulipvp.hcf.listeners.player.PlayerListener;
import me.hulipvp.hcf.utils.*;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModCommand {

    public static List<UUID> paniced;
    public static boolean serverFrozen = false;

    public ModCommand() {
        paniced = new ArrayList<>();
    }

    @Command(label = "mod", aliases = { "h", "modmode", "staff", "staffmode" }, permission = "command.mod", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        LunarClientAPI api = LunarClientAPI.getInstance();

        if(profile.getVanish() == null) {
            profile.setVanish(new Vanish(p));
            profile.getVanish().enable();
            p.sendMessage(Locale.MODMODE_ENABLED.toString());
        } else {
            if (args.getArgs().length == 1 && args.getArg(0).equalsIgnoreCase("bypass")) {
                if (p.hasPermission("hcf.command.mod.bypass")) {
                    if (profile.getVanish().isBypass()) {
                        profile.getVanish().setBypass(false);
                        p.sendMessage(Locale.MODMODE_BYPASS_DISABLED.toString());
                        api.sendTitle(p, TitleType.TITLE, CC.translate("&cBypass Disabled!"), Duration.ofSeconds(1));
                    } else {
                        profile.getVanish().setBypass(true);
                        p.sendMessage(Locale.MODMODE_BYPASS_ENABLED.toString());
                        api.sendTitle(p, TitleType.TITLE, CC.translate("&aBypass Enabled!"), Duration.ofSeconds(1));

                    }
                } else {
                    p.sendMessage(Locale.NO_PERMISSION.toString());
                }
                return;
            }
            profile.getVanish().disable();
            profile.setVanish(null);
            p.sendMessage(Locale.MODMODE_DISABLED.toString());
        }
    }

    @Command(label = "invsee", permission = "command.invsee", playerOnly = true)
    public void onInvSee(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() < 1) {
            player.sendMessage(C.color("&cUsage: /invsee <player>"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args.getArg(0));
        if(target == null || !target.isOnline()) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }

        player.openInventory(target.getInventory());
        player.sendMessage(Locale.MODMODE_INVSEE.toString().replace("%player%", target.getName()));
    }

    @Command(label = "inspect", permission = "command.inspect", playerOnly = true)
    public void onInspect(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() < 1) {
            player.sendMessage(C.color("&cUsage: /inspect <player>"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args.getArg(0));
        if(target == null || !target.isOnline()) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }

        player.openInventory(InvUtils.createInventory(player, target));
        player.sendMessage(Locale.MODMODE_INSPECT.toString().replace("%player%", target.getName()));
    }

    @Command(label = "panic", aliases = { "unpanic" }, permission = "command.panic", playerOnly = true)
    public void onPanic(CommandData args) {
        Player player = args.getPlayer();
        if(!paniced.contains(player.getUniqueId())) {
            paniced.add(player.getUniqueId());

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "freeze " + player.getName());

            player.sendMessage(Locale.PANIC_INITIATED.toString());
            String broadcast = Locale.PANIC_BROADCAST.toString().replace("%player%", player.getName());
            Bukkit.getOnlinePlayers().stream()
                    .filter(staff -> staff.hasPermission("hcf.staff"))
                    .forEach(staff -> staff.sendMessage(broadcast));
        } else {
            paniced.remove(player.getUniqueId());

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unfreeze " + player.getName());

            player.sendMessage(Locale.PANIC_FINE.toString());
        }
    }

    @Command(label = "freeze", aliases = { "ss", "unfreeze" }, permission = "command.freeze")
    public void onFreeze(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 1) {
            sender.sendMessage(C.color("&cUsage: /" + args.getLabel() + " <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if(target == null) {
            sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
            return;
        }

        if(target.hasMetadata("hcf-frozen")) {
            paniced.remove(target.getUniqueId());
            target.removeMetadata("hcf-frozen", HCF.getInstance());

            target.sendMessage(Locale.FREEZE_UNFROZEN.toString());
            sender.sendMessage(Locale.FREEZE_UNFROZE_PLAYER.toString().replace("%player%", target.getName()));
            return;
        }

        target.setMetadata("hcf-frozen", new FixedMetadataValue(HCF.getInstance(), true));

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!target.isOnline() || !target.hasMetadata("hcf-frozen")) {
                    cancel();
                    return;
                }

                if(paniced.contains(target.getUniqueId())) {
                    String broadcast = Locale.PANIC_BROADCAST.toString().replace("%player%", target.getName());
                    Bukkit.getOnlinePlayers().stream()
                            .filter(staff -> staff.hasPermission("hcf.staff"))
                            .forEach(staff -> staff.sendMessage(broadcast));

                    HCF.getInstance().getMessagesFile().getStaffPanicedLines().forEach(line -> target.sendMessage(Placeholders.replacePlaceholders(line, target, HCFProfile.getByPlayer(target))));
                } else {
                    HCF.getInstance().getMessagesFile().getStaffFrozenLines().forEach(line -> target.sendMessage(Placeholders.replacePlaceholders(line, target, HCFProfile.getByPlayer(target))));

                    if(ConfigValues.FREEZE_GUI_ENABLED)
                        target.openInventory(PlayerListener.frozenInventory);
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 0L, 20L * ConfigValues.FREEZE_MESSAGE_REPEAT);

        sender.sendMessage(Locale.FREEZE_FROZE_PLAYER.toString().replace("%player%", target.getName()));
    }

    @Command(label = "freezeall", aliases = { "ssall", "unfreezeall" }, permission = "command.freeze.all")
    public void onFreezeAll(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() > 1) {
            sender.sendMessage(C.color("&cUsage: /" + args.getLabel()));
            return;
        }

        serverFrozen = !serverFrozen;

        if(serverFrozen) Bukkit.broadcastMessage(Locale.FREEZE_SERVER_FROZEN.toString());
        else Bukkit.broadcastMessage(Locale.FREEZE_SERVER_UNFROZEN.toString());


        new BukkitRunnable() {
            @Override
            public void run() {
                if (!serverFrozen) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("hcf.freeze.bypass")) {
                        player.sendMessage(C.color("&f████&c█&f████"));
                        player.sendMessage(C.color("&f███&c█&6█&c█&f███ &a&lThe server has been frozen!"));
                        player.sendMessage(C.color("&f██&c█&6█&0█&6█&c█&f██"));
                        player.sendMessage(C.color("&f██&c█&6█&0█&6█&c█&f██ &fPlease wait a few minutes!"));
                        player.sendMessage(C.color("&f█&c█&6██&0█&6██&c█&f█ &fSorry for the inconvenience"));
                        player.sendMessage(C.color("&f█&c█&6█████&c█&f█"));
                        player.sendMessage(C.color("&c█&6███&0█&6███&c█ &7This is an automated message"));
                        player.sendMessage(C.color("&c█████████"));
                    }
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 0L, 20L * ConfigValues.FREEZE_MESSAGE_REPEAT);
    }


    @Command(label = "world", permission = "command.world", playerOnly = true)
    public void onWorld(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() < 1) {
            player.sendMessage(C.color("&cUsage: /world <name;id>"));
            return;
        }

        World world;
        try {
            if(StringUtils.isInt(args.getArg(0)))
                world = Bukkit.getWorlds().get(Integer.parseInt(args.getArg(0)));
            else
                world = Bukkit.getWorld(args.getArg(0));
        } catch(Exception ex) {
            world = null;
        }

        if(world == null) {
            player.sendMessage(C.color("&cThe world with the name of id of '" + args.getArg(0) + "' does not exist."));
            return;
        }

        player.teleport(world.getSpawnLocation());
        player.sendMessage(C.color("&eTeleporting to world " + world.getName() + "..."));
    }

    @Command(label = "vanish", aliases = { "v", "hide" }, permission = "command.vanish", playerOnly = true)
    public void onVanish(CommandData args) {
        Player player = args.getPlayer();
        if(!player.hasPermission("hcf.command.vanish")) {
            player.sendMessage(Locale.NO_PERMISSION.toString());
            return;
        }

        HCFProfile profile = HCFProfile.getByPlayer(player);
        if(profile.getVanish() == null)
            profile.setVanish(new Vanish(player));

        profile.getVanish().setVanished(!profile.getVanish().isVanished());
        player.sendMessage(Locale.MODMODE_VANISH_TOGGLED.toString().replace("%status%", profile.getVanish().isVanished() ? C.color("&aEnabled") : C.color("&cDisabled")));
    }
}
