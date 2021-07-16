package me.hulipvp.hcf.ui.sidebar;

public class SidebarStyle {

    private int startNumber;
    private boolean descend;

    public SidebarStyle(int startNumber, boolean descend) {
        this.startNumber = startNumber;
        this.descend = descend;
    }

    public static SidebarStyle getKohiStyle() {
        return new SidebarStyle(15, true);
    }

    public static SidebarStyle getViperStyle() {
        return new SidebarStyle(-1, true);
    }

    public static SidebarStyle getModernStyle() {
        return new SidebarStyle(1, false);
    }

    int getStartNumber() {
        return startNumber;
    }

    boolean isDescend() {
        return descend;
    }

}
