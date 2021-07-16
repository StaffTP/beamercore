package me.hulipvp.hcf.ui;

import dev.fusiongames.gutilities.gUtils;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.game.player.data.setting.HCFSetting;
import me.hulipvp.hcf.game.player.data.setting.SettingType;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.ui.sidebar.Sidebar;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.type.RampageTimer;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.Placeholders;
import me.hulipvp.hcf.utils.TimeUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class ScoreboardHandler {

    public ScoreboardHandler() {
        setupScoreboard();
    }

    private void setupScoreboard() {
        new Sidebar(CC.translate(ConfigValues.SCOREBOARD_TITLE)).setLines(player -> {
            List<String> lines = new ArrayList<>();
            HCFProfile profile = HCFProfile.getByPlayer(player);
            if (profile == null)
                return null;

            HCFSetting setting = profile.getSetting(SettingType.SCOREBOARD);
            if (setting != null && !setting.isValue())
                return null;

            lines.add(CC.translate("&7&m--------------------"));

            for (String line : ConfigValues.SCOREBOARD_LINES) {
                if (!line.isEmpty()) {
                    if (line.contains("%mod_lines%")) {
                        if (profile.getVanish() != null && profile.getVanish().isModMode()) {
                            for (String string : ConfigValues.SCOREBOARD_MOD_LINES) {
                                String finalLine = Placeholders.replacePlaceholders(string
                                        .replace("%vanished%", profile.getVanish() != null ? (profile.getVanish().isVanished() ? "&aEnabled" : "&cDisabled") : "")
                                        .replace("%modmode%", profile.getVanish() != null ? (profile.getVanish().isModMode() ? "&aEnabled" : "&cDisabled") : "")
                                        .replace("%modmode_bypass%", profile.getVanish() != null ? (profile.getVanish().isBypass() ? "&aEnabled" : "&cDisabled") : "")
                                        .replace("%gamemode%", WordUtils.capitalizeFully(player.getGameMode().name()))
                                        .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())), player, profile)
                                        .replace("%modules%", profile.isStaffModule() ? "&aOn" : "&cOff");

                                if (finalLine != null && !finalLine.isEmpty()) lines.add(finalLine);
                            }
                        }
                    } else if (line.contains("%conquest_lines%")) {
                        if (Conquest.getActiveConquest() != null) {
                            Conquest conquest = Conquest.getActiveConquest();

                            lines.add(ConfigValues.SCOREBOARD_CONQUEST_HEADER);
                            Map<String, Integer> points = conquest.getPoints();
                            if (points.size() > 0) {
                                List<Map.Entry<String, Integer>> entries = new ArrayList<>(points.entrySet());
                                entries
                                        .stream()
                                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                                        .limit(3)
                                        .forEach(entry -> lines.add(ConfigValues.SCOREBOARD_CONQUEST_SCORE
                                                .replace("%faction%", Faction.getFaction(UUID.fromString(entry.getKey())).getDisplayFor(player))
                                                .replace("%points%", String.valueOf(entry.getValue()))
                                        ));
                            } else {
                                lines.add(ConfigValues.SCOREBOARD_CONQUEST_NO_SCORES);
                            }
                        }
                    } else if (line.contains("%dtc_lines%")) {
                        if (DTC.getActiveDTC() != null) {
                            DTC dtc = DTC.getActiveDTC();

                            lines.add(ConfigValues.SCOREBOARD_DTC_HEADER);
                            Map<String, Integer> points = dtc.getPoints();
                            if (points.size() > 0) {
                                List<Map.Entry<String, Integer>> entries = new ArrayList<>(points.entrySet());
                                entries
                                        .stream()
                                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                                        .limit(3)
                                        .forEach(entry -> lines.add(ConfigValues.SCOREBOARD_DTC_SCORE
                                                .replace("%faction%", Faction.getFaction(UUID.fromString(entry.getKey())).getDisplayFor(player))
                                                .replace("%points%", String.valueOf(entry.getValue()))
                                        ));
                            } else {
                                lines.add(ConfigValues.SCOREBOARD_DTC_NO_SCORES);
                            }
                        }
                    } else if (line.contains("%koth_lines%")) {
                        if (Koth.getActiveKoth() != null) {
                            Koth koth = Koth.getActiveKoth();

                            lines.add(((koth.isSpecial()) ? ConfigValues.SCOREBOARD_KOTH_SPECIAL : ConfigValues.SCOREBOARD_KOTH_NORMAL).replace("%name%", koth.getName()).replace("%time%", TimeUtils.getFormatted(koth.getTimer())));
                        }
                    } else if (line.contains("%customtimer_lines%")) {
                        if (profile.getTimers().stream().anyMatch(playerTimer -> playerTimer.getType().equals(PlayerTimerType.CUSTOM))) {
                            profile.getTimers().stream().filter(playerTimer -> playerTimer.getType().equals(PlayerTimerType.CUSTOM)).forEach(playerTimer -> {
                                lines.add(ConfigValues.SCOREBOARD_CUSTOMTIMER_LINE.replace("%text%", playerTimer.getText()).replace("%timer%", playerTimer.getDisplay()));
                            });
                        }
                    } else if (line.contains("%customservertimer_lines%")) {
                        if (Timer.getTimers().values().stream().anyMatch(timer -> timer instanceof ServerTimer && ((ServerTimer) timer).getType().equals(ServerTimerType.CUSTOM))) {
                            Timer.getTimers().values().stream().filter(timer -> timer instanceof ServerTimer && ((ServerTimer) timer).getType().equals(ServerTimerType.CUSTOM)).forEach(serverTimer -> {
                                lines.add(ConfigValues.SCOREBOARD_CUSTOMSERVERTIMER_LINE.replace("%text%", ((ServerTimer) serverTimer).getText()).replace("%timer%", ((ServerTimer) serverTimer).getDisplay()));
                            });
                        }
                    } else if (line.contains("%rampage_lines%")) {
                        if (ServerTimer.getTimer(ServerTimerType.RAMPAGE) != null) {
                            RampageTimer rampageTimer = (RampageTimer) ServerTimer.getTimer(ServerTimerType.RAMPAGE);
                            for (String string : ConfigValues.SCOREBOARD_RAMPAGE_LINES) {
                                String finalLine = Placeholders.replacePlaceholders(string
                                        .replace("%rampage-remaining%", "" + rampageTimer.getDisplay())
                                        .replace("%rampage-kills%", "" + rampageTimer.getPlayerKills(player))
                                        .replace("%rampage-top-kills%", "" + rampageTimer.getTopKillerKills()), player, profile);
                                if (finalLine != null && !finalLine.isEmpty()) lines.add(finalLine);
                            }
                        }
                    } else if (line.contains("%extraction_lines%")) {
                        if (ServerTimer.getTimer(ServerTimerType.EXTRACTION) != null) {
                            ServerTimer rampageTimer = ServerTimer.getTimer(ServerTimerType.EXTRACTION);
                            for (String string : ConfigValues.SCOREBOARD_EXTRACTION_LINES) {
                                String finalLine = Placeholders.replacePlaceholders(string
                                        .replace("%extraction-remaining%", "" + rampageTimer.getDisplay()), player, profile);
                                if (finalLine != null && !finalLine.isEmpty()) lines.add(finalLine);
                            }
                        }
                    } else {
                        String finalLine = Placeholders.replacePlaceholders(line, player, profile);
                        if (finalLine != null && !finalLine.isEmpty()) lines.add(finalLine);
                    }


                }
            }

            lines.add("&d&lClaim&7: &f" + getLocation(player));

            if (gUtils.getInstance().getCooldowns().getOppearl().onCooldown(player)) {
                lines.add(CC.translate("&4&lOP Pearl&7: &c" + gUtils.getInstance().getCooldowns().getOppearl().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getWebgun().onCooldown(player)) {
                lines.add(CC.translate("&e&lWebGun&7: &c" + gUtils.getInstance().getCooldowns().getWebgun().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getTimestone().onCooldown(player)) {
                lines.add(CC.translate("&5&lTimeStone&7: &c" + gUtils.getInstance().getCooldowns().getTimestone().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getOppearl().onCooldown(player)) {
                lines.add(CC.translate("&6&lTeleport Egg&7: &c" + gUtils.getInstance().getCooldowns().getTeleportegg().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getTeleportbow().onCooldown(player)) {
                lines.add(CC.translate("&2&lTeleport Bow&7: &c" + gUtils.getInstance().getCooldowns().getTeleportbow().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getSwitchstick().onCooldown(player)) {
                lines.add(CC.translate("&6&lSwitch Stick&7: &c" + gUtils.getInstance().getCooldowns().getSwitchstick().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getSwapperaxe().onCooldown(player)) {
                lines.add(CC.translate("&9&lSwapper Axe&7: &c" + gUtils.getInstance().getCooldowns().getSwapperaxe().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getOppearl().onCooldown(player)) {
                lines.add(CC.translate("&4&lOP Pearl&7: &c" + gUtils.getInstance().getCooldowns().getOppearl().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getSnowport().onCooldown(player)) {
                lines.add(CC.translate("&a&lSnowport&7: &c" + gUtils.getInstance().getCooldowns().getSnowport().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getPotioncounter().onCooldown(player)) {
                lines.add(CC.translate("&d&lPotion Counter&7: &c" + gUtils.getInstance().getCooldowns().getPotioncounter().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getMedkit().onCooldown(player)) {
                lines.add(CC.translate("&a&lMed Kit&7: &c" + gUtils.getInstance().getCooldowns().getMedkit().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getExoticbone().onCooldown(player)) {
                lines.add(CC.translate("&5&lExotic Bone&7: &c" + gUtils.getInstance().getCooldowns().getExoticbone().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getBelchbomb().onCooldown(player)) {
                lines.add(CC.translate("&2&lBelch Bomb&7: &c" + gUtils.getInstance().getCooldowns().getBelchbomb().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getCombo().onCooldown(player)) {
                lines.add(CC.translate("&e&lCombo&7: &c" + gUtils.getInstance().getCooldowns().getCombo().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getNinjastar().onCooldown(player)) {
                lines.add(CC.translate("&b&lNinja Star&7: &c" + gUtils.getInstance().getCooldowns().getNinjastar().getRemaining(player)));
            }

            if (gUtils.getInstance().getCooldowns().getTimewarp().onCooldown(player)) {
                lines.add(CC.translate("&6&lTimeWarp&7: &c" + gUtils.getInstance().getCooldowns().getTimewarp().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getArmorViolator().onCooldown(player)) {
                lines.add(CC.translate("&b&lArmorViolator&7: &c" + gUtils.getInstance().getCooldowns().getArmorViolator().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getReverseSwitcher().onCooldown(player)) {
                lines.add(CC.translate("&b&lReverse Switcher&7: &c" + gUtils.getInstance().getCooldowns().getReverseSwitcher().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getCloseCall().onCooldown(player)) {
                lines.add(CC.translate("&6&lClose Call&7: &c" + gUtils.getInstance().getCooldowns().getCloseCall().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getRiskyMode().onCooldown(player)) {
                lines.add(CC.translate("&c&lRisky Mode&7: &c" + gUtils.getInstance().getCooldowns().getRiskyMode().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getLastresort().onCooldown(player)) {
                lines.add(CC.translate("&d&lLast Resort&7: &c" + gUtils.getInstance().getCooldowns().getLastresort().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getAggressionorb().onCooldown(player)) {
                lines.add(CC.translate("&6&lAggression Orb&7: &c" + gUtils.getInstance().getCooldowns().getAggressionorb().getRemaining(player)));
            }
            if (gUtils.getInstance().getCooldowns().getGlobalCooldown().onCooldown(player)) {
                lines.add(CC.translate("&dAbility Cooldown&7: &c" + gUtils.getInstance().getCooldowns().getGlobalCooldown().getRemaining(player)));
            }

            if (profile.getFactionObj() != null) {
                if (profile.getFactionObj().getFactionFocus() != null) {
                    Location loc = profile.getFactionObj().getFactionFocus().getHome();
                    PlayerFaction targetFaction = profile.getFactionObj().getFactionFocus();
                    if (targetFaction != null) {
                        lines.add(CC.translate("&7&m--------------------"));
                        lines.add(CC.translate("&d&lTeam&7: &f" + targetFaction.getName()));
                        lines.add(CC.translate("&d&lDTR&7: &f" + targetFaction.getDtr()));
                        lines.add(CC.translate("&d&lOnline&7: &f" + targetFaction.getOnlineCount() + "&7/&f" + targetFaction.getMembers().size()));
                        lines.add(CC.translate("&d&lHome&7: &f" + targetFaction.getHome().getBlockX() + "&7,&f " + targetFaction.getHome().getBlockZ()));
                    }


                }

            }



            lines.add(CC.translate("&7&m--------------------"));

            if (lines.size() <= 2) {
                return null;
            }

            return lines;
        }).build(HCF.getInstance());
    }
    public static String getLocation(Player player) {
        Faction currentTerritory = Faction.getByLocation(player.getLocation());
        if(currentTerritory != null) {
            if(currentTerritory instanceof PlayerFaction) {
                PlayerFaction pf = (PlayerFaction) currentTerritory;
                return pf.getRelationColor(player.getUniqueId()) + pf.getName();
            } else {
                if(currentTerritory instanceof SystemFaction) {
                    SystemFaction sf = (SystemFaction) currentTerritory;
                    return sf.getColoredName();
                } else {
                    return currentTerritory.getName();
                }
            }
        } else {
            return Locale.FACTION_WILDERNESS.toString();
        }
    }

}
