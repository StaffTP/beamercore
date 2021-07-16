package me.hulipvp.hcf.utils.ui;

import lombok.Getter;

import java.util.List;

public class Tab {

	@Getter private boolean enabled;
	@Getter private String title;
	@Getter private String header;
	@Getter private String footer;
	@Getter private List<String> leftRows;
	@Getter private List<String> centerRows;
	@Getter private List<String> rightRows;

	public Tab(boolean enabled, String title, String header, String footer, List<String> leftRows, List<String> centerRows, List<String> rightRows) {
		this.enabled = enabled;
		this.title = title;
		this.header = header;
		this.footer = footer;
		this.title = title;
		this.leftRows = leftRows;
		this.centerRows = centerRows;
		this.rightRows = rightRows;
	}

}
