package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.ConfigOption;
import me.jackz.missilewars.lib.MWUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class GameConfig {
    private int item_interval_sec = 20;
    private boolean allow_midgame_joins = false;
    private int max_items = 1;
    private int randomize_mode = 0; //0 -> ALL, 1 -> Per team, 2 -> Per individual
    private boolean show_item_timer = true;
    private HashMap<String, ConfigOption> options = new HashMap<>();

    public final static int DEFAULT_item_interval_sec = 15;
    public final static boolean DEFAULT_allow_midgame_joins = false;
    public final static int DEFAULT_max_items = 1;
    public final static int DEFAULT_randomize_mode = 0; //0 -> ALL, 1 -> Per team, 2 -> Per individual
    public final static boolean DEFAULT_show_item_timer = true;

    private static File file;
    private static YamlConfiguration config;

    //These values are definitely temporarily
    public static Location RED_SPAWNPOINT;
    public static Location GREEN_SPAWNPOINT;

    public static Location RED_LOBBY_SPAWNPOINT;
    public static Location GREEN_LOBBY_SPAWNPOINT;

    public static Location SPAWN_LOCATION;

    public GameConfig() {
        file = new File(MissileWars.getInstance().getDataFolder(),"config.yml");
        if(!file.exists()) MissileWars.getInstance().saveResource("config.yml",false);

        config = YamlConfiguration.loadConfiguration(file);
        reload();
    }

    public void registerOption(ConfigOption option) {
        options.put(option.getSafeId(), option);
        config.addDefault(option.getSafeId(), option.getDefault());
    }
    public void registerOptions(ConfigOption... option) {
        for (ConfigOption configOption : option) {
           registerOption(configOption);
        }
    }


    public void reload() {
        //config options
        for (ConfigOption option : options.values()) {
            reloadOption(option);
        }
        /*item_interval_sec = config.getInt("item-interval-seconds", DEFAULT_item_interval_sec);
        max_items = config.getInt("max-item-count", DEFAULT_max_items);
        randomize_mode = config.getInt("randomize-mode", DEFAULT_randomize_mode);
        allow_midgame_joins = config.getBoolean("allow-midgame-joins", DEFAULT_allow_midgame_joins);
        show_item_timer = config.getBoolean("show-item-timer", DEFAULT_show_item_timer);*/


        RED_LOBBY_SPAWNPOINT = MWUtil.getLocation("locations.lobby.red");
        GREEN_LOBBY_SPAWNPOINT = MWUtil.getLocation("locations.lobby.green");
        RED_SPAWNPOINT = MWUtil.getLocation("locations.spawns.red");
        GREEN_SPAWNPOINT = MWUtil.getLocation("locations.spawns.green");
        SPAWN_LOCATION = MWUtil.getLocation("locations.spawns.default");
    }

    public void save() throws IOException {
        config.save(file);
    }

    private void reloadOption(ConfigOption option) {
        if(option.getType() == ConfigOption.ConfigType.Boolean) {
            boolean value = config.getBoolean(option.getId(), (Boolean) option.getDefault());
            option.setValue(value);
        }else if(option.getType() == ConfigOption.ConfigType.Integer) {
            int value = config.getInt(option.getId(), (Integer) option.getDefault());
            if(option.hasRange()) {
                if(value == -1 && option.canDisable()) {
                    option.setValue(value);
                }else {
                    if (option.hasMin() && value <= option.getMin()) {
                        Bukkit.getLogger().warning("Option " + option.getId() + " is below minimum value (min: " + option.getMin() + ")");
                    }else if(option.hasMax() && value >= option.getMax()) {
                        Bukkit.getLogger().warning("Option " + option.getId() + " is above maximum value (ax: " + option.getMax() + ")");
                    }else{
                        option.setValue(value);
                    }
                }
            }
        }else if(option.getType() == ConfigOption.ConfigType.String) {
            String value = config.getString(option.getId(), (String) option.getDefault());
            option.setValue(value);
        }
    }

    //#region getters

    public int getItemInterval() {
        //15+1.4x+Math.floor(x/2)
        int x = (2*MissileWars.gameManager.players().size()) + 2;
        double scale_amount = 15 + (1.4*x) + Math.floor((double)x/2);
        return item_interval_sec;
    }

    public ConfigOption getOption(String id) {
        return options.get(id);
    }
    public Set<String> getOptionIds() {
        return options.keySet();
    }

}
