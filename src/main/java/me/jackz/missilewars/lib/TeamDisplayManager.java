package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GamePlayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class TeamDisplayManager {
    private MissileWars plugin;
    private Scoreboard sb;
    private Objective sidebar;

    private int red_team_size = 0;
    private int green_team_size = 0;

    private int refreshTimerID;

    public TeamDisplayManager(MissileWars plugin) {
        this.plugin = plugin;
        this.sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
        createObjective();
        refreshSidebar();
        refreshTimerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::refreshSidebar,0,20 * 1); //every 3s
    }
    private void createObjective() {
        Objective old = sb.getObjective("teamlist");
        if(old != null) old.unregister();
        sidebar = sb.registerNewObjective("teamlist", "dummy","§lTeam Information", RenderType.INTEGER);
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.getScore("§a§nGreen Team Players").setScore(4);
        sidebar.getScore(" ").setScore(2);
        sidebar.getScore("§c§nRed Team Players").setScore(1);
    }
    public void refreshSidebar(boolean force) {
        List<Player> greenTeamPlayers = MissileWars.gameManager.players().get(GamePlayers.MWTeam.GREEN);
        List<Player> redTeamPlayers = MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED);
        if(force || green_team_size != greenTeamPlayers.size() || red_team_size != redTeamPlayers.size()) {
            sidebar.unregister();
            createObjective();
            for (Player player : greenTeamPlayers) {
                if (player == null || !player.isOnline()) continue;
                sidebar.getScore(player.getName()).setScore(3);
            }
            for (Player player : redTeamPlayers) {
                if (player == null || !player.isOnline()) continue;
                sidebar.getScore(player.getName()).setScore(0);
            }
            green_team_size = greenTeamPlayers.size();
            red_team_size = redTeamPlayers.size();
        }
    }
    public void refreshSidebar() {
        refreshSidebar(false);
    }

    public void unregister() {
        Bukkit.getScheduler().cancelTask(refreshTimerID);
        sidebar.unregister();
    }
}
