package me.hulipvp.hcf.commands;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.commands.donator.effects.InvisibilityCommand;
import me.hulipvp.hcf.game.event.conquest.command.ConquestCommand;
import me.hulipvp.hcf.game.event.dtc.command.DTCCommand;
import me.hulipvp.hcf.game.event.koth.command.KothCommand;
import me.hulipvp.hcf.game.event.mountain.command.MountainCommand;
import me.hulipvp.hcf.game.faction.command.FactionCommand;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.commands.admin.*;
import me.hulipvp.hcf.commands.donator.*;
import me.hulipvp.hcf.commands.member.*;
import me.hulipvp.hcf.commands.staff.*;
import me.hulipvp.hcf.utils.command.CommandAPI;

public class CommandManager {

    @Getter private CommandAPI commandApi;

    public CommandManager() {
        this.commandApi = new CommandAPI(HCF.getInstance(), "hcf", Locale.NO_PERMISSION.toString(), Locale.PLAYER_ONLY.toString(), Locale.COMMAND_NOT_FOUND.toString());
    }

    public void registerCommands() {
        new FactionCommand().getArguments().forEach(this::registerCommands);

        registerCommands(new BalanceCommand());
        registerCommands(new BottleCommand());
        registerCommands(new BroadcastCommand());
        registerCommands(new ConquestCommand());
        registerCommands(new CustomTimerCommand());
        registerCommands(new CustomServerTimerCommand());
        registerCommands(new CrowbarCommand());
        registerCommands(new DebugHCFCommand());
        registerCommands(new DTCCommand());
        registerCommands(new EconomyCommand());
        registerCommands(new HCFReloadCommand());
        registerCommands(new ItemCommand());
        registerCommands(new LffCommand());
        registerCommands(new InvisibilityCommand());
        registerCommands(new EotwCommand());
        registerCommands(new SendTitleCommand());
        registerCommands(new BlockShopCommand());
        registerCommands(new RefillStationCommand());
        registerCommands(new StaffModuleCommand());
        if (HCF.getInstance().getConfig().contains("board.extraction")) {
            registerCommands(new ExtractionCommand());
        }
        registerCommands(new FilterCommand());
        registerCommands(new FlyCommand());
        registerCommands(new GamemodeCommand());
        registerCommands(new HelpCommand());
        registerCommands(new KothCommand());
        registerCommands(new LivesCommand());
        registerCommands(new LogoutCommand());
        registerCommands(new MapkitCommand());
        if (HCF.getInstance().getModModeFile().getConfig().getBoolean("mod-mode.enabled", true)) {
            registerCommands(new ModCommand());
        }
        registerCommands(new ModulesCommand());
        registerCommands(new MountainCommand());
        registerCommands(new PayCommand());
        registerCommands(new PingCommand());
        registerCommands(new PvpCommand());
        if (HCF.getInstance().getConfig().contains("board.rampage")) {
            registerCommands(new RampageCommand());
        }
        registerCommands(new ReclaimCommand());
        registerCommands(new ResetDataCommand());
        registerCommands(new RestartCommand());
        registerCommands(new RestoreInventoryCommand());
        registerCommands(new SetCommand());
        registerCommands(new SpawnCommand());
        registerCommands(new SetMaxPlayersCommand());
        registerCommands(new SettingsCommand());
        registerCommands(new SotwCommand());
        registerCommands(new StaffReviveCommand());
        registerCommands(new StatsCommand());
        registerCommands(new TeleportCommand());
        registerCommands(new TimerCommand());
        registerCommands(new ToggleChatCommand());
        registerCommands(new HideStaffCommand());
        registerCommands(new TopCommand());
        registerCommands(new SpeedCommand());
        registerCommands(new GetposCommand());
        registerCommands(new FeedCommand());
        registerCommands(new HealCommand());
        registerCommands(new BlockCommand());
        registerCommands(new IngotCommand());
        registerCommands(new MeltCommand());
        registerCommands(new ClearCommand());
        registerCommands(new RepairCommand());
        if (ConfigValues.FEATURES_MSG) registerCommands(new MessageCommand());
        if (ConfigValues.FEATURES_STAFF_CHAT) registerCommands(new StaffChatCommand());
        if (ConfigValues.FEATURES_MANAGE_CHAT) registerCommands(new ChatCommand());
        if (ConfigValues.FEATURES_REPORT) registerCommands(new ReportCommand());
        if (ConfigValues.FEATURES_REQUEST) registerCommands(new RequestCommand());
        if (ConfigValues.FEATURES_NOTES) registerCommands(new NotesCommand());
    }

    public void registerCommands(Object object) {
        commandApi.registerCommands(object);
    }

    public void unregisterCommands(Object object) {
        commandApi.unregisterCommands(object);
    }
}
