package me.jackz.missilewars.game;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamePlayers {
    public enum MWTeam {
        NONE,
        GREEN,
        RED
    }
    private List<Player> redTeamPlayers = new ArrayList<>();
    private List<Player> greenTeamPlayers = new ArrayList<>();
    public void add(Player player, MWTeam team) {
        if(team.equals(MWTeam.GREEN)) {
            greenTeamPlayers.add(player);
        }else{
            redTeamPlayers.add(player);
        }
    }

    public void remove(Player player, MWTeam team) {
        if(team.equals(MWTeam.GREEN)) {
            greenTeamPlayers.remove(player);
        }else{
            redTeamPlayers.remove(player);
        }
    }

    public List<Player> get(MWTeam team) {
        return team == MWTeam.GREEN ? greenTeamPlayers : redTeamPlayers;
    }
    public MWTeam getTeam(Player player) {
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
        List<Player> players = (team == MWTeam.GREEN) ? greenTeamPlayers : redTeamPlayers;
        for (Player player1 : players) {
            if(player1.equals(player)) return true;
        }
        return false;
    }
    public boolean has(Player player) {
        return getTeam(player) != MWTeam.NONE;
    }
}
