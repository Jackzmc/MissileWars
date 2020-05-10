package me.jackz.missilewars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.List;

public class TeamDisplayManager {
    private MissileWars plugin;
    private Scoreboard sb;
    private Objective sidebar;

    private Team redTeam;
    private Team greenTeam;
    private int red_team_size = 0;
    private int green_team_size = 0;

    private int refreshTimerID;

    TeamDisplayManager(MissileWars plugin) {
        this.plugin = plugin;
        this.sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
        redTeam = sb.getTeam("Red");
        greenTeam = sb.getTeam("Green");
        createObjective();
        refreshSidebar();
        refreshTimerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::refreshSidebar,0,20 * 1); //every 3s
    }
    private void createObjective() {
        sidebar = sb.registerNewObjective("teamlist", "dummy","§lTeam Information", RenderType.INTEGER);
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.getScore("§a§nGreen Team Players").setScore(4);
        sidebar.getScore(" ").setScore(2);
        sidebar.getScore("§c§nRed Team Players").setScore(1);
    }
    void refreshSidebar() {
        if(green_team_size != greenTeam.getSize() || red_team_size != redTeam.getSize()) {
            sidebar.unregister();
            createObjective();

            for (String entry : greenTeam.getEntries()) {
                Player p = plugin.getServer().getPlayerExact(entry);
                if (p == null || !p.isOnline()) continue;
                sidebar.getScore(entry).setScore(3);
            }
            for (String entry : redTeam.getEntries()) {
                Player p = plugin.getServer().getPlayerExact(entry);
                if (p == null || !p.isOnline()) continue;
                sidebar.getScore(entry).setScore(0);
            }
            green_team_size = greenTeam.getSize();
            red_team_size = redTeam.getSize();
        }
    }

    void unregister() {
        Bukkit.getScheduler().cancelTask(refreshTimerID);
        sidebar.unregister();
    }
}
