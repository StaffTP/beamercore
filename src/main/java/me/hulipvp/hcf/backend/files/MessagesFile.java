package me.hulipvp.hcf.backend.files;

import me.hulipvp.hcf.utils.chat.C;

import java.util.List;
import java.util.stream.Collectors;

public class MessagesFile extends ConfigFile {

    public MessagesFile() {
        super("messages.yml");
    }

    public List<String> getHelp() {
        return this.getConfig().getStringList("help.main");
    }

    public List<String> getCoords() {
        return this.getConfig().getStringList("help.coords");
    }

    public List<String> getFactionSubclaiming() {
        return this.getConfig().getStringList("faction.help.subclaiming");
    }

    public List<String> getFactionClaimingStart() {
        return this.getConfig().getStringList("faction.help.claiming");
    }

    public List<String> getFactionHelp() {
        return this.getConfig().getStringList("faction.help.main");
    }

    public List<String> getFactionCaptainHelp() {
        return this.getConfig().getStringList("faction.help.captain");
    }

    public List<String> getFactionColeaderHelp() {
        return this.getConfig().getStringList("faction.help.coleader");
    }

    public List<String> getPlayerFactionShow() {
        return this.getConfig().getStringList("faction.show.player");
    }

    public List<String> getSystemFactionShow() {
        return this.getConfig().getStringList("faction.show.system");
    }

    public List<String> getEconomyHelp() {
        return this.getConfig().getStringList("help.economy");
    }

    public List<String> getLivesHelp() {
        return this.getConfig().getStringList("help.lives");
    }

    public List<String> getPvpHelp() {
        return this.getConfig().getStringList("help.pvp");
    }

    public List<String> getFilterHelp() {
        return this.getConfig().getStringList("help.filter");
    }

    public List<String> getLff() {
        return this.getConfig().getStringList("other.lff");
    }

    public List<String> getShopBuyLines() {
        return this.getConfig().getStringList("shop.lines.buy").stream()
                .map(C::color).collect(Collectors.toList());
    }

    public List<String> getShopSellLines() {
        return this.getConfig().getStringList("shop.lines.sell").stream()
                .map(C::color).collect(Collectors.toList());
    }

    public List<String> getShopInsufficientLines() {
        return this.getConfig().getStringList("shop.lines.insufficient").stream()
                .map(C::color).collect(Collectors.toList());
    }

    public List<String> getShopNoItemLines() {
        return this.getConfig().getStringList("shop.lines.no-items").stream()
                .map(C::color).collect(Collectors.toList());
    }

    public List<String> getStaffFrozenLines() {
        return this.getConfig().getStringList("staff.frozen").stream()
                .map(C::color).collect(Collectors.toList());
    }

    public List<String> getStaffPanicedLines() {
        return this.getConfig().getStringList("staff.paniced").stream()
                .map(C::color).collect(Collectors.toList());
    }
}
