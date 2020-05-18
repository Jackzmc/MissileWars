package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatsTracker {

    private Map<String,Integer> savedStats = new HashMap<>();
    private Map<String,Integer> memStats = new HashMap<>();
    private File file;
    private long game_start_time = -1;

    public StatsTracker() {
        file = new File(MissileWars.getInstance().getDataFolder(),"statistics.yml");
        reload();
    }
    public void incSavedStat(String name) {
        incSavedStat(name, 1);
    }
    public void incSavedStat(String name, int incAmount) {
        int prev = savedStats.getOrDefault(name, 0);
        setSavedStat(name,prev+incAmount);
    }
    public void incSessionStat(String name) {
        int prev = memStats.getOrDefault(name, 0);
        memStats.put(name,prev+1);
    }
    public void incStat(String name) {
        incSavedStat(name);
        incSessionStat(name);
    }

    public void setSavedStat(String name, int value) {
        savedStats.put(name,value);
    }

    public int getSavedStat(String name) {
        return savedStats.getOrDefault(name,0);
    }
    public int getSessionStat(String name) {
        return memStats.getOrDefault(name, 0);
    }

    public void clearSessionStats() {
        memStats.clear();
    }

    public void reload() {
        savedStats.clear();
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (String key : data.getKeys(true)) {
            if(data.isInt(key)) {
                savedStats.put(key, data.getInt(key));
            }
        }
    }

    public void save() {
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Integer> entries : savedStats.entrySet()) {
            data.set(entries.getKey(),entries.getValue());
        }
        try {
            data.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not save statistics.yml. IOException");
        }

    }

    public long getGameTimeMS() {
        if(game_start_time < 0) {
            return -1;
        }else{
            return System.currentTimeMillis() - game_start_time;
        }
    }
    public void resetGameTime() {
        game_start_time = System.currentTimeMillis();
    }

}
