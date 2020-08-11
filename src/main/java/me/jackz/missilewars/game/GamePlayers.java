package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.TeamDisplayManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
    private TeamDisplayManager displayManager;
    //temporarily
    private Scoreboard scoreboard;
    private Team redTeam;
    private Team greenTeam;

    private final static PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 2, true, false, false);
    private final static PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION, 99999, 2, true, false, false);
    private final static PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 99999, 0, true, false, false);

    public GamePlayers() {
        scoreboard =  Bukkit.getScoreboardManager().getMainScoreboard();
        redTeam = scoreboard.getTeam("Red");
        greenTeam = scoreboard.getTeam("Green");
        displayManager = MissileWars.getInstance().getDisplayManager();
    }
    //#region static methods
    public static String getTeamName(MWTeam team) {
        if(team == MWTeam.RED) {
            return "Red";
        }else if(team == MWTeam.GREEN) {
            return "Green";
        }else {
            return "None";
        }
    }
    public static ChatColor getTeamColor(MWTeam team) {
        if(team == MWTeam.RED) {
            return ChatColor.RED;
        }else if(team == MWTeam.GREEN) {
            return ChatColor.GREEN;
        }else{
            return ChatColor.WHITE;
        }
    }
    //#endregion


    public void setupPlayer(Player player) {
        player.getInventory().clear();
        player.addPotionEffect(nightVision);
        player.addPotionEffect(saturation);
        player.addPotionEffect(regeneration);
        player.setHealth(20);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setExp(0.0f);
    }

    public void joinPlayer(Player player, MWTeam team) {
        add(player, team);
        boolean gameActive = MissileWars.gameManager.getState().isGameActive();
        player.setGameMode(gameActive ? GameMode.SURVIVAL : GameMode.ADVENTURE);
        setupPlayer(player);
        if(team == MWTeam.RED) {
            Location spawnLocation = gameActive ? GameConfig.RED_SPAWNPOINT : GameConfig.RED_LOBBY_SPAWNPOINT;
            player.teleport(spawnLocation);
            Bukkit.broadcastMessage("§c" + player.getName() + " joined the Red team!");
        }else{
            Location spawnLocation = gameActive ? GameConfig.GREEN_SPAWNPOINT : GameConfig.GREEN_LOBBY_SPAWNPOINT;
            player.teleport(spawnLocation);
            Bukkit.broadcastMessage("§a" + player.getName() + " joined the Green team!");
        }

    }

    public int size() {
        return redTeamPlayers.size() + greenTeamPlayers.size();
    }

    public void add(Player player, MWTeam team) {
        if(has(player)) {
            remove(player);
        }

        if(team.equals(MWTeam.GREEN)) {
            greenTeam.addEntry(player.getName());
            greenTeamPlayers.add(player);
        }else{
            redTeam.addEntry(player.getName());
            redTeamPlayers.add(player);
        }
        //displayManager.refreshSidebar(true);
    }

    public void remove(Player player, MWTeam team) {
        if(team.equals(MWTeam.GREEN)) {
            greenTeam.removeEntry(player.getName());
            greenTeamPlayers.remove(player);
        }else{
            redTeam.removeEntry(player.getName());
            redTeamPlayers.remove(player);
        }
        processRemoval();
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
        processRemoval();
    }

    private void processRemoval() {
        if(MissileWars.gameManager.getState().isGameActive()) {
            if(size() > 0) {
                //TODO: Auto rebalance teams
            }else{
                MissileWars.gameManager.end();
                Bukkit.broadcastMessage("§cLast player left, game has ended.");
            }
        }
        //displayManager.refreshSidebar(true);
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
