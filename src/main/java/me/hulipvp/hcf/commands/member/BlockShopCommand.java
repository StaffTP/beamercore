package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.blockshop.BlockShop;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/3/2021 / 4:37 PM
 * vhcf / me.hulipvp.hcf.commands.member
 */
public class BlockShopCommand {


    @Command(label = "blockshop", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        Faction spawn = Faction.getByName("Spawn");
        new BlockShop().openMenu(p);

    }

}
