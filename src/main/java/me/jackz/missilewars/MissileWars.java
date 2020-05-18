package me.jackz.missilewars;

import me.jackz.missilewars.commands.*;
import me.jackz.missilewars.events.*;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.lib.RestartManager;
import me.jackz.missilewars.lib.TeamDisplayManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MissileWars extends JavaPlugin {


    private static MissileWars main;
    public static GameManager gameManager;
    private TeamDisplayManager displayManager;
    private RestartManager restartManager;


    @Override
    public void onEnable() {
        main = this;
        getDataFolder().mkdirs();
        // Plugin startup logic
        gameManager = new GameManager(this);
        restartManager = new RestartManager();
        displayManager = new TeamDisplayManager(this);

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        getCommand("spectate").setExecutor(new SpectateCommand());
        getCommand("game").setExecutor(new GameCommand(this));
        getCommand("stats").setExecutor(new PlayerStatsCommand(this));
        getCommand("missilewarsadmin").setExecutor(new AdminCommand(this));
    }
    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(),this);
        pm.registerEvents(new PlayerMoveEvent(),this);
        pm.registerEvents(new PlayerSpawning(),this);
        pm.registerEvents(new MiscPlayerEvents(),this);
        pm.registerEvents(new Explosion(),this);
        pm.registerEvents(new BarrierDeploy(),this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if(displayManager != null) {
            displayManager.unregister();
            displayManager = null;
        }
        if(gameManager != null) {
            gameManager.shutdown();
            gameManager = null;
        }
        if(restartManager != null) restartManager = null;
        main = null;

        // Plugin shutdown logic
    }


    public TeamDisplayManager getDisplayManager() {
        return displayManager;
    }
    public RestartManager getRestartManager() { return restartManager; }
    public static MissileWars getInstance() {
        return main;
    }

}
