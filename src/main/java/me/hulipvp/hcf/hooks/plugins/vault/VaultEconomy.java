package me.hulipvp.hcf.hooks.plugins.vault;

import me.hulipvp.hcf.game.player.HCFProfile;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultEconomy implements Economy {

    @Override
    public boolean isEnabled() {
        return VaultHook.canHook();
    }

    @Override
    public String getName() {
        return "HCF";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return "$" + String.valueOf(v);
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public double getBalance(String s) {
        return HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance();
    }

    @Override
    public double getBalance(String s, String s1) {
        return HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance();
    }

    @Override
    public boolean has(String s, double v) {
        return HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance() >= (int) v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance() >= (int) v;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance() >= (int) v;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        if (HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance() >= v) {
            HCFProfile.getByPlayer(Bukkit.getPlayer(s)).removeFromBalance((int) v);
            return new EconomyResponse(v, HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(v, HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance(), EconomyResponse.ResponseType.FAILURE, "Not enough money");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        if (HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance() >= v) {
            HCFProfile.getByPlayer(offlinePlayer.getPlayer()).removeFromBalance((int) v);
            return new EconomyResponse(v, HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(v, HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance(), EconomyResponse.ResponseType.FAILURE, "Not enough money");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        if (HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance() >= v) {
            HCFProfile.getByPlayer(Bukkit.getPlayer(s)).removeFromBalance((int) v);
            return new EconomyResponse(v, HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(v, HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance(), EconomyResponse.ResponseType.FAILURE, "Not enough money");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        if (HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance() >= v) {
            HCFProfile.getByPlayer(offlinePlayer.getPlayer()).removeFromBalance((int) v);
            return new EconomyResponse(v, HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(v, HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance(), EconomyResponse.ResponseType.FAILURE, "Not enough money");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        HCFProfile.getByPlayer(Bukkit.getPlayer(s)).addToBalance((int) v);
        return new EconomyResponse(v, HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        HCFProfile.getByPlayer(offlinePlayer.getPlayer()).addToBalance((int) v);
        return new EconomyResponse(v, HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        HCFProfile.getByPlayer(Bukkit.getPlayer(s)).addToBalance((int) v);
        return new EconomyResponse(v, HCFProfile.getByPlayer(Bukkit.getPlayer(s)).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        HCFProfile.getByPlayer(offlinePlayer.getPlayer()).addToBalance((int) v);
        return new EconomyResponse(v, HCFProfile.getByPlayer(offlinePlayer.getPlayer()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        HCFProfile.getByPlayer(Bukkit.getPlayer(s)).setBalance(0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        HCFProfile.getByPlayer(offlinePlayer.getPlayer()).setBalance(0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        HCFProfile.getByPlayer(Bukkit.getPlayer(s)).setBalance(0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        HCFProfile.getByPlayer(offlinePlayer.getPlayer()).setBalance(0);
        return true;
    }
}
