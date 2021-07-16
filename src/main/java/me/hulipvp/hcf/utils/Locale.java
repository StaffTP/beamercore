package me.hulipvp.hcf.utils;

import lombok.Getter;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.backend.files.LocaleFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Locale {
    
    PLAYER_ONLY("primary.player-only", "&cYou must be a player to execute this command."),
    NO_PERMISSION("primary.no-permission", "&cNo permission."),
    COMMAND_NOT_FOUND("primary.not-found", "&fUnknown command."),
    MOJANG_API_FAIL("primary.mojang-api-fail", "&cAn error occurred while contacting the Mojang API."),
    PLAYER_NOT_FOUND("primary.player-not-found", "&c%name%&c is not currently online."),
    PLAYER_OR_FACTION_NOT_FOUND("primary.player-or-faction-not-found", "&cThat player or faction is not found."),
    TARGET_NOT_FOUND("primary.target-not-found", "&c%name%&c is not found."),
    INVALID_NUMBER("primary.invalid-number", "&c%number% is not a valid number."),
    INVALID_NUMBER_TOO_LOW("primary.invalid-number-too-low", "&cEnter a number above %number%."),
    INVALID_TIME("primary.invalid-time", "&c%number% is not a valid time, please use a valid time D:"),

    IN_COMBAT("other.in-combat", "&cYou may not do this in combat or inside of event factions! "),
    OUTSIDE_SAFEZONE("other.outside-safezone", "&cYou may not do this outside the safezone."),
    BLOCKED_POTION("other.blocked-potion", "&cYou may not use that potion."),
    BOOK_DISENCHANTED("other.book-disenchanted", "&aYou have removed all of the enchants on the book."),
    BORDER_CANNOT_BUILD("other.border-cannot-build", "&cYou cannot build past the border."),
    BORDER_REACHED("other.border-reached", "&cYou cannot go past the border."),
    CAPZONE_ENTRY("other.capzone-entry", "&eNow entering: %zone% Zone"),
    CAPZONE_LEFT("other.capzone-left", "&eNow leaving: %zone% Zone"),
    CREATED_SHOP_SIGN("other.created-shop-sign", "&aYou successfully created a shop sign."),
    FOUND_DIAMONDS("other.found-diamonds", "&f[FD] &b%name%&b found %amount% diamonds."),
    INVALID_ELEVATOR_SIGN("other.invalid-elevator-sign", "&cPlease use one of the following status\'s for line 2: Up, Top, Down, Bottom"),
    PLAYER_HEAD_INFO("other.player-head", "&eThis is the head of: %player%"),

    SHOP_SIGN_NOT_ENOUGH("shop.not-enough", "&cYou do not have enough money to buy this item."),
    SHOP_SIGN_NO_ITEMS("shop.no-items", "&cYou do not have any of this item to sell."),

    WELCOME_STARTING_BALANCE_MESSAGE("welcome.starting-balance-message", "&aSince this is your first time logging on you have been given $" + ConfigValues.WELCOME_STARTING_BALANCE + " to start with."),

    KITMAP_INVALID_KIT("kitmap.invalid-kit", "&cInvalid kit type: %invalid%"),
    KITMAP_CREATED_SIGN("kitmap.created-sign", "&7You have created a sign for the kit %mapkit%"),
    KITMAP_MONEY_TAKEN("kitmap.money-taken", "&e&l%name% &e&ltook &c%amount% &e&lfrom your balance for killing you."),
    KITMAP_REWARDED_MONEY("kitmap.rewarded-money", "&e&lYou earned &c%amount% &e&lfor killing &a&l%player%&e&l."),
    KITMAP_REWARDED_STREAK("kitmap.rewarded-streak", "&f%player% &ehas gotten the &c%name% &ekillstreak!"),

    MOUNTAIN_RESET("mountain.reset", "&a[%type%] &eMountain has been reset."),

    CHAT_FORMAT_PUBLIC_FACTION("chat.format.public.faction", "&6[%name%&6]&f %display%&7: &f%message%"),
    CHAT_FORMAT_PUBLIC_NO_FACTION("chat.format.public.no-faction", "%display%&7: &f%message%"),
    CHAT_FORMAT_FACTION("chat.format.faction", "%prefix% %name%: &f%message%"),
    CHAT_FORMAT_ALLY("chat.format.ally", "%prefix% %name%: &f%message%"),
    CHAT_FORMAT_CAPTAIN("chat.format.captain", "%prefix% %name%: &f%message%"),

    COMMAND_CHAT_ENABLED("command.chat.enabled", "&aYou can now see public chat."),
    COMMAND_CHAT_DISABLED("command.chat.disabled", "&aYou can no longer see public chat."),
    COMMAND_CHAT_CLEARED("command.chat.cleared", "&eThe chat has been cleared."),
    COMMAND_CHAT_MUTED("command.chat.muted", "&eThe chat has been muted."),
    COMMAND_CHAT_UNMUTED("command.chat.unmuted", "&eThe chat is no longer muted."),
    COMMAND_CHAT_CHAT_MUTED("command.chat.chat-muted", "&cThe chat is currently muted!"),
    COMMAND_CHAT_SLOWED("command.chat.slowed", "&eThe chat is now slowed for %time% seconds."),
    COMMAND_CHAT_UNSLOWED("command.chat.unslowed", "&eThe chat is no longer slowed."),
    COMMAND_CHAT_CHAT_SLOWED("command.chat.chat-slowed", "&cThe chat is currently slowed for %time% seconds."),

    COMMAND_LFF_COOLDOWN("command.lff.cooldown", "&cYou are still on cooldown for %time%."),

    COMMAND_FLY_ENABLED("command.fly.enabled", "&eYou have &aenabled &eflight mode!"),
    COMMAND_FLY_DISABLED("command.fly.disabled", "&eYou have &cdisabled &eflight mode!"),
    COMMAND_FLY_ENABLED_OTHER("command.fly.enabled_other", "&eYou have &aenabled &f%name%'s &eflight mode!"),
    COMMAND_FLY_DISABLED_OTHER("command.fly.disabled_other", "&eYou have &cdisabled &f%name%'s &eflight mode!"),
    COMMAND_FLY_ENABLED_OTHER_TARGET("command.fly.enabled_other_target", "&eYour flight was &aenabled &eby &f%name%&e!"),
    COMMAND_FLY_DISABLED_OTHER_TARGET("command.fly.disabled_other_target", "&eYour flight was &cdisabled &eby &f%name%&e!"),

    COMMAND_CLEAR_CLEARED("command.clear.cleared", "&eYou have cleared your inventory!"),
    COMMAND_CLEAR_CLEARED_OTHER("command.clear.cleared_other", "&eYou have &ccleared &f%name%'s &einventory!"),
    COMMAND_CLEAR_CLEARED_OTHER_TARGET("command.clear.cleared_other_target", "&eYour inventory was &ccleared &eby &f%name%&e!"),

    COMMAND_ECONOMY_SET("command.economy.set", "&aSet %player%\'s balance to %amount%"),
    COMMAND_ECONOMY_GIVEN("command.economy.given", "&aGiven %amount% to %target%"),
    COMMAND_ECONOMY_TAKEN("command.economy.taken", "&aTook %amount% from %target%"),
    COMMAND_BALANCE("command.balance.self", "&eBalance: &d$%balance%"),
    COMMAND_BALANCE_OTHER("command.balance.other", "&b%player%\'s &eBalance: &d$%balance%"),
    COMMAND_PAY_USAGE("command.pay.usage", "&cUsage: /pay <player> <amount>"),
    COMMAND_PAY_NOT_ENOUGH("command.pay.not-enough", "&cYou do not have enough money."),
    COMMAND_PAY_SENT("command.pay.sent", "&aSent %amount% to %recipient%"),
    COMMAND_PAY_RECEIVED("command.pay.received", "&aReceived %amount% from %sender%"),

    COMMAND_FILTER_TOGGLED("command.filter.toggled", "&eFilter: %status%"),
    COMMAND_FILTER_ALREADY_ADDED("command.filter.already-added", "&cThat item is already added into your filter."),
    COMMAND_FILTER_MATERIAL_NOT_ADDED("command.filter.not-added", "&cThat item was not found in your filter."),
    COMMAND_FILTER_ADDED_MATERIAL("command.filter.added-material", "&eAdded &a%type% &eto your item-filter."),
    COMMAND_FILTER_REMOVED_MATERIAL("command.filter.removed-material", "&eRemoved &a%type% &efrom your item-filter."),
    COMMAND_FILTER_INVALID_MATERIAL("command.filter.invalid-material", "&cThat is an invalid material."),

    COMMAND_NOTES_USAGE("command.notes.usage", "&cUsage: /notes <player> or /notes <add;remove> <player> <message;index>"),
    COMMAND_NOTES_PLAYER("command.notes.player", "&6%player%&e\'s notes:"),
    COMMAND_NOTES_LIST("command.notes.list", "&7%index%. &6%staffMember% &7- &e%note%"),
    COMMAND_NOTES_LIST_NONE("command.notes.list-none", "&eNone."),

    COMMAND_MESSAGE_USAGE("command.message.usage", "&cUsage: /%label% <player> <message>"),
    COMMAND_MESSAGE_FORMAT_TO("command.message.format.to", "&7(To &f%recipient%&7) %message%"),
    COMMAND_MESSAGE_FORMAT_FROM("command.message.format.from", "&7(From &f%sender%&7) %message%"),
    COMMAND_MESSAGE_TOGGLED("command.message.toggled", "&eMessages are now %status%&e."),
    COMMAND_MESSAGE_TOGGLED_SOUNDS("command.message.toggled-sounds", "&eMessaging sounds are now %status%&e."),
    COMMAND_MESSAGE_CANNOT("command.message.cannot", "&cYou cannot message that player."),
    COMMAND_MESSAGE_SELF("command.message.self", "&cYou cannot message yourself."),
    COMMAND_MESSAGE_DISABLED("command.message.disabled", "&cYou have messaging disabled."),
    COMMAND_MESSAGE_PLAYER_DISABLED("command.message.player-disabled", "&cThat player has messaging disabled."),

    COMMAND_REPLY_USAGE("command.reply.usage", "&cUsage: /%label% <message>"),
    COMMAND_REPLY_NO_PLAYER("command.reply.no-player", "&cNo one has messaged you :("),
    COMMAND_REPLY_PLAYER_OFFLINE("command.reply.player-offline", "&cThat player has logged off."),

    COMMAND_IGNORE_USAGE("command.ignore.usage", "&cUsage: /ignore <player>"),
    COMMAND_IGNORE_PLAYER("command.ignore.player", "&eYou are %status% &eignoring %player%."),
    COMMAND_IGNORE_CANNOT("command.ignore.cannot-ignore", "&cYou cannot ignore that player."),
    COMMAND_IGNORE_SELF("command.ignore.self", "&cYou cannot ignore yourself."),

    COMMAND_BOTTLE_SUCCESS("command.bottle.success", "&aYou withdrew %amount% XP."),
    COMMAND_BOTTLE_REDEEMED("command.bottle.redeemed", "&aYou redeemed %amount% XP."),
    COMMAND_BOTTLE_NOT_ENOUGH("command.bottle.not-enough", "&cYou do not have enough XP to withdraw!"),
    COMMAND_BOTTLE_FULL_INVENTORY("command.bottle.full-inventory", "&cYou must have a full inventory."),

    COMMAND_STAFF_CHAT_USAGE("command.staff-chat.usage", "&cUsage: /%label% <message>"),
    COMMAND_STAFF_CHAT_MESSAGE("command.staff-chat.message", "&b%name%: %message%"),
    COMMAND_STAFF_CHAT_TOGGLED("command.staff-chat.toggled", "&eStaff Chat: %status%"),

    COMMAND_SPAWN("command.spawn", "&cSpawn point is not set."),
    COMMAND_BROADCAST_PREFIX("command.broadcast-prefix", "&c&l(Broadcast) &f"),

    COMMAND_PING_SELF("command.ping.self", "&eYour ping: %ping%ms"),
    COMMAND_PING_OTHER("command.ping.other", "&e%player%\'s ping: %ping%ms"),

    COMMAND_PLAYTIME_USAGE("command.playtime.usage", "&cUsage: /playtime <player>"),
    COMMAND_PLAYTIME_SELF("command.playtime.self", "&eYour playtime: &7&f%days% &7days, &f%hours% &7hours, &f%minutes% &7minutes and &f%seconds% &7seconds."),
    COMMAND_PLAYTIME_OTHER("command.playtime.other", "&6%player%&e\'s playtime: &7&f%days% &7days &f%hours% &7hours, &f%minutes% &7minutes and &f%seconds% &7seconds."),

    COMMAND_LIVES("command.lives.self", "&eYou currently have &d%lives% &elives.\n&eVisit our store to purchase more: &d%store%"),
    COMMAND_LIVES_OTHER("command.lives.other", "&e%name% currently has &d%lives% &elives."),
    COMMAND_LIVES_SET("command.lives.set", "&eYou set &6%name%&e\'s lives to &a%amount%&e."),
    COMMAND_LIVES_GIVEN("command.lives.given", "&eYou gave &6%amount% &elives to &a%name%&e."),
    COMMAND_LIVES_RECEIVED("command.lives.received", "&eYou received &6%amount% &elives from &a%name%&e."),
    COMMAND_LIVES_TAKEN("command.lives.taken", "&eYou have taken &6%amount%&e\'s lives from &a%name%&e."),
    COMMAND_LIVES_CHECK("command.lives.check", "&eThe player \'%name%\' is death-banned for %time%."),
    COMMAND_LIVES_NOT_ENOUGH("command.lives.not-enough", "&cYou do not have enough lives to send."),
    COMMAND_LIVES_EOTW("command.lives.lives", "&cYou cannot use lives during EOTW."),

    COMMAND_PVP_SET("command.pvp.set", "&aSet %player%\'s PvP timer to %time%."),
    COMMAND_PVP_GIVEN("command.pvp.given", "&aSuccessfully gave %player% a PvP timer."),
    COMMAND_PVP_REMOVED("command.pvp.removed", "&aRemoved %player%\'s PvP timer."),
    COMMAND_PVP_ENABLED("command.pvp.enabled", "&aYour PvP has been enabled."),
    COMMAND_PVP_NO_TIMER("command.pvp.no-timer", "&cYou do not have an active PvP timer."),
    COMMAND_PVP_ALREADY_HAS("command.pvp.already-has", "&cThis player already has a PvP timer."),

    COMMAND_REVIVE_USAGE("command.revive.usage", "&cUsage: /revive <player>"),
    COMMAND_REVIVE_NO_DEATHBAN("command.revive.not-found", "&cNo death-ban found for %player%."),
    COMMAND_REVIVE_SUCCESS("command.revive.success", "&aRemoved %player%\'s death-ban."),
    COMMAND_REVIVE_NO_LIVES("command.revive.no-lives", "&cYou do not have any lives"),

    COMMAND_RESTORE_USAGE("command.restore.usage", "&cUsage: /restoreinv <player>"),
    COMMAND_RESTORE_NOT_FOUND("command.restore.not-found", "&cNo inventory found for %player%"),
    COMMAND_RESTORE_SUCCESS("command.restore.success", "&aRestored inventory for %player%"),
    COMMAND_RESTORE_RESTORED("command.restore.restores", "&aYour inventory was restored by %restorer%"),

    COMMAND_EOTW_USAGE("command.eotw.usage", "&cUsage: /eotw <start;stop;commence>"),
    COMMAND_EOTW_ALREADY_STARTING("command.eotw.already-starting", "&cEOTW has already started."),
    COMMAND_EOTW_NOT_RUNNING("command.eotw.not-running", "&cEOTW is not running."),
    COMMAND_EOTW_STARTING("command.eotw.starting", "&cEOTW will commence in 10 minutes."),
    COMMAND_EOTW_STOPPED("command.eotw.stopped", "&cEOTW has been cancelled."),
    COMMAND_EOTW_CANNOT("command.eotw.cannot", "&cYou cannot do this while EOTW is running."),
    COMMAND_EOTW_CANNOT_CLAIM("command.eotw.cannot-claim", "&cYou cannot claim during EOTW."),
    COMMAND_EOTW_COMMENCED("command.eotw.commenced", "&cThe EOTW period has commenced."),
    COMMAND_EOTW_CONSOLE_ONLY("command.eotw.console-only", "&cSorry, but only console can execute this command."),

    COMMAND_RAMPAGE_USAGE("command.rampage.usage", "&cUsage: /rampage <start;stop;pause;info>"),
    COMMAND_RAMPAGE_ALREADY_STARTING("command.rampage.already-starting", "&cRampage has already started."),
    COMMAND_RAMPAGE_NOT_RUNNING("command.rampage.not-running", "&cRampage is not running."),
    COMMAND_RAMPAGE_STOPPED("command.rampage.stopped", "&4&lRampage Event &fRampage has been &ccancelled"),
    COMMAND_RAMPAGE_PAUSED("command.rampage.paused", "&4&lRampage Event&f has been &epaused"),
    COMMAND_RAMPAGE_UNPAUSED("command.rampage.unpaused", "&4&lRampage Event&f has been &aunpaused"),
    COMMAND_RAMPAGE_STARTED("command.rampage.starting", "§x" +
            "\n &4&lRampage Event&f has started!" +
            "\n &7&oGet the most kills before the timer runs out" +
            "\n§x"),
    COMMAND_RAMPAGE_INFO("command.rampage.info", "&4&lRampage Event &7- &fTop Kills" +
            "\n  &c1. &f%rampage-top-name-1%: &c%rampage-top-kills-1%" +
            "\n  &c2. &f%rampage-top-name-2%: &c%rampage-top-kills-2%" +
            "\n  &c3. &f%rampage-top-name-3%: &c%rampage-top-kills-3%" +
            "\n  &c4. &f%rampage-top-name-4%: &c%rampage-top-kills-4%" +
            "\n  &c5. &f%rampage-top-name-5%: &c%rampage-top-kills-5%"),
    COMMAND_RAMPAGE_ENDED("command.rampage.ended", "§x" +
            "\n &4&lRampage Event &c&l(ENDED) &7- &fFinal Top Kills" +
            "\n  &c1. &f%rampage-top-name-1%: &c%rampage-top-kills-1%" +
            "\n  &c2. &f%rampage-top-name-2%: &c%rampage-top-kills-2%" +
            "\n  &c3. &f%rampage-top-name-3%: &c%rampage-top-kills-3%" +
            "\n  &c4. &f%rampage-top-name-4%: &c%rampage-top-kills-4%" +
            "\n  &c5. &f%rampage-top-name-5%: &c%rampage-top-kills-5%" +
            "\n§x"),

    COMMAND_EXTRACTION_USAGE("command.extraction.usage", "&cUsage: /extraction <start;stop;pause>"),
    COMMAND_EXTRACTION_ALREADY_STARTED("command.extraction.already-started", "&cExtraction has already started."),
    COMMAND_EXTRACTION_NOT_RUNNING("command.extraction.not-running", "&cExtraction is not running."),
    COMMAND_EXTRACTION_STOPPED("command.extraction.stopped", "&3&lExtraction Event&f has been &ccancelled"),
    COMMAND_EXTRACTION_PAUSED("command.extraction.paused", "&3&lExtraction Event&f has been &epaused"),
    COMMAND_EXTRACTION_UNPAUSED("command.extraction.unpaused", "&3&lExtraction Event&f has been &aunpaused"),
    COMMAND_EXTRACTION_STARTED("command.extraction.starting", "§x" +
            "\n &3&lExtraction Event&f has started!" +
            "\n &7&oYou can interact with all items in enemy claims" +
            "\n§x"),
    COMMAND_EXTRACTION_ENDED("command.extraction.ended", "§x" +
            "\n &3&lExtraction Event &fhas ended!" +
            "\n§x"),

    COMMAND_SOTW_USAGE("command.sotw.usage", "&cUsage: /sotw <start;stop;pause> <time>"),
    COMMAND_SOTW_RUNNING("command.sotw.running", "&cSOTW is already runnning."),
    COMMAND_SOTW_NOT_RUNNING("command.sotw.not-running", "&cSOTW is not runnning."),
    COMMAND_SOTW_STARTED("command.sotw.started", "&aThe SOTW period has started."),
    COMMAND_SOTW_STOPPED("command.sotw.stopped", "&aSOTW has been stopped."),
    COMMAND_SOTW_ADDED("command.sotw.extended", "&aSOTW has been extended."),
    COMMAND_SOTW_PAUSED("command.sotw.paused", "&aThe SOTW period has been paused."),
    COMMAND_SOTW_UNPAUSED("command.sotw.unpaused", "&aThe SOTW period has been unpaused."),
    COMMAND_SOTW_PVP_ENABLED("command.sotw.pvp-enabled", "&aPvP has now been enabled during the SOTW."),
    COMMAND_SOTW_PVP_DISABLED("command.sotw.pvp-disabled", "&aPvP has now been disabled during the SOTW."),
    COMMAND_SOTW_PLAYER_PVP_ENABLED("command.sotw.player-pvp-enabled", "&aYour own PvP status has now been enabled during the SOTW."),
    COMMAND_SOTW_PLAYER_PVP_DISABLED("command.sotw.player-pvp-disabled", "&aYour own PvP status has now been disabled during the SOTW."),
    COMMAND_SOTW_ENDED("command.sotw.ended", "&cThe SOTW period has ended!"),

    COMMAND_TIMER_USAGE("command.timer.usage", "&cUsage: /timer <set;remove> <player> <timername> [time]"),
    COMMAND_TIMER_WRONG("command.timer.wrong", "&cThat timer is not valid, use: enderpearl, gapple, apple, pvptimer, bardeffect, logout, starting, stuck, archermark, backstab, combat, home, jumpeffect"),
    COMMAND_TIMER_STARTED("command.timer.started", "&aTimer %timer% set for %name%"),
    COMMAND_TIMER_NOT_RUNNING("command.timer.not-running", "&c%name% does not have that timer."),
    COMMAND_TIMER_STOPPED("command.timer.stopped", "&aRemoved %timer% timer for %name%."),

    COMMAND_CUSTOMTIMER_USAGE("command.customtimer.usage", "&cUsage: /customtimer <add;reset> <player> <text> [time]"),
    COMMAND_CUSTOMTIMER_STARTED("command.customtimer.started", "&aCustom timer with text %timer%&a added for %name%"),
    COMMAND_CUSTOMTIMER_REMOVED("command.customtimer.removed", "&aRemoved all customtimers for %name%."),

    COMMAND_CUSTOMSERVERTIMER_USAGE("command.customservertimer.usage", "&cUsage: /customservertimer <add;reset> <text> [time]"),
    COMMAND_CUSTOMSERVERTIMER_NOT_FOUND("command.customservertimer.not-found", "&aCan't find a server timer with text %name%."),
    COMMAND_CUSTOMSERVERTIMER_REMOVED("command.customservertimer.removed", "&aRemoved customservertimer with text %name%"),
    COMMAND_CUSTOMSERVERTIMER_STARTED("command.customservertimer.started", "&8&m--------------------------------------" +
            "\n&6&l ⚠ &b&l%timer% &ftimer has started!" +
            "\n&8&m--------------------------------------"),

    COMMAND_SALE_USAGE("command.sale.usage", "&cUsage: /sale <time;stop>"),
    COMMAND_SALE_STARTED("command.sale.started", "&aA Sale has begun."),
    COMMAND_SALE_STOPPED("command.sale.stopped", "&cThe Sale was forcefully stopped."),
    COMMAND_SALE_EXPIRED("command.sale.expired", "&cThe Sale has expired."),
    COMMAND_SALE_RUNNING("command.sale.running", "&aThe Sale is currently running."),
    COMMAND_SALE_NOT_RUNNING("command.sale.not-running", "&aThe Sale is not running."),

    COMMAND_KEY_SALE_USAGE("command.key-sale.usage", "&cUsage: /keysale <time;stop>"),
    COMMAND_KEY_SALE_STARTED("command.key-sale.started", "&aA Key Sale has begun."),
    COMMAND_KEY_SALE_STOPPED("command.key-sale.stopped", "&cThe Key Sale was forcefully stopped."),
    COMMAND_KEY_SALE_EXPIRED("command.key-sale.expired", "&cThe Key Sale has expired."),
    COMMAND_KEY_SALE_RUNNING("command.key-sale.running", "&aThe Key Sale is currently running."),
    COMMAND_KEY_SALE_NOT_RUNNING("command.key-sale.not-running", "&aThe Key Sale is not running."),

    COMMAND_KEY_ALL_USAGE("command.key-all.usage", "&cUsage: /keyall <time;stop> <command including spaces example:crate giveallkey Tier1 1>"),
    COMMAND_KEY_ALL_STARTED("command.key-all.started", "&aA Key All has begun."),
    COMMAND_KEY_ALL_STOPPED("command.key-all.stopped", "&cThe Key All was forcefully stopped."),
    COMMAND_KEY_ALL_DONE("command.key-all.expired", "&cThe Key All has been given."),
    COMMAND_KEY_ALL_RUNNING("command.key-all.running", "&aThe Key All is currently running."),
    COMMAND_KEY_ALL_NOT_RUNNING("command.key-all.not-running", "&aThe Key Sale is not running."),

    COMMAND_RESTART_USAGE("command.restart.usage", "&cUsage: /restart <time;pause;cancel;force>"),
    COMMAND_RESTART_KICK_MESSAGE("command.restart.kick-message", "&cThe server is currently restarting."),
    COMMAND_RESTART_RUNNING("command.restart.not-running", "&cThe restart timer is running. (/restart cancel)"),
    COMMAND_RESTART_NOT_RUNNING("command.restart.not-running", "&cThe restart timer is not running. (/restart <time>)"),
    COMMAND_RESTART_PAUSED("command.restart.paused", "&eRestart sequence is now %status%&e."),
    COMMAND_RESTART_CANCELLED("command.restart.cancelled", "&eRestart sequence is now &ccanelled&e."),

    COMMAND_REPORT_USAGE("command.report.usage", "&cUsage: /report <player> <reason>"),
    COMMAND_REPORT_SELF("command.report.self", "&cYou cannot report yourself."),
    COMMAND_REPORT_COOLDOWN("command.report.cooldown", "&cPlease wait before using /report again."),
    COMMAND_REPORT_BROADCAST("command.report.broadcast", "&6[Report] &e%reported% &7was reported by &e%reporter% &7for &e%reason%"),
    COMMAND_REPORT_SUCCESS("command.report.success", "&aThank you for your report. A staff member will look into this soon."),

    COMMAND_REQUEST_USAGE("command.request.usage", "&cUsage: /request <message>"),
    COMAMND_REQUEST_COOLDOWN("command.request.cooldown", "&cPlease wait before using /request again."),
    COMMAND_REQUEST_BROADCAST("command.request.broadcast", "&6[Request] &e%sender% &7requested assistance: &e%message%"),
    COMMAND_REQUEST_SUCCESS("command.request.success", "&aThank you for your request. A staff member will be with you shortly."),

    COMMAND_MAPKIT_USAGE("command.mapkit.usage", "&cUsage: /mapkit <create;delete;toggle;load;update;setcolor> <mapkit>"),
    COMMAND_MAPKIT_ALREADY_EXISTS("command.mapkit.already-exists", "&cThat kit is already exists."),
    COMMAND_MAPKIT_CREATED("command.mapkit.created", "&e%type% has been created."),
    COMMAND_MAPKIT_DELETED("command.mapkit.deleted", "&e%type% has been deleted."),
    COMMAND_MAPKIT_INVALID_KIT("command.mapkit.invalid-kit", "&cThat kit is an invalid kit."),
    COMMAND_MAPKIT_TOGGLED("command.mapkit.toggled", "&e%type%&7: %status%"),
    COMMAND_MAPKIT_LOADED("command.mapkit.loaded", "&eLoaded inventory for %type%&e."),
    COMMAND_MAPKIT_UPDATED("command.mapkit.updated", "&eInventory for %type% &eupdated to your current inventory."),
    COMMAND_MAPKIT_SET_COLOR("command.mapkit.set-color", "&eColor for %type% &echanged to %color%&e."),

    COMMAND_KOTH_USAGE("command.koth.usage", "&cUsage: /koth <name> [create;delete;start;stop;setpos1;setpos2;settime;pearlable;special]"),
    COMMAND_KOTH_ALREADY_EXISTS("command.koth.already-exists", "&7[Koth] &eKoth &6%koth% &ealready exists."),
    COMMAND_KOTH_DOESNT_EXIST("command.koth.doesnt-exist", "&7[Koth] &eKoth &6%koth% &edoes not exist."),
    COMMAND_KOTH_DOESNT_EXIST_SUBTEXT("command.koth.doesnt-exist-subtext", "  &7- &eUse &c/koth %koth% create &eto create it."),
    COMMAND_KOTH_CREATED("command.koth.created", "&7[Koth] &eKoth &6%koth% &ehas been created."),
    COMMAND_KOTH_DELETED("command.koth.deleted", "&7[Koth] &eKoth &6%koth% &ehas been deleted."),
    COMMAND_KOTH_STARTED("command.koth.started", "&7[Koth] &eKoth &6%koth% &ehas been started."),
    COMMAND_KOTH_STOPPED("command.koth.stopped", "&7[Koth] &eKoth &6%koth% &ehas been stopped."),
    COMMAND_KOTH_SETPOS1("command.koth.setpos1", "&7[Koth] &ePosition #1 set for &6%koth%"),
    COMMAND_KOTH_SETPOS2("command.koth.setpos2", "&7[Koth] &ePosition #2 set for &6%koth%"),
    COMMAND_KOTH_SPECIAL_USAGE("command.koth.special.usage", "&cUsage: /koth <name> special <boolean>"),
    COMMAND_KOTH_SPECIAL_SET("command.koth.special.set", "&7[Koth] &eSpecial set for &6%koth%&e to &c%status%"),
    COMMAND_KOTH_PEARLABLE_USAGE("command.koth.pearlable.usage", "&cUsage: /koth <name> pearlable <boolean>"),
    COMMAND_KOTH_PEARLABLE_SET("command.koth.pearlable.set", "&7[Koth] &ePearlable set for &6%koth%&e to &c%status%"),
    COMMAND_KOTH_SETTIME_USAGE("command.koth.settime.usage", "&cUsage: /koth <name> settime <minutes>"),
    COMMAND_KOTH_SETTIME_SET("command.koth.settime.set", "&7[Koth] &eTime set for &6%koth%"),

    COMMAND_CONQUEST_USAGE("command.conquest.usage", "&cUsage: /conquest <name> [create;delete;start;stop;setarea;setpoints]"),
    COMMAND_CONQUEST_ALREADY_EXISTS("command.conquest.already-exists", "&7[Conquest] &eConquest &6%conquest% &ealready exists."),
    COMMAND_CONQUEST_DOESNT_EXIST("command.conquest.doesnt-exist", "&7[Conquest] &eConquest &6%conquest% &edoes not exist."),
    COMMAND_CONQUEST_DOESNT_EXIST_SUBTEXT("command.conquest.doesnt-exist-subtext", "  &7- &eUse &c/conquest %conquest% create &eto create it."),
    COMMAND_CONQUEST_CREATED("command.conquest.created", "&7[Conquest] &eConquest &6%conquest% &ehas been created."),
    COMMAND_CONQUEST_DELETED("command.conquest.deleted", "&7[Conquest] &eConquest &6%conquest% &ehas been deleted."),
    COMMAND_CONQUEST_STARTED("command.conquest.started", "&7[Conquest] &eConquest &6%conquest% &ehas been started."),
    COMMAND_CONQUEST_STOPPED("command.conquest.stopped", "&7[Conquest] &eConquest &6%conquest% &ehas been stopped."),
    COMMAND_CONQUEST_SETAREA_USAGE("command.conquest.setarea.usage", "&cUsage: /koth <name> setarea <RED;YELLOW;GREEN;BLUE> <1;2>"),
    COMMAND_CONQUEST_SETAREA_ONE("command.conquest.setarea.one", "&7[Conquest] %zonetype%&7: &ePosition #1 set for &6%conquest%"),
    COMMAND_CONQUEST_SETAREA_TWO("command.conquest.setarea.two", "&7[Conquest] %zonetype%&7: &ePosition #2 set for &6%conquest%"),
    COMMAND_CONQUEST_SETAREA_INVALID_ZONE("command.conquest.setarea.invalid-zone", "&7[Conquest] &eZone &6%zone% &eis invalid."),
    COMMAND_CONQUEST_SETPOINTS_USAGE("command.conquest.setpoints.usage", "&cUsage: /koth <name> setpoints <faction> <points>"),
    COMMAND_CONQUEST_SETPOINTS_SUCCESS("command.conquest.setpoints.success", "&7[Conquest] &eFaction &6%faction% &epoints have been set to &6%points%"),

    COMMAND_DTC_USAGE("command.dtc.usage", "&cUsage: /dtc <name> [create;delete;start;stop;setpoints]"),
    COMMAND_DTC_ALREADY_EXISTS("command.dtc.already-exists", "&7[DTC] &eDTC &6%dtc% &ealready exists."),
    COMMAND_DTC_DOESNT_EXIST("command.dtc.doesnt-exist", "&7[DTC] &eDTC &6%dtc% &edoes not exist."),
    COMMAND_DTC_DOESNT_EXIST_SUBTEXT("command.dtc.doesnt-exist-subtext", "  &7- &eUse &c/dtc %dtc% create &eto create it."),
    COMMAND_DTC_CREATED("command.dtc.created", "&7[DTC] &eDTC &6%dtc% &ehas been created."),
    COMMAND_DTC_DELETED("command.dtc.deleted", "&7[DTC] &eDTC &6%dtc% &ehas been deleted."),
    COMMAND_DTC_STARTED("command.dtc.started", "&7[DTC] &eDTC &6%dtc% &ehas been started."),
    COMMAND_DTC_STOPPED("command.dtc.stopped", "&7[DTC] &eDTC &6%dtc% &ehas been stopped."),
    COMMAND_DTC_SETPOINTS_USAGE("command.dtc.setpoints.usage", "&cUsage: /dtc <name> setpoints <faction> <points>"),
    COMMAND_DTC_SETPOINTS_SUCCESS("command.dtc.setpoints.success", "&7[DTC] &eFaction &6%faction% &epoints have been set to &6%points%"),

    COMMAND_MOUNTAIN_USAGE("command.mountain.usage", "&cUsage: /mountain <name> [create;delete;reset;setpos1;setpos2;setreset] <type>"),
    COMMAND_MOUNTAIN_EXISTS("command.mountain.exists", "&cA mountain by that name already exists."),
    COMMAND_MOUNTAIN_DOESNT_EXIST("command.mountain.doesnt-exist", "&cThe mountain %mountain% doesn\'t exist."),
    COMMAND_MOUNTAIN_CREATED("command.mountain.created", "&7[Mountain] &eMountain &6%mountain% &ehas been created."),
    COMMAND_MOUNTAIN_REMOVED("command.mountain.removed", "&7[Mountain] &eMountain &6%mountain% &ehas been deleted."),
    COMMAND_MOUNTAIN_SETPOS1("command.mountain.setpos1", "&7[Mountain] &6%mountain%: &ePosition #1 set for &6%mountain%"),
    COMMAND_MOUNTAIN_SETPOS2("command.mountain.setpos2", "&7[Mountain] &6%mountain%: &ePosition #2 set for &6%mountain%"),
    COMMAND_MOUNTAIN_INVALID_TYPE("command.mountain.invalid-type", "&cThat type doesn\'t exist."),
    COMMAND_MOUNTAIN_SET_TIME("command.mountain.set-time", "&7[Mountain] &eMountain &6%mountain%&e\'s time has been set to &a%time%&e."),

    COMMAND_FACTION_USAGE("command.faction.usage", "&cUsage: /f %data%"),
    COMMAND_FACTION_NOT_LEADER("command.faction.not-leader", "&cYou must be the leader to do this."),
    COMMAND_FACTION_NOT_COLEADER("command.faction.not-coleader", "&cYou must be a co-leader or above to do this."),
    COMMAND_FACTION_NOT_CAPTAIN("command.faction.not-captain", "&cYou must be a captain or above to do this."),
    COMMAND_FACTION_DOESNT_EXIST("command.faction.doesnt-exist", "&cNo faction by the name of %faction% exists."),
    COMMAND_FACTION_PROFILE_INVALID("command.faction.profile-invalid", "&cYour profile's faction is invalid."),
    COMMAND_FACTION_NOT_ALLOWED("command.faction.not-allowed", "&cYou cannot use this while in a faction."),
    COMMAND_FACTION_NOT_PLAYER("command.faction.not-player", "&cFaction must be a player faction."),
    COMMAND_FACTION_NOT_IN("command.faction.not-in", "&7You are not in a faction!"),
    COMMAND_FACTION_PLAYER_NOT_IN("command.faction.player-not-in", "&c%name% &cis not in your faction."),
    COMMAND_FACTION_RAIDABLE("command.faction.raidable", "&cYou cannot do this while your faction is raidable."),
    COMMAND_FACTION_ATTEMPT_CREATE("command.faction.attempt-create", "&cYou must leave you current faction before you create one."),
    COMMAND_FACTION_ATTEMPT_JOIN("command.faction.attempt-join", "&cYou must leave you current faction before you join another one."),
    COMMAND_FACTION_ACTION_COOLDOWN("command.faction.action-cooldown", "&eYou must wait 1 minute between creating and disbanding a faction."),
    COMMAND_FACTION_NOT_INVITED("command.faction.not-invited", "&cThis faction has not invited you."),
    COMMAND_FACTION_JOINED("command.faction.joined", "&e%name% &ehas joined the faction."),
    COMMAND_FACTION_LEFT("command.faction.left", "&c%name% &chas left the faction."),
    COMMAND_FACTION_LEFT_SELF("command.faction.left-self", "&eYou have left your faction."),
    COMMAND_FACTION_MEMBER_MAX("command.faction.member.max", "&cThis faction already has the max members allowed."),
    COMMAND_FACTION_HOME_NOT_SET("command.faction.home.not-set", "&cFaction home point is not set."),
    COMMAND_FACTION_HOME_UNABLE("command.faction.home.unable", "&cYou can not teleport to your hq here."),
    COMMAND_FACTION_HOME_WAITING("command.faction.home.waiting", "&eTeleporting to your faction's HQ in &d10 seconds&e... Stay still and do not take damage."),
    COMMAND_FACTION_HOME_PVP_TIMER("command.faction.home.pvp-timer", "&cYou cannot teleport to your Faction home whilst having PvP timer."),
    COMMAND_FACTION_HOME_SPAWN_TIMER("command.faction.home.spawn-timer", "&cYou cannot teleport to your Faction home whilst having Spawn Tag."),
    COMMAND_FACTION_CANNOT_SET_HOME("command.faction.home.cannot-home", "&cYou can only set your faction's home in it's own territory."),
    COMMAND_FACTION_HOME_UPDATED("command.faction.home.updated", "&3%name% &3has updated your faction's HQ point!"),
    COMMAND_FACTION_ANNOUNCEMENT_REMOVED("command.faction.announcement.removed", "&d%name% &eremoved the faction announcement"),
    COMMAND_FACTION_ANNOUNCEMENT_CHANGED("command.faction.announcement.changed", "&d%name% &echanged the faction announcement to &d%announcement%"),
    COMMAND_FACTION_CHAT_INVALID("command.faction.chat.invalid", "&c%type% is an invalid chat type."),
    COMMAND_FACTION_CHAT_CHANGED("command.faction.chat.changed", "&eYou are now chatting in %mode%&e."),
    COMMAND_FACTION_CREATED("command.faction.created.msg", "&3Faction Created!"),
    COMMAND_FACTION_CREATED_HELP("command.faction.created.help", "&7To learn more about factions, do /f"),
    COMMAND_FACTION_CREATED_BC("command.faction.created.bc", "&eFaction &9%faction% &ehas been &acreated &eby &f%player%"),
    COMMAND_FACTION_DISBAND_BC("command.faction.disband.bc", "&eFaction &9%faction% &ehas been &cdisbanded &eby &f%player%"),
    COMMAND_FACTION_DISBAND_FBC("command.faction.disband.faction-bc", "&c&l%name% &c&lhas disbanded the faction."),
    COMMAND_FACTION_ON_FREEZE("command.faction.on-freeze", "&cYou cannot join this faction because they are on dtr freeze."),
    COMMAND_FACTION_INVITE_BC("command.faction.invite.bc", "&e%name% &ehas been invited to the team."),
    COMMAND_FACTION_INVITE_PM("command.faction.invite.pm", "&e%name% &ehas invited you to join %faction%."),
    COMMAND_FACTION_INVITE_ALREADY_IN("command.faction.invite.already-in", "&a%name% &eis already invited to the team."),
    COMMAND_FACTION_INVITE_ALREADY_IN_TEAM("command.faction.invite.already-in-team", "&a%name% &ais already in the team."),
    COMMAND_FACTION_INVITE_MEMBER_MAX("command.faction.invite.member.max", "&cYour faction already has the max amount of members on it\'s team."),
    COMMAND_FACTION_UNINVITE_MESSAGE("command.faction.uninvite.message", "&c%name% &chas been uninvited from the team."),
    COMMAND_FACTION_UNINVITE_NOT_INVITED("command.faction.uninvite.not-invited", "&a%name% &ais not invited."),
    COMMAND_FACTION_CANNOT_AFFORD("command.faction.cannot-afford", "&cThe faction doesn't have enough money to do this."),
    COMMAND_FACTION_WITHDREW("command.faction.withdraw.withdrew", "&e%name% &ewithdrew &d$%amount% &efrom the faction balance."),
    COMMAND_FACTION_WITHDRAW_CANNOT("command.faction.withdraw.cannot", "&cYou cannot withdraw $0 or less."),
    COMMAND_FACTION_WITHDRAW_SUCCESS("command.faction.withdraw.success", "&eYou have withdrawn &d$%amount% &efrom the faction balance."),
    COMMAND_FACTION_DEPOSIT("command.faction.deposit.deposited", "&e%name% &edeposited &d$%amount% &einto the team balance."),
    COMMAND_FACTION_DEPOSIT_CANNOT("command.faction.deposit.cannot", "&cYou cannot deposit $0 or less."),
    COMMAND_FACTION_DEPOSIT_CANNOT_AFFORD("command.faction.deposit.cannot-afford", "&cYou don't have enough money to do this."),
    COMMAND_FACTION_DEPOSIT_SUCCESS("command.faction.deposit.success", "&eYou have added &d$%amount% &eto the faction balance."),
    COMMAND_FACTION_CHANGE_INVALID("command.faction.change.invalid", "&cYou cannot change this player\'s rank."),
    COMMAND_FACTION_CAPTAIN_SUCCESS("command.faction.captain.success", "&ePromoted &a%player% &eto &6Captain&e."),
    COMMAND_FACTION_CAPTAIN_PROMOTED("command.faction.captain.promoted", "&eYou have been promoted to &6Captain&e."),
    COMMAND_FACTION_CAPTAIN_SUCCESS_DEMOTE("command.faction.captain.success-demoted", "&eDemoted &a%player% &eto &6Member&e."),
    COMMAND_FACTION_CAPTAIN_DEMOTED("command.faction.captain.demoted", "&eYou have been demoted to &6Member&e."),
    COMMAND_FACTION_COLEADER_INVALID("command.faction.coleader.invalid", "&cYou can only promote players to Captain that are a &6Member&e."),
    COMMAND_FACTION_COLEADER_SUCCESS("command.faction.coleader.success", "&ePromoted &a%player% &eto &6Captain."),
    COMMAND_FACTION_COLEADER_PROMOTED("command.faction.coleader.promoted", "&eYou have been promoted to &6Captain&e."),
    COMMAND_FACTION_COLEADER_SUCCESS_DEMOTE("command.faction.coleader.success-demoted", "&eDemoted &a%player% &eto &6Member&e."),
    COMMAND_FACTION_COLEADER_DEMOTED("command.faction.coleader.demoted", "&eYou have been demoted to &6Member&e."),
    COMMAND_FACTION_LEADER_SELF("command.faction.leader.self", "&cYou cannot give yourself leader."),
    COMMAND_FACTION_LEADER_CHANGED("command.faction.leader.changed", "&eOwnership of the faction was changed to %name%&e."),
    COMMAND_FACTION_LIVES_SUCCESS("command.faction.lives.success", "&aDeposited %amount% lives to the Faction."),
    COMMAND_FACTION_LIVES_SUCCESS_FACTION("command.faction.lives.success-faction", "&6%player% &edeposited &a%amount% &elives to the faction."),
    COMMAND_FACTION_LIVES_NO_LIVES("command.faction.lives.no-lives", "&cYou do not have any lives to deposit to the Faction."),
    COMMAND_FACTION_LIVES_NOT_ENOUGH("command.faction.lives.not-enough", "&cYou do not have %amount% lives."),
    COMMAND_FACTION_REVIVE_NO_LIVES("command.faction.revive.no-lives", "&cYour faction does not have any lives."),
    COMMAND_FACTION_RENAME("command.faction.rename.msg", "&eRenamed faction to &a%name%&e."),
    COMMAND_FACTION_RENAME_BROADCAST("command.faction.rename.broadcast", "&eFaction &6%oldname% &erenamed to &a%name%&e."),
    COMMAND_FACTION_RENAME_COOLDOWN("command.faction.rename.cooldown", "&eYou are still on rename cooldown."),
    COMMAND_FACTION_RESET_CLAIMS("command.faction.reset.claims", "&eReset faction claims of %name%&e."),
    COMMAND_FACTION_SETCOLOR_SET("command.faction.setdeathban.set", "&e%name%'s color is now set to %color%"),
    COMMAND_FACTION_SETCOLOR_INVALID("command.faction.setdeathban.invalid", "&6%invalid% &eis not a valid color."),
    COMMAND_FACTION_SETDEATHBAN_SET("command.faction.setdeathban.set", "&e%name%'s deathban status is now set to %status%"),
    COMMAND_FACTION_OPEN("command.faction.open", "&eFaction &9%faction% &eis now &aopen&e."),
    COMMAND_FACTION_CLOSED("command.faction.closed", "&eFaction &9%faction% &eis now &cclosed&e."),
    COMMAND_FACTION_SETREGEN_SET("command.faction.setregen.set", "&e%name%&e's Regen Time has been set to: &d%minutes% minutes"),
    COMMAND_FACTION_SETREGEN_SET_BC("command.faction.setregen.set-bc", "&eRegen Time has been set to: &d%minutes% minutes"),
    COMMAND_FACTION_NOW_REGENERATING("command.faction.regenerating", "&e&lYour faction is now regenerating DTR."),
    COMMAND_FACTION_SETPOINTS_SET("command.faction.points.set", "&e%name%'s points have been set to: &d%points%"),
    COMMAND_FACTION_ADDPOINTS_ADDED("command.faction.points.added", "&e%name%'s points have been added with: &d%points%"),
    COMMAND_FACTION_SETDTR_SET("command.faction.setdtr.set", "&e%name%'s DTR has been set to: &d%dtr%"),
    COMMAND_FACTION_SETDTR_SET_BC("command.faction.setdtr.set-bc", "&eDTR has been set to: &d%dtr%"),
    COMMAND_FACTION_MAP_SHOWN("command.faction.map.shown", "&eThe faction map is now &ashown&e."),
    COMMAND_FACTION_MAP_HIDDEN("command.faction.map.hidden", "&eThe faction map is now &chidden&e."),
    COMMAND_FACTION_MAP_FACTION("command.faction.map.faction", "&b%faction% &eis shown with block &c%material%&e."),
    COMMAND_FACTION_MAP_NONE_NEARBY("command.faction.map.none-nearby", "&cThere are no Factions within 25 blocks from you."),
    COMMAND_FACTION_LIST_INVALID_PAGE("command.faction.list.invalid-page", "&cThat page is invalid."),
    COMMAND_FACTION_CANNOT_UNCLAIM_DTR("command.faction.unclaim.not-dtr", "&cYou can not unclaim whilst being raidable."),
    COMMAND_FACTION_UNCLAIM_BROADCAST("command.faction.unclaim.broadcast", "&eFaction claim has been voided by &d%player%."),
    COMMAND_FACTION_UNCLAIM_PLAYER("command.faction.unclaim.player", "&eSuccesfully unclaimed."),
    COMMAND_FACTION_ALLY_MAX("command.faction.ally.max", "&6Your faction or %faction% &ehas already reached the max allies limit."),
    COMMAND_FACTION_ALLY_ALREADY("command.faction.ally.already", "&cYou are already allied to this faction or the request is pending."),
    COMMAND_FACTION_ALLY_ACCEPTED("command.faction.ally.accepted", "&eYour faction is now allied with &d%faction%&e."),
    COMMAND_FACTION_ALLY_SENT("command.faction.ally.sent", "&eYou sent an ally request to &6%faction%&e."),
    COMMAND_FACTION_ALLY_PENDING("command.faction.ally.pending", "&6%faction% &ewishes to be your ally."),
    COMMAND_FACTION_UNALLY_DENIED("command.faction.unally.denied", "&eYou denied an ally request to &c%faction%&e."),
    COMMAND_FACTION_UNALLY_REMOVED("command.faction.unally.removed", "&eYou are no longer allied with &c%faction%&e."),
    COMMAND_FACTION_UNALLY_REMOVED_REQUEST("command.faction.unally.removed-request", "&eYou are no longer requested to be allied with &c%faction%&e."),
    COMMAND_FACTION_UNALLY_NOT_ALLIED("command.faction.unally.not-allied", "&cYou are not allied with this faction."),

    COMMAND_SETENDEXIT_SUCCESS("command.setendexit.success", "&aSet end exit location successfully."),
    COMMAND_SETENDEXIT_FAILURE("command.setendexit.failure", "&cFailed to set end exit location."),

    COMMAND_SETENDSPAWN_SUCCESS("command.setendspawn.success", "&aSet end spawn location successfully."),
    COMMAND_SETENDSPAWN_FAILURE("command.setendspawn.failure", "&cFailed to set end spawn location."),

    COMMAND_SETNETHEREXIT_SUCCESS("command.setnetherexit.success", "&aSet nether exit location successfully."),
    COMMAND_SETNETHEREXIT_FAILURE("command.setnetherexit.failure", "&cFailed to set nether exit location."),

    COMMAND_SETNETHERSPAWN_SUCCESS("command.setnetherspawn.success", "&aSet nether spawn location successfully."),
    COMMAND_SETNETHERSPAWN_FAILURE("command.setnetherspawn.failure", "&cFailed to set nether spawn location."),

    COMMAND_RANK_REVIVE_COOLDOWN("command.rank-revive.cooldown", "&cYou are still on cooldown for %time%."),

    FACTION_WILDERNESS("faction.wilderness", "&7Wilderness"),
    FACTION_LEAVING("faction.leaving", "&eNow leaving: %faction%"),
    FACTION_ENTERING("faction.entering", "&eNow entering: %faction%"),
    FACTION_DEATHBAN("faction.deathban", "&e(&cDeathban&e)"),
    FACTION_NONDEATHBAN("faction.non-deathban", "&e(&aNon-Deathban&e)"),
    FACTION_WARPING("faction.warping", "&eWarping to &d%faction%&e's HQ."),
    FACTION_TELEPORT_CANCELLED("faction.teleport-cancelled", "&eTeleport cancelled."),
    FACTION_CANNOT_BUILD("faction.cannot-build", "&eYou cannot build in %faction%&e\'s &eterritory."),
    FACTION_CANNOT_USE("faction.cannot-use", "&eYou cannot do this in %faction%&e\'s &eterritory."),
    FACTION_CANNOT_ATTACK_HERE("faction.cannot-attack-here", "&eYou cannot attack &c%name% &ewhile they are in a safezone."),
    FACTION_CANNOT_ATTACK_INSIDE_SAFEZONE("faction.cannot-attack-inside-safezone", "&eYou cannot attack &c%name% &ewhile you are in a safezone."),

    FACTION_NAME_TOO_SHORT("faction.name.too-short", "&cThe name must be at least %min% characters."),
    FACTION_NAME_TOO_LONG("faction.name.too-long", "&cThe name cannot be longer than %max% characters"),
    FACTION_NAME_NOT_ALPHANUMERIC("faction.name.not-alphanumeric", "&cThe name must be alphanumeric."),
    FACTION_NAME_IN_USE("faction.name.in-use", "&cThat name is already in use."),
    FACTION_NAME_BLOCKED("faction.name.blocked", "&cThat name is blocked."),

    FACTION_MEMBER_ONLNE("faction.member.online", "&aMember Online: &f%name%"),
    FACTION_MEMBER_OFFLINE("faction.member.offline", "&cMember Offline: &f%name%"),
    FACTION_MEMBER_DEATH("faction.member.death", "&cMember Death: &f%name%\n&cDTR: &f%dtr%"),
    FACTION_MEMBER_DAMAGE("faction.member.damage", "&eYou cannot damage &2%name%&e."),

    FACTION_ALLY_DAMAGE("faction.ally.damage", "&eBe careful, &e%name% &eis an ally."),

    CLAIMING_SETPOS1("claiming.setpos1", "&eSet position #1."),
    CLAIMING_SETPOS2("claiming.setpos2", "&eSet position #2."),
    CLAIMING_CLEARED("claiming.cleared", "&eClaim selection cleared."),
    CLAIMING_NOT_SELECTED("claiming.not-selected", "&cYou must select both corners."),
    CLAIMING_CANT_DORP("claiming.cant-drop", "&cYou cannot drop this."),
    CLAIMING_CANNOT_AFFORD("claiming.cannot-afford", "&cYour faction cannot afford this claim."),
    CLAIMING_CANNOT_CLAIM_HERE("claiming.cannot-claim-here", "&cYou cannot claim here."),
    CLAIMING_CANNOT_CLAIM_BORDER("claiming.cannot-claim-border", "&cYou cannot claim past the border."),
    CLAIMING_INVALID_SIZE("claiming.invalid-size", "&cYou must only claim a 5x5 area and claim must not exceed than 16 chunks."),
    CLAIMING_COST("claiming.cost", "&eClaim Cost: %cost%&e. &eCurrent size: (&f%sizeX%, %sizeZ%&e)"),
    CLAIMING_STARTED("claiming.started", "&eAttempting to claim area..."),
    CLAIMING_ALREADY_STARTED("claiming.started-already", "&eAlready attempting to claim area!"),
    CLAIMING_COMPLETED("claiming.completed", "&cClaim completed."),
    CLAIMING_CLAIM_WAND("claiming.claim-wand", "&aGave you a claim wand."),
    CLAIMING_ENDED("claiming.ended", "&cEnded claiming."),
    CLAIMING_ALREADY_CLAIMED("claiming.already-claimed", "&cYour faction already has a claim."),
    CLAIMING_RAIDABLE("claiming.raidable", "&cYou cannot claim while you are raidable."),

    DEATHBAN_KICK_MESSAGE("deathban.kick-message", "&cYou are currently death-banned until %remaining%.\nBuy lives @ %store%."),
    DEATHBAN_USED_LIFE("deathban.used-life", "&aYou have used a life to remove your deathban."),

    TIMER_CANNOT_USE("timer.cannot-use", "&cYou cannot use this for another &c&l%time% &cseconds!"),
    TIMER_CANNOT_CLAIM("timer.cannot-claim", "&cYou cannot claim whilst having a PvP timer."),
    TIMER_CANNOT_PLACE("timer.cannot-place", "&cYou cannot place blocks while in combat."),
    TIMER_AREA_CLAIMED("timer.area-claimed", "&cYou are protected and the area where you were standing was claimed so you were teleported out."),
    TIMER_ENDERPEARL_REFUNDED_GLITCH("timer.enderpearl.refunded.glitch", "&cYou cannot pearl there!"),
    TIMER_ENDERPEARL_REFUNDED_SAFEZONE("timer.enderpearl.refunded.safezone", "&cYou cannot pearl into Safezone areas."),
    TIMER_ENDERPEARL_REFUNDED_SPECIAL("timer.enderpearl.refunded.special-koth", "&cYou cannot pearl into this koth area."),
    TIMER_LOGOUT("timer.logout.message", "&e&lLogging out... &ePlease wait &c30 &eseconds."),
    TIMER_LOGOUT_CANCELLED("timer.logout.cancelled", "&e&lLOGOUT &c&lCANCELLED!"),
    TIMER_LOGOUT_RUNNING("timer.logout.running", "&cYou are already logging out."),
    TIMER_LOGOUT_SUCCESS("timer.logout.success", "&cYou have been successfully logged out."),
    TIMER_SPAWN_RUNNING("timer.spawn.running", "&cYou are already spawning."),
    TIMER_SPAWNTAG("timer.spawntag.message", "&eYou have been spawn-tagged for &c30 &eseconds!"),
    TIMER_SPAWNTAG_END("timer.spawntag.end", "&eYou cannot enter end whilst having a combat timer."),
    TIMER_STUCK("timer.stuck.message", "&cStarted stuck timer, please wait 2 minutes and 30 seconds. Do not move more than 5 blocks."),
    TIMER_STUCK_CANCELLED("timer.stuck.cancelled", "&cYou moved more than 5 blocks so your stuck timer was cancelled."),
    TIMER_STUCK_RUNNING("timer.stuck.running", "&cYou have already started your stuck timer."),
    TIMER_STUCK_STARTING("timer.stuck.starting", "&aAttempting to find a safe nearby location..."),
    TIMER_STUCK_SUCCESS("timer.stuck.success", "&aYou have been teleported to the Wilderness nearby."),
    TIMER_PROTECTED("timer.protected", "&cYou cannot attack other players while you have a PVP Timer."),
    TIMER_ATTACK_PROTECTED("timer.attack-protected", "&cYou cannot attack this player as they currently have a PVP Timer."),

    EVENT_CONQUEST_STARTED("event.conquest.started", "&7[Conquest] &eConquest &6%name% &ehas started."),
    EVENT_CONQUEST_CANCELLED("event.conquest.end.cancelled", "&7[Conquest] &eConquest &6%name% &ehas been stopped."),
    EVENT_CONQUEST_CAPTURED("event.conquest.stopped.captured", "&7[Conquest] &eConquest &6%name% &ewas captured."),
    EVENT_CONQUEST_CONTROL("event.conquest.control", "&7[Conquest] &6%faction% &ehas started to control %zone%&e."),
    EVENT_CONQUEST_CONTROL_TEAM("event.conquest.control-team", "&7[Conquest] &eYour team is now controlling %zone%&e."),
    EVENT_CONQUEST_KNOCK("event.conquest.knock", "&7[Conquest] &6%faction% &ehas lost control of %zone%&e."),
    EVENT_CONQUEST_CAPPED("event.conquest.zone-capped", "&7[Conquest] %zone% &ewas captured by &6%name%&e."),
    EVENT_CONQUEST_CAPPING("event.conquest.zone-capping", "&7[Conquest] &eAttempting to capture %zone% &b(%time%)"),
    EVENT_KOTH_STARTED("event.koth.started", "&7[Koth] &eKoth &6%name% &ehas started."),
    EVENT_KOTH_CANCELLED("event.koth.end.cancelled", "&7[Koth] &eKoth &6%name% &ehas been stopped."),
    EVENT_KOTH_CAPTURED("event.koth.stopped.captured", "&7[Koth] &eKoth &6%name% &ewas captured."),
    EVENT_KOTH_CONTROL("event.koth.control", "&7[Koth] &6%faction% &ehas started to control %name%&e."),
    EVENT_KOTH_CONTROL_TEAM("event.koth.control-team", "&7[Koth] &6Your team is now controlling &6%name%&e."),
    EVENT_KOTH_CONTROL_PLAYER("event.koth.control-player", "&7[Koth] &6You are now now controlling &6%name%&e."),
    EVENT_KOTH_KNOCK("event.koth.knock", "&7[Koth] &6%faction% &ehas lost control of %name%&e."),
    EVENT_KOTH_CONTROLLING("event.koth.controlling", "&7[Koth] &6%name% &eis now at &6%time%."),
    EVENT_DTC_STARTED("event.dtc.started", "&7[Koth] &eKoth &6%name% &ehas started."),
    EVENT_DTC_CANCELLED("event.dtc.end.cancelled", "&7[DTC] &eDTC &6%name% &ehas been stopped."),
    EVENT_DTC_CAPTURED("event.dtc.captured", "&7[DTC] &e&6%name%s &ebroke the DTCs core (Points %points%)"),
    EVENT_DTC_WON("event.dtc.stopped.won", "&7[DTC] &eDTC &6%name% &ehas ended."),
    EVENT_DTC_BROKEN("event.conquest.zone-capped", "&7[DTC] &eThe DTC core has been broken by &6%name%&e. (%points%x)"),

    MODMODE_ENABLED("mod-mode.enabled", "&eMod mode has been &aenabled."),
    MODMODE_DISABLED("mod-mode.disabled", "&eMod mode has been &cdisabled."),
    MODMODE_BYPASS_ENABLED("mod-mode.bypass-enabled", "&eMod mode &abypass&e has been &aenabled."),
    MODMODE_BYPASS_DISABLED("mod-mode.bypass-disabled", "&eMod mode &cbypass&e has been &cdisabled."),
    MODMODE_CANNOT_DO("mod-mode.cannot-do", "&cYou cannot do this while in mod mode!"),
    MODMODE_INVSEE("mod-mode.invsee", "&eNow opening %player%\'s inventory..."),
    MODMODE_INSPECT("mod-mode.inspect", "&eNow inspecting %player%\'s inventory... (no editing)"),
    MODMODE_VANISH_TOGGLED("mod-mode.vanish.toggled", "&eVanish: %status%"),
    MODMODE_FLY_TOGGLED("mod-mode.fly.toggled", "&eFly: %status%"),
    MODMODE_TELEPORT_PLAYER("mod-mode.teleport-player", "%primary%Teleporting to player %secondary%%name%%primary%."),
    MODMODE_HIDESTAFF("mod-mode.hidestaff", "%primary%You are now %status% %primary%staff."),

    PANIC_INITIATED("panic.initiated", "&cYou have initiated your panic sequence."),
    PANIC_BROADCAST("panic.broadcast", "&c%player% has initiated their panic sequence!"),
    PANIC_FINE("panic.fine", "&eYou have stopped your panic sequence."),

    FREEZE_FROZE_PLAYER("freeze.froze-player", "&eYou have frozen %player%."),
    FREEZE_UNFROZEN("freeze.unfrozen", "&eYou have been unfrozen."),
    FREEZE_UNFROZE_PLAYER("freeze.unfroze-player", "&eYou have unfrozen %player%."),
    FREEZE_CANNOT("freeze.cannot", "&cYou cannot do that while you\'re frozen."),
    FREEZE_CANNOT_ATTACK("freeze.cannot-attack", "&cThat player is currently frozen."),
    FREEZE_SERVER_FROZEN("freeze.server-frozen", "&eThe server is now frozen."),
    FREEZE_SERVER_UNFROZEN("freeze.server-unfrozen", "&eThe server is now unfrozen."),

    RECLAIM_CLAIMED("reclaimed.claimed", "&cYou have already claimed your items for this map."),
    RECLAIM_CLAIMED_ITEMS("reclaimed.claimed-items", "&eYou have claimed your items for rank &6%rank%&e."),
    RECLAIM_NO_REWARDS("reclaimed.no-rewards", "&cYou have no rewards to reclaim."),

    KIT_ENABLED("kit.enabled", "&bClass: &b&l%name% &7--> &aEnabled!\n&bClass Info: &a%website%/%name%"),
    KIT_DISABLED("kit.disabled", "&bClass: &b&l%name% &7--> &cDisabled!"),

    ARCHER_TAG("archer.tag", "&e[&9Arrow Range &e(&c%range%&e)] &6Marked player for %time% seconds. &9(%hearts% hearts)"),
    ARCHER_CANNOT_TAG("archer.cannot-tag", "&e[&9Arrow Range &e(&c%range%&e)] &cCannot mark other Archers. &9(%hearts% hearts)"),

    BARD_SAFEZONE("bard.safezone", "&cYou cannot use Bard effects in SafeZone areas."),
    BARD_NOT_ENOUGH("bard.not-enough", "&cYou do not have enough energy to use this effect."),
    BARD_USED_EFFECT("bard.used-effect", "&eYou used &b%effect% &efor &b%energy%&e."),

    MINER_INVIS_STATUS("miner.invis-change", "&eMiner Invisibility: %status%"),

    ROGUE_BACKSTAB("rogue.backstab", "&cYou backstabbed %backstabbed%"),
    ROGUE_BACKSTABBED("rogue.backstabbed", "&cYou were backstabbed by %backstabber%"),

    CROWBAR_INVALID_WORLD("crowbar.invalid-world", "&cYou cannot use Spawners in %world%."),
    CROWBAR_INVALID_LOCATION("crowbar.invalid-location", "&cYou cannot use the crowbar in that territory."),

    ITEM_STATISTIC_LORE_KILL("statistic.lore.kill", "&e%killer% &fkilled &e%dead% &7%time%"),
    ITEM_STATISTIC_LORE_DEATH("statistic.lore.death", "&e%dead% &fdied to &e%killer% &7%time%"),
    ITEM_STATISTIC_HEADER_DEATHS("statistic.header.deaths", "&7&lDeaths:"),
    ITEM_STATISTIC_HEADER_KILLS("statistic.header.kills", "&7&lKills:"),

    TELEPORT_LOCATION("teleport.location", "&eTeleporting to %x%, %y%, %z%."),
    TELEPORT_TOP("teleport.top", "&eTeleporting to the top..."),
    TELEPORT_PLAYER_HERE("teleport.player.here", "&eTeleporting to %name% to %you%."),
    TELEPORT_PLAYER_ONLINE("teleport.player.online", "&eTeleporting to player %name%."),
    TELEPORT_ALL("teleport.all", "&eTeleporting the server to you..."),

    GAMEMODE_CREATIVE("gamemode.creative.switched", "&eSwitched gamemode to Creative."),
    GAMEMODE_CREATIVE_OTHER("gamemode.survival.other", "&eYou set %target% his gamemode to creative"),
    GAMEMODE_CREATIVE_FOR_OTHER("gamemode.survival.for-other", "&eYour gamemode was set to creative by %player%"),
    GAMEMODE_SURVIVAL("gamemode.survival.switched", "&eSwitched gamemode to Survival."),
    GAMEMODE_SURVIVAL_OTHER("gamemode.survival.other", "&eYou set %target% his gamemode to survival"),
    GAMEMODE_SURVIVAL_FOR_OTHER("gamemode.survival.for-other", "&eYour gamemode was set to survival by %player%"),

    SUBCLAIM_ALREADY_EXISTS("subclaim.already-exists", "&cA subclaim already exists on this block."),
    SUBCLAIM_NEED_FACTION("subclaim.need-faction", "&cYou need a Faction to create a subclaim."),
    SUBCLAIM_INVALID_RANK("subclaim.invalid-rank", "&cYou need to be a Captain or above to create a subclaim."),
    SUBCLAIM_INVALID_TERRITORY("subclaim.invalid-territory", "&cYou need to be in your Faction\'s territory to create a subclaim."),
    SUBCLAIM_INVALID_MEMBER("subclaim.invalid-faction-member", "&c%name% is not on your team."),
    SUBCLAIM_CANNOT_OPEN("subclaim.cannot-open", "&cYou have no permission to open this subclaim."),

    DEATHMESSAGE_CONTACT("deathmessage.contact", "&c%dead%&4[%dead_kills%] &elost a battle with a Cactus"),
    DEATHMESSAGE_CUSTOM("deathmessage.custom", "&c%dead%&4[%dead_kills%] &ehas died"),
    DEATHMESSAGE_DROWNING("deathmessage.drowning", "&c%dead%&4[%dead_kills%] &ethought he could swim"),
    DEATHMESSAGE_LOGGER_PLAYER("deathmessage.logger.player", "&7(Combat-Logger) &c%dead%&4[%dead_kills%] &ewas killed by &c%killer%&4[%killer_kills%]"),
    DEATHMESSAGE_LOGGER_UNKNOWN("deathmessage.logger.unknown", "&7(Combat-Logger) &c%dead%&4[%dead_kills%] &ehas died"),
    DEATHMESSAGE_ENTITY_ENTITY("deathmessage.entity-attack.entity", "&c%dead%&4[%dead_kills%] &ewas slain by a &c%killer%"),
    DEATHMESSAGE_ENTITY_PLAYER("deathmessage.entity-attack.player", "&c%dead%&4[%dead_kills%] &ewas slain by &c%killer%&4[%killer_kills%] &eusing &c%item%"),
    DEATHMESSAGE_ENTITY_PLAYER_NOITEM("deathmessage.entity-attack.player-noitem", "&c%dead%&4[%dead_kills%] &ewas fisted by &c%killer%&4[%killer_kills%]"),
    DEATHMESSAGE_PROJECTILE_ENTITY("deathmessage.projectile.entity", "&c%dead%&4[%dead_kills%] &edied to a &cSkeleton"),
    DEATHMESSAGE_PROJECTILE_PLAYER("deathmessage.projectile.player", "&c%dead%&4[%dead_kills%] &ewas shot by &c%killer%&4[%killer_kills%] &eusing &c%item% &efrom &9&l%distance% blocks"),
    DEATHMESSAGE_PROJECTILE_PLAYER_NOITEM("deathmessage.projectile.player-noitem", "&c%dead%&4[%dead_kills%] &ewas shot by &c%killer%&4[%killer_kills%]"),
    DEATHMESSAGE_BLOCK_EXPLOSION("deathmessage.block-explosion", "&c%dead%&4[%dead_kills%] &eblew up"),
    DEATHMESSAGE_ENTITY_EXPLOSION("deathmessage.entity-explosion", "&c%dead%&4[%dead_kills%] &eblew up"),
    DEATHMESSAGE_FALL("deathmessage.fall", "&c%dead%&4[%dead_kills%] &ehit the ground too hard"),
    DEATHMESSAGE_FALLING_BLOCK("deathmessage.falling_block", "&c%dead%&4[%dead_kills%] &edied to a falling block"),
    DEATHMESSAGE_FIRE("deathmessage.fire", "&c%dead%-4[%dead_kills%] &ethought he was a firefighter"),
    DEATHMESSAGE_FIRE_TICK("deathmessage.fire-tick", "&c%dead%&4[%dead_kills%] &ethought he was a firefighter"),
    DEATHMESSAGE_LAVA("deathmessage.lava", "&c%dead%&4[%dead_kills%] &etried to swim in lava"),
    DEATHMESSAGE_LIGHTNING("deathmessage.lightning", "&c%dead%&4[%dead_kills%] &ehas been electrocuted"),
    DEATHMESSAGE_MAGIC("deathmessage.magic", "&c%dead%&4[%dead_kills%] &ethought he was a wizaard"),
    DEATHMESSAGE_MELTING("deathmessage.melting", "&c%dead%&4[%dead_kills%] &etried to make some smores\'s"),
    DEATHMESSAGE_POISON("deathmessage.poison", "&c%dead%&4[%dead_kills%] &ehas died from poison"),
    DEATHMESSAGE_STARVATION("deathmessage.starvation", "&c%dead%&4[%dead_kills%] &edied of starvation"),
    DEATHMESSAGE_SUFFOCATION("deathmessage.suffocation", "&c%dead%&4[%dead_kills%] &ehas suffocated in a wall"),
    DEATHMESSAGE_SUICIDE("deathmessage.suicide", "&c%dead%&4[%dead_kills%] &ehas committed suicide"),
    DEATHMESSAGE_THORNS("deathmessage.thorns", "&c%dead%&4[%dead_kills%] &edied to a prickly little thang"),
    DEATHMESSAGE_VOID("deathmessage.void", "&c%dead%&4[%dead_kills%] &efell into the void"),
    DEATHMESSAGE_WITHER("deathmessage.wither", "&c%dead%&4[%dead_kills%] &ehas whitered away");

    @Getter private static LocaleFile localeFile;
    @Getter private static YamlConfiguration config;

    @Getter private String path, def;

    Locale(String path, String def) {
        this.path = path;
        this.def = def;
    }

    /**
     * Gets the default value.
     *
     * @return def
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * Gets the string from the config file and converts it to a chatcolored message.
     *
     * @return Config string.
     */
    @Override
    public String toString() {
        return C.color(config.getString(path, def))
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                .replace("%store%", ConfigValues.SERVER_STORE)
                .replace("%website%", ConfigValues.SERVER_WEBSITE)
                .replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK);
    }

    private static Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    public String format(Object... arguments) {
        String toString = toString();
        StringBuffer toReturn = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(toString);
        int count = 0;

        while(matcher.find() && count != arguments.length) {
            String replacement = arguments[count].toString();
            matcher.appendReplacement(toReturn, Matcher.quoteReplacement(replacement));

            count++;
        }

        return matcher.appendTail(toReturn).toString();
    }

    /**
     * Loads and saves values from the file.
     */
    public static void load(JavaPlugin plugin, boolean override) {
        localeFile = new LocaleFile();
        config = localeFile.getConfig();

        for(Locale item : values()) {
            if(override || config.getString(item.getPath()) == null)
                config.set(item.getPath(), item.getDefault());
        }

        localeFile.save();
    }
}
