package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.ConfigOption;
import me.jackz.missilewars.lib.MWUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class GameConfig {
    private static HashMap<String, ConfigOption> options = new HashMap<>();

    private static File file;
    private static YamlConfiguration config;

    //These values are definitely temporarily
    public static Location RED_SPAWNPOINT;
    public static Location GREEN_SPAWNPOINT;

    public static Location RED_LOBBY_SPAWNPOINT;
    public static Location GREEN_LOBBY_SPAWNPOINT;

    public static Location SPAWN_LOCATION;

    static {
        file = new File(MissileWars.getInstance().getDataFolder(),"config.yml");
        if(!file.exists()) MissileWars.getInstance().saveResource("config.yml",false);

        config = YamlConfiguration.loadConfiguration(file);
        reload();
    }

    public static void registerOption(ConfigOption option) {
        options.put(option.getId(), option);
        config.addDefault(option.getSafeId(), option.getDefault());
        reloadOption(option);
    }
    public static void registerOptions(ConfigOption... option) {
        for (ConfigOption configOption : option) {
           registerOption(configOption);
        }
        Bukkit.getLogger().info("Registered " + option.length + " options.");
    }


    public static void reload() {
        //config options
        for (ConfigOption option : options.values()) {
            reloadOption(option);
        }

        RED_LOBBY_SPAWNPOINT = MWUtil.getLocation("locations.lobby.red");
        GREEN_LOBBY_SPAWNPOINT = MWUtil.getLocation("locations.lobby.green");
        RED_SPAWNPOINT = MWUtil.getLocation("locations.spawns.red");
        GREEN_SPAWNPOINT = MWUtil.getLocation("locations.spawns.green");
        SPAWN_LOCATION = MWUtil.getLocation("locations.spawns.default");
    }

    public static void save() throws IOException {
        config.save(file);
    }

    private static void reloadOption(ConfigOption option) {
        if(option.getType() == ConfigOption.ConfigType.Boolean) {
            boolean value = config.getBoolean(option.getId(), (Boolean) option.getDefault());
            option.setValue(value);
        }else if(option.getType() == ConfigOption.ConfigType.Integer) {
            int value = config.getInt(option.getId(), (Integer) option.getDefault());
            if(option.hasRange()) {
                if(value == -1 && option.canDisable()) {
                    option.setValue(value);
                }else {
                    if (option.hasMin() && value < option.getMin()) {
                        Bukkit.getLogger().warning("Option " + option.getId() + " is below minimum value (min: " + option.getMin() + ")");
                    }else if(option.hasMax() && value > option.getMax()) {
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

    public static int getItemInterval() {
        //15+1.4x+Math.floor(x/2)
        int x = (2*MissileWars.gameManager.players().size()) + 2;
        double scale_amount = 15 + (1.4*x) + Math.floor((double)x/2);
        return (int) scale_amount;
    }

    public static ConfigOption getOption(String id) {
        return options.get(id.toLowerCase());
    }
    public static Set<String> getOptionIds() {
        return options.keySet();
    }
    public static Collection<ConfigOption> getOptions() {
        return options.values();
    }

}
