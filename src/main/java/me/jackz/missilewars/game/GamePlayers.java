package me.jackz.missilewars.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GamePlayers {
    public enum MWTeam {
        NONE,
        GREEN,
        RED
    }
    private List<Player> redTeamPlayers = new ArrayList<>();
    private List<Player> greenTeamPlayers = new ArrayList<>();
    //temporarily
    private Scoreboard scoreboard;
    private Team redTeam;
    private Team greenTeam;

    public GamePlayers() {
        scoreboard =  Bukkit.getScoreboardManager().getMainScoreboard();
        redTeam = scoreboard.getTeam("Red");
        greenTeam = scoreboard.getTeam("Green");
    }

    public void add(Player player, MWTeam team) {
        if(team.equals(MWTeam.GREEN)) {
            greenTeam.addEntry(player.getName());
            greenTeamPlayers.add(player);
        }else{
            redTeam.addEntry(player.getName());
            redTeamPlayers.add(player);
        }
    }

    public void remove(Player player, MWTeam team) {
        if(team.equals(MWTeam.GREEN)) {
            greenTeam.removeEntry(player.getName());
            greenTeamPlayers.remove(player);
        }else{
            redTeam.removeEntry(player.getName());
            redTeamPlayers.remove(player);
        }
    }
    public void remove(Player player) {
        MWTeam team = getTeam(player);
        if(team.equals(MWTeam.GREEN)) {
            greenTeam.removeEntry(player.getName());
            greenTeamPlayers.remove(player);
        }else{
            redTeam.removeEntry(player.getName());
            redTeamPlayers.remove(player);
        }
    }

    public List<Player> get(MWTeam team) {
        if(team == MWTeam.RED) {
            return redTeam.getEntries().stream().map(Bukkit::getPlayerExact).collect(Collectors.toList());
        }else if(team == MWTeam.GREEN) {
            return greenTeam.getEntries().stream().map(Bukkit::getPlayerExact).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
    }
    public MWTeam getTeam(Player player) {
        Team scoreboardTeam = scoreboard.getEntryTeam(player.getName());
        if(scoreboardTeam != null) {
            if(scoreboardTeam.getName().equalsIgnoreCase("Red")) {
                return MWTeam.RED;
            }else if(scoreboardTeam.getName().equalsIgnoreCase("Green")) {
                return MWTeam.GREEN;
            }
            return MWTeam.NONE;
        }
        for (Player greenTeamPlayer : greenTeamPlayers) {
            if(greenTeamPlayer.equals(player)) {
                return MWTeam.GREEN;
            }
        }
        for (Player redTeamPlayer : redTeamPlayers) {
            if(redTeamPlayer.equals(player)) {
                return MWTeam.RED;
            }
        }
        return MWTeam.NONE;
    }

    public boolean has(Player player, MWTeam team) {
        List<Player> players = get(team);
        for (Player player1 : players) {
            if(player1.equals(player)) return true;
        }
        return false;
    }
    public boolean has(Player player) {
        return getTeam(player) != MWTeam.NONE;
    }
}
