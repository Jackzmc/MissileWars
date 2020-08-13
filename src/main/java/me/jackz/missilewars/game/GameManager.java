package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.Set;

public class GameManager {
    private GameState state;
    private GamePlayers players;
    private static MissileLoader missileLoader;
    private ItemSystem itemSystem;
    private static StatsTracker stats;

    private static World WORLD;

    static {
        WORLD = Bukkit.getWorld("world");
    }

    public GameManager() {
        state = new GameState();
        itemSystem = new ItemSystem();
        players = new GamePlayers();
        missileLoader = new MissileLoader();
        stats = new StatsTracker();

        ItemSystem.getTypes(); //initalize missiles
        Bukkit.getLogger().info("Loaded " + missileLoader.getMissiles().size() + " missiles");
        initializeScoreboard();
        try {
            Class.forName("me.jackz.missilewars.game.Options");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setWorld(World world) {
        WORLD  = world;
    }

    //#region privatemethods
    private void initializeScoreboard() {
        Scoreboard main = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
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
    }
    public void ready(GamePlayers.MWTeam team) {
        state.setTeamReady(team,true);
        String name = (team == GamePlayers.MWTeam.RED) ? "§cRed" : "§aGreen";
        Bukkit.broadcastMessage(name + " team §eis ready to go!");
        if(state.isGameReady()) {
            if(Reset.isResetting()) {
                Bukkit.broadcastMessage("Game is still resetting, please wait.");
                state.setTeamReady(GamePlayers.MWTeam.RED,false);
                state.setTeamReady(GamePlayers.MWTeam.GREEN,false);
            }else{
                start();

            }
        }
    }
    //#endregion
    public void start() {
        if(players.size() == 0) {
            Bukkit.getLogger().warning("GameManager: Not starting with 0 players.");
        }else if(Reset.isResetting()) {
            Bukkit.getLogger().warning("GameManager: Game is still resetting");
        }else {
            ItemStack bow = ItemSystem.getItem("bow");
            state.setActive(true);
            for (Map.Entry<Player, GamePlayers.MWTeam> entry : players.getAll()) {
                Player player = entry.getKey();
                GamePlayers.MWTeam team = entry.getValue();
                if (team == GamePlayers.MWTeam.GREEN) {
                    player.teleport(GameConfig.GREEN_SPAWNPOINT);
                    player.setBedSpawnLocation(GameConfig.GREEN_SPAWNPOINT, true);
                } else if (team == GamePlayers.MWTeam.RED) {
                    player.teleport(GameConfig.RED_SPAWNPOINT);
                    player.setBedSpawnLocation(GameConfig.RED_SPAWNPOINT, true);
                } else {
                    continue;
                }
                Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
                    player.sendTitle("§c3", "", 0, 20, 0);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.0f);
                }, 20);
                Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
                    player.sendTitle("§e2", "", 0, 20, 0);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.0f);
                }, 20 * 2);
                Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
                    player.sendTitle("§a1", "", 0, 20, 0);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.0f);
                }, 20 * 3);
                Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
                    player.sendTitle("§aGO!", "Game has started", 0, 20, 0);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.0f);
                    player.sendMessage("§eMissile Wars has started!");
                    player.sendMessage("§9Tip: " + Tips.getRandomTip());
                }, 20 * 4);
                players.setupPlayer(player);
                ItemSystem.giveItem(player, bow, false);
                player.setGameMode(GameMode.SURVIVAL);
            }
            stats.resetGameTime();
            stats.clearSessionStats();
            Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> itemSystem.start(), 20 * 5);
        }
    }

    public void end() {
        Set<Player> allPlayers = players.getAllPlayers();
        int duration_minutes = (int) stats.getGameTimeMS() / 1000 / 60;

        stats.incSavedStat("gametime_min.total",duration_minutes);
        int longest_game = stats.getSavedStat("gametime_min.longest");
        if(longest_game < duration_minutes) {
            stats.setSavedStat("gametime_min.longest", duration_minutes);
            Bukkit.broadcastMessage("§eGame was a record breaking total of §9" + duration_minutes + " minutes §elong! ");
        }else{
            Bukkit.broadcastMessage("§eGame was a total of §9" + duration_minutes + " minutes §elong!");
        }
        itemSystem.stop();
        for (Player player : allPlayers) {
            players.setupPlayer(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.setBedSpawnLocation(GameConfig.SPAWN_LOCATION, true);
            stats.incSavedStat("gametime_min." + player.getUniqueId(),duration_minutes);
        }
        state.setActive(false);

        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), this::reset , 20 * 10);
    }

    public void reset() {
        Reset.reset();
        state.setTeamReady(GamePlayers.MWTeam.GREEN, false);
        state.setTeamReady(GamePlayers.MWTeam.RED, false);
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
            Set<Player> allPlayers = players.getAllPlayers();
            for(Player player : allPlayers) {
                player.getInventory().clear();
                player.teleport(GameConfig.SPAWN_LOCATION);
                player.setGameMode(GameMode.ADVENTURE);
                players.remove(player);
                players.setupPlayer(player);
            }
        }, 20 * 30);
    }

    public void reload() {
        itemSystem.stop();
        itemSystem.start();
    }


    public void shutdown() {
        state = null;
        if(itemSystem != null) {
            itemSystem.stop();
            itemSystem = null;
        }
        for (Map.Entry<Player, GamePlayers.MWTeam> playerMWTeamEntry : players.getAll()) {
            Player player = playerMWTeamEntry.getKey();
            players.remove(player, playerMWTeamEntry.getValue());
            players.setupPlayer(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(GameConfig.SPAWN_LOCATION);
        }
        players = null;
        if(stats != null) {
            stats.save();
            stats = null;
        }
        missileLoader = null;

    }

    public GameState getState() {
        return state;
    }

    public GamePlayers players() {
        return players;
    }
    public static StatsTracker getStats() {
        return stats;
    }
    public static MissileLoader getMissileLoader() {
        return missileLoader;
    }

    public static World getWorld() {
        return WORLD;
    }
}
