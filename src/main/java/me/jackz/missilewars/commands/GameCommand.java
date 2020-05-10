package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameCommand implements CommandExecutor  {
    private Scoreboard sb;
    private MissileWars plugin;
    public GameCommand(MissileWars plugin) {
        this.plugin = plugin;
        sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
    }
    /*
    todo:
    check if team players are online
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Set<Team> teams = sb.getTeams();
        Set<String> red_players = new HashSet<>();
        Set<String> green_players = new HashSet<>();

        int total_red_wins = sb.getObjective("TotalWins").getScore("Red").getScore();
        int total_green_wins = sb.getObjective("TotalWins").getScore("Green").getScore();

        for (Team team : teams) {
            if(team.getName().equalsIgnoreCase("green")) {
                green_players = team.getEntries().stream().filter(this::isPlayerOnline).collect(Collectors.toSet());
            }else if(team.getName().equalsIgnoreCase("red")) {
                red_players = team.getEntries().stream().filter(this::isPlayerOnline).collect(Collectors.toSet());
            }
        }
        // if (game active) { */
        sender.sendMessage(String.format("§cRed Team (%d wins)",total_red_wins));
        if(red_players.size() > 0) {
            sender.sendMessage(ChatColor.GRAY + String.join(", ", red_players));
        }else{
            sender.sendMessage("§7There are no players on the red team");
        }
        sender.sendMessage("");
        sender.sendMessage(String.format("§aGreen Team (%d wins)",total_green_wins));
        if(green_players.size() > 0) {
            sender.sendMessage(ChatColor.GRAY + String.join(", ", green_players));
        }else{
            sender.sendMessage("§7There are no players on the green team");
        }
        // }
        return true;
    }

    private boolean isPlayerOnline(String username) {
        return plugin.getServer().getPlayer(username.toLowerCase()) != null;
    }
}
