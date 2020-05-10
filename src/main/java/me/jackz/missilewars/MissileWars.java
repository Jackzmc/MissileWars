package me.jackz.missilewars;

import me.jackz.missilewars.commands.*;
import me.jackz.missilewars.events.ChatListener;
import me.jackz.missilewars.events.PlayerMoveEvent;
import me.jackz.missilewars.events.PlayerSpawning;
import me.jackz.missilewars.lib.TeamDisplayManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class MissileWars extends JavaPlugin {

    private boolean PENDING_RESTART = false;
    private final Long RESTART_DELAY_SECONDS = 172800L;

    private TeamDisplayManager displayManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new ChatListener(),this);
        getCommand("spectate").setExecutor(new SpectateCommand());
        getCommand("game").setExecutor(new GameCommand(this));
        getCommand("stats").setExecutor(new PlayerStatsCommand(this));
        getCommand("missilewarsadmin").setExecutor(new AdminCommand(this));
        getCommand("scramble").setExecutor(new ScrambleCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(),this);
        getServer().getPluginManager().registerEvents(new PlayerSpawning(this),this);

        Scoreboard main = getServer().getScoreboardManager().getMainScoreboard();
        if(main.getTeam("Red") == null) {
            Team red = main.registerNewTeam("Red");

            red.setColor(ChatColor.RED);
            red.setAllowFriendlyFire(false);
            red.setCanSeeFriendlyInvisibles(true);
            red.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        }
        if(main.getTeam("Green") == null) {
            Team green = main.registerNewTeam("Green");

            green.setColor(ChatColor.GREEN);
            green.setAllowFriendlyFire(false);
            green.setCanSeeFriendlyInvisibles(true);
            green.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        }

        if(main.getObjective("wins") == null ) main.registerNewObjective("wins", "dummy", "Wins");
        if(main.getObjective("loses") == null ) main.registerNewObjective("loses", "dummy", "Loses");

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if(getServer().getOnlinePlayers().size() > 0) {
                PENDING_RESTART = true;
                getServer().broadcastMessage("§2[Missilewars] §eNotice: Server will being restarting when all players leave.");
            }else{
                getServer().broadcastMessage("§2[MissileWars] §cServer is now auto restarting.");
                getServer().shutdown();
            }
        },20 * RESTART_DELAY_SECONDS);

        getDataFolder().mkdirs();
        displayManager = new TeamDisplayManager(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if(displayManager != null) {
            displayManager.unregister();
            displayManager = null;
        }

        // Plugin shutdown logic
    }

    public boolean isPendingRestart() {
        return PENDING_RESTART;
    }
    public TeamDisplayManager getDisplayManager() {
        return displayManager;
    }

}
