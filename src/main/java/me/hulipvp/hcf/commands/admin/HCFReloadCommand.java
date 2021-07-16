package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.kits.Kit;
import me.hulipvp.hcf.game.player.data.mod.item.ModItems;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;

public class HCFReloadCommand {

    @Command(label = "hcf.reload")
    public void onCommand(CommandData args) {
        long time = System.currentTimeMillis();

        ConfigValues.init();

        HCF.getInstance().loadConfigs();
        Kit.instate();
        ModItems.instate();

        args.getSender().sendMessage(CC.translate("&aReloaded HCF tab, sb, locale, config, kits and mod items in " + (System.currentTimeMillis() - time) + "ms"));
    }
}
