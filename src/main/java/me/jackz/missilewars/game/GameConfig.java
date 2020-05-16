package me.jackz.missilewars.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class GameConfig {
    private int item_interval_sec = 20;
    private boolean prioritize_defense = false;
    private int max_items = 1;
    private int randomize_mode = 0; //0 -> ALL, 1 -> Per team, 2 -> Per individual

    public final static Location RED_SPAWNPOINT = new Location(Bukkit.getWorld("world"),-27, 78, -65, 0, 0);
    public final static Location GREEN_SPAWNPOINT = new Location(Bukkit.getWorld("world"),-27, 78, 65, 180, 0);

    public final static Location RED_LOBBY_SPAWNPOINT = new Location(Bukkit.getWorld("world"), -81,78,-18.5, 90, 0);
    public final static Location GREEN_LOBBY_SPAWNPOINT = new Location(Bukkit.getWorld("world"), -81,78,18.5, 90, 0);

    public final static Location SPAWN_LOCATION = new Location(Bukkit.getWorld("world"),-100.5 ,71,.5);


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

    public int getRandomizeMode() {
        return randomize_mode;
    }

}
