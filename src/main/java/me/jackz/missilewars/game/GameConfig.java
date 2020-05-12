package me.jackz.missilewars.game;

public class GameConfig {
    private int item_interval_sec = 30;
    private boolean prioritize_defense = false;
    private int max_items = 1;

    public void reload() {
        //load config
    }

    public int getItemInterval() {
        return item_interval_sec;
    }

    public boolean prioritizeDefenseEnabled() {
        return prioritize_defense;
    }

    public int getMaxItems() {
        return max_items;
    }
}
