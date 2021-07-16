package me.hulipvp.hcf.commands.donator.effects;

import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/5/2021 / 7:55 PM
 * vhcf / me.hulipvp.hcf.commands.donator.effects
 */
public class InvisibilityCommand {

    @Command(label = "invis", aliases = "invisibility", permission = "hcf.command.invis", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
            p.sendMessage(CC.translate("&aYour &2&lInvisibility&a effect has been &cremoved!"));
            return;
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 0));
        p.sendMessage(CC.translate("&aYour &2&lInvisibility&a effect has been &aenabled!"));

    }
}
