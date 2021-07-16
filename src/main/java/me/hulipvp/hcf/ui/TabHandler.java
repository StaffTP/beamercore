package me.hulipvp.hcf.ui;

import me.allen.ziggurat.ZigguratAdapter;
import me.allen.ziggurat.objects.BufferedTabObject;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.backend.files.feature.TabFile;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TabHandler implements ZigguratAdapter {

	@Override
	public String getHeader() {
		return "" + CC.translate(TabFile.getTab().getHeader());
	}

	@Override
	public String getFooter() {
		return "" + CC.translate(TabFile.getTab().getFooter());
	}

	@Override
	public Set<BufferedTabObject> getSlots(Player player) {
		Set<BufferedTabObject> tabObjects = new HashSet<>();

		HCFProfile profile = HCFProfile.getByPlayer(player);
		int i = 1;

		boolean isLunarPlayer = HCF.getInstance().getLunarHook() != null && HCF.getInstance().getLunarHook().isLunarPlayer(player);

		for (String row : TabFile.getTab().getLeftRows()) {
			String finalRow = Placeholders.replacePlaceholders(row, player, profile);
			tabObjects.add(new BufferedTabObject().text((finalRow != null && !finalRow.isEmpty()) ? finalRow : "").slot(i).ping(isLunarPlayer ? -1 : 0));
			i++;
		}


		for (String row : TabFile.getTab().getCenterRows()) {
			String finalRow = Placeholders.replacePlaceholders(row, player, profile);
			tabObjects.add(new BufferedTabObject().text((finalRow != null && !finalRow.isEmpty()) ? finalRow : "").slot(i).ping(isLunarPlayer ? -1 : 0));
			i++;
		}


		for (String row : TabFile.getTab().getRightRows()) {
			String finalRow = Placeholders.replacePlaceholders(row, player, profile);
			tabObjects.add(new BufferedTabObject().text((finalRow != null && !finalRow.isEmpty()) ? finalRow : "").slot(i).ping(isLunarPlayer ? -1 : 0));
			i++;
		}

		return tabObjects;
	}


}
