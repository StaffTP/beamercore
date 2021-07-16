package me.hulipvp.hcf.game.kits.type;

import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class MinerKit extends Kit {

    public MinerKit() {
        super("Miner", ArmorType.IRON, Arrays.asList(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1, false), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false), new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false)));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.getFrom().getBlockY() == event.getTo().getBlockY())
            return;
        if(!Kit.isWearingKit(event.getPlayer(), this.getType()))
            return;

        Player player = event.getPlayer();
        int level = player.getLocation().getBlockY();

        boolean sendMessage = false;
        if(level <= ConfigValues.KITS_MINER_INVIS_LEVEL) {
            if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false));
                sendMessage = true;
            }
        } else {
            if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                sendMessage = true;
            }
        }

        if(sendMessage)
            player.sendMessage(Locale.MINER_INVIS_STATUS.toString()
                    .replace("%status%", player.hasPotionEffect(PotionEffectType.INVISIBILITY) ? C.color("&aEnabled") : C.color("&cDisabled")));
    }
}
