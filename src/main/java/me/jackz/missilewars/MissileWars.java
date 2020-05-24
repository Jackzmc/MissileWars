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

import java.io.File;

public final class MissileWars extends JavaPlugin {

    private static MissileWars main;
    public static GameManager gameManager;
    private TeamDisplayManager displayManager;
    private RestartManager restartManager;
    private Chat vaultChat;

    private final static String[] DEFAULT_SCHEMS = new String[] { "cube3", "guardian", "juggernaut", "lightning", "shield", "shieldbuster", "tomahawk"};

    @Override
    public void onEnable() {
        main = this;
        getDataFolder().mkdirs();
        // Plugin startup logic
        gameManager = new GameManager();
        restartManager = new RestartManager();
        displayManager = new TeamDisplayManager(this);

        registerCommands();
        registerListeners();
        setupChat();
        saveSchematics();

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
        pm.registerEvents(new PlayerInteract(),this);
        pm.registerEvents(new MiscPlayerEvents(),this);
        pm.registerEvents(new Explosion(),this);
        pm.registerEvents(new BarrierDeploy(),this);
    }
    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if(rsp != null) vaultChat = rsp.getProvider();
    }
    private void saveSchematics() {
        File file = new File(getDataFolder(),"schematics");
        if(!file.exists() || (file.isDirectory() && file.list().length == 0)) {
            file.mkdirs();

            for (String schem : DEFAULT_SCHEMS) {
                saveResource("schematics/green-" + schem + ".schem",false);
                saveResource("schematics/red-" + schem + ".schem",false);
            }
        }
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
