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

    public StatsTracker() {
        file = new File(MissileWars.getInstance().getDataFolder(),"statistics.yml");
        load();
    }
    public void incSavedStat(String name) {
        int prev = savedStats.getOrDefault(name, 0);
        savedStats.put(name,prev+1);
    }
    public int getSavedStat(String name) {
        return savedStats.getOrDefault(name,0);
    }

    public void incStat(String name) {
        int prev = memStats.getOrDefault(name, 0);
        memStats.put(name,prev+1);
    }
    public int getStat(String name) {
        return memStats.getOrDefault(name, 0);
    }
    public void clearStats() {
        memStats.clear();
    }

    private void load() {

    }

    public void save() {
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Integer> entries : savedStats.entrySet()) {
            savedStats.put(entries.getKey(),entries.getValue());
        }
        try {
            data.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not save statistics.yml. IOException");
        }

    }

}