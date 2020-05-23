package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GameConfig {
    private int item_interval_sec = 20;
    private boolean prioritize_defense = false;
    private boolean allow_midgame_joins = false;
    private int max_items = 1;
    private int randomize_mode = 0; //0 -> ALL, 1 -> Per team, 2 -> Per individual

    public final static int DEFAULT_item_interval_sec = 15;
    public final static boolean DEFAULT_prioritize_defense = false;
    public final static boolean DEFAULT_allow_midgame_joins = false;
    public final static int DEFAULT_max_items = 1;
    public final static int DEFAULT_randomize_mode = 0; //0 -> ALL, 1 -> Per team, 2 -> Per individual


    private static File file;
    private static YamlConfiguration config;

    //These values are definitely temporarily
    public final static Location RED_SPAWNPOINT = new Location(GameManager.WORLD,-27, 78, -65, 0, 0);
    public final static Location GREEN_SPAWNPOINT = new Location(GameManager.WORLD,-27, 78, 65, 180, 0);

    public final static Location RED_LOBBY_SPAWNPOINT = new Location(GameManager.WORLD, -81,78,-18.5, 90, 0);
    public final static Location GREEN_LOBBY_SPAWNPOINT = new Location(GameManager.WORLD, -81,78,18.5, 90, 0);

    public final static Location SPAWN_LOCATION = new Location(GameManager.WORLD,-100.5 ,71,.5);

    public GameConfig() {
        file = new File(MissileWars.getInstance().getDataFolder(),"config.yml");
        if(!file.exists()) MissileWars.getInstance().saveResource("config.yml",false);

        config = YamlConfiguration.loadConfiguration(file);
        config.addDefault("item-interval-seconds", DEFAULT_item_interval_sec);
        config.addDefault("prioritize-defense-items", DEFAULT_prioritize_defense);
        config.addDefault("max-item-count", DEFAULT_max_items);
        config.addDefault("randomize-mode", DEFAULT_randomize_mode);
        config.addDefault("allow-midgame-joins", DEFAULT_allow_midgame_joins);
        reload();
    }


    public void reload() {
        item_interval_sec = config.getInt("item-interval-seconds", DEFAULT_item_interval_sec);
        prioritize_defense = config.getBoolean("prioritize-defense-items", DEFAULT_prioritize_defense);
        max_items = config.getInt("max-item-count", DEFAULT_max_items);
        randomize_mode = config.getInt("randomize-mode", DEFAULT_randomize_mode);
        allow_midgame_joins =config.getBoolean("allow-midgame-joins", DEFAULT_allow_midgame_joins);
    }

    public void save() throws IOException {
        config.save(file);
    }

    //#region getters

    public int getItemInterval() {
        //15+1.4x+Math.floor(x/2)
        int x = (2*MissileWars.gameManager.players().size()) + 2;
        double scale_amount = 15 + (1.4*x) + Math.floor((double)x/2);
        return item_interval_sec;
    }

    public boolean isPrioritizeDefenseEnabled() {
        return prioritize_defense;
    }

    public int getMaxItems() {
        return max_items;
    }

    public int getRandomizeMode() {
        return randomize_mode;
    }

    public boolean isMidGameJoinAllowed() {
        return allow_midgame_joins;
    }

    //#endregion

    //#region setters
    public void setItemIntervalSec(int item_interval_sec) {
        this.item_interval_sec = item_interval_sec;
    }

    public void setPrioritizeDefense(boolean prioritize_defense) {
        this.prioritize_defense = prioritize_defense;
    }

    public void setMidgameJoins(boolean allow_midgame_joins) {
        this.allow_midgame_joins = allow_midgame_joins;
    }

    public void setMaxItems(int max_items) {
        this.max_items = max_items;
    }

    public void setRandomizeMode(int randomize_mode) {
        this.randomize_mode = randomize_mode;
    }
    //#endregion
}
