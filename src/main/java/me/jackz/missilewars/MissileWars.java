package me.jackz.missilewars;

import me.jackz.missilewars.commands.AdminCommand;
import me.jackz.missilewars.commands.GameCommand;
import me.jackz.missilewars.commands.PlayerStatsCommand;
import me.jackz.missilewars.commands.SpectateCommand;
import me.jackz.missilewars.events.*;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.lib.RestartManager;
import me.jackz.missilewars.lib.TeamDisplayManager;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MissileWars extends JavaPlugin {

    private static MissileWars main;
    public static GameManager gameManager;
    private TeamDisplayManager displayManager;
    private RestartManager restartManager;
    private Chat vaultChat;

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
        setupChat();
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
    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if(rsp != null) vaultChat = rsp.getProvider();
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
    public Chat getVaultChat() {
        return vaultChat;
    }
}
