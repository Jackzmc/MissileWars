package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.game.Reset;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataLoader {
    private static File file;
    private static YamlConfiguration data;

    static {
        file = new File(MissileWars.getInstance().getDataFolder(),"data.yml");
        reload();
    }
    public static void reload() {
        if(file.exists()) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            Reset.reloadVariables();
            World world = Bukkit.getWorld(data.getString("world-name","world"));
            GameManager.setWorld(world);
        }else{
            MissileWars.getInstance().saveResource("data.yml",true);
            Bukkit.getLogger().warning("Could not find data.yml, creating default.");
        }
    }

    public static void save() throws IOException {
        data.save(file);
    }

    public static YamlConfiguration getData() {
        return data;
    }
}
