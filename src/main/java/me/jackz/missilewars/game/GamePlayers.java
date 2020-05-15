package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
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

    private final static PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 9999, 2, true, false, false);
    private final static PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION, 9999, 2, true, false, false);
    private final static PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 9999, 0, true, false, false);

    public GamePlayers() {
        scoreboard =  Bukkit.getScoreboardManager().getMainScoreboard();
        redTeam = scoreboard.getTeam("Red");
        greenTeam = scoreboard.getTeam("Green");
    }

    public static String getTeamName(MWTeam team) {
        if(team == MWTeam.RED) {
            return "Red";
        }else if(team == MWTeam.GREEN) {
            return "Green";
        }else {
            return "None";
        }
    }

    public void setupPlayer(Player player) {
        player.getInventory().clear();
        player.addPotionEffect(nightVision);
        player.addPotionEffect(saturation);
        player.addPotionEffect(regeneration);
        player.setHealth(20);
    }

    public void joinPlayer(Player player, MWTeam team) {
        add(player, team);
        player.setGameMode(GameMode.ADVENTURE);
        setupPlayer(player);
        player.teleport(GameConfig.RED_LOBBY_SPAWNPOINT);
        if(team == MWTeam.RED) {
            Bukkit.broadcastMessage("§c" + player.getName() + " joined the Red team!");
        }else{
            Bukkit.broadcastMessage("§a" + player.getName() + " joined the Green team!");
        }

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
            return redTeam.getEntries().stream().map(Bukkit::getPlayerExact).filter(Objects::nonNull).collect(Collectors.toList());
        }else if(team == MWTeam.GREEN) {
            return greenTeam.getEntries().stream().map(Bukkit::getPlayerExact).filter(Objects::nonNull).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
    }
    private Map<Player, MWTeam> getAllMap() {
        Map<Player, MWTeam> players = new HashMap<>();
        for (String entry : redTeam.getEntries()) {
            Player p = Bukkit.getPlayerExact(entry);
            if(p != null) players.put(p, MWTeam.RED);
        }
        for (String entry : greenTeam.getEntries()) {
            Player p = Bukkit.getPlayerExact(entry);
            if(p != null) players.put(p, MWTeam.GREEN);
        }
        return players;
    }
    public Set<Map.Entry<Player,MWTeam>> getAll() {
        return getAllMap().entrySet();
    }
    public Set<Player> getAllPlayers() {
        return getAllMap().keySet();
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
