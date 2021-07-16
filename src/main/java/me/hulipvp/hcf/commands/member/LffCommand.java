package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LffCommand {

    private Map<UUID, Long> cooldowns;

    public LffCommand() {
        cooldowns = new HashMap<>();
    }

    @Command(label = "lff", permission = "command.lff", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = args.getPlayer();

        HCFProfile profile = HCFProfile.getByPlayer(player);
        if(profile.hasFaction()) {
            player.sendMessage(Locale.COMMAND_FACTION_NOT_ALLOWED.toString());
            return;
        }

        long lffCooldown;
        if(cooldowns.containsKey(player.getUniqueId()) && (lffCooldown = cooldowns.get(player.getUniqueId())) > System.currentTimeMillis()) {
            player.sendMessage(Locale.COMMAND_LFF_COOLDOWN.toString().replace("%time%", TimeUtils.getTimeTill(new Timestamp(lffCooldown))));
            return;
        }

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (TimeUnit.SECONDS.toMillis(ConfigValues.LIMITERS_LFF_COOLDOWN)));
        HCF.getInstance().getMessagesFile().getLff().forEach(message -> {
            Bukkit.broadcastMessage(C.color(message
                    .replace("%player%", player.getName())
                    .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                    .replace("%secondary%", ConfigValues.SERVER_SECONDARY)));
        });
    }
}
