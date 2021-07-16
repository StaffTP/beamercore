package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.setting.HCFSetting;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SettingsCommand {

    @Command(label = "settings", aliases = {"setting", "hcfsettings"}, playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(player);
        openSettings(player, profile);
    }

    private void openSettings(Player player, HCFProfile profile) {
        String title = ConfigValues.SETTINGS_GUI_TITLE
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY);

        Inventory settings = Bukkit.createInventory(null, 9, C.color(title));
        for(HCFSetting setting : profile.getSettings())
            settings.setItem(setting.getType().getSlot(), setting.getSettingItem());

        player.openInventory(settings);
    }
}
