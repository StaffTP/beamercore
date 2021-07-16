package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.modules.Module;
import me.hulipvp.hcf.api.modules.ex.InvalidModuleException;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.command.CommandSender;

import java.io.File;

public class ModulesCommand {

    @Command(label = "modules", aliases = { "module", "module.list" }, permission = "command.modules")
    public void onModule(CommandData args) {
        CommandSender sender = args.getSender();

        sender.sendMessage(C.color("&7Modules: " + (HCF.getInstance().getModuleManager().getModules().size() == 0 ? "&cNone" : "")));
        for(Module module : HCF.getInstance().getModuleManager().getModules().values())
            sender.sendMessage(C.color("  &8- " + ((module.isEnabled()) ? "&a" : "&c") + module.getInfo().getName() + " &7(&6" + module.getInfo().getVersion() + "&7)"));
    }

    @Command(label = "module.load", permission = "command.modules")
    public void onModuleLoad(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 2) {
            sender.sendMessage(C.color("&c/module load <name>"));
            return;
        }

        String name = args.getArg(1);
        if(HCF.getInstance().getModuleManager().getModule(name) != null) {
            sender.sendMessage(C.color("&cThat module is already loaded."));
            return;
        }

        File file = new File(HCF.getInstance().getDataFolder() + File.separator + "modules", name + ".jar");
        if(!file.exists()) {
            sender.sendMessage(C.color("&cThat module file doesn't exist."));
            return;
        }

        try {
            Module module = HCF.getInstance().getModuleManager().loadModule(file);

            if(HCF.getInstance().getModuleManager().enableModule(module))
                sender.sendMessage(C.color("[HCF] &aSuccessfully enabled the module '" + module.getInfo().getName() + "'."));
            else
                throw new InvalidModuleException("Module had an issue being enabled, please look for the stacktrace above.");
        } catch(InvalidModuleException ex) {
            sender.sendMessage(C.color("&cThere was an error loading module '" + name + "'."));
            ex.printStackTrace();
        }
    }

    @Command(label = "module.unload", permission = "command.modules")
    public void onModuleUnload(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 2) {
            sender.sendMessage(C.color("&c/module unload <name>"));
            return;
        }

        String name = args.getArg(1);
        Module module = HCF.getInstance().getModuleManager().getModule(name);
        if(module == null) {
            sender.sendMessage(C.color("&cThat module is not loaded."));
            return;
        }

        try {
            HCF.getInstance().getModuleManager().disableModule(module);
            sender.sendMessage(C.color("&aSuccessfully unloaded '" + module.getInfo().getName() + "'."));
        } catch(Exception ex) {
            sender.sendMessage(C.color("&cThere was an error loading module '" + name + "'."));
            ex.printStackTrace();
        }
    }

    @Command(label = "module.reload", permission = "command.modules")
    public void onModuleReload(CommandData args) {
        CommandSender sender = args.getSender();
        if(args.length() < 2) {
            sender.sendMessage(C.color("&c/module reload <name>"));
            return;
        }

        String name = args.getArg(1);
        Module module = HCF.getInstance().getModuleManager().getModule(name);
        if(module == null) {
            sender.sendMessage(C.color("&cThat module is not loaded."));
            return;
        }

        File file = new File(HCF.getInstance().getDataFolder() + File.separator + "modules", name + ".jar");
        if(!file.exists()) {
            sender.sendMessage(C.color("&cThat module file doesn't exist."));
            return;
        }

        try {
            HCF.getInstance().getModuleManager().disableModule(module);
            module = HCF.getInstance().getModuleManager().loadModule(file);

            if(HCF.getInstance().getModuleManager().enableModule(module))
                sender.sendMessage(C.color("&aSuccessfully reloaded module '" + module.getInfo().getName() + "'."));
            else
                throw new InvalidModuleException("Module had an issue being enabled, please look for the stacktrace above.");
        } catch(Exception ex) {
            sender.sendMessage(C.color("&cThere was an error loading module '" + name + "'."));
            ex.printStackTrace();
        }
    }
}
