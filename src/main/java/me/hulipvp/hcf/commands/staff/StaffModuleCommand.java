package me.hulipvp.hcf.commands.staff;

import com.google.common.base.Joiner;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/14/2021 / 1:36 AM
 * GlacialHCF / me.hulipvp.hcf.commands.staff
 */
public class StaffModuleCommand {


    @Command(label = "staffmodule", aliases = { "staffmodules" }, permission = "command.staffmodule", playerOnly = true)
    public void onStaffModule(CommandData args) {
        Player player = (Player) args.getSender();
        HCFProfile hcfProfile = HCFProfile.getByPlayer(player);
        LunarClientAPI lunarClientAPI = LunarClientAPI.getInstance();

        if (!lunarClientAPI.isRunningLunarClient(player)) {
            player.sendMessage(CC.translate("&cYou must be on lunar client to use this command!"));
            return;
        }
        if (hcfProfile.isStaffModule()) {
            lunarClientAPI.disableAllStaffModules(player);
            hcfProfile.setStaffModule(false);
            lunarClientAPI.sendTitle(player, TitleType.TITLE, CC.translate("&cStaff Modules have been disabled!"), Duration.ofSeconds(1));
        } else {
            lunarClientAPI.giveAllStaffModules(player);
            hcfProfile.setStaffModule(true);
            lunarClientAPI.sendTitle(player, TitleType.TITLE, CC.translate("&aStaff Modules have been enabled!"), Duration.ofSeconds(1));
        }


    }

}
