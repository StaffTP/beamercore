package me.hulipvp.hcf.hooks.plugins.vault;

import lombok.Getter;
import me.hulipvp.hcf.hooks.plugins.PlayerHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements PlayerHook {

    @Getter private Permission perms;
    @Getter private Chat chat;
    @Getter private Economy economy;

    @Getter private boolean hooked;

    public VaultHook() {
        RegisteredServiceProvider<Permission> permsRsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if(permsRsp != null && permsRsp.getProvider() != null)
            this.perms = permsRsp.getProvider();

        RegisteredServiceProvider<Chat> chatRsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if(chatRsp != null && chatRsp.getProvider() != null)
            this.chat = chatRsp.getProvider();

//        RegisteredServiceProvider<Economy> economyRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
//        if(economyRsp != null && economyRsp.getProvider() != null)
//            this.economy = economyRsp.getProvider();
//
//        Bukkit.getServer().getServicesManager().register(Economy.class, new VaultEconomy(), HCF.getInstance(), ServicePriority.Highest);


        this.hooked = true;
        Bukkit.getLogger().info("[HCF] Successfully hooked into Vault.");
    }

    public static boolean canHook() {
        return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
    }

    @Override
    public boolean canChat(Player player) {
        return true;
    }

    @Override
    public String getRankName(Player player) {
        if(perms == null)
            return "";

        return perms.getPrimaryGroup(player);
    }

    @Override
    public String getRankPrefix(Player player) {
        if(chat == null)
            return "";
        return chat.getPlayerPrefix(player);
    }

    @Override
    public String getRankSuffix(Player player) {
        if(chat == null)
            return "";

        return chat.getPlayerSuffix(player);
    }

    @Override
    public String getTag(Player player) {
        return "";
    }
}
