package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


public class StatsTracker {
    public enum StatisticType {
        Session,
        Saved
    }

    private Map<String,Integer> savedStats = new HashMap<>();
    private Map<String,Integer> memStats = new HashMap<>();
    private File file;
    private long game_start_time = -1;

    public StatsTracker() {
        file = new File(MissileWars.getInstance().getDataFolder(),"statistics.yml");
        reload();
    }
    public void increaseSavedStat(String name) {
        increaseSavedStat(name, 1);
    }
    public void increaseSavedStat(String name, int incAmount) {
        int prev = savedStats.getOrDefault(name, 0);
        setSavedStat(name,prev+incAmount);
    }
    public void increaseSessionStat(String name) {
        int prev = memStats.getOrDefault(name, 0);
        memStats.put(name,prev+1);
    }
    public void increaseStat(String name) {
        increaseSavedStat(name);
        increaseSessionStat(name);
    }

    public Entry<Player, Integer> getHighestSessionStat(String statName) {
        int highest = 0;
        String name = null;
        for (Entry<String, Integer> stringIntegerEntry : memStats.entrySet()) {
            String key = stringIntegerEntry.getKey();
            if(key.startsWith(statName)) {
                String id = key.substring(key.lastIndexOf(".") + 1);
                int value = stringIntegerEntry.getValue();

                if (id.matches("\"[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\"")) {
                    if (value > highest) {
                        highest = value;
                        name = id;
                    }
                }
            }
        }
        if(name != null) {
            Player player = Bukkit.getPlayer(name);
            return new AbstractMap.SimpleEntry<>(player, highest);
        }else{
            return null;
        }
    }

    public void setSavedStat(String name, int value) {
        savedStats.put(name,value);
    }

    public int getSavedStat(String name) {
        return savedStats.getOrDefault(name,0);
    }
    public int getStat(String name, boolean saved) {
        return saved ? getSavedStat(name) : getSessionStat(name);
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

    private String formatNumber(int number) {
        return String.format("%,d", number);
    }
    public int get(StatisticType type, String name) {
        if(type == StatisticType.Session) {
            return getSessionStat(name);
        }else{
            return getSavedStat(name);
        }
    }
    public int get(StatisticType type, String prefix, String id) {
        return get(type, prefix + "." + id);
    }
    public int get(StatisticType type, String name, UUID id) {
        return get(type, name, id.toString());
    }
    public String getFormatted(StatisticType type, String name) {
        int number = get(type, name);
        return String.format("%,d", number);
    }
    public String getFormatted(StatisticType type, String prefix, String id) {
        int number = get(type, prefix, id);
        return String.format("%,d", number);
    }
    public String getFormatted(StatisticType type, String prefix, UUID id) {
        return getFormatted(type, prefix, id.toString());
    }
}
